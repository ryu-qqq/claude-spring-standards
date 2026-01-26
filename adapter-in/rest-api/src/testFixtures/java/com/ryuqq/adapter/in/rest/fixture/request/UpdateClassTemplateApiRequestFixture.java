package com.ryuqq.adapter.in.rest.fixture.request;

import com.ryuqq.adapter.in.rest.classtemplate.dto.request.UpdateClassTemplateApiRequest;
import java.util.List;

/**
 * UpdateClassTemplateApiRequest Test Fixture
 *
 * <p>REST Docs 및 통합 테스트에서 사용하는 Request 객체 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UpdateClassTemplateApiRequestFixture {

    private UpdateClassTemplateApiRequestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 정상 요청 - 기본 케이스 */
    public static UpdateClassTemplateApiRequest valid() {
        return new UpdateClassTemplateApiRequest(
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
    public static UpdateClassTemplateApiRequest validMinimal() {
        return new UpdateClassTemplateApiRequest(
                2L, "public record {ClassName} { ... }", null, null, null, null, null, null, null);
    }

    /** 잘못된 요청 - classTypeId null */
    public static UpdateClassTemplateApiRequest invalidWithNullClassTypeId() {
        return new UpdateClassTemplateApiRequest(
                null, "code", null, null, null, null, null, null, null);
    }

    /** 잘못된 요청 - templateCode 누락 (빈 문자열) */
    public static UpdateClassTemplateApiRequest invalidWithBlankTemplateCode() {
        return new UpdateClassTemplateApiRequest(1L, "", null, null, null, null, null, null, null);
    }

    /** 잘못된 요청 - templateCode 길이 초과 (100자 초과) */
    public static UpdateClassTemplateApiRequest invalidWithLongTemplateCode() {
        return new UpdateClassTemplateApiRequest(
                1L, "A".repeat(101), null, null, null, null, null, null, null);
    }

    /** 잘못된 요청 - namingPattern 길이 초과 (200자 초과) */
    public static UpdateClassTemplateApiRequest invalidWithLongNamingPattern() {
        return new UpdateClassTemplateApiRequest(
                1L, "code", "A".repeat(201), null, null, null, null, null, null);
    }

    /** 잘못된 요청 - description 길이 초과 (2000자 초과) */
    public static UpdateClassTemplateApiRequest invalidWithLongDescription() {
        return new UpdateClassTemplateApiRequest(
                1L, "code", null, "A".repeat(2001), null, null, null, null, null);
    }

    /** 커스텀 요청 생성 */
    public static UpdateClassTemplateApiRequest custom(
            Long classTypeId,
            String templateCode,
            String namingPattern,
            String description,
            List<String> requiredAnnotations,
            List<String> forbiddenAnnotations,
            List<String> requiredInterfaces,
            List<String> forbiddenInheritance,
            List<String> requiredMethods) {
        return new UpdateClassTemplateApiRequest(
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
