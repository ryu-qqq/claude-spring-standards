"""
Feedback Loop Storage
AESA-129 Task 5.1: PostgreSQL 비동기 저장소

위반 로그를 PostgreSQL에 비동기로 저장하고 조회
"""

import json
import os
from datetime import datetime
from typing import Optional

try:
    import asyncpg
except ImportError:
    asyncpg = None  # Optional dependency

from .models import ViolationLog, ViolationPattern, RuleWeight


def get_pg_dsn_from_env() -> Optional[str]:
    """환경 변수에서 PostgreSQL DSN 구성

    환경 변수:
        PG_HOST: PostgreSQL 호스트
        PG_PORT: PostgreSQL 포트 (기본값: 5432)
        PG_DATABASE: 데이터베이스명
        PG_USER: 사용자명
        PG_PASSWORD: 비밀번호

    Returns:
        DSN 문자열 또는 None (환경 변수 미설정 시)
    """
    host = os.environ.get("PG_HOST")
    if not host:
        return None

    port = os.environ.get("PG_PORT", "5432")
    database = os.environ.get("PG_DATABASE", "shared_api")
    user = os.environ.get("PG_USER", "shared_api_user")
    password = os.environ.get("PG_PASSWORD", "")

    return f"postgresql://{user}:{password}@{host}:{port}/{database}"


# Singleton instance
_storage: Optional["ViolationStorage"] = None


# DDL for PostgreSQL tables
VIOLATION_LOG_DDL = """
CREATE TABLE IF NOT EXISTS violation_logs (
    id UUID PRIMARY KEY,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- 위반 정보
    rule_code VARCHAR(20) NOT NULL,
    rule_name VARCHAR(200) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,

    -- 위치 정보
    line_number INTEGER,
    column_number INTEGER,
    code_snippet TEXT,

    -- 레이어 정보
    layer VARCHAR(50),
    class_type VARCHAR(50),

    -- 컨텍스트
    project_id VARCHAR(100),
    project_name VARCHAR(200),
    file_path TEXT,
    class_name VARCHAR(200),
    method_name VARCHAR(200),
    session_id VARCHAR(100),
    user_id VARCHAR(100),
    metadata JSONB DEFAULT '{}',

    -- 수정 관련
    was_auto_fixed BOOLEAN DEFAULT FALSE,
    fix_applied TEXT,

    -- 인덱스용 컬럼
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 조회 최적화 인덱스
CREATE INDEX IF NOT EXISTS idx_violation_logs_rule_code ON violation_logs(rule_code);
CREATE INDEX IF NOT EXISTS idx_violation_logs_severity ON violation_logs(severity);
CREATE INDEX IF NOT EXISTS idx_violation_logs_layer ON violation_logs(layer);
CREATE INDEX IF NOT EXISTS idx_violation_logs_project_id ON violation_logs(project_id);
CREATE INDEX IF NOT EXISTS idx_violation_logs_timestamp ON violation_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_violation_logs_created_at ON violation_logs(created_at);

-- 복합 인덱스 (패턴 분석용)
CREATE INDEX IF NOT EXISTS idx_violation_logs_rule_layer ON violation_logs(rule_code, layer);
CREATE INDEX IF NOT EXISTS idx_violation_logs_project_rule ON violation_logs(project_id, rule_code);
"""

VIOLATION_PATTERN_DDL = """
CREATE TABLE IF NOT EXISTS violation_patterns (
    id UUID PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- 패턴 정보
    pattern_type VARCHAR(50) NOT NULL,
    rule_codes JSONB NOT NULL,

    -- 분석 결과
    occurrence_count INTEGER NOT NULL,
    confidence REAL NOT NULL,
    description TEXT NOT NULL,

    -- 필터 조건
    project_id VARCHAR(100),
    user_id VARCHAR(100),
    layer VARCHAR(50),

    -- 권장 조치
    recommended_action TEXT
);

CREATE INDEX IF NOT EXISTS idx_violation_patterns_type ON violation_patterns(pattern_type);
CREATE INDEX IF NOT EXISTS idx_violation_patterns_project ON violation_patterns(project_id);
"""

RULE_WEIGHT_DDL = """
CREATE TABLE IF NOT EXISTS rule_weights (
    rule_code VARCHAR(20) PRIMARY KEY,

    -- 가중치
    base_weight REAL NOT NULL DEFAULT 1.0,
    adjusted_weight REAL NOT NULL DEFAULT 1.0,
    adjustment_reason TEXT,

    -- 통계
    violation_count INTEGER DEFAULT 0,
    auto_fix_rate REAL DEFAULT 0.0,

    -- 프로젝트별 오버라이드
    project_overrides JSONB DEFAULT '{}',

    -- 메타데이터
    last_updated TIMESTAMPTZ DEFAULT NOW()
);
"""


class ViolationStorage:
    """PostgreSQL 비동기 위반 로그 저장소"""

    def __init__(self, dsn: str):
        """저장소 초기화

        Args:
            dsn: PostgreSQL 연결 문자열
                 예: postgresql://user:password@localhost:5432/dbname
        """
        if asyncpg is None:
            raise ImportError(
                "asyncpg is required for ViolationStorage. Install with: pip install asyncpg"
            )

        self._dsn = dsn
        self._pool: Optional[asyncpg.Pool] = None

    async def initialize(self) -> None:
        """연결 풀 초기화 및 테이블 생성"""
        self._pool = await asyncpg.create_pool(
            self._dsn,
            min_size=2,
            max_size=10,
            command_timeout=30,
        )

        # 테이블 생성
        async with self._pool.acquire() as conn:
            await conn.execute(VIOLATION_LOG_DDL)
            await conn.execute(VIOLATION_PATTERN_DDL)
            await conn.execute(RULE_WEIGHT_DDL)

    async def close(self) -> None:
        """연결 풀 종료"""
        if self._pool:
            await self._pool.close()
            self._pool = None

    # ==================== ViolationLog 관련 ====================

    async def save_violation_log(self, log: ViolationLog) -> None:
        """위반 로그 저장"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        async with self._pool.acquire() as conn:
            await conn.execute(
                """
                INSERT INTO violation_logs (
                    id, timestamp, rule_code, rule_name, severity, message,
                    line_number, column_number, code_snippet,
                    layer, class_type,
                    project_id, project_name, file_path, class_name, method_name,
                    session_id, user_id, metadata,
                    was_auto_fixed, fix_applied
                ) VALUES (
                    $1, $2, $3, $4, $5, $6,
                    $7, $8, $9,
                    $10, $11,
                    $12, $13, $14, $15, $16,
                    $17, $18, $19,
                    $20, $21
                )
                """,
                log.id,
                log.timestamp,
                log.rule_code,
                log.rule_name,
                log.severity,
                log.message,
                log.line_number,
                log.column,
                log.code_snippet,
                log.layer,
                log.class_type,
                log.context.project_id,
                log.context.project_name,
                log.context.file_path,
                log.context.class_name,
                log.context.method_name,
                log.context.session_id,
                log.context.user_id,
                json.dumps(log.context.metadata),
                log.was_auto_fixed,
                log.fix_applied,
            )

    async def save_violation_logs_batch(self, logs: list[ViolationLog]) -> None:
        """위반 로그 배치 저장"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        if not logs:
            return

        async with self._pool.acquire() as conn:
            await conn.executemany(
                """
                INSERT INTO violation_logs (
                    id, timestamp, rule_code, rule_name, severity, message,
                    line_number, column_number, code_snippet,
                    layer, class_type,
                    project_id, project_name, file_path, class_name, method_name,
                    session_id, user_id, metadata,
                    was_auto_fixed, fix_applied
                ) VALUES (
                    $1, $2, $3, $4, $5, $6,
                    $7, $8, $9,
                    $10, $11,
                    $12, $13, $14, $15, $16,
                    $17, $18, $19,
                    $20, $21
                )
                """,
                [
                    (
                        log.id,
                        log.timestamp,
                        log.rule_code,
                        log.rule_name,
                        log.severity,
                        log.message,
                        log.line_number,
                        log.column,
                        log.code_snippet,
                        log.layer,
                        log.class_type,
                        log.context.project_id,
                        log.context.project_name,
                        log.context.file_path,
                        log.context.class_name,
                        log.context.method_name,
                        log.context.session_id,
                        log.context.user_id,
                        json.dumps(log.context.metadata),
                        log.was_auto_fixed,
                        log.fix_applied,
                    )
                    for log in logs
                ],
            )

    async def get_violation_logs(
        self,
        rule_code: Optional[str] = None,
        severity: Optional[str] = None,
        layer: Optional[str] = None,
        project_id: Optional[str] = None,
        since: Optional[datetime] = None,
        until: Optional[datetime] = None,
        limit: int = 100,
        offset: int = 0,
    ) -> list[dict]:
        """위반 로그 조회"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        # 동적 쿼리 빌드
        conditions = []
        params = []
        param_idx = 1

        if rule_code:
            conditions.append(f"rule_code = ${param_idx}")
            params.append(rule_code)
            param_idx += 1

        if severity:
            conditions.append(f"severity = ${param_idx}")
            params.append(severity)
            param_idx += 1

        if layer:
            conditions.append(f"layer = ${param_idx}")
            params.append(layer)
            param_idx += 1

        if project_id:
            conditions.append(f"project_id = ${param_idx}")
            params.append(project_id)
            param_idx += 1

        if since:
            conditions.append(f"timestamp >= ${param_idx}")
            params.append(since)
            param_idx += 1

        if until:
            conditions.append(f"timestamp <= ${param_idx}")
            params.append(until)
            param_idx += 1

        where_clause = f"WHERE {' AND '.join(conditions)}" if conditions else ""

        query = f"""
            SELECT * FROM violation_logs
            {where_clause}
            ORDER BY timestamp DESC
            LIMIT ${param_idx} OFFSET ${param_idx + 1}
        """
        params.extend([limit, offset])

        async with self._pool.acquire() as conn:
            rows = await conn.fetch(query, *params)
            return [dict(row) for row in rows]

    async def get_violation_count_by_rule(
        self,
        since: Optional[datetime] = None,
        project_id: Optional[str] = None,
    ) -> dict[str, int]:
        """규칙별 위반 횟수 집계"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        conditions = []
        params = []
        param_idx = 1

        if since:
            conditions.append(f"timestamp >= ${param_idx}")
            params.append(since)
            param_idx += 1

        if project_id:
            conditions.append(f"project_id = ${param_idx}")
            params.append(project_id)
            param_idx += 1

        where_clause = f"WHERE {' AND '.join(conditions)}" if conditions else ""

        query = f"""
            SELECT rule_code, COUNT(*) as count
            FROM violation_logs
            {where_clause}
            GROUP BY rule_code
            ORDER BY count DESC
        """

        async with self._pool.acquire() as conn:
            rows = await conn.fetch(query, *params)
            return {row["rule_code"]: row["count"] for row in rows}

    async def get_violation_count_by_layer(
        self,
        since: Optional[datetime] = None,
        project_id: Optional[str] = None,
    ) -> dict[str, int]:
        """레이어별 위반 횟수 집계"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        conditions = ["layer IS NOT NULL"]
        params = []
        param_idx = 1

        if since:
            conditions.append(f"timestamp >= ${param_idx}")
            params.append(since)
            param_idx += 1

        if project_id:
            conditions.append(f"project_id = ${param_idx}")
            params.append(project_id)
            param_idx += 1

        where_clause = f"WHERE {' AND '.join(conditions)}"

        query = f"""
            SELECT layer, COUNT(*) as count
            FROM violation_logs
            {where_clause}
            GROUP BY layer
            ORDER BY count DESC
        """

        async with self._pool.acquire() as conn:
            rows = await conn.fetch(query, *params)
            return {row["layer"]: row["count"] for row in rows}

    # ==================== ViolationPattern 관련 ====================

    async def save_pattern(self, pattern: ViolationPattern) -> None:
        """위반 패턴 저장"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        async with self._pool.acquire() as conn:
            await conn.execute(
                """
                INSERT INTO violation_patterns (
                    id, created_at, pattern_type, rule_codes,
                    occurrence_count, confidence, description,
                    project_id, user_id, layer, recommended_action
                ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)
                """,
                pattern.id,
                pattern.created_at,
                pattern.pattern_type.value,
                json.dumps(pattern.rule_codes),
                pattern.occurrence_count,
                pattern.confidence,
                pattern.description,
                pattern.project_id,
                pattern.user_id,
                pattern.layer,
                pattern.recommended_action,
            )

    async def get_patterns(
        self,
        pattern_type: Optional[str] = None,
        project_id: Optional[str] = None,
        min_confidence: float = 0.0,
        limit: int = 50,
    ) -> list[dict]:
        """위반 패턴 조회"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        conditions = [f"confidence >= ${1}"]
        params = [min_confidence]
        param_idx = 2

        if pattern_type:
            conditions.append(f"pattern_type = ${param_idx}")
            params.append(pattern_type)
            param_idx += 1

        if project_id:
            conditions.append(f"(project_id = ${param_idx} OR project_id IS NULL)")
            params.append(project_id)
            param_idx += 1

        where_clause = f"WHERE {' AND '.join(conditions)}"

        query = f"""
            SELECT * FROM violation_patterns
            {where_clause}
            ORDER BY confidence DESC, occurrence_count DESC
            LIMIT ${param_idx}
        """
        params.append(limit)

        async with self._pool.acquire() as conn:
            rows = await conn.fetch(query, *params)
            return [dict(row) for row in rows]

    # ==================== RuleWeight 관련 ====================

    async def save_rule_weight(self, weight: RuleWeight) -> None:
        """규칙 가중치 저장 (upsert)"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        async with self._pool.acquire() as conn:
            await conn.execute(
                """
                INSERT INTO rule_weights (
                    rule_code, base_weight, adjusted_weight, adjustment_reason,
                    violation_count, auto_fix_rate, project_overrides, last_updated
                ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
                ON CONFLICT (rule_code) DO UPDATE SET
                    base_weight = EXCLUDED.base_weight,
                    adjusted_weight = EXCLUDED.adjusted_weight,
                    adjustment_reason = EXCLUDED.adjustment_reason,
                    violation_count = EXCLUDED.violation_count,
                    auto_fix_rate = EXCLUDED.auto_fix_rate,
                    project_overrides = EXCLUDED.project_overrides,
                    last_updated = EXCLUDED.last_updated
                """,
                weight.rule_code,
                weight.base_weight,
                weight.adjusted_weight,
                weight.adjustment_reason,
                weight.violation_count,
                weight.auto_fix_rate,
                json.dumps(weight.project_overrides),
                weight.last_updated,
            )

    async def get_rule_weight(self, rule_code: str) -> Optional[dict]:
        """규칙 가중치 조회"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        async with self._pool.acquire() as conn:
            row = await conn.fetchrow(
                "SELECT * FROM rule_weights WHERE rule_code = $1",
                rule_code,
            )
            return dict(row) if row else None

    async def get_all_rule_weights(self) -> list[dict]:
        """모든 규칙 가중치 조회"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        async with self._pool.acquire() as conn:
            rows = await conn.fetch(
                "SELECT * FROM rule_weights ORDER BY violation_count DESC"
            )
            return [dict(row) for row in rows]

    async def increment_violation_count(self, rule_code: str) -> None:
        """규칙 위반 횟수 증가"""
        if not self._pool:
            raise RuntimeError("Storage not initialized. Call initialize() first.")

        async with self._pool.acquire() as conn:
            await conn.execute(
                """
                INSERT INTO rule_weights (rule_code, violation_count, last_updated)
                VALUES ($1, 1, NOW())
                ON CONFLICT (rule_code) DO UPDATE SET
                    violation_count = rule_weights.violation_count + 1,
                    last_updated = NOW()
                """,
                rule_code,
            )


async def get_violation_storage(dsn: Optional[str] = None) -> ViolationStorage:
    """싱글톤 ViolationStorage 인스턴스 반환

    Args:
        dsn: PostgreSQL 연결 문자열 (없으면 환경 변수에서 자동 구성)
    """
    global _storage

    if _storage is None:
        # DSN이 없으면 환경 변수에서 구성
        if dsn is None:
            dsn = get_pg_dsn_from_env()

        if dsn is None:
            raise ValueError(
                "DSN is required for initialization. "
                "Provide dsn parameter or set PG_HOST, PG_PORT, "
                "PG_DATABASE, PG_USER, PG_PASSWORD environment variables."
            )

        _storage = ViolationStorage(dsn)
        await _storage.initialize()

    return _storage
