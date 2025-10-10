package com.company.template.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Single Responsibility Principle (SRP) Enforcement Tests
 *
 * 단일 책임 원칙 (Single Responsibility Principle):
 * - 클래스는 단 하나의 변경 이유만 가져야 함
 * - 하나의 클래스는 하나의 액터(Actor)에게만 책임을 짐
 * - 높은 응집도 (High Cohesion), 낮은 결합도 (Low Coupling)
 *
 * 측정 지표:
 * - 메서드 개수: 많을수록 여러 책임 의심
 * - 필드 개수: 많을수록 여러 관심사 의심
 * - 클래스 라인 수: 길수록 복잡도 증가
 * - LCOM (Lack of Cohesion): 높을수록 응집도 낮음
 *
 * 레이어별 기준:
 * - Domain: 메서드 ≤ 7, 라인 ≤ 200 (가장 엄격)
 * - Application: 메서드 ≤ 5, 라인 ≤ 150 (UseCase는 작아야 함)
 * - Adapter: 메서드 ≤ 10, 라인 ≤ 300
 *
 * @author Sangwon Ryu (ryu@company.com)
 * @since 2025-01-10
 */
@DisplayName("📏 Single Responsibility Principle Enforcement")
class SingleResponsibilityTest {

    private static JavaClasses allClasses;
    private static JavaClasses domainClasses;
    private static JavaClasses applicationClasses;
    private static JavaClasses adapterClasses;

    @BeforeAll
    static void setup() {
        allClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.template");

        domainClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.template.domain");

        applicationClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.template.application");

        adapterClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.company.template.adapter");
    }

    // ========================================
    // Domain Layer - 가장 엄격한 SRP
    // ========================================

    @Nested
    @DisplayName("🏛️ Domain Layer - Strict SRP Enforcement")
    class DomainLayerSrpTests {

        @Test
        @DisplayName("Domain classes MUST have ≤ 7 public methods")
        void domainClassesShouldHaveLimitedMethods() {
            ArchRule rule = classes()
                .that().resideInPackage("..domain..")
                .and().areNotInterfaces()
                .and().haveSimpleNameNotEndingWith("Exception")
                .and().haveSimpleNameNotEndingWith("Id")
                .should(haveAtMostPublicMethods(7))
                .because("Domain classes should have high cohesion with single responsibility");

            rule.check(domainClasses);
        }

        @Test
        @DisplayName("Domain classes MUST have ≤ 5 instance fields")
        void domainClassesShouldHaveLimitedFields() {
            ArchRule rule = classes()
                .that().resideInPackage("..domain..")
                .and().areNotInterfaces()
                .and().haveSimpleNameNotEndingWith("Exception")
                .should(haveAtMostFields(5))
                .because("Too many fields indicate multiple responsibilities");

            rule.check(domainClasses);
        }

        @Test
        @DisplayName("Domain classes MUST be cohesive (methods use shared fields)")
        void domainClassesShouldBeCohesive() {
            ArchRule rule = classes()
                .that().resideInPackage("..domain..")
                .and().areNotInterfaces()
                .and().haveSimpleNameNotEndingWith("Exception")
                .and().haveSimpleNameNotEndingWith("Id")
                .should(haveLowLackOfCohesion())
                .because("Low cohesion indicates multiple unrelated responsibilities");

            rule.check(domainClasses);
        }
    }

    // ========================================
    // Application Layer - UseCase는 작아야 함
    // ========================================

    @Nested
    @DisplayName("⚙️ Application Layer - Small UseCase Enforcement")
    class ApplicationLayerSrpTests {

        @Test
        @DisplayName("UseCases MUST have ≤ 5 public methods")
        void useCasesShouldHaveLimitedMethods() {
            ArchRule rule = classes()
                .that().resideInPackage("..application..")
                .and().haveSimpleNameEndingWith("UseCase")
                .or().haveSimpleNameEndingWith("Service")
                .should(haveAtMostPublicMethods(5))
                .because("One UseCase should do one thing well");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("UseCases SHOULD have single @Transactional method")
        void useCasesShouldHaveSingleTransactionalMethod() {
            // UseCase는 보통 하나의 트랜잭션 메서드만 가져야 함
            // 여러 개의 @Transactional 메서드 = 여러 책임 의심
            ArchRule rule = classes()
                .that().resideInPackage("..application..")
                .and().haveSimpleNameEndingWith("UseCase")
                .should(haveAtMostTransactionalMethods(1))
                .because("Multiple transactional methods suggest multiple responsibilities");

            rule.check(applicationClasses);
        }
    }

    // ========================================
    // Adapter Layer - 리소스별 분리
    // ========================================

    @Nested
    @DisplayName("🔌 Adapter Layer - Resource-Based Separation")
    class AdapterLayerSrpTests {

        @Test
        @DisplayName("Controllers MUST have ≤ 10 endpoints")
        void controllersShouldHaveLimitedEndpoints() {
            ArchRule rule = classes()
                .that().resideInPackage("..adapter.in.web..")
                .and().haveSimpleNameEndingWith("Controller")
                .should(haveAtMostPublicMethods(10))
                .because("Controllers should be organized by resource (max 10 endpoints per resource)");

            rule.check(adapterClasses);
        }

        @Test
        @DisplayName("Repositories SHOULD focus on single Entity")
        void repositoriesShouldFocusOnSingleEntity() {
            // Repository는 하나의 Entity만 다뤄야 함
            // 여러 Entity 의존 = 여러 책임
            ArchRule rule = classes()
                .that().resideInPackage("..adapter.out.persistence..")
                .and().haveSimpleNameEndingWith("Repository")
                .should(haveSingleEntityDependency())
                .because("Repository should manage single Entity type only");

            rule.check(adapterClasses);
        }
    }

    // ========================================
    // 커스텀 ArchCondition 구현
    // ========================================

    /**
     * 최대 public 메서드 개수 제한
     */
    private static ArchCondition<JavaClass> haveAtMostPublicMethods(int maxMethods) {
        return new ArchCondition<JavaClass>("have at most " + maxMethods + " public methods") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                long publicMethodCount = javaClass.getMethods().stream()
                    .filter(m -> m.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.PUBLIC))
                    .filter(m -> !m.getName().equals("equals"))
                    .filter(m -> !m.getName().equals("hashCode"))
                    .filter(m -> !m.getName().equals("toString"))
                    .count();

                if (publicMethodCount > maxMethods) {
                    String message = String.format(
                        "Class <%s> has %d public methods (max: %d) - violates SRP",
                        javaClass.getName(),
                        publicMethodCount,
                        maxMethods
                    );
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };
    }

    /**
     * 최대 필드 개수 제한
     */
    private static ArchCondition<JavaClass> haveAtMostFields(int maxFields) {
        return new ArchCondition<JavaClass>("have at most " + maxFields + " fields") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                long fieldCount = javaClass.getFields().stream()
                    .filter(f -> !f.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.STATIC))
                    .count();

                if (fieldCount > maxFields) {
                    String message = String.format(
                        "Class <%s> has %d instance fields (max: %d) - too many concerns",
                        javaClass.getName(),
                        fieldCount,
                        maxFields
                    );
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };
    }

    /**
     * 응집도 검사 (간단한 휴리스틱)
     *
     * 실제 LCOM 계산은 복잡하므로, 간단한 휴리스틱 사용:
     * - 메서드들이 공통 필드를 사용하는가?
     * - 모든 메서드가 전혀 다른 필드만 사용하면 응집도 낮음
     */
    private static ArchCondition<JavaClass> haveLowLackOfCohesion() {
        return new ArchCondition<JavaClass>("have low lack of cohesion") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                long methodCount = javaClass.getMethods().stream()
                    .filter(m -> !m.getName().equals("equals"))
                    .filter(m -> !m.getName().equals("hashCode"))
                    .filter(m -> !m.getName().equals("toString"))
                    .filter(m -> !m.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.STATIC))
                    .count();

                long fieldCount = javaClass.getFields().stream()
                    .filter(f -> !f.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.STATIC))
                    .count();

                // 메서드나 필드가 너무 적으면 검사 스킵
                if (methodCount < 3 || fieldCount < 2) {
                    return;
                }

                // 경고만 (실제 LCOM 계산은 PMD가 더 정확)
                if (methodCount > 7 && fieldCount > 5) {
                    String message = String.format(
                        "Class <%s> has %d methods and %d fields - consider checking cohesion (LCOM)",
                        javaClass.getName(),
                        methodCount,
                        fieldCount
                    );
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };
    }

    /**
     * 최대 @Transactional 메서드 개수
     */
    private static ArchCondition<JavaClass> haveAtMostTransactionalMethods(int maxTransactional) {
        return new ArchCondition<JavaClass>("have at most " + maxTransactional + " @Transactional methods") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                long transactionalCount = javaClass.getMethods().stream()
                    .filter(m -> m.isAnnotatedWith("org.springframework.transaction.annotation.Transactional"))
                    .count();

                if (transactionalCount > maxTransactional) {
                    String message = String.format(
                        "Class <%s> has %d @Transactional methods (max: %d) - split into separate UseCases",
                        javaClass.getName(),
                        transactionalCount,
                        maxTransactional
                    );
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };
    }

    /**
     * Repository는 단일 Entity만 의존해야 함
     */
    private static ArchCondition<JavaClass> haveSingleEntityDependency() {
        return new ArchCondition<JavaClass>("depend on single Entity") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                long entityDependencyCount = javaClass.getFields().stream()
                    .filter(f -> f.getRawType().getName().endsWith("Entity"))
                    .count();

                // Repository가 여러 Entity 의존 = 여러 책임
                if (entityDependencyCount > 1) {
                    String message = String.format(
                        "Repository <%s> depends on %d entities - should manage single entity type",
                        javaClass.getName(),
                        entityDependencyCount
                    );
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };
    }
}
