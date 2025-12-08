# Spring Standards Template

> **Spring Boot 3.5.x + Java 21 헥사고날 아키텍처 템플릿**
> Documentation-Driven Development 기반 엔터프라이즈 표준 프로젝트

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](https://alistair.cockburn.us/hexagonal-architecture/)
[![Claude Skills](https://img.shields.io/badge/Claude%20Skills-15-purple.svg)](#claude-skills-15개)
[![Serena Memories](https://img.shields.io/badge/Serena%20Memories-45-green.svg)](#serena-memory-시스템)
[![Docs](https://img.shields.io/badge/Docs-GitHub%20Pages-blue.svg)](https://ryu-qqq.github.io/claude-spring-standards/)

---

## 이 프로젝트는?

**Spring Boot 3.5.x + Java 21** 기반의 프로덕션 레디 헥사고날 아키텍처 템플릿입니다.

**15개 전문 Claude Skills**, **12개 Claude Commands**, **5개 자동화 Hooks**, **45개 Serena Memories**가 일관된 고품질 코드 생성을 보장합니다.

### 핵심 철학

| 원칙 | 설명 |
|------|------|
| **Documentation-Driven** | 88개 코딩 컨벤션 문서가 설계를 강제 |
| **Smart Strategy** | 기존 코드 수정 → TDD, 신규 코드 생성 → Doc-Driven |
| **Zero-Tolerance** | Lombok 금지, Law of Demeter, Long FK 전략 |
| **AI-First** | Claude Code + Serena MCP + 자동화 Hooks |
| **Contract-First** | 레이어 간 계약 명시로 병렬 작업 안전 보장 |

---

## 빠른 시작

```bash
# 1. 템플릿 클론
git clone https://github.com/ryu-qqq/claude-spring-standards.git my-project
cd my-project

# 2. Git Hooks 설치
./scripts/setup-hooks.sh

# 3. 빌드 및 테스트
./gradlew clean build

# 4. Claude Code에서 기능 개발 시작
/plan "회원 가입 기능"
```

---

## 개발 사이클

### 전체 플로우

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Development Lifecycle                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   /plan "{기능}"                                                             │
│       ↓                                                                      │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │ 1️⃣ 요구사항 분석 (requirements-analyst)                              │   │
│   │ 2️⃣ 영향도 분석 (layer-architect) - Serena MCP                       │   │
│   │ 3️⃣ 전략 결정: TDD vs Doc-Driven                                     │   │
│   │ 4️⃣ plan-{feature} 메모리 저장                                       │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│       ↓                                                                      │
│   /design "{기능}"                                                           │
│       ↓                                                                      │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │ 1️⃣ 사용 이력 검색 (component-usage-history)                          │   │
│   │ 2️⃣ 기본 골격 생성 (component-dependency-graph)                       │   │
│   │ 3️⃣ 옵션 질문 (component-options)                                     │   │
│   │ 4️⃣ 컴포넌트 목록 + 레이어 간 계약 명시                               │   │
│   │ 5️⃣ 체크리스트 JSON 생성                                              │   │
│   │ 6️⃣ design-{feature} 메모리 저장                                      │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│       ↓                                                                      │
│   /impl:{layer} {feature}  또는  /kb/{layer}/go                             │
│       ↓                                                                      │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │ 신규 생성 → /impl:domain, /impl:application, /impl:persistence, ...  │   │
│   │ 기존 수정 → /kb/domain/go, /kb/application/go, ...                    │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│       ↓                                                                      │
│   /status {feature}  →  진행률 확인                                          │
│       ↓                                                                      │
│   /verify {feature}  →  체크리스트 검증 + ArchUnit                           │
│       ↓                                                                      │
│   /complete {feature}  →  아카이브 + 메모리 정리                              │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 단계별 상세

#### 1단계: 기능 분석 (`/plan`)

```bash
/plan "주문 취소 기능"
```

**프로세스**:
1. **요구사항 분석**: 질문으로 비즈니스 규칙 도출
2. **영향도 분석**: Serena MCP로 기존 코드 검색
3. **전략 결정**: 수정(TDD) vs 신규(Doc-Driven)
4. **메모리 저장**: `plan-{feature}` 자동 저장

#### 2단계: 컴포넌트 설계 (`/design`)

```bash
/design "주문 취소 기능"
```

**프로세스**:
1. **패턴 추천**: 사용 이력 기반 유사 패턴 제안
2. **컴포넌트 도출**: 레이어별 필요 컴포넌트 목록화
3. **계약 명시**: 레이어 간 인터페이스 스펙 정의 (병렬 작업 안전)
4. **체크리스트 생성**: 규칙 기반 JSON 생성
5. **메모리 저장**: `design-{feature}` 저장

#### 3단계: Layer별 구현

```bash
# 신규 코드 생성 (Doc-Driven)
/impl:domain order-cancel       # Aggregate, VO, Exception, Event
/impl:application order-cancel  # UseCase, Service, DTO, Assembler
/impl:persistence order-cancel  # Entity, Repository, Mapper, Adapter
/impl:rest-api order-cancel     # Controller, Request/Response DTO

# 기존 코드 수정 (TDD)
/kb/domain/go                   # Domain 기존 코드 TDD 수정
/kb/application/go              # Application 기존 코드 TDD 수정
```

#### 4단계: 검증 및 완료

```bash
# 진행률 빠른 확인
/status order-cancel

# 상세 검증 (규칙 + ArchUnit)
/verify order-cancel

# 완료 처리 (아카이브 + 메모리 정리)
/complete order-cancel
```

---

## 리팩토링 워크플로우

기존 코드 리팩토링은 별도 플로우를 사용합니다.

```bash
# 리팩토링 분석
/refactor-plan domain           # Domain Layer 전체 분석
/refactor-plan Order            # 특정 클래스 분석

# 결과: 코드 스멜 + 규칙 위반 + Phase별 계획

# 실행
/kb/domain/go "Order Aggregate Lombok 제거"
```

### `/plan` vs `/refactor-plan`

| 항목 | `/plan` | `/refactor-plan` |
|------|---------|------------------|
| **목적** | 신규 기능 설계 | 기존 코드 개선 |
| **입력** | 비즈니스 요구사항 | 코드/패키지/레이어 |
| **분석** | 컴포넌트 설계 | 코드 스멜, 규칙 위반 |
| **출력** | 체크리스트 + 계약 | 문제점 + 수정 전략 |

---

## Claude Commands (12개)

### 핵심 개발 Commands

| Command | 용도 |
|---------|------|
| `/plan "{기능}"` | 요구사항 분석 + 영향도 분석 + 구현 전략 |
| `/design "{기능}"` | 컴포넌트 설계 + 체크리스트 JSON 생성 |
| `/impl:domain {feature}` | Domain Layer 구현 |
| `/impl:application {feature}` | Application Layer 구현 |
| `/impl:persistence {feature}` | Persistence Layer 구현 |
| `/impl:rest-api {feature}` | REST API Layer 구현 |

### 진행 관리 Commands

| Command | 용도 |
|---------|------|
| `/status {feature}` | 진행률 시각화 (빠른 확인) |
| `/verify {feature}` | 체크리스트 검증 + ArchUnit 테스트 |
| `/complete {feature}` | 완료 처리 + 아카이브 + 메모리 정리 |

### 기타 Commands

| Command | 용도 |
|---------|------|
| `/refactor-plan [scope]` | 리팩토링 분석 및 계획 |
| `/create-prd` | PRD 문서 생성 |
| `/memory-guide` | Serena Memory 사용 가이드 |

### TDD Commands

| Command | 용도 |
|---------|------|
| `/kb/domain/go` | Domain 기존 코드 TDD 수정 |
| `/kb/application/go` | Application 기존 코드 TDD 수정 |
| `/kb/persistence/go` | Persistence 기존 코드 TDD 수정 |
| `/kb/rest-api/go` | REST API 기존 코드 TDD 수정 |

---

## Claude Hooks (5개)

휴먼 에러 방지를 위한 자동화 훅 시스템입니다.

| Hook | 트리거 | 역할 |
|------|--------|------|
| `user-prompt-submit.sh` | 프롬프트 제출 | 진행 중인 작업 표시 |
| `pre-tool-use-impl.sh` | `/impl` 실행 전 | plan/design 메모리 존재 확인 |
| `pre-tool-use-edit.sh` | Edit/Write 전 | 코딩 컨벤션 검증 |
| `post-tool-use-format.sh` | Edit/Write 후 | 코드 포맷팅 |
| `stop-session.sh` | 세션 종료 | 미완료 작업 경고 |

### 훅 동작 예시

```bash
# /impl 실행 시 자동 검증
/impl:domain order-cancel

# Hook 결과:
# ✅ plan-order-cancel 메모리 존재
# ✅ design-order-cancel 메모리 존재
# → 구현 진행

# 메모리 없을 경우:
# ⚠️ plan-order-cancel 메모리가 없습니다.
# 먼저 /plan "주문 취소" 를 실행하세요.
# → 실행 차단
```

---

## Serena Memory 시스템

### 메모리 구조 (45개)

```
Serena Memories
├── 📋 Plan/Design (작업별)
│   ├── plan-{feature}         # 분석 결과 + 구현 계획
│   └── design-{feature}       # 체크리스트 JSON + 계약
│
├── 🧩 Component System (3개)
│   ├── component-dependency-graph   # 전체 레이어 의존성
│   ├── component-options            # 선택적 컴포넌트 + 질문
│   └── component-usage-history      # 패턴 사용 이력
│
├── 📜 Layer Rules (42개)
│   ├── domain-rules-*         # Domain Layer 규칙 (6개)
│   ├── app-rules-*            # Application Layer 규칙 (6개)
│   ├── persistence-rules-*    # Persistence Layer 규칙 (11개)
│   ├── redis-rules-*          # Redis 규칙 (4개)
│   └── rest-api-rules-*       # REST API Layer 규칙 (9개)
│
└── 🔍 Validation Rules (6개)
    ├── convention-domain-layer-validation-rules
    ├── convention-application-layer-validation-rules
    ├── convention-persistence-mysql-validation-rules
    ├── convention-persistence-redis-validation-rules
    └── convention-rest-api-layer-validation-rules
```

### 메모리 라이프사이클

```
/plan → plan-{feature} 생성
    ↓
/design → design-{feature} 생성
    ↓
/impl → 규칙 메모리 참조하며 구현
    ↓
/complete → 아카이브 저장 → plan/design 메모리 삭제
```

### 세션 연속성

```
세션 1                          세션 2 (컴팩팅 후)
────────                        ────────────────────
/plan "기능A"                   "아까 작업 이어서"
  ↓                               ↓
write_memory("plan-A")          read_memory("plan-A")
  ↓                               ↓
/design "기능A"                 컨텍스트 복구 ✅
  ↓                               ↓
write_memory("design-A")        /impl:application 계속
  ↓
⚠️ 오토컴팩팅
```

---

## Claude Skills (15개)

### Planning & Analysis

| Skill | 역할 | 활성화 |
|-------|------|--------|
| `requirements-analyst` | 추상적 요구사항 → 구체적 비즈니스 규칙 | `/plan` |
| `layer-architect` | 영향도 분석, TDD vs Doc-Driven 결정 | `/plan` |
| `refactoring-analyst` | 코드 스멜 탐지, 리팩토링 전략 | `/refactor-plan` |

### Domain Layer

| Skill | 역할 | 활성화 |
|-------|------|--------|
| `domain-expert` | Aggregate, VO, Event, Exception 설계 | `/impl:domain` |

### Application Layer

| Skill | 역할 | 활성화 |
|-------|------|--------|
| `usecase-expert` | Port-In 인터페이스, UseCase/Service 구현 | `/impl:application` |
| `transaction-expert` | TransactionManager, ReadManager, Facade | `/impl:application` |
| `factory-assembler-expert` | CommandFactory, QueryAssembler, Bundle | `/impl:application` |

### Persistence Layer

| Skill | 역할 | 활성화 |
|-------|------|--------|
| `entity-mapper-expert` | JPA Entity, EntityMapper (Long FK) | `/impl:persistence` |
| `repository-expert` | JpaRepository, QueryDslRepository | `/impl:persistence` |
| `adapter-expert` | CommandAdapter, QueryAdapter, LockAdapter | `/impl:persistence` |
| `redis-expert` | Lettuce 캐시 + Redisson 분산락 | `/impl:persistence` |

### REST API Layer

| Skill | 역할 | 활성화 |
|-------|------|--------|
| `controller-expert` | REST Controller, Command/Query DTO | `/impl:rest-api` |

### Cross-Cutting

| Skill | 역할 | 활성화 |
|-------|------|--------|
| `testing-expert` | Integration Test, TestRestTemplate, Fixtures | 테스트 작성 시 |
| `project-setup-expert` | Multi-module 구조, Gradle, Version Catalog | 프로젝트 설정 시 |
| `devops-expert` | GitHub Actions, Terraform, Docker Compose | CI/CD 설정 시 |

---

## 아키텍처

### Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────────┐
│                         adapter-in/                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  rest-api/                                               │   │
│  │  ├─ controller/    # Command/Query Controller 분리       │   │
│  │  ├─ dto/           # Command/Query/Response DTO          │   │
│  │  ├─ mapper/        # API ↔ Application DTO 변환          │   │
│  │  └─ error/         # ErrorMapper (RFC 7807)             │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                               ↓
┌─────────────────────────────────────────────────────────────────┐
│                        application/                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  port/in/         # UseCase 인터페이스                    │   │
│  │  port/out/        # Repository/Cache/Lock Port          │   │
│  │  service/         # UseCase 구현 (Command/Query 분리)    │   │
│  │  manager/         # TransactionManager, ReadManager      │   │
│  │  facade/          # CommandFacade, QueryFacade           │   │
│  │  factory/         # CommandFactory (Domain 생성)         │   │
│  │  assembler/       # QueryAssembler (Response 조립)       │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                               ↓
┌─────────────────────────────────────────────────────────────────┐
│                          domain/                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  aggregate/       # Aggregate Root (비즈니스 로직)        │   │
│  │  vo/              # Value Object (불변 객체)             │   │
│  │  event/           # Domain Event                        │   │
│  │  exception/       # Domain Exception                    │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                               ↓
┌─────────────────────────────────────────────────────────────────┐
│                        adapter-out/                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  persistence-mysql/                                      │   │
│  │  ├─ entity/        # JPA Entity (Long FK 전략)           │   │
│  │  ├─ repository/    # JpaRepository, QueryDslRepository   │   │
│  │  ├─ mapper/        # EntityMapper (Domain ↔ Entity)      │   │
│  │  └─ adapter/       # CommandAdapter, QueryAdapter        │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │  persistence-redis/                                      │   │
│  │  ├─ adapter/       # CacheAdapter, LockAdapter           │   │
│  │  └─ config/        # Lettuce + Redisson 설정             │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 모듈 구조

```
claude-spring-standards/
├── adapter-in/
│   └── rest-api/              # REST API Layer
├── adapter-out/
│   ├── persistence-mysql/     # MySQL 영속성
│   └── persistence-redis/     # Redis 캐시/분산락
├── application/               # Application Layer
├── domain/                    # Domain Layer
├── bootstrap/
│   └── bootstrap-web-api/     # Spring Boot 메인 모듈
├── docs/
│   └── coding_convention/     # 88개 코딩 컨벤션 문서
├── .claude/
│   ├── commands/              # 12개 Claude Commands
│   ├── skills/                # 15개 Claude Skills
│   └── hooks/                 # 5개 자동화 Hooks
├── .serena/
│   └── memories/              # 45개 Serena Memories
├── terraform/                 # IaC 인프라
└── local-dev/                 # 로컬 개발 환경 (Docker Compose)
```

---

## Zero-Tolerance 규칙

### 1. Lombok 금지

```java
// ❌ BAD
@Data
@Builder
public class Order { }

// ✅ GOOD - Plain Java
public class Order {
    private final OrderId id;
    private final Long userId;

    private Order(OrderId id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public static Order forNew(Long userId) {
        return new Order(OrderId.empty(), userId);
    }

    public OrderId id() { return id; }
    public Long userId() { return userId; }
}
```

### 2. Law of Demeter (Getter 체이닝 금지)

```java
// ❌ BAD - Tell, Don't Ask 위반
String zipCode = order.getCustomer().getAddress().getZipCode();

// ✅ GOOD - 행동 요청
String zipCode = order.getCustomerZipCode();
```

### 3. Long FK Strategy (JPA 관계 금지)

```java
// ❌ BAD
@ManyToOne
private User user;

// ✅ GOOD
private Long userId;
```

### 4. Transaction 경계

```java
// ❌ BAD - @Transactional 내 외부 API 호출
@Transactional
public void process() {
    save(entity);
    externalApi.call();  // 트랜잭션 오염!
}

// ✅ GOOD - 트랜잭션 분리
public void process() {
    transactionManager.persist(entity);
    externalApi.call();  // 트랜잭션 밖에서 호출
}
```

### 5. CQRS 분리

```java
// ✅ Command: persist() - 상태 변경
@Transactional
public Order persist(Order order) { }

// ✅ Query: findBy*(), get*() - 상태 조회
@Transactional(readOnly = true)
public Optional<Order> findById(OrderId id) { }
```

---

## 레이어 간 계약 (Contract)

`/design` 커맨드에서 자동 생성되며, 병렬 작업 시 인터페이스 불일치를 방지합니다.

```markdown
## Domain ↔ Application 계약

| 항목 | 스펙 |
|------|------|
| Aggregate 메서드 | `Order.cancel(Instant now): void` |
| 반환 VO | `OrderId` |
| 발행 Event | `OrderCancelledEvent(OrderId, Instant)` |

## Application ↔ Persistence 계약

| Port | 메서드 시그니처 | 반환 |
|------|----------------|------|
| `OrderPersistencePort` | `persist(Order)` | `OrderId` |
| `OrderQueryPort` | `findById(OrderId)` | `Optional<Order>` |

## Application ↔ REST API 계약

| 구분 | 타입 | 필드 |
|------|------|------|
| Request | `CancelOrderRequest` | `reason: String` |
| Response | `OrderResponse` | `id, status, cancelledAt` |
```

---

## 아카이브 시스템

완료된 작업은 외부 아카이브에 저장됩니다.

**위치**: `/Users/sangwon-ryu/archive/{project}/{feature}/{date}.md`

```bash
/complete order-cancel
# → /Users/sangwon-ryu/archive/claude-spring-standards/order-cancel/2024-12-08.md
```

**아카이브 내용**:
- 요구사항 요약
- 생성된 컴포넌트 목록
- 레이어 간 계약
- 특이사항

---

## 기술 스택

| 카테고리 | 기술 |
|----------|------|
| **언어** | Java 21 |
| **프레임워크** | Spring Boot 3.5.x |
| **아키텍처** | Hexagonal (Ports & Adapters) |
| **설계 패턴** | DDD, CQRS |
| **ORM** | JPA + QueryDSL 5.x |
| **캐시** | Redis (Lettuce) |
| **분산락** | Redis (Redisson) |
| **테스팅** | JUnit 5, Mockito, Testcontainers, ArchUnit |
| **빌드** | Gradle 8.x, Version Catalog |
| **인프라** | Terraform, Docker Compose, GitHub Actions |
| **AI 도구** | Claude Code, Serena MCP, CodeRabbit |

---

## 문서

### 핵심 가이드

| 문서 | 설명 |
|------|------|
| [코딩 컨벤션](docs/coding_convention/) | 88개 상세 규칙 |
| [.claude/CLAUDE.md](.claude/CLAUDE.md) | 프로젝트 설정 |
| [.coderabbit.yaml](.coderabbit.yaml) | CodeRabbit 설정 |

### Layer별 가이드

| Layer | 문서 |
|-------|------|
| Domain | [docs/coding_convention/02-domain-layer/](docs/coding_convention/02-domain-layer/) |
| Application | [docs/coding_convention/03-application-layer/](docs/coding_convention/03-application-layer/) |
| Persistence | [docs/coding_convention/04-persistence-layer/](docs/coding_convention/04-persistence-layer/) |
| REST API | [docs/coding_convention/01-adapter-in-layer/](docs/coding_convention/01-adapter-in-layer/) |
| Testing | [docs/coding_convention/05-testing/](docs/coding_convention/05-testing/) |

### 온라인 문서

**URL**: https://ryu-qqq.github.io/claude-spring-standards/

---

## 커밋 규칙

| Prefix | 용도 | 예시 |
|--------|------|------|
| `feat:` | 기능 추가 (구현 + 테스트 포함) | `feat: 주문 취소 기능 구현` |
| `fix:` | 버그 수정 | `fix: Email null 처리 누락 수정` |
| `refactor:` | 리팩토링 | `refactor: OrderService 메서드 분리` |
| `docs:` | 문서 변경 | `docs: README 업데이트` |
| `chore:` | 빌드/설정 변경 | `chore: Gradle 버전 업데이트` |

---

## 라이선스

(c) 2025 Ryu-qqq. All Rights Reserved.

---

## 기여

이슈와 PR은 언제나 환영합니다!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 링크

- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)

---

*최종 업데이트: 2025-12-08*
