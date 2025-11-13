package com.ryuqq.domain.architecture.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.common.exception.ErrorCode;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Exception ArchUnit 아키텍처 검증 테스트
 *
 * <p><strong>검증 규칙</strong>:</p>
 * <ul>
 *   <li>ErrorCode Enum은 ErrorCode 인터페이스 구현 필수</li>
 *   <li>ErrorCode 형식: {BC}-{3자리 숫자}</li>
 *   <li>Concrete Exception은 DomainException 상속 필수</li>
 *   <li>Lombok, JPA, Spring 어노테이션 금지</li>
 *   <li>패키지 위치: domain.[bc].exception</li>
 *   <li>IllegalArgumentException은 생성자/팩토리 메서드에서만 사용</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("architecture")
@Tag("domain")
@Tag("exception")
@DisplayName("Exception 아키텍처 검증 테스트")
class ExceptionArchTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages("com.ryuqq.domain");
    }

    // ==================== ErrorCode Enum 규칙 ====================

    /**
     * 규칙 1: ErrorCode Enum은 ErrorCode 인터페이스를 구현해야 한다
     */
    @Test
    @DisplayName("[필수] ErrorCode Enum은 ErrorCode 인터페이스를 구현해야 한다")
    void errorCodeEnums_ShouldImplementErrorCodeInterface() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should().implement(ErrorCode.class)
            .because("ErrorCode Enum은 ErrorCode 인터페이스를 구현해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 2: ErrorCode Enum은 domain.[bc].exception 패키지에 위치해야 한다
     */
    @Test
    @DisplayName("[필수] ErrorCode Enum은 domain.[bc].exception 패키지에 위치해야 한다")
    void errorCodeEnums_ShouldBeInExceptionPackage() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should().resideInAPackage("..domain..exception..")
            .because("ErrorCode Enum은 domain.[bc].exception 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 3: ErrorCode Enum은 Lombok 어노테이션을 사용하지 않아야 한다
     */
    @Test
    @DisplayName("[금지] ErrorCode Enum은 Lombok 어노테이션을 사용하지 않아야 한다")
    void errorCodeEnums_ShouldNotUseLombok() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should().beAnnotatedWith("lombok.Getter")
            .orShould().beAnnotatedWith("lombok.AllArgsConstructor")
            .orShould().beAnnotatedWith("lombok.RequiredArgsConstructor")
            .because("ErrorCode Enum은 Lombok을 사용하지 않고 Pure Java Enum으로 구현해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 4: ErrorCode Enum은 public이어야 한다
     */
    @Test
    @DisplayName("[필수] ErrorCode Enum은 public이어야 한다")
    void errorCodeEnums_ShouldBePublic() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should().bePublic()
            .because("ErrorCode Enum은 다른 레이어에서 사용되기 위해 public이어야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 5: ErrorCode Enum은 getCode() 메서드를 가져야 한다
     */
    @Test
    @DisplayName("[필수] ErrorCode Enum은 getCode() 메서드를 가져야 한다")
    void errorCodeEnums_ShouldHaveGetCodeMethod() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should(haveMethodWithName("getCode"))
            .because("ErrorCode Enum은 getCode() 메서드를 구현해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 6: ErrorCode Enum은 getHttpStatus() 메서드를 가져야 한다
     */
    @Test
    @DisplayName("[필수] ErrorCode Enum은 getHttpStatus() 메서드를 가져야 한다")
    void errorCodeEnums_ShouldHaveGetHttpStatusMethod() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should(haveMethodWithName("getHttpStatus"))
            .because("ErrorCode Enum은 getHttpStatus() 메서드를 구현해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 7: ErrorCode Enum은 getMessage() 메서드를 가져야 한다
     */
    @Test
    @DisplayName("[필수] ErrorCode Enum은 getMessage() 메서드를 가져야 한다")
    void errorCodeEnums_ShouldHaveGetMessageMethod() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should(haveMethodWithName("getMessage"))
            .because("ErrorCode Enum은 getMessage() 메서드를 구현해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 8: ErrorCode Enum은 HttpStatus를 의존해야 한다
     */
    @Test
    @DisplayName("[필수] ErrorCode Enum은 HttpStatus를 의존해야 한다")
    void errorCodeEnums_ShouldDependOnHttpStatus() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("ErrorCode")
            .and().areEnums()
            .should().dependOnClassesThat().areAssignableTo(HttpStatus.class)
            .because("ErrorCode Enum은 HTTP 상태 코드 매핑을 위해 HttpStatus를 사용해야 합니다");

        rule.check(classes);
    }

    // ==================== Concrete Exception 클래스 규칙 ====================

    /**
     * 규칙 9: Concrete Exception 클래스는 DomainException을 상속해야 한다
     */
    @Test
    @DisplayName("[필수] Concrete Exception 클래스는 DomainException을 상속해야 한다")
    void concreteExceptions_ShouldExtendDomainException() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .and().doNotHaveSimpleName("DomainException")
            .should().beAssignableTo(DomainException.class)
            .because("Concrete Exception 클래스는 DomainException을 상속해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 10: Concrete Exception 클래스는 domain.[bc].exception 패키지에 위치해야 한다
     */
    @Test
    @DisplayName("[필수] Concrete Exception 클래스는 domain.[bc].exception 패키지에 위치해야 한다")
    void concreteExceptions_ShouldBeInExceptionPackage() {
        ArchRule rule = classes()
            .that().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .and().doNotHaveSimpleName("DomainException")
            .and().resideInAPackage("..domain..")
            .should().resideInAPackage("..domain..exception..")
            .because("Concrete Exception 클래스는 domain.[bc].exception 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 11: Concrete Exception 클래스는 Lombok 어노테이션을 사용하지 않아야 한다
     */
    @Test
    @DisplayName("[금지] Concrete Exception 클래스는 Lombok 어노테이션을 사용하지 않아야 한다")
    void concreteExceptions_ShouldNotUseLombok() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .should().beAnnotatedWith("lombok.Data")
            .orShould().beAnnotatedWith("lombok.Builder")
            .orShould().beAnnotatedWith("lombok.Getter")
            .orShould().beAnnotatedWith("lombok.AllArgsConstructor")
            .orShould().beAnnotatedWith("lombok.NoArgsConstructor")
            .because("Concrete Exception 클래스는 Lombok을 사용하지 않고 Pure Java로 구현해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 12: Concrete Exception 클래스는 JPA 어노테이션을 사용하지 않아야 한다
     */
    @Test
    @DisplayName("[금지] Concrete Exception 클래스는 JPA 어노테이션을 사용하지 않아야 한다")
    void concreteExceptions_ShouldNotUseJPA() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .should().beAnnotatedWith("jakarta.persistence.Entity")
            .orShould().beAnnotatedWith("jakarta.persistence.Table")
            .because("Concrete Exception 클래스는 JPA 어노테이션을 사용하지 않아야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 13: Concrete Exception 클래스는 Spring 어노테이션을 사용하지 않아야 한다
     */
    @Test
    @DisplayName("[금지] Concrete Exception 클래스는 Spring 어노테이션을 사용하지 않아야 한다")
    void concreteExceptions_ShouldNotUseSpring() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .should().beAnnotatedWith("org.springframework.stereotype.Component")
            .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
            .because("Concrete Exception 클래스는 Spring 어노테이션을 사용하지 않아야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 14: Concrete Exception 클래스는 public이어야 한다
     */
    @Test
    @DisplayName("[필수] Concrete Exception 클래스는 public이어야 한다")
    void concreteExceptions_ShouldBePublic() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .and().doNotHaveSimpleName("DomainException")
            .should().bePublic()
            .because("Concrete Exception 클래스는 다른 레이어에서 사용되기 위해 public이어야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 15: Concrete Exception 클래스는 RuntimeException을 상속해야 한다 (DomainException을 통해)
     */
    @Test
    @DisplayName("[필수] Concrete Exception 클래스는 RuntimeException을 상속해야 한다")
    void concreteExceptions_ShouldExtendRuntimeException() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .and().doNotHaveSimpleName("DomainException")
            .should().beAssignableTo(RuntimeException.class)
            .because("Concrete Exception 클래스는 RuntimeException을 상속해야 합니다 (Checked Exception 금지)");

        rule.check(classes);
    }

    // ==================== DomainException 기본 클래스 규칙 ====================

    /**
     * 규칙 16: DomainException은 RuntimeException을 상속해야 한다
     */
    @Test
    @DisplayName("[필수] DomainException은 RuntimeException을 상속해야 한다")
    void domainException_ShouldExtendRuntimeException() {
        ArchRule rule = classes()
            .that().haveSimpleName("DomainException")
            .and().resideInAPackage("..domain.common.exception")
            .should().beAssignableTo(RuntimeException.class)
            .because("DomainException은 RuntimeException을 상속해야 합니다");

        rule.check(classes);
    }

    /**
     * 규칙 17: DomainException은 domain.common.exception 패키지에 위치해야 한다
     */
    @Test
    @DisplayName("[필수] DomainException은 domain.common.exception 패키지에 위치해야 한다")
    void domainException_ShouldBeInCommonExceptionPackage() {
        ArchRule rule = classes()
            .that().haveSimpleName("DomainException")
            .should().resideInAPackage("..domain.common.exception")
            .because("DomainException은 domain.common.exception 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    // ==================== 레이어 의존성 규칙 ====================

    /**
     * 규칙 18: Exception은 Application/Adapter 레이어에 의존하지 않아야 한다
     */
    @Test
    @DisplayName("[필수] Exception은 Application/Adapter 레이어에 의존하지 않아야 한다")
    void exceptions_ShouldNotDependOnOuterLayers() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("..domain..exception..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..application..",
                "..adapter.."
            )
            .because("Domain Exception은 Application/Adapter 레이어에 의존하지 않아야 합니다 (헥사고날 아키텍처)");

        rule.check(classes);
    }

    /**
     * 규칙 19: Exception은 domain 패키지 내에서만 사용되어야 한다 (VO, Aggregate에서 throw)
     */
    @Test
    @DisplayName("[권장] Domain Exception은 domain 패키지 내에서만 throw되어야 한다")
    void domainExceptions_ShouldBeThrownFromDomainOnly() {
        // Note: 이 규칙은 정적 분석으로 완벽히 검증하기 어려우므로, 코드 리뷰 시 확인 필요
        // ArchUnit으로는 메서드 호출 시점의 throw 위치까지 추적이 제한적

        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .should().onlyBeAccessed().byAnyPackage(
                "..domain..",
                "..adapter.." // GlobalExceptionHandler는 adapter layer에 위치
            )
            .because("Domain Exception은 Domain layer에서 throw되고, Adapter layer의 GlobalExceptionHandler에서 처리됩니다");

        rule.check(classes);
    }

    // ==================== 네이밍 규칙 ====================

    /**
     * 규칙 20: Concrete Exception 네이밍은 명확한 의미를 가져야 한다
     */
    @Test
    @DisplayName("[권장] Concrete Exception 네이밍은 명확한 의미를 가져야 한다")
    void concreteExceptions_ShouldHaveMeaningfulNames() {
        ArchRule rule = classes()
            .that().resideInAPackage("..domain..exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().haveSimpleNameNotContaining("Test")
            .and().areNotInterfaces()
            .and().doNotHaveSimpleName("DomainException")
            .should().haveSimpleNameMatching(".*(?:NotFound|Invalid|Already|Cannot|Failed|Exceeded|Unsupported).*Exception")
            .because("Concrete Exception 이름은 명확한 의미를 가져야 합니다 (예: OrderNotFoundException, InvalidOrderStatusException)");

        rule.check(classes);
    }

    // ==================== 커스텀 ArchCondition 헬퍼 메서드 ====================

    /**
     * 클래스가 특정 이름의 메서드를 가지고 있는지 검증
     */
    private static ArchCondition<JavaClass> haveMethodWithName(String methodName) {
        return new ArchCondition<JavaClass>("have method with name " + methodName) {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean hasMethod = javaClass.getAllMethods().stream()
                    .anyMatch(method -> method.getName().equals(methodName));

                if (!hasMethod) {
                    String message = String.format(
                        "Class %s does not have a method named '%s'",
                        javaClass.getName(), methodName
                    );
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };
    }
}
