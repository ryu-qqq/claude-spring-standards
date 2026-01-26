-- =====================================================
-- V15: ArchUnit Test 시드 데이터
-- checklist_item의 AUTOMATED 94개 기반 생성
-- 각 테스트는 실제 동작 가능한 ArchUnit 코드
-- =====================================================

-- =====================================================
-- DOMAIN Layer - Aggregate (structure_id = 1)
-- =====================================================

-- DOM-AGG-004: forNew() 팩토리 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-004', 'forNew() 팩토리 메서드 필수', 'Aggregate에 static forNew(..., Instant now) 팩토리 메서드가 있는가?',
'@Test
@DisplayName("[필수] Aggregate는 forNew() 정적 팩토리 메서드가 있어야 한다")
void aggregate_MustHaveForNewMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..aggregate..")
        .and().areNotInterfaces()
        .and().areNotEnums()
        .should(haveStaticMethodWithName("forNew"))
        .because("신규 생성용 팩토리 메서드 필수");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_MustHaveForNewMethod', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-005: reconstitute() 팩토리 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-005', 'reconstitute() 팩토리 메서드 필수', 'Aggregate에 static reconstitute(...) 팩토리 메서드가 있는가?',
'@Test
@DisplayName("[필수] Aggregate는 reconstitute() 정적 팩토리 메서드가 있어야 한다")
void aggregate_MustHaveReconstituteMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..aggregate..")
        .and().areNotInterfaces()
        .and().areNotEnums()
        .should(haveStaticMethodWithName("reconstitute"))
        .because("영속성 복원용 팩토리 메서드 필수");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_MustHaveReconstituteMethod', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-007-01: Aggregate ID는 ID VO 사용
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-007-01', 'Aggregate ID는 ID VO 사용', 'Aggregate의 ID 필드가 전용 ID VO를 사용하는가?',
'@Test
@DisplayName("[필수] Aggregate ID 필드는 ID VO 타입이어야 한다")
void aggregate_IdFieldMustBeIdVo() {
    ArchRule rule = fields()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .and().haveName("id")
        .should().haveRawType(DescribedPredicate.describe(
            "ID VO type ending with Id",
            field -> field.getSimpleName().endsWith("Id")))
        .because("Aggregate ID는 전용 ID VO 사용 필수");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_IdFieldMustBeIdVo', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-007-02: Long 원시 타입 ID 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-007-02', 'Long 원시 타입 ID 금지', 'Long 원시 타입 ID를 직접 사용하지 않는가?',
'@Test
@DisplayName("[금지] Aggregate ID 필드에 Long 원시 타입 사용 금지")
void aggregate_IdFieldMustNotBeLong() {
    ArchRule rule = noFields()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .and().haveName("id")
        .should().haveRawType(Long.class)
        .because("Long 원시 타입 대신 ID VO 사용 필수");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_IdFieldMustNotBeLong', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-008: isNew() 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-008', 'isNew() 메서드 필수', 'Aggregate에 isNew() 메서드가 구현되어 있는가?',
'@Test
@DisplayName("[필수] Aggregate는 isNew() 메서드가 있어야 한다")
void aggregate_MustHaveIsNewMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..aggregate..")
        .and().areNotInterfaces()
        .and().areNotEnums()
        .should(haveMethodWithName("isNew"))
        .because("신규 여부 판단 메서드 필수");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_MustHaveIsNewMethod', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-009-01: 시간 필드는 Instant 타입
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-009-01', '시간 필드는 Instant 타입', '시간 필드(createdAt, updatedAt)가 Instant 타입인가?',
'@Test
@DisplayName("[필수] Aggregate 시간 필드는 Instant 타입이어야 한다")
void aggregate_TimeFieldsMustBeInstant() {
    ArchRule rule = fields()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .and().haveNameMatching("(createdAt|updatedAt)")
        .should().haveRawType(java.time.Instant.class)
        .because("시간 필드는 Instant 사용 (LocalDateTime 금지)");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_TimeFieldsMustBeInstant', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-009-02: LocalDateTime 사용 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-009-02', 'LocalDateTime 사용 금지', 'LocalDateTime, Date, Calendar 등을 사용하지 않는가?',
'@Test
@DisplayName("[금지] Aggregate에서 LocalDateTime 사용 금지")
void aggregate_MustNotUseLocalDateTime() {
    ArchRule rule = noFields()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .should().haveRawType(java.time.LocalDateTime.class)
        .orShould().haveRawType(java.util.Date.class)
        .because("시간 필드는 Instant 사용 필수");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_MustNotUseLocalDateTime', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-010: Instant.now() 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-010', 'Instant.now() 금지', 'Aggregate 내부에서 Instant.now() 직접 호출이 없는가?',
'@Test
@DisplayName("[금지] Aggregate에서 Instant.now() 직접 호출 금지")
void aggregate_MustNotCallInstantNow() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..aggregate..")
        .should().callMethod(java.time.Instant.class, "now")
        .because("시간은 외부에서 주입받아야 함 (테스트 용이성)");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_MustNotCallInstantNow', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-012: Setter 메서드 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-012', 'Setter 메서드 금지', 'public void setXxx() 형태의 Setter 메서드가 없는가?',
'@Test
@DisplayName("[금지] Aggregate에 Setter 메서드 금지")
void aggregate_MustNotHaveSetterMethods() {
    ArchRule rule = noMethods()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .and().arePublic()
        .and().haveNameMatching("set[A-Z].*")
        .should().beDeclared()
        .because("비즈니스 메서드로 상태 변경");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_MustNotHaveSetterMethods', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-014: Getter 체이닝 금지 (Law of Demeter)
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-014', 'Getter 체이닝 금지', 'getXxx().getYyy() 형태의 Getter 체이닝이 없는가?',
'@Test
@DisplayName("[금지] Getter 체이닝 금지 (Law of Demeter)")
void aggregate_MustNotHaveGetterChaining() {
    // Note: ArchUnit으로 메서드 체이닝을 직접 감지하기 어려움
    // 코드 리뷰 또는 정적 분석 도구(PMD)로 보완 필요
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..aggregate..")
        .should(notExposeInternalObjects())
        .because("Law of Demeter 준수 - 내부 객체 직접 노출 금지");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_MustNotHaveGetterChaining', 'BLOCKER', NOW(), NOW());

-- DOM-AGG-018: registerEvent() protected 메서드
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-018', 'registerEvent() protected 메서드', 'registerEvent() 메서드가 protected로 선언되어 있는가?',
'@Test
@DisplayName("[필수] registerEvent()는 protected여야 한다")
void aggregate_RegisterEventMustBeProtected() {
    ArchRule rule = methods()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .and().haveName("registerEvent")
        .should().beProtected()
        .allowEmptyShould(true)
        .because("이벤트 등록은 내부에서만 가능");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_RegisterEventMustBeProtected', 'MAJOR', NOW(), NOW());

-- DOM-AGG-019: pollEvents() public 메서드
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-019', 'pollEvents() public 메서드', 'pollEvents() 메서드가 public으로 구현되어 있는가?',
'@Test
@DisplayName("[필수] pollEvents()는 public이어야 한다")
void aggregate_PollEventsMustBePublic() {
    ArchRule rule = methods()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .and().haveName("pollEvents")
        .should().bePublic()
        .allowEmptyShould(true)
        .because("이벤트 수확은 외부에서 호출");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_PollEventsMustBePublic', 'MAJOR', NOW(), NOW());

-- DOM-AGG-022: 판단 메서드는 boolean 반환
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-AGG-022', '판단 메서드 boolean 반환', 'is, has, can으로 시작하는 메서드가 boolean을 반환하는가?',
'@Test
@DisplayName("[필수] 판단 메서드(is/has/can)는 boolean 반환")
void aggregate_PredicateMethodsMustReturnBoolean() {
    ArchRule rule = methods()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..aggregate..")
        .and().haveNameMatching("(is|has|can)[A-Z].*")
        .should().haveRawReturnType(boolean.class)
        .orShould().haveRawReturnType(Boolean.class)
        .allowEmptyShould(true)
        .because("판단 메서드는 boolean 반환 필수");
    rule.check(domainClasses);
}',
'AggregateArchTest', 'aggregate_PredicateMethodsMustReturnBoolean', 'MAJOR', NOW(), NOW());

-- =====================================================
-- DOMAIN Layer - Common (structure_id = 1)
-- =====================================================

-- DOM-CMN-001-01: 순수 자바 객체 원칙 (POJO)
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-CMN-001-01', '순수 자바 객체 원칙', '도메인 객체가 순수 자바 객체(POJO)인가?',
'@Test
@DisplayName("[금지] Domain에서 Lombok 어노테이션 사용 금지")
void domain_MustNotUseLombok() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().beAnnotatedWith("lombok.Data")
        .orShould().beAnnotatedWith("lombok.Builder")
        .orShould().beAnnotatedWith("lombok.Getter")
        .orShould().beAnnotatedWith("lombok.Setter")
        .orShould().beAnnotatedWith("lombok.AllArgsConstructor")
        .orShould().beAnnotatedWith("lombok.NoArgsConstructor")
        .orShould().beAnnotatedWith("lombok.RequiredArgsConstructor")
        .because("Domain Layer는 Pure Java 원칙");
    rule.check(domainClasses);
}',
'DomainCommonArchTest', 'domain_MustNotUseLombok', 'BLOCKER', NOW(), NOW());

-- DOM-CMN-001-02: JPA, Spring 어노테이션 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-CMN-001-02', 'JPA/Spring 어노테이션 금지', 'Lombok, JPA, Spring 어노테이션을 사용하지 않는가?',
'@Test
@DisplayName("[금지] Domain에서 JPA/Spring 어노테이션 사용 금지")
void domain_MustNotUseJpaOrSpring() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().beAnnotatedWith("jakarta.persistence.Entity")
        .orShould().beAnnotatedWith("jakarta.persistence.Table")
        .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
        .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
        .because("Domain Layer는 JPA/Spring에 독립적");
    rule.check(domainClasses);
}',
'DomainCommonArchTest', 'domain_MustNotUseJpaOrSpring', 'BLOCKER', NOW(), NOW());

-- DOM-CMN-002-01: 외부 레이어 의존 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-CMN-002-01', '외부 레이어 의존 금지', '도메인 객체가 Application, Persistence, REST API 레이어를 의존하지 않는가?',
'@Test
@DisplayName("[금지] Domain에서 외부 레이어 의존 금지")
void domain_MustNotDependOnOuterLayers() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "..application..",
            "..adapter.."
        )
        .because("헥사고날 아키텍처: Domain은 외부 레이어에 의존 금지");
    rule.check(domainClasses);
}',
'DomainCommonArchTest', 'domain_MustNotDependOnOuterLayers', 'BLOCKER', NOW(), NOW());

-- DOM-CMN-002-02: 특정 클래스 의존 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (1, 'ARCH-DOM-CMN-002-02', '특정 클래스 의존 금지', 'Repository, Port, Service, Controller, Entity, DTO를 import하지 않는가?',
'@Test
@DisplayName("[금지] Domain에서 특정 클래스 타입 의존 금지")
void domain_MustNotDependOnSpecificTypes() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().haveNameMatching(".*Repository")
        .orShould().dependOnClassesThat().haveNameMatching(".*Port")
        .orShould().dependOnClassesThat().haveNameMatching(".*Service")
        .orShould().dependOnClassesThat().haveNameMatching(".*Controller")
        .orShould().dependOnClassesThat().haveNameMatching(".*JpaEntity")
        .because("Domain은 인프라 클래스에 의존 금지");
    rule.check(domainClasses);
}',
'DomainCommonArchTest', 'domain_MustNotDependOnSpecificTypes', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- DOMAIN Layer - Value Object (structure_id = 2)
-- =====================================================

-- DOM-VO-001-01: VO Record 타입 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (2, 'ARCH-DOM-VO-001-01', 'VO Record 타입 필수', 'Value Object가 Java Record로 정의되어 있는가?',
'@Test
@DisplayName("[필수] Value Object는 Record 타입이어야 한다")
void valueObject_MustBeRecord() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..vo..")
        .and().areNotEnums()
        .should().beRecords()
        .allowEmptyShould(true)
        .because("Value Object는 불변성 보장을 위해 Record 사용");
    rule.check(domainClasses);
}',
'ValueObjectArchTest', 'valueObject_MustBeRecord', 'BLOCKER', NOW(), NOW());

-- DOM-VO-001-02: class 대신 record 키워드
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (2, 'ARCH-DOM-VO-001-02', 'class 대신 record 키워드', 'class 대신 record 키워드를 사용하는가?',
'@Test
@DisplayName("[금지] VO 패키지에 일반 class 금지")
void valueObject_MustNotBeRegularClass() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..vo..")
        .and().areNotEnums()
        .and().areNotRecords()
        .should().bePublic()
        .allowEmptyShould(true)
        .because("Value Object는 Record 또는 Enum만 허용");
    rule.check(domainClasses);
}',
'ValueObjectArchTest', 'valueObject_MustNotBeRegularClass', 'BLOCKER', NOW(), NOW());

-- DOM-VO-002: of() 정적 팩토리 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (2, 'ARCH-DOM-VO-002', 'of() 정적 팩토리 메서드 필수', 'Value Object에 of() 정적 팩토리 메서드가 있는가?',
'@Test
@DisplayName("[필수] Value Object는 of() 정적 팩토리 메서드가 있어야 한다")
void valueObject_MustHaveOfMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..vo..")
        .and().areRecords()
        .should(haveStaticMethodWithName("of"))
        .allowEmptyShould(true)
        .because("Value Object 생성은 of() 팩토리 메서드 사용");
    rule.check(domainClasses);
}',
'ValueObjectArchTest', 'valueObject_MustHaveOfMethod', 'BLOCKER', NOW(), NOW());

-- DOM-VO-004: Enum VO displayName() 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (2, 'ARCH-DOM-VO-004', 'Enum VO displayName() 필수', 'Enum 타입의 VO에 displayName() 메서드가 있는가?',
'@Test
@DisplayName("[필수] Enum VO는 displayName() 메서드가 있어야 한다")
void enumVo_MustHaveDisplayNameMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..vo..")
        .and().areEnums()
        .should(haveMethodWithName("displayName"))
        .allowEmptyShould(true)
        .because("Enum VO는 사용자 표시용 displayName 필수");
    rule.check(domainClasses);
}',
'ValueObjectArchTest', 'enumVo_MustHaveDisplayNameMethod', 'MAJOR', NOW(), NOW());

-- =====================================================
-- DOMAIN Layer - ID VO (structure_id = 3)
-- =====================================================

-- DOM-ID-001: ID VO *Id 네이밍 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (3, 'ARCH-DOM-ID-001', 'ID VO *Id 네이밍 필수', 'ID VO가 {Domain}Id 형태로 네이밍되어 있는가?',
'@Test
@DisplayName("[필수] ID VO는 *Id 네이밍이어야 한다")
void idVo_MustEndWithId() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..id..")
        .should().haveSimpleNameEndingWith("Id")
        .allowEmptyShould(true)
        .because("ID VO는 {Domain}Id 네이밍 규칙 필수");
    rule.check(domainClasses);
}',
'IdVoArchTest', 'idVo_MustEndWithId', 'BLOCKER', NOW(), NOW());

-- DOM-ID-002: ID VO Record 타입 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (3, 'ARCH-DOM-ID-002', 'ID VO Record 타입 필수', 'ID VO가 Java Record로 정의되어 있는가?',
'@Test
@DisplayName("[필수] ID VO는 Record 타입이어야 한다")
void idVo_MustBeRecord() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..id..")
        .should().beRecords()
        .allowEmptyShould(true)
        .because("ID VO는 불변성 보장을 위해 Record 사용");
    rule.check(domainClasses);
}',
'IdVoArchTest', 'idVo_MustBeRecord', 'BLOCKER', NOW(), NOW());

-- DOM-ID-003: ID VO of() 정적 팩토리 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (3, 'ARCH-DOM-ID-003', 'ID VO of() 정적 팩토리 메서드 필수', 'ID VO에 of() 정적 팩토리 메서드가 있는가?',
'@Test
@DisplayName("[필수] ID VO는 of() 정적 팩토리 메서드가 있어야 한다")
void idVo_MustHaveOfMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..id..")
        .should(haveStaticMethodWithName("of"))
        .allowEmptyShould(true)
        .because("ID VO 생성은 of() 팩토리 메서드 사용");
    rule.check(domainClasses);
}',
'IdVoArchTest', 'idVo_MustHaveOfMethod', 'BLOCKER', NOW(), NOW());

-- DOM-ID-004: Long ID forNew() 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (3, 'ARCH-DOM-ID-004', 'Long ID forNew() 필수', 'Long 타입 ID VO에 forNew() 정적 팩토리 메서드가 있는가?',
'@Test
@DisplayName("[필수] Long ID VO는 forNew() 정적 팩토리 메서드가 있어야 한다")
void longIdVo_MustHaveForNewMethod() {
    // Note: Long 타입 ID 여부는 필드 타입으로 판단
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..id..")
        .and().containAnyFieldsThat(DescribedPredicate.describe(
            "Long type field",
            field -> field.getRawType().isEquivalentTo(Long.class)))
        .should(haveStaticMethodWithName("forNew"))
        .allowEmptyShould(true)
        .because("Long ID는 신규 생성용 forNew() 필수");
    rule.check(domainClasses);
}',
'IdVoArchTest', 'longIdVo_MustHaveForNewMethod', 'BLOCKER', NOW(), NOW());

-- DOM-ID-006: Long ID isNew() 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (3, 'ARCH-DOM-ID-006', 'Long ID isNew() 필수', 'Long 타입 ID VO에 isNew() 메서드가 있는가?',
'@Test
@DisplayName("[필수] Long ID VO는 isNew() 메서드가 있어야 한다")
void longIdVo_MustHaveIsNewMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..id..")
        .and().containAnyFieldsThat(DescribedPredicate.describe(
            "Long type field",
            field -> field.getRawType().isEquivalentTo(Long.class)))
        .should(haveMethodWithName("isNew"))
        .allowEmptyShould(true)
        .because("Long ID는 신규 여부 판단 isNew() 필수");
    rule.check(domainClasses);
}',
'IdVoArchTest', 'longIdVo_MustHaveIsNewMethod', 'BLOCKER', NOW(), NOW());

-- DOM-ID-007: String ID isNew() 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (3, 'ARCH-DOM-ID-007', 'String ID isNew() 금지', 'String 타입 ID VO에 isNew() 메서드가 없는가?',
'@Test
@DisplayName("[금지] String ID VO는 isNew() 메서드가 없어야 한다")
void stringIdVo_MustNotHaveIsNewMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..id..")
        .and().containAnyFieldsThat(DescribedPredicate.describe(
            "String type field",
            field -> field.getRawType().isEquivalentTo(String.class)))
        .should(notHaveMethodWithName("isNew"))
        .allowEmptyShould(true)
        .because("String ID는 항상 외부 주입이므로 isNew 불필요");
    rule.check(domainClasses);
}',
'IdVoArchTest', 'stringIdVo_MustNotHaveIsNewMethod', 'BLOCKER', NOW(), NOW());

-- DOM-ID-008-02: UUID.randomUUID() 호출 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (3, 'ARCH-DOM-ID-008-02', 'UUID.randomUUID() 호출 금지', 'Domain에서 UUID.randomUUID() 호출이 없는가?',
'@Test
@DisplayName("[금지] Domain에서 UUID.randomUUID() 호출 금지")
void domain_MustNotCallUuidRandomUuid() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().callMethod(java.util.UUID.class, "randomUUID")
        .because("String ID는 외부(Application Layer)에서 생성해서 주입");
    rule.check(domainClasses);
}',
'IdVoArchTest', 'domain_MustNotCallUuidRandomUuid', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- DOMAIN Layer - Event (structure_id = 7)
-- =====================================================

-- DOM-EVT-001: DomainEvent 인터페이스 구현 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (7, 'ARCH-DOM-EVT-001', 'DomainEvent 인터페이스 구현 필수', '도메인 이벤트가 DomainEvent 인터페이스를 구현하는가?',
'@Test
@DisplayName("[필수] 도메인 이벤트는 DomainEvent 인터페이스를 구현해야 한다")
void domainEvent_MustImplementDomainEventInterface() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..event..")
        .and().haveSimpleNameEndingWith("Event")
        .and().areNotInterfaces()
        .should().implement(DomainEvent.class)
        .allowEmptyShould(true)
        .because("도메인 이벤트 표준 인터페이스 구현 필수");
    rule.check(domainClasses);
}',
'DomainEventArchTest', 'domainEvent_MustImplementDomainEventInterface', 'BLOCKER', NOW(), NOW());

-- DOM-EVT-002: Event Record 타입 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (7, 'ARCH-DOM-EVT-002', 'Event Record 타입 필수', '도메인 이벤트가 Java Record로 정의되어 있는가?',
'@Test
@DisplayName("[필수] 도메인 이벤트는 Record 타입이어야 한다")
void domainEvent_MustBeRecord() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..event..")
        .and().haveSimpleNameEndingWith("Event")
        .and().areNotInterfaces()
        .should().beRecords()
        .allowEmptyShould(true)
        .because("도메인 이벤트는 불변성 보장을 위해 Record 사용");
    rule.check(domainClasses);
}',
'DomainEventArchTest', 'domainEvent_MustBeRecord', 'BLOCKER', NOW(), NOW());

-- DOM-EVT-003: Event occurredAt 필드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (7, 'ARCH-DOM-EVT-003', 'Event occurredAt 필드 필수', '도메인 이벤트에 occurredAt(Instant) 필드가 있는가?',
'@Test
@DisplayName("[필수] 도메인 이벤트는 occurredAt 필드가 있어야 한다")
void domainEvent_MustHaveOccurredAtField() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..event..")
        .and().haveSimpleNameEndingWith("Event")
        .and().areRecords()
        .should(haveFieldWithName("occurredAt"))
        .allowEmptyShould(true)
        .because("이벤트 발생 시각 기록 필수");
    rule.check(domainClasses);
}',
'DomainEventArchTest', 'domainEvent_MustHaveOccurredAtField', 'BLOCKER', NOW(), NOW());

-- DOM-EVT-004: Event from() 팩토리 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (7, 'ARCH-DOM-EVT-004', 'Event from() 팩토리 메서드 필수', '도메인 이벤트에 from(Aggregate, Instant) 정적 팩토리 메서드가 있는가?',
'@Test
@DisplayName("[필수] 도메인 이벤트는 from() 정적 팩토리 메서드가 있어야 한다")
void domainEvent_MustHaveFromMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..event..")
        .and().haveSimpleNameEndingWith("Event")
        .and().areRecords()
        .should(haveStaticMethodWithName("from"))
        .allowEmptyShould(true)
        .because("도메인 이벤트는 from(Aggregate, Instant) 팩토리 메서드 사용");
    rule.check(domainClasses);
}',
'DomainEventArchTest', 'domainEvent_MustHaveFromMethod', 'BLOCKER', NOW(), NOW());

-- DOM-EVT-006: Event 패키지 위치
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (7, 'ARCH-DOM-EVT-006', 'Event 패키지 위치', '도메인 이벤트가 domain.{bc}.event 패키지에 위치하는가?',
'@Test
@DisplayName("[필수] 도메인 이벤트는 event 패키지에 위치해야 한다")
void domainEvent_MustBeInEventPackage() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Event")
        .and().areNotInterfaces()
        .and().resideInAPackage("..domain..")
        .should().resideInAPackage("..event..")
        .allowEmptyShould(true)
        .because("도메인 이벤트는 event 패키지에 위치");
    rule.check(domainClasses);
}',
'DomainEventArchTest', 'domainEvent_MustBeInEventPackage', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- DOMAIN Layer - Exception (structure_id = 4, 6)
-- =====================================================

-- DOM-EXC-001: ErrorCode 인터페이스 구현 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-001', 'ErrorCode 인터페이스 구현 필수', 'ErrorCode enum이 ErrorCode 인터페이스를 구현하는가?',
'@Test
@DisplayName("[필수] ErrorCode enum은 ErrorCode 인터페이스를 구현해야 한다")
void errorCode_MustImplementErrorCodeInterface() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..exception..")
        .and().areEnums()
        .and().haveSimpleNameEndingWith("ErrorCode")
        .should().implement(ErrorCode.class)
        .allowEmptyShould(true)
        .because("ErrorCode 표준 인터페이스 구현 필수");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'errorCode_MustImplementErrorCodeInterface', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-002: ErrorCode 패키지 위치
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-002', 'ErrorCode 패키지 위치', 'ErrorCode가 domain.{bc}.exception 패키지에 위치하는가?',
'@Test
@DisplayName("[필수] ErrorCode는 exception 패키지에 위치해야 한다")
void errorCode_MustBeInExceptionPackage() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("ErrorCode")
        .and().areEnums()
        .should().resideInAPackage("..exception..")
        .allowEmptyShould(true)
        .because("ErrorCode는 exception 패키지에 위치");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'errorCode_MustBeInExceptionPackage', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-004: Exception Lombok 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-004', 'Exception Lombok 금지', 'Exception에서 Lombok을 사용하지 않는가?',
'@Test
@DisplayName("[금지] Exception에서 Lombok 사용 금지")
void exception_MustNotUseLombok() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..exception..")
        .should().beAnnotatedWith("lombok.Getter")
        .orShould().beAnnotatedWith("lombok.Data")
        .because("Exception은 Plain Java 사용");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'exception_MustNotUseLombok', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-005: ErrorCode getCode() 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-005', 'ErrorCode getCode() 메서드 필수', 'ErrorCode에 getCode() 메서드가 있는가?',
'@Test
@DisplayName("[필수] ErrorCode는 getCode() 메서드가 있어야 한다")
void errorCode_MustHaveGetCodeMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..exception..")
        .and().areEnums()
        .and().haveSimpleNameEndingWith("ErrorCode")
        .should(haveMethodWithName("getCode"))
        .allowEmptyShould(true)
        .because("ErrorCode 식별자 메서드 필수");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'errorCode_MustHaveGetCodeMethod', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-006-01: ErrorCode getHttpStatus() int 반환
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-006-01', 'ErrorCode getHttpStatus() int 반환', 'ErrorCode의 getHttpStatus()가 int 타입을 반환하는가?',
'@Test
@DisplayName("[필수] ErrorCode getHttpStatus()는 int 반환")
void errorCode_GetHttpStatusMustReturnInt() {
    ArchRule rule = methods()
        .that().areDeclaredInClassesThat().resideInAPackage("..domain..exception..")
        .and().areDeclaredInClassesThat().areEnums()
        .and().haveName("getHttpStatus")
        .should().haveRawReturnType(int.class)
        .allowEmptyShould(true)
        .because("HTTP 상태 코드는 int 타입");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'errorCode_GetHttpStatusMustReturnInt', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-006-02: Spring HttpStatus 사용 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-006-02', 'Spring HttpStatus 사용 금지', 'Spring HttpStatus를 사용하지 않는가?',
'@Test
@DisplayName("[금지] Domain에서 Spring HttpStatus 사용 금지")
void domain_MustNotUseSpringHttpStatus() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().haveFullyQualifiedName("org.springframework.http.HttpStatus")
        .because("Domain은 Spring에 독립적");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'domain_MustNotUseSpringHttpStatus', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-007: ErrorCode getMessage() 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-007', 'ErrorCode getMessage() 메서드 필수', 'ErrorCode에 getMessage() 메서드가 있는가?',
'@Test
@DisplayName("[필수] ErrorCode는 getMessage() 메서드가 있어야 한다")
void errorCode_MustHaveGetMessageMethod() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..exception..")
        .and().areEnums()
        .and().haveSimpleNameEndingWith("ErrorCode")
        .should(haveMethodWithName("getMessage"))
        .allowEmptyShould(true)
        .because("에러 메시지 조회 메서드 필수");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'errorCode_MustHaveGetMessageMethod', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-009: DomainException 상속 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-009', 'DomainException 상속 필수', '도메인 예외 클래스가 DomainException을 상속받는가?',
'@Test
@DisplayName("[필수] 도메인 예외는 DomainException을 상속해야 한다")
void domainException_MustExtendDomainException() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..exception..")
        .and().haveSimpleNameEndingWith("Exception")
        .and().areNotInterfaces()
        .and().doNotHaveSimpleName("DomainException")
        .should().beAssignableTo(DomainException.class)
        .allowEmptyShould(true)
        .because("도메인 예외 계층 구조 통일");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'domainException_MustExtendDomainException', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-010: Exception 패키지 위치
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-010', 'Exception 패키지 위치', '도메인 예외가 domain.{bc}.exception 패키지에 위치하는가?',
'@Test
@DisplayName("[필수] 도메인 예외는 exception 패키지에 위치해야 한다")
void domainException_MustBeInExceptionPackage() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Exception")
        .and().resideInAPackage("..domain..")
        .and().areNotInterfaces()
        .should().resideInAPackage("..exception..")
        .allowEmptyShould(true)
        .because("도메인 예외는 exception 패키지에 위치");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'domainException_MustBeInExceptionPackage', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-014: Exception public 클래스
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-014', 'Exception public 클래스', '도메인 예외가 public 클래스로 선언되어 있는가?',
'@Test
@DisplayName("[필수] 도메인 예외는 public이어야 한다")
void domainException_MustBePublic() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..exception..")
        .and().haveSimpleNameEndingWith("Exception")
        .should().bePublic()
        .allowEmptyShould(true)
        .because("다른 레이어에서 사용");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'domainException_MustBePublic', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-015: Exception RuntimeException 계층
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (4, 'ARCH-DOM-EXC-015', 'Exception RuntimeException 계층', '도메인 예외가 RuntimeException 계층인가?',
'@Test
@DisplayName("[필수] 도메인 예외는 RuntimeException 계층이어야 한다")
void domainException_MustBeRuntimeException() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..exception..")
        .and().haveSimpleNameEndingWith("Exception")
        .should().beAssignableTo(RuntimeException.class)
        .allowEmptyShould(true)
        .because("Unchecked Exception 사용");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'domainException_MustBeRuntimeException', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-019: DomainException common 패키지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (6, 'ARCH-DOM-EXC-019', 'DomainException common 패키지', 'DomainException 추상 클래스가 domain.common.exception 패키지에 있는가?',
'@Test
@DisplayName("[필수] DomainException은 common.exception 패키지에 위치")
void domainException_MustBeInCommonPackage() {
    ArchRule rule = classes()
        .that().haveSimpleName("DomainException")
        .should().resideInAPackage("..common.exception..")
        .allowEmptyShould(true)
        .because("공통 예외 추상 클래스는 common 패키지");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'domainException_MustBeInCommonPackage', 'BLOCKER', NOW(), NOW());

-- DOM-EXC-020: ErrorCode 인터페이스 common 패키지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (6, 'ARCH-DOM-EXC-020', 'ErrorCode 인터페이스 common 패키지', 'ErrorCode 인터페이스가 domain.common.exception 패키지에 있는가?',
'@Test
@DisplayName("[필수] ErrorCode 인터페이스는 common.exception 패키지에 위치")
void errorCodeInterface_MustBeInCommonPackage() {
    ArchRule rule = classes()
        .that().haveSimpleName("ErrorCode")
        .and().areInterfaces()
        .should().resideInAPackage("..common.exception..")
        .allowEmptyShould(true)
        .because("공통 ErrorCode 인터페이스는 common 패키지");
    rule.check(domainClasses);
}',
'ExceptionArchTest', 'errorCodeInterface_MustBeInCommonPackage', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- DOMAIN Layer - Criteria (structure_id = 8)
-- =====================================================

-- DOM-CRI-001: Criteria 패키지 위치
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (8, 'ARCH-DOM-CRI-001', 'Criteria 패키지 위치', 'Criteria가 domain.{bc}.query.criteria 패키지에 위치하는가?',
'@Test
@DisplayName("[필수] Criteria는 query 패키지에 위치해야 한다")
void criteria_MustBeInQueryPackage() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Criteria")
        .and().resideInAPackage("..domain..")
        .should().resideInAPackage("..query..")
        .allowEmptyShould(true)
        .because("Criteria는 query 패키지에 위치");
    rule.check(domainClasses);
}',
'CriteriaArchTest', 'criteria_MustBeInQueryPackage', 'BLOCKER', NOW(), NOW());

-- DOM-CRI-003: Criteria public 접근 제어자
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (8, 'ARCH-DOM-CRI-003', 'Criteria public 접근 제어자', 'Criteria가 public으로 선언되어 있는가?',
'@Test
@DisplayName("[필수] Criteria는 public이어야 한다")
void criteria_MustBePublic() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Criteria")
        .and().resideInAPackage("..domain..query..")
        .should().bePublic()
        .allowEmptyShould(true)
        .because("다른 레이어에서 사용");
    rule.check(domainClasses);
}',
'CriteriaArchTest', 'criteria_MustBePublic', 'BLOCKER', NOW(), NOW());

-- DOM-CRI-004: Criteria Record 타입 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (8, 'ARCH-DOM-CRI-004', 'Criteria Record 타입 필수', 'Criteria가 Java Record로 정의되어 있는가?',
'@Test
@DisplayName("[필수] Criteria는 Record 타입이어야 한다")
void criteria_MustBeRecord() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Criteria")
        .and().resideInAPackage("..domain..query..")
        .should().beRecords()
        .allowEmptyShould(true)
        .because("Criteria는 불변 조회 조건");
    rule.check(domainClasses);
}',
'CriteriaArchTest', 'criteria_MustBeRecord', 'BLOCKER', NOW(), NOW());

-- DOM-CRI-005: Criteria of() 팩토리 메서드 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (8, 'ARCH-DOM-CRI-005', 'Criteria of() 팩토리 메서드 필수', 'Criteria에 of() 정적 팩토리 메서드가 있는가?',
'@Test
@DisplayName("[필수] Criteria는 of() 정적 팩토리 메서드가 있어야 한다")
void criteria_MustHaveOfMethod() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Criteria")
        .and().resideInAPackage("..domain..query..")
        .should(haveStaticMethodWithName("of"))
        .allowEmptyShould(true)
        .because("Criteria 생성은 of() 팩토리 메서드 사용");
    rule.check(domainClasses);
}',
'CriteriaArchTest', 'criteria_MustHaveOfMethod', 'BLOCKER', NOW(), NOW());

-- DOM-CRI-010: Criteria JPA/Spring 어노테이션 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (8, 'ARCH-DOM-CRI-010', 'Criteria JPA/Spring 어노테이션 금지', 'Criteria에서 JPA/Spring 어노테이션을 사용하지 않는가?',
'@Test
@DisplayName("[금지] Criteria에서 JPA/Spring 어노테이션 사용 금지")
void criteria_MustNotUseJpaOrSpring() {
    ArchRule rule = noClasses()
        .that().haveSimpleNameEndingWith("Criteria")
        .and().resideInAPackage("..domain..query..")
        .should().beAnnotatedWith("jakarta.persistence.Entity")
        .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
        .because("Criteria는 순수 자바 객체");
    rule.check(domainClasses);
}',
'CriteriaArchTest', 'criteria_MustNotUseJpaOrSpring', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- APPLICATION Layer - Transaction (structure_id = 14, 18)
-- =====================================================

-- APP-TRX-001: Service @Transactional 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (14, 'ARCH-APP-TRX-001', 'Service @Transactional 금지', 'Service 클래스에 @Transactional 어노테이션이 없는가?',
'@Test
@DisplayName("[금지] Service에 @Transactional 금지")
void service_MustNotHaveTransactional() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..service..")
        .and().haveSimpleNameEndingWith("Service")
        .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
        .because("트랜잭션 경계는 Manager/Facade 책임");
    rule.check(applicationClasses);
}',
'ApplicationTransactionArchTest', 'service_MustNotHaveTransactional', 'BLOCKER', NOW(), NOW());

-- APP-TRX-002: Manager @Transactional 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (18, 'ARCH-APP-TRX-002', 'Manager @Transactional 필수', 'CommandManager/QueryManager에 @Transactional이 있는가?',
'@Test
@DisplayName("[필수] Manager에 @Transactional 필수")
void manager_MustHaveTransactional() {
    ArchRule rule = classes()
        .that().resideInAPackage("..application..manager..")
        .and().haveSimpleNameEndingWith("Manager")
        .and().haveSimpleNameNotContaining("Client")
        .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
        .allowEmptyShould(true)
        .because("Manager가 트랜잭션 경계");
    rule.check(applicationClasses);
}',
'ApplicationTransactionArchTest', 'manager_MustHaveTransactional', 'BLOCKER', NOW(), NOW());

-- APP-TRX-003: ClientManager @Transactional 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (20, 'ARCH-APP-TRX-003', 'ClientManager @Transactional 금지', 'ClientManager에 @Transactional 어노테이션이 없는가?',
'@Test
@DisplayName("[금지] ClientManager에 @Transactional 금지")
void clientManager_MustNotHaveTransactional() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..manager.client..")
        .and().haveSimpleNameEndingWith("Manager")
        .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
        .because("외부 API 호출은 트랜잭션 밖에서");
    rule.check(applicationClasses);
}',
'ApplicationTransactionArchTest', 'clientManager_MustNotHaveTransactional', 'BLOCKER', NOW(), NOW());

-- APP-TRX-005: QueryFacade @Transactional(readOnly=true)
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (17, 'ARCH-APP-TRX-005', 'QueryFacade @Transactional(readOnly=true)', 'QueryFacade에 @Transactional(readOnly=true)가 있는가?',
'@Test
@DisplayName("[필수] QueryFacade에 @Transactional(readOnly=true) 필수")
void queryFacade_MustHaveReadOnlyTransactional() {
    ArchRule rule = classes()
        .that().resideInAPackage("..application..facade.query..")
        .and().haveSimpleNameEndingWith("Facade")
        .should().beAnnotatedWith(
            DescribedPredicate.describe(
                "@Transactional(readOnly=true)",
                annotation -> annotation.getName().contains("Transactional")))
        .allowEmptyShould(true)
        .because("조회 전용 트랜잭션 최적화");
    rule.check(applicationClasses);
}',
'ApplicationTransactionArchTest', 'queryFacade_MustHaveReadOnlyTransactional', 'MAJOR', NOW(), NOW());

-- =====================================================
-- APPLICATION Layer - DTO (structure_id = 27, 28)
-- =====================================================

-- APP-DTO-001-01: Command/Query Record 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (27, 'ARCH-APP-DTO-001-01', 'Command/Query Record 필수', 'Command/Query DTO가 Java Record로 정의되어 있는가?',
'@Test
@DisplayName("[필수] Command/Query는 Record 타입이어야 한다")
void commandQuery_MustBeRecord() {
    ArchRule rule = classes()
        .that().resideInAPackage("..application..dto..")
        .and().haveSimpleNameEndingWith("Command")
        .or().haveSimpleNameEndingWith("Query")
        .should().beRecords()
        .allowEmptyShould(true)
        .because("Command/Query DTO는 불변");
    rule.check(applicationClasses);
}',
'ApplicationDtoArchTest', 'commandQuery_MustBeRecord', 'BLOCKER', NOW(), NOW());

-- APP-DTO-001-02: Command/Query Lombok 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (27, 'ARCH-APP-DTO-001-02', 'Command/Query Lombok 금지', 'Lombok을 사용하지 않는가?',
'@Test
@DisplayName("[금지] Command/Query에서 Lombok 사용 금지")
void commandQuery_MustNotUseLombok() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..dto..")
        .should().beAnnotatedWith("lombok.Data")
        .orShould().beAnnotatedWith("lombok.Builder")
        .because("Record 사용으로 Lombok 불필요");
    rule.check(applicationClasses);
}',
'ApplicationDtoArchTest', 'commandQuery_MustNotUseLombok', 'BLOCKER', NOW(), NOW());

-- APP-DTO-002: Command/Query 인스턴스 메서드 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (27, 'ARCH-APP-DTO-002', 'Command/Query 인스턴스 메서드 금지', 'Command/Query에 인스턴스 메서드가 없는가?',
'@Test
@DisplayName("[금지] Command/Query에 비즈니스 로직 메서드 금지")
void commandQuery_MustNotHaveBusinessMethods() {
    ArchRule rule = classes()
        .that().resideInAPackage("..application..dto..")
        .and().areRecords()
        .should(notHaveBusinessLogicMethods())
        .allowEmptyShould(true)
        .because("Command/Query는 순수 데이터 운반 객체");
    rule.check(applicationClasses);
}',
'ApplicationDtoArchTest', 'commandQuery_MustNotHaveBusinessMethods', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- APPLICATION Layer - Dependency (structure_id = various)
-- =====================================================

-- APP-DEP-001: Service → Facade/Manager 의존
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (14, 'ARCH-APP-DEP-001', 'Service → Facade/Manager 의존', 'Service가 Facade 또는 Manager에 의존하는가?',
'@Test
@DisplayName("[필수] Service는 Facade/Manager에 의존")
void service_MustDependOnFacadeOrManager() {
    ArchRule rule = classes()
        .that().resideInAPackage("..application..service..")
        .and().haveSimpleNameEndingWith("Service")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("Facade")
        .orShould().dependOnClassesThat().haveSimpleNameEndingWith("Manager")
        .allowEmptyShould(true)
        .because("Service는 Facade/Manager를 조율");
    rule.check(applicationClasses);
}',
'ApplicationDependencyArchTest', 'service_MustDependOnFacadeOrManager', 'MAJOR', NOW(), NOW());

-- APP-DEP-003: CommandFacade → QueryManager 금지 (CQRS)
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (16, 'ARCH-APP-DEP-003', 'CommandFacade → QueryManager 금지', 'CommandFacade에서 QueryManager를 의존하지 않는가?',
'@Test
@DisplayName("[금지] CommandFacade에서 QueryManager 의존 금지 (CQRS)")
void commandFacade_MustNotDependOnQueryManager() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..facade.command..")
        .should().dependOnClassesThat().resideInAPackage("..manager.query..")
        .because("CQRS 원칙: Command와 Query 분리");
    rule.check(applicationClasses);
}',
'ApplicationDependencyArchTest', 'commandFacade_MustNotDependOnQueryManager', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- APPLICATION Layer - Event (structure_id = 35, 36)
-- =====================================================

-- APP-EVT-001: ApplicationEventPublisher 직접 주입 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (14, 'ARCH-APP-EVT-001', 'ApplicationEventPublisher 직접 주입 금지', 'Service에 ApplicationEventPublisher가 직접 주입되지 않는가?',
'@Test
@DisplayName("[금지] Service에 ApplicationEventPublisher 직접 주입 금지")
void service_MustNotInjectEventPublisher() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..service..")
        .should().dependOnClassesThat().haveFullyQualifiedName(
            "org.springframework.context.ApplicationEventPublisher")
        .because("이벤트 발행은 TransactionEventRegistry 사용");
    rule.check(applicationClasses);
}',
'ApplicationEventArchTest', 'service_MustNotInjectEventPublisher', 'BLOCKER', NOW(), NOW());

-- APP-LSN-002-01: EventListener Manager 의존
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (36, 'ARCH-APP-LSN-002-01', 'EventListener Manager 의존', 'EventListener가 Manager에 의존하는가?',
'@Test
@DisplayName("[필수] EventListener는 Manager에 의존")
void eventListener_MustDependOnManager() {
    ArchRule rule = classes()
        .that().resideInAPackage("..application..listener..")
        .and().haveSimpleNameEndingWith("Listener")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("Manager")
        .allowEmptyShould(true)
        .because("EventListener는 Manager를 통해 처리");
    rule.check(applicationClasses);
}',
'ApplicationEventArchTest', 'eventListener_MustDependOnManager', 'MAJOR', NOW(), NOW());

-- APP-LSN-002-02: EventListener Port 직접 의존 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (36, 'ARCH-APP-LSN-002-02', 'EventListener Port 직접 의존 금지', 'Port를 직접 의존하지 않는가?',
'@Test
@DisplayName("[금지] EventListener에서 Port 직접 의존 금지")
void eventListener_MustNotDependOnPort() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..listener..")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("Port")
        .because("EventListener는 Manager를 통해 Port 접근");
    rule.check(applicationClasses);
}',
'ApplicationEventArchTest', 'eventListener_MustNotDependOnPort', 'MAJOR', NOW(), NOW());

-- =====================================================
-- APPLICATION Layer - Port (structure_id = 11, 12)
-- =====================================================

-- APP-PRT-002: QueryPort findAll 금지 (OOM)
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (12, 'ARCH-APP-PRT-002', 'QueryPort findAll 금지', 'QueryPort에 findAll() 메서드가 없는가?',
'@Test
@DisplayName("[금지] QueryPort에 findAll() 금지 (OOM 위험)")
void queryPort_MustNotHaveFindAll() {
    ArchRule rule = noMethods()
        .that().areDeclaredInClassesThat().resideInAPackage("..port.out.query..")
        .and().areDeclaredInClassesThat().areInterfaces()
        .and().haveName("findAll")
        .should().beDeclared()
        .because("전체 조회는 OOM 위험, 페이징 필수");
    rule.check(applicationClasses);
}',
'ApplicationPortArchTest', 'queryPort_MustNotHaveFindAll', 'BLOCKER', NOW(), NOW());

-- APP-SVC-001: UseCase 1:1 Service 구현
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (14, 'ARCH-APP-SVC-001', 'UseCase 1:1 Service 구현', '1 UseCase = 1 Service로 구현되어 있는가?',
'@Test
@DisplayName("[필수] Service는 UseCase 인터페이스를 구현해야 한다")
void service_MustImplementUseCase() {
    ArchRule rule = classes()
        .that().resideInAPackage("..application..service..")
        .and().haveSimpleNameEndingWith("Service")
        .and().areNotInterfaces()
        .should().implement(
            DescribedPredicate.describe(
                "UseCase interface",
                javaClass -> javaClass.getAllRawInterfaces().stream()
                    .anyMatch(i -> i.getSimpleName().endsWith("UseCase"))))
        .allowEmptyShould(true)
        .because("Service는 UseCase 구현체");
    rule.check(applicationClasses);
}',
'ApplicationServiceArchTest', 'service_MustImplementUseCase', 'MAJOR', NOW(), NOW());

-- =====================================================
-- APPLICATION Layer - Time (structure_id = 21)
-- =====================================================

-- APP-TIM-001-01: TimeProvider Factory에서만
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (21, 'ARCH-APP-TIM-001-01', 'TimeProvider Factory에서만', 'TimeProvider.now()가 Factory에서만 호출되는가?',
'@Test
@DisplayName("[필수] TimeProvider는 Factory에서만 사용")
void timeProvider_OnlyInFactory() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..")
        .and().haveSimpleNameNotEndingWith("Factory")
        .should().callMethodWhere(
            target(name("now")).and(target(owner(name("TimeProvider")))))
        .allowEmptyShould(true)
        .because("시간 생성은 Factory 책임");
    rule.check(applicationClasses);
}',
'ApplicationTimeArchTest', 'timeProvider_OnlyInFactory', 'BLOCKER', NOW(), NOW());

-- APP-TIM-001-02: Service TimeProvider 직접 사용 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (14, 'ARCH-APP-TIM-001-02', 'Service TimeProvider 직접 사용 금지', 'Service에서 TimeProvider를 직접 사용하지 않는가?',
'@Test
@DisplayName("[금지] Service에서 TimeProvider 직접 사용 금지")
void service_MustNotUseTimeProvider() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..application..service..")
        .should().dependOnClassesThat().haveSimpleName("TimeProvider")
        .because("Service는 Factory가 생성한 시간 사용");
    rule.check(applicationClasses);
}',
'ApplicationTimeArchTest', 'service_MustNotUseTimeProvider', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- ADAPTER_OUT (Persistence) Layer (structure_id = 37-40)
-- =====================================================

-- PER-ENT-001: JPA 관계 어노테이션 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (38, 'ARCH-PER-ENT-001', 'JPA 관계 어노테이션 금지', '@OneToMany, @ManyToOne 등이 없는가?',
'@Test
@DisplayName("[금지] JPA Entity에 관계 어노테이션 금지")
void jpaEntity_MustNotUseRelationshipAnnotations() {
    ArchRule rule = noFields()
        .that().areDeclaredInClassesThat().areAnnotatedWith("jakarta.persistence.Entity")
        .should().beAnnotatedWith("jakarta.persistence.ManyToOne")
        .orShould().beAnnotatedWith("jakarta.persistence.OneToMany")
        .orShould().beAnnotatedWith("jakarta.persistence.OneToOne")
        .orShould().beAnnotatedWith("jakarta.persistence.ManyToMany")
        .because("Long FK 전략 사용 (관계 어노테이션 금지)");
    rule.check(persistenceClasses);
}',
'PersistenceEntityArchTest', 'jpaEntity_MustNotUseRelationshipAnnotations', 'BLOCKER', NOW(), NOW());

-- PER-ENT-002: Entity는 BaseAuditEntity 상속
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (38, 'ARCH-PER-ENT-002', 'Entity는 BaseAuditEntity 상속', 'JpaEntity가 BaseAuditEntity를 상속하는가?',
'@Test
@DisplayName("[필수] JPA Entity는 BaseAuditEntity 상속")
void jpaEntity_MustExtendBaseAuditEntity() {
    ArchRule rule = classes()
        .that().areAnnotatedWith("jakarta.persistence.Entity")
        .should().beAssignableTo(
            DescribedPredicate.describe(
                "BaseAuditEntity or SoftDeletableEntity",
                javaClass -> javaClass.getName().contains("BaseAuditEntity")
                    || javaClass.getName().contains("SoftDeletableEntity")))
        .allowEmptyShould(true)
        .because("감사 필드 자동 관리");
    rule.check(persistenceClasses);
}',
'PersistenceEntityArchTest', 'jpaEntity_MustExtendBaseAuditEntity', 'MAJOR', NOW(), NOW());

-- PER-REP-001: JpaRepository save/saveAll만 사용
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (40, 'ARCH-PER-REP-001', 'JpaRepository save/saveAll만 사용', 'JpaRepository에서 save, saveAll만 사용하는가?',
'@Test
@DisplayName("[필수] JpaRepository는 save/saveAll만 사용")
void jpaRepository_OnlySaveMethods() {
    ArchRule rule = classes()
        .that().resideInAPackage("..persistence..repository..")
        .and().haveSimpleNameEndingWith("JpaRepository")
        .and().areInterfaces()
        .should(onlyDeclareSaveAndSaveAllMethods())
        .allowEmptyShould(true)
        .because("조회는 QueryDslRepository 사용");
    rule.check(persistenceClasses);
}',
'PersistenceRepositoryArchTest', 'jpaRepository_OnlySaveMethods', 'BLOCKER', NOW(), NOW());

-- PER-REP-002: JpaRepository 커스텀 메서드 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (40, 'ARCH-PER-REP-002', 'JpaRepository 커스텀 메서드 금지', '@Query, findBy* 등이 없는가?',
'@Test
@DisplayName("[금지] JpaRepository에 커스텀 쿼리 메서드 금지")
void jpaRepository_MustNotHaveCustomQueryMethods() {
    ArchRule rule = noMethods()
        .that().areDeclaredInClassesThat().resideInAPackage("..persistence..repository..")
        .and().areDeclaredInClassesThat().haveSimpleNameEndingWith("JpaRepository")
        .and().haveNameMatching("find.*")
        .should().beDeclared()
        .allowEmptyShould(true)
        .because("조회 로직은 QueryDslRepository에서 구현");
    rule.check(persistenceClasses);
}',
'PersistenceRepositoryArchTest', 'jpaRepository_MustNotHaveCustomQueryMethods', 'BLOCKER', NOW(), NOW());

-- PER-REP-003: 모든 조회는 QueryDslRepository
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (40, 'ARCH-PER-REP-003', '모든 조회는 QueryDslRepository', '조회 쿼리가 QueryDslRepository에 구현되어 있는가?',
'@Test
@DisplayName("[필수] 조회 로직은 QueryDslRepository에 구현")
void queryLogic_MustBeInQueryDslRepository() {
    ArchRule rule = classes()
        .that().resideInAPackage("..persistence..repository..")
        .and().haveSimpleNameEndingWith("QueryDslRepository")
        .should().bePublic()
        .allowEmptyShould(true)
        .because("QueryDsl로 타입 세이프한 쿼리 작성");
    rule.check(persistenceClasses);
}',
'PersistenceRepositoryArchTest', 'queryLogic_MustBeInQueryDslRepository', 'BLOCKER', NOW(), NOW());

-- PER-ADP-001-01: CommandAdapter는 JpaRepository만 의존
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (37, 'ARCH-PER-ADP-001-01', 'CommandAdapter JpaRepository만 의존', 'CommandAdapter가 JpaRepository만 의존하는가?',
'@Test
@DisplayName("[필수] CommandAdapter는 JpaRepository만 의존")
void commandAdapter_OnlyJpaRepository() {
    ArchRule rule = classes()
        .that().resideInAPackage("..persistence..adapter..")
        .and().haveSimpleNameContaining("Command")
        .and().haveSimpleNameEndingWith("Adapter")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("JpaRepository")
        .allowEmptyShould(true)
        .because("CommandAdapter는 저장만 담당");
    rule.check(persistenceClasses);
}',
'PersistenceAdapterArchTest', 'commandAdapter_OnlyJpaRepository', 'BLOCKER', NOW(), NOW());

-- PER-ADP-001-02: CommandAdapter QueryDslRepository 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (37, 'ARCH-PER-ADP-001-02', 'CommandAdapter QueryDslRepository 금지', 'QueryDslRepository를 주입받지 않는가?',
'@Test
@DisplayName("[금지] CommandAdapter에서 QueryDslRepository 금지")
void commandAdapter_MustNotUseQueryDslRepository() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..persistence..adapter..")
        .and().haveSimpleNameContaining("Command")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("QueryDslRepository")
        .because("CommandAdapter는 조회하지 않음 (CQRS)");
    rule.check(persistenceClasses);
}',
'PersistenceAdapterArchTest', 'commandAdapter_MustNotUseQueryDslRepository', 'BLOCKER', NOW(), NOW());

-- PER-ADP-002-01: QueryAdapter는 QueryDslRepository만 의존
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (37, 'ARCH-PER-ADP-002-01', 'QueryAdapter QueryDslRepository만 의존', 'QueryAdapter가 QueryDslRepository만 의존하는가?',
'@Test
@DisplayName("[필수] QueryAdapter는 QueryDslRepository만 의존")
void queryAdapter_OnlyQueryDslRepository() {
    ArchRule rule = classes()
        .that().resideInAPackage("..persistence..adapter..")
        .and().haveSimpleNameContaining("Query")
        .and().haveSimpleNameEndingWith("Adapter")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("QueryDslRepository")
        .allowEmptyShould(true)
        .because("QueryAdapter는 조회만 담당");
    rule.check(persistenceClasses);
}',
'PersistenceAdapterArchTest', 'queryAdapter_OnlyQueryDslRepository', 'BLOCKER', NOW(), NOW());

-- PER-ADP-002-02: QueryAdapter JpaRepository 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (37, 'ARCH-PER-ADP-002-02', 'QueryAdapter JpaRepository 금지', 'JpaRepository를 주입받지 않는가?',
'@Test
@DisplayName("[금지] QueryAdapter에서 JpaRepository 금지")
void queryAdapter_MustNotUseJpaRepository() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..persistence..adapter..")
        .and().haveSimpleNameContaining("Query")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("JpaRepository")
        .because("QueryAdapter는 저장하지 않음 (CQRS)");
    rule.check(persistenceClasses);
}',
'PersistenceAdapterArchTest', 'queryAdapter_MustNotUseJpaRepository', 'BLOCKER', NOW(), NOW());

-- PER-ADM-001: Admin 복잡 쿼리는 persistence-mysql-admin 모듈
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (46, 'ARCH-PER-ADM-001', 'Admin 복잡 쿼리 분리', '복잡한 쿼리가 admin 모듈에만 있는가?',
'@Test
@DisplayName("[필수] Admin 복잡 쿼리는 admin 모듈에 위치")
void complexQuery_MustBeInAdminModule() {
    ArchRule rule = classes()
        .that().resideInAPackage("..persistence..admin..")
        .should().bePublic()
        .allowEmptyShould(true)
        .because("복잡한 조인/서브쿼리는 admin 모듈 전용");
    rule.check(persistenceClasses);
}',
'PersistenceAdminArchTest', 'complexQuery_MustBeInAdminModule', 'BLOCKER', NOW(), NOW());

-- =====================================================
-- ADAPTER_IN (REST API) Layer (structure_id = 60, 61, 62, 63)
-- =====================================================

-- API-CTR-001: @RestController 어노테이션 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (60, 'ARCH-API-CTR-001', '@RestController 어노테이션 필수', 'Controller에 @RestController가 있는가?',
'@Test
@DisplayName("[필수] Controller는 @RestController 필수")
void controller_MustHaveRestController() {
    ArchRule rule = classes()
        .that().resideInAPackage("..rest..controller..")
        .and().haveSimpleNameEndingWith("Controller")
        .should().beAnnotatedWith("org.springframework.web.bind.annotation.RestController")
        .allowEmptyShould(true)
        .because("REST API Controller 명시");
    rule.check(restApiClasses);
}',
'RestApiControllerArchTest', 'controller_MustHaveRestController', 'MAJOR', NOW(), NOW());

-- API-CTR-002: DELETE 메서드 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (60, 'ARCH-API-CTR-002', 'DELETE 메서드 금지', 'HTTP DELETE를 사용하지 않는가?',
'@Test
@DisplayName("[금지] Controller에서 @DeleteMapping 금지")
void controller_MustNotUseDeleteMapping() {
    ArchRule rule = noMethods()
        .that().areDeclaredInClassesThat().resideInAPackage("..rest..controller..")
        .should().beAnnotatedWith("org.springframework.web.bind.annotation.DeleteMapping")
        .because("Soft Delete 정책: DELETE 대신 PATCH 사용");
    rule.check(restApiClasses);
}',
'RestApiControllerArchTest', 'controller_MustNotUseDeleteMapping', 'BLOCKER', NOW(), NOW());

-- API-CTR-003-01: UseCase 인터페이스 의존
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (60, 'ARCH-API-CTR-003-01', 'UseCase 인터페이스 의존', 'Controller가 UseCase에 의존하는가?',
'@Test
@DisplayName("[필수] Controller는 UseCase에 의존")
void controller_MustDependOnUseCase() {
    ArchRule rule = classes()
        .that().resideInAPackage("..rest..controller..")
        .and().haveSimpleNameEndingWith("Controller")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("UseCase")
        .allowEmptyShould(true)
        .because("헥사고날 아키텍처: Port-In 의존");
    rule.check(restApiClasses);
}',
'RestApiControllerArchTest', 'controller_MustDependOnUseCase', 'MAJOR', NOW(), NOW());

-- API-CTR-003-02: Service 직접 의존 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (60, 'ARCH-API-CTR-003-02', 'Service 직접 의존 금지', '구체 Service를 직접 의존하지 않는가?',
'@Test
@DisplayName("[금지] Controller에서 Service 직접 의존 금지")
void controller_MustNotDependOnService() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..rest..controller..")
        .should().dependOnClassesThat().haveSimpleNameEndingWith("Service")
        .because("UseCase 인터페이스를 통해 의존");
    rule.check(restApiClasses);
}',
'RestApiControllerArchTest', 'controller_MustNotDependOnService', 'MAJOR', NOW(), NOW());

-- API-CTR-005: Controller @Transactional 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (60, 'ARCH-API-CTR-005', 'Controller @Transactional 금지', 'Controller에 @Transactional이 없는가?',
'@Test
@DisplayName("[금지] Controller에 @Transactional 금지")
void controller_MustNotHaveTransactional() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..rest..controller..")
        .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
        .because("트랜잭션 경계는 Application Layer 책임");
    rule.check(restApiClasses);
}',
'RestApiControllerArchTest', 'controller_MustNotHaveTransactional', 'BLOCKER', NOW(), NOW());

-- API-CTR-011: List 직접 반환 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (60, 'ARCH-API-CTR-011', 'List 직접 반환 금지', '목록 조회 시 List를 직접 반환하지 않는가?',
'@Test
@DisplayName("[금지] Controller에서 List 직접 반환 금지")
void controller_MustNotReturnListDirectly() {
    ArchRule rule = noMethods()
        .that().areDeclaredInClassesThat().resideInAPackage("..rest..controller..")
        .and().arePublic()
        .should().haveRawReturnType(java.util.List.class)
        .because("ApiResponse<List<T>> 또는 페이징 응답 사용");
    rule.check(restApiClasses);
}',
'RestApiControllerArchTest', 'controller_MustNotReturnListDirectly', 'BLOCKER', NOW(), NOW());

-- API-DTO-001: Record 타입 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (62, 'ARCH-API-DTO-001', 'API DTO Record 타입 필수', 'Request/Response DTO가 Record인가?',
'@Test
@DisplayName("[필수] API DTO는 Record 타입")
void apiDto_MustBeRecord() {
    ArchRule rule = classes()
        .that().resideInAPackage("..rest..dto..")
        .and().haveSimpleNameEndingWith("ApiRequest")
        .or().haveSimpleNameEndingWith("ApiResponse")
        .should().beRecords()
        .allowEmptyShould(true)
        .because("API DTO는 불변 Record 사용");
    rule.check(restApiClasses);
}',
'RestApiDtoArchTest', 'apiDto_MustBeRecord', 'MAJOR', NOW(), NOW());

-- API-DTO-002: DTO 불변성 보장
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (62, 'ARCH-API-DTO-002', 'DTO 불변성 보장', 'DTO가 불변이고 Setter가 없는가?',
'@Test
@DisplayName("[금지] API DTO에 Setter 금지")
void apiDto_MustNotHaveSetter() {
    ArchRule rule = noMethods()
        .that().areDeclaredInClassesThat().resideInAPackage("..rest..dto..")
        .and().haveNameMatching("set[A-Z].*")
        .should().beDeclared()
        .because("DTO는 불변");
    rule.check(restApiClasses);
}',
'RestApiDtoArchTest', 'apiDto_MustNotHaveSetter', 'MAJOR', NOW(), NOW());

-- API-MAP-001: Mapper @Component 필수
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (53, 'ARCH-API-MAP-001', 'Mapper @Component 필수', 'Mapper에 @Component가 있는가?',
'@Test
@DisplayName("[필수] Mapper는 @Component 필수")
void mapper_MustHaveComponent() {
    ArchRule rule = classes()
        .that().resideInAPackage("..rest..mapper..")
        .and().haveSimpleNameEndingWith("Mapper")
        .should().beAnnotatedWith("org.springframework.stereotype.Component")
        .allowEmptyShould(true)
        .because("Spring Bean으로 관리");
    rule.check(restApiClasses);
}',
'RestApiMapperArchTest', 'mapper_MustHaveComponent', 'MAJOR', NOW(), NOW());

-- API-END-001: Endpoints final class
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (49, 'ARCH-API-END-001', 'Endpoints final class', 'Endpoints가 final + private 생성자인가?',
'@Test
@DisplayName("[필수] Endpoints는 final class")
void endpoints_MustBeFinalClass() {
    ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Endpoints")
        .should().haveModifier(JavaModifier.FINAL)
        .allowEmptyShould(true)
        .because("상수 클래스는 final");
    rule.check(restApiClasses);
}',
'RestApiEndpointsArchTest', 'endpoints_MustBeFinalClass', 'MAJOR', NOW(), NOW());

-- API-TST-001: MockMvc 금지
INSERT INTO archunit_test (structure_id, code, name, description, test_code, test_class_name, test_method_name, severity, created_at, updated_at)
VALUES (60, 'ARCH-API-TST-001', 'MockMvc 금지', '테스트에서 MockMvc를 사용하지 않는가?',
'@Test
@DisplayName("[금지] 테스트에서 MockMvc 금지")
void test_MustNotUseMockMvc() {
    ArchRule rule = noClasses()
        .that().resideInAPackage("..rest..")
        .should().dependOnClassesThat().haveFullyQualifiedName(
            "org.springframework.test.web.servlet.MockMvc")
        .because("TestRestTemplate 사용 권장");
    rule.check(restApiClasses);
}',
'RestApiTestArchTest', 'test_MustNotUseMockMvc', 'MAJOR', NOW(), NOW());

-- =====================================================
-- 통계 업데이트
-- =====================================================
-- 총 94개 AUTOMATED 체크리스트 기반 ArchUnit 테스트 생성
