# 문서-코드 갭 분석 리포트

**분석 일시**: 2025-10-10
**대상 커밋**: fd5b2aa (SRP 구현) ~ HEAD (Gemini 리뷰 반영)

---

## 📋 요약

최근 PR #24 및 #25에서 **Single Responsibility Principle (SRP)**과 **Law of Demeter** 강화 작업이 이루어졌으나, 주요 가이드 문서들에는 이러한 변경사항이 **반영되지 않음**.

---

## 🔍 신규 추가된 아키텍처 규칙

### 1. Single Responsibility Principle (SRP) 강화

#### 신규 ArchUnit 테스트: `SingleResponsibilityTest.java`

**레이어별 SRP 기준:**

| 레이어 | 메서드 수 제한 | 필드 수 제한 | 라인 수 제한 | 근거 |
|--------|---------------|-------------|-------------|------|
| Domain | ≤ 7 | ≤ 5 | ≤ 200 | 가장 엄격 (비즈니스 로직 집중) |
| Application | ≤ 5 | - | ≤ 150 | UseCase는 작아야 함 |
| Adapter | ≤ 10 | - | ≤ 300 | 리소스별 분리 |

**새로운 검증 항목:**

1. **Domain Layer**
   - Public 메서드 ≤ 7개
   - Instance 필드 ≤ 5개
   - LCOM (Lack of Cohesion) 측정 → PMD GodClass 룰 활용

2. **Application Layer**
   - UseCase는 Public 메서드 ≤ 5개
   - 단일 @Transactional 메서드 권장 (여러 트랜잭션 = 여러 책임 의심)

3. **Adapter Layer**
   - Controller: 엔드포인트 ≤ 10개 (리소스별 분리)
   - Repository: 단일 Entity만 다뤄야 함 (여러 Entity 의존 금지)

---

### 2. Law of Demeter 강화

#### 신규 ArchUnit 테스트: `LawOfDemeterTest.java`

**핵심 원칙:**
- 객체는 자기 자신, 메서드 파라미터, 생성한 객체, 인스턴스 변수만 접근
- **Train wreck (`obj.getX().getY().getZ()`) 절대 금지**
- **Tell, Don't Ask 원칙 준수**

**허용 패턴:**
- ✅ Builder 패턴 (Fluent API)
- ✅ Stream API
- ✅ StringBuilder

**금지 패턴:**
- ❌ Getter 체이닝
- ❌ 중간 객체 조작
- ❌ JPA 관계 체이닝

**레이어별 Law of Demeter 규칙:**

1. **Domain Layer - 가장 엄격**
   - Domain 객체는 **위임 메서드(delegation methods)** 제공 필수
   - Getter만 제공하는 것이 아닌, 비즈니스 로직을 캡슐화한 메서드 제공
   - PMD의 `DomainLayerDemeterStrict` 룰로 XPath 기반 AST 분석

2. **Persistence Layer - Long FK 전략**
   - ❌ JPA 관계 어노테이션 절대 금지 (`@OneToMany`, `@ManyToOne`, etc.)
   - ✅ Long FK 필드만 사용
   - ❌ Entity에 Setter 메서드 금지 → static factory method 사용

3. **Controller Layer - Record DTO**
   - Request/Response DTO는 **반드시 record 타입**
   - Record는 getter 체이닝 방지 및 불변성 강제
   - Controller는 Repository 직접 접근 금지 → UseCase만 사용

---

## 🛠️ PMD 룰셋 변경사항

### `DomainLayerDemeterStrict` 규칙 강화

**변경 내용:**
```xml
<!-- 변경 전 -->
//PrimaryExpression[count(PrimarySuffix) > 2]

<!-- 변경 후 -->
//PrimaryExpression[count(PrimarySuffix) > 1]
```

**영향:**
- 이전: `obj.getX().getY().getZ()` 금지 (3단계 이상)
- 현재: `obj.getX().getY()` 금지 (2단계 이상)
- **더 엄격한 Demeter 법칙 적용**

---

## 📚 문서 업데이트 필요 항목

### 1. CODING_STANDARDS.md

**누락된 내용:**

#### Domain Layer 섹션
- [ ] SRP 메트릭 추가
  - Public 메서드 ≤ 7개
  - Instance 필드 ≤ 5개
  - LCOM 측정 (PMD GodClass 룰 참조)

- [ ] Law of Demeter 규칙 추가
  - Getter 체이닝 금지
  - Tell, Don't Ask 원칙 설명
  - 위임 메서드 패턴 가이드

#### Application Layer 섹션
- [ ] UseCase SRP 규칙
  - Public 메서드 ≤ 5개
  - 단일 @Transactional 메서드 권장
  - 여러 트랜잭션 메서드 = 여러 책임 경고

#### Adapter Layer 섹션
- [ ] Controller SRP
  - 엔드포인트 ≤ 10개 (리소스별 분리)

- [ ] Repository SRP
  - 단일 Entity 의존 강제
  - 여러 Entity 의존 시 분리 가이드

#### Persistence Adapter 섹션
- [ ] Long FK 전략 상세 설명
  - JPA 관계 어노테이션 금지 근거
  - Long FK 사용 패턴
  - Entity Setter 금지 및 static factory 사용

---

### 2. ENTERPRISE_SPRING_STANDARDS_PROMPT.md

**누락된 규칙 (87개 룰 체계에 추가 필요):**

#### Domain Layer 규칙 추가
- [ ] **D-031**: Domain classes MUST have ≤ 7 public methods (SRP 메트릭)
- [ ] **D-032**: Domain classes MUST have ≤ 5 instance fields (SRP 메트릭)
- [ ] **D-033**: Domain MUST provide delegation methods (Law of Demeter)
- [ ] **D-034**: NO getter chaining in Domain (Law of Demeter)

#### Application Layer 규칙 추가
- [ ] **A-026**: UseCases MUST have ≤ 5 public methods (SRP)
- [ ] **A-027**: UseCases SHOULD have single @Transactional method (SRP)

#### Adapter Layer 규칙 추가
- [ ] **AI-017**: Controllers MUST have ≤ 10 endpoints (SRP)
- [ ] **AO-033**: Repositories MUST focus on single Entity (SRP)
- [ ] **AO-034**: Entities MUST use Long FK, NO JPA relationships (Law of Demeter)

---

### 3. README.md

**업데이트 필요 사항:**

- [ ] 프로젝트 특징 섹션에 SRP/Law of Demeter 강화 언급
- [ ] 아키텍처 테스트 항목에 `SingleResponsibilityTest`, `LawOfDemeterTest` 추가
- [ ] PMD 룰셋 강화 내용 반영

---

### 4. 신규 문서 작성 필요

#### `SRP_ENFORCEMENT_GUIDE.md` (제안)
- SRP 원칙 상세 설명
- 레이어별 SRP 메트릭 및 근거
- 위반 사례 및 리팩토링 가이드
- ArchUnit 테스트 작성 방법

#### `LAW_OF_DEMETER_GUIDE.md` (제안)
- Law of Demeter 원칙 상세 설명
- Getter 체이닝 문제점
- Tell, Don't Ask 패턴
- Long FK 전략 vs JPA 관계
- PMD 룰 활용법

---

## 🎯 우선순위별 액션 플랜

### High Priority (즉시 반영 필요)

1. **CODING_STANDARDS.md 업데이트**
   - Domain Layer에 SRP 메트릭 추가
   - Persistence Adapter에 Long FK 전략 상세화
   - Law of Demeter 규칙 전체 추가

2. **ENTERPRISE_SPRING_STANDARDS_PROMPT.md 업데이트**
   - 87개 → 96개 규칙으로 확장
   - SRP/Law of Demeter 관련 9개 규칙 추가
   - 레벨 및 검증 방법 명시

### Medium Priority (단기 내 반영)

3. **README.md 업데이트**
   - 프로젝트 특징에 SRP/Law of Demeter 강화 언급
   - 아키텍처 테스트 항목 업데이트

4. **신규 가이드 문서 작성**
   - `SRP_ENFORCEMENT_GUIDE.md`
   - `LAW_OF_DEMETER_GUIDE.md`

### Low Priority (향후 개선)

5. **예제 코드 추가**
   - 각 문서에 실제 프로젝트 코드 예제 추가
   - Before/After 리팩토링 사례

6. **동영상/다이어그램**
   - SRP/Law of Demeter 개념 시각화
   - 아키텍처 테스트 실행 데모

---

## 📊 변경 영향도 분석

### 코드 변경 범위
- ✅ **ArchUnit 테스트**: 2개 추가 (`SingleResponsibilityTest`, `LawOfDemeterTest`)
- ✅ **PMD 룰셋**: 1개 규칙 강화 (`DomainLayerDemeterStrict`)
- ❌ **문서**: 업데이트 누락

### 문서-코드 일관성 점수
- **현재**: **60%** (코드 규칙은 반영, 문서는 미반영)
- **목표**: **100%** (문서와 코드 완전 일치)

---

## 🔗 참고 링크

- **관련 PR**:
  - [PR #24: SRP 구현](https://github.com/ryu-qqq/claude-spring-standards/pull/24)
  - [PR #25: Gemini 리뷰 반영](https://github.com/ryu-qqq/claude-spring-standards/pull/25)

- **코드 참조**:
  - `application/src/test/java/com/company/template/architecture/SingleResponsibilityTest.java`
  - `application/src/test/java/com/company/template/architecture/LawOfDemeterTest.java`
  - `config/pmd/pmd-ruleset.xml`

---

## ✅ 다음 단계

1. 본 분석 리포트 검토 및 승인
2. 우선순위별 문서 업데이트 작업 착수
3. 업데이트 완료 후 팀 공유 및 피드백 수렴
4. 정기적인 문서-코드 갭 분석 프로세스 수립

---

**작성자**: Claude Code Analysis
**검토 필요**: Architecture Team
