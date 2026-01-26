package com.ryuqq.adapter.in.rest.layer.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.layer.LayerApiEndpoints;
import com.ryuqq.adapter.in.rest.layer.dto.request.CreateLayerApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.request.UpdateLayerApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.response.LayerIdApiResponse;
import com.ryuqq.adapter.in.rest.layer.mapper.LayerCommandApiMapper;
import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import com.ryuqq.application.layer.port.in.CreateLayerUseCase;
import com.ryuqq.application.layer.port.in.UpdateLayerUseCase;
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
 * LayerCommandController - Layer 생성/수정 API
 *
 * <p>레이어 CU(Create, Update) 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-003: @Valid 필수 적용.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "Layer", description = "레이어 관리 API")
@RestController
@RequestMapping(LayerApiEndpoints.LAYERS)
public class LayerCommandController {

    private final CreateLayerUseCase createLayerUseCase;
    private final UpdateLayerUseCase updateLayerUseCase;
    private final LayerCommandApiMapper mapper;

    /**
     * LayerCommandController 생성자
     *
     * @param createLayerUseCase Layer 생성 UseCase
     * @param updateLayerUseCase Layer 수정 UseCase
     * @param mapper API 매퍼
     */
    public LayerCommandController(
            CreateLayerUseCase createLayerUseCase,
            UpdateLayerUseCase updateLayerUseCase,
            LayerCommandApiMapper mapper) {
        this.createLayerUseCase = createLayerUseCase;
        this.updateLayerUseCase = updateLayerUseCase;
        this.mapper = mapper;
    }

    /**
     * Layer 생성 API
     *
     * <p>새로운 레이어를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Layer ID
     */
    @Operation(summary = "Layer 생성", description = "새로운 레이어를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Architecture를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 레이어 코드")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<LayerIdApiResponse>> create(
            @Valid @RequestBody CreateLayerApiRequest request) {

        CreateLayerCommand command = mapper.toCommand(request);
        Long id = createLayerUseCase.execute(command);

        LayerIdApiResponse response = LayerIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * Layer 수정 API
     *
     * <p>기존 레이어의 정보를 수정합니다.
     *
     * @param layerId Layer ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "Layer 수정", description = "기존 레이어의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Layer를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 레이어 코드")
    })
    @PutMapping(LayerApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Layer ID", required = true)
                    @PathVariable(LayerApiEndpoints.PATH_LAYER_ID)
                    Long layerId,
            @Valid @RequestBody UpdateLayerApiRequest request) {

        UpdateLayerCommand command = mapper.toCommand(layerId, request);
        updateLayerUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
