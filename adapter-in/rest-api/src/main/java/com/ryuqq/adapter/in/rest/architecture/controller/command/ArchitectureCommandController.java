package com.ryuqq.adapter.in.rest.architecture.controller.command;

import com.ryuqq.adapter.in.rest.architecture.ArchitectureApiEndpoints;
import com.ryuqq.adapter.in.rest.architecture.dto.request.CreateArchitectureApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.request.UpdateArchitectureApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.response.ArchitectureIdApiResponse;
import com.ryuqq.adapter.in.rest.architecture.mapper.ArchitectureCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import com.ryuqq.application.architecture.port.in.CreateArchitectureUseCase;
import com.ryuqq.application.architecture.port.in.UpdateArchitectureUseCase;
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
 * ArchitectureCommandController - Architecture 생성/수정 API
 *
 * <p>아키텍처 생성/수정 엔드포인트를 제공합니다.
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
@Tag(name = "Architecture", description = "아키텍처 관리 API")
@RestController
@RequestMapping(ArchitectureApiEndpoints.ARCHITECTURES)
public class ArchitectureCommandController {

    private final CreateArchitectureUseCase createArchitectureUseCase;
    private final UpdateArchitectureUseCase updateArchitectureUseCase;
    private final ArchitectureCommandApiMapper mapper;

    /**
     * ArchitectureCommandController 생성자
     *
     * @param createArchitectureUseCase Architecture 생성 UseCase
     * @param updateArchitectureUseCase Architecture 수정 UseCase
     * @param mapper API 매퍼
     */
    public ArchitectureCommandController(
            CreateArchitectureUseCase createArchitectureUseCase,
            UpdateArchitectureUseCase updateArchitectureUseCase,
            ArchitectureCommandApiMapper mapper) {
        this.createArchitectureUseCase = createArchitectureUseCase;
        this.updateArchitectureUseCase = updateArchitectureUseCase;
        this.mapper = mapper;
    }

    /**
     * Architecture 생성 API
     *
     * <p>새로운 아키텍처를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Architecture ID
     */
    @Operation(summary = "Architecture 생성", description = "새로운 아키텍처를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "TechStack을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ArchitectureIdApiResponse>> create(
            @Valid @RequestBody CreateArchitectureApiRequest request) {

        CreateArchitectureCommand command = mapper.toCommand(request);
        Long id = createArchitectureUseCase.execute(command);

        ArchitectureIdApiResponse response = ArchitectureIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * Architecture 수정 API
     *
     * <p>기존 아키텍처의 정보를 수정합니다.
     *
     * @param architectureId Architecture ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "Architecture 수정", description = "기존 아키텍처의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Architecture를 찾을 수 없음")
    })
    @PutMapping(ArchitectureApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Architecture ID", required = true)
                    @PathVariable(ArchitectureApiEndpoints.PATH_ARCHITECTURE_ID)
                    Long architectureId,
            @Valid @RequestBody UpdateArchitectureApiRequest request) {

        UpdateArchitectureCommand command = mapper.toCommand(architectureId, request);
        updateArchitectureUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
