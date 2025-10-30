#!/bin/bash
# Cascade Logger - Windsurf/Cascade 로그를 .cascade/metrics.jsonl에 기록
#
# Usage:
#   1. Windsurf에서 Cascade 실행 시 자동으로 이 스크립트를 호출하도록 설정
#   2. 또는 수동으로: bash .windsurf/cascade-logger.sh "task_name" "status" "duration"

set -euo pipefail

# 로그 디렉토리
LOG_DIR=".cascade"
LOG_FILE="${LOG_DIR}/metrics.jsonl"

# 디렉토리 생성
mkdir -p "${LOG_DIR}"

# 인자 파싱
TASK_NAME="${1:-unknown}"
STATUS_CODE="${2:-0}"
DURATION="${3:-0}"
TIMESTAMP=$(date +%s)

# JSONL 형식으로 로그 기록
echo -e "${TIMESTAMP}\t${TASK_NAME}\t${STATUS_CODE}\t${DURATION}" >> "${LOG_FILE}"

echo "✅ Cascade 로그 기록됨: ${TASK_NAME} (status: ${STATUS_CODE}, duration: ${DURATION}s)"
