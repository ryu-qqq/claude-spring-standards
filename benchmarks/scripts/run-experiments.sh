#!/bin/bash
#
# Automated Experiment Runner
#
# 동일한 PRD로 3회 독립적인 코드 생성을 수행하고 일관성을 검증합니다.
#
# Usage:
#   ./run-experiments.sh
#

set -e

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 프로젝트 루트 디렉토리
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BENCHMARKS_DIR="$PROJECT_ROOT/benchmarks"
RESULTS_DIR="$BENCHMARKS_DIR/results"

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Automated Code Generation Experiment${NC}"
echo -e "${BLUE}  PRD: prd/order-management.md${NC}"
echo -e "${BLUE}============================================${NC}\n"

# 1. 기존 결과 백업 (있는 경우)
if [ -d "$RESULTS_DIR/run-1" ] || [ -d "$RESULTS_DIR/run-2" ] || [ -d "$RESULTS_DIR/run-3" ]; then
    echo -e "${YELLOW}⚠️  Previous results detected. Backing up...${NC}"
    BACKUP_DIR="$RESULTS_DIR/backup-$(date +%Y%m%d-%H%M%S)"
    mkdir -p "$BACKUP_DIR"
    [ -d "$RESULTS_DIR/run-1" ] && mv "$RESULTS_DIR/run-1" "$BACKUP_DIR/"
    [ -d "$RESULTS_DIR/run-2" ] && mv "$RESULTS_DIR/run-2" "$BACKUP_DIR/"
    [ -d "$RESULTS_DIR/run-3" ] && mv "$RESULTS_DIR/run-3" "$BACKUP_DIR/"
    echo -e "${GREEN}✅ Backup complete: $BACKUP_DIR${NC}\n"
fi

# 2. 결과 디렉토리 초기화
mkdir -p "$RESULTS_DIR/run-1" "$RESULTS_DIR/run-2" "$RESULTS_DIR/run-3"

# 3. 안내 메시지
echo -e "${BLUE}📝 Instructions:${NC}"
echo -e "   This script will guide you through 3 independent code generation runs."
echo -e "   For each run, you'll use Claude Code with the following command:\n"
echo -e "   ${GREEN}/code-gen-domain Order prd/order-management.md${NC}\n"
echo -e "   After generation, copy the resulting files to the specified directory.\n"

# Run 1
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Run 1/3${NC}"
echo -e "${BLUE}============================================${NC}"
echo -e "\n1. Open Claude Code"
echo -e "2. Run: ${GREEN}/code-gen-domain Order prd/order-management.md${NC}"
echo -e "3. Copy generated files to: ${YELLOW}$RESULTS_DIR/run-1/${NC}\n"
read -p "Press Enter when Run 1 is complete..."

# 생성 확인
if [ ! -d "$RESULTS_DIR/run-1" ] || [ -z "$(ls -A $RESULTS_DIR/run-1 2>/dev/null)" ]; then
    echo -e "${YELLOW}⚠️  Warning: No files found in run-1/. Please copy the generated files.${NC}"
    read -p "Press Enter to continue after copying files..."
fi

echo -e "${GREEN}✅ Run 1 complete${NC}\n"
sleep 2

# Run 2
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Run 2/3${NC}"
echo -e "${BLUE}============================================${NC}"
echo -e "\n1. **IMPORTANT**: Clear Claude Code context (start new session)"
echo -e "2. Run: ${GREEN}/code-gen-domain Order prd/order-management.md${NC}"
echo -e "3. Copy generated files to: ${YELLOW}$RESULTS_DIR/run-2/${NC}\n"
read -p "Press Enter when Run 2 is complete..."

if [ ! -d "$RESULTS_DIR/run-2" ] || [ -z "$(ls -A $RESULTS_DIR/run-2 2>/dev/null)" ]; then
    echo -e "${YELLOW}⚠️  Warning: No files found in run-2/. Please copy the generated files.${NC}"
    read -p "Press Enter to continue after copying files..."
fi

echo -e "${GREEN}✅ Run 2 complete${NC}\n"
sleep 2

# Run 3
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Run 3/3${NC}"
echo -e "${BLUE}============================================${NC}"
echo -e "\n1. **IMPORTANT**: Clear Claude Code context (start new session)"
echo -e "2. Run: ${GREEN}/code-gen-domain Order prd/order-management.md${NC}"
echo -e "3. Copy generated files to: ${YELLOW}$RESULTS_DIR/run-3/${NC}\n"
read -p "Press Enter when Run 3 is complete..."

if [ ! -d "$RESULTS_DIR/run-3" ] || [ -z "$(ls -A $RESULTS_DIR/run-3 2>/dev/null)" ]; then
    echo -e "${YELLOW}⚠️  Warning: No files found in run-3/. Please copy the generated files.${NC}"
    read -p "Press Enter to continue after copying files..."
fi

echo -e "${GREEN}✅ Run 3 complete${NC}\n"

# 4. 일관성 검증 실행
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  Running Consistency Check${NC}"
echo -e "${BLUE}============================================${NC}\n"

cd "$PROJECT_ROOT"
python3 "$BENCHMARKS_DIR/scripts/consistency-check.py" \
    "$RESULTS_DIR/run-1" \
    "$RESULTS_DIR/run-2" \
    "$RESULTS_DIR/run-3"

# 5. 토큰 벤치마크 실행
echo -e "\n${BLUE}============================================${NC}"
echo -e "${BLUE}  Running Token Benchmark${NC}"
echo -e "${BLUE}============================================${NC}\n"

python3 "$BENCHMARKS_DIR/scripts/token-benchmark.py" --layer domain

# 6. 최종 리포트
echo -e "\n${GREEN}============================================${NC}"
echo -e "${GREEN}  Experiment Complete!${NC}"
echo -e "${GREEN}============================================${NC}\n"
echo -e "📊 Results:"
echo -e "   - Consistency Report: ${YELLOW}$RESULTS_DIR/consistency-report.json${NC}"
echo -e "   - Token Benchmark: ${YELLOW}$RESULTS_DIR/token-comparison.json${NC}"
echo -e "   - Generated Code:"
echo -e "     • Run 1: ${YELLOW}$RESULTS_DIR/run-1/${NC}"
echo -e "     • Run 2: ${YELLOW}$RESULTS_DIR/run-2/${NC}"
echo -e "     • Run 3: ${YELLOW}$RESULTS_DIR/run-3/${NC}\n"

echo -e "📝 Next Steps:"
echo -e "   1. Review consistency report"
echo -e "   2. Capture screenshots for documentation"
echo -e "   3. Update tutorials with actual results\n"
