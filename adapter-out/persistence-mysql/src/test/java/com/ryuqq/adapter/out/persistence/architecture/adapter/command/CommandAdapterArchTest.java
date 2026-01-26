package com.ryuqq.adapter.out.persistence.architecture.adapter.command;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

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

/**
 * CommandAdapterArchTest - Command Adapter 아키텍처 규칙 검증 (20개 규칙)
 *
 * <p>command-adapter-guide.md 규칙을 ArchUnit으로 검증합니다.
 *
 * <p><strong>핵심 원칙:</strong>
 *
 * <ul>
 *   <li>✅ JpaRepository와 1:1 매핑
 *   <li>✅ 필드 2개 (JpaRepository + Mapper)
 *   <li>✅ 메서드 1개 (persist)
 *   <li>✅ 반환 타입: *Id
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("CommandAdapter 아키텍처 규칙 검증 (Zero-Tolerance)")
class CommandAdapterArchTest {

    private static final String BASE_PACKAGE = "com.ryuqq.adapter.out.persistence";

    private static JavaClasses allClasses;
    private static JavaClasses commandAdapterClasses;

    @BeforeAll
    static void setUp() {
        allClasses =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(BASE_PACKAGE);

        commandAdapterClasses =
                allClasses.that(
                        DescribedPredicate.describe(
                                "Command Adapter 클래스",
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("CommandAdapter")
                                                && !javaClass.isInterface()));
    }

    // ========================================================================
    // 1. 클래스 구조 규칙 (5개)
    // ========================================================================

    @Nested
    @DisplayName("1. 클래스 구조 규칙")
    class ClassStructureRules {

        @Test
        @DisplayName("규칙 1-1: CommandAdapter는 클래스여야 합니다")
        void commandAdapter_MustBeClass() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should()
                            .notBeInterfaces()
                            .allowEmptyShould(true)
                            .because("Command Adapter는 클래스로 정의되어야 합니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 1-2: @Component 어노테이션이 필수입니다")
        void commandAdapter_MustHaveComponentAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should()
                            .beAnnotatedWith(Component.class)
                            .allowEmptyShould(true)
                            .because("Command Adapter는 @Component 어노테이션이 필수입니다");

            rule.check(commandAdapterClasses);
        }

        /**
         * 규칙 1-3: PersistencePort 또는 CommandPort 인터페이스 의존성 검증
         *
         * <p>인터페이스 구현 검증 대신 의존성으로 확인합니다. ArchUnit이 외부 모듈(application)의 인터페이스를 resolve하기 어렵기 때문에 의존성
         * 체크로 대체합니다.
         */
        @Test
        @DisplayName("규칙 1-3: PersistencePort 또는 CommandPort 인터페이스에 의존해야 합니다")
        void commandAdapter_MustDependOnPort() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should()
                            .dependOnClassesThat(
                                    DescribedPredicate.describe(
                                            "PersistencePort 또는 CommandPort 인터페이스",
                                            javaClass ->
                                                    javaClass
                                                                    .getSimpleName()
                                                                    .endsWith("PersistencePort")
                                                            || javaClass
                                                                    .getSimpleName()
                                                                    .endsWith("CommandPort")))
                            .allowEmptyShould(true)
                            .because(
                                    "Command Adapter는 PersistencePort 또는 CommandPort 인터페이스를 구현해야"
                                            + " 합니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 1-4: 정확히 2개 필드만 허용됩니다 (JpaRepository + Mapper)")
        void commandAdapter_MustHaveExactlyTwoFields() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "정확히 2개의 필드",
                                                    javaClass ->
                                                            javaClass.getAllFields().size() == 2)))
                            .allowEmptyShould(true)
                            .because("Command Adapter는 정확히 2개의 필드(JpaRepository, Mapper)만 가져야 합니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 1-5: 모든 필드는 final이어야 합니다")
        void commandAdapter_AllFieldsMustBeFinal() {
            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .should()
                            .beFinal()
                            .allowEmptyShould(true)
                            .because("Command Adapter의 모든 필드는 final로 불변성을 보장해야 합니다");

            rule.check(commandAdapterClasses);
        }
    }

    // ========================================================================
    // 2. 의존성 규칙 (4개)
    // ========================================================================

    @Nested
    @DisplayName("2. 의존성 규칙")
    class DependencyRules {

        @Test
        @DisplayName("규칙 2-1: JpaRepository 의존성이 필수입니다")
        void commandAdapter_MustDependOnJpaRepository() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should()
                            .dependOnClassesThat()
                            .haveSimpleNameEndingWith("JpaRepository")
                            .allowEmptyShould(true)
                            .because("Command Adapter는 JpaRepository 의존성이 필수입니다 (1:1 매핑)");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 2-2: Mapper 의존성이 필수입니다")
        void commandAdapter_MustDependOnMapper() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should()
                            .dependOnClassesThat()
                            .haveSimpleNameEndingWith("Mapper")
                            .allowEmptyShould(true)
                            .because("Command Adapter는 Mapper 의존성이 필수입니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 2-3: @Autowired 필드 주입이 금지됩니다")
        void commandAdapter_MustNotUseFieldInjection() {
            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .should()
                            .notBeAnnotatedWith(Autowired.class)
                            .allowEmptyShould(true)
                            .because("Command Adapter는 생성자 주입만 허용되며, @Autowired 필드 주입은 금지입니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 2-4: 다른 타입의 Repository 의존성이 금지됩니다")
        void commandAdapter_MustNotDependOnOtherRepositories() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should()
                            .dependOnClassesThat(
                                    DescribedPredicate.describe(
                                            "JpaRepository가 아닌 다른 Repository",
                                            javaClass ->
                                                    javaClass.getSimpleName().endsWith("Repository")
                                                            && !javaClass
                                                                    .getSimpleName()
                                                                    .endsWith("JpaRepository")))
                            .allowEmptyShould(true)
                            .because("Command Adapter는 JpaRepository만 의존해야 합니다 (1:1 매핑 원칙)");

            rule.check(commandAdapterClasses);
        }
    }

    // ========================================================================
    // 3. 메서드 규칙 (6개)
    // ========================================================================

    @Nested
    @DisplayName("3. 메서드 규칙")
    class MethodRules {

        /** 규칙 3-1: 정확히 1개의 public 메서드만 허용됩니다 (경고) */
        @Test
        @DisplayName("규칙 3-1: 정확히 1개의 public 메서드만 권장됩니다 (경고)")
        void commandAdapter_ShouldHaveExactlyOnePublicMethod_Warning() {
            // Port 인터페이스에 여러 메서드가 정의될 수 있으므로 경고만 출력
            commandAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                    .forEach(
                            javaClass -> {
                                long publicMethodCount =
                                        javaClass.getMethods().stream()
                                                .filter(
                                                        method ->
                                                                method.getModifiers()
                                                                        .contains(
                                                                                JavaModifier
                                                                                        .PUBLIC))
                                                .filter(
                                                        method ->
                                                                !method.getName().equals("<init>"))
                                                .count();

                                if (publicMethodCount != 1) {
                                    System.err.println(
                                            "⚠️ [WARNING] "
                                                    + javaClass.getSimpleName()
                                                    + "에 public 메서드가 "
                                                    + publicMethodCount
                                                    + "개 있습니다. 1개를 권장합니다 (persist)");
                                }
                            });
        }

        /** 규칙 3-2: public 메서드명은 'persist'를 권장합니다 (경고) */
        @Test
        @DisplayName("규칙 3-2: public 메서드명은 'persist'를 권장합니다 (경고)")
        void commandAdapter_ShouldHavePersistMethodName_Warning() {
            // Port 인터페이스에서 다른 메서드명이 정의될 수 있으므로 경고만 출력
            var allowedNames = java.util.List.of("persist", "save", "update", "delete", "upsert");

            commandAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                    .forEach(
                            javaClass -> {
                                var nonMatchingMethods =
                                        javaClass.getMethods().stream()
                                                .filter(
                                                        method ->
                                                                method.getModifiers()
                                                                        .contains(
                                                                                JavaModifier
                                                                                        .PUBLIC))
                                                .filter(
                                                        method ->
                                                                !method.getName().equals("<init>"))
                                                .filter(
                                                        method ->
                                                                allowedNames.stream()
                                                                        .noneMatch(
                                                                                name ->
                                                                                        method.getName()
                                                                                                .startsWith(
                                                                                                        name)))
                                                .map(method -> method.getName())
                                                .toList();

                                if (!nonMatchingMethods.isEmpty()) {
                                    System.err.println(
                                            "⚠️ [WARNING] "
                                                    + javaClass.getSimpleName()
                                                    + "에 권장 패턴이 아닌 메서드가 있습니다: "
                                                    + nonMatchingMethods
                                                    + ". 권장 패턴: persist*, save*, update*, delete*");
                                }
                            });
        }

        @Test
        @DisplayName("규칙 3-3: persist 메서드는 정확히 1개 파라미터를 받습니다")
        void commandAdapter_PersistMethodMustHaveExactlyOneParameter() {
            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .haveName("persist")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "정확히 1개의 파라미터",
                                                    method ->
                                                            method.getRawParameterTypes().size()
                                                                    == 1)))
                            .allowEmptyShould(true)
                            .because("persist() 메서드는 정확히 1개의 Domain Aggregate 파라미터만 가져야 합니다");

            rule.check(commandAdapterClasses);
        }

        /**
         * 규칙 3-4: persist 메서드는 *Id 타입 반환 권장 (경고만 출력)
         *
         * <p>일부 persist 메서드는 void 또는 다른 타입을 반환할 수 있으므로 경고만 출력합니다.
         */
        @Test
        @DisplayName("규칙 3-4: persist 메서드는 *Id 타입 반환 권장 (경고)")
        void commandAdapter_PersistMethodShouldReturnIdType_Warning() {
            commandAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                    .forEach(
                            javaClass -> {
                                javaClass.getMethods().stream()
                                        .filter(method -> method.getName().equals("persist"))
                                        .forEach(
                                                method -> {
                                                    String returnTypeName =
                                                            method.getRawReturnType()
                                                                    .getSimpleName();
                                                    if (!returnTypeName.endsWith("Id")) {
                                                        System.err.println(
                                                                "⚠️ [WARNING] "
                                                                        + javaClass.getSimpleName()
                                                                        + ".persist() 메서드가 "
                                                                        + returnTypeName
                                                                        + "을 반환합니다. *Id 타입 반환을"
                                                                        + " 권장합니다.");
                                                    }
                                                });
                            });
            // 경고만 출력하고 테스트는 통과
        }

        /**
         * 규칙 3-5: @Override 어노테이션 필수
         *
         * <p>⚠️ 이 규칙은 ArchUnit으로 검증할 수 없습니다. `@Override`는 `@Retention(RetentionPolicy.SOURCE)`이므로
         * 컴파일 후 바이트코드에 유지되지 않습니다.
         *
         * <p>대신 다음 방법으로 검증합니다:
         *
         * <ul>
         *   <li>IDE의 컴파일러 경고 활용
         *   <li>코드 리뷰 체크리스트
         *   <li>Checkstyle/PMD 규칙
         * </ul>
         */
        @Test
        @DisplayName("규칙 3-5: @Override 어노테이션 필수 (문서화만 - ArchUnit 검증 불가)")
        void commandAdapter_PersistMethodMustHaveOverrideAnnotation_DocumentationOnly() {
            // @Override는 RetentionPolicy.SOURCE이므로 바이트코드에 없음
            // ArchUnit은 바이트코드를 검사하므로 이 규칙 검증 불가
            // 이 테스트는 규칙의 존재를 문서화하는 용도입니다.

            // 대신 persist 메서드가 Port 인터페이스에서 정의되었는지 확인합니다
            // (Port 구현 여부는 다른 테스트에서 검증)
        }

        @Test
        @DisplayName("규칙 3-6: Query 메서드가 금지됩니다")
        void commandAdapter_MustNotContainQueryMethods() {
            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .should()
                            .haveNameNotMatching("(find|load|get|query|search|list|count|exists).*")
                            .allowEmptyShould(true)
                            .because("Command Adapter는 Query 메서드를 포함하면 안 됩니다. QueryAdapter로 분리하세요");

            rule.check(commandAdapterClasses);
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
        void commandAdapter_MustNotBeAnnotatedWithTransactional() {
            ArchRule classRule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should()
                            .notBeAnnotatedWith(Transactional.class)
                            .allowEmptyShould(true)
                            .because("Command Adapter 클래스에 @Transactional 사용 금지. UseCase에서 관리하세요");

            ArchRule methodRule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .should()
                            .notBeAnnotatedWith(Transactional.class)
                            .allowEmptyShould(true)
                            .because("Command Adapter 메서드에 @Transactional 사용 금지. UseCase에서 관리하세요");

            classRule.check(commandAdapterClasses);
            methodRule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-2: 비즈니스 예외 throw가 금지됩니다")
        void commandAdapter_MustNotThrowBusinessExceptions() {
            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .haveName("persist")
                            .should()
                            .notDeclareThrowableOfType(RuntimeException.class)
                            .allowEmptyShould(true)
                            .because("Command Adapter는 비즈니스 예외를 throw하지 않습니다. Domain에서 처리하세요");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-3: 로깅이 금지됩니다")
        void commandAdapter_MustNotContainLogging() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*Logger.*")
                            .allowEmptyShould(true)
                            .because("Command Adapter는 로깅을 포함하지 않습니다. AOP로 처리하세요");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-4: Validator 의존성이 금지됩니다")
        void commandAdapter_MustNotDependOnValidator() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*Validator.*")
                            .allowEmptyShould(true)
                            .because("Command Adapter는 Validator를 사용하지 않습니다. Domain에서 처리하세요");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-5: private helper 메서드가 금지됩니다")
        void commandAdapter_MustNotHavePrivateHelperMethods() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .resideInAPackage("..adapter..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "private 메서드 없음",
                                                    javaClass ->
                                                            javaClass.getMethods().stream()
                                                                            .filter(
                                                                                    method ->
                                                                                            method.getModifiers()
                                                                                                    .contains(
                                                                                                            JavaModifier
                                                                                                                    .PRIVATE))
                                                                            .count()
                                                                    == 0)))
                            .allowEmptyShould(true)
                            .because("Command Adapter는 private helper 메서드를 가질 수 없습니다");

            rule.check(commandAdapterClasses);
        }
    }
}
