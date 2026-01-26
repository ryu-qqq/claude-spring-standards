package com.ryuqq.adapter.in.rest.packagestructure.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.packagestructure.PackageStructureApiEndpoints;
import com.ryuqq.adapter.in.rest.packagestructure.dto.request.CreatePackageStructureApiRequest;
import com.ryuqq.adapter.in.rest.packagestructure.dto.request.UpdatePackageStructureApiRequest;
import com.ryuqq.adapter.in.rest.packagestructure.dto.response.PackageStructureIdApiResponse;
import com.ryuqq.adapter.in.rest.packagestructure.mapper.PackageStructureCommandApiMapper;
import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import com.ryuqq.application.packagestructure.port.in.CreatePackageStructureUseCase;
import com.ryuqq.application.packagestructure.port.in.UpdatePackageStructureUseCase;
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
 * PackageStructureCommandController - PackageStructure 생성/수정 API
 *
 * <p>패키지 구조 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "PackageStructure", description = "패키지 구조 관리 API")
@RestController
@RequestMapping(PackageStructureApiEndpoints.PACKAGE_STRUCTURES)
public class PackageStructureCommandController {

    private final CreatePackageStructureUseCase createPackageStructureUseCase;
    private final UpdatePackageStructureUseCase updatePackageStructureUseCase;
    private final PackageStructureCommandApiMapper mapper;

    /**
     * PackageStructureCommandController 생성자
     *
     * @param createPackageStructureUseCase PackageStructure 생성 UseCase
     * @param updatePackageStructureUseCase PackageStructure 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public PackageStructureCommandController(
            CreatePackageStructureUseCase createPackageStructureUseCase,
            UpdatePackageStructureUseCase updatePackageStructureUseCase,
            PackageStructureCommandApiMapper mapper) {
        this.createPackageStructureUseCase = createPackageStructureUseCase;
        this.updatePackageStructureUseCase = updatePackageStructureUseCase;
        this.mapper = mapper;
    }

    /**
     * PackageStructure 생성 API
     *
     * <p>새로운 패키지 구조를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 PackageStructure ID
     */
    @Operation(summary = "PackageStructure 생성", description = "새로운 패키지 구조를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Module을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 경로 패턴")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PackageStructureIdApiResponse>> create(
            @Valid @RequestBody CreatePackageStructureApiRequest request) {

        CreatePackageStructureCommand command = mapper.toCommand(request);
        Long id = createPackageStructureUseCase.execute(command);

        PackageStructureIdApiResponse response = PackageStructureIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * PackageStructure 수정 API
     *
     * <p>기존 패키지 구조의 정보를 수정합니다.
     *
     * @param packageStructureId PackageStructure ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "PackageStructure 수정", description = "기존 패키지 구조의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "PackageStructure를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 경로 패턴")
    })
    @PutMapping(PackageStructureApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "PackageStructure ID", required = true)
                    @PathVariable(PackageStructureApiEndpoints.PATH_PACKAGE_STRUCTURE_ID)
                    Long packageStructureId,
            @Valid @RequestBody UpdatePackageStructureApiRequest request) {

        UpdatePackageStructureCommand command = mapper.toCommand(packageStructureId, request);
        updatePackageStructureUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
