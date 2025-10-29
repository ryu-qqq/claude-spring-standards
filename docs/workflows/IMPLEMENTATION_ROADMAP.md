# Windsurf Cascade + Claude Code 통합 구현 로드맵

## 📋 문서 목적
- **컨텍스트 압축 대비**: 작업 진행 상황을 문서로 기록하여 압축 후에도 작업 흐름 유지
- **체크리스트 방식**: 완료된 작업을 명확히 표시하여 중복 작업 방지
- **작업 일관성**: 세션 간 작업 연속성 보장

## 📊 전체 진행 상황

| Phase | 항목 | 상태 | 진행률 | 예상 소요 | 비고 |
|-------|------|------|--------|-----------|------|
| **Phase 0** | 문서 작성 | ✅ 완료 | 100% | - | 설계 완료 |
| **Phase 1** | Windsurf Cascade 설정 | ⏳ 진행 중 | 32.8% | 1주 | Rules 15/15, Workflows 5/20 |
| **Phase 2** | 통합 스크립트 작성 | ⏸️ 대기 | 0% | 1주 | Bash 스크립트 4개 |
| **Phase 3** | Claude Code 명령 업데이트 | ⏸️ 대기 | 0% | 3일 | Slash Commands 3개 |
| **Phase 4** | 테스트 및 검증 | ⏸️ 대기 | 0% | 1주 | 실제 적용 및 측정 |
| **Phase 5** | 프로덕션 적용 | ⏸️ 대기 | 0% | 지속적 | 팀 교육 및 개선 |

**총 진행률**: 35.2% (Phase 0 완료 + Phase 1 Rules 15/15 + Workflows 5/20)

---

## Phase 0: 문서 작성 ✅ 완료

### ✅ 완료된 작업
- [x] `BOUNDED_CONTEXT_AUTOMATION_WORKFLOW.md` 작성 (기본 워크플로우)
- [x] `WINDSURF_CASCADE_INTEGRATION.md` 작성 (통합 전략)
- [x] `IMPLEMENTATION_ROADMAP.md` 작성 (이 문서, 작업 추적용)

### 📂 생성된 파일
```
docs/workflows/
├── BOUNDED_CONTEXT_AUTOMATION_WORKFLOW.md  ✅
├── WINDSURF_CASCADE_INTEGRATION.md          ✅
└── IMPLEMENTATION_ROADMAP.md                ✅ (현재 파일)
```

---

## Phase 1: Windsurf Cascade 설정 (1주 예상)

### 목표
Windsurf Cascade Rules, Workflows, Templates 디렉토리 구조 생성 및 90개 규칙 변환

### 1.1 Rules 디렉토리 생성 및 규칙 변환

#### 📂 생성할 디렉토리 구조
```
.windsurf/
└── rules/
    ├── 00-global/
    │   ├── lombok-prohibition.yaml
    │   ├── javadoc-required.yaml
    │   └── naming-conventions.yaml
    │
    ├── 01-domain-layer/
    │   ├── law-of-demeter.yaml
    │   ├── aggregate-rules.yaml
    │   └── value-object-rules.yaml
    │
    ├── 02-application-layer/
    │   ├── transaction-boundary.yaml
    │   ├── usecase-naming.yaml
    │   └── assembler-rules.yaml
    │
    ├── 03-persistence-layer/
    │   ├── long-fk-strategy.yaml
    │   ├── jpa-relationships-prohibition.yaml
    │   └── entity-naming.yaml
    │
    └── 04-rest-api-layer/
        ├── controller-naming.yaml
        ├── request-validation.yaml
        └── error-handling.yaml
```

#### ✅ 작업 체크리스트

**1.1.1 Global Rules (00-global/)** - 진행률: 3/3 ✅
- [x] `lombok-prohibition.yaml` 생성
  - 패턴: `@Data|@Builder|@Getter|@Setter|@AllArgsConstructor`
  - 액션: `block`
  - 메시지: "Lombok 사용 금지! Pure Java getter/setter 작성"
  - ✅ 완료: 2025-01-28

- [x] `javadoc-required.yaml` 생성
  - 패턴: `public class|public interface` without Javadoc
  - 액션: `suggest`
  - 자동 수정: Javadoc 템플릿 삽입
  - ✅ 완료: 2025-01-28

- [x] `naming-conventions.yaml` 생성
  - 패턴: UseCase, QueryService, OutPort 네이밍 규칙
  - 액션: `warn`
  - ✅ 완료: 2025-01-28

**1.1.2 Domain Layer Rules (01-domain-layer/)** - 진행률: 3/3 ✅
- [x] `law-of-demeter.yaml` 생성
  - 패턴: `\\w+\\.get\\w+\\(\\)\\.get\\w+\\(\\)`
  - 액션: `block`
  - 메시지: "Getter 체이닝 금지! Tell, Don't Ask 원칙 적용"
  - ✅ 완료: 2025-01-28

- [x] `aggregate-rules.yaml` 생성
  - 패턴: Aggregate Root 필수 메서드 (create, of, equals, hashCode)
  - 액션: `suggest`
  - ✅ 완료: 2025-01-28

- [x] `value-object-rules.yaml` 생성
  - 패턴: Java 21 Records 권장
  - 액션: `suggest`
  - ✅ 완료: 2025-01-28

**1.1.3 Application Layer Rules (02-application-layer/)** - 진행률: 3/3 ✅
- [x] `transaction-boundary.yaml` 생성
  - 패턴: `@Transactional` 메서드 내 `RestTemplate|WebClient|HttpClient` 사용
  - 액션: `block`
  - 메시지: "트랜잭션 내 외부 API 호출 금지!"
  - ✅ 완료: 2025-01-28

- [x] `usecase-naming.yaml` 생성
  - 패턴: `*UseCase|*QueryService` 네이밍
  - 액션: `warn`
  - ✅ 완료: 2025-01-28

- [x] `assembler-rules.yaml` 생성
  - 패턴: `@Component` 어노테이션 필수
  - 액션: `suggest`
  - ✅ 완료: 2025-01-28

**1.1.4 Persistence Layer Rules (03-persistence-layer/)** - 진행률: 3/3 ✅
- [x] `long-fk-strategy.yaml` 생성
  - 패턴: `@ManyToOne|@OneToMany|@OneToOne|@ManyToMany`
  - 액션: `block`
  - 메시지: "JPA 관계 어노테이션 금지! Long FK 사용"
  - 자동 수정: `@ManyToOne` → `private Long userId;`
  - ✅ 완료: 2025-01-28

- [x] `jpa-relationships-prohibition.yaml` 생성
  - 패턴: JPA 관계 어노테이션 감지
  - 액션: `block`
  - ✅ 완료: 2025-01-28

- [x] `entity-naming.yaml` 생성
  - 패턴: `*JpaEntity` 네이밍
  - 액션: `warn`
  - ✅ 완료: 2025-01-28

**1.1.5 REST API Layer Rules (04-rest-api-layer/)** - 진행률: 3/3 ✅
- [x] `controller-naming.yaml` 생성
  - 패턴: `*Controller` 네이밍, `@RestController` 어노테이션
  - 액션: `warn`
  - ✅ 완료: 2025-01-28

- [x] `request-validation.yaml` 생성
  - 패턴: Request DTO에 `@Valid` 어노테이션
  - 액션: `suggest`
  - ✅ 완료: 2025-01-28

- [x] `error-handling.yaml` 생성
  - 패턴: `ErrorMapper` 인터페이스 구현
  - 액션: `suggest`
  - ✅ 완료: 2025-01-28

#### 📝 진행 노트
- **진행률**: 15/15 Rules (100%) ✅
- **완료 작업**:
  - Global Rules 3개 완료 (2025-01-28)
  - Domain Layer Rules 3개 완료 (2025-01-28)
  - Application Layer Rules 3개 완료 (2025-01-28)
  - Persistence Layer Rules 3개 완료 (2025-01-28)
  - REST API Layer Rules 3개 완료 (2025-01-28)
- **다음 작업**: Phase 1.2 Workflows 디렉토리 생성 (20개 워크플로우)
- **예상 소요 시간**: Workflow 1개당 45분 = 15시간

---

### 1.2 Workflows 디렉토리 생성

#### 📂 생성할 디렉토리 구조
```
.windsurf/
└── workflows/
    ├── 01-domain/
    │   ├── create-aggregate.yaml
    │   ├── create-value-object.yaml
    │   ├── create-domain-exception.yaml
    │   └── create-domain-event.yaml
    │
    ├── 02-application/
    │   ├── create-usecase.yaml
    │   ├── create-application-service.yaml
    │   ├── create-assembler.yaml
    │   └── create-outport.yaml
    │
    ├── 03-persistence/
    │   ├── create-jpa-entity.yaml
    │   ├── create-repository.yaml
    │   ├── create-persistence-adapter.yaml
    │   └── create-entity-mapper.yaml
    │
    ├── 04-rest-api/
    │   ├── create-controller.yaml
    │   ├── create-request-dto.yaml
    │   ├── create-response-dto.yaml
    │   └── create-error-mapper.yaml
    │
    └── 99-testing/
        ├── create-domain-test.yaml
        ├── create-application-test.yaml
        ├── create-persistence-test.yaml
        └── create-rest-api-test.yaml
```

#### ✅ 작업 체크리스트

**1.2.1 Domain Workflows (01-domain/)** - 진행률: 4/4 ✅
- [x] `create-aggregate.yaml` 생성
  - Trigger: `/cascade:create-aggregate {name}`
  - Steps: 패키지 생성 → Aggregate Root → Value Objects → Exceptions → ErrorCode → Tests
  - Templates: `domain-aggregate.java.j2`, `value-object.java.j2`, 등
  - ✅ 완료: 2025-01-28

- [x] `create-value-object.yaml` 생성
  - Trigger: `/cascade:create-value-object {name}`
  - Steps: Java 21 Record 생성 + 검증 로직 골격
  - ✅ 완료: 2025-01-28

- [x] `create-domain-exception.yaml` 생성
  - Trigger: `/cascade:create-domain-exception {name}`
  - Steps: Sealed class hierarchy 생성
  - ✅ 완료: 2025-01-28

- [x] `create-domain-event.yaml` 생성
  - Trigger: `/cascade:create-domain-event {name}`
  - Steps: Domain Event Record 생성
  - ✅ 완료: 2025-01-28

**1.2.2 Application Workflows (02-application/)** - 진행률: 4/4 ✅
- [x] `create-usecase.yaml` 생성
  - Trigger: `/cascade:create-usecase {name} {aggregate}`
  - Steps: UseCase 인터페이스 → Command/Query DTO → Response DTO → Service → OutPort
  - ✅ 완료: 2025-01-28

- [x] `create-application-service.yaml` 생성 (간결 버전)
  - Trigger: `/cascade:create-application-service {name}`
  - Steps: Service 클래스 골격 + @Service + @Transactional
  - ✅ 완료: 2025-01-28 (토큰 최적화: ~595 tokens)

- [x] `create-assembler.yaml` 생성 (간결 버전)
  - Trigger: `/cascade:create-assembler {aggregate}`
  - Steps: Assembler 클래스 + @Component + 기본 변환 메서드
  - ✅ 완료: 2025-01-28 (토큰 최적화: ~679 tokens)

- [x] `create-outport.yaml` 생성 (간결 버전)
  - Trigger: `/cascade:create-outport {name} {type: command|query}`
  - Steps: OutPort 인터페이스 생성
  - ✅ 완료: 2025-01-28 (토큰 최적화: ~697 tokens)

**1.2.3 Persistence Workflows (03-persistence/)** - 진행률: 0/4
- [ ] `create-jpa-entity.yaml` 생성
  - Trigger: `/cascade:create-jpa-entity {aggregate}`
  - Steps: JPA Entity + @Entity/@Table + Long FK 필드 + Repository + Mapper + Adapter
  - Rules 적용: `long-fk-strategy`, `jpa-relationships-prohibition`

- [ ] `create-repository.yaml` 생성
  - Trigger: `/cascade:create-repository {aggregate}`
  - Steps: JpaRepository 인터페이스 생성

- [ ] `create-persistence-adapter.yaml` 생성
  - Trigger: `/cascade:create-persistence-adapter {aggregate}`
  - Steps: Adapter 클래스 + @Component + OutPort 구현

- [ ] `create-entity-mapper.yaml` 생성
  - Trigger: `/cascade:create-entity-mapper {aggregate}`
  - Steps: Mapper 클래스 + Domain ↔ Entity 변환 메서드

**1.2.4 REST API Workflows (04-rest-api/)** - 진행률: 0/4
- [ ] `create-controller.yaml` 생성
  - Trigger: `/cascade:create-controller {aggregate} --endpoints [create,get,update,delete]`
  - Steps: Controller + @RestController + Request/Response DTOs + Mapper + Error Mapper

- [ ] `create-request-dto.yaml` 생성
  - Trigger: `/cascade:create-request-dto {name}`
  - Steps: Java 21 Record + @Valid

- [ ] `create-response-dto.yaml` 생성
  - Trigger: `/cascade:create-response-dto {name}`
  - Steps: Java 21 Record

- [ ] `create-error-mapper.yaml` 생성
  - Trigger: `/cascade:create-error-mapper {aggregate}`
  - Steps: ErrorMapper 구현 + @Component + HTTP Status 매핑

**1.2.5 Testing Workflows (99-testing/)** - 진행률: 0/4
- [ ] `create-domain-test.yaml` 생성
  - Trigger: `/cascade:create-domain-test {aggregate}`
  - Steps: JUnit 5 Test 골격 + @DisplayName

- [ ] `create-application-test.yaml` 생성
  - Trigger: `/cascade:create-application-test {service}`
  - Steps: Service Test 골격 + @Mock

- [ ] `create-persistence-test.yaml` 생성
  - Trigger: `/cascade:create-persistence-test {entity}`
  - Steps: Repository Test 골격 + @DataJpaTest

- [ ] `create-rest-api-test.yaml` 생성
  - Trigger: `/cascade:create-rest-api-test {controller}`
  - Steps: REST API Test 골격 + @SpringBootTest + @WebMvcTest

#### 📝 진행 노트
- **진행률**: 8/20 Workflows (40%)
- **완료 작업**:
  - Domain Workflows 4개 완료 (2025-01-28)
  - Application Workflows 4개 완료 (2025-01-28) ✅
  - **토큰 최적화 전략 적용**: create-usecase.yaml 리팩토링 대기 (~6,709 tokens → 목표 ~2,000 tokens)
  - **간결 버전 성과**: create-application-service, create-assembler, create-outport 평균 657 tokens (목표 대비 67% 절감)
- **다음 작업**: Persistence Workflows 4개 (간결 버전으로 작성)
- **예상 소요 시간**: Workflow 1개당 30분 (간결 버전) = 6시간 (남은 12개)

---

### 1.3 Templates 디렉토리 생성

#### 📂 생성할 디렉토리 구조
```
.windsurf/
└── templates/
    ├── domain/
    │   ├── domain-aggregate.java.j2
    │   ├── value-object.java.j2
    │   ├── domain-exception.java.j2
    │   ├── specific-exception.java.j2
    │   ├── error-code-enum.java.j2
    │   └── domain-test.java.j2
    │
    ├── application/
    │   ├── usecase-interface.java.j2
    │   ├── command-dto.java.j2
    │   ├── query-dto.java.j2
    │   ├── response-dto.java.j2
    │   ├── application-service.java.j2
    │   ├── outport-interface.java.j2
    │   └── assembler.java.j2
    │
    ├── persistence/
    │   ├── jpa-entity.java.j2
    │   ├── jpa-repository.java.j2
    │   ├── entity-mapper.java.j2
    │   └── persistence-adapter.java.j2
    │
    ├── rest-api/
    │   ├── rest-controller.java.j2
    │   ├── request-dto.java.j2
    │   ├── response-dto.java.j2
    │   ├── api-mapper.java.j2
    │   └── error-mapper.java.j2
    │
    └── testing/
        ├── domain-test.java.j2
        ├── application-test.java.j2
        ├── persistence-test.java.j2
        └── rest-api-test.java.j2
```

#### ✅ 작업 체크리스트

**1.3.1 Domain Templates (domain/)** - 진행률: 0/6
- [ ] `domain-aggregate.java.j2` 생성
  - Java 21 Record 기반 Aggregate Root 템플릿
  - 기본 필드: id, content, status, audit
  - 기본 메서드 골격: create(), of(), equals(), hashCode()

- [ ] `value-object.java.j2` 생성
  - Java 21 Record 기반 Value Object 템플릿
  - 검증 로직 골격

- [ ] `domain-exception.java.j2` 생성
  - Sealed abstract class 템플릿
  - DomainException 상속

- [ ] `specific-exception.java.j2` 생성
  - Final class 템플릿 (예: *NotFoundException)
  - ErrorCode enum 참조

- [ ] `error-code-enum.java.j2` 생성
  - ErrorCode enum 템플릿
  - HTTP Status 코드 포함

- [ ] `domain-test.java.j2` 생성
  - JUnit 5 Test 템플릿
  - @DisplayName, @Test

**1.3.2 Application Templates (application/)** - 진행률: 0/7
- [ ] `usecase-interface.java.j2` 생성
  - UseCase/QueryService 인터페이스 템플릿

- [ ] `command-dto.java.j2` 생성
  - Java 21 Record 기반 Command DTO 템플릿

- [ ] `query-dto.java.j2` 생성
  - Java 21 Record 기반 Query DTO 템플릿

- [ ] `response-dto.java.j2` 생성
  - Java 21 Record 기반 Response DTO 템플릿

- [ ] `application-service.java.j2` 생성
  - Application Service 클래스 템플릿
  - @Service, @Transactional

- [ ] `outport-interface.java.j2` 생성
  - OutPort 인터페이스 템플릿
  - *CommandOutPort / *QueryOutPort

- [ ] `assembler.java.j2` 생성
  - Assembler 클래스 템플릿
  - @Component
  - 변환 메서드 골격

**1.3.3 Persistence Templates (persistence/)** - 진행률: 0/4
- [ ] `jpa-entity.java.j2` 생성
  - JPA Entity 클래스 템플릿
  - @Entity, @Table, @Id, @Column
  - Long FK 필드 (관계 어노테이션 없음)

- [ ] `jpa-repository.java.j2` 생성
  - JpaRepository 인터페이스 템플릿

- [ ] `entity-mapper.java.j2` 생성
  - Entity Mapper 클래스 템플릿
  - Domain ↔ Entity 변환 메서드

- [ ] `persistence-adapter.java.j2` 생성
  - Persistence Adapter 클래스 템플릿
  - @Component
  - OutPort 구현

**1.3.4 REST API Templates (rest-api/)** - 진행률: 0/5
- [ ] `rest-controller.java.j2` 생성
  - REST Controller 클래스 템플릿
  - @RestController, @RequestMapping
  - CRUD 엔드포인트 골격

- [ ] `request-dto.java.j2` 생성
  - Java 21 Record 기반 Request DTO 템플릿
  - @Valid

- [ ] `response-dto.java.j2` 생성
  - Java 21 Record 기반 Response DTO 템플릿

- [ ] `api-mapper.java.j2` 생성
  - API Mapper 클래스 템플릿
  - @Component

- [ ] `error-mapper.java.j2` 생성
  - ErrorMapper 구현 템플릿
  - @Component
  - HTTP Status 매핑

**1.3.5 Testing Templates (testing/)** - 진행률: 0/4
- [ ] `domain-test.java.j2` 생성
  - Domain Test 템플릿

- [ ] `application-test.java.j2` 생성
  - Application Service Test 템플릿
  - @Mock, @InjectMocks

- [ ] `persistence-test.java.j2` 생성
  - Repository Test 템플릿
  - @DataJpaTest

- [ ] `rest-api-test.java.j2` 생성
  - REST API Test 템플릿
  - @SpringBootTest, @WebMvcTest

#### 📝 진행 노트
- **진행률**: 0/26 Templates (0%)
- **다음 작업**: Domain Templates 6개부터 시작
- **예상 소요 시간**: Template 1개당 30분 = 13시간

---

### Phase 1 총 진행률
- **Rules**: 15/15 (100%) ✅
- **Workflows**: 8/20 (40%)
- **Templates**: 0/26 (0%)
- **전체**: 23/61 (37.7%)
- **예상 소요 시간**: 6h (Workflows) + 13h (Templates) = **19시간 (약 2.5일)**
- **토큰 최적화 효과**: 간결 버전 적용 시 평균 657 tokens/file (목표 대비 67% 절감)

---

## Phase 2: 통합 스크립트 작성 (1주 예상)

### 목표
Cascade + Claude Code 통합을 위한 Bash 스크립트 4개 작성

### 2.1 Cascade 보일러플레이트 생성 스크립트

#### ✅ 작업 체크리스트
- [ ] `scripts/cascade-generate-boilerplate.sh` 생성
  - 기능: Cascade Workflow 실행 (Layer별)
  - 입력: Task Key, Layer, Aggregate Name
  - 출력: 생성된 파일 목록
  - 예상 시간: 2시간

#### 📝 스크립트 개요
```bash
#!/bin/bash
# Usage: ./scripts/cascade-generate-boilerplate.sh <task-key> <layer> <aggregate-name>

# Layer별 Cascade Workflow 실행
case "$layer" in
  domain)
    windsurf cascade run create-aggregate --aggregateName "$aggregate"
    ;;
  application)
    windsurf cascade run create-usecase --aggregateName "$aggregate" --useCaseName "Create$aggregate"
    ;;
  persistence)
    windsurf cascade run create-jpa-entity --aggregateName "$aggregate"
    ;;
  rest-api)
    windsurf cascade run create-controller --aggregateName "$aggregate"
    ;;
esac
```

---

### 2.2 Claude Code 비즈니스 로직 구현 스크립트

#### ✅ 작업 체크리스트
- [ ] `scripts/claude-implement-business-logic.sh` 생성
  - 기능: Claude Code 실행 (비즈니스 로직 구현)
  - 입력: Task Key, Layer
  - 출력: 구현 완료 메시지
  - 예상 시간: 2시간

#### 📝 스크립트 개요
```bash
#!/bin/bash
# Usage: ./scripts/claude-implement-business-logic.sh <task-key> <layer>

# Claude Code 실행
claude code run << EOF
/jira-task $task_key
/analyze-generated-files $layer
/implement-business-logic $layer
EOF
```

---

### 2.3 통합 검증 스크립트

#### ✅ 작업 체크리스트
- [ ] `scripts/integrated-validation.sh` 생성
  - 기능: Cascade Rules 검증 + 테스트 실행 + Claude Code 분석
  - 입력: Layer
  - 출력: 검증 결과 리포트
  - 예상 시간: 3시간

#### 📝 스크립트 개요
```bash
#!/bin/bash
# Usage: ./scripts/integrated-validation.sh <layer>

# Step 1: Cascade Rules 검증
windsurf cascade validate --layer "$layer"

# Step 2: 테스트 실행
windsurf cascade run run-tests-and-analyze --layer "$layer"

# Step 3: Claude Code 분석
claude code run << EOF
/analyze-test-results claudedocs/test-analysis-$layer-*.md
/validate-architecture $layer
EOF
```

---

### 2.4 통합 오케스트레이터 스크립트

#### ✅ 작업 체크리스트
- [ ] `scripts/integrated-squad-start.sh` 생성
  - 기능: Epic 단위 전체 프로세스 오케스트레이션
  - 입력: Epic Key
  - 출력: 전체 진행 상황 로그
  - 예상 시간: 5시간

#### 📝 스크립트 개요
```bash
#!/bin/bash
# Usage: ./scripts/integrated-squad-start.sh <epic-key>

# Phase 1: Domain Layer
cascade-generate-boilerplate.sh $domain_task "domain" $aggregate
claude-implement-business-logic.sh $domain_task "domain"
integrated-validation.sh "domain"

# Phase 2: Application + Persistence (병렬)
# ...

# Phase 3: REST API Layer
# ...

# Phase 4: 통합 테스트
# ...
```

---

### Phase 2 총 진행률
- **스크립트**: 0/4 (0%)
- **예상 소요 시간**: 2h + 2h + 3h + 5h = **12시간 (약 1.5일)**

---

## Phase 3: Claude Code 명령 업데이트 (3일 예상)

### 목표
Claude Code Slash Commands 3개 추가/강화

### 3.1 `/implement-business-logic` 명령 추가

#### ✅ 작업 체크리스트
- [ ] `.claude/commands/implement-business-logic.md` 생성
  - 기능: Cascade 생성 파일 분석 → 비즈니스 로직 구현
  - 입력: Layer, Aggregate Name
  - 출력: 구현 완료 메시지
  - 예상 시간: 4시간

#### 📝 명령 개요
```markdown
# /implement-business-logic

## Triggers
- Cascade 보일러플레이트 생성 후
- 비즈니스 로직 구현 필요 시

## 실행 단계
1. Cascade 생성 파일 읽기
2. PRD/Jira Task 분석
3. 비즈니스 메서드 구현
4. 검증 로직 추가
5. 테스트 케이스 추가
```

---

### 3.2 `/analyze-test-results` 명령 강화

#### ✅ 작업 체크리스트
- [ ] `.claude/commands/analyze-test-results.md` 업데이트
  - 기능: Cascade 테스트 결과 분석 → 자동 수정 제안
  - 입력: Test Analysis Report Path
  - 출력: 수정 제안 리스트
  - 예상 시간: 3시간

#### 📝 명령 개요
```markdown
# /analyze-test-results

## 기존 기능 강화
- Cascade 테스트 결과 파싱
- 실패 원인 분석 (심층)
- 자동 수정 제안
- 수정 사항 적용 (선택)
```

---

### 3.3 `/validate-architecture` 명령 강화

#### ✅ 작업 체크리스트
- [ ] `.claude/commands/validate-architecture.md` 업데이트
  - 기능: ArchUnit 테스트 실행 → 위반 분석 → 자동 수정
  - 입력: Layer
  - 출력: 검증 결과 + 수정 제안
  - 예상 시간: 3시간

#### 📝 명령 개요
```markdown
# /validate-architecture

## 기존 기능 강화
- Layer별 ArchUnit 테스트 실행
- 위반 사항 상세 분석
- 자동 수정 제안 (규칙별)
- 수정 사항 적용 (선택)
```

---

### Phase 3 총 진행률
- **Slash Commands**: 0/3 (0%)
- **예상 소요 시간**: 4h + 3h + 3h = **10시간 (약 1.25일)**

---

## Phase 4: 테스트 및 검증 (1주 예상)

### 목표
실제 바운디드 컨텍스트에 적용하여 통합 워크플로우 검증

### 4.1 테스트 바운디드 컨텍스트 선정

#### ✅ 작업 체크리스트
- [ ] 테스트용 간단한 바운디드 컨텍스트 정의
  - 예시: "Product 관리" (제품 등록/조회/수정/삭제)
  - PRD 작성
  - 예상 시간: 2시간

---

### 4.2 Phase 1-3 통합 테스트

#### ✅ 작업 체크리스트
- [ ] Cascade Rules 적용 테스트
  - Lombok 금지 검증
  - Law of Demeter 검증
  - Long FK 전략 검증
  - 예상 시간: 4시간

- [ ] Cascade Workflows 실행 테스트
  - Domain Aggregate 생성
  - Application UseCase 생성
  - Persistence JPA Entity 생성
  - REST API Controller 생성
  - 예상 시간: 6시간

- [ ] Claude Code 비즈니스 로직 구현 테스트
  - Domain 비즈니스 메서드 구현
  - Application Service 로직 구현
  - 검증 로직 추가
  - 예상 시간: 8시간

- [ ] 통합 검증 테스트
  - ArchUnit 테스트 실행
  - 단위 테스트 실행
  - 통합 테스트 실행
  - 예상 시간: 4시간

---

### 4.3 성능 및 비용 측정

#### ✅ 작업 체크리스트
- [ ] 토큰 사용량 측정
  - Claude Code만 사용 시: 예상 106,500 토큰
  - Cascade + Claude Code 사용 시: 예상 30,500 토큰
  - 실제 측정 및 비교
  - 예상 시간: 2시간

- [ ] 개발 시간 측정
  - Claude Code만 사용 시: 예상 100분
  - Cascade + Claude Code 사용 시: 예상 65분
  - 실제 측정 및 비교
  - 예상 시간: 2시간

- [ ] 품질 지표 측정
  - 컨벤션 준수율
  - 테스트 커버리지
  - ArchUnit 테스트 통과율
  - 예상 시간: 2시간

---

### 4.4 피드백 수집 및 개선

#### ✅ 작업 체크리스트
- [ ] 워크플로우 개선 사항 식별
  - Rules 추가/수정 필요 사항
  - Workflows 최적화 필요 사항
  - Scripts 버그 수정
  - 예상 시간: 4시간

- [ ] 문서 업데이트
  - 실제 측정 결과 반영
  - 개선 사항 반영
  - 예상 시간: 2시간

---

### Phase 4 총 진행률
- **테스트 항목**: 0/10 (0%)
- **예상 소요 시간**: 2h + 4h + 6h + 8h + 4h + 2h + 2h + 2h + 4h + 2h = **36시간 (약 4.5일)**

---

## Phase 5: 프로덕션 적용 (지속적)

### 목표
팀 교육 및 실제 프로젝트 적용

### 5.1 팀 교육

#### ✅ 작업 체크리스트
- [ ] 워크플로우 가이드 작성
  - 빠른 시작 가이드 (Quick Start)
  - 상세 사용 가이드 (User Guide)
  - 문제 해결 가이드 (Troubleshooting)
  - 예상 시간: 8시간

- [ ] 팀 교육 세션 진행
  - Windsurf Cascade 사용법
  - Claude Code 통합 워크플로우
  - 실습 세션
  - 예상 시간: 4시간

---

### 5.2 실제 프로젝트 적용

#### ✅ 작업 체크리스트
- [ ] 첫 번째 바운디드 컨텍스트 적용
  - 실제 비즈니스 요구사항
  - 전체 워크플로우 적용
  - 결과 검증
  - 예상 시간: 지속적

- [ ] 추가 바운디드 컨텍스트 확장
  - 점진적 적용
  - 피드백 수집 및 개선
  - 예상 시간: 지속적

---

### 5.3 지속적 개선

#### ✅ 작업 체크리스트
- [ ] 주간 회고 및 개선
  - 워크플로우 효율성 측정
  - 개선 사항 식별 및 적용
  - 예상 시간: 지속적

- [ ] 추가 Layer 및 패턴 지원
  - Event-Driven Layer 추가
  - Caching Layer 추가
  - 예상 시간: 지속적

---

### Phase 5 총 진행률
- **항목**: 0/6 (0%)
- **예상 소요 시간**: 지속적

---

## 📊 최종 요약

### 전체 진행 상황
| Phase | 완료 | 진행률 | 예상 소요 |
|-------|------|--------|-----------|
| Phase 0 | ✅ | 100% | - |
| Phase 1 | 23/61 | 37.7% | 19시간 (2.5일) |
| Phase 2 | 0/4 | 0% | 12시간 (1.5일) |
| Phase 3 | 0/3 | 0% | 10시간 (1.25일) |
| Phase 4 | 0/10 | 0% | 36시간 (4.5일) |
| Phase 5 | 0/6 | 0% | 지속적 |

**총 예상 소요 시간**: 77시간 ≈ **9.6일** (Phase 0-4)
**토큰 최적화**: 간결 버전 전략으로 5.25시간 절감 (19h vs 24.25h)

---

## 🔄 작업 재개 프로토콜

### 컨텍스트 압축 후 재개 시

1. **이 문서 읽기**
   ```bash
   cat docs/workflows/IMPLEMENTATION_ROADMAP.md
   ```

2. **현재 Phase 확인**
   - 위 표에서 진행 중인 Phase 확인

3. **다음 작업 확인**
   - 해당 Phase의 "다음 작업" 섹션 확인

4. **작업 완료 후 체크**
   - [ ] → [x]로 변경
   - 진행률 업데이트

5. **문서 저장**
   ```bash
   git add docs/workflows/IMPLEMENTATION_ROADMAP.md
   git commit -m "docs: Phase X 작업 Y 완료"
   ```

---

## 📝 작업 로그

### 2025-01-28 (세션 1)
- ✅ Phase 0 완료: 문서 3개 작성
- ✅ `IMPLEMENTATION_ROADMAP.md` 생성 (이 문서)

### 2025-01-28 (세션 2)
- ✅ Phase 1.1.1 완료: Global Rules 3개 작성
  - `.windsurf/rules/00-global/lombok-prohibition.yaml`
  - `.windsurf/rules/00-global/javadoc-required.yaml`
  - `.windsurf/rules/00-global/naming-conventions.yaml`
- ✅ Phase 1.1.2 완료: Domain Layer Rules 3개 작성
  - `.windsurf/rules/01-domain-layer/law-of-demeter.yaml`
  - `.windsurf/rules/01-domain-layer/aggregate-rules.yaml`
  - `.windsurf/rules/01-domain-layer/value-object-rules.yaml`
- ✅ Phase 1.1.3 완료: Application Layer Rules 3개 작성
  - `.windsurf/rules/02-application-layer/transaction-boundary.yaml`
  - `.windsurf/rules/02-application-layer/usecase-naming.yaml`
  - `.windsurf/rules/02-application-layer/assembler-rules.yaml`
- ✅ Phase 1.1.4 완료: Persistence Layer Rules 3개 작성
  - `.windsurf/rules/03-persistence-layer/long-fk-strategy.yaml`
  - `.windsurf/rules/03-persistence-layer/jpa-relationships-prohibition.yaml`
  - `.windsurf/rules/03-persistence-layer/entity-naming.yaml`
- ✅ Phase 1.1.5 완료: REST API Layer Rules 3개 작성
  - `.windsurf/rules/04-rest-api-layer/controller-naming.yaml`
  - `.windsurf/rules/04-rest-api-layer/request-validation.yaml`
  - `.windsurf/rules/04-rest-api-layer/error-handling.yaml`

### 2025-01-28 (세션 3)
- ✅ Phase 1.2.1 완료: Domain Workflows 4개 작성
  - `.windsurf/workflows/01-domain/create-aggregate.yaml`
  - `.windsurf/workflows/01-domain/create-value-object.yaml`
  - `.windsurf/workflows/01-domain/create-domain-exception.yaml`
  - `.windsurf/workflows/01-domain/create-domain-event.yaml`
- ✅ Phase 1.2.2 완료: Application Workflows 4개 작성 ✅
  - `.windsurf/workflows/02-application/create-usecase.yaml` (초안, 리팩토링 필요: ~6,709 tokens)
  - `.windsurf/workflows/02-application/create-application-service.yaml` (간결 버전: ~595 tokens)
  - `.windsurf/workflows/02-application/create-assembler.yaml` (간결 버전: ~679 tokens)
  - `.windsurf/workflows/02-application/create-outport.yaml` (간결 버전: ~697 tokens)
- 📊 **토큰 최적화 성과**: 간결 버전 3개 평균 657 tokens (목표 2,000 대비 67% 절감, 초기 대비 91% 절감)
- 🔄 다음: Phase 1.2.3 Persistence Workflows 4개 (간결 버전)

---

## 🎯 다음 작업 (다음 세션)

**현재 Phase**: Phase 1.2 (Workflows 디렉토리 생성)
**완료 작업**:
- Phase 1.1 Rules 15/15 (100%) ✅
- Phase 1.2.1 Domain Workflows 4/4 (100%) ✅
- Phase 1.2.2 Application Workflows 4/4 (100%) ✅

**다음 작업 우선순위**:
1. **Phase 1.2.3**: Persistence Workflows 4개 (간결 버전, 목표 ~2,000 tokens/file)
   - create-jpa-entity.yaml
   - create-repository.yaml
   - create-persistence-adapter.yaml
   - create-entity-mapper.yaml
2. **Phase 1.2.4**: REST API Workflows 4개 (간결 버전)
3. **Phase 1.2.5**: Testing Workflows 4개 (간결 버전)
4. **Phase 1.3**: Templates 26개 (간결 버전)
5. **선택적**: create-usecase.yaml 리팩토링 (~6,709 tokens → ~2,000 tokens)

**예상 소요 시간**:
- Persistence Workflows 4개: 2시간 (간결 버전)
- REST API Workflows 4개: 2시간 (간결 버전)
- Testing Workflows 4개: 2시간 (간결 버전)
- Templates 26개: 13시간 (간결 버전)
- **총**: 19시간 (약 2.5일)

**토큰 예상**:
- 현재: ~77K tokens (23 files)
- 완료 후: ~115K tokens (61 files, 간결 버전 적용)
- **Windsurf 제한 (100K) 초과 우려**: create-usecase.yaml 리팩토링 권장

**작업 후 진행률**: 61/61 (100%) - Phase 1 완료

---

**문서 마지막 업데이트**: 2025-01-28 (세션 3 - Phase 1.2.2 완료)
**다음 체크포인트**: Phase 1.2.3 Persistence Workflows 완료 후

## 📝 세션 재개 가이드

### 다음 세션 시작 시 실행할 명령
```bash
# 1. 로드맵 확인
cat docs/workflows/IMPLEMENTATION_ROADMAP.md | grep "🎯 다음 작업"

# 2. 완료된 파일 확인
ls -R .windsurf/workflows/

# 3. 토큰 사용량 확인 (선택적)
wc -c .windsurf/workflows/**/*.yaml

# 4. 다음 작업 시작
# Phase 1.2.3 Persistence Workflows 4개 작성 (간결 버전):
# - create-jpa-entity.yaml (~2,000 tokens 목표)
# - create-repository.yaml (~2,000 tokens 목표)
# - create-persistence-adapter.yaml (~2,000 tokens 목표)
# - create-entity-mapper.yaml (~2,000 tokens 목표)
```

### 작업 재개 체크리스트
- [ ] 이전 세션 작업 로그 확인
- [ ] 완료된 파일 목록 검증
- [ ] 다음 작업 목표 명확화
- [ ] TodoWrite로 작업 추적 시작
