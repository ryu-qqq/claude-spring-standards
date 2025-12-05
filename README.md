# Spring Standards Template

> **Spring Boot 3.5.x + Java 21 헥사고날 아키텍처 템플릿**
> Documentation-Driven Development 기반 엔터프라이즈 표준 프로젝트

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](https://alistair.cockburn.us/hexagonal-architecture/)
[![Claude Skills](https://img.shields.io/badge/Claude%20Skills-14-purple.svg)](#claude-skills-14개)
[![Docs](https://img.shields.io/badge/Docs-GitHub%20Pages-blue.svg)](https://ryu-qqq.github.io/claude-spring-standards/)

---

## 이 프로젝트는?

**Spring Boot 3.5.x + Java 21** 기반의 프로덕션 레디 헥사고날 아키텍처 템플릿입니다.

**14개 전문 Claude Skills**와 **88개 코딩 컨벤션 문서**가 일관된 고품질 코드 생성을 보장합니다.

### 핵심 철학

| 원칙 | 설명 |
|------|------|
| **Documentation-Driven** | 88개 코딩 컨벤션 문서가 설계를 강제 |
| **Smart Strategy** | 기존 코드 수정 → TDD, 신규 코드 생성 → Doc-Driven |
| **Zero-Tolerance** | Lombok 금지, Law of Demeter, Long FK 전략 |
| **AI-First** | Claude Code + Serena MCP + 14개 전문 Skills |

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

## 개발 플로우

### Plan → Impl 프로세스

```
기능 요청
    ↓
/plan "{기능명}"
    ↓
┌─────────────────────────────────────────┐
│ 1️⃣ 요구사항 분석 (requirements-analyst) │
│ 2️⃣ 영향도 분석 (layer-architect)        │
│ 3️⃣ 전략 결정 (TDD vs Doc-Driven)       │
│ 4️⃣ Serena Memory 저장                  │
└─────────────────────────────────────────┘
    ↓                      ↓
기존 코드 수정            신규 코드 생성
    ↓                      ↓
┌──────────┐          ┌──────────┐
│   TDD    │          │ Doc-Driven│
│ /kb/*/go │          │  /impl    │
└──────────┘          └──────────┘
    ↓                      ↓
    └───────────┬──────────┘
                ↓
         ./gradlew test
                ↓
            feat: 커밋
```

### 1단계: 기능 분석 및 계획

```bash
/plan "주문 취소 기능"
```

**출력 예시**:
```markdown
## 📊 영향도 분석 결과

| 레이어 | 파일 | 상태 | 전략 |
|--------|------|------|------|
| Domain | Order.java | 🔧 수정 | TDD |
| Application | - | 🆕 신규 | Doc |
| Persistence | OrderEntity.java | 🔧 수정 | TDD |
| REST API | - | 🆕 신규 | Doc |

## 🚀 실행 계획
1. [TDD] Domain: Order.cancel() 추가
2. [Doc] Application: CancelOrderUseCase 생성
3. [TDD] Persistence: OrderEntity 수정
4. [Doc] REST API: POST /orders/{id}/cancel
```

### 2단계: Layer별 구현

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
│   ├── commands/              # Claude Commands
│   └── skills/                # 14개 Claude Skills
├── terraform/                 # IaC 인프라
└── local-dev/                 # 로컬 개발 환경 (Docker Compose)
```

---

## Claude Skills (14개)

프로젝트에 특화된 14개 전문 Skills이 레이어별 구현을 가이드합니다.

### Planning & Analysis

| Skill | 역할 | 활성화 |
|-------|------|--------|
| `requirements-analyst` | 추상적 요구사항 → 구체적 비즈니스 규칙 (BR-XXX) | `/plan` |
| `layer-architect` | 영향도 분석, TDD vs Doc-Driven 결정 | `/plan` |

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
| `repository-expert` | JpaRepository, QueryDslRepository (DTO Projection) | `/impl:persistence` |
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

## Claude Commands

### 핵심 Commands

| Command | 용도 |
|---------|------|
| `/plan "{기능}"` | 요구사항 분석 + 영향도 분석 + 구현 계획 |
| `/impl:domain {feature}` | Domain Layer 구현 (Aggregate, VO, Event) |
| `/impl:application {feature}` | Application Layer 구현 (UseCase, Service) |
| `/impl:persistence {feature}` | Persistence Layer 구현 (Entity, Repository) |
| `/impl:rest-api {feature}` | REST API Layer 구현 (Controller, DTO) |
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

## Zero-Tolerance 규칙

다음 규칙은 **예외 없이** 반드시 준수해야 합니다.

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

    public Order(OrderId id, Long userId) {
        this.id = id;
        this.userId = userId;
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

## Serena Memory 연동

### 컴팩팅 대응

```
세션 1                          세션 2 (컴팩팅 후)
────────                        ────────────────────
/plan "기능A"                   "아까 작업 이어서"
  ↓                               ↓
write_memory("plan-A")          read_memory("plan-A")
  ↓                               ↓
/impl:domain ...                컨텍스트 복구 ✅
  ↓                               ↓
⚠️ 오토컴팩팅                   /impl:application ... 계속
```

### 작업 재개

```bash
# 진행 중인 작업 확인
"현재 진행 중인 작업 확인해줘"

# 특정 작업 재개
"주문 취소 작업 이어서 해줘"
```

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

© 2025 Ryu-qqq. All Rights Reserved.

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

*최종 업데이트: 2025-12-05*
