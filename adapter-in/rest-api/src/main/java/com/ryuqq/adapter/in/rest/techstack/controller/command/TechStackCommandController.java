package com.ryuqq.adapter.in.rest.techstack.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.techstack.TechStackApiEndpoints;
import com.ryuqq.adapter.in.rest.techstack.dto.request.CreateTechStackApiRequest;
import com.ryuqq.adapter.in.rest.techstack.dto.request.UpdateTechStackApiRequest;
import com.ryuqq.adapter.in.rest.techstack.dto.response.TechStackIdApiResponse;
import com.ryuqq.adapter.in.rest.techstack.mapper.TechStackCommandApiMapper;
import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import com.ryuqq.application.techstack.port.in.CreateTechStackUseCase;
import com.ryuqq.application.techstack.port.in.UpdateTechStackUseCase;
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
 * TechStackCommandController - TechStack 생성/수정 API
 *
 * <p>기술 스택 CU(Create, Update) 엔드포인트를 제공합니다.
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
@Tag(name = "TechStack", description = "기술 스택 관리 API")
@RestController
@RequestMapping(TechStackApiEndpoints.TECH_STACKS)
public class TechStackCommandController {

    private final CreateTechStackUseCase createTechStackUseCase;
    private final UpdateTechStackUseCase updateTechStackUseCase;
    private final TechStackCommandApiMapper mapper;

    /**
     * TechStackCommandController 생성자
     *
     * @param createTechStackUseCase TechStack 생성 UseCase
     * @param updateTechStackUseCase TechStack 수정 UseCase
     * @param mapper API 매퍼
     */
    public TechStackCommandController(
            CreateTechStackUseCase createTechStackUseCase,
            UpdateTechStackUseCase updateTechStackUseCase,
            TechStackCommandApiMapper mapper) {
        this.createTechStackUseCase = createTechStackUseCase;
        this.updateTechStackUseCase = updateTechStackUseCase;
        this.mapper = mapper;
    }

    /**
     * TechStack 생성 API
     *
     * <p>새로운 기술 스택을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 TechStack ID
     */
    @Operation(summary = "TechStack 생성", description = "새로운 기술 스택을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 이름")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<TechStackIdApiResponse>> create(
            @Valid @RequestBody CreateTechStackApiRequest request) {

        CreateTechStackCommand command = mapper.toCommand(request);
        Long id = createTechStackUseCase.execute(command);

        TechStackIdApiResponse response = TechStackIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * TechStack 수정 API
     *
     * <p>기존 기술 스택의 정보를 수정합니다.
     *
     * @param techStackId TechStack ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "TechStack 수정", description = "기존 기술 스택의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "TechStack을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 이름")
    })
    @PutMapping(TechStackApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "TechStack ID", required = true)
                    @PathVariable(TechStackApiEndpoints.PATH_TECH_STACK_ID)
                    Long techStackId,
            @Valid @RequestBody UpdateTechStackApiRequest request) {

        UpdateTechStackCommand command = mapper.toCommand(techStackId, request);
        updateTechStackUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
