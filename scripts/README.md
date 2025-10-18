# 설치 스크립트

## install-claude-hooks.sh

Claude Hooks + Cache 시스템을 다른 프로젝트에 설치하는 대화형 스크립트입니다.

### 사용법

```bash
# 1. 이 프로젝트를 임시로 클론
git clone https://github.com/your-org/claude-spring-standards.git /tmp/claude-spring-standards

# 2. 본인 프로젝트로 이동
cd your-project

# 3. 설치 스크립트 실행
bash /tmp/claude-spring-standards/scripts/install-claude-hooks.sh

# 4. 완료 후 임시 디렉토리 삭제
rm -rf /tmp/claude-spring-standards
```

### 설치되는 항목

#### 필수 항목 (자동 설치)
- `.claude/hooks/user-prompt-submit.sh` - 사용자 입력 Hook
- `.claude/hooks/after-tool-use.sh` - 도구 사용 후 Hook
- `.claude/hooks/scripts/log-helper.py` - JSON 로그 헬퍼
- `.claude/hooks/scripts/view-logs.sh` - 로그 뷰어
- `.claude/hooks/scripts/validation-helper.py` - 검증 헬퍼
- `.claude/hooks/scripts/build-rule-cache.py` - Cache 빌드 스크립트
- `.claude/commands/lib/inject-rules.py` - 규칙 주입 스크립트
- `.claude/commands/code-gen-domain.md` - Domain 코드 생성 명령
- `.claude/commands/code-gen-usecase.md` - UseCase 코드 생성 명령
- `.claude/commands/code-gen-controller.md` - Controller 코드 생성 명령
- `.claude/commands/validate-domain.md` - Domain 검증 명령
- `.claude/commands/validate-architecture.md` - 아키텍처 검증 명령
- `.claude/commands/README.md` - Commands 설명
- `.claude/hooks/logs/README.md` - 로그 시스템 설명

#### 선택 항목 (대화형 선택)
- `.claude/CLAUDE.md` - Claude Code 프로젝트 설정
- `docs/coding_convention/` - 코딩 규칙 문서 (90개 규칙)

### 설치 후 작업

#### 1. 프로젝트별 설정 수정

```bash
# CLAUDE.md 편집 (프로젝트 정보 업데이트)
vim .claude/CLAUDE.md
```

#### 2. 코딩 규칙 추가/수정

```bash
# 기존 규칙 수정 또는 새 규칙 추가
vim docs/coding_convention/02-domain-layer/...
```

#### 3. Cache 빌드

```bash
# 규칙 문서를 JSON Cache로 변환
python3 .claude/hooks/scripts/build-rule-cache.py
```

### 의존성

스크립트가 자동으로 확인하는 항목:

- **Python 3**: 필수 (스크립트 실행용)
- **tiktoken**: 선택 설치 (Token 측정용)
- **jq**: 권장 (JSON 로그 분석용)

### 백업

스크립트는 다음과 같이 자동 백업합니다:

- 기존 `.claude/hooks/` → `.claude/hooks.backup.YYYYMMDD_HHMMSS`
- 기존 `docs/coding_convention/` → `docs/coding_convention.backup.YYYYMMDD_HHMMSS`

### 실행 예시

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🚀 Claude Hooks + Cache 시스템 설치
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

설치 대상 디렉토리: /Users/user/my-project

⚠️  이미 Claude Hooks가 설치되어 있습니다.

덮어쓰시겠습니까? (y/N): y

기존 설정을 백업합니다...
✅ 백업 완료: /Users/user/my-project/.claude/hooks.backup.20251017_143000

📁 디렉토리 구조 생성 중...
📋 설정 파일 복사 중...
🔧 실행 권한 설정 중...
✅ 파일 복사 완료

💡 CLAUDE.md 파일이 없습니다.
템플릿 CLAUDE.md를 복사하시겠습니까? (y/N): y
✅ CLAUDE.md 복사 완료
⚠️  프로젝트에 맞게 CLAUDE.md를 수정하세요!

📚 코딩 규칙 문서 (docs/coding_convention/)
이 디렉토리는 프로젝트별로 다를 수 있습니다.

코딩 규칙 문서도 복사하시겠습니까? (y/N): y
✅ 코딩 규칙 문서 복사 완료

🐍 Python 의존성 확인 중...
✅ Python 3 확인 완료
⚠️  tiktoken이 설치되지 않았습니다.
tiktoken을 설치하시겠습니까? (y/N): y
✅ tiktoken 설치 완료

🔧 jq 설치 확인 중...
✅ jq 설치 확인 완료

💾 Cache 빌드
지금 Cache를 빌드하시겠습니까? (y/N): y
✅ Cache 빌드 완료

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ 설치 완료!
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📖 다음 단계:

1. 프로젝트별 설정 수정:
   - .claude/CLAUDE.md 편집 (프로젝트 정보 업데이트)
   - docs/coding_convention/ 규칙 추가/수정

2. Cache 빌드 (규칙 변경 시마다):
   python3 .claude/hooks/scripts/build-rule-cache.py

3. 로그 확인:
   ./.claude/hooks/scripts/view-logs.sh
   ./.claude/hooks/scripts/view-logs.sh -f  # 실시간
   ./.claude/hooks/scripts/view-logs.sh -s  # 통계

💡 Claude Code에서 다음과 같이 사용하세요:
   - domain, usecase, controller 등 키워드 입력
   - 자동으로 Layer별 규칙이 주입되고 검증됩니다
```

### 문제 해결

#### 권한 오류
```bash
chmod +x /tmp/claude-spring-standards/scripts/install-claude-hooks.sh
```

#### Python 없음
```bash
# macOS
brew install python3

# Ubuntu
sudo apt-get install python3
```

#### jq 없음
```bash
# macOS
brew install jq

# Ubuntu
sudo apt-get install jq
```

### 제거 방법

```bash
# Claude 설정 완전 제거
rm -rf .claude/hooks
rm -rf .claude/cache
rm -rf .claude/commands/lib/inject-rules.py

# 코딩 규칙 문서도 제거 (선택)
rm -rf docs/coding_convention
```
