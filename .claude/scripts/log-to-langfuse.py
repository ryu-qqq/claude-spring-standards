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


def upload_to_langfuse(event_type: str, data: dict):
    """
    LangFuse SDK로 업로드
    환경 변수 필요:
    - LANGFUSE_PUBLIC_KEY
    - LANGFUSE_SECRET_KEY
    - LANGFUSE_HOST (optional, default: https://us.cloud.langfuse.com)
    """
    try:
        from langfuse import Langfuse
        from langfuse.types import TraceContext
        import uuid
    except ImportError:
        # langfuse SDK 없으면 JSONL만 저장
        return

    public_key = os.getenv("LANGFUSE_PUBLIC_KEY")
    secret_key = os.getenv("LANGFUSE_SECRET_KEY")

    if not public_key or not secret_key:
        # 환경 변수 없으면 JSONL만 저장
        return

    try:
        # LangFuse 클라이언트 생성
        langfuse = Langfuse()

        # Trace ID 생성 (32 lowercase hex chars)
        trace_id = uuid.uuid4().hex
        project = data.get("project", "unknown")

        # Trace context 생성
        trace_context = TraceContext(
            trace_id=trace_id,
            trace_name=f"TDD Cycle - {project}"
        )

        # 이벤트 타입별 이름 매핑
        event_name_map = {
            "tdd_commit": f"TDD Commit - {data.get('tdd_phase', 'unknown')}",
            "tdd_test": "Test Execution",
            "archunit_check": "ArchUnit Validation"
        }
        event_name = event_name_map.get(event_type, event_type)

        # Event 생성
        langfuse.create_event(
            trace_context=trace_context,
            name=event_name,
            metadata=data
        )

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
