package com.ryuqq.adapter.in.rest.module.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.module.ModuleApiEndpoints;
import com.ryuqq.adapter.in.rest.module.dto.request.CreateModuleApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.request.UpdateModuleApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleIdApiResponse;
import com.ryuqq.adapter.in.rest.module.mapper.ModuleCommandApiMapper;
import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import com.ryuqq.application.module.port.in.CreateModuleUseCase;
import com.ryuqq.application.module.port.in.UpdateModuleUseCase;
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
 * ModuleCommandController - Module 생성/수정 API
 *
 * <p>모듈 CU(Create, Update) 엔드포인트를 제공합니다.
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
@Tag(name = "Module", description = "모듈 관리 API")
@RestController
@RequestMapping(ModuleApiEndpoints.MODULES)
public class ModuleCommandController {

    private final CreateModuleUseCase createModuleUseCase;
    private final UpdateModuleUseCase updateModuleUseCase;
    private final ModuleCommandApiMapper mapper;

    /**
     * ModuleCommandController 생성자
     *
     * @param createModuleUseCase Module 생성 UseCase
     * @param updateModuleUseCase Module 수정 UseCase
     * @param mapper API 매퍼
     */
    public ModuleCommandController(
            CreateModuleUseCase createModuleUseCase,
            UpdateModuleUseCase updateModuleUseCase,
            ModuleCommandApiMapper mapper) {
        this.createModuleUseCase = createModuleUseCase;
        this.updateModuleUseCase = updateModuleUseCase;
        this.mapper = mapper;
    }

    /**
     * Module 생성 API
     *
     * <p>새로운 모듈을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Module ID
     */
    @Operation(summary = "Module 생성", description = "새로운 모듈을 생성합니다.")
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
                description = "중복된 모듈 이름")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ModuleIdApiResponse>> create(
            @Valid @RequestBody CreateModuleApiRequest request) {

        CreateModuleCommand command = mapper.toCommand(request);
        Long id = createModuleUseCase.execute(command);

        ModuleIdApiResponse response = ModuleIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * Module 수정 API
     *
     * <p>기존 모듈의 정보를 수정합니다.
     *
     * @param moduleId Module ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "Module 수정", description = "기존 모듈의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Module을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 모듈 이름")
    })
    @PutMapping(ModuleApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Module ID", required = true)
                    @PathVariable(ModuleApiEndpoints.PATH_MODULE_ID)
                    Long moduleId,
            @Valid @RequestBody UpdateModuleApiRequest request) {

        UpdateModuleCommand command = mapper.toCommand(moduleId, request);
        updateModuleUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
