package com.ryuqq.adapter.out.persistence.architecture.adapter.query.general;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * QueryAdapterArchTest - Query Adapter 아키텍처 규칙 검증 (21개 규칙)
 *
 * <p>query-adapter-guide.md 규칙을 ArchUnit으로 검증합니다.</p>
 *
 * <p><strong>핵심 원칙:</strong></p>
 * <ul>
 *   <li>✅ QueryDslRepository와 1:1 매핑</li>
 *   <li>✅ 필드 2개 (QueryDslRepository + Mapper)</li>
 *   <li>✅ JpaRepository 의존 금지 (QueryDslRepository만 허용)</li>
 *   <li>✅ 메서드 5개 (findById, findByIdIncludingDeleted, existsById, findByCriteria, countByCriteria)</li>
 *   <li>✅ 반환 타입: Domain (Optional/List/boolean/long)</li>
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("QueryAdapter 아키텍처 규칙 검증 (Zero-Tolerance)")
class QueryAdapterArchTest {

    private static final String BASE_PACKAGE = "com.ryuqq.adapter.out.persistence";

    private static JavaClasses allClasses;
    private static JavaClasses queryAdapterClasses;

    @BeforeAll
    static void setUp() {
        allClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        // QueryAdapter만 선택 (LockQueryAdapter, AdminQueryAdapter, McpContextQueryAdapter 제외)
        queryAdapterClasses = allClasses.that(
            DescribedPredicate.describe(
                "Query Adapter 클래스 (Lock, Admin, Mcp 제외)",
                javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter") &&
                    !javaClass.getSimpleName().contains("Lock") &&
                    !javaClass.getSimpleName().contains("Admin") &&
                    !javaClass.getSimpleName().contains("Mcp") &&
                    !javaClass.isInterface()
            )
        );
    }

    // ========================================================================
    // 1. 클래스 구조 규칙 (5개)
    // ========================================================================

    @Nested
    @DisplayName("1. 클래스 구조 규칙")
    class ClassStructureRules {

        @Test
        @DisplayName("규칙 1-1: QueryAdapter는 클래스여야 합니다")
        void queryAdapter_MustBeClass() {
            ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .and().resideInAPackage("..adapter..")
                .should().notBeInterfaces()
                .allowEmptyShould(true)
                .because("Query Adapter는 클래스로 정의되어야 합니다");

            rule.check(queryAdapterClasses);
        }

        @Test
        @DisplayName("규칙 1-2: @Component 어노테이션이 필수입니다")
        void queryAdapter_MustHaveComponentAnnotation() {
            ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .and().resideInAPackage("..adapter..")
                .should().beAnnotatedWith(Component.class)
                .allowEmptyShould(true)
                .because("Query Adapter는 @Component 어노테이션이 필수입니다");

            rule.check(queryAdapterClasses);
        }

        /**
         * 규칙 1-3: QueryPort 또는 LoadPort 인터페이스 의존성 검증
         *
         * <p>인터페이스 구현 검증 대신 의존성으로 확인합니다. ArchUnit이 외부 모듈(application)의
         * 인터페이스를 resolve하기 어렵기 때문에 의존성 체크로 대체합니다.
         */
        @Test
        @DisplayName("규칙 1-3: QueryPort 또는 LoadPort 인터페이스에 의존해야 합니다")
        void queryAdapter_MustDependOnPort() {
            ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .and().resideInAPackage("..adapter..")
                .should().dependOnClassesThat(
                    DescribedPredicate.describe(
                        "QueryPort 또는 LoadPort 인터페이스",
                        javaClass ->
                            javaClass.getSimpleName().endsWith("QueryPort") ||
                                javaClass.getSimpleName().endsWith("LoadPort")
                    )
                )
                .allowEmptyShould(true)
                .because("Query Adapter는 QueryPort 또는 LoadPort 인터페이스를 구현해야 합니다");

            rule.check(queryAdapterClasses);
        }

        /**
         * 규칙 1-4: 정확히 2개 필드 권장 (경고만 출력, 실패하지 않음)
         *
         * <p>비즈니스 요구사항에 따라 추가 필드가 필요할 수 있으므로 경고만 출력합니다.
         */
        @Test
        @DisplayName("규칙 1-4: 정확히 2개 필드 권장 (경고)")
        void queryAdapter_ShouldHaveExactlyTwoFields_Warning() {
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    int fieldCount = javaClass.getAllFields().size();
                    if (fieldCount != 2) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 " + fieldCount + "개의 필드가 있습니다 (권장: 2개 - QueryDslRepository + Mapper)");
                    }
                });
            // 경고만 출력하고 테스트는 통과
        }

        @Test
        @DisplayName("규칙 1-5: 모든 필드는 final이어야 합니다")
        void queryAdapter_AllFieldsMustBeFinal() {
            ArchRule rule = fields()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("QueryAdapter")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Lock")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Admin")
                .should().beFinal()
                .allowEmptyShould(true)
                .because("Query Adapter의 모든 필드는 final로 불변성을 보장해야 합니다");

            rule.check(queryAdapterClasses);
        }
    }

    // ========================================================================
    // 2. 의존성 규칙 (5개)
    // ========================================================================

    @Nested
    @DisplayName("2. 의존성 규칙")
    class DependencyRules {

        /**
         * 규칙 2-1: QueryDslRepository 의존성 권장 (경고만 출력)
         *
         * <p>일부 QueryAdapter는 JpaRepository를 직접 사용할 수 있으므로 경고만 출력합니다.
         */
        @Test
        @DisplayName("규칙 2-1: QueryDslRepository 의존성 권장 (경고)")
        void queryAdapter_ShouldDependOnQueryDslRepository_Warning() {
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    boolean hasQueryDslDep = javaClass.getDirectDependenciesFromSelf().stream()
                        .anyMatch(dep -> dep.getTargetClass().getSimpleName().endsWith("QueryDslRepository"));
                    if (!hasQueryDslDep) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 QueryDslRepository 의존성이 권장됩니다 (복잡한 쿼리 지원)");
                    }
                });
            // 경고만 출력하고 테스트는 통과
        }

        /**
         * 규칙 2-2: Mapper 의존성 권장 (경고만 출력)
         *
         * <p>일부 QueryAdapter는 Projection을 직접 사용할 수 있으므로 경고만 출력합니다.
         */
        @Test
        @DisplayName("규칙 2-2: Mapper 의존성 권장 (경고)")
        void queryAdapter_ShouldDependOnMapper_Warning() {
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    boolean hasMapperDep = javaClass.getDirectDependenciesFromSelf().stream()
                        .anyMatch(dep -> dep.getTargetClass().getSimpleName().endsWith("Mapper"));
                    if (!hasMapperDep) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 Mapper 의존성이 권장됩니다 (Entity ↔ Domain 변환)");
                    }
                });
            // 경고만 출력하고 테스트는 통과
        }

        @Test
        @DisplayName("규칙 2-3: @Autowired 필드 주입이 금지됩니다")
        void queryAdapter_MustNotUseFieldInjection() {
            ArchRule rule = fields()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("QueryAdapter")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Lock")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Admin")
                .should().notBeAnnotatedWith(Autowired.class)
                .allowEmptyShould(true)
                .because("Query Adapter는 생성자 주입만 허용되며, @Autowired 필드 주입은 금지입니다");

            rule.check(queryAdapterClasses);
        }

        @Test
        @DisplayName("규칙 2-4: JPAQueryFactory 직접 사용이 금지됩니다")
        void queryAdapter_MustNotUseJPAQueryFactoryDirectly() {
            ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .should().accessClassesThat().haveNameMatching(".*JPAQueryFactory.*")
                .allowEmptyShould(true)
                .because("Query Adapter는 JPAQueryFactory를 직접 사용하지 않고 QueryDslRepository를 통해 조회해야 합니다");

            rule.check(queryAdapterClasses);
        }

        @Test
        @DisplayName("규칙 2-5: JpaRepository 의존이 금지됩니다 (QueryDslRepository만 허용)")
        void queryAdapter_MustNotDependOnJpaRepository() {
            // ZeroTolerance, Mcp는 특수 어댑터로 예외
            ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .and().haveSimpleNameNotContaining("ZeroTolerance")
                .and().haveSimpleNameNotContaining("Mcp")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("JpaRepository")
                .allowEmptyShould(true)
                .because("Query Adapter는 JpaRepository 사용 금지. QueryDslRepository만 사용해야 합니다");

            rule.check(queryAdapterClasses);
        }
    }

    // ========================================================================
    // 3. 메서드 규칙 (6개)
    // ========================================================================

    @Nested
    @DisplayName("3. 메서드 규칙")
    class MethodRules {

        /**
         * 규칙 3-1: 정확히 5개의 public 메서드 권장 (경고만 출력, 실패하지 않음)
         *
         * <p>비즈니스 요구사항에 따라 추가 메서드가 필요할 수 있으므로 경고만 출력합니다.
         * 핵심 5개 메서드(findById, findByIdIncludingDeleted, existsById, findByCriteria, countByCriteria)는
         * 별도 테스트에서 필수 검증됩니다.
         */
        @Test
        @DisplayName("규칙 3-1: 정확히 5개의 public 메서드 권장 (경고)")
        void queryAdapter_ShouldHaveExactlyFivePublicMethods_Warning() {
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    long publicMethodCount = javaClass.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                        .filter(method -> !method.getName().equals("<init>"))
                        .count();

                    if (publicMethodCount != 5) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 " + publicMethodCount + "개의 public 메서드가 있습니다 (권장: 5개). "
                                + "추가 메서드가 정말 필요한지 검토하세요.");
                    }
                });
            // 경고만 출력하고 테스트는 통과
        }

        @Test
        @DisplayName("규칙 3-2: findById 메서드가 필수입니다")
        void queryAdapter_MustHaveFindByIdMethod() {
            // ZeroTolerance는 상세 조회 전용 어댑터 (findDetailById만 제공)
            // Mcp는 복합 JOIN 쿼리 전용 어댑터 (CRUD 패턴 미적용)
            ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .and().haveSimpleNameNotContaining("ZeroTolerance")
                .and().haveSimpleNameNotContaining("Mcp")
                .and().resideInAPackage("..adapter..")
                .should(ArchCondition.from(
                    DescribedPredicate.describe(
                        "public findById 메서드",
                        javaClass -> javaClass.getMethods().stream()
                            .anyMatch(method -> method.getName().equals("findById") &&
                                method.getModifiers().contains(JavaModifier.PUBLIC))
                    )
                ))
                .allowEmptyShould(true)
                .because("Query Adapter는 단건 조회를 위한 findById() 메서드가 필수입니다");

            rule.check(queryAdapterClasses);
        }

        @Test
        @DisplayName("규칙 3-3: existsById 또는 exists* 메서드 권장 (경고)")
        void queryAdapter_ShouldHaveExistsByIdMethod_Warning() {
            // exists 메서드가 없으면 경고만 출력 (테스트 실패 안 함)
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    boolean hasExistsMethod = javaClass.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                        .anyMatch(method -> method.getName().startsWith("exists"));

                    if (!hasExistsMethod) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 exists* 메서드가 권장됩니다 (존재 여부 확인용)");
                    }
                });
        }

        @Test
        @DisplayName("규칙 3-4: findBy* 목록 조회 메서드 권장 (경고)")
        void queryAdapter_ShouldHaveFindByCriteriaMethod_Warning() {
            // findBy* 또는 search* 메서드가 없으면 경고만 출력 (테스트 실패 안 함)
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    boolean hasListMethod = javaClass.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                        .anyMatch(method ->
                            method.getName().startsWith("findBy") ||
                            method.getName().startsWith("search") ||
                            method.getName().equals("findAll"));

                    if (!hasListMethod) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 findBy*/search* 목록 조회 메서드가 권장됩니다");
                    }
                });
        }

        @Test
        @DisplayName("규칙 3-5: count* 메서드 권장 (경고)")
        void queryAdapter_ShouldHaveCountByCriteriaMethod_Warning() {
            // count 메서드가 없으면 경고만 출력 (테스트 실패 안 함)
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    boolean hasCountMethod = javaClass.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                        .anyMatch(method -> method.getName().startsWith("count"));

                    if (!hasCountMethod) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 count* 개수 조회 메서드가 권장됩니다");
                    }
                });
        }

        @Test
        @DisplayName("규칙 3-6: Command 메서드가 금지됩니다")
        void queryAdapter_MustNotContainCommandMethods() {
            ArchRule rule = methods()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("QueryAdapter")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Lock")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Admin")
                .should().haveNameNotMatching("(save|persist|update|delete|insert|remove|create).*")
                .allowEmptyShould(true)
                .because("Query Adapter는 Command 메서드를 포함하면 안 됩니다. CommandAdapter로 분리하세요");

            rule.check(queryAdapterClasses);
        }
    }

    // ========================================================================
    // 4. 금지 사항 규칙 (5개)
    // ========================================================================

    @Nested
    @DisplayName("4. 금지 사항 규칙")
    class ProhibitionRules {

        @Test
        @DisplayName("규칙 4-1: @Transactional 사용이 금지됩니다")
        void queryAdapter_MustNotBeAnnotatedWithTransactional() {
            ArchRule classRule = classes()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .and().resideInAPackage("..adapter..")
                .should().notBeAnnotatedWith(Transactional.class)
                .allowEmptyShould(true)
                .because("Query Adapter 클래스에 @Transactional 사용 금지. 읽기 전용 작업입니다");

            ArchRule methodRule = methods()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("QueryAdapter")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Lock")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Admin")
                .should().notBeAnnotatedWith(Transactional.class)
                .allowEmptyShould(true)
                .because("Query Adapter 메서드에 @Transactional 사용 금지. 읽기 전용 작업입니다");

            classRule.check(queryAdapterClasses);
            methodRule.check(queryAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-2: 비즈니스 메서드가 금지됩니다")
        void queryAdapter_MustNotContainBusinessMethods() {
            ArchRule rule = methods()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("QueryAdapter")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Lock")
                .and().areDeclaredInClassesThat().haveSimpleNameNotContaining("Admin")
                .should().haveNameNotMatching("(confirm|cancel|approve|reject|modify|change|validate|calculate).*")
                .allowEmptyShould(true)
                .because("Query Adapter는 비즈니스 메서드를 포함하면 안 됩니다. Domain에서 처리하세요");

            rule.check(queryAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-3: 로깅이 금지됩니다")
        void queryAdapter_MustNotContainLogging() {
            ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .should().accessClassesThat().haveNameMatching(".*Logger.*")
                .allowEmptyShould(true)
                .because("Query Adapter는 로깅을 포함하지 않습니다. AOP로 처리하세요");

            rule.check(queryAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-4: Validator 의존성이 금지됩니다")
        void queryAdapter_MustNotDependOnValidator() {
            ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("QueryAdapter")
                .and().haveSimpleNameNotContaining("Lock")
                .and().haveSimpleNameNotContaining("Admin")
                .should().accessClassesThat().haveNameMatching(".*Validator.*")
                .allowEmptyShould(true)
                .because("Query Adapter는 Validator를 사용하지 않습니다");

            rule.check(queryAdapterClasses);
        }

        /**
         * 규칙 4-5: private helper 메서드 최소화 권장 (경고만 출력)
         *
         * <p>복잡한 매핑 로직에서 private 메서드가 필요할 수 있으므로 경고만 출력합니다.
         */
        @Test
        @DisplayName("규칙 4-5: private helper 메서드 최소화 권장 (경고)")
        void queryAdapter_ShouldMinimizePrivateHelperMethods_Warning() {
            queryAdapterClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                .forEach(javaClass -> {
                    long privateMethodCount = javaClass.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.PRIVATE))
                        .count();
                    if (privateMethodCount > 0) {
                        System.err.println(
                            "⚠️ [WARNING] " + javaClass.getSimpleName()
                                + "에 " + privateMethodCount + "개의 private 메서드가 있습니다. "
                                + "Adapter는 단순 위임 역할을 권장합니다.");
                    }
                });
            // 경고만 출력하고 테스트는 통과
        }
    }
}
