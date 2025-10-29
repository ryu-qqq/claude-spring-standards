# 바운디드 컨텍스트 자동화 개발 워크플로우

## 📋 목차
1. [시스템 개요](#시스템-개요)
2. [전체 아키텍처](#전체-아키텍처)
3. [워크플로우 단계](#워크플로우-단계)
4. [Layer별 병렬 개발 전략](#layer별-병렬-개발-전략)
5. [Claude Squad 병렬 실행](#claude-squad-병렬-실행)
6. [자동 검증 및 테스트](#자동-검증-및-테스트)
7. [Jira 통합 전략](#jira-통합-전략)
8. [실행 예시](#실행-예시)

---

## 시스템 개요

### 목표
바운디드 컨텍스트 단위로 **문서 → Jira Epic/Task → 레이어별 병렬 개발 → 자동 검증 → 테스트** 전체 과정을 자동화

### 핵심 원칙
1. **문서 주도 개발**: PRD/기술 문서를 먼저 작성하고, 이를 기반으로 Jira Epic/Task 생성
2. **레이어 독립성**: Domain → Application → Persistence → REST API 순서로, 각 레이어는 독립적으로 병렬 개발 가능
3. **컨벤션 자동 검증**: 모든 코드는 90개 규칙 기반 Cache 시스템으로 실시간 검증
4. **Claude Squad**: 여러 Claude Code 인스턴스가 병렬로 작업 (각 Task당 1개 인스턴스)
5. **캐스케이드 워크플로우**: 상위 레이어 완료 → 하위 레이어 자동 시작

---

## 전체 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                    Phase 1: 문서 생성                          │
│  사용자 → Claude Code → PRD/Tech Spec 생성 (markdown)         │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│              Phase 2: Jira Epic/Task 자동 생성                │
│  PRD 분석 → Epic(바운디드 컨텍스트) + 4개 Layer Task 생성       │
│  - Epic: "User 바운디드 컨텍스트 개발"                          │
│    ├─ Task 1: Domain Layer (USER-101)                       │
│    ├─ Task 2: Application Layer (USER-102, depends: 101)    │
│    ├─ Task 3: Persistence Layer (USER-103, depends: 101)    │
│    └─ Task 4: REST API Layer (USER-104, depends: 102, 103)  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│            Phase 3: Claude Squad 병렬 개발                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Claude #1   │  │ Claude #2   │  │ Claude #3   │         │
│  │ USER-101    │  │ (대기 중)    │  │ (대기 중)    │         │
│  │ Domain      │  │             │  │             │         │
│  └──────┬──────┘  └─────────────┘  └─────────────┘         │
│         │ 완료 & 검증 통과                                     │
│         ↓                                                    │
│  ┌─────────────┐  ┌─────────────┐                          │
│  │ Claude #2   │  │ Claude #3   │  (병렬 실행)               │
│  │ USER-102    │  │ USER-103    │                          │
│  │ Application │  │ Persistence │                          │
│  └──────┬──────┘  └──────┬──────┘                          │
│         │ 완료           │ 완료                              │
│         └────────┬────────┘                                 │
│                  ↓                                           │
│         ┌─────────────┐                                     │
│         │ Claude #4   │                                     │
│         │ USER-104    │                                     │
│         │ REST API    │                                     │
│         └─────────────┘                                     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│               Phase 4: 자동 검증 & 통합 테스트                  │
│  1. ArchUnit Tests (각 레이어)                                │
│  2. Unit Tests (도메인 로직)                                  │
│  3. Integration Tests (전체 플로우)                           │
│  4. 컨벤션 검증 (validation-helper.py)                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                  Phase 5: PR 생성 & 리뷰                       │
│  - 각 Task → 개별 PR                                          │
│  - 자동 검증 결과 첨부                                          │
│  - Jira Task 자동 전환 (In Progress → Review)                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 워크플로우 단계

### Phase 1: 문서 생성 (Manual or AI-Assisted)

#### 입력
- 사용자 요구사항: "User 관리 바운디드 컨텍스트 개발"
- 기능 목록: 회원가입, 로그인, 프로필 조회/수정, 탈퇴

#### 출력
**1. PRD 문서** (`prd/user-management-context.md`)
```markdown
# User Management 바운디드 컨텍스트 PRD

## 목표
사용자 인증/인가 및 프로필 관리 기능 제공

## 기능 요구사항
1. 회원가입 (이메일, 비밀번호, 닉네임)
2. 로그인 (JWT 토큰 발급)
3. 프로필 조회/수정
4. 회원 탈퇴

## 비기능 요구사항
- 헥사고날 아키텍처 준수
- Law of Demeter 엄격 적용
- 트랜잭션 경계 관리
- Long FK 전략 (JPA 관계 금지)

## Acceptance Criteria
- [ ] 이메일 중복 검증
- [ ] 비밀번호 암호화 (BCrypt)
- [ ] JWT 토큰 만료 시간 30분
- [ ] 탈퇴 시 Soft Delete
```

**2. 기술 문서** (`docs/technical/user-management-design.md`)
```markdown
# User Management 기술 설계

## Domain Layer
- Aggregate: User (UserId, UserEmail, UserProfile, UserStatus, UserAudit)
- Value Objects: UserId, UserEmail, UserProfile, UserPassword
- Domain Events: UserCreated, UserUpdated, UserDeleted
- Exceptions: UserNotFoundException, UserAlreadyExistsException

## Application Layer
- Commands: CreateUserCommand, UpdateUserProfileCommand, DeleteUserCommand
- Queries: GetUserQuery, SearchUsersQuery
- UseCases: CreateUserUseCase, GetUserQueryService
- Assemblers: UserAssembler

## Persistence Layer
- Entity: UserJpaEntity (Long userId, String email, String encryptedPassword, ...)
- Repository: UserRepository (JpaRepository)
- Adapter: UserPersistenceAdapter (implements UserCommandOutPort, UserQueryOutPort)

## REST API Layer
- Controller: UserController
- Request DTOs: CreateUserRequest, UpdateUserProfileRequest
- Response DTOs: UserDetailResponse, UserPageResponse
```

**Claude Code 명령**:
```bash
# PRD 생성
/sc:brainstorm "User 관리 바운디드 컨텍스트 PRD 작성"

# 기술 문서 생성
/sc:design user-management --type architecture --format spec
```

---

### Phase 2: Jira Epic/Task 자동 생성

#### 자동화 스크립트 설계

**1. PRD → Jira Epic/Task 변환 스크립트** (`scripts/prd-to-jira.py`)

```python
#!/usr/bin/env python3
"""
PRD/기술 문서를 분석하여 Jira Epic 및 하위 Task 자동 생성

Usage:
    python3 scripts/prd-to-jira.py prd/user-management-context.md
"""

import sys
import json
from pathlib import Path

def parse_prd(prd_path: str) -> dict:
    """PRD 마크다운 파일 파싱"""
    content = Path(prd_path).read_text()

    # 제목, 기능 목록, Acceptance Criteria 추출
    # (정규식 또는 Markdown 파서 사용)

    return {
        "epic_title": "User Management 바운디드 컨텍스트 개발",
        "epic_description": content,
        "features": [
            "회원가입", "로그인", "프로필 조회/수정", "탈퇴"
        ],
        "acceptance_criteria": [
            "이메일 중복 검증",
            "비밀번호 암호화 (BCrypt)",
            "JWT 토큰 만료 시간 30분",
            "탈퇴 시 Soft Delete"
        ]
    }

def generate_jira_tasks(prd_data: dict) -> list:
    """레이어별 Jira Task 생성"""

    base_description = f"""
## 바운디드 컨텍스트: {prd_data['epic_title']}

## 기능 요구사항
{chr(10).join(f'- {f}' for f in prd_data['features'])}

## Acceptance Criteria
{chr(10).join(f'- [ ] {ac}' for ac in prd_data['acceptance_criteria'])}

## 컨벤션 체크리스트
- [ ] Lombok 금지 (Pure Java)
- [ ] Law of Demeter 준수
- [ ] Javadoc 필수 (모든 public 클래스/메서드)
- [ ] ArchUnit 테스트 통과
"""

    tasks = [
        {
            "summary": f"[Domain Layer] {prd_data['epic_title']} - Aggregate 및 도메인 로직 구현",
            "description": f"{base_description}\n\n## Layer 특화 요구사항\n- Aggregate Root 설계\n- Value Objects 구현\n- Domain Events 정의\n- Domain Exception 구현",
            "task_type": "domain",
            "priority": "Highest",
            "depends_on": None,
            "conventions": [
                "docs/coding_convention/02-domain-layer/",
                "docs/coding_convention/06-java21-patterns/",
                "docs/coding_convention/08-error-handling/"
            ]
        },
        {
            "summary": f"[Application Layer] {prd_data['epic_title']} - UseCase 및 Application Service 구현",
            "description": f"{base_description}\n\n## Layer 특화 요구사항\n- Command/Query DTO 정의\n- UseCase 인터페이스 구현\n- Application Service 구현\n- Assembler 패턴 적용",
            "task_type": "application",
            "priority": "High",
            "depends_on": "domain",
            "conventions": [
                "docs/coding_convention/03-application-layer/",
                "docs/coding_convention/06-java21-patterns/"
            ]
        },
        {
            "summary": f"[Persistence Layer] {prd_data['epic_title']} - JPA Entity 및 Repository 구현",
            "description": f"{base_description}\n\n## Layer 특화 요구사항\n- JPA Entity 설계 (Long FK 전략)\n- Repository 인터페이스 구현\n- Persistence Adapter 구현\n- QueryDSL 최적화 (필요 시)",
            "task_type": "persistence",
            "priority": "High",
            "depends_on": "domain",
            "conventions": [
                "docs/coding_convention/04-persistence-layer/",
                "docs/coding_convention/06-java21-patterns/"
            ]
        },
        {
            "summary": f"[REST API Layer] {prd_data['epic_title']} - Controller 및 API 구현",
            "description": f"{base_description}\n\n## Layer 특화 요구사항\n- REST Controller 구현\n- Request/Response DTO 정의\n- Error Mapping 구현\n- API 문서화 (Swagger/OpenAPI)",
            "task_type": "rest-api",
            "priority": "Medium",
            "depends_on": "application,persistence",
            "conventions": [
                "docs/coding_convention/01-adapter-rest-api-layer/",
                "docs/coding_convention/06-java21-patterns/",
                "docs/coding_convention/08-error-handling/"
            ]
        }
    ]

    return tasks

def create_jira_epic_and_tasks(prd_data: dict, tasks: list) -> dict:
    """
    Jira API를 호출하여 Epic 및 Task 생성

    Note: 실제로는 mcp__atlassian MCP 서버를 Claude Code에서 호출
    """

    # 1. Epic 생성
    epic_payload = {
        "summary": prd_data["epic_title"],
        "description": prd_data["epic_description"],
        "issuetype": "Epic",
        "customfield_epic_name": prd_data["epic_title"]  # Epic Name 커스텀 필드
    }

    # epic_key = create_jira_issue(epic_payload)  # 예: USER-EPIC-1

    # 2. Task 생성 (Epic 하위)
    task_keys = {}
    for task in tasks:
        task_payload = {
            "summary": task["summary"],
            "description": task["description"],
            "issuetype": "Task",
            "priority": task["priority"],
            "parent": "epic_key",  # Epic 연결
            "labels": [task["task_type"], "bounded-context", "automation"],
            "customfield_conventions": task["conventions"]  # 커스텀 필드: 준수할 컨벤션 문서 목록
        }

        # task_key = create_jira_issue(task_payload)
        # task_keys[task["task_type"]] = task_key

    # 3. Task 간 의존성 설정 (Blocks/Depends on)
    # for task in tasks:
    #     if task["depends_on"]:
    #         # set_jira_link(task_key, "Depends on", depends_task_key)

    return {
        "epic_key": "USER-EPIC-1",
        "tasks": task_keys
    }

if __name__ == "__main__":
    prd_path = sys.argv[1]
    prd_data = parse_prd(prd_path)
    tasks = generate_jira_tasks(prd_data)
    result = create_jira_epic_and_tasks(prd_data, tasks)

    print(json.dumps(result, indent=2, ensure_ascii=False))
```

**2. Claude Code Slash Command 통합** (`.claude/commands/bounded-context-init.md`)

```markdown
---
description: 바운디드 컨텍스트 Jira Epic/Task 자동 생성
tags: [project]
---

# Bounded Context Initialization

PRD 문서를 기반으로 Jira Epic 및 레이어별 Task를 자동 생성합니다.

## Usage
/bounded-context-init <prd-file-path>

## 실행 단계

1. **PRD 파일 읽기 및 분석**
   - PRD 마크다운 파일에서 바운디드 컨텍스트 이름, 기능 목록, Acceptance Criteria 추출

2. **Jira Cloud ID 획득**
   ```
   mcp__atlassian__getAccessibleAtlassianResources
   ```

3. **Jira Epic 생성**
   ```
   mcp__atlassian__createJiraIssue
   - 제목: "{바운디드 컨텍스트 이름} 개발"
   - 설명: PRD 전체 내용
   - Issue Type: Epic
   ```

4. **4개 Layer Task 생성 (Domain → Application → Persistence → REST API)**
   각 Task마다:
   ```
   mcp__atlassian__createJiraIssue
   - 제목: "[{Layer}] {바운디드 컨텍스트 이름}"
   - 설명: Layer 특화 요구사항 + PRD 기능 목록 + Acceptance Criteria
   - Parent: Epic Key
   - Priority: Domain(Highest) → Application/Persistence(High) → REST API(Medium)
   - Labels: [layer-name, bounded-context, automation]
   - 커스텀 필드:
     * conventions: Layer별 준수할 컨벤션 문서 경로 목록
     * depends_on: 의존하는 Task Key 목록
   ```

5. **Task 간 의존성 링크 설정**
   - Application Layer → Depends on → Domain Layer
   - Persistence Layer → Depends on → Domain Layer
   - REST API Layer → Depends on → Application Layer, Persistence Layer

6. **브랜치 전략 설정**
   - Epic 브랜치: `epic/{epic-key}-{context-name}` (예: `epic/USER-EPIC-1-user-management`)
   - Task 브랜치: `feature/{task-key}-{layer}` (예: `feature/USER-101-domain`)

7. **출력**
   ```markdown
   ## ✅ Jira Epic/Task 생성 완료

   **Epic**: [USER-EPIC-1] User Management 바운디드 컨텍스트 개발
   - URL: https://ryuqqq.atlassian.net/browse/USER-EPIC-1

   **Tasks**:
   1. [USER-101] [Domain Layer] User Management - Aggregate 및 도메인 로직 구현
      - Branch: feature/USER-101-domain
      - Priority: Highest
      - Conventions: 15개 규칙 (Domain Layer)

   2. [USER-102] [Application Layer] User Management - UseCase 및 Application Service 구현
      - Branch: feature/USER-102-application
      - Priority: High
      - Depends on: USER-101
      - Conventions: 18개 규칙 (Application Layer)

   3. [USER-103] [Persistence Layer] User Management - JPA Entity 및 Repository 구현
      - Branch: feature/USER-103-persistence
      - Priority: High
      - Depends on: USER-101
      - Conventions: 10개 규칙 (Persistence Layer)

   4. [USER-104] [REST API Layer] User Management - Controller 및 API 구현
      - Branch: feature/USER-104-rest-api
      - Priority: Medium
      - Depends on: USER-102, USER-103
      - Conventions: 18개 규칙 (REST API Layer)

   ## 다음 단계
   1. Claude Squad로 병렬 개발 시작: `scripts/claude-squad-start.sh USER-EPIC-1`
   2. 수동 개발 시작: `/jira-task USER-101`
   ```

## 에러 처리
- PRD 파일 없음: 사용자에게 파일 경로 확인 요청
- Jira API 오류: 재시도 또는 수동 생성 가이드 제공
- 커스텀 필드 없음: 기본 값으로 생성 후 경고 메시지
```

**실행 예시**:
```bash
# 1. PRD 작성 (Manual or AI)
/sc:brainstorm "User 관리 바운디드 컨텍스트 PRD 작성"

# 2. Jira Epic/Task 자동 생성
/bounded-context-init prd/user-management-context.md
```

---

### Phase 3: Claude Squad 병렬 개발

#### 병렬 실행 전략

**1. Task 의존성 그래프**
```
Domain Layer (USER-101)  [독립 실행 가능 - 최우선]
    ├─→ Application Layer (USER-102)  [Domain 완료 후 실행]
    └─→ Persistence Layer (USER-103)  [Domain 완료 후 실행, Application과 병렬]
            └─→ REST API Layer (USER-104)  [Application + Persistence 완료 후 실행]
```

**2. Claude Squad 오케스트레이션 스크립트** (`scripts/claude-squad-start.sh`)

```bash
#!/bin/bash
# Claude Squad - 병렬 개발 오케스트레이터
# Usage: ./scripts/claude-squad-start.sh <epic-key>

set -euo pipefail

EPIC_KEY="$1"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SQUAD_LOG_DIR="$PROJECT_ROOT/logs/claude-squad/$EPIC_KEY"
mkdir -p "$SQUAD_LOG_DIR"

# Jira에서 Epic의 모든 Task 조회
echo "📋 Epic $EPIC_KEY의 Task 목록 조회 중..."
TASKS=$(curl -s -X GET \
  -H "Authorization: Bearer $JIRA_API_TOKEN" \
  "https://ryuqqq.atlassian.net/rest/api/3/search?jql=parent=$EPIC_KEY" \
  | jq -r '.issues[] | "\(.key)|\(.fields.customfield_layer)|\(.fields.customfield_depends_on // "")"')

# Task 분류
DOMAIN_TASK=""
APP_TASK=""
PERSISTENCE_TASK=""
REST_TASK=""

while IFS='|' read -r task_key layer depends_on; do
  case "$layer" in
    domain) DOMAIN_TASK="$task_key" ;;
    application) APP_TASK="$task_key" ;;
    persistence) PERSISTENCE_TASK="$task_key" ;;
    rest-api) REST_TASK="$task_key" ;;
  esac
done <<< "$TASKS"

# Phase 1: Domain Layer (독립 실행)
echo "🚀 Phase 1: Domain Layer 개발 시작 ($DOMAIN_TASK)"
"$PROJECT_ROOT/scripts/claude-worker.sh" "$DOMAIN_TASK" > "$SQUAD_LOG_DIR/$DOMAIN_TASK.log" 2>&1 &
DOMAIN_PID=$!

# Domain 완료 대기
wait $DOMAIN_PID
DOMAIN_EXIT_CODE=$?

if [ $DOMAIN_EXIT_CODE -ne 0 ]; then
  echo "❌ Domain Layer 개발 실패 (Task: $DOMAIN_TASK)"
  exit 1
fi

echo "✅ Domain Layer 완료, 검증 통과"

# Phase 2: Application + Persistence (병렬 실행)
echo "🚀 Phase 2: Application + Persistence Layer 병렬 개발 시작"
"$PROJECT_ROOT/scripts/claude-worker.sh" "$APP_TASK" > "$SQUAD_LOG_DIR/$APP_TASK.log" 2>&1 &
APP_PID=$!

"$PROJECT_ROOT/scripts/claude-worker.sh" "$PERSISTENCE_TASK" > "$SQUAD_LOG_DIR/$PERSISTENCE_TASK.log" 2>&1 &
PERSISTENCE_PID=$!

# 두 Task 완료 대기
wait $APP_PID $PERSISTENCE_PID
APP_EXIT_CODE=$?
PERSISTENCE_EXIT_CODE=$?

if [ $APP_EXIT_CODE -ne 0 ] || [ $PERSISTENCE_EXIT_CODE -ne 0 ]; then
  echo "❌ Application 또는 Persistence Layer 개발 실패"
  exit 1
fi

echo "✅ Application + Persistence Layer 완료, 검증 통과"

# Phase 3: REST API Layer
echo "🚀 Phase 3: REST API Layer 개발 시작 ($REST_TASK)"
"$PROJECT_ROOT/scripts/claude-worker.sh" "$REST_TASK" > "$SQUAD_LOG_DIR/$REST_TASK.log" 2>&1 &
REST_PID=$!

wait $REST_PID
REST_EXIT_CODE=$?

if [ $REST_EXIT_CODE -ne 0 ]; then
  echo "❌ REST API Layer 개발 실패 (Task: $REST_TASK)"
  exit 1
fi

echo "✅ REST API Layer 완료, 검증 통과"

# Phase 4: 통합 테스트
echo "🧪 Phase 4: 통합 테스트 실행 중..."
"$PROJECT_ROOT/scripts/run-integration-tests.sh" "$EPIC_KEY"

echo "🎉 Epic $EPIC_KEY 개발 완료!"
echo "📊 상세 로그: $SQUAD_LOG_DIR/"
```

**3. Claude Worker 스크립트** (`scripts/claude-worker.sh`)

```bash
#!/bin/bash
# Claude Worker - 단일 Task 개발 실행
# Usage: ./scripts/claude-worker.sh <task-key>

set -euo pipefail

TASK_KEY="$1"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🔍 Task $TASK_KEY 정보 조회 중..."

# Jira Task 상세 조회
TASK_JSON=$(curl -s -X GET \
  -H "Authorization: Bearer $JIRA_API_TOKEN" \
  "https://ryuqqq.atlassian.net/rest/api/3/issue/$TASK_KEY")

LAYER=$(echo "$TASK_JSON" | jq -r '.fields.customfield_layer')
BRANCH=$(echo "$TASK_JSON" | jq -r '.fields.customfield_branch')
CONVENTIONS=$(echo "$TASK_JSON" | jq -r '.fields.customfield_conventions[]')

echo "📌 Layer: $LAYER"
echo "🌿 Branch: $BRANCH"
echo "📚 Conventions: $(echo "$CONVENTIONS" | wc -l)개 문서"

# Git 브랜치 체크아웃
cd "$PROJECT_ROOT"
git fetch origin
if git rev-parse --verify --quiet "origin/$BRANCH" > /dev/null; then
  git checkout "$BRANCH"
  git pull origin "$BRANCH"
else
  git checkout -b "$BRANCH"
fi

# Jira Task 상태 변경: To Do → In Progress
curl -s -X POST \
  -H "Authorization: Bearer $JIRA_API_TOKEN" \
  -H "Content-Type: application/json" \
  "https://ryuqqq.atlassian.net/rest/api/3/issue/$TASK_KEY/transitions" \
  -d '{"transition": {"id": "21"}}' > /dev/null  # 21 = "In Progress" transition ID

# Claude Code로 개발 실행
echo "🤖 Claude Code 실행 중..."

# /jira-task 명령으로 TodoList 생성 + 자동 개발
claude code run << EOF
/jira-task $TASK_KEY

# Layer별 자동 개발 명령
$(case "$LAYER" in
  domain)
    echo "/domain 바운디드 컨텍스트 $TASK_KEY 개발 시작"
    ;;
  application)
    echo "/application 바운디드 컨텍스트 $TASK_KEY 개발 시작"
    ;;
  persistence)
    echo "/persistence 바운디드 컨텍스트 $TASK_KEY 개발 시작"
    ;;
  rest-api)
    echo "/rest 바운디드 컨텍스트 $TASK_KEY 개발 시작"
    ;;
esac)

# 자동 테스트 실행
/test $LAYER

# 컨벤션 검증
python3 .claude/hooks/scripts/validation-helper.py $LAYER

# 변경사항 커밋
git add .
git commit -m "feat($LAYER): $TASK_KEY 구현 완료

- $(echo "$TASK_JSON" | jq -r '.fields.summary')

🤖 Generated with Claude Code
"

# PR 생성
gh pr create --title "$TASK_KEY: $LAYER 구현" \
  --body "$(echo "$TASK_JSON" | jq -r '.fields.description')" \
  --label "$LAYER,auto-generated"

# Jira Task 상태 변경: In Progress → Review
curl -s -X POST \
  -H "Authorization: Bearer $JIRA_API_TOKEN" \
  -H "Content-Type: application/json" \
  "https://ryuqqq.atlassian.net/rest/api/3/issue/$TASK_KEY/transitions" \
  -d '{"transition": {"id": "31"}}' > /dev/null  # 31 = "Review" transition ID

echo "✅ Task $TASK_KEY 개발 완료"
EOF

EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
  echo "❌ Task $TASK_KEY 개발 중 오류 발생"

  # Jira Task 상태 변경: In Progress → Blocked
  curl -s -X POST \
    -H "Authorization: Bearer $JIRA_API_TOKEN" \
    -H "Content-Type: application/json" \
    "https://ryuqqq.atlassian.net/rest/api/3/issue/$TASK_KEY/transitions" \
    -d '{"transition": {"id": "41"}}' > /dev/null  # 41 = "Blocked" transition ID

  exit $EXIT_CODE
fi

exit 0
```

---

### Phase 4: 자동 검증 및 테스트

#### 검증 레벨

**Level 1: 실시간 컨벤션 검증** (코드 생성 직후)
- `.claude/hooks/after-tool-use.sh` → `validation-helper.py`
- 90개 규칙 Cache 기반 O(1) 검증
- 위반 시 즉시 수정

**Level 2: Layer별 ArchUnit 테스트** (코드 완성 후)
```bash
# Domain Layer
./gradlew :domain:test --tests "DomainLayerRulesTest"

# Application Layer
./gradlew :application:test --tests "ApplicationLayerRulesTest"

# Persistence Layer
./gradlew :adapter-out:persistence-mysql:test --tests "PersistenceLayerRulesTest"

# REST API Layer
./gradlew :adapter-in:rest-api:test --tests "RestApiLayerRulesTest"
```

**Level 3: 통합 테스트** (전체 완성 후)
```bash
# 헥사고날 아키텍처 검증
./gradlew :bootstrap:bootstrap-web-api:test --tests "HexagonalArchitectureTest"

# E2E 시나리오 테스트
./gradlew :bootstrap:bootstrap-web-api:integrationTest
```

#### 검증 스크립트 (`scripts/validate-layer.sh`)

```bash
#!/bin/bash
# Layer별 검증 스크립트
# Usage: ./scripts/validate-layer.sh <layer> <task-key>

set -euo pipefail

LAYER="$1"
TASK_KEY="$2"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🔍 $LAYER Layer 검증 시작 (Task: $TASK_KEY)"

# 1. 컨벤션 검증
echo "📚 컨벤션 규칙 검증 중..."
python3 "$PROJECT_ROOT/.claude/hooks/scripts/validation-helper.py" "$LAYER"
CONVENTION_EXIT_CODE=$?

if [ $CONVENTION_EXIT_CODE -ne 0 ]; then
  echo "❌ 컨벤션 위반 발견"
  exit 1
fi

# 2. ArchUnit 테스트
echo "🏗️ 아키텍처 규칙 검증 중..."

case "$LAYER" in
  domain)
    ./gradlew :domain:test --tests "DomainLayerRulesTest"
    ;;
  application)
    ./gradlew :application:test --tests "ApplicationLayerRulesTest"
    ;;
  persistence)
    ./gradlew :adapter-out:persistence-mysql:test --tests "PersistenceLayerRulesTest"
    ;;
  rest-api)
    ./gradlew :adapter-in:rest-api:test --tests "RestApiLayerRulesTest"
    ;;
esac

ARCHUNIT_EXIT_CODE=$?

if [ $ARCHUNIT_EXIT_CODE -ne 0 ]; then
  echo "❌ ArchUnit 테스트 실패"
  exit 1
fi

# 3. 단위 테스트
echo "🧪 단위 테스트 실행 중..."

case "$LAYER" in
  domain)
    ./gradlew :domain:test
    ;;
  application)
    ./gradlew :application:test
    ;;
  persistence)
    ./gradlew :adapter-out:persistence-mysql:test
    ;;
  rest-api)
    ./gradlew :adapter-in:rest-api:test
    ;;
esac

UNIT_TEST_EXIT_CODE=$?

if [ $UNIT_TEST_EXIT_CODE -ne 0 ]; then
  echo "❌ 단위 테스트 실패"
  exit 1
fi

echo "✅ $LAYER Layer 검증 완료"
exit 0
```

---

## Layer별 병렬 개발 전략

### Domain Layer (독립 실행 - 최우선)

**브랜치**: `feature/{TASK-KEY}-domain`

**자동 생성 대상**:
1. Aggregate Root (예: `UserDomain.java`)
2. Value Objects (예: `UserId.java`, `UserEmail.java`, `UserProfile.java`)
3. Domain Events (예: `UserCreatedEvent.java`)
4. Domain Exceptions (예: `UserNotFoundException.java`)
5. ErrorCode Enum (예: `UserErrorCode.java`)
6. Domain Tests (예: `UserDomainTest.java`)

**Slash Command**: `/domain`

**예시**:
```bash
/jira-task USER-101
# 자동 TodoList 생성:
# 1. ✅ 브랜치 체크아웃 (feature/USER-101-domain)
# 2. 🔄 UserId Value Object 구현
# 3. 🔄 UserEmail Value Object 구현
# 4. 🔄 UserProfile Value Object 구현
# 5. 🔄 UserPassword Value Object 구현
# 6. 🔄 UserDomain Aggregate Root 구현
# 7. 🔄 UserCreatedEvent 구현
# 8. 🔄 UserNotFoundException 구현
# 9. 🔄 UserErrorCode Enum 구현
# 10. ⏳ Domain 단위 테스트 작성
# 11. ⏳ DomainLayerRulesTest 실행
# 12. ⏳ PR 생성

# 각 Todo 자동 실행
```

**Claude Code Slash Command 예시** (`.claude/commands/domain.md`):
```markdown
---
description: Domain Layer 자동 개발
tags: [project]
---

# Domain Layer 개발

## Triggers
- Jira Task의 `customfield_layer == "domain"`
- `/domain` 명령어

## 실행 단계

1. **Jira Task 분석** (Task Key로부터)
   - Aggregate 이름 추출 (예: User)
   - 필요한 Value Objects 식별
   - Domain Events 식별
   - Exception 목록 추출

2. **패키지 구조 생성**
   ```
   domain/src/main/java/com/ryuqq/domain/{aggregate}/
   ├── {Aggregate}Domain.java         (Aggregate Root)
   ├── {Aggregate}Id.java              (Value Object)
   ├── {Aggregate}Content.java         (Value Object)
   ├── {Aggregate}Status.java          (Value Object)
   ├── {Aggregate}Audit.java           (Value Object)
   ├── {Aggregate}ErrorCode.java       (Enum)
   └── exception/
       ├── {Aggregate}Exception.java   (Sealed abstract)
       ├── {Aggregate}NotFoundException.java
       └── {Aggregate}AlreadyExistsException.java
   ```

3. **코드 생성** (컨벤션 자동 주입)
   - Law of Demeter 엄격 적용 (Getter 체이닝 금지)
   - Lombok 금지 (Pure Java)
   - Java 21 Records 사용 (Value Objects)
   - Java 21 Sealed Classes 사용 (Exception 계층)
   - Javadoc 필수 (모든 public 클래스/메서드)

4. **테스트 생성**
   ```
   domain/src/test/java/com/ryuqq/domain/{aggregate}/
   ├── {Aggregate}DomainTest.java
   ├── {Aggregate}IdTest.java
   └── exception/{Aggregate}NotFoundExceptionTest.java
   ```

5. **검증**
   - 컨벤션 검증: `validation-helper.py domain`
   - ArchUnit 테스트: `./gradlew :domain:test --tests "DomainLayerRulesTest"`
   - 단위 테스트: `./gradlew :domain:test`

6. **완료**
   - Git commit + push
   - PR 생성
   - Jira Task → "Review" 상태 전환
```

---

### Application Layer (Domain 완료 후 실행)

**브랜치**: `feature/{TASK-KEY}-application`

**의존성**: Domain Layer 완료 필수

**자동 생성 대상**:
1. Command DTOs (예: `CreateUserCommand.java`)
2. Query DTOs (예: `GetUserQuery.java`)
3. Response DTOs (예: `UserDetailResponse.java`)
4. InboundPort (UseCase 인터페이스, 예: `CreateUserUseCase.java`)
5. OutboundPort (Repository 인터페이스, 예: `UserCommandOutPort.java`)
6. Application Service (예: `CreateUserService.java`)
7. Assembler (예: `UserAssembler.java`)
8. Application Tests

**Slash Command**: `/application`

---

### Persistence Layer (Domain 완료 후 실행, Application과 병렬)

**브랜치**: `feature/{TASK-KEY}-persistence`

**의존성**: Domain Layer 완료 필수 (Application과 독립)

**자동 생성 대상**:
1. JPA Entity (예: `UserJpaEntity.java`)
2. Repository 인터페이스 (예: `UserRepository.java`)
3. Entity Mapper (예: `UserEntityMapper.java`)
4. Persistence Adapter (예: `UserPersistenceAdapter.java`)
5. QueryDSL Repository (필요 시)
6. Persistence Tests

**Slash Command**: `/persistence`

**주요 규칙**:
- Long FK 전략 (JPA 관계 어노테이션 금지)
- `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany` 사용 금지
- `private Long userId;` 형태로 외래 키 관리

---

### REST API Layer (Application + Persistence 완료 후 실행)

**브랜치**: `feature/{TASK-KEY}-rest-api`

**의존성**: Application Layer + Persistence Layer 완료 필수

**자동 생성 대상**:
1. Controller (예: `UserController.java`)
2. Request DTOs (예: `CreateUserRequest.java`)
3. Response DTOs (예: `UserDetailApiResponse.java`)
4. API Mapper (예: `UserMapper.java`)
5. Error Mapper (예: `UserErrorMapper.java`)
6. REST API Tests

**Slash Command**: `/rest`

---

## Claude Squad 병렬 실행

### 병렬화 전략

```
Timeline:

T0: Domain Layer 시작 (Claude #1)
    ├─ Aggregate 설계
    ├─ Value Objects 구현
    ├─ Domain Events 구현
    └─ Domain Tests 작성

T+30min: Domain 완료 & 검증 통과
    ├─ Application Layer 시작 (Claude #2)
    │   ├─ UseCase 인터페이스 구현
    │   ├─ Application Service 구현
    │   ├─ Assembler 구현
    │   └─ Application Tests 작성
    │
    └─ Persistence Layer 시작 (Claude #3, 병렬)
        ├─ JPA Entity 구현
        ├─ Repository 구현
        ├─ Persistence Adapter 구현
        └─ Persistence Tests 작성

T+50min: Application + Persistence 완료
    └─ REST API Layer 시작 (Claude #4)
        ├─ Controller 구현
        ├─ API Mapper 구현
        ├─ Error Mapper 구현
        └─ REST API Tests 작성

T+70min: 전체 완료
    └─ 통합 테스트 실행
```

### 리소스 관리

**최대 동시 실행**: 3개 Claude Code 인스턴스
- Phase 1: 1개 (Domain)
- Phase 2: 2개 (Application + Persistence)
- Phase 3: 1개 (REST API)
- Phase 4: 1개 (Integration Tests)

**실행 환경**:
- 각 Claude Code 인스턴스는 독립된 워크스페이스 (별도 디렉토리)
- Git worktree 활용 (동시에 여러 브랜치 체크아웃)
```bash
git worktree add ../claude-spring-standards-worker1 feature/USER-101-domain
git worktree add ../claude-spring-standards-worker2 feature/USER-102-application
git worktree add ../claude-spring-standards-worker3 feature/USER-103-persistence
```

---

## 자동 검증 및 테스트

### 검증 파이프라인

```
코드 생성 (Write/Edit)
    ↓
after-tool-use.sh (실시간)
    ↓
validation-helper.py (90개 규칙 Cache 검증)
    ↓
[위반 시] 즉시 수정 → 재검증
    ↓
[통과 시] ArchUnit 테스트
    ↓
[통과 시] 단위 테스트
    ↓
[통과 시] Git commit + PR
    ↓
[실패 시] Jira Task → "Blocked"
```

### 테스트 전략

**1. 도메인 로직 테스트** (Domain Layer)
```java
@DisplayName("User Domain 테스트")
class UserDomainTest {
    @Test
    @DisplayName("유효한 이메일로 User 생성 성공")
    void createUserWithValidEmail() {
        // given
        String validEmail = "test@example.com";

        // when
        UserDomain user = UserDomain.create(validEmail);

        // then
        assertThat(user.getEmail()).isEqualTo(validEmail);
        assertThat(user.getStatus()).isEqualTo("ACTIVE");
    }
}
```

**2. Application Service 테스트** (Application Layer)
```java
@DisplayName("CreateUserService 테스트")
class CreateUserServiceTest {
    @Test
    @DisplayName("회원가입 성공")
    void createUserSuccess() {
        // given
        CreateUserCommand command = new CreateUserCommand("test@example.com", "password123");
        when(userQueryOutPort.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userCommandOutPort.save(any())).thenReturn(savedUser);

        // when
        UserDetailResponse response = createUserService.create(command);

        // then
        assertThat(response.email()).isEqualTo("test@example.com");
    }
}
```

**3. REST API 테스트** (REST API Layer)
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DisplayName("UserController API 테스트")
class UserControllerTest {
    @Test
    @DisplayName("POST /api/v1/users - 회원가입 성공")
    void createUserApi() {
        // given
        CreateUserRequest request = new CreateUserRequest("test@example.com", "password123");

        // when
        ResponseEntity<UserDetailApiResponse> response = restTemplate.postForEntity(
            "/api/v1/users",
            request,
            UserDetailApiResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
```

---

## Jira 통합 전략

### Jira 커스텀 필드 설계

**필수 커스텀 필드**:
1. `customfield_layer` (Single Select)
   - Options: `domain`, `application`, `persistence`, `rest-api`

2. `customfield_conventions` (Labels)
   - 준수할 컨벤션 문서 경로 목록
   - 예: `["docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md"]`

3. `customfield_branch` (Text)
   - Git 브랜치명
   - 예: `feature/USER-101-domain`

4. `customfield_depends_on` (Issue Link)
   - 의존하는 Task Key 목록
   - 예: `["USER-101"]`

5. `customfield_pr_url` (URL)
   - GitHub Pull Request URL
   - 자동으로 업데이트

### Jira Automation Rules

**Rule 1: Task 상태 전환 시 Slack 알림**
```yaml
Trigger: Issue transitioned
Condition: Status changed to "In Progress"
Action: Send Slack message
  - Channel: #dev-automation
  - Message: "🚀 [{{issue.key}}] {{issue.summary}} 개발 시작 (Layer: {{issue.customfield_layer}})"
```

**Rule 2: PR 생성 시 Jira Task 자동 전환**
```yaml
Trigger: Webhook (GitHub PR created)
Condition: PR title contains issue key
Action: Transition issue to "Review"
Action: Add comment
  - Comment: "✅ PR 생성됨: {{pr.url}}"
```

**Rule 3: 의존성 Task 완료 시 자동 시작**
```yaml
Trigger: Issue transitioned
Condition: Status changed to "Done"
Action: For linked issues (is depended on by)
  - Transition to "In Progress"
  - Add comment: "⏩ 의존성 Task {{issue.key}} 완료, 개발 시작 가능"
```

---

## 실행 예시

### 전체 워크플로우 실행

```bash
# Step 1: PRD 작성 (Manual or AI)
/sc:brainstorm "User 관리 바운디드 컨텍스트 PRD 작성"
# 출력: prd/user-management-context.md

# Step 2: 기술 문서 작성
/sc:design user-management --type architecture --format spec
# 출력: docs/technical/user-management-design.md

# Step 3: Jira Epic/Task 자동 생성
/bounded-context-init prd/user-management-context.md
# 출력:
# - Epic: USER-EPIC-1
# - Tasks: USER-101 (domain), USER-102 (application), USER-103 (persistence), USER-104 (rest-api)

# Step 4: Claude Squad 병렬 개발 시작
./scripts/claude-squad-start.sh USER-EPIC-1
# 자동 실행:
# - Phase 1: Domain Layer (USER-101)
# - Phase 2: Application + Persistence (USER-102 + USER-103, 병렬)
# - Phase 3: REST API (USER-104)
# - Phase 4: 통합 테스트

# Step 5: 결과 확인
# - 4개 PR 생성됨
# - Jira Tasks → "Review" 상태
# - 모든 검증 통과
```

### 수동 개발 (단일 Task)

```bash
# Jira Task 분석 + TodoList 생성
/jira-task USER-101

# 자동 개발 실행
/domain User 관리 바운디드 컨텍스트 개발

# 검증
/test domain

# PR 생성
gh pr create --title "feat(domain): USER-101 User 도메인 구현" \
  --body "$(gh issue view USER-101 --repo ryuqqq/jira-tasks --json body -q .body)"
```

---

## 예상 효과

### 개발 속도
- **기존**: 1개 바운디드 컨텍스트 개발 → 약 8시간 (Layer별 순차 개발)
- **자동화**: 1개 바운디드 컨텍스트 개발 → 약 2시간 (병렬 + 자동화)
- **개선율**: **75% 시간 단축**

### 품질
- **컨벤션 준수율**: 100% (실시간 자동 검증)
- **테스트 커버리지**: 95% 이상 (자동 테스트 생성)
- **아키텍처 규칙 위반**: 0건 (ArchUnit 자동 검증)

### 문서화
- **PRD/기술 문서**: 100% (자동 생성)
- **Jira 추적성**: 100% (Epic → Task → PR 완전 연결)
- **커밋 메시지**: 표준화 (자동 생성)

---

## 다음 단계

### 구현 우선순위

**Phase 1: 기본 워크플로우 구축** (1-2주)
1. ✅ `/bounded-context-init` Slash Command 구현
2. ✅ `scripts/claude-squad-start.sh` 오케스트레이터 구현
3. ✅ `scripts/claude-worker.sh` Worker 스크립트 구현
4. ✅ Layer별 Slash Commands 구현 (`/domain`, `/application`, `/persistence`, `/rest`)

**Phase 2: 자동 검증 강화** (1주)
1. ✅ `validation-helper.py` Layer별 검증 로직 강화
2. ✅ ArchUnit 테스트 자동 생성
3. ✅ 통합 테스트 자동 생성

**Phase 3: Jira 통합 고도화** (1주)
1. ✅ Jira 커스텀 필드 설정
2. ✅ Jira Automation Rules 설정
3. ✅ Slack 알림 통합

**Phase 4: 병렬 실행 최적화** (1주)
1. ⏳ Git worktree 기반 동시 브랜치 체크아웃
2. ⏳ 리소스 사용량 모니터링
3. ⏳ 에러 복구 및 재시도 로직

**Phase 5: 프로덕션 적용** (지속적)
1. ⏳ 실제 바운디드 컨텍스트에 적용
2. ⏳ 피드백 수집 및 개선
3. ⏳ 추가 Layer 지원 (Event-Driven, Caching 등)

---

## 참고 자료

### 관련 문서
- [Dynamic Hooks Guide](../DYNAMIC_HOOKS_GUIDE.md)
- [Jira Task Command](./../.claude/commands/jira-task.md)
- [Coding Conventions](./coding_convention/)

### 외부 도구
- [Claude Code](https://claude.ai/code)
- [Jira REST API](https://developer.atlassian.com/cloud/jira/platform/rest/v3/)
- [GitHub CLI](https://cli.github.com/)
- [ArchUnit](https://www.archunit.org/)
