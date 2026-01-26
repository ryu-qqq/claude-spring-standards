package com.ryuqq.adapter.in.rest.resourcetemplate.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.resourcetemplate.ResourceTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.CreateResourceTemplateApiRequest;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.UpdateResourceTemplateApiRequest;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.response.ResourceTemplateIdApiResponse;
import com.ryuqq.adapter.in.rest.resourcetemplate.mapper.ResourceTemplateCommandApiMapper;
import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.port.in.CreateResourceTemplateUseCase;
import com.ryuqq.application.resourcetemplate.port.in.UpdateResourceTemplateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ResourceTemplateCommandController - ResourceTemplate 생성/수정 API
 *
 * <p>리소스 템플릿 생성 및 수정 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>CTR-003: @Valid 필수 적용.
 *
 * <p>CTR-004: DELETE 메서드 금지 (소프트 삭제는 PATCH).
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>CTR-009: Controller에서 Lombok 사용 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "ResourceTemplate", description = "리소스 템플릿 관리 API")
@RestController
@RequestMapping(ResourceTemplateApiEndpoints.RESOURCE_TEMPLATES)
public class ResourceTemplateCommandController {

    private final CreateResourceTemplateUseCase createResourceTemplateUseCase;
    private final UpdateResourceTemplateUseCase updateResourceTemplateUseCase;
    private final ResourceTemplateCommandApiMapper mapper;

    /**
     * ResourceTemplateCommandController 생성자
     *
     * @param createResourceTemplateUseCase ResourceTemplate 생성 UseCase
     * @param updateResourceTemplateUseCase ResourceTemplate 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public ResourceTemplateCommandController(
            CreateResourceTemplateUseCase createResourceTemplateUseCase,
            UpdateResourceTemplateUseCase updateResourceTemplateUseCase,
            ResourceTemplateCommandApiMapper mapper) {
        this.createResourceTemplateUseCase = createResourceTemplateUseCase;
        this.updateResourceTemplateUseCase = updateResourceTemplateUseCase;
        this.mapper = mapper;
    }

    /**
     * ResourceTemplate 생성 API
     *
     * <p>새로운 리소스 템플릿을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 ResourceTemplate ID
     */
    @Operation(summary = "ResourceTemplate 생성", description = "새로운 리소스 템플릿을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Module을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ResourceTemplateIdApiResponse>> create(
            @Valid @RequestBody CreateResourceTemplateApiRequest request) {

        CreateResourceTemplateCommand command = mapper.toCommand(request);
        Long id = createResourceTemplateUseCase.execute(command);

        ResourceTemplateIdApiResponse response = ResourceTemplateIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * ResourceTemplate 수정 API
     *
     * <p>기존 리소스 템플릿의 정보를 수정합니다.
     *
     * @param resourceTemplateId ResourceTemplate ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "ResourceTemplate 수정", description = "기존 리소스 템플릿의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "ResourceTemplate을 찾을 수 없음")
    })
    @PutMapping(ResourceTemplateApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "ResourceTemplate ID", required = true)
                    @PathVariable(ResourceTemplateApiEndpoints.PATH_RESOURCE_TEMPLATE_ID)
                    Long resourceTemplateId,
            @Valid @RequestBody UpdateResourceTemplateApiRequest request) {

        UpdateResourceTemplateCommand command = mapper.toCommand(resourceTemplateId, request);
        updateResourceTemplateUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
