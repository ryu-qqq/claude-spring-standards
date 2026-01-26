package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtemplate.dto.request.CreateClassTemplateApiRequest;
import java.util.List;

/**
 * CreateClassTemplateApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CreateClassTemplateApiRequestFixture {

    private CreateClassTemplateApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static CreateClassTemplateApiRequest valid() {
        return new CreateClassTemplateApiRequest(
                1L,
                1L,
                "public class {ClassName} { ... }",
                ".*Aggregate",
                "Aggregate Root 클래스 템플릿",
                List.of("@Entity"),
                List.of("@Data"),
                List.of(),
                List.of(),
                List.of());
    }

    /** 정상 요청 - 최소 필수 필드만 */
    public static CreateClassTemplateApiRequest validMinimal() {
        return new CreateClassTemplateApiRequest(
                1L,
                2L,
                "public record {ClassName} { ... }",
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    /** 잘못된 요청 - structureId null */
    public static CreateClassTemplateApiRequest invalidWithNullStructureId() {
        return new CreateClassTemplateApiRequest(
                null,
                1L,
                "public class {ClassName} { ... }",
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    /** 잘못된 요청 - classTypeId null */
    public static CreateClassTemplateApiRequest invalidWithNullClassTypeId() {
        return new CreateClassTemplateApiRequest(
                1L,
                null,
                "public class {ClassName} { ... }",
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    /** 잘못된 요청 - templateCode 누락 (빈 문자열) */
    public static CreateClassTemplateApiRequest invalidWithBlankTemplateCode() {
        return new CreateClassTemplateApiRequest(
                1L, 1L, "", null, null, null, null, null, null, null);
    }

    /** 잘못된 요청 - templateCode 길이 초과 (100자 초과) */
    public static CreateClassTemplateApiRequest invalidWithLongTemplateCode() {
        String longCode = "A".repeat(101);
        return new CreateClassTemplateApiRequest(
                1L, 1L, longCode, null, null, null, null, null, null, null);
    }

    /** 잘못된 요청 - namingPattern 길이 초과 (200자 초과) */
    public static CreateClassTemplateApiRequest invalidWithLongNamingPattern() {
        String longPattern = "A".repeat(201);
        return new CreateClassTemplateApiRequest(
                1L,
                1L,
                "public class {ClassName} { ... }",
                longPattern,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    /** 잘못된 요청 - description 길이 초과 (2000자 초과) */
    public static CreateClassTemplateApiRequest invalidWithLongDescription() {
        String longDescription = "A".repeat(2001);
        return new CreateClassTemplateApiRequest(
                1L,
                1L,
                "public class {ClassName} { ... }",
                null,
                longDescription,
                null,
                null,
                null,
                null,
                null);
    }

    /** 커스텀 요청 생성 */
    public static CreateClassTemplateApiRequest custom(
            Long structureId,
            Long classTypeId,
            String templateCode,
            String namingPattern,
            String description,
            List<String> requiredAnnotations,
            List<String> forbiddenAnnotations,
            List<String> requiredInterfaces,
            List<String> forbiddenInheritance,
            List<String> requiredMethods) {
        return new CreateClassTemplateApiRequest(
                structureId,
                classTypeId,
                templateCode,
                namingPattern,
                description,
                requiredAnnotations,
                forbiddenAnnotations,
                requiredInterfaces,
                forbiddenInheritance,
                requiredMethods);
    }
}
