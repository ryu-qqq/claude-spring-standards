# Claude Code Slash Commands

이 디렉토리는 Claude Code에서 사용 가능한 슬래시 커맨드들을 포함합니다.

---

## 🎯 Claude Code vs Windsurf - 역할 분담

**핵심 원칙**: Windsurf는 빠른 Boilerplate 생성, Claude Code는 고품질 검증 및 테스트

자세한 역할 정의는 [ROLE_DEFINITION.md](./ROLE_DEFINITION.md)를 참조하세요.

---

## 🧠 Serena Memory 시스템

모든 코드 생성 커맨드는 **Serena Memory + Cache**를 함께 사용합니다:

1. **세션 시작**: `/cc:load` 실행 → Serena 메모리 활성화
2. **자동 로드**: Layer별 컨벤션이 메모리에 상주
3. **코드 생성**: Serena 메모리 우선 참조 + Cache 보조
4. **실시간 검증**: Cache 기반 고속 검증

**효과**:
- 세션 간 컨텍스트 유지 (Claude가 이전 컨벤션 기억)
- 78% 컨벤션 위반 감소 (23회 → 5회)
- 47% 세션 시간 단축 (15분 → 8분)

**상세**: [/cc:load 명령어](./cc/load.md), [Serena 설정 가이드](../hooks/scripts/setup-serena-conventions.sh)

---

## 📋 사용 가능한 커맨드

### 🚀 세션 관리

#### `/cc:load`
**목적**: 코딩 컨벤션 자동 로드 (세션 시작 시 실행)

**사용법**:
```bash
/cc:load
```

**효과**:
- Serena Memory에 5개 컨벤션 로드
- 세션 간 컨텍스트 유지
- 컨벤션 위반 78% 감소

---

### 🧪 테스트 자동 생성 (New Role)

#### `/test-gen-domain`
**목적**: Domain 계층 단위 테스트 자동 생성

**사용법**:
```bash
/test-gen-domain Order
/test-gen-domain Payment
```

**생성 내용**:
- Happy Path (성공 케이스)
- Edge Cases (경계값)
- Exception Cases (예외 처리)
- Invariant Validation (불변식 검증)

**예상 테스트 수**: 12-15개

**상세**: [test-gen-domain.md](./test-gen-domain.md)

---

#### `/test-gen-usecase`
**목적**: Application UseCase 단위 테스트 자동 생성

**사용법**:
```bash
/test-gen-usecase PlaceOrder
/test-gen-usecase CancelOrder
```

**생성 내용**:
- Transaction 경계 검증
- Port Interface Mock
- Command Validation
- Exception Handling

**예상 테스트 수**: 10-12개

**상세**: [test-gen-usecase.md](./test-gen-usecase.md)

---

#### `/test-gen-repository-unit`
**목적**: Repository 단위 테스트 자동 생성 (Mock 기반, 빠른 실행)

**사용법**:
```bash
/test-gen-repository-unit OrderRepository
/test-gen-repository-unit OrderQueryService
```

**생성 내용**:
- Mock 기반 CRUD 테스트
- Test Fixtures (재사용 가능한 테스트 데이터)
- QueryDSL Mock 테스트
- 비즈니스 로직 격리
- Exception 처리

**예상 테스트 수**: 10-12개

**실행 속도**: 밀리초 단위 (매우 빠름)

**상세**: [test-gen-repository-unit.md](./test-gen-repository-unit.md)

---

#### `/test-gen-repository-integration`
**목적**: Repository 통합 테스트 자동 생성 (Testcontainers, Real DB)

**사용법**:
```bash
/test-gen-repository-integration OrderRepository
/test-gen-repository-integration OrderQueryService
```

**생성 내용**:
- Testcontainers 설정 (실제 MySQL)
- Real CRUD 테스트
- **N+1 쿼리 검증** (Fetch Join)
- Transaction Rollback 테스트
- DB Constraints 테스트 (Unique, FK)
- 동시성 테스트

**예상 테스트 수**: 15-20개

**실행 속도**: 초 단위 (느림)

**상세**: [test-gen-repository-integration.md](./test-gen-repository-integration.md)

---

#### `/test-gen-integration`
**목적**: Infrastructure 통합 테스트 자동 생성 (Redis, Kafka 등)

**사용법**:
```bash
/test-gen-integration OrderCacheService
/test-gen-integration OrderEventPublisher
/test-gen-integration PaymentGatewayClient
```

**생성 내용**:
- Redis Cache 통합 테스트
- Kafka Event 통합 테스트
- External API 통합 테스트
- Message Queue 테스트

**예상 테스트 수**: 10-15개

**상세**: [test-gen-integration.md](./test-gen-integration.md)

---

#### `/test-gen-e2e`
**목적**: REST API E2E 시나리오 테스트 자동 생성 (RestAssured 기반)

**사용법**:
```bash
/test-gen-e2e OrderApi
/test-gen-e2e PaymentApi
```

**생성 내용**:
- Happy Path 시나리오 (전체 플로우)
- Multi-step 복잡 시나리오
- Error Handling (400, 404, 500)
- Security (인증/권한)
- Performance (대량 요청)
- Idempotency (멱등성)

**예상 테스트 수**: 10-15개

**상세**: [test-gen-e2e.md](./test-gen-e2e.md)

---

#### `/test-gen-api-docs`
**목적**: API 문서 자동 생성 (Spring REST Docs + OpenAPI 3.0)

**사용법**:
```bash
/test-gen-api-docs OrderApi
/test-gen-api-docs PaymentApi
```

**생성 내용**:
- Spring REST Docs 테스트
- Request/Response Fields 문서화
- Error Response 문서화
- AsciiDoc 템플릿
- OpenAPI 3.0 Spec

**생성 파일**:
- `{Api}DocumentationTest.java`
- `{api}.adoc`
- `openapi.json`

**상세**: [test-gen-api-docs.md](./test-gen-api-docs.md)

---

#### `/test-gen-testcontainers`
**목적**: Testcontainers 설정 자동 생성 (MySQL, Redis, Kafka)

**사용법**:
```bash
/test-gen-testcontainers MySQL
/test-gen-testcontainers Redis
/test-gen-testcontainers Kafka
/test-gen-testcontainers All
```

**생성 내용**:
- TestcontainersConfiguration (공통 설정)
- AbstractIntegrationTest (Base Class)
- AbstractMySQLIntegrationTest
- AbstractRedisIntegrationTest
- AbstractKafkaIntegrationTest
- Gradle 빌드 설정

**상세**: [test-gen-testcontainers.md](./test-gen-testcontainers.md)

---

### 🔍 코드 리뷰 & 개선 (New Role)

#### `/code-review`
**목적**: 코드 리뷰 및 구체적인 개선 제안

**사용법**:
```bash
/code-review domain/Order.java
/code-review application/order/
/code-review --recent
```

**검토 항목**:
- 🚨 Convention Violations (Lombok, Law of Demeter 등)
- ⚡ Performance Issues (N+1, 비효율적 Stream)
- 🛡️ Security Vulnerabilities (민감 정보 로깅)
- 🧪 Testing Gaps (테스트 누락, 커버리지)
- 🏗️ Architecture Violations (Layer 의존성)

**출력**:
- Before/After 비교 코드
- 구체적인 수정 방법
- 우선순위 (Critical/Important/Nice-to-have)
- Overall Score (100점 만점)

**상세**: [code-review.md](./code-review.md)

---

### 🔧 검증 커맨드

#### `/validate-domain`
**목적**: Domain layer 파일 검증

**사용법**:
```bash
/validate-domain domain/Order.java
```

**검증 항목**:
- Lombok 사용 금지
- Law of Demeter (Getter 체이닝)
- Setter 사용 금지
- Javadoc 필수

---

#### `/validate-architecture`
**목적**: ArchUnit 기반 아키텍처 검증

**사용법**:
```bash
/validate-architecture
/validate-architecture domain
```

**검증 항목**:
- Layer 의존성 규칙
- 네이밍 규칙
- JPA 관계 어노테이션 금지
- @Transactional 경계

---

### 🤖 AI 리뷰

#### `/ai-review`
**목적**: 통합 AI 리뷰 (Gemini + CodeRabbit + Codex)

**사용법**:
```bash
/ai-review
/ai-review 123
/ai-review --bots gemini,coderabbit
/ai-review --strategy merge
```

**기능**:
- 3개 AI 봇 병렬 실행
- TodoList 자동 생성
- 우선순위 자동 분류

---

#### `/gemini-review`
**목적**: Gemini Code Assist 전용 리뷰 (Deprecated)

**권장**: `/ai-review --bots gemini` 사용

---

### 📋 Jira 통합

#### `/jira-analyze`
**목적**: Jira 이슈 분석 및 TodoList 생성

**사용법**:
```bash
/jira-analyze PROJ-123
/jira-analyze https://ryuqqq.atlassian.net/browse/PROJ-123
```

**기능**:
- Jira Issue 상세 정보 조회
- Epic 정보 포함
- TodoList 자동 생성
- Feature 브랜치 자동 생성/체크아웃

**상세**: [jira-analyze.md](./jira-analyze.md)

---

#### `/jira-create`
**목적**: 새로운 Jira 이슈 생성

**사용법**:
```bash
/jira-create --project PROJ --type Task --summary "사용자 로그인 기능"
/jira-create --project PROJ --type Story --summary "주문 API" --priority High
/jira-create --project PROJ --type Task --summary "결제 테스트" --epic PROJ-10
```

**기능**:
- Task, Story, Bug, Epic 생성
- 담당자, 우선순위, 라벨 설정
- Epic Link 연동
- 브랜치 생성 제안

**상세**: [jira-create.md](./jira-create.md)

---

#### `/jira-transition`
**목적**: Jira 이슈 상태 변경 (To Do → In Progress → Done)

**사용법**:
```bash
/jira-transition PROJ-123 "In Progress"
/jira-transition PROJ-123 "Code Review" --comment "PR 생성 완료"
/jira-transition PROJ-123 "Done" --resolution "Fixed"
```

**기능**:
- 상태 전환 (To Do, In Progress, Code Review, Done)
- 해결 방법 설정 (Fixed, Won't Fix 등)
- 코멘트 추가
- Git 브랜치 정리 (Done 전환 시)

**상세**: [jira-transition.md](./jira-transition.md)

---

#### `/jira-update`
**목적**: Jira 이슈 정보 업데이트

**사용법**:
```bash
/jira-update PROJ-123 --summary "로그인 기능 개선"
/jira-update PROJ-123 --priority "High" --story-points 8
/jira-update PROJ-123 --add-labels "security,authentication"
/jira-update PROJ-123 --assignee "user@example.com"
```

**기능**:
- 제목, 설명, 담당자, 우선순위 변경
- 라벨 추가/제거
- Story Points 설정
- Epic Link 변경

**상세**: [jira-update.md](./jira-update.md)

---

#### `/jira-comment`
**목적**: Jira 이슈에 코멘트 추가

**사용법**:
```bash
/jira-comment PROJ-123 "테스트 완료했습니다."
/jira-comment PROJ-123 "@john.doe 리뷰 부탁드립니다."
/jira-comment PROJ-123 "PR 생성: https://github.com/org/repo/pull/456"
```

**기능**:
- 텍스트 코멘트 추가
- 사용자 멘션
- 코드 스니펫, 링크 포함
- 진행 상황 업데이트

**상세**: [jira-comment.md](./jira-comment.md)

---

#### `/jira-link-pr`
**목적**: GitHub PR과 Jira 이슈 연동

**사용법**:
```bash
/jira-link-pr PROJ-123  # 현재 브랜치의 PR 자동 감지
/jira-link-pr PROJ-123 --pr 456
/jira-link-pr PROJ-123 --pr 456 --transition "Code Review"
```

**기능**:
- PR 정보를 Jira 이슈에 링크
- Jira 이슈를 PR에 링크
- 상태 자동 전환 (In Progress → Code Review)
- 코멘트 자동 추가

**상세**: [jira-link-pr.md](./jira-link-pr.md)

---

## 🚀 권장 워크플로우

### 1. 새로운 기능 개발 (Full Stack)

```bash
# 1️⃣ Windsurf: Boilerplate 생성
"Order Aggregate를 생성해줘"
→ OrderDomain.java, OrderId.java, OrderStatus.java

# 2️⃣ Claude Code: 비즈니스 로직 구현
/cc:load
"Order Domain에 비즈니스 메서드 구현해줘"
→ placeOrder(), cancelOrder(), confirmOrder()

# 3️⃣ Claude Code: 테스트 피라미드 자동 생성 ⭐
/test-gen-domain Order
→ OrderDomainTest.java (15개 단위 테스트)

/test-gen-usecase PlaceOrder
→ PlaceOrderUseCaseTest.java (12개 UseCase 테스트)

/test-gen-integration OrderRepository
→ OrderRepositoryIntegrationTest.java (20개 통합 테스트)

/test-gen-e2e OrderApi
→ OrderApiE2ETest.java (15개 E2E 시나리오)

/test-gen-api-docs OrderApi
→ OrderApiDocumentationTest.java + openapi.json

# 4️⃣ Claude Code: 코드 리뷰
/code-review domain/Order.java
→ 개선 제안 5가지

# 5️⃣ Claude Code: TODO 수정 및 테스트 보강
"Fix all TODOs and add edge case tests"

# 6️⃣ Windsurf: 최종 검증
/validate-architecture
→ ArchUnit 테스트 통과

# 7️⃣ PR 생성
gh pr create
```

### 2. 기존 코드 개선

```bash
# 1️⃣ Claude Code: 코드 리뷰
/code-review domain/Order.java
→ 개선 제안 5가지

# 2️⃣ Claude Code: 리팩토링
"Refactor Order.java to follow Law of Demeter"

# 3️⃣ Claude Code: 테스트 보강
/test-enhance Order
→ 누락된 엣지 케이스 추가

# 4️⃣ Windsurf: 검증
/validate-conventions
```

### 3. 버그 수정

```bash
# 1️⃣ Claude Code: 근본 원인 분석
/root-cause "Order cancel fails with NPE"
→ Sequential thinking으로 원인 파악

# 2️⃣ Claude Code: 수정 및 회귀 테스트
"Fix the NPE and add regression tests"

# 3️⃣ Windsurf: 검증
/run-unit-tests
```

### 4. Jira 통합 워크플로우 (전체 라이프사이클) ⭐

```bash
# 1️⃣ Jira 이슈 분석 및 작업 시작
/jira-analyze PROJ-123
→ Epic 정보 포함 조회
→ TodoList 자동 생성 (10개 작업 항목)
→ feature/PROJ-123-user-login 브랜치 생성

# 2️⃣ 작업 시작 상태로 변경
/jira-transition PROJ-123 "In Progress"
→ Jira 상태: To Do → In Progress
→ 시작 시간 자동 기록

# 3️⃣ 진행 상황 업데이트
/jira-comment PROJ-123 "## 진행 상황
- ✅ Domain 모델 작성 완료
- 🔄 UseCase 구현 중
- ⏳ 테스트 작성 예정"
→ 팀원들에게 진행 상황 공유

# 4️⃣ 코드 작업 진행
/cc:load  # 컨벤션 로드
"Order Domain에 비즈니스 메서드 구현"
/test-gen-domain Order
/test-gen-usecase PlaceOrder

# 5️⃣ PR 생성 및 Jira 연동
git add . && git commit -m "feat(PROJ-123): 사용자 로그인 구현"
gh pr create --title "feat(PROJ-123): 사용자 로그인 기능" --body "..."

/jira-link-pr PROJ-123 --transition "Code Review"
→ PR 링크를 Jira에 자동 추가
→ Jira 이슈를 PR에 자동 링크
→ 상태 자동 변경: In Progress → Code Review

# 6️⃣ 코드 리뷰 반영
/jira-comment PROJ-123 "@reviewer 리뷰 반영 완료했습니다.
수정 사항:
- Law of Demeter 적용
- 엣지 케이스 테스트 추가"

# 7️⃣ PR 머지 후 완료 처리
/jira-transition PROJ-123 "Done" --resolution "Fixed" --comment "✅ PR #456 머지 완료
- 모든 테스트 통과
- 프로덕션 배포 완료"

→ Jira 상태: Code Review → Done
→ 로컬 브랜치 정리 제안

# 🎯 결과
# - Jira 이슈 전체 라이프사이클 자동 관리
# - GitHub PR과 완벽 연동
# - 팀 투명성 향상 (진행 상황 실시간 공유)
# - 수동 작업 90% 절감
```

### 5. 복잡한 Epic 관리

```bash
# 1️⃣ Epic 분석
/jira-analyze PROJ-100  # Epic
→ Epic 목표 및 하위 태스크 조회
→ Epic 전체 TodoList 생성 (50개 작업)

# 2️⃣ 하위 Task 생성
/jira-create --project PROJ --type Task --summary "사용자 인증 API" --epic PROJ-100 --priority High
→ Task PROJ-101 생성
→ Epic PROJ-100에 자동 링크

/jira-create --project PROJ --type Task --summary "권한 관리 API" --epic PROJ-100
→ Task PROJ-102 생성

# 3️⃣ 각 Task 작업 및 완료
/jira-analyze PROJ-101
→ TodoList 생성 및 작업

/jira-transition PROJ-101 "Done"
→ Epic 진행률 자동 업데이트 (33%)

# 4️⃣ Epic 완료 확인
/jira-analyze PROJ-100
→ 모든 하위 Task 완료 확인
→ Epic 완료 처리
```

---

## 💡 Claude Code의 핵심 강점

### 1. 테스트 피라미드 자동 생성 (New Role) ⭐
- **6가지 테스트 유형 완벽 지원**:
  - 단위 테스트 (Domain, UseCase)
  - 통합 테스트 (Repository + Testcontainers)
  - E2E 테스트 (REST API 시나리오)
  - API 문서 (Spring REST Docs + OpenAPI)
  - 테스트 인프라 (Testcontainers 설정)
- 비즈니스 로직 이해 → 자동 테스트 생성
- 엣지 케이스, 경계값, 예외 처리 자동 커버
- **예상 생성 테스트**: 단위 15개 + 통합 20개 + E2E 15개 = **총 50개**

### 2. 코드 리뷰 및 개선 (New Role) ⭐
- PR 리뷰 시간 90% 절감
- 컨벤션 위반, 성능 이슈, 보안 취약점 자동 탐지
- **구체적인 개선 코드 제시** (Before/After)

### 3. 복잡한 비즈니스 로직 구현 (Existing Role)
- Serena Memory로 프로젝트 컨텍스트 유지
- DDD 패턴 이해 (Aggregate, Value Object, Domain Event)
- Law of Demeter, SOLID 자동 준수

---

## 📊 명령어 정리

| 카테고리 | 명령어 | 역할 | 우선순위 |
|---------|--------|------|---------|
| **세션 관리** | /cc:load | 컨벤션 로드 | 🔴 필수 |
| **단위 테스트** | /test-gen-domain | Domain 단위 | 🟢 권장 |
| **단위 테스트** | /test-gen-usecase | UseCase 단위 | 🟢 권장 |
| **단위 테스트** | /test-gen-repository-unit | Repository 단위 (Mock) | 🟢 권장 |
| **통합 테스트** | /test-gen-repository-integration | Repository 통합 (Testcontainers) | 🟢 권장 |
| **통합 테스트** | /test-gen-integration | Infrastructure 통합 (Redis, Kafka) | 🟢 권장 |
| **E2E 테스트** | /test-gen-e2e | API E2E 시나리오 | 🟢 권장 |
| **API 문서** | /test-gen-api-docs | REST Docs + OpenAPI | 🟢 권장 |
| **테스트 인프라** | /test-gen-testcontainers | Testcontainers 설정 | 🟡 선택 |
| **코드 리뷰** | /code-review | 리뷰 & 개선 | 🟢 권장 |
| **검증** | /validate-domain | Domain 검증 | 🟡 선택 |
| **검증** | /validate-architecture | 아키텍처 검증 | 🟡 선택 |
| **AI 리뷰** | /ai-review | 통합 AI 리뷰 | 🟢 권장 |
| **Jira** | /jira-analyze | 이슈 분석 & Todo | 🟢 권장 |
| **Jira** | /jira-create | 이슈 생성 | 🟡 선택 |
| **Jira** | /jira-transition | 상태 변경 | 🟢 권장 |
| **Jira** | /jira-update | 이슈 정보 수정 | 🟡 선택 |
| **Jira** | /jira-comment | 코멘트 추가 | 🟡 선택 |
| **Jira** | /jira-link-pr | PR 연동 | 🟢 권장 |

---

## 📖 참고 문서

### 역할 정의 & 전략
- [ROLE_DEFINITION.md](./ROLE_DEFINITION.md) - Claude Code vs Windsurf 역할 정의
- [TEST_AUTOMATION_STRATEGY.md](./TEST_AUTOMATION_STRATEGY.md) - 테스트 자동화 전략

### 세션 관리
- [/cc:load](./cc/load.md) - Serena 컨벤션 로드

### 단위 테스트 생성
- [test-gen-domain.md](./test-gen-domain.md) - Domain 계층 테스트
- [test-gen-usecase.md](./test-gen-usecase.md) - UseCase 테스트

### 통합/E2E 테스트 생성
- [test-gen-integration.md](./test-gen-integration.md) - Repository 통합 테스트
- [test-gen-e2e.md](./test-gen-e2e.md) - REST API E2E 테스트
- [test-gen-api-docs.md](./test-gen-api-docs.md) - API 문서 자동 생성
- [test-gen-testcontainers.md](./test-gen-testcontainers.md) - Testcontainers 설정

### 코드 품질
- [code-review.md](./code-review.md) - 코드 리뷰 및 개선

---

**✅ 핵심**: Windsurf는 "빠른 생성", Claude Code는 "고품질 검증 및 테스트"

**💡 시너지**: Windsurf Boilerplate → Claude Test → Claude Review → Windsurf Validate
