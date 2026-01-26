package com.ryuqq.adapter.in.rest.convention.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.convention.ConventionApiEndpoints;
import com.ryuqq.adapter.in.rest.convention.dto.request.CreateConventionApiRequest;
import com.ryuqq.adapter.in.rest.convention.dto.request.UpdateConventionApiRequest;
import com.ryuqq.adapter.in.rest.convention.dto.response.ConventionIdApiResponse;
import com.ryuqq.adapter.in.rest.convention.mapper.ConventionCommandApiMapper;
import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import com.ryuqq.application.convention.port.in.CreateConventionUseCase;
import com.ryuqq.application.convention.port.in.UpdateConventionUseCase;
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
 * ConventionCommandController - Convention 생성/수정 API
 *
 * <p>코딩 컨벤션 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "Convention", description = "코딩 컨벤션 관리 API")
@RestController
@RequestMapping(ConventionApiEndpoints.CONVENTIONS)
public class ConventionCommandController {

    private final CreateConventionUseCase createConventionUseCase;
    private final UpdateConventionUseCase updateConventionUseCase;
    private final ConventionCommandApiMapper mapper;

    /**
     * ConventionCommandController 생성자
     *
     * @param createConventionUseCase Convention 생성 UseCase
     * @param updateConventionUseCase Convention 수정 UseCase
     * @param mapper API 매퍼
     */
    public ConventionCommandController(
            CreateConventionUseCase createConventionUseCase,
            UpdateConventionUseCase updateConventionUseCase,
            ConventionCommandApiMapper mapper) {
        this.createConventionUseCase = createConventionUseCase;
        this.updateConventionUseCase = updateConventionUseCase;
        this.mapper = mapper;
    }

    /**
     * Convention 생성 API
     *
     * <p>새로운 코딩 컨벤션을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Convention ID
     */
    @Operation(summary = "Convention 생성", description = "새로운 코딩 컨벤션을 생성합니다.")
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
    public ResponseEntity<ApiResponse<ConventionIdApiResponse>> create(
            @Valid @RequestBody CreateConventionApiRequest request) {

        CreateConventionCommand command = mapper.toCommand(request);
        Long id = createConventionUseCase.execute(command);

        ConventionIdApiResponse response = ConventionIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * Convention 수정 API
     *
     * <p>기존 코딩 컨벤션의 정보를 수정합니다.
     *
     * @param conventionId Convention ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "Convention 수정", description = "기존 코딩 컨벤션의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Convention을 찾을 수 없음")
    })
    @PutMapping(ConventionApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Convention ID", required = true)
                    @PathVariable(ConventionApiEndpoints.PATH_CONVENTION_ID)
                    Long conventionId,
            @Valid @RequestBody UpdateConventionApiRequest request) {

        UpdateConventionCommand command = mapper.toCommand(conventionId, request);
        updateConventionUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
