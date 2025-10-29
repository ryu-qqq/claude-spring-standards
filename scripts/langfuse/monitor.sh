#!/bin/bash
# LangFuse 실시간 모니터링 스크립트
#
# Usage:
#   export LANGFUSE_PUBLIC_KEY="pk-lf-..."
#   export LANGFUSE_SECRET_KEY="sk-lf-..."
#   bash scripts/langfuse/monitor.sh

set -e

# 설정
CLAUDE_LOGS=".claude/hooks/logs/hook-execution.jsonl"
CASCADE_LOGS=".cascade/metrics.jsonl"
INTERVAL=${LANGFUSE_MONITOR_INTERVAL:-300}  # 기본 5분

# 텔레메트리 설정 읽기
TELEMETRY_ENABLED=false
if [[ -f ".langfuse.telemetry" ]]; then
    while IFS='=' read -r key value; do
        case "$key" in
            enabled) TELEMETRY_ENABLED="$value" ;;
        esac
    done < .langfuse.telemetry
fi

# 텔레메트리가 비활성화되어 있으면 종료
if [[ "$TELEMETRY_ENABLED" != "true" ]]; then
    log_error "Telemetry is disabled in .langfuse.telemetry"
    echo "   To enable telemetry:"
    echo "   1. Edit .langfuse.telemetry and set enabled=true"
    echo "   2. Or re-run: bash scripts/install-claude-hooks.sh"
    exit 1
fi

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 함수: 로그 메시지
log() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} ✅ $1"
}

log_error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} ❌ $1"
}

log_warning() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} ⚠️  $1"
}

# 환경 변수 확인
if [ -z "$LANGFUSE_PUBLIC_KEY" ] || [ -z "$LANGFUSE_SECRET_KEY" ]; then
    log_error "LangFuse credentials not found"
    echo "   Set environment variables:"
    echo "   export LANGFUSE_PUBLIC_KEY='pk-lf-...'"
    echo "   export LANGFUSE_SECRET_KEY='sk-lf-...'"
    exit 1
fi

# Python 확인
if ! command -v python3 &> /dev/null; then
    log_error "Python 3 not found"
    exit 1
fi

# 스크립트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 배너 출력
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 LangFuse Monitor Started (Telemetry Mode)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "   Claude logs:  $CLAUDE_LOGS"
echo "   Cascade logs: $CASCADE_LOGS"
echo "   Interval:     ${INTERVAL}s"
echo "   Telemetry:    enabled (anonymized)"
echo "   Host:         https://us.cloud.langfuse.com"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 카운터
UPLOAD_COUNT=0
SUCCESS_COUNT=0
FAIL_COUNT=0

# 메인 루프
while true; do
    log "Aggregating logs..."

    # 로그 집계 (텔레메트리 모드)
    if python3 "$SCRIPT_DIR/aggregate-logs.py" \
        --claude-logs "$CLAUDE_LOGS" \
        --cascade-logs "$CASCADE_LOGS" \
        --output "/tmp/langfuse-data-$$.json" \
        --telemetry 2>&1; then

        log_success "Logs aggregated"

        # LangFuse 업로드 (텔레메트리 모드)
        log "Uploading to LangFuse..."

        if python3 "$SCRIPT_DIR/upload-to-langfuse.py" \
            --input "/tmp/langfuse-data-$$.json" \
            --telemetry 2>&1; then

            log_success "Upload complete"
            ((UPLOAD_COUNT++))
            ((SUCCESS_COUNT++))

            # 임시 파일 삭제
            rm -f "/tmp/langfuse-data-$$.json"
        else
            log_error "Upload failed"
            ((UPLOAD_COUNT++))
            ((FAIL_COUNT++))
        fi
    else
        log_error "Aggregation failed"
        ((FAIL_COUNT++))
    fi

    # 통계 출력
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "   Uploads: $UPLOAD_COUNT | Success: $SUCCESS_COUNT | Failed: $FAIL_COUNT"
    echo "   Next upload in ${INTERVAL}s (Ctrl+C to stop)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""

    # 대기
    sleep "$INTERVAL"
done
