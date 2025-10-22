#!/bin/bash

# =====================================================
# Claude Hooks + Cache 시스템 설치 스크립트
# 이 스크립트를 실행하면 현재 프로젝트에 Claude 설정을 복사합니다.
# =====================================================

set -e

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# y/N 입력 검증 함수
ask_yes_no() {
    local prompt="$1"
    local reply

    while true; do
        read -p "$prompt (y/N): " -n 1 -r reply
        echo ""

        case "$reply" in
            [Yy])
                return 0  # Yes
                ;;
            [Nn]|"")
                return 1  # No (기본값)
                ;;
            *)
                echo -e "${RED}❌ 잘못된 입력입니다. y 또는 N을 입력하세요.${NC}"
                ;;
        esac
    done
}

# 현재 스크립트 위치
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOURCE_PROJECT="$(dirname "$SCRIPT_DIR")"
TARGET_PROJECT="$(pwd)"

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}🚀 Claude Hooks + Cache 시스템 설치${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# 설치 대상 디렉토리 확인
echo -e "${YELLOW}설치 대상 디렉토리:${NC} $TARGET_PROJECT"
echo ""

# 이미 설치되어 있는지 확인
if [[ -d "$TARGET_PROJECT/.claude/hooks" ]]; then
    echo -e "${YELLOW}⚠️  이미 Claude Hooks가 설치되어 있습니다.${NC}"
    echo ""

    if ask_yes_no "덮어쓰시겠습니까?"; then
        echo -e "${YELLOW}기존 설정을 백업합니다...${NC}"
        BACKUP_DIR="$TARGET_PROJECT/.claude/hooks.backup.$(date +%Y%m%d_%H%M%S)"
        mv "$TARGET_PROJECT/.claude/hooks" "$BACKUP_DIR"
        echo -e "${GREEN}✅ 백업 완료: $BACKUP_DIR${NC}"
        echo ""
    else
        echo -e "${RED}❌ 설치를 취소합니다.${NC}"
        exit 1
    fi
fi

# 필수 디렉토리 생성
echo -e "${BLUE}📁 디렉토리 구조 생성 중...${NC}"
mkdir -p "$TARGET_PROJECT/.claude/hooks/scripts"
mkdir -p "$TARGET_PROJECT/.claude/hooks/logs"
mkdir -p "$TARGET_PROJECT/.claude/cache/rules"
mkdir -p "$TARGET_PROJECT/.claude/commands/lib"

# Claude 설정 파일 복사
echo -e "${BLUE}📋 설정 파일 복사 중...${NC}"

# hooks.json 복사
cp "$SOURCE_PROJECT/.claude/hooks.json" "$TARGET_PROJECT/.claude/"

# Hooks 복사
cp "$SOURCE_PROJECT/.claude/hooks/user-prompt-submit.sh" "$TARGET_PROJECT/.claude/hooks/"
cp "$SOURCE_PROJECT/.claude/hooks/after-tool-use.sh" "$TARGET_PROJECT/.claude/hooks/"

# Scripts 복사
cp "$SOURCE_PROJECT/.claude/hooks/scripts/log-helper.py" "$TARGET_PROJECT/.claude/hooks/scripts/"
cp "$SOURCE_PROJECT/.claude/hooks/scripts/view-logs.sh" "$TARGET_PROJECT/.claude/hooks/scripts/"
cp "$SOURCE_PROJECT/.claude/hooks/scripts/validation-helper.py" "$TARGET_PROJECT/.claude/hooks/scripts/"
cp "$SOURCE_PROJECT/.claude/hooks/scripts/build-rule-cache.py" "$TARGET_PROJECT/.claude/hooks/scripts/"
cp "$SOURCE_PROJECT/.claude/hooks/scripts/init-session.sh" "$TARGET_PROJECT/.claude/hooks/scripts/"
cp "$SOURCE_PROJECT/.claude/hooks/scripts/preserve-rules.sh" "$TARGET_PROJECT/.claude/hooks/scripts/"

# Commands 복사
cp "$SOURCE_PROJECT/.claude/commands/lib/inject-rules.py" "$TARGET_PROJECT/.claude/commands/lib/"

# Slash Commands 복사
cp "$SOURCE_PROJECT/.claude/commands/README.md" "$TARGET_PROJECT/.claude/commands/"

# 레이어별 작업 모드 Commands
cp "$SOURCE_PROJECT/.claude/commands/domain.md" "$TARGET_PROJECT/.claude/commands/"
cp "$SOURCE_PROJECT/.claude/commands/application.md" "$TARGET_PROJECT/.claude/commands/"
cp "$SOURCE_PROJECT/.claude/commands/rest.md" "$TARGET_PROJECT/.claude/commands/"
cp "$SOURCE_PROJECT/.claude/commands/persistence.md" "$TARGET_PROJECT/.claude/commands/"
cp "$SOURCE_PROJECT/.claude/commands/test.md" "$TARGET_PROJECT/.claude/commands/"

# 코드 생성 Commands
cp "$SOURCE_PROJECT/.claude/commands/code-gen-controller.md" "$TARGET_PROJECT/.claude/commands/"
cp "$SOURCE_PROJECT/.claude/commands/code-gen-domain.md" "$TARGET_PROJECT/.claude/commands/"
cp "$SOURCE_PROJECT/.claude/commands/code-gen-usecase.md" "$TARGET_PROJECT/.claude/commands/"

# 검증 Commands
cp "$SOURCE_PROJECT/.claude/commands/validate-architecture.md" "$TARGET_PROJECT/.claude/commands/"
cp "$SOURCE_PROJECT/.claude/commands/validate-domain.md" "$TARGET_PROJECT/.claude/commands/"

# README 복사
cp "$SOURCE_PROJECT/.claude/hooks/logs/README.md" "$TARGET_PROJECT/.claude/hooks/logs/"

# 실행 권한 부여
echo -e "${BLUE}🔧 실행 권한 설정 중...${NC}"
chmod +x "$TARGET_PROJECT/.claude/hooks/user-prompt-submit.sh"
chmod +x "$TARGET_PROJECT/.claude/hooks/after-tool-use.sh"
chmod +x "$TARGET_PROJECT/.claude/hooks/scripts/"*.{sh,py}
chmod +x "$TARGET_PROJECT/.claude/commands/lib/"*.py

echo -e "${GREEN}✅ 파일 복사 완료${NC}"
echo ""

# CLAUDE.md 복사 여부 확인
if [[ ! -f "$TARGET_PROJECT/.claude/CLAUDE.md" ]]; then
    echo -e "${YELLOW}💡 CLAUDE.md 파일이 없습니다.${NC}"

    if ask_yes_no "템플릿 CLAUDE.md를 복사하시겠습니까?"; then
        cp "$SOURCE_PROJECT/.claude/CLAUDE.md" "$TARGET_PROJECT/.claude/"
        echo -e "${GREEN}✅ CLAUDE.md 복사 완료${NC}"
        echo -e "${YELLOW}⚠️  프로젝트에 맞게 CLAUDE.md를 수정하세요!${NC}"
        echo ""
    fi
fi

# 코딩 규칙 문서 복사 여부 확인
echo -e "${YELLOW}📚 코딩 규칙 문서 (docs/coding_convention/)${NC}"
echo "이 디렉토리는 프로젝트별로 다를 수 있습니다."
echo ""

if ask_yes_no "코딩 규칙 문서도 복사하시겠습니까?"; then
    if [[ -d "$TARGET_PROJECT/docs/coding_convention" ]]; then
        echo -e "${YELLOW}⚠️  기존 coding_convention 디렉토리를 백업합니다.${NC}"
        BACKUP_CONV="$TARGET_PROJECT/docs/coding_convention.backup.$(date +%Y%m%d_%H%M%S)"
        mv "$TARGET_PROJECT/docs/coding_convention" "$BACKUP_CONV"
        echo -e "${GREEN}✅ 백업 완료: $BACKUP_CONV${NC}"
    fi

    mkdir -p "$TARGET_PROJECT/docs"
    cp -r "$SOURCE_PROJECT/docs/coding_convention" "$TARGET_PROJECT/docs/"
    echo -e "${GREEN}✅ 코딩 규칙 문서 복사 완료${NC}"
    echo ""
else
    echo -e "${YELLOW}⚠️  코딩 규칙 문서를 복사하지 않았습니다.${NC}"
    echo -e "${YELLOW}   Cache 빌드를 위해 docs/coding_convention/ 디렉토리가 필요합니다.${NC}"
    echo ""
fi

# Python 의존성 확인
echo -e "${BLUE}🐍 Python 의존성 확인 중...${NC}"
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}❌ Python 3가 설치되지 않았습니다.${NC}"
    echo "Python 3를 설치한 후 다시 실행하세요."
    exit 1
fi

# tiktoken 설치 확인
if ! python3 -c "import tiktoken" 2>/dev/null; then
    echo -e "${YELLOW}⚠️  tiktoken이 설치되지 않았습니다.${NC}"

    if ask_yes_no "tiktoken을 설치하시겠습니까?"; then
        pip3 install tiktoken
        echo -e "${GREEN}✅ tiktoken 설치 완료${NC}"
    else
        echo -e "${YELLOW}⚠️  Cache 빌드를 위해 tiktoken이 필요합니다.${NC}"
    fi
fi
echo ""

# jq 설치 확인
echo -e "${BLUE}🔧 jq 설치 확인 중...${NC}"
if ! command -v jq &> /dev/null; then
    echo -e "${YELLOW}⚠️  jq가 설치되지 않았습니다 (JSON 로그 분석 도구).${NC}"
    echo ""
    echo "jq 설치 방법:"
    echo "  macOS: brew install jq"
    echo "  Ubuntu: sudo apt-get install jq"
    echo ""
else
    echo -e "${GREEN}✅ jq 설치 확인 완료${NC}"
fi
echo ""

# Cache 빌드 여부 확인
if [[ -d "$TARGET_PROJECT/docs/coding_convention" ]]; then
    echo -e "${BLUE}💾 Cache 빌드${NC}"

    if ask_yes_no "지금 Cache를 빌드하시겠습니까?"; then
        cd "$TARGET_PROJECT"
        python3 .claude/hooks/scripts/build-rule-cache.py
        echo -e "${GREEN}✅ Cache 빌드 완료${NC}"
        echo ""
    else
        echo -e "${YELLOW}⚠️  나중에 다음 명령어로 Cache를 빌드하세요:${NC}"
        echo "   python3 .claude/hooks/scripts/build-rule-cache.py"
        echo ""
    fi
else
    echo -e "${YELLOW}⚠️  docs/coding_convention/ 디렉토리가 없어 Cache를 빌드할 수 없습니다.${NC}"
    echo "코딩 규칙 문서를 준비한 후 다음 명령어로 Cache를 빌드하세요:"
    echo "   python3 .claude/hooks/scripts/build-rule-cache.py"
    echo ""
fi

# Git Pre-commit Hooks 설치 여부 확인
echo -e "${BLUE}🔗 Git Pre-commit Hooks (선택사항)${NC}"
echo "Git pre-commit hooks는 커밋 시점에 코드를 검증합니다."
echo "※ 주의: Spring 프로젝트 전용 검증 로직이 포함되어 있습니다."
echo ""
echo "검증 항목:"
echo "  - Transaction 경계 검증 (@Transactional 내 외부 API 호출)"
echo "  - Spring 프록시 제약사항 (Private/Final 메서드)"
echo "  - Lombok 사용 금지"
echo "  - Law of Demeter (Getter 체이닝)"
echo ""

if ask_yes_no "Git pre-commit hooks를 설치하시겠습니까?"; then
    # .git 디렉토리 존재 확인
    if [[ ! -d "$TARGET_PROJECT/.git" ]]; then
        echo -e "${RED}❌ Git 저장소가 아닙니다 (.git 디렉토리 없음).${NC}"
        echo -e "${YELLOW}   git init 실행 후 다시 시도하세요.${NC}"
        echo ""
    else
        # hooks 디렉토리 확인
        if [[ ! -d "$SOURCE_PROJECT/hooks" ]]; then
            echo -e "${RED}❌ 소스 프로젝트에 hooks/ 디렉토리가 없습니다.${NC}"
            echo ""
        else
            # 기존 pre-commit hook 백업
            if [[ -f "$TARGET_PROJECT/.git/hooks/pre-commit" ]] || [[ -L "$TARGET_PROJECT/.git/hooks/pre-commit" ]]; then
                echo -e "${YELLOW}⚠️  기존 pre-commit hook을 백업합니다.${NC}"
                BACKUP_HOOK="$TARGET_PROJECT/.git/hooks/pre-commit.backup.$(date +%Y%m%d_%H%M%S)"
                mv "$TARGET_PROJECT/.git/hooks/pre-commit" "$BACKUP_HOOK"
                echo -e "${GREEN}✅ 백업 완료: $BACKUP_HOOK${NC}"
            fi

            # 프로젝트 루트에 hooks 디렉토리 복사
            echo -e "${BLUE}📋 Git hooks 파일 복사 중...${NC}"
            mkdir -p "$TARGET_PROJECT/hooks/validators"
            cp "$SOURCE_PROJECT/hooks/pre-commit" "$TARGET_PROJECT/hooks/"
            cp -r "$SOURCE_PROJECT/hooks/validators/"* "$TARGET_PROJECT/hooks/validators/"

            # 실행 권한 부여
            chmod +x "$TARGET_PROJECT/hooks/pre-commit"
            chmod +x "$TARGET_PROJECT/hooks/validators/"*.sh

            # .git/hooks에 심볼릭 링크 생성
            ln -sf "../../hooks/pre-commit" "$TARGET_PROJECT/.git/hooks/pre-commit"

            echo -e "${GREEN}✅ Git pre-commit hooks 설치 완료${NC}"
            echo -e "${BLUE}   위치: hooks/pre-commit${NC}"
            echo -e "${BLUE}   심볼릭 링크: .git/hooks/pre-commit → ../../hooks/pre-commit${NC}"
            echo ""
            echo -e "${YELLOW}💡 프로젝트에 맞게 hooks/validators/ 스크립트를 수정하세요!${NC}"
            echo ""
        fi
    fi
else
    echo -e "${YELLOW}⚠️  Git pre-commit hooks를 설치하지 않았습니다.${NC}"
    echo -e "${YELLOW}   나중에 설치하려면: ln -sf ../../hooks/pre-commit .git/hooks/pre-commit${NC}"
    echo ""
fi

# 완료 메시지
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ 설치 완료!${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${BLUE}📖 다음 단계:${NC}"
echo ""
echo "1. 프로젝트별 설정 수정:"
echo "   - .claude/CLAUDE.md 편집 (프로젝트 정보 업데이트)"
echo "   - docs/coding_convention/ 규칙 추가/수정"
if [[ -d "$TARGET_PROJECT/hooks" ]]; then
    echo "   - hooks/validators/ 스크립트 수정 (프로젝트 검증 규칙)"
fi
echo ""
echo "2. Cache 빌드 (규칙 변경 시마다):"
echo "   python3 .claude/hooks/scripts/build-rule-cache.py"
echo ""
echo "3. 로그 확인:"
echo "   ./.claude/hooks/scripts/view-logs.sh"
echo "   ./.claude/hooks/scripts/view-logs.sh -f  # 실시간"
echo "   ./.claude/hooks/scripts/view-logs.sh -s  # 통계"
echo ""
if [[ -L "$TARGET_PROJECT/.git/hooks/pre-commit" ]]; then
    echo "4. Git pre-commit hooks 테스트:"
    echo "   git add <file>"
    echo "   git commit -m \"test\" # 검증 자동 실행"
    echo ""
fi
echo -e "${YELLOW}💡 Claude Code에서 다음과 같이 사용하세요:${NC}"
echo "   - domain, usecase, controller 등 키워드 입력"
echo "   - 자동으로 Layer별 규칙이 주입되고 검증됩니다"
echo ""
