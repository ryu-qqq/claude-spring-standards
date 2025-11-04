package com.ryuqq.testing.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * Field Final 검증 ArchUnit 규칙 생성기
 *
 * <p>특정 필드 (예: ID)가 final로 선언되어 있는지 검증하는 규칙을 생성합니다.</p>
 *
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * @TestFactory
 * Stream<DynamicTest> idFieldFinalTests() {
 *     return FieldFinalCheckRules.generateIdFieldCheck("domain", domainClasses);
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public class FieldFinalCheckRules {

    /**
     * ID 필드 Final 검증 규칙 생성
     *
     * @param layer Layer 이름 (예: "domain", "application")
     * @param classes 검증할 JavaClasses
     * @return DynamicTest 스트림
     */
    public static Stream<DynamicTest> generateIdFieldCheck(String layer, JavaClasses classes) {
        return Stream.of(
            DynamicTest.dynamicTest(
                String.format("%s Layer의 ID 필드는 final이어야 함", capitalize(layer)),
                () -> checkIdFieldFinal(layer, classes)
            )
        );
    }

    /**
     * 특정 필드 Final 검증 규칙 생성 (범용)
     *
     * @param layer Layer 이름
     * @param fieldName 검증할 필드명
     * @param reason 검증 이유
     * @param classes 검증할 JavaClasses
     * @return DynamicTest 스트림
     */
    public static Stream<DynamicTest> generateFieldFinalCheck(
        String layer,
        String fieldName,
        String reason,
        JavaClasses classes
    ) {
        return Stream.of(
            DynamicTest.dynamicTest(
                String.format("%s Layer의 %s 필드는 final이어야 함", capitalize(layer), fieldName),
                () -> checkFieldFinal(layer, fieldName, reason, classes)
            )
        );
    }

    /**
     * ID 필드 Final 검증
     *
     * @param layer Layer 이름
     * @param classes 검증할 JavaClasses
     */
    private static void checkIdFieldFinal(String layer, JavaClasses classes) {
        ArchRule rule = fields()
            .that().haveName("id")
            .and().areDeclaredInClassesThat().resideInAPackage(".." + layer + "..")
            .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
            .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Fixture")
            .should().beFinal()
            .because("ID는 생성 후 변경되면 안 됩니다 (불변성 보장)");

        rule.check(classes);
    }

    /**
     * 특정 필드 Final 검증 (범용)
     *
     * @param layer Layer 이름
     * @param fieldName 필드명
     * @param reason 검증 이유
     * @param classes 검증할 JavaClasses
     */
    private static void checkFieldFinal(
        String layer,
        String fieldName,
        String reason,
        JavaClasses classes
    ) {
        ArchRule rule = fields()
            .that().haveName(fieldName)
            .and().areDeclaredInClassesThat().resideInAPackage(".." + layer + "..")
            .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
            .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Fixture")
            .should().beFinal()
            .because(reason);

        rule.check(classes);
    }

    /**
     * 첫 글자를 대문자로 변환
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Private 생성자 - 유틸리티 클래스
    private FieldFinalCheckRules() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}
