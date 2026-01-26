package com.ryuqq.adapter.in.rest.classtemplate.controller.query;

import com.ryuqq.adapter.in.rest.classtemplate.ClassTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.classtemplate.dto.request.SearchClassTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.response.ClassTemplateApiResponse;
import com.ryuqq.adapter.in.rest.classtemplate.mapper.ClassTemplateQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.application.classtemplate.port.in.SearchClassTemplatesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassTemplateQueryController - ClassTemplate 조회 API
 *
 * <p>클래스 템플릿 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/class-templates).
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "ClassTemplate", description = "클래스 템플릿 조회 API")
@RestController
@RequestMapping(ClassTemplateApiEndpoints.CLASS_TEMPLATES)
public class ClassTemplateQueryController {

    private final SearchClassTemplatesByCursorUseCase searchClassTemplatesByCursorUseCase;
    private final ClassTemplateQueryApiMapper mapper;

    /**
     * ClassTemplateQueryController 생성자
     *
     * @param searchClassTemplatesByCursorUseCase ClassTemplate 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public ClassTemplateQueryController(
            SearchClassTemplatesByCursorUseCase searchClassTemplatesByCursorUseCase,
            ClassTemplateQueryApiMapper mapper) {
        this.searchClassTemplatesByCursorUseCase = searchClassTemplatesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * ClassTemplate 복합 조건 조회 API (커서 기반)
     *
     * <p>ClassTemplate 목록을 커서 기반으로 조회합니다. 패키지 구조 ID 및 클래스 타입 필터링을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, structureIds, classTypes 필터 포함)
     * @return ClassTemplate 슬라이스 목록
     */
    @Operation(
            summary = "ClassTemplate 복합 조건 조회",
            description = "ClassTemplate 목록을 커서 기반으로 조회합니다. 패키지 구조 ID 및 클래스 타입 필터링을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ClassTemplateApiResponse>>>
            searchClassTemplatesByCursor(@Valid SearchClassTemplatesCursorApiRequest request) {

        ClassTemplateSearchParams searchParams = mapper.toSearchParams(request);
        ClassTemplateSliceResult sliceResult =
                searchClassTemplatesByCursorUseCase.execute(searchParams);
        SliceApiResponse<ClassTemplateApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
