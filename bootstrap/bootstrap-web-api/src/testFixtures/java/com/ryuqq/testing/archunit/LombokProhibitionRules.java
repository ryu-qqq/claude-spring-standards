package com.ryuqq.testing.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Lombok 금지 ArchUnit 규칙 생성기
 *
 * <p>레이어별 Lombok 어노테이션 사용 금지 규칙을 동적으로 생성합니다.</p>
 *
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * @TestFactory
 * Stream<DynamicTest> lombokProhibitionTests() {
 *     return LombokProhibitionRules.generate("domain", domainClasses);
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public class LombokProhibitionRules {

    /**
     * Lombok 금지 어노테이션 목록
     */
    private static final List<LombokAnnotation> LOMBOK_ANNOTATIONS = List.of(
        new LombokAnnotation("lombok.Data", "@Data", "Plain Java getter/setter 사용"),
        new LombokAnnotation("lombok.Builder", "@Builder", "Static Factory Method 사용"),
        new LombokAnnotation("lombok.Getter", "@Getter", "명시적 getter 메서드 작성"),
        new LombokAnnotation("lombok.Setter", "@Setter", "불변성 유지 (Setter 금지)"),
        new LombokAnnotation("lombok.AllArgsConstructor", "@AllArgsConstructor", "명시적 생성자 작성"),
        new LombokAnnotation("lombok.NoArgsConstructor", "@NoArgsConstructor", "명시적 기본 생성자 작성")
    );

    /**
     * Lombok 금지 규칙 동적 테스트 생성
     *
     * @param layer Layer 이름 (예: "domain", "application")
     * @param classes 검증할 JavaClasses
     * @return DynamicTest 스트림
     */
    public static Stream<DynamicTest> generate(String layer, JavaClasses classes) {
        return LOMBOK_ANNOTATIONS.stream().map(annotation ->
            DynamicTest.dynamicTest(
                String.format("%s Layer는 %s 사용 금지", capitalize(layer), annotation.simpleName),
                () -> checkLombokProhibition(layer, annotation, classes)
            )
        );
    }

    /**
     * 특정 Lombok 어노테이션 금지 검증
     *
     * @param layer Layer 이름
     * @param annotation Lombok 어노테이션 정보
     * @param classes 검증할 JavaClasses
     */
    private static void checkLombokProhibition(
        String layer,
        LombokAnnotation annotation,
        JavaClasses classes
    ) {
        ArchRule rule = noClasses()
            .that().resideInAPackage(".." + layer + "..")
            .and().haveSimpleNameNotEndingWith("Test")  // 테스트 클래스 제외
            .should().beAnnotatedWith(annotation.fullName)
            .because(String.format(
                "%s 객체는 %s를 사용하지 말고 %s해야 합니다",
                capitalize(layer),
                annotation.simpleName,
                annotation.alternative
            ));

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

    /**
     * Lombok 어노테이션 정보
     */
    private record LombokAnnotation(
        String fullName,      // 전체 클래스명 (예: "lombok.Data")
        String simpleName,    // 간단한 이름 (예: "@Data")
        String alternative    // 대안 (예: "Plain Java 사용")
    ) {}

    // Private 생성자 - 유틸리티 클래스
    private LombokProhibitionRules() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}
