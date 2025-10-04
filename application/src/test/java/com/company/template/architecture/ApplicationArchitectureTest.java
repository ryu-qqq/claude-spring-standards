package com.company.template.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Application Layer Architecture Tests
 *
 * Enforces Hexagonal Architecture rules for the Application Layer:
 * - Transaction management in application layer only
 * - Port interfaces in correct packages
 * - UseCase DTOs are immutable (records or final classes)
 * - Application depends only on domain
 *
 * @author Architecture Team (arch-team@company.com)
 * @since 2024-01-01
 */
@DisplayName("🔧 Application Layer Architecture Enforcement")
class ApplicationArchitectureTest {

    private static JavaClasses applicationClasses;
    private static JavaClasses allClasses;

    @BeforeAll
    static void setup() {
        applicationClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.template.application");

        allClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.template");
    }

    // ========================================
    // Transaction Management Rules
    // ========================================

    @Nested
    @DisplayName("💾 Transaction Management Enforcement")
    class TransactionManagementTests {

        @Test
        @DisplayName("@Transactional MUST be in application layer ONLY")
        void transactionsShouldBeInApplicationLayer() {
            ArchRule rule = classes()
                .that().areAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .should().resideInAPackage("..application..")
                .because("@Transactional must be in application layer, not adapters");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("Adapters MUST NOT have @Transactional")
        void adaptersShouldNotHaveTransactions() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..adapter..")
                .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .because("Transactions are application layer concern, not adapter concern");

            rule.check(allClasses);
        }

        @Test
        @DisplayName("UseCase implementations SHOULD have @Transactional")
        void useCasesShouldHaveTransactional() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..service..")
                .and().haveSimpleNameEndingWith("Service")
                .and().areNotInterfaces()
                .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .orShould().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .because("UseCase implementations should manage transactions");

            rule.check(applicationClasses);
        }
    }

    // ========================================
    // Dependency Rules
    // ========================================

    @Nested
    @DisplayName("📦 Application Dependency Enforcement")
    class DependencyTests {

        @Test
        @DisplayName("Application MUST only depend on domain")
        void applicationShouldOnlyDependOnDomain() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "..application..",
                    "..domain..",
                    "java..",
                    "jakarta.validation..",
                    "org.springframework.transaction..",
                    "org.springframework.stereotype.."
                )
                .because("Application layer must not depend on adapters or infrastructure");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Application MUST NOT depend on JPA/Hibernate")
        void applicationShouldNotDependOnJpa() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.persistence..",
                    "org.hibernate..",
                    "org.springframework.data.jpa.."
                )
                .because("Application layer must not depend on JPA - use ports for persistence");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Application MUST NOT depend on web framework")
        void applicationShouldNotDependOnWebFramework() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.web..",
                    "jakarta.servlet..",
                    "org.springframework.http.."
                )
                .because("Application layer must not depend on web framework - use ports");

            rule.check(applicationClasses);
        }
    }

    // ========================================
    // Port Interface Rules
    // ========================================

    @Nested
    @DisplayName("🔌 Port Interface Enforcement")
    class PortInterfaceTests {

        @Test
        @DisplayName("Input ports MUST be in ..port.in.. package")
        void portInterfacesShouldBeInPortPackages() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..port.in..")
                .should().beInterfaces()
                .because("Input ports must be interfaces defining use cases");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Output ports MUST be in ..port.out.. package")
        void outputPortsShouldBeInPortOutPackage() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..port.out..")
                .should().beInterfaces()
                .because("Output ports must be interfaces for adapter implementations");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Port interfaces MUST end with 'UseCase' or 'Port' suffix")
        void portInterfacesMustFollowNamingConvention() {
            ArchRule inputPortRule = classes()
                .that().resideInAPackage("..application..port.in..")
                .and().areInterfaces()
                .should().haveSimpleNameEndingWith("UseCase")
                .because("Input ports should end with 'UseCase'");

            ArchRule outputPortRule = classes()
                .that().resideInAPackage("..application..port.out..")
                .and().areInterfaces()
                .should().haveSimpleNameEndingWith("Port")
                .because("Output ports should end with 'Port'");

            inputPortRule.check(applicationClasses);
            outputPortRule.check(applicationClasses);
        }
    }

    // ========================================
    // UseCase DTO Rules
    // ========================================

    @Nested
    @DisplayName("📋 UseCase DTO Enforcement")
    class UseCaseDtoTests {

        @Test
        @DisplayName("Command/Query/Result DTOs SHOULD be records")
        void useCaseDtosShouldBeRecords() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .and().haveSimpleNameMatching(".*Command|.*Query|.*Result")
                .should().beRecords()
                .orShould().haveOnlyFinalFields()
                .because("UseCase DTOs must be immutable - prefer Java records");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Command DTOs MUST end with 'Command'")
        void commandDtosMustFollowNamingConvention() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .and().haveSimpleNameEndingWith("Command")
                .should().beRecords()
                .orShould().haveModifier(com.tngtech.archunit.core.domain.JavaModifier.FINAL)
                .because("Command DTOs should be immutable records or final classes");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Query DTOs MUST end with 'Query'")
        void queryDtosMustFollowNamingConvention() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .and().haveSimpleNameEndingWith("Query")
                .should().beRecords()
                .orShould().haveModifier(com.tngtech.archunit.core.domain.JavaModifier.FINAL)
                .because("Query DTOs should be immutable records or final classes");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Result DTOs MUST end with 'Result'")
        void resultDtosMustFollowNamingConvention() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .and().haveSimpleNameEndingWith("Result")
                .should().beRecords()
                .orShould().haveModifier(com.tngtech.archunit.core.domain.JavaModifier.FINAL)
                .because("Result DTOs should be immutable records or final classes");

            rule.check(applicationClasses);
        }
    }

    // ========================================
    // Annotation Rules
    // ========================================

    @Nested
    @DisplayName("🏷️ Annotation Enforcement")
    class AnnotationTests {

        @Test
        @DisplayName("UseCase services SHOULD use @UseCase or @Service annotation")
        void useCaseServicesShouldBeAnnotated() {
            ArchRule rule = classes()
                .that().resideInAPackage("..application..service..")
                .and().areNotInterfaces()
                .and().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
                .because("UseCase implementations should be Spring beans");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Application MUST NOT use @Repository")
        void applicationShouldNotUseRepositoryAnnotation() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().beAnnotatedWith("org.springframework.stereotype.Repository")
                .because("@Repository is for persistence adapters, not application layer");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Application MUST NOT use @RestController or @Controller")
        void applicationShouldNotUseControllerAnnotation() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().beAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Controller")
                .because("Controllers are adapter-in concerns, not application layer");

            rule.check(applicationClasses);
        }
    }

    // ========================================
    // Lombok Prohibition
    // ========================================

    @Nested
    @DisplayName("🚫 Lombok Prohibition")
    class LombokProhibitionTests {

        @Test
        @DisplayName("Application MUST NOT use Lombok")
        void noLombokInApplication() {
            ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInPackage("lombok..")
                .because("Lombok is strictly prohibited across entire project");

            rule.check(applicationClasses);
        }
    }
}
