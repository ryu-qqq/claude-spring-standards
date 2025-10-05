# claude-spring-standards 팩트체크 및 구현 TODO

## 📋 검증 대상 항목

블로그에서 언급했지만 실제 레포에서 **확인이 필요하거나 구현 여부가 불명확한** 항목들입니다.

---

## 🔍 SECTION 1: Claude Code Hooks 관련

### 1.1 `.claude/` 디렉토리 구조 존재 여부

**블로그에서 제안한 구조:**
```
.claude/
├── settings.json          # 프로젝트 공유 설정
├── settings.local.json    # 개인 설정 (.gitignore 추천)
└── scripts/
    ├── init-session.sh
    ├── validate-architecture.sh
    ├── validate-prompt.sh
    ├── preserve-rules.sh
    └── inject-rules.sh
```

**확인 사항:**
- [ ] `.claude/` 디렉토리가 존재하는가?
- [ ] `settings.json` 파일이 존재하는가?
- [ ] `settings.local.json.example` 같은 템플릿이 있는가?
- [ ] `.gitignore`에 `settings.local.json`이 포함되어 있는가?
- [ ] `scripts/` 디렉토리가 존재하는가?

**TODO (존재하지 않을 경우):**
```bash
# 1. 디렉토리 생성
mkdir -p .claude/scripts

# 2. .gitignore에 추가
echo ".claude/settings.local.json" >> .gitignore

# 3. 템플릿 파일 생성
touch .claude/settings.json.example
touch .claude/settings.local.json.example
```

---

### 1.2 Hook 스크립트 구현 여부

**블로그에서 언급한 각 Hook:**

#### 1.2.1 SessionStart Hook (`init-session.sh`)

**기대 기능:**
- 현재 브랜치에서 지라 태스크 파싱 (예: `feature/FF-123-xxx`)
- 아키텍처 규칙 요약 생성
- 세션 컨텍스트 파일 생성 (`/tmp/claude-session-context.md`)

**확인 사항:**
- [ ] `.claude/scripts/init-session.sh` 파일이 존재하는가?
- [ ] 스크립트가 실행 가능한가? (`chmod +x` 확인)
- [ ] 지라 태스크 파싱 로직이 구현되어 있는가?
- [ ] `docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md`를 읽어서 요약하는가?

**TODO (구현되지 않은 경우):**
```bash
# .claude/scripts/init-session.sh 생성
cat > .claude/scripts/init-session.sh << 'EOF'
#!/bin/bash

BRANCH=$(git branch --show-current)
JIRA_TASK=$(echo "$BRANCH" | grep -oP 'FF-\d+')

if [ -z "$JIRA_TASK" ]; then
  echo "⚠️ 경고: 브랜치명에 지라 태스크가 없습니다 (예: feature/FF-123-xxx)"
fi

# 아키텍처 규칙 요약 생성
cat > /tmp/claude-session-context.md <<CONTEXT
# 현재 작업 정보
- 브랜치: $BRANCH
- 지라 태스크: $JIRA_TASK

# 핵심 규칙 (상세: docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md)
1. Domain: Spring/JPA/Lombok 사용 금지
2. Application: @Transactional만 허용, Adapter 직접 참조 금지
3. Adapter: 각 계층별 분리 (In/Out)
4. 금지어: "일단", "나중에", "TODO", "임시로"

# 품질 게이트
- 커버리지: Domain 90%, Application 80%, Adapter 70%
- ArchUnit 테스트 필수 통과
- Javadoc + @author 태그 필수
CONTEXT

echo "✅ 세션 초기화 완료"
exit 0
EOF

chmod +x .claude/scripts/init-session.sh
```

---

#### 1.2.2 UserPromptSubmit Hook (`validate-prompt.sh`)

**기대 기능:**
- 사용자 입력에서 금지어 감지
- 금지어 발견 시 차단 (decision: blocked)

**확인 사항:**
- [ ] `.claude/scripts/validate-prompt.sh` 파일이 존재하는가?
- [ ] 금지어 목록이 정의되어 있는가?
- [ ] JSON 형식으로 응답하는가?

**TODO (구현되지 않은 경우):**
```bash
cat > .claude/scripts/validate-prompt.sh << 'EOF'
#!/bin/bash

INPUT=$(cat)
USER_PROMPT=$(echo "$INPUT" | jq -r '.text')

# 금지어 목록
FORBIDDEN_PHRASES=(
  "일단 주석"
  "나중에 정리"
  "임시로"
  "TODO로"
  "skip.*test"
)

for PHRASE in "${FORBIDDEN_PHRASES[@]}"; do
  if echo "$USER_PROMPT" | grep -qiE "$PHRASE"; then
    echo '{"decision": "blocked", "message": "🚫 금지어 감지: '"$PHRASE"'. AC 변경이 필요하면 지라 태스크부터 수정하세요."}'
    exit 0
  fi
done

echo '{"decision": "allowed"}'
exit 0
EOF

chmod +x .claude/scripts/validate-prompt.sh
```

---

#### 1.2.3 PreToolUse Hook (`validate-architecture.sh`)

**기대 기능:**
- 파일 경로에서 모듈 파악 (domain/application/adapter)
- 작성하려는 코드에서 금지 패턴 감지
- 레이어별 규칙 적용

**확인 사항:**
- [ ] `.claude/scripts/validate-architecture.sh` 파일이 존재하는가?
- [ ] Domain 레이어 검증 로직 (Spring/JPA/Lombok 금지)
- [ ] Application 레이어 검증 로직 (Adapter 직접 참조 금지)
- [ ] Persistence Adapter 검증 로직 (@Transactional 금지)

**TODO (구현되지 않은 경우):**
```bash
cat > .claude/scripts/validate-architecture.sh << 'EOF'
#!/bin/bash

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.input.file_path // .input.path // ""')
CONTENT=$(echo "$INPUT" | jq -r '.input.content // .input.new_str // ""')

# 파일 경로가 없으면 통과
if [ -z "$FILE_PATH" ]; then
  echo '{"decision": "allowed"}'
  exit 0
fi

# Domain 레이어 체크
if [[ "$FILE_PATH" =~ domain/ ]]; then
  if echo "$CONTENT" | grep -qE "(import.*springframework|import.*jakarta\.persistence|import.*lombok|@Data|@Builder|@Getter|@Setter)"; then
    echo '{"decision": "blocked", "message": "⛔ DOMAIN VIOLATION: Spring/JPA/Lombok 사용 금지"}'
    exit 0
  fi
fi

# Application 레이어 체크
if [[ "$FILE_PATH" =~ application/ ]]; then
  if echo "$CONTENT" | grep -qE "import.*adapter\."; then
    echo '{"decision": "blocked", "message": "⛔ ARCHITECTURE VIOLATION: Adapter 직접 참조 금지. Port를 사용하세요."}'
    exit 0
  fi
fi

# Persistence Adapter에서 @Transactional 체크
if [[ "$FILE_PATH" =~ adapter.*persistence ]]; then
  if echo "$CONTENT" | grep -q "@Transactional"; then
    echo '{"decision": "blocked", "message": "⛔ PERSISTENCE VIOLATION: 트랜잭션은 Application 레이어에서만 관리합니다."}'
    exit 0
  fi
fi

echo '{"decision": "allowed"}'
exit 0
EOF

chmod +x .claude/scripts/validate-architecture.sh
```

---

#### 1.2.4 PreCompact Hook (`preserve-rules.sh`)

**기대 기능:**
- 컨텍스트 압축 전 핵심 규칙을 텍스트로 출력
- AI가 압축 후에도 규칙을 기억하도록 보장

**확인 사항:**
- [ ] `.claude/scripts/preserve-rules.sh` 파일이 존재하는가?
- [ ] 핵심 규칙이 명확히 정의되어 있는가?

**TODO (구현되지 않은 경우):**
```bash
cat > .claude/scripts/preserve-rules.sh << 'EOF'
#!/bin/bash

cat <<'RULES'
🔒 CRITICAL RULES (절대 잊지 말 것)
1. Domain: 순수 Java만, 프레임워크 의존 금지
2. Application: @Transactional만 허용
3. Adapter: Port 통해서만 Application과 통신
4. Lombok 전체 금지
5. 임시 구현/주석 금지

❌ 금지 문구: "일단", "나중에", "TODO", "임시로"

✅ 허용 Import (Domain):
- java.util.*
- java.time.*
- org.apache.commons.lang3.*

❌ 금지 Import (Domain):
- org.springframework.*
- jakarta.persistence.*
- lombok.*
RULES

exit 0
EOF

chmod +x .claude/scripts/preserve-rules.sh
```

---

#### 1.2.5 문서 기반 자동 주입 Hook (`inject-rules.sh`)

**기대 기능:**
- `docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md` 읽기
- 현재 작업 중인 모듈 파악
- 해당 모듈의 규칙만 필터링해서 프롬프트에 추가

**확인 사항:**
- [ ] `.claude/scripts/inject-rules.sh` 파일이 존재하는가?
- [ ] `docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md` 파일이 모듈별로 섹션 분리되어 있는가?
- [ ] 현재 모듈을 자동으로 감지하는 로직이 있는가?

**TODO (구현되지 않은 경우):**
```bash
cat > .claude/scripts/inject-rules.sh << 'EOF'
#!/bin/bash

INPUT=$(cat)
USER_PROMPT=$(echo "$INPUT" | jq -r '.text')

# 현재 작업 디렉토리에서 모듈 파악
CURRENT_DIR=$(pwd)
CURRENT_MODULE="unknown"

if [[ "$CURRENT_DIR" =~ domain ]]; then
  CURRENT_MODULE="domain"
elif [[ "$CURRENT_DIR" =~ application ]]; then
  CURRENT_MODULE="application"
elif [[ "$CURRENT_DIR" =~ adapter-in ]]; then
  CURRENT_MODULE="adapter-in"
elif [[ "$CURRENT_DIR" =~ adapter-out ]]; then
  CURRENT_MODULE="adapter-out"
fi

# 프로젝트 루트 찾기
PROJECT_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || pwd)
STANDARDS_FILE="$PROJECT_ROOT/docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md"

if [ ! -f "$STANDARDS_FILE" ]; then
  # 표준 문서가 없으면 원본 프롬프트만 반환
  echo "$INPUT"
  exit 0
fi

# 모듈별 규칙 추출 (간단한 버전)
MODULE_RULES=$(sed -n "/## $CURRENT_MODULE/,/## /p" "$STANDARDS_FILE" | head -n -1)

# 프롬프트에 규칙 추가
ENHANCED=$(echo "$INPUT" | jq --arg rules "$MODULE_RULES" '.text = .text + "\n\n---\n📋 현재 모듈 규칙 ('"$CURRENT_MODULE"'):\n" + $rules')

echo "$ENHANCED"
exit 0
EOF

chmod +x .claude/scripts/inject-rules.sh
```

---

### 1.3 Hook 설정 파일 (`settings.json`)

**기대 내용:**
```json
{
  "hooks": {
    "SessionStart": {
      "command": "./.claude/scripts/init-session.sh",
      "timeout": 3000
    },
    "UserPromptSubmit": {
      "command": "./.claude/scripts/validate-prompt.sh",
      "timeout": 1000
    },
    "PreToolUse": {
      "command": "./.claude/scripts/validate-architecture.sh",
      "matchers": {
        "tool": "Write|Edit"
      },
      "timeout": 2000
    },
    "PreCompact": {
      "command": "./.claude/scripts/preserve-rules.sh",
      "timeout": 1000
    }
  }
}
```

**확인 사항:**
- [ ] `.claude/settings.json` 또는 `.claude/settings.json.example` 존재?
- [ ] 위 4가지 Hook이 모두 설정되어 있는가?
- [ ] timeout 값이 적절한가?
- [ ] matchers가 올바르게 설정되어 있는가?

**TODO (존재하지 않을 경우):**
```bash
cat > .claude/settings.json.example << 'EOF'
{
  "hooks": {
    "SessionStart": {
      "command": "./.claude/scripts/init-session.sh",
      "timeout": 3000
    },
    "UserPromptSubmit": {
      "command": "./.claude/scripts/validate-prompt.sh",
      "timeout": 1000
    },
    "PreToolUse": {
      "command": "./.claude/scripts/validate-architecture.sh",
      "matchers": {
        "tool": "Write|Edit"
      },
      "timeout": 2000
    },
    "PreCompact": {
      "command": "./.claude/scripts/preserve-rules.sh",
      "timeout": 1000
    }
  }
}
EOF
```

---

## 🔍 SECTION 2: 문서 구조 확인

### 2.1 `docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md` 구조

**블로그에서 가정한 것:**
- 모듈별 섹션으로 분리 (`## domain`, `## application`, `## adapter`)
- `inject-rules.sh`가 이 구조를 파싱할 수 있어야 함

**확인 사항:**
- [ ] 파일이 존재하는가?
- [ ] 모듈별 섹션이 명확히 구분되어 있는가?
- [ ] 각 섹션의 헤더가 일관된가? (예: `## Domain Layer Rules`)

**TODO (구조가 다를 경우):**
- `inject-rules.sh`의 파싱 로직을 실제 문서 구조에 맞게 수정
- 또는 문서를 Hook이 파싱하기 쉬운 구조로 리팩토링

---

### 2.2 `docs/DYNAMIC_HOOKS_GUIDE.md` 내용

**블로그에서 언급했지만 내용 미확인:**
- README에 링크만 있고 실제 내용을 보지 못함

**확인 사항:**
- [ ] 파일이 존재하는가?
- [ ] Hook 작성 예시가 포함되어 있는가?
- [ ] 보안 가이드라인이 포함되어 있는가? (USE AT YOUR OWN RISK)
- [ ] 이 문서가 블로그에서 제안한 Hook 스크립트들을 포함하는가?

**TODO (내용 부족할 경우):**
- 블로그에서 작성한 Hook 예시를 이 문서에 추가
- 각 Hook의 입력/출력 스키마 문서화
- 트러블슈팅 섹션 추가

---

## 🔍 SECTION 3: 예외 관리 시스템

### 3.1 `.claude/exceptions.json` (블로그에서 제안)

**블로그에서 제안한 구조:**
```json
{
  "allowed_violations": [
    {
      "rule": "lombok_금지",
      "path": "adapter-out-persistence-jpa/src/main/java/.../entity/*",
      "reason": "JPA Entity는 @Entity/@Id 필요"
    }
  ]
}
```

**확인 사항:**
- [ ] 이 파일이 존재하는가?
- [ ] Hook 스크립트가 이 파일을 읽는가?
- [ ] Git Hook Validator들이 이 파일을 참조하는가?

**현실:**
- ❌ 이건 블로그에서 **제안만** 한 것
- 실제 템플릿에는 **구현 안 됨**

**결정사항 (2025-10-05):**
- ✅ **Option 1 선택: 미구현, 향후 개선 후보로 명시**
- 📍 현재는 validator 스크립트(`hooks/validators/*.sh`)를 직접 수정해야 함
- 💡 향후 개선 시 아래 Option 2의 구조를 참고할 것
- 📝 이유: 핵심 기능 완성도 우선, 예외 관리는 실제 필요 발생 시 추가

**~~Option 2: 구현함~~ (미채택)**
```bash
# 1. 예외 파일 템플릿 생성
cat > .claude/exceptions.json.example << 'EOF'
{
  "allowed_violations": [
    {
      "rule": "lombok_forbidden",
      "path": "adapter-out-persistence-jpa/**/entity/*.java",
      "reason": "JPA Entity requires @Entity, @Id annotations",
      "approved_by": "team@company.com",
      "expires": "2025-12-31"
    }
  ]
}
EOF

# 2. Hook 스크립트에 예외 처리 로직 추가
# (각 validator에 JSON 파싱 및 예외 체크 로직 추가 필요)
```

---

## 🔍 SECTION 4: Git Hooks 세부 검증

### 4.1 `hooks/validators/` 각 스크립트 기능 확인

**블로그에서 언급한 기능들이 실제로 구현되어 있는지:**

#### 4.1.1 `domain-validator.sh`
- [ ] Spring import 금지 (`org.springframework.*`)
- [ ] JPA import 금지 (`jakarta.persistence.*`)
- [ ] Lombok 금지 (`lombok.*`, `@Data`, `@Builder`, etc.)
- [ ] 에러 메시지가 명확한가?

#### 4.1.2 `application-validator.sh`
- [ ] Adapter 직접 참조 금지 (`import.*adapter\.`)
- [ ] @Transactional 사용 허용 (이건 허용이므로 체크 안 함)
- [ ] Port 사용 강제하는 메시지가 있는가?

#### 4.1.3 `adapter-in-validator.sh` / `adapter-out-validator.sh`
- [ ] Persistence Adapter에서 @Transactional 체크
- [ ] 각 Adapter별 특정 규칙이 있는가?

#### 4.1.4 `common-validator.sh`
- [ ] Javadoc 체크 (Public 메서드)
- [ ] @author 태그 체크
- [ ] TODO/FIXME 주석 금지

#### 4.1.5 `dead-code-detector.sh`
- [ ] Utils/Helper 클래스 미사용 감지
- [ ] 실제로 작동하는가?

**TODO (기능 누락 시):**
- 각 validator를 블로그에서 제안한 로직과 비교
- 누락된 체크 항목 추가

---

## 🔍 SECTION 5: ArchUnit 테스트 세부 검증

### 5.1 `HexagonalArchitectureTest.java` 규칙 확인

**블로그에서 언급한 규칙들:**

- [ ] `domain_레이어는_독립적이어야_함`
- [ ] `application_레이어는_adapter를_직접_참조하면_안됨`
- [ ] `transactional은_application에서만_사용`
- [ ] `lombok_사용_금지`
- [ ] `순환_참조_금지`

**확인 방법:**
```bash
# 테스트 파일 찾기
find . -name "*HexagonalArchitectureTest.java" -o -name "*ArchitectureTest.java"

# 테스트 실행
./gradlew :bootstrap:test --tests "*ArchitectureTest"
```

**TODO (규칙 누락 시):**
- 블로그에서 제안한 ArchUnit 규칙을 테스트 파일에 추가

---

## 🔍 SECTION 6: Gradle 설정 검증

### 6.1 커버리지 기준이 실제로 강제되는가?

**블로그에서 언급:**
- Domain: 90%
- Application: 80%
- Adapter: 70%

**확인 사항:**
- [ ] `build.gradle.kts`에 `jacocoTestCoverageVerification` 태스크 존재?
- [ ] 모듈별로 다른 커버리지 기준이 설정되어 있는가?
- [ ] `tasks.check`가 이 태스크에 의존하는가?

**확인 방법:**
```bash
# 각 모듈의 build.gradle.kts 확인
grep -A 20 "jacocoTestCoverageVerification" */build.gradle.kts

# 빌드 시 실제로 커버리지 체크가 실행되는지 확인
./gradlew clean build
```

---

### 6.2 Checkstyle 규칙

**블로그에서 언급:**
- Javadoc 강제
- @author 태그 강제

**확인 사항:**
- [ ] `config/checkstyle/checkstyle.xml`에 JavadocMethod 규칙 존재?
- [ ] @author 태그 체크 규칙 존재?

**확인 방법:**
```bash
grep -i "javadoc" config/checkstyle/checkstyle.xml
grep -i "author" config/checkstyle/checkstyle.xml
```

---

## 📊 검증 체크리스트 요약

### 우선순위 HIGH (블로그 주장의 핵심)

- [ ] **H1:** Git Pre-commit Hook이 실제로 모듈별 검증을 수행하는가?
- [ ] **H2:** ArchUnit 테스트가 5가지 핵심 규칙을 포함하는가?
- [ ] **H3:** Gradle 커버리지 검증이 모듈별로 다른 기준을 적용하는가?
- [ ] **H4:** `docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md`가 존재하고 87개 규칙을 포함하는가?

### 우선순위 MEDIUM (Claude Code Hooks)

- [ ] **M1:** `.claude/` 디렉토리가 존재하는가?
- [ ] **M2:** Hook 스크립트들이 구현되어 있는가? (4개)
- [ ] **M3:** `settings.json` 설정 파일이 존재하는가?
- [ ] **M4:** `docs/DYNAMIC_HOOKS_GUIDE.md`에 실용적인 예시가 있는가?

### 우선순위 LOW (향후 개선)

- [ ] **L1:** 예외 관리 시스템 (`exceptions.json`)
- [ ] **L2:** CI/CD 파이프라인 템플릿

---

## 🚀 실행 계획

### Step 1: 클로드 코드에 이 문서 전달
```bash
# 이 파일을 claude-spring-standards 프로젝트 루트에 복사
cp factcheck-todo.md /path/to/claude-spring-standards/

# 클로드 코드 시작
cd /path/to/claude-spring-standards
claude-code
```

### Step 2: 클로드 코드에 요청할 프롬프트
```
이 factcheck-todo.md 파일을 읽고, 각 섹션의 체크리스트를 검증해줘.

우선순위 순서:
1. HIGH 항목 먼저 검증
2. MEDIUM 항목 검증
3. LOW 항목은 TODO로 남기기

검증 결과를 다음 형식으로 정리해줘:

## 검증 결과

### ✅ 구현 완료
- [항목 ID] 설명

### ⚠️ 부분 구현
- [항목 ID] 설명 (누락 내용)

### ❌ 미구현
- [항목 ID] 설명

### 💡 구현 TODO 리스트
1. [우선순위] 작업 내용
2. ...
```

### Step 3: TODO 리스트 기반 구현
- 클로드 코드가 제공한 TODO 리스트를 하나씩 구현
- 각 구현 후 테스트
- 블로그 내용과 실제 코드 동기화

---

## 📝 메모

**블로그 수정이 필요한 경우:**
- ❌ 미구현 항목은 "향후 개선 아이디어" 섹션으로 이동
- ⚠️ 부분 구현 항목은 "현재 가이드만 제공" 형태로 표현 수정
- ✅ 구현 완료 항목만 "즉시 사용 가능"으로 표시

**레포 업데이트가 필요한 경우:**
- README.md에 정확한 구현 상태 명시
- 누락된 Hook 스크립트 추가
- 예시 코드를 실제 작동하는 코드로 교체