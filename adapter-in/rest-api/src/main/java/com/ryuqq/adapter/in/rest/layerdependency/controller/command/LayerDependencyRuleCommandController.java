package com.ryuqq.adapter.in.rest.layerdependency.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.layerdependency.LayerDependencyRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.CreateLayerDependencyRuleApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.UpdateLayerDependencyRuleApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.response.LayerDependencyRuleIdApiResponse;
import com.ryuqq.adapter.in.rest.layerdependency.mapper.LayerDependencyRuleCommandApiMapper;
import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.port.in.CreateLayerDependencyRuleUseCase;
import com.ryuqq.application.layerdependency.port.in.UpdateLayerDependencyRuleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LayerDependencyRuleCommandController - LayerDependencyRule 생성/수정 API
 *
 * <p>Architecture의 Sub-resource로 레이어 의존성 규칙 생성 및 수정 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>CTR-003: @Valid 필수 적용.
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
@Tag(name = "LayerDependencyRule", description = "레이어 의존성 규칙 관리 API (Architecture Sub-resource)")
@RestController
@RequestMapping(LayerDependencyRuleApiEndpoints.BASE)
public class LayerDependencyRuleCommandController {

    private final CreateLayerDependencyRuleUseCase createLayerDependencyRuleUseCase;
    private final UpdateLayerDependencyRuleUseCase updateLayerDependencyRuleUseCase;
    private final LayerDependencyRuleCommandApiMapper mapper;

    /**
     * LayerDependencyRuleCommandController 생성자
     *
     * @param createLayerDependencyRuleUseCase LayerDependencyRule 생성 UseCase
     * @param updateLayerDependencyRuleUseCase LayerDependencyRule 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public LayerDependencyRuleCommandController(
            CreateLayerDependencyRuleUseCase createLayerDependencyRuleUseCase,
            UpdateLayerDependencyRuleUseCase updateLayerDependencyRuleUseCase,
            LayerDependencyRuleCommandApiMapper mapper) {
        this.createLayerDependencyRuleUseCase = createLayerDependencyRuleUseCase;
        this.updateLayerDependencyRuleUseCase = updateLayerDependencyRuleUseCase;
        this.mapper = mapper;
    }

    /**
     * LayerDependencyRule 생성 API
     *
     * <p>Architecture에 새로운 레이어 의존성 규칙을 생성합니다.
     *
     * @param architectureId Architecture ID (PathVariable)
     * @param request 생성 요청 DTO
     * @return 생성된 LayerDependencyRule ID
     */
    @Operation(
            summary = "LayerDependencyRule 생성",
            description = "Architecture에 새로운 레이어 의존성 규칙을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Architecture를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<LayerDependencyRuleIdApiResponse>> create(
            @Parameter(description = "Architecture ID", required = true)
                    @PathVariable(LayerDependencyRuleApiEndpoints.PATH_ARCHITECTURE_ID)
                    Long architectureId,
            @Valid @RequestBody CreateLayerDependencyRuleApiRequest request) {

        CreateLayerDependencyRuleCommand command = mapper.toCommand(architectureId, request);
        Long id = createLayerDependencyRuleUseCase.execute(command);

        LayerDependencyRuleIdApiResponse response = LayerDependencyRuleIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * LayerDependencyRule 수정 API
     *
     * <p>기존 레이어 의존성 규칙의 정보를 수정합니다.
     *
     * @param architectureId Architecture ID (PathVariable)
     * @param ldrId LayerDependencyRule ID (PathVariable)
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "LayerDependencyRule 수정", description = "기존 레이어 의존성 규칙의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "LayerDependencyRule을 찾을 수 없음")
    })
    @PatchMapping(LayerDependencyRuleApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Architecture ID", required = true)
                    @PathVariable(LayerDependencyRuleApiEndpoints.PATH_ARCHITECTURE_ID)
                    Long architectureId,
            @Parameter(description = "LayerDependencyRule ID", required = true)
                    @PathVariable(LayerDependencyRuleApiEndpoints.PATH_LDR_ID)
                    Long ldrId,
            @Valid @RequestBody UpdateLayerDependencyRuleApiRequest request) {

        UpdateLayerDependencyRuleCommand command = mapper.toCommand(architectureId, ldrId, request);
        updateLayerDependencyRuleUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
