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
    커밋 메시지에서 Trace ID 추출 (32자 lowercase hex로 변환)

    LangFuse는 Trace ID가 32자 lowercase hexadecimal이어야 함
    → MD5 해시 사용 (32자 hex)

    예시:
    - "test: Email VO 검증 테스트" → "a1b2c3d4..." (32자 hex)
    - "feat: Member 생성 API"      → "e5f6g7h8..." (32자 hex)
    """
    import hashlib

    # 커밋 prefix 제거
    msg = commit_msg
    for prefix in ["test:", "feat:", "impl:", "struct:", "fix:", "chore:", "docs:"]:
        if msg.startswith(prefix):
            msg = msg[len(prefix):].strip()
            break

    # MD5 해시로 32자 hex 생성
    trace_id = hashlib.md5(msg.encode('utf-8')).hexdigest()

    return trace_id  # 32자 lowercase hex


def upload_to_langfuse(event_type: str, data: dict):
    """
    LangFuse SDK로 Span 업로드 (@observe 데코레이터 사용)

    핵심 아이디어:
    - @observe 데코레이터 + langfuse_trace_id로 같은 Trace에 Span 추가
    - Red/Green/Structural 각각 독립적인 함수 호출
    - 커밋 메시지에서 Trace ID 추출

    환경 변수 필요:
    - LANGFUSE_PUBLIC_KEY
    - LANGFUSE_SECRET_KEY
    - LANGFUSE_HOST (optional, default: https://us.cloud.langfuse.com)
    """
    try:
        from langfuse import observe
    except ImportError:
        # langfuse SDK 없으면 JSONL만 저장
        return

    public_key = os.getenv("LANGFUSE_PUBLIC_KEY")
    secret_key = os.getenv("LANGFUSE_SECRET_KEY")

    if not public_key or not secret_key:
        # 환경 변수 없으면 JSONL만 저장
        return

    try:
        import sys
        print(f"[DEBUG] LangFuse 업로드 시작: {event_type}", file=sys.stderr)

        if event_type == "tdd_commit":
            # 커밋 메시지에서 Trace ID 추출
            commit_msg = data.get("commit_msg", "unknown")
            trace_id = extract_trace_id(commit_msg)

            # Phase별 함수 정의
            phase = data.get("tdd_phase", "unknown")
            phase_names = {
                "red": "🔴 Red Phase",
                "green": "🟢 Green Phase",
                "structural": "♻️ Structural Phase"
            }
            span_name = phase_names.get(phase, f"{phase} Phase")

            # @observe 데코레이터를 동적으로 적용
            @observe(name=span_name)
            def log_phase(commit_data):
                """TDD Phase를 Span으로 기록"""
                return {
                    "commit_hash": commit_data.get("commit_hash"),
                    "commit_msg": commit_data.get("commit_msg"),
                    "files_changed": commit_data.get("files_changed"),
                    "tdd_phase": commit_data.get("tdd_phase")
                }

            # langfuse_trace_id로 같은 Trace에 추가!
            result = log_phase(
                data,
                langfuse_trace_id=trace_id
            )

            print(f"[DEBUG] Span 생성 완료: {trace_id} / {span_name}", file=sys.stderr)

        elif event_type == "tdd_test":
            @observe(name="Test Execution")
            def log_test(test_data):
                return test_data

            log_test(data, langfuse_trace_id="test-execution")

        elif event_type == "archunit_check":
            @observe(name="ArchUnit Validation")
            def log_archunit(check_data):
                return check_data

            log_archunit(data, langfuse_trace_id="archunit-check")

        print(f"[DEBUG] LangFuse 업로드 완료!", file=sys.stderr)

    except Exception as e:
        # 실패해도 조용히 넘어감 (개발 흐름 방해 안 함)
        print(f"[ERROR] LangFuse 업로드 실패: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc(file=sys.stderr)


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
