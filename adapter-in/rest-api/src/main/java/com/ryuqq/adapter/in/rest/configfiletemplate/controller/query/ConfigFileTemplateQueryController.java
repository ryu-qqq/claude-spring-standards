package com.ryuqq.adapter.in.rest.configfiletemplate.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.configfiletemplate.ConfigFileTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.SearchConfigFileTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.response.ConfigFileTemplateApiResponse;
import com.ryuqq.adapter.in.rest.configfiletemplate.mapper.ConfigFileTemplateQueryApiMapper;
import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;
import com.ryuqq.application.configfiletemplate.port.in.SearchConfigFileTemplatesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ConfigFileTemplateQueryController - ConfigFileTemplate 조회 API
 *
 * <p>설정 파일 템플릿 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 처리.
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "ConfigFileTemplate", description = "AI 도구 설정 파일 템플릿 관리 API")
@RestController
@RequestMapping(ConfigFileTemplateApiEndpoints.CONFIG_FILES)
public class ConfigFileTemplateQueryController {

    private final SearchConfigFileTemplatesByCursorUseCase searchConfigFileTemplatesByCursorUseCase;
    private final ConfigFileTemplateQueryApiMapper mapper;

    /**
     * ConfigFileTemplateQueryController 생성자
     *
     * @param searchConfigFileTemplatesByCursorUseCase ConfigFileTemplate 복합 조건 조회 UseCase
     * @param mapper API 매퍼
     */
    public ConfigFileTemplateQueryController(
            SearchConfigFileTemplatesByCursorUseCase searchConfigFileTemplatesByCursorUseCase,
            ConfigFileTemplateQueryApiMapper mapper) {
        this.searchConfigFileTemplatesByCursorUseCase = searchConfigFileTemplatesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * ConfigFileTemplate 복합 조건 조회 API (커서 기반)
     *
     * <p>도구 타입, TechStack ID, Architecture ID, 카테고리 필터를 지원하여 설정 파일 템플릿 목록을 커서 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (커서 기반, 필터 포함)
     * @return ConfigFileTemplate 슬라이스 목록
     */
    @Operation(
            summary = "ConfigFileTemplate 복합 조건 조회",
            description =
                    "도구 타입, TechStack ID, Architecture ID, 카테고리 필터를 지원하여 "
                            + "설정 파일 템플릿 목록을 커서 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ConfigFileTemplateApiResponse>>>
            searchConfigFileTemplatesByCursor(
                    @Valid SearchConfigFileTemplatesCursorApiRequest request) {

        ConfigFileTemplateSearchParams searchParams = mapper.toSearchParams(request);
        ConfigFileTemplateSliceResult sliceResult =
                searchConfigFileTemplatesByCursorUseCase.execute(searchParams);
        SliceApiResponse<ConfigFileTemplateApiResponse> response =
                mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
