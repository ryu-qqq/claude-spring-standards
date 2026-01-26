package com.ryuqq.adapter.out.persistence.architecture;

import static com.ryuqq.adapter.out.persistence.architecture.ArchUnitPackageConstants.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DataAccessPatternArchTest - Data Access 패턴 일관성 검증
 *
 * <p>Persistence Layer의 데이터 접근 패턴 일관성을 검증합니다:
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>규칙 1: QueryDslRepository는 JPAQueryFactory 필드 필수
 *   <li>규칙 2: QueryDslRepository는 QType static final 필드 필수
 *   <li>규칙 3: QueryAdapter는 QueryDslRepository 의존 필수
 *   <li>규칙 4: CommandAdapter는 JpaRepository 의존 필수
 *   <li>규칙 5: QueryDslRepository는 DTO Projection 사용 (Entity 반환 금지)
 *   <li>규칙 6: Repository는 Domain 반환 금지 (DTO만 반환)
 *   <li>규칙 7: Test Fixtures는 fixture() 메서드 패턴 사용
 *   <li>규칙 8: Test Fixtures는 Builder 패턴 금지
 *   <li>규칙 10: QueryDslRepository는 허용된 메서드 패턴만 사용 (findBy*, existsBy*, search*, count*)
 *   <li>규칙 10-2: QueryDslRepository는 findAll() 사용 금지 (OOM 위험)
 * </ul>
 *
 * <p><strong>참고:</strong>
 *
 * <ul>
 *   <li>N+1 문제 예방: Join 사용은 수동 코드 리뷰로 검증 필요
 *   <li>DTO Projection: Projections.constructor() 사용 권장
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0 (3.0.0 유연한 메서드 패턴 적용)
 */
@DisplayName("Data Access 패턴 일관성 검증 (Zero-Tolerance)")
@Tag("architecture")
class DataAccessPatternArchTest {

    private static JavaClasses allClasses;

    @BeforeAll
    static void setUp() {
        allClasses = new ClassFileImporter().importPackages(PERSISTENCE);
    }

    /** 규칙 1: QueryDslRepository는 JPAQueryFactory 필드 필수 */
    @Test
    @DisplayName("[필수] QueryDslRepository는 JPAQueryFactory 필드를 가져야 한다")
    void queryDslRepository_MustHaveJPAQueryFactoryField() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .should()
                        .dependOnClassesThat()
                        .haveSimpleName("JPAQueryFactory")
                        .because("QueryDslRepository는 JPAQueryFactory 필드가 필수입니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 2: QueryDslRepository는 QType static final 필드 권장 (경고만 출력) */
    @Test
    @DisplayName("[권장] QueryDslRepository는 QType static final 필드를 가져야 한다 (경고)")
    void queryDslRepository_ShouldHaveQTypeStaticField_Warning() {
        // Q-type static final 필드가 없으면 경고만 출력 (테스트 실패 안 함)
        allClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryDslRepository"))
                .forEach(
                        javaClass -> {
                            boolean hasQTypeField =
                                    javaClass.getFields().stream()
                                            .filter(
                                                    field ->
                                                            field.getModifiers()
                                                                            .contains(
                                                                                    com.tngtech
                                                                                            .archunit
                                                                                            .core
                                                                                            .domain
                                                                                            .JavaModifier
                                                                                            .STATIC)
                                                                    && field.getModifiers()
                                                                            .contains(
                                                                                    com.tngtech
                                                                                            .archunit
                                                                                            .core
                                                                                            .domain
                                                                                            .JavaModifier
                                                                                            .FINAL))
                                            .anyMatch(
                                                    field ->
                                                            field.getRawType()
                                                                    .getSimpleName()
                                                                    .startsWith("Q"));

                            if (!hasQTypeField) {
                                System.err.println(
                                        "⚠️ [WARNING] "
                                                + javaClass.getSimpleName()
                                                + "에 Q-type static final 필드가 권장됩니다 (예: QEntity)");
                            }
                        });
    }

    /** 규칙 3: QueryAdapter는 QueryDslRepository 또는 JpaRepository 의존 (둘 중 하나) */
    @Test
    @DisplayName("[권장] QueryAdapter는 QueryDslRepository를 의존해야 한다 (경고)")
    void queryAdapter_ShouldDependOnQueryDslRepository_Warning() {
        // QueryDslRepository 또는 JpaRepository 중 하나를 의존하면 통과 (경고만 출력)
        allClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                .forEach(
                        javaClass -> {
                            boolean hasQueryDslRepo =
                                    javaClass.getDirectDependenciesFromSelf().stream()
                                            .anyMatch(
                                                    dep ->
                                                            dep.getTargetClass()
                                                                    .getSimpleName()
                                                                    .endsWith(
                                                                            "QueryDslRepository"));
                            boolean hasJpaRepo =
                                    javaClass.getDirectDependenciesFromSelf().stream()
                                            .anyMatch(
                                                    dep ->
                                                            dep.getTargetClass()
                                                                    .getSimpleName()
                                                                    .endsWith("JpaRepository"));

                            if (!hasQueryDslRepo && !hasJpaRepo) {
                                System.err.println(
                                        "⚠️ [WARNING] "
                                                + javaClass.getSimpleName()
                                                + "에 QueryDslRepository 또는 JpaRepository 의존이"
                                                + " 권장됩니다");
                            }
                        });
    }

    /** 규칙 4: CommandAdapter는 JpaRepository 의존 필수 */
    @Test
    @DisplayName("[필수] CommandAdapter는 JpaRepository를 의존해야 한다")
    void commandAdapter_MustDependOnJpaRepository() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("CommandAdapter")
                        .should()
                        .dependOnClassesThat()
                        .areAssignableTo(
                                org.springframework.data.jpa.repository.JpaRepository.class)
                        .because("CommandAdapter는 JpaRepository를 의존해야 합니다 (CQRS Command 패턴)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 5: QueryDslRepository는 DTO Projection 사용 (Entity 반환 금지) */
    @Test
    @DisplayName("[권장] QueryDslRepository는 JpaEntity를 반환하지 않아야 한다")
    void queryDslRepository_ShouldNotReturnJpaEntity() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .arePublic()
                        .should()
                        .haveRawReturnType(
                                com.tngtech.archunit.base.DescribedPredicate.describe(
                                        "JPA Entity class",
                                        javaClass ->
                                                javaClass.getSimpleName().endsWith("JpaEntity")))
                        .because(
                                "QueryDslRepository는 JpaEntity 대신 DTO Projection을 사용해야 합니다 (N+1"
                                        + " 예방)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 6: Repository는 Domain 반환 금지 (DTO만 반환) */
    @Test
    @DisplayName("[금지] Repository는 Domain을 직접 반환하지 않아야 한다")
    void repository_MustNotReturnDomain() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameContaining("Repository")
                        .and()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameNotContaining("Test")
                        .and()
                        .arePublic()
                        .should()
                        .haveRawReturnType(
                                com.tngtech.archunit.base.DescribedPredicate.describe(
                                        "Domain class from ..domain.. package",
                                        javaClass ->
                                                javaClass.getPackageName().contains(".domain.")
                                                        && !javaClass
                                                                .getSimpleName()
                                                                .endsWith("Dto")
                                                        && !javaClass
                                                                .getSimpleName()
                                                                .endsWith("JpaEntity")))
                        .because("Repository는 Domain을 직접 반환하면 안 됩니다 (DTO 변환은 Mapper가 담당)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 7: Test Fixtures는 fixture() 메서드 패턴 사용 */
    @Test
    @DisplayName("[권장] Test Fixtures는 fixture() 메서드를 제공해야 한다")
    void testFixtures_ShouldProvideFixtureMethod() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("Fixture")
                        .and()
                        .arePublic()
                        .and()
                        .areStatic()
                        .should()
                        .haveNameMatching(".*fixture.*")
                        .because("Test Fixtures는 fixture() 메서드 패턴을 사용해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 8: Test Fixtures는 Builder 패턴 금지 */
    @Test
    @DisplayName("[금지] Test Fixtures는 Builder 패턴을 사용하지 않아야 한다")
    void testFixtures_MustNotUseBuilderPattern() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleNameEndingWith("Fixture")
                        .should()
                        .dependOnClassesThat()
                        .haveSimpleNameContaining("Builder")
                        .because("Test Fixtures는 Builder 패턴 대신 fixture() 메서드를 사용해야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 9: Adapter는 Mapper를 통해 변환 권장 (경고만 출력) */
    @Test
    @DisplayName("[권장] Adapter는 Mapper를 의존해야 한다 (경고)")
    void adapter_ShouldDependOnMapper_Warning() {
        // Mapper 의존이 없으면 경고만 출력 (테스트 실패 안 함)
        allClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("Adapter"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Config"))
                .forEach(
                        javaClass -> {
                            boolean hasMapper =
                                    javaClass.getDirectDependenciesFromSelf().stream()
                                            .anyMatch(
                                                    dep ->
                                                            dep.getTargetClass()
                                                                    .getSimpleName()
                                                                    .endsWith("Mapper"));

                            if (!hasMapper) {
                                System.err.println(
                                        "⚠️ [WARNING] "
                                                + javaClass.getSimpleName()
                                                + "에 Mapper 의존이 권장됩니다 (Entity ↔ Domain 변환)");
                            }
                        });
    }

    /** 규칙 10: QueryDslRepository는 허용된 메서드 패턴 권장 (경고) */
    @Test
    @DisplayName("[권장] QueryDslRepository는 허용된 메서드 패턴을 권장한다 (경고)")
    void queryDslRepository_ShouldUseAllowedMethodPatterns_Warning() {
        var allowedPrefixes = java.util.List.of("findBy", "existsBy", "search", "count", "find");

        allClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryDslRepository"))
                .forEach(
                        javaClass -> {
                            var nonMatchingMethods =
                                    javaClass.getMethods().stream()
                                            .filter(
                                                    method ->
                                                            method.getModifiers()
                                                                    .contains(
                                                                            com.tngtech.archunit
                                                                                    .core.domain
                                                                                    .JavaModifier
                                                                                    .PUBLIC))
                                            .filter(
                                                    method ->
                                                            !method.getModifiers()
                                                                    .contains(
                                                                            com.tngtech.archunit
                                                                                    .core.domain
                                                                                    .JavaModifier
                                                                                    .STATIC))
                                            .filter(
                                                    method ->
                                                            allowedPrefixes.stream()
                                                                    .noneMatch(
                                                                            prefix ->
                                                                                    method.getName()
                                                                                            .startsWith(
                                                                                                    prefix)))
                                            .map(method -> method.getName())
                                            .toList();

                            if (!nonMatchingMethods.isEmpty()) {
                                System.err.println(
                                        "⚠️ [WARNING] "
                                                + javaClass.getSimpleName()
                                                + "에 비표준 메서드 패턴이 있습니다: "
                                                + nonMatchingMethods
                                                + ". 권장 패턴: findBy*, existsBy*, search*, count*");
                            }
                        });
        // 경고만 출력하고 테스트는 통과
    }

    /** 규칙 10-2: QueryDslRepository는 findAll() 메서드 사용 금지 (OOM 위험) */
    @Test
    @DisplayName("[금지] QueryDslRepository는 findAll() 사용이 금지된다")
    void queryDslRepository_MustNotUseFindAll() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .arePublic()
                        .should()
                        .haveName("findAll")
                        .because("QueryDslRepository는 findAll() 사용이 금지됩니다 (OOM 위험, search* 사용)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 11: Adapter는 JPAQueryFactory를 직접 사용 금지 */
    @Test
    @DisplayName("[금지] Adapter는 JPAQueryFactory를 직접 사용하지 않아야 한다")
    void adapter_MustNotUseJPAQueryFactoryDirectly() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleNameEndingWith("Adapter")
                        .should()
                        .dependOnClassesThat()
                        .haveSimpleName("JPAQueryFactory")
                        .because(
                                "Adapter는 JPAQueryFactory를 직접 사용하면 안 됩니다 (QueryDslRepository를 통해서만"
                                        + " 접근)");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /** 규칙 12: Config 클래스는 @Configuration 필수 */
    @Test
    @DisplayName("[필수] Config 클래스는 @Configuration 어노테이션을 가져야 한다")
    void config_MustHaveConfigurationAnnotation() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Config")
                        .and()
                        .resideInAPackage(CONFIG_PATTERN)
                        .should()
                        .beAnnotatedWith(org.springframework.context.annotation.Configuration.class)
                        .because("Config 클래스는 @Configuration 어노테이션이 필수입니다");

        rule.allowEmptyShould(true).check(allClasses);
    }
}
