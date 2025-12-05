package com.ryuqq.application.architecture.service;

import static com.tngtech.archunit.core.domain.JavaModifier.FINAL;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;

/**
 * CommandService ArchUnit 검증 테스트 (Zero-Tolerance)
 *
 * <p>핵심 철학: CommandService는 UseCase 구현체로 조율만 수행, 트랜잭션/비즈니스 로직 금지
 */
@DisplayName("CommandService ArchUnit Tests (Zero-Tolerance)")
@Tag("architecture")
@Tag("service")
class CommandServiceArchTest {

    private static JavaClasses classes;
    private static boolean hasCommandServiceClasses;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages("com.ryuqq.application");

        hasCommandServiceClasses =
                classes.stream()
                        .anyMatch(
                                javaClass ->
                                        javaClass.getPackageName().contains("service.command")
                                                && javaClass.getSimpleName().endsWith("Service"));
    }

    // ==================== 기본 구조 규칙 ====================

    @Nested
    @DisplayName("기본 구조 규칙")
    class BasicStructureRules {

        @Test
        @DisplayName("[필수] service.command 패키지의 Service는 @Service 어노테이션을 가져야 한다")
        void commandService_MustHaveServiceAnnotation() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .and()
                            .areNotInterfaces()
                            .should()
                            .beAnnotatedWith(Service.class)
                            .because("CommandService는 @Service 어노테이션을 가져야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] service.command 패키지의 클래스는 'Service' 접미사를 가져야 한다")
        void commandService_MustHaveCorrectSuffix() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .areNotInterfaces()
                            .and()
                            .areNotAnonymousClasses()
                            .should()
                            .haveSimpleNameEndingWith("Service")
                            .because("Command Service는 'Service' 접미사를 사용해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] CommandService는 final 클래스가 아니어야 한다")
        void commandService_MustNotBeFinal() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .and()
                            .areNotInterfaces()
                            .should()
                            .notHaveModifier(FINAL)
                            .because("Spring 프록시 생성을 위해 CommandService가 final이 아니어야 합니다");

            rule.check(classes);
        }
    }

    // ==================== 금지 규칙 (Zero-Tolerance) ====================

    @Nested
    @DisplayName("금지 규칙 (Zero-Tolerance)")
    class ProhibitionRules {

        @Test
        @DisplayName("[금지] CommandService는 @Transactional을 가지지 않아야 한다")
        void commandService_MustNotHaveTransactionalAnnotation() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .should()
                            .beAnnotatedWith(
                                    "org.springframework.transaction.annotation.Transactional")
                            .because(
                                    "CommandService는 @Transactional을 가지지 않아야 합니다. "
                                            + "트랜잭션 경계는 Manager/Facade 책임입니다.");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] CommandService는 @Component 어노테이션을 가지지 않아야 한다")
        void commandService_MustNotHaveComponentAnnotation() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .should()
                            .beAnnotatedWith("org.springframework.stereotype.Component")
                            .because("CommandService는 @Component가 아닌 @Service를 사용해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] CommandService는 Repository를 직접 의존하지 않아야 한다")
        void commandService_MustNotDependOnRepositories() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*Repository")
                            .because(
                                    "CommandService는 Repository를 직접 의존하지 않아야 합니다. "
                                            + "Manager/Facade를 통해 접근합니다.");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] CommandService는 Lombok 어노테이션을 가지지 않아야 한다")
        void commandService_MustNotUseLombok() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .should()
                            .beAnnotatedWith("lombok.Data")
                            .orShould()
                            .beAnnotatedWith("lombok.Builder")
                            .orShould()
                            .beAnnotatedWith("lombok.Getter")
                            .orShould()
                            .beAnnotatedWith("lombok.Setter")
                            .orShould()
                            .beAnnotatedWith("lombok.AllArgsConstructor")
                            .orShould()
                            .beAnnotatedWith("lombok.NoArgsConstructor")
                            .orShould()
                            .beAnnotatedWith("lombok.RequiredArgsConstructor")
                            .because("CommandService는 Plain Java를 사용해야 합니다 (Lombok 금지)");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] CommandService는 Query 관련 클래스를 의존하지 않아야 한다")
        void commandService_MustNotDependOnQueryClasses() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .should()
                            .dependOnClassesThat()
                            .haveSimpleNameContaining("Query")
                            .because(
                                    "CommandService는 Query 관련 클래스를 의존하지 않아야 합니다. "
                                            + "CQRS 분리 원칙을 준수해야 합니다.");

            rule.check(classes);
        }
    }

    // ==================== 의존성 규칙 ====================

    @Nested
    @DisplayName("의존성 규칙")
    class DependencyRules {

        @Test
        @DisplayName("[필수] CommandService는 Application Layer와 Domain Layer만 의존해야 한다")
        void commandService_MustOnlyDependOnApplicationAndDomainLayers() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .should()
                            .onlyAccessClassesThat()
                            .resideInAnyPackage(
                                    "com.ryuqq.application..",
                                    "com.ryuqq.domain..",
                                    "org.springframework..",
                                    "java..",
                                    "jakarta..")
                            .because("CommandService는 Application Layer와 Domain Layer만 의존해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] CommandService는 Adapter Layer를 의존하지 않아야 한다")
        void commandService_MustNotDependOnAdapterLayer() {
            assumeTrue(hasCommandServiceClasses, "CommandService 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..application..service.command..")
                            .and()
                            .haveSimpleNameEndingWith("Service")
                            .should()
                            .dependOnClassesThat()
                            .resideInAPackage("..adapter..")
                            .because("CommandService는 Adapter Layer를 의존하지 않아야 합니다");

            rule.check(classes);
        }
    }
}
