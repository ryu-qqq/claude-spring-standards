# Windsurf Cascade + Claude Code 통합 전략

## 📋 목차
1. [통합 개요](#통합-개요)
2. [역할 분담 전략](#역할-분담-전략)
3. [Windsurf Cascade Rules 설계](#windsurf-cascade-rules-설계)
4. [Windsurf Cascade Workflows 설계](#windsurf-cascade-workflows-설계)
5. [Claude Code Squad 재정의](#claude-code-squad-재정의)
6. [통합 워크플로우](#통합-워크플로우)
7. [토큰 절약 효과](#토큰-절약-효과)
8. [실행 예시](#실행-예시)

---

## 통합 개요

### 핵심 아이디어
**Windsurf Cascade** (보일러플레이트 생성) + **Claude Code** (비즈니스 로직, 검증, 분석) = **최적의 효율**

### 문제 인식
- **Claude Code Squad만 사용**: 토큰 소모가 너무 크다 (보일러플레이트 생성에 불필요한 토큰 사용)
- **Cascade만 사용**: 비즈니스 로직, 검증, 분석이 부족하다

### 해결 방안
**2-Tier 자동화 시스템**:
```
Tier 1: Windsurf Cascade (빠르고 저렴한 보일러플레이트 생성)
    ↓
Tier 2: Claude Code (고급 비즈니스 로직 구현, 검증, 분석)
```

---

## 역할 분담 전략

### Windsurf Cascade의 역할 (Tier 1)

#### 1. **보일러플레이트 코드 생성** (Rules + Workflows)
**대상**:
- 패키지 구조 생성
- 기본 클래스/인터페이스 골격
- Getter/Setter, equals/hashCode 등 반복 코드
- 기본 생성자, 빌더 패턴 (Pure Java)
- JPA Entity 기본 필드/어노테이션
- REST Controller 엔드포인트 골격
- 기본 Exception 클래스
- 기본 테스트 클래스 골격

**장점**:
- ⚡ **빠른 생성**: 몇 초 내 수십 개 파일 생성
- 💰 **토큰 절약**: LLM 호출 최소화
- 🔄 **일관성**: 템플릿 기반 → 항상 동일한 구조

**예시**:
```java
// Cascade가 생성 (Workflow: create-domain-aggregate)
package com.ryuqq.domain.user;

/**
 * User Aggregate Root
 *
 * @author windsurf-cascade
 * @since 1.0.0
 */
public record UserDomain(
    UserId userId,
    UserEmail email,
    UserProfile profile,
    UserStatus status,
    UserAudit audit
) {
    // Cascade가 기본 골격만 생성
    // 비즈니스 로직은 Claude Code가 추가
}
```

#### 2. **컨벤션 강제 적용** (Rules)
**대상**:
- Lombok 금지 (자동 차단)
- Javadoc 필수 (자동 추가)
- Law of Demeter 위반 방지 (Getter 체이닝 금지)
- Long FK 전략 강제 (JPA 관계 어노테이션 차단)
- 네이밍 규칙 강제 (*UseCase, *QueryService, *OutPort 등)

**장점**:
- 🚫 **사전 차단**: 코드 생성 시점에 위반 방지 (validation-helper.py보다 빠름)
- ✅ **100% 준수**: 규칙 위반 불가능

**예시**:
```yaml
# .windsurf/rules/lombok-prohibition.yaml
- pattern: "@Data|@Builder|@Getter|@Setter|@AllArgsConstructor"
  action: block
  message: "❌ Lombok 사용 금지! Pure Java getter/setter를 작성하세요."
```

---

### Claude Code의 역할 (Tier 2)

#### 1. **비즈니스 로직 구현**
**대상**:
- Domain 객체의 비즈니스 메서드 구현
- Application Service의 트랜잭션 로직
- 복잡한 Query (QueryDSL)
- Domain Event 발행 로직
- Custom Exception 메시지 및 처리 로직

**장점**:
- 🧠 **고급 추론**: 복잡한 비즈니스 규칙 이해
- 📊 **컨텍스트 활용**: PRD/기술 문서 기반 정확한 구현
- 🔍 **의존성 분석**: 다른 레이어와의 연계 이해

**예시**:
```java
// Cascade가 생성한 골격
public record UserDomain(...) {
    // Cascade: 기본 구조만 생성
}

// Claude Code가 추가한 비즈니스 로직
public record UserDomain(...) {
    /**
     * 비밀번호 변경 (비즈니스 규칙 적용)
     *
     * - 현재 비밀번호 검증 필수
     * - 새 비밀번호는 이전과 달라야 함
     * - 최소 8자 이상, 영문/숫자/특수문자 포함
     *
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return 비밀번호가 변경된 User
     * @throws InvalidPasswordException 비밀번호 규칙 위반
     */
    public UserDomain changePassword(String currentPassword, String newPassword) {
        // ✅ Claude Code가 비즈니스 로직 구현
        if (!this.password.matches(currentPassword)) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다");
        }

        if (currentPassword.equals(newPassword)) {
            throw new InvalidPasswordException("새 비밀번호는 이전과 달라야 합니다");
        }

        UserPassword validatedPassword = UserPassword.of(newPassword); // 검증 포함

        return new UserDomain(
            this.userId,
            this.email,
            this.profile,
            this.status,
            this.audit.updateNow(),
            validatedPassword
        );
    }
}
```

#### 2. **검증 및 분석**
**대상**:
- ArchUnit 테스트 실행 및 분석
- 통합 테스트 결과 분석
- 빌드 에러 분석 및 수정
- 코드 리뷰 (PR 분석)
- 성능 병목 분석
- 보안 취약점 분석

**장점**:
- 🔍 **심층 분석**: 단순 패턴 매칭이 아닌 의미론적 분석
- 🛠️ **자동 수정**: 에러 원인 파악 → 자동 수정 제안
- 📊 **리포트 생성**: 분석 결과를 문서화

**예시**:
```bash
# Cascade가 테스트 실행
./gradlew test

# Claude Code가 결과 분석
/analyze-test-results
# 출력:
# ❌ 3개 테스트 실패 발견
# 1. UserDomainTest.testChangePassword()
#    - 원인: InvalidPasswordException 발생하지 않음
#    - 수정: changePassword() 메서드에 검증 로직 추가 필요
# 2. ...
```

#### 3. **Jira 통합 및 문서화**
**대상**:
- Jira Task 분석 및 TodoList 생성
- PR 생성 및 설명 작성
- Jira Task 상태 전환
- 커밋 메시지 생성
- 기술 문서 업데이트

**장점**:
- 📋 **자동 추적**: Jira ↔ Git ↔ PR 완전 연결
- 📝 **고품질 문서**: 컨텍스트 기반 상세 문서 생성

---

## Windsurf Cascade Rules 설계

### Rules 디렉토리 구조
```
.windsurf/
├── rules/
│   ├── 00-global/
│   │   ├── lombok-prohibition.yaml
│   │   ├── javadoc-required.yaml
│   │   └── naming-conventions.yaml
│   │
│   ├── 01-domain-layer/
│   │   ├── law-of-demeter.yaml
│   │   ├── aggregate-rules.yaml
│   │   └── value-object-rules.yaml
│   │
│   ├── 02-application-layer/
│   │   ├── transaction-boundary.yaml
│   │   ├── usecase-naming.yaml
│   │   └── assembler-rules.yaml
│   │
│   ├── 03-persistence-layer/
│   │   ├── long-fk-strategy.yaml
│   │   ├── jpa-relationships-prohibition.yaml
│   │   └── entity-naming.yaml
│   │
│   └── 04-rest-api-layer/
│       ├── controller-naming.yaml
│       ├── request-validation.yaml
│       └── error-handling.yaml
```

### Rules 예시

#### 1. **Global Rule: Lombok 금지**
```yaml
# .windsurf/rules/00-global/lombok-prohibition.yaml
name: "Lombok 사용 금지"
description: "프로젝트 전체에서 Lombok 어노테이션 사용 금지"
layer: "global"
severity: "error"

rules:
  - pattern:
      regex: "@(Data|Builder|Getter|Setter|AllArgsConstructor|NoArgsConstructor|RequiredArgsConstructor|ToString|EqualsAndHashCode)"
      type: "annotation"
    action: "block"
    message: |
      ❌ Lombok 사용 금지!

      대신 Pure Java getter/setter를 작성하세요.

      예시:
      ```java
      public String getName() {
          return name;
      }

      public void setName(String name) {
          this.name = name;
      }
      ```

      참고: docs/coding_convention/02-domain-layer/aggregate-design/03_lombok-prohibition.md

  - pattern:
      import: "lombok.*"
    action: "block"
    message: "Lombok import 금지"
```

#### 2. **Domain Layer Rule: Law of Demeter**
```yaml
# .windsurf/rules/01-domain-layer/law-of-demeter.yaml
name: "Law of Demeter (Getter 체이닝 금지)"
description: "Getter 체이닝을 금지하고 Tell, Don't Ask 원칙 적용"
layer: "domain"
severity: "error"

rules:
  - pattern:
      regex: "\\w+\\.get\\w+\\(\\)\\.get\\w+\\(\\)"
      type: "method_chain"
    action: "block"
    message: |
      ❌ Getter 체이닝 금지!

      나쁜 예:
      ```java
      String zip = order.getCustomer().getAddress().getZipCode();
      ```

      좋은 예:
      ```java
      String zip = order.getCustomerZipCode();  // Tell, Don't Ask
      ```

      참고: docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md

    suggestions:
      - "Aggregate에 직접 메서드 추가: getCustomerZipCode()"
      - "Value Object로 캡슐화: CustomerInfo.zipCode()"
```

#### 3. **Persistence Layer Rule: Long FK 전략**
```yaml
# .windsurf/rules/03-persistence-layer/long-fk-strategy.yaml
name: "Long FK 전략 (JPA 관계 어노테이션 금지)"
description: "JPA Entity에서 관계 어노테이션 사용 금지, Long FK 사용"
layer: "persistence"
severity: "error"

rules:
  - pattern:
      regex: "@(ManyToOne|OneToMany|OneToOne|ManyToMany)"
      type: "annotation"
      location: "*.Entity.java"
    action: "block"
    message: |
      ❌ JPA 관계 어노테이션 사용 금지!

      대신 Long FK를 사용하세요:

      나쁜 예:
      ```java
      @ManyToOne
      @JoinColumn(name = "user_id")
      private User user;
      ```

      좋은 예:
      ```java
      @Column(name = "user_id", nullable = false)
      private Long userId;  // Long FK
      ```

      참고: docs/coding_convention/04-persistence-layer/jpa-entity-design/01_long-fk-strategy.md

    auto_fix:
      - type: "replace"
        from: "@ManyToOne\\s+@JoinColumn\\(name = \"(\\w+)\"\\)\\s+private (\\w+) (\\w+);"
        to: "@Column(name = \"$1\", nullable = false)\\nprivate Long $1;"
```

#### 4. **Application Layer Rule: Transaction 경계**
```yaml
# .windsurf/rules/02-application-layer/transaction-boundary.yaml
name: "Transaction 경계 관리"
description: "@Transactional 내 외부 API 호출 금지"
layer: "application"
severity: "error"

rules:
  - pattern:
      within: "method_with_annotation(@Transactional)"
      contains:
        - "RestTemplate"
        - "WebClient"
        - "HttpClient"
        - "FeignClient"
    action: "block"
    message: |
      ❌ @Transactional 내 외부 API 호출 금지!

      트랜잭션은 짧게 유지해야 합니다.
      외부 API 호출은 트랜잭션 밖에서 수행하세요.

      나쁜 예:
      ```java
      @Transactional
      public void updateUser(Long userId) {
          userRepository.save(user);
          externalApi.sendEmail(user.getEmail());  // ❌
      }
      ```

      좋은 예:
      ```java
      public void updateUser(Long userId) {
          updateUserTransaction(userId);  // @Transactional
          externalApi.sendEmail(user.getEmail());  // 트랜잭션 밖
      }

      @Transactional
      private void updateUserTransaction(Long userId) {
          userRepository.save(user);
      }
      ```

      참고: docs/coding_convention/03-application-layer/transaction-management/01_transaction-boundary.md
```

#### 5. **Global Rule: Javadoc 필수**
```yaml
# .windsurf/rules/00-global/javadoc-required.yaml
name: "Javadoc 필수"
description: "모든 public 클래스/메서드에 Javadoc 필수"
layer: "global"
severity: "warning"

rules:
  - pattern:
      type: "public_class"
      missing: "javadoc"
    action: "suggest"
    message: "public 클래스에 Javadoc을 추가하세요"
    auto_fix:
      - type: "insert_before"
        template: |
          /**
           * {className} - {brief_description}
           *
           * @author {author}
           * @since {version}
           */

  - pattern:
      type: "public_method"
      missing: "javadoc"
    action: "suggest"
    message: "public 메서드에 Javadoc을 추가하세요"
    auto_fix:
      - type: "insert_before"
        template: |
          /**
           * {method_brief}
           *
           * @param {params}
           * @return {return_type}
           */
```

---

## Windsurf Cascade Workflows 설계

### Workflows 디렉토리 구조
```
.windsurf/
├── workflows/
│   ├── 01-domain/
│   │   ├── create-aggregate.yaml
│   │   ├── create-value-object.yaml
│   │   ├── create-domain-exception.yaml
│   │   └── create-domain-event.yaml
│   │
│   ├── 02-application/
│   │   ├── create-usecase.yaml
│   │   ├── create-application-service.yaml
│   │   ├── create-assembler.yaml
│   │   └── create-outport.yaml
│   │
│   ├── 03-persistence/
│   │   ├── create-jpa-entity.yaml
│   │   ├── create-repository.yaml
│   │   ├── create-persistence-adapter.yaml
│   │   └── create-entity-mapper.yaml
│   │
│   ├── 04-rest-api/
│   │   ├── create-controller.yaml
│   │   ├── create-request-dto.yaml
│   │   ├── create-response-dto.yaml
│   │   └── create-error-mapper.yaml
│   │
│   └── 99-testing/
│       ├── create-domain-test.yaml
│       ├── create-application-test.yaml
│       ├── create-persistence-test.yaml
│       └── create-rest-api-test.yaml
```

### Workflows 예시

#### 1. **Domain Workflow: Create Aggregate**
```yaml
# .windsurf/workflows/01-domain/create-aggregate.yaml
name: "Create Domain Aggregate"
description: "Domain Aggregate Root + Value Objects + Exception 생성"
trigger:
  command: "/cascade:create-aggregate"
  parameters:
    - name: "aggregateName"
      type: "string"
      required: true
      description: "Aggregate 이름 (예: User, Order)"

steps:
  - name: "Create Package Structure"
    action: "create_directory"
    path: "domain/src/main/java/com/ryuqq/domain/{{ aggregateName | lower }}"

  - name: "Create Aggregate Root"
    action: "create_file"
    path: "domain/src/main/java/com/ryuqq/domain/{{ aggregateName | lower }}/{{ aggregateName }}Domain.java"
    template: "templates/domain-aggregate.java.j2"
    variables:
      aggregateName: "{{ aggregateName }}"
      packageName: "com.ryuqq.domain.{{ aggregateName | lower }}"

  - name: "Create Value Objects"
    action: "create_files"
    foreach:
      - "{{ aggregateName }}Id"
      - "{{ aggregateName }}Content"
      - "{{ aggregateName }}Status"
      - "{{ aggregateName }}Audit"
    template: "templates/value-object.java.j2"
    path: "domain/src/main/java/com/ryuqq/domain/{{ aggregateName | lower }}/{{ item }}.java"

  - name: "Create Exception Package"
    action: "create_directory"
    path: "domain/src/main/java/com/ryuqq/domain/{{ aggregateName | lower }}/exception"

  - name: "Create Domain Exception"
    action: "create_file"
    path: "domain/src/main/java/com/ryuqq/domain/{{ aggregateName | lower }}/exception/{{ aggregateName }}Exception.java"
    template: "templates/domain-exception.java.j2"

  - name: "Create Specific Exceptions"
    action: "create_files"
    foreach:
      - "{{ aggregateName }}NotFoundException"
      - "{{ aggregateName }}AlreadyExistsException"
    template: "templates/specific-exception.java.j2"
    path: "domain/src/main/java/com/ryuqq/domain/{{ aggregateName | lower }}/exception/{{ item }}.java"

  - name: "Create ErrorCode Enum"
    action: "create_file"
    path: "domain/src/main/java/com/ryuqq/domain/{{ aggregateName | lower }}/{{ aggregateName }}ErrorCode.java"
    template: "templates/error-code-enum.java.j2"

  - name: "Create Test Package"
    action: "create_directory"
    path: "domain/src/test/java/com/ryuqq/domain/{{ aggregateName | lower }}"

  - name: "Create Domain Test"
    action: "create_file"
    path: "domain/src/test/java/com/ryuqq/domain/{{ aggregateName | lower }}/{{ aggregateName }}DomainTest.java"
    template: "templates/domain-test.java.j2"

outputs:
  message: "✅ {{ aggregateName }} Aggregate 생성 완료"
  files_created:
    - "{{ aggregateName }}Domain.java"
    - "{{ aggregateName }}Id.java"
    - "{{ aggregateName }}Content.java"
    - "{{ aggregateName }}Status.java"
    - "{{ aggregateName }}Audit.java"
    - "{{ aggregateName }}Exception.java"
    - "{{ aggregateName }}NotFoundException.java"
    - "{{ aggregateName }}AlreadyExistsException.java"
    - "{{ aggregateName }}ErrorCode.java"
    - "{{ aggregateName }}DomainTest.java"
  next_steps:
    - "비즈니스 로직 구현은 Claude Code에게 요청하세요: /implement-business-logic {{ aggregateName }}"
```

#### 2. **Application Workflow: Create UseCase**
```yaml
# .windsurf/workflows/02-application/create-usecase.yaml
name: "Create Application UseCase"
description: "UseCase 인터페이스 + Service + Assembler + OutPort 생성"
trigger:
  command: "/cascade:create-usecase"
  parameters:
    - name: "useCaseName"
      type: "string"
      required: true
      description: "UseCase 이름 (예: CreateUser, GetUser)"
    - name: "aggregateName"
      type: "string"
      required: true
      description: "관련 Aggregate 이름"

steps:
  - name: "Create UseCase Interface"
    action: "create_file"
    path: "application/src/main/java/com/ryuqq/application/{{ aggregateName | lower }}/port/in/{{ useCaseName }}UseCase.java"
    template: "templates/usecase-interface.java.j2"

  - name: "Create Command/Query DTO"
    action: "create_file"
    path: "application/src/main/java/com/ryuqq/application/{{ aggregateName | lower }}/dto/{{ 'command' if is_command else 'query' }}/{{ useCaseName }}{{ 'Command' if is_command else 'Query' }}.java"
    template: "templates/{{ 'command' if is_command else 'query' }}-dto.java.j2"

  - name: "Create Response DTO"
    action: "create_file"
    path: "application/src/main/java/com/ryuqq/application/{{ aggregateName | lower }}/dto/response/{{ aggregateName }}DetailResponse.java"
    template: "templates/response-dto.java.j2"

  - name: "Create Application Service"
    action: "create_file"
    path: "application/src/main/java/com/ryuqq/application/{{ aggregateName | lower }}/service/{{ useCaseName }}Service.java"
    template: "templates/application-service.java.j2"

  - name: "Create OutPort Interface"
    action: "create_file"
    path: "application/src/main/java/com/ryuqq/application/{{ aggregateName | lower }}/port/out/{{ aggregateName }}{{ 'Command' if is_command else 'Query' }}OutPort.java"
    template: "templates/outport-interface.java.j2"

  - name: "Create or Update Assembler"
    action: "update_or_create_file"
    path: "application/src/main/java/com/ryuqq/application/{{ aggregateName | lower }}/assembler/{{ aggregateName }}Assembler.java"
    template: "templates/assembler.java.j2"
    mode: "append_method"  # 기존 파일이 있으면 메서드만 추가

outputs:
  message: "✅ {{ useCaseName }} UseCase 생성 완료"
  next_steps:
    - "Service 로직 구현은 Claude Code에게 요청하세요: /implement-service {{ useCaseName }}"
    - "Persistence Adapter 생성: /cascade:create-persistence-adapter {{ aggregateName }}"
```

#### 3. **Persistence Workflow: Create JPA Entity**
```yaml
# .windsurf/workflows/03-persistence/create-jpa-entity.yaml
name: "Create JPA Entity"
description: "JPA Entity + Repository + Mapper + Adapter 생성"
trigger:
  command: "/cascade:create-jpa-entity"
  parameters:
    - name: "aggregateName"
      type: "string"
      required: true

steps:
  - name: "Create JPA Entity"
    action: "create_file"
    path: "adapter-out/persistence-mysql/src/main/java/com/ryuqq/adapter/out/persistence/{{ aggregateName | lower }}/entity/{{ aggregateName }}JpaEntity.java"
    template: "templates/jpa-entity.java.j2"
    rules_applied:
      - "long-fk-strategy"  # Long FK 전략 자동 적용
      - "jpa-relationships-prohibition"  # JPA 관계 어노테이션 차단

  - name: "Create Repository Interface"
    action: "create_file"
    path: "adapter-out/persistence-mysql/src/main/java/com/ryuqq/adapter/out/persistence/{{ aggregateName | lower }}/repository/{{ aggregateName }}Repository.java"
    template: "templates/jpa-repository.java.j2"

  - name: "Create Entity Mapper"
    action: "create_file"
    path: "adapter-out/persistence-mysql/src/main/java/com/ryuqq/adapter/out/persistence/{{ aggregateName | lower }}/mapper/{{ aggregateName }}EntityMapper.java"
    template: "templates/entity-mapper.java.j2"

  - name: "Create Persistence Adapter"
    action: "create_file"
    path: "adapter-out/persistence-mysql/src/main/java/com/ryuqq/adapter/out/persistence/{{ aggregateName | lower }}/adapter/{{ aggregateName }}PersistenceAdapter.java"
    template: "templates/persistence-adapter.java.j2"

outputs:
  message: "✅ {{ aggregateName }} JPA Entity 생성 완료"
  next_steps:
    - "QueryDSL 최적화는 Claude Code에게 요청하세요: /optimize-querydsl {{ aggregateName }}"
```

#### 4. **REST API Workflow: Create Controller**
```yaml
# .windsurf/workflows/04-rest-api/create-controller.yaml
name: "Create REST Controller"
description: "REST Controller + Request/Response DTO + Error Mapper 생성"
trigger:
  command: "/cascade:create-controller"
  parameters:
    - name: "aggregateName"
      type: "string"
      required: true
    - name: "endpoints"
      type: "array"
      required: true
      description: "생성할 엔드포인트 목록 (예: [create, get, update, delete])"

steps:
  - name: "Create Controller"
    action: "create_file"
    path: "adapter-in/rest-api/src/main/java/com/ryuqq/adapter/in/rest/{{ aggregateName | lower }}/controller/{{ aggregateName }}Controller.java"
    template: "templates/rest-controller.java.j2"
    variables:
      endpoints: "{{ endpoints }}"

  - name: "Create Request DTOs"
    action: "create_files"
    foreach: "{{ endpoints | filter('is_command') }}"
    path: "adapter-in/rest-api/src/main/java/com/ryuqq/adapter/in/rest/{{ aggregateName | lower }}/dto/request/{{ item | capitalize }}{{ aggregateName }}Request.java"
    template: "templates/request-dto.java.j2"

  - name: "Create Response DTOs"
    action: "create_files"
    foreach:
      - "{{ aggregateName }}DetailApiResponse"
      - "{{ aggregateName }}PageApiResponse"
    path: "adapter-in/rest-api/src/main/java/com/ryuqq/adapter/in/rest/{{ aggregateName | lower }}/dto/response/{{ item }}.java"
    template: "templates/response-dto.java.j2"

  - name: "Create API Mapper"
    action: "create_file"
    path: "adapter-in/rest-api/src/main/java/com/ryuqq/adapter/in/rest/{{ aggregateName | lower }}/mapper/{{ aggregateName }}Mapper.java"
    template: "templates/api-mapper.java.j2"

  - name: "Create Error Mapper"
    action: "create_file"
    path: "adapter-in/rest-api/src/main/java/com/ryuqq/adapter/in/rest/{{ aggregateName | lower }}/error/{{ aggregateName }}ErrorMapping.java"
    template: "templates/error-mapper.java.j2"

outputs:
  message: "✅ {{ aggregateName }} Controller 생성 완료"
  next_steps:
    - "API 테스트는 Claude Code에게 요청하세요: /test-api {{ aggregateName }}"
```

#### 5. **Testing Workflow: Run Tests and Analyze**
```yaml
# .windsurf/workflows/99-testing/run-tests-and-analyze.yaml
name: "Run Tests and Analyze"
description: "테스트 실행 → 결과 분석 → Claude Code가 수정"
trigger:
  command: "/cascade:test"
  parameters:
    - name: "layer"
      type: "string"
      required: true
      options: ["domain", "application", "persistence", "rest-api", "all"]

steps:
  - name: "Run Tests"
    action: "execute_command"
    command: |
      case "{{ layer }}" in
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
        all)
          ./gradlew test
          ;;
      esac
    capture_output: true

  - name: "Analyze Test Results"
    action: "analyze_output"
    input: "{{ previous_step.output }}"
    analysis_type: "test_failures"

  - name: "Generate Analysis Report"
    action: "create_file"
    path: "claudedocs/test-analysis-{{ layer }}-{{ timestamp }}.md"
    template: "templates/test-analysis-report.md.j2"
    variables:
      failures: "{{ analysis_result.failures }}"
      errors: "{{ analysis_result.errors }}"
      suggestions: "{{ analysis_result.suggestions }}"

  - name: "Notify Claude Code"
    action: "trigger_claude_code"
    command: "/analyze-test-results claudedocs/test-analysis-{{ layer }}-{{ timestamp }}.md"
    message: |
      ⚠️ {{ analysis_result.failures | length }}개 테스트 실패 발견

      Claude Code에게 분석을 요청했습니다.
      Claude Code가 자동으로 수정을 제안할 것입니다.

outputs:
  report_path: "claudedocs/test-analysis-{{ layer }}-{{ timestamp }}.md"
  failures_count: "{{ analysis_result.failures | length }}"
  next_steps:
    - "Claude Code가 분석 중입니다..."
```

---

## Claude Code Squad 재정의

### 기존 역할 (Before)
```
Claude Code Squad = 모든 코드 생성 + 검증 + 테스트 + 분석
```
- **문제**: 토큰 소모가 너무 크다 (보일러플레이트 생성에 불필요한 토큰)

### 새로운 역할 (After)
```
Windsurf Cascade = 보일러플레이트 생성 (Tier 1)
    ↓
Claude Code Squad = 비즈니스 로직 + 검증 + 분석 (Tier 2)
```

### Claude Code Squad 새로운 워크플로우

#### Phase 1: Cascade가 보일러플레이트 생성
```bash
# Domain Layer
/cascade:create-aggregate User

# 생성 결과:
# - UserDomain.java (골격)
# - UserId.java (골격)
# - UserEmail.java (골격)
# - UserProfile.java (골격)
# - UserStatus.java (골격)
# - UserAudit.java (골격)
# - UserException.java (골격)
# - UserNotFoundException.java (골격)
# - UserAlreadyExistsException.java (골격)
# - UserErrorCode.java (골격)
# - UserDomainTest.java (골격)
```

#### Phase 2: Claude Code가 비즈니스 로직 구현
```bash
# Jira Task 분석
/jira-task USER-101

# Claude Code가 Cascade 생성 파일을 읽고, 비즈니스 로직만 추가
/implement-business-logic User

# Claude Code 작업:
# 1. UserDomain에 비즈니스 메서드 추가
#    - changePassword()
#    - updateProfile()
#    - deactivate()
# 2. UserEmail에 검증 로직 추가
#    - 이메일 형식 검증
#    - 도메인 화이트리스트 검증
# 3. UserDomainTest에 테스트 케이스 추가
#    - 비즈니스 규칙 검증 테스트
```

#### Phase 3: Claude Code가 검증 및 분석
```bash
# Cascade가 테스트 실행
/cascade:test domain

# Claude Code가 결과 분석
/analyze-test-results
# 출력: 실패한 테스트 분석 + 자동 수정 제안

# Claude Code가 ArchUnit 테스트 실행 및 분석
/validate-architecture domain
```

---

## 통합 워크플로우

### 전체 프로세스

```
Phase 1: 문서 생성 (Claude Code)
    ↓
Phase 2: Jira Epic/Task 생성 (Claude Code)
    ↓
Phase 3: 보일러플레이트 생성 (Windsurf Cascade) ⚡ NEW
    ↓
Phase 4: 비즈니스 로직 구현 (Claude Code) 🔄 UPDATED
    ↓
Phase 5: 검증 및 테스트 (Cascade + Claude Code) 🔄 UPDATED
    ↓
Phase 6: PR 생성 (Claude Code)
```

### 상세 워크플로우

#### **Phase 3: Cascade 보일러플레이트 생성** (NEW)

**실행 스크립트** (`scripts/cascade-generate-boilerplate.sh`)
```bash
#!/bin/bash
# Cascade로 보일러플레이트 자동 생성
# Usage: ./scripts/cascade-generate-boilerplate.sh <task-key> <layer>

set -euo pipefail

TASK_KEY="$1"
LAYER="$2"
AGGREGATE_NAME="$3"  # Jira Task에서 추출

echo "🚀 Cascade: $LAYER Layer 보일러플레이트 생성 중..."

case "$LAYER" in
  domain)
    # Windsurf Cascade Workflow 실행
    windsurf cascade run create-aggregate --aggregateName "$AGGREGATE_NAME"
    ;;
  application)
    windsurf cascade run create-usecase --aggregateName "$AGGREGATE_NAME" --useCaseName "CreateUser"
    windsurf cascade run create-usecase --aggregateName "$AGGREGATE_NAME" --useCaseName "GetUser"
    ;;
  persistence)
    windsurf cascade run create-jpa-entity --aggregateName "$AGGREGATE_NAME"
    ;;
  rest-api)
    windsurf cascade run create-controller --aggregateName "$AGGREGATE_NAME" --endpoints "create,get,update,delete"
    ;;
esac

echo "✅ Cascade: 보일러플레이트 생성 완료"
```

#### **Phase 4: Claude Code 비즈니스 로직 구현** (UPDATED)

**실행 스크립트** (`scripts/claude-implement-business-logic.sh`)
```bash
#!/bin/bash
# Claude Code로 비즈니스 로직 구현
# Usage: ./scripts/claude-implement-business-logic.sh <task-key> <layer>

set -euo pipefail

TASK_KEY="$1"
LAYER="$2"

echo "🤖 Claude Code: $LAYER Layer 비즈니스 로직 구현 중..."

# Claude Code 실행 (Cascade 생성 파일을 읽고, 비즈니스 로직만 추가)
claude code run << EOF
/jira-task $TASK_KEY

# Cascade가 생성한 파일 분석
/analyze-generated-files $LAYER

# 비즈니스 로직 구현 (컨벤션 자동 주입)
/implement-business-logic $LAYER

# 완료
echo "✅ 비즈니스 로직 구현 완료"
EOF
```

#### **Phase 5: 통합 검증** (UPDATED)

**실행 스크립트** (`scripts/integrated-validation.sh`)
```bash
#!/bin/bash
# Cascade + Claude Code 통합 검증
# Usage: ./scripts/integrated-validation.sh <layer>

set -euo pipefail

LAYER="$1"

echo "🔍 통합 검증 시작: $LAYER Layer"

# Step 1: Cascade Rules 검증 (실시간)
echo "📚 Step 1: Cascade Rules 검증..."
windsurf cascade validate --layer "$LAYER"

# Step 2: Cascade Workflows로 테스트 실행
echo "🧪 Step 2: 테스트 실행..."
windsurf cascade run run-tests-and-analyze --layer "$LAYER"

# Step 3: Claude Code가 테스트 결과 분석
echo "🤖 Step 3: Claude Code 테스트 분석..."
claude code run << EOF
/analyze-test-results claudedocs/test-analysis-$LAYER-*.md
EOF

# Step 4: Claude Code가 ArchUnit 테스트 실행 및 분석
echo "🏗️ Step 4: ArchUnit 검증..."
claude code run << EOF
/validate-architecture $LAYER
EOF

echo "✅ 통합 검증 완료"
```

### 통합 오케스트레이터 (`scripts/integrated-squad-start.sh`)

```bash
#!/bin/bash
# Windsurf Cascade + Claude Code Squad 통합 오케스트레이터
# Usage: ./scripts/integrated-squad-start.sh <epic-key>

set -euo pipefail

EPIC_KEY="$1"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SQUAD_LOG_DIR="$PROJECT_ROOT/logs/integrated-squad/$EPIC_KEY"
mkdir -p "$SQUAD_LOG_DIR"

echo "🚀 통합 Squad 시작: Epic $EPIC_KEY"

# Jira Epic의 모든 Task 조회 (기존 코드와 동일)
# ...

# Phase 1: Domain Layer
echo "📦 Phase 1: Domain Layer"
echo "  ⚡ Cascade: 보일러플레이트 생성..."
"$PROJECT_ROOT/scripts/cascade-generate-boilerplate.sh" "$DOMAIN_TASK" "domain" "$AGGREGATE_NAME"

echo "  🤖 Claude Code: 비즈니스 로직 구현..."
"$PROJECT_ROOT/scripts/claude-implement-business-logic.sh" "$DOMAIN_TASK" "domain"

echo "  🔍 통합 검증..."
"$PROJECT_ROOT/scripts/integrated-validation.sh" "domain"

# Phase 2: Application + Persistence (병렬)
echo "📦 Phase 2: Application + Persistence (병렬)"

(
  echo "  ⚡ Cascade: Application 보일러플레이트 생성..."
  "$PROJECT_ROOT/scripts/cascade-generate-boilerplate.sh" "$APP_TASK" "application" "$AGGREGATE_NAME"

  echo "  🤖 Claude Code: Application 비즈니스 로직 구현..."
  "$PROJECT_ROOT/scripts/claude-implement-business-logic.sh" "$APP_TASK" "application"

  echo "  🔍 Application 검증..."
  "$PROJECT_ROOT/scripts/integrated-validation.sh" "application"
) &
APP_PID=$!

(
  echo "  ⚡ Cascade: Persistence 보일러플레이트 생성..."
  "$PROJECT_ROOT/scripts/cascade-generate-boilerplate.sh" "$PERSISTENCE_TASK" "persistence" "$AGGREGATE_NAME"

  echo "  🤖 Claude Code: Persistence 비즈니스 로직 구현..."
  "$PROJECT_ROOT/scripts/claude-implement-business-logic.sh" "$PERSISTENCE_TASK" "persistence"

  echo "  🔍 Persistence 검증..."
  "$PROJECT_ROOT/scripts/integrated-validation.sh" "persistence"
) &
PERSISTENCE_PID=$!

wait $APP_PID $PERSISTENCE_PID

# Phase 3: REST API Layer
echo "📦 Phase 3: REST API Layer"
echo "  ⚡ Cascade: REST API 보일러플레이트 생성..."
"$PROJECT_ROOT/scripts/cascade-generate-boilerplate.sh" "$REST_TASK" "rest-api" "$AGGREGATE_NAME"

echo "  🤖 Claude Code: REST API 비즈니스 로직 구현..."
"$PROJECT_ROOT/scripts/claude-implement-business-logic.sh" "$REST_TASK" "rest-api"

echo "  🔍 REST API 검증..."
"$PROJECT_ROOT/scripts/integrated-validation.sh" "rest-api"

# Phase 4: 통합 테스트
echo "🧪 Phase 4: 통합 테스트"
"$PROJECT_ROOT/scripts/run-integration-tests.sh" "$EPIC_KEY"

echo "🎉 Epic $EPIC_KEY 개발 완료!"
```

---

## 토큰 절약 효과

### 비교 분석

#### **Claude Code만 사용 (기존)**
```
Domain Layer 생성:
- Aggregate Root: 5,000 토큰
- 6개 Value Objects: 6 × 2,000 = 12,000 토큰
- 3개 Exceptions: 3 × 1,500 = 4,500 토큰
- ErrorCode Enum: 2,000 토큰
- Tests: 3,000 토큰
───────────────────────────────
합계: 26,500 토큰
```

#### **Cascade + Claude Code (통합)**
```
Domain Layer 생성:
1. Cascade (보일러플레이트):
   - Aggregate Root 골격: 0 토큰 (템플릿)
   - 6개 Value Objects 골격: 0 토큰 (템플릿)
   - 3개 Exceptions 골격: 0 토큰 (템플릿)
   - ErrorCode Enum 골격: 0 토큰 (템플릿)
   - Tests 골격: 0 토큰 (템플릿)

2. Claude Code (비즈니스 로직만):
   - Aggregate 비즈니스 메서드: 3,000 토큰
   - Value Objects 검증 로직: 2,000 토큰
   - Exception 메시지 커스터마이징: 500 토큰
   - Tests 비즈니스 케이스: 2,000 토큰
───────────────────────────────
합계: 7,500 토큰
```

#### **절약 효과**
- **토큰 절약**: 26,500 → 7,500 = **19,000 토큰 절약 (71.7% 감소)**
- **비용 절약**: $0.265 → $0.075 = **$0.19 절약**
- **속도 향상**: Cascade 템플릿 생성이 훨씬 빠름

### 전체 바운디드 컨텍스트 (4개 Layer)

#### **Claude Code만 사용**
```
Domain: 26,500 토큰
Application: 35,000 토큰
Persistence: 20,000 토큰
REST API: 25,000 토큰
───────────────────────────────
합계: 106,500 토큰 ≈ $1.07
```

#### **Cascade + Claude Code**
```
Domain: 7,500 토큰
Application: 10,000 토큰
Persistence: 5,000 토큰
REST API: 8,000 토큰
───────────────────────────────
합계: 30,500 토큰 ≈ $0.31
```

#### **최종 절약 효과**
- **토큰 절약**: 106,500 → 30,500 = **76,000 토큰 절약 (71.4% 감소)**
- **비용 절약**: $1.07 → $0.31 = **$0.76 절약 (71.0% 감소)**
- **개발 시간**: 2시간 → **1.5시간** (25% 단축, Cascade 템플릿이 즉시 생성)

---

## 실행 예시

### 전체 워크플로우 실행

```bash
# Step 1: PRD 작성 (Claude Code)
/sc:brainstorm "User 관리 바운디드 컨텍스트 PRD 작성"
# 출력: prd/user-management-context.md

# Step 2: Jira Epic/Task 자동 생성 (Claude Code)
/bounded-context-init prd/user-management-context.md
# 출력: Epic (USER-EPIC-1) + 4개 Task

# Step 3: 통합 Squad 시작 (Cascade + Claude Code)
./scripts/integrated-squad-start.sh USER-EPIC-1

# 자동 실행 흐름:
# ┌─────────────────────────────────────────┐
# │ Phase 1: Domain Layer (30분 → 20분)    │
# ├─────────────────────────────────────────┤
# │ ⚡ Cascade: 보일러플레이트 생성 (3분)    │
# │ 🤖 Claude Code: 비즈니스 로직 (15분)    │
# │ 🔍 통합 검증 (2분)                      │
# └─────────────────────────────────────────┘
#             ↓
# ┌─────────────────────────────────────────┐
# │ Phase 2: Application + Persistence (병렬)│
# │        (40분 → 25분)                    │
# ├─────────────────────────────────────────┤
# │ ⚡ Cascade: 보일러플레이트 (각 3분)      │
# │ 🤖 Claude Code: 비즈니스 로직 (각 10분) │
# │ 🔍 통합 검증 (각 2분)                   │
# └─────────────────────────────────────────┘
#             ↓
# ┌─────────────────────────────────────────┐
# │ Phase 3: REST API Layer (20분 → 15분)  │
# ├─────────────────────────────────────────┤
# │ ⚡ Cascade: 보일러플레이트 (3분)         │
# │ 🤖 Claude Code: 비즈니스 로직 (10분)    │
# │ 🔍 통합 검증 (2분)                      │
# └─────────────────────────────────────────┘
#             ↓
# ┌─────────────────────────────────────────┐
# │ Phase 4: 통합 테스트 (10분 → 10분)     │
# └─────────────────────────────────────────┘
#
# 총 시간: 100분 → 70분 → 50분 (50% 단축!)
# 총 비용: $2.00 → $0.60 (70% 절감!)
```

### 수동 개발 (단일 Layer)

```bash
# Step 1: Jira Task 분석 (Claude Code)
/jira-task USER-101

# Step 2: Cascade로 보일러플레이트 생성
/cascade:create-aggregate User
# 출력: 11개 파일 생성 (3초)

# Step 3: Claude Code로 비즈니스 로직 구현
/implement-business-logic User
# 출력: 비즈니스 메서드 추가 완료 (10분)

# Step 4: 통합 검증
/cascade:test domain
# Cascade가 테스트 실행 → Claude Code가 결과 분석

# Step 5: PR 생성 (Claude Code)
/create-pr USER-101
```

---

## 예상 효과 (최종)

### 개발 속도

| 항목 | Claude Code만 | Cascade + Claude Code | 개선율 |
|------|---------------|----------------------|--------|
| Domain Layer | 30분 | **20분** | **33% 단축** |
| Application Layer | 25분 | **15분** | **40% 단축** |
| Persistence Layer | 25분 | **15분** | **40% 단축** |
| REST API Layer | 20분 | **15분** | **25% 단축** |
| **전체** | **100분** | **65분** | **35% 단축** |

### 토큰 사용량 & 비용

| 항목 | Claude Code만 | Cascade + Claude Code | 절약 |
|------|---------------|----------------------|------|
| Domain Layer | 26,500 토큰 | **7,500 토큰** | **71.7% 감소** |
| Application Layer | 35,000 토큰 | **10,000 토큰** | **71.4% 감소** |
| Persistence Layer | 20,000 토큰 | **5,000 토큰** | **75% 감소** |
| REST API Layer | 25,000 토큰 | **8,000 토큰** | **68% 감소** |
| **전체** | **106,500 토큰** | **30,500 토큰** | **71.4% 감소** |
| **비용** | **$1.07** | **$0.31** | **$0.76 절약** |

### 품질

| 항목 | 효과 |
|------|------|
| 컨벤션 준수율 | **100%** (Cascade Rules 강제) |
| 보일러플레이트 일관성 | **100%** (템플릿 기반) |
| 비즈니스 로직 품질 | **높음** (Claude Code 집중) |
| 테스트 커버리지 | **95%+** (자동 생성) |

---

## 다음 단계

### Phase 1: Windsurf Cascade 설정 (1주)
1. ✅ `.windsurf/rules/` 디렉토리 생성 및 90개 규칙 변환
2. ✅ `.windsurf/workflows/` 디렉토리 생성 및 Layer별 Workflows 정의
3. ✅ `.windsurf/templates/` 디렉토리 생성 및 Jinja2 템플릿 작성

### Phase 2: 통합 스크립트 작성 (1주)
1. ✅ `cascade-generate-boilerplate.sh` 구현
2. ✅ `claude-implement-business-logic.sh` 구현
3. ✅ `integrated-validation.sh` 구현
4. ✅ `integrated-squad-start.sh` 통합 오케스트레이터 구현

### Phase 3: Claude Code Slash Commands 업데이트 (3일)
1. ✅ `/implement-business-logic` 명령 추가
2. ✅ `/analyze-test-results` 명령 강화
3. ✅ `/validate-architecture` 명령 강화

### Phase 4: 테스트 및 검증 (1주)
1. ⏳ 실제 바운디드 컨텍스트에 적용
2. ⏳ 토큰 사용량 및 비용 측정
3. ⏳ 품질 지표 측정 (컨벤션 준수율, 테스트 커버리지)

### Phase 5: 프로덕션 적용 (지속적)
1. ⏳ 팀 교육 및 워크플로우 공유
2. ⏳ 피드백 수집 및 개선
3. ⏳ 추가 Layer 및 패턴 지원

---

## 참고 자료

### 관련 문서
- [바운디드 컨텍스트 자동화 워크플로우](./BOUNDED_CONTEXT_AUTOMATION_WORKFLOW.md)
- [Dynamic Hooks Guide](../DYNAMIC_HOOKS_GUIDE.md)
- [Coding Conventions](./coding_convention/)

### 외부 도구
- [Windsurf Cascade](https://docs.windsurf.ai/cascade) (가정)
- [Claude Code](https://claude.ai/code)
- [Jira REST API](https://developer.atlassian.com/cloud/jira/platform/rest/v3/)
