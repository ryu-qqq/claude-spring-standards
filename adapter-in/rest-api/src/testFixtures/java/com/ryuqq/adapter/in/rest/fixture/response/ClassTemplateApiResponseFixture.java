package com.ryuqq.adapter.in.rest.fixture.response;

import com.ryuqq.adapter.in.rest.classtemplate.dto.response.ClassTemplateApiResponse;
import java.util.List;

/**
 * ClassTemplateApiResponse Test Fixture
 *
 * <p>REST API 단위 테스트에서 사용하는 Response 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ClassTemplateApiResponseFixture {

    private ClassTemplateApiResponseFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 응답 - 기본 케이스 */
    public static ClassTemplateApiResponse valid() {
        return new ClassTemplateApiResponse(
                1L,
                1L,
                1L,
                "public class {ClassName} { ... }",
                ".*Aggregate",
                "Aggregate Root 클래스 템플릿",
                List.of("@Entity"),
                List.of("@Data"),
                List.of(),
                List.of(),
                List.of(),
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 정상 응답 - 최소 필드만 */
    public static ClassTemplateApiResponse validMinimal() {
        return new ClassTemplateApiResponse(
                1L,
                1L,
                2L,
                "public record {ClassName} { ... }",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "2024-01-01T00:00:00Z",
                "2024-01-01T00:00:00Z");
    }

    /** 커스텀 응답 생성 */
    public static ClassTemplateApiResponse custom(
            Long classTemplateId,
            Long structureId,
            Long classTypeId,
            String templateCode,
            String namingPattern,
            String description,
            List<String> requiredAnnotations,
            List<String> forbiddenAnnotations,
            List<String> requiredInterfaces,
            List<String> forbiddenInheritance,
            List<String> requiredMethods,
            String createdAt,
            String updatedAt) {
        return new ClassTemplateApiResponse(
                classTemplateId,
                structureId,
                classTypeId,
                templateCode,
                namingPattern,
                description,
                requiredAnnotations,
                forbiddenAnnotations,
                requiredInterfaces,
                forbiddenInheritance,
                requiredMethods,
                createdAt,
                updatedAt);
    }
}
