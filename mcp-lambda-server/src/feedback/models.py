"""
Feedback Loop Models
AESA-129 Task 5.1: 위반 로깅 시스템 모델

ViolationLog, LogContext, ViolationPattern, RuleWeight 모델 정의
"""

from datetime import datetime
from enum import Enum
from typing import TYPE_CHECKING, Optional
from uuid import UUID, uuid4

from pydantic import BaseModel, Field

if TYPE_CHECKING:
    from ..validation.models import Violation


class LogContext(BaseModel):
    """위반 로그 컨텍스트 정보"""

    # 프로젝트 식별 (선택)
    project_id: Optional[str] = Field(None, description="프로젝트 식별자")
    project_name: Optional[str] = Field(None, description="프로젝트 명")

    # 코드 컨텍스트
    file_path: Optional[str] = Field(None, description="파일 경로")
    class_name: Optional[str] = Field(None, description="클래스 명")
    method_name: Optional[str] = Field(None, description="메서드 명")

    # 실행 컨텍스트
    session_id: Optional[str] = Field(None, description="세션 ID")
    user_id: Optional[str] = Field(None, description="사용자 ID")

    # 추가 메타데이터
    metadata: dict = Field(default_factory=dict, description="추가 메타데이터")


class ViolationLog(BaseModel):
    """위반 로그 엔트리

    ValidationEngine의 Violation을 확장하여 로깅에 필요한 정보 추가
    """

    # 기본 식별자
    id: UUID = Field(default_factory=uuid4, description="로그 고유 ID")
    timestamp: datetime = Field(
        default_factory=datetime.utcnow, description="발생 시각"
    )

    # 위반 정보 (Violation 모델에서 가져옴)
    rule_code: str = Field(..., description="규칙 코드 (예: AGG-001)")
    rule_name: str = Field(..., description="규칙 명칭")
    severity: str = Field(..., description="심각도 (CRITICAL, ERROR, WARNING, INFO)")
    message: str = Field(..., description="위반 설명")

    # 위치 정보
    line_number: Optional[int] = Field(None, description="라인 번호")
    column: Optional[int] = Field(None, description="컬럼 위치")
    code_snippet: Optional[str] = Field(None, description="위반 코드 조각")

    # 레이어 정보
    layer: Optional[str] = Field(
        None, description="대상 레이어 (DOMAIN, APPLICATION 등)"
    )
    class_type: Optional[str] = Field(
        None, description="클래스 타입 (AGGREGATE, USE_CASE 등)"
    )

    # 컨텍스트
    context: LogContext = Field(default_factory=LogContext, description="추가 컨텍스트")

    # 수정 관련
    was_auto_fixed: bool = Field(False, description="자동 수정 여부")
    fix_applied: Optional[str] = Field(None, description="적용된 수정 내용")

    def to_db_dict(self) -> dict:
        """데이터베이스 저장용 딕셔너리 변환"""
        return {
            "id": str(self.id),
            "timestamp": self.timestamp.isoformat(),
            "rule_code": self.rule_code,
            "rule_name": self.rule_name,
            "severity": self.severity,
            "message": self.message,
            "line_number": self.line_number,
            "column": self.column,
            "code_snippet": self.code_snippet,
            "layer": self.layer,
            "class_type": self.class_type,
            "project_id": self.context.project_id,
            "project_name": self.context.project_name,
            "file_path": self.context.file_path,
            "class_name": self.context.class_name,
            "method_name": self.context.method_name,
            "session_id": self.context.session_id,
            "user_id": self.context.user_id,
            "metadata": self.context.metadata,
            "was_auto_fixed": self.was_auto_fixed,
            "fix_applied": self.fix_applied,
        }

    @classmethod
    def from_violation(
        cls,
        violation: "Violation",  # validation/models.py의 Violation
        context: Optional[LogContext] = None,
        class_type: Optional[str] = None,
    ) -> "ViolationLog":
        """Violation 객체에서 ViolationLog 생성"""
        return cls(
            rule_code=violation.rule_code,
            rule_name=violation.rule_name,
            severity=violation.severity.value
            if hasattr(violation.severity, "value")
            else str(violation.severity),
            message=violation.message,
            line_number=violation.line_number,
            column=violation.column,
            code_snippet=violation.code_snippet,
            layer=violation.layer,
            class_type=class_type,
            context=context or LogContext(),
        )


class PatternType(str, Enum):
    """위반 패턴 유형"""

    RECURRING = "RECURRING"  # 반복 발생
    CORRELATED = "CORRELATED"  # 상관 관계 (특정 규칙이 함께 발생)
    TIME_BASED = "TIME_BASED"  # 시간 기반 (특정 시간대에 집중)
    PROJECT_SPECIFIC = "PROJECT_SPECIFIC"  # 프로젝트 특화
    USER_SPECIFIC = "USER_SPECIFIC"  # 사용자 특화


class ViolationPattern(BaseModel):
    """위반 패턴 분석 결과

    Task 5.2에서 PatternAnalyzer가 생성
    """

    id: UUID = Field(default_factory=uuid4, description="패턴 고유 ID")
    created_at: datetime = Field(
        default_factory=datetime.utcnow, description="발견 시각"
    )

    # 패턴 정보
    pattern_type: PatternType = Field(..., description="패턴 유형")
    rule_codes: list[str] = Field(..., description="관련 규칙 코드 목록")

    # 분석 결과
    occurrence_count: int = Field(..., description="발생 횟수")
    confidence: float = Field(..., ge=0.0, le=1.0, description="패턴 신뢰도")
    description: str = Field(..., description="패턴 설명")

    # 필터 조건 (어떤 조건에서 이 패턴이 발견되었는지)
    project_id: Optional[str] = Field(
        None, description="프로젝트 ID (프로젝트 특화 시)"
    )
    user_id: Optional[str] = Field(None, description="사용자 ID (사용자 특화 시)")
    layer: Optional[str] = Field(None, description="레이어 (레이어 특화 시)")

    # 권장 조치
    recommended_action: Optional[str] = Field(None, description="권장 조치 사항")


class RuleWeight(BaseModel):
    """규칙 가중치 정보

    Task 5.3에서 RuleWeightAdjuster가 관리
    """

    rule_code: str = Field(..., description="규칙 코드")

    # 기본 가중치 (1.0 = 기본)
    base_weight: float = Field(1.0, ge=0.0, le=5.0, description="기본 가중치")

    # 동적 조정된 가중치
    adjusted_weight: float = Field(1.0, ge=0.0, le=5.0, description="조정된 가중치")

    # 조정 이유
    adjustment_reason: Optional[str] = Field(None, description="가중치 조정 사유")

    # 통계 기반 정보
    violation_count: int = Field(0, description="총 위반 횟수")
    auto_fix_rate: float = Field(0.0, ge=0.0, le=1.0, description="자동 수정 비율")

    # 프로젝트별 오버라이드 (Task 5.4)
    project_overrides: dict[str, float] = Field(
        default_factory=dict,
        description="프로젝트별 가중치 오버라이드 {project_id: weight}",
    )

    # 메타데이터
    last_updated: datetime = Field(
        default_factory=datetime.utcnow, description="마지막 업데이트"
    )

    def get_weight_for_project(self, project_id: Optional[str] = None) -> float:
        """프로젝트별 가중치 반환"""
        if project_id and project_id in self.project_overrides:
            return self.project_overrides[project_id]
        return self.adjusted_weight
