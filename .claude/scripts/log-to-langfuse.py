#!/usr/bin/env python3
"""
LangFuse TDD Cycle Logger
Purpose: Kent Beck TDD 사이클 및 개발 메트릭을 LangFuse로 전송
Usage: python3 log-to-langfuse.py --event-type tdd_commit --data '{...}'
"""

import argparse
import json
import os
import sys
from datetime import datetime
from pathlib import Path

# JSONL 로그 파일 경로
LOG_DIR = Path.home() / ".claude" / "logs"
JSONL_LOG = LOG_DIR / "tdd-cycle.jsonl"


def ensure_log_dir():
    """로그 디렉토리 생성"""
    LOG_DIR.mkdir(parents=True, exist_ok=True)


def append_to_jsonl(event_type: str, data: dict):
    """JSONL 파일에 이벤트 추가"""
    ensure_log_dir()

    log_entry = {
        "timestamp": datetime.utcnow().isoformat() + "Z",
        "event_type": event_type,
        "data": data
    }

    with open(JSONL_LOG, "a") as f:
        f.write(json.dumps(log_entry, ensure_ascii=False) + "\n")


def extract_trace_id(commit_msg: str) -> str:
    """
    커밋 메시지에서 Trace ID 추출

    예시:
    - "test: Email VO 검증 테스트" → "Email-VO"
    - "feat: Member 생성 API" → "Member-생성-API"
    - "struct: Order 리팩토링" → "Order-리팩토링"
    """
    # 커밋 prefix 제거 (test:, feat:, struct:, fix:, chore: 등)
    msg = commit_msg
    for prefix in ["test:", "feat:", "impl:", "struct:", "fix:", "chore:", "docs:"]:
        if msg.startswith(prefix):
            msg = msg[len(prefix):].strip()
            break

    # 공백을 하이픈으로 변경
    trace_id = msg.replace(" ", "-")

    # 특수문자 제거 (하이픈, 한글, 영문, 숫자만 유지)
    import re
    trace_id = re.sub(r'[^a-zA-Z0-9가-힣-]', '', trace_id)

    # 최대 50자로 제한
    if len(trace_id) > 50:
        trace_id = trace_id[:50]

    return trace_id


def upload_to_langfuse(event_type: str, data: dict):
    """
    LangFuse SDK로 Span 업로드 (개별 Phase 측정)

    핵심 아이디어:
    - Red/Green/Structural 각각 독립적인 Span 생성
    - 같은 기능은 같은 Trace ID 사용 (커밋 메시지에서 추출)
    - 세션 관리 불필요 (각 커밋이 독립적)

    환경 변수 필요:
    - LANGFUSE_PUBLIC_KEY
    - LANGFUSE_SECRET_KEY
    - LANGFUSE_HOST (optional, default: https://us.cloud.langfuse.com)
    """
    try:
        from langfuse import Langfuse
    except ImportError:
        # langfuse SDK 없으면 JSONL만 저장
        return

    public_key = os.getenv("LANGFUSE_PUBLIC_KEY")
    secret_key = os.getenv("LANGFUSE_SECRET_KEY")

    if not public_key or not secret_key:
        # 환경 변수 없으면 JSONL만 저장
        return

    try:
        langfuse = Langfuse()

        if event_type == "tdd_commit":
            # 커밋 메시지에서 Trace ID 추출
            commit_msg = data.get("commit_msg", "unknown")
            trace_id = extract_trace_id(commit_msg)

            # Phase별 Span 이름
            phase = data.get("tdd_phase", "unknown")
            phase_names = {
                "red": "🔴 Red Phase",
                "green": "🟢 Green Phase",
                "structural": "♻️ Structural Phase"
            }
            span_name = phase_names.get(phase, f"{phase} Phase")

            # 독립적인 Span 생성 (즉시 시작하고 종료)
            span = langfuse.start_span(
                trace_id=trace_id,
                name=span_name,
                input={
                    "commit_hash": data.get("commit_hash"),
                    "commit_msg": commit_msg,
                    "files_changed": data.get("files_changed"),
                    "lines_changed": data.get("lines_changed")
                },
                metadata={
                    "project": data.get("project"),
                    "tdd_phase": phase,
                    "timestamp": data.get("timestamp")
                }
            )

            # 즉시 종료 (duration은 커밋 작업 시간)
            span.end()

        elif event_type == "tdd_test":
            # 테스트 결과도 Span으로 기록
            trace_id = "test-execution"

            span = langfuse.start_span(
                trace_id=trace_id,
                name="Test Execution",
                input={
                    "test_status": data.get("test_status")
                },
                metadata={
                    "tests_passed": data.get("tests_passed"),
                    "tests_failed": data.get("tests_failed"),
                    "duration_seconds": data.get("duration_seconds")
                }
            )
            span.end()

        elif event_type == "archunit_check":
            # ArchUnit 검증 결과
            trace_id = "archunit-check"

            span = langfuse.start_span(
                trace_id=trace_id,
                name="ArchUnit Validation",
                metadata={
                    "violations": data.get("violations"),
                    "timestamp": data.get("timestamp")
                }
            )
            span.end()

        # Flush to ensure upload
        langfuse.flush()

    except Exception as e:
        # 실패해도 조용히 넘어감 (개발 흐름 방해 안 함)
        pass


def main():
    parser = argparse.ArgumentParser(description="Log TDD cycle events to LangFuse")
    parser.add_argument("--event-type", required=True, help="Event type (tdd_commit, tdd_test, etc)")
    parser.add_argument("--data", required=True, help="Event data (JSON string)")

    args = parser.parse_args()

    try:
        data = json.loads(args.data)
    except json.JSONDecodeError:
        print(f"Error: Invalid JSON data: {args.data}", file=sys.stderr)
        sys.exit(1)

    # 1. JSONL 로그에 저장 (항상)
    append_to_jsonl(args.event_type, data)

    # 2. LangFuse에 업로드 (환경 변수 있을 때만)
    upload_to_langfuse(args.event_type, data)


if __name__ == "__main__":
    main()
