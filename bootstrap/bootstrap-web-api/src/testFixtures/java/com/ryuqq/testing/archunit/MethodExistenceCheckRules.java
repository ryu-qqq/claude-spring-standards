package com.ryuqq.testing.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * 메서드 존재 검증 ArchUnit 규칙 생성기
 *
 * <p>Domain Entity에 필수 메서드 (reconstitute(), getIdValue())가 존재하는지 검증하는 규칙을 생성합니다.</p>
 *
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * @TestFactory
 * Stream<DynamicTest> methodExistenceTests() {
 *     return Stream.concat(
 *         MethodExistenceCheckRules.generateReconstituteCheck("domain", domainClasses),
 *         MethodExistenceCheckRules.generateGetIdValueCheck("domain", domainClasses)
 *     );
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public class MethodExistenceCheckRules {

    /**
     * reconstitute() 메서드 존재 검증 규칙 생성
     *
     * <p>Domain 엔티티는 DB에서 복원 시 사용하는 static reconstitute() 메서드를 반드시 가져야 합니다.</p>
     *
     * @param layer Layer 이름 (예: "domain")
     * @param classes 검증할 JavaClasses
     * @return DynamicTest 스트림
     */
    public static Stream<DynamicTest> generateReconstituteCheck(String layer, JavaClasses classes) {
        return Stream.of(
            DynamicTest.dynamicTest(
                String.format("%s Layer 엔티티는 reconstitute() 메서드를 가져야 함", capitalize(layer)),
                () -> checkReconstituteExists(layer, classes)
            )
        );
    }

    /**
     * getIdValue() 메서드 존재 검증 규칙 생성
     *
     * <p>Domain 엔티티는 Law of Demeter 준수를 위해 getIdValue() 메서드를 반드시 가져야 합니다.</p>
     *
     * @param layer Layer 이름 (예: "domain")
     * @param classes 검증할 JavaClasses
     * @return DynamicTest 스트림
     */
    public static Stream<DynamicTest> generateGetIdValueCheck(String layer, JavaClasses classes) {
        return Stream.of(
            DynamicTest.dynamicTest(
                String.format("%s Layer 엔티티는 getIdValue() 메서드를 가져야 함", capitalize(layer)),
                () -> checkGetIdValueExists(layer, classes)
            )
        );
    }

    /**
     * 특정 메서드 존재 검증 규칙 생성 (범용)
     *
     * @param layer Layer 이름
     * @param methodName 검증할 메서드명
     * @param isStatic Static 메서드 여부
     * @param reason 검증 이유
     * @param classes 검증할 JavaClasses
     * @return DynamicTest 스트림
     */
    public static Stream<DynamicTest> generateMethodExistenceCheck(
        String layer,
        String methodName,
        boolean isStatic,
        String reason,
        JavaClasses classes
    ) {
        return Stream.of(
            DynamicTest.dynamicTest(
                String.format("%s Layer는 %s() 메서드를 가져야 함", capitalize(layer), methodName),
                () -> checkMethodExists(layer, methodName, isStatic, reason, classes)
            )
        );
    }

    /**
     * reconstitute() 메서드 존재 검증
     *
     * @param layer Layer 이름
     * @param classes 검증할 JavaClasses
     */
    private static void checkReconstituteExists(String layer, JavaClasses classes) {
        ArchRule rule = classes()
            .that().resideInAPackage(".." + layer + "..")
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotEndingWith("Fixture")
            .and().haveSimpleNameNotEndingWith("Id")  // ID Value Object 제외
            .should(new ArchCondition<JavaClass>("have static reconstitute() method") {
                @Override
                public void check(JavaClass item, ConditionEvents events) {
                    boolean hasMethod = item.getMethods().stream()
                        .anyMatch(method ->
                            method.getName().equals("reconstitute") &&
                            method.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.STATIC)
                        );

                    if (!hasMethod) {
                        String message = String.format(
                            "%s does not have static reconstitute() method",
                            item.getName()
                        );
                        events.add(SimpleConditionEvent.violated(item, message));
                    }
                }
            })
            .because("Domain 엔티티는 DB에서 복원 시 사용하는 reconstitute() 메서드를 가져야 합니다");

        rule.check(classes);
    }

    /**
     * getIdValue() 메서드 존재 검증
     *
     * @param layer Layer 이름
     * @param classes 검증할 JavaClasses
     */
    private static void checkGetIdValueExists(String layer, JavaClasses classes) {
        ArchRule rule = classes()
            .that().resideInAPackage(".." + layer + "..")
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotEndingWith("Fixture")
            .and().haveSimpleNameNotEndingWith("Id")  // ID Value Object 제외
            .should(new ArchCondition<JavaClass>("have getIdValue() method") {
                @Override
                public void check(JavaClass item, ConditionEvents events) {
                    boolean hasMethod = item.getMethods().stream()
                        .anyMatch(method -> method.getName().equals("getIdValue"));

                    if (!hasMethod) {
                        String message = String.format(
                            "%s does not have getIdValue() method (Law of Demeter 위반)",
                            item.getName()
                        );
                        events.add(SimpleConditionEvent.violated(item, message));
                    }
                }
            })
            .because("Domain 엔티티는 Law of Demeter 준수를 위해 getIdValue() 메서드를 가져야 합니다");

        rule.check(classes);
    }

    /**
     * 특정 메서드 존재 검증 (범용)
     *
     * @param layer Layer 이름
     * @param methodName 메서드명
     * @param isStatic Static 메서드 여부
     * @param reason 검증 이유
     * @param classes 검증할 JavaClasses
     */
    private static void checkMethodExists(
        String layer,
        String methodName,
        boolean isStatic,
        String reason,
        JavaClasses classes
    ) {
        ArchRule rule = classes()
            .that().resideInAPackage(".." + layer + "..")
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotEndingWith("Fixture")
            .should(new ArchCondition<JavaClass>("have " + methodName + "() method") {
                @Override
                public void check(JavaClass item, ConditionEvents events) {
                    boolean hasMethod = item.getMethods().stream()
                        .anyMatch(method -> {
                            boolean nameMatches = method.getName().equals(methodName);
                            if (!isStatic) {
                                return nameMatches;
                            }
                            return nameMatches &&
                                method.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.STATIC);
                        });

                    if (!hasMethod) {
                        String staticModifier = isStatic ? "static " : "";
                        String message = String.format(
                            "%s does not have %s%s() method",
                            item.getName(),
                            staticModifier,
                            methodName
                        );
                        events.add(SimpleConditionEvent.violated(item, message));
                    }
                }
            })
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
    private MethodExistenceCheckRules() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}
