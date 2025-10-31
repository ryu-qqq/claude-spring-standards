#!/bin/bash
# LangFuse 업로드 스크립트
#
# 용도: Claude Code Hook 로그 및 Pipeline 메트릭을 LangFuse로 업로드
# SSOT: Workflow upload-langfuse.md의 실제 구현체

set -e  # 에러 발생 시 즉시 종료

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 프로젝트 루트 디렉토리
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo -e "${BLUE}🚀 LangFuse Upload Pipeline${NC}"
echo "   Project: $PROJECT_ROOT"
echo ""

# 환경 변수 확인
check_env_vars() {
    if [ -z "$LANGFUSE_PUBLIC_KEY" ]; then
        echo -e "${RED}❌ Error: LANGFUSE_PUBLIC_KEY not set${NC}"
        echo ""
        echo "   Set environment variables:"
        echo "   export LANGFUSE_PUBLIC_KEY='pk-lf-...'"
        echo "   export LANGFUSE_SECRET_KEY='sk-lf-...'"
        echo "   export LANGFUSE_HOST='https://cloud.langfuse.com'"
        echo ""
        return 1
    fi

    if [ -z "$LANGFUSE_SECRET_KEY" ]; then
        echo -e "${RED}❌ Error: LANGFUSE_SECRET_KEY not set${NC}"
        return 1
    fi

    echo -e "${GREEN}✅ Environment variables configured${NC}"
    echo "   Host: ${LANGFUSE_HOST:-https://cloud.langfuse.com}"
    echo ""
    return 0
}

# Python 확인
check_python() {
    if ! command -v python3 &> /dev/null; then
        echo -e "${RED}❌ Error: python3 not found${NC}"
        return 1
    fi

    echo -e "${GREEN}✅ Python 3 available${NC}"
    echo "   Version: $(python3 --version)"
    echo ""
    return 0
}

# 로그 파일 확인
check_logs() {
    local claude_logs="$PROJECT_ROOT/.claude/hooks/logs/hook-execution.jsonl"
    local pipeline_metrics="$PROJECT_ROOT/.pipeline-metrics/metrics.jsonl"

    if [ ! -f "$claude_logs" ]; then
        echo -e "${YELLOW}⚠️  Warning: Claude logs not found${NC}"
        echo "   Path: $claude_logs"
        echo "   Note: Logs are created after first Hook execution"
        echo ""
    else
        local log_size=$(wc -l < "$claude_logs")
        echo -e "${GREEN}✅ Claude logs found${NC}"
        echo "   Path: $claude_logs"
        echo "   Events: $log_size"
        echo ""
    fi

    if [ ! -f "$pipeline_metrics" ]; then
        echo -e "${YELLOW}⚠️  Pipeline metrics not found (optional)${NC}"
        echo "   Path: $pipeline_metrics"
        echo ""
    else
        echo -e "${GREEN}✅ Pipeline metrics found${NC}"
        echo "   Path: $pipeline_metrics"
        echo ""
    fi
}

# 1단계: 로그 집계
aggregate_logs() {
    echo -e "${BLUE}📊 Step 1: Aggregating logs...${NC}"
    echo ""

    local output_file="$PROJECT_ROOT/langfuse-data.json"

    python3 "$PROJECT_ROOT/scripts/langfuse/aggregate-logs.py" \
        --claude-logs "$PROJECT_ROOT/.claude/hooks/logs/hook-execution.jsonl" \
        --pipeline-metrics "$PROJECT_ROOT/.pipeline-metrics/metrics.jsonl" \
        --output "$output_file" \
        --anonymize

    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}✅ Log aggregation complete${NC}"
        echo "   Output: $output_file"

        # 통계 출력
        if command -v jq &> /dev/null; then
            local traces=$(jq '.traces | length' "$output_file")
            local observations=$(jq '.observations | length' "$output_file")
            echo "   Traces: $traces"
            echo "   Observations: $observations"
        fi
        echo ""
        return 0
    else
        echo -e "${RED}❌ Log aggregation failed${NC}"
        return 1
    fi
}

# 2단계: LangFuse 업로드
upload_to_langfuse() {
    echo -e "${BLUE}📤 Step 2: Uploading to LangFuse...${NC}"
    echo ""

    local input_file="$PROJECT_ROOT/langfuse-data.json"

    python3 "$PROJECT_ROOT/scripts/langfuse/upload-to-langfuse.py" \
        --input "$input_file"

    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}✅ Upload complete!${NC}"
        echo ""
        echo -e "${BLUE}📊 View your metrics:${NC}"
        echo "   ${LANGFUSE_HOST:-https://cloud.langfuse.com}"
        echo ""
        return 0
    else
        echo -e "${RED}❌ Upload failed${NC}"
        return 1
    fi
}

# 메인 실행
main() {
    cd "$PROJECT_ROOT"

    # 사전 검증
    check_python || exit 1
    check_env_vars || exit 1
    check_logs

    # 실행
    aggregate_logs || exit 1
    upload_to_langfuse || exit 1

    echo -e "${GREEN}✨ LangFuse upload pipeline completed successfully!${NC}"
    echo ""
}

# 스크립트 실행
main "$@"
