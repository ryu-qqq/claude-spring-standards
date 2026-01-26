package com.ryuqq.adapter.in.rest.packagepurpose.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.packagepurpose.PackagePurposeApiEndpoints;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.CreatePackagePurposeApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.UpdatePackagePurposeApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeIdApiResponse;
import com.ryuqq.adapter.in.rest.packagepurpose.mapper.PackagePurposeCommandApiMapper;
import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.port.in.CreatePackagePurposeUseCase;
import com.ryuqq.application.packagepurpose.port.in.UpdatePackagePurposeUseCase;
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
 * PackagePurposeCommandController - PackagePurpose 생성/수정 API
 *
 * <p>패키지 목적 CUD(Create, Update) 엔드포인트를 제공합니다.
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
@Tag(name = "PackagePurpose", description = "패키지 목적 관리 API")
@RestController
@RequestMapping(PackagePurposeApiEndpoints.BASE)
public class PackagePurposeCommandController {

    private final CreatePackagePurposeUseCase createPackagePurposeUseCase;
    private final UpdatePackagePurposeUseCase updatePackagePurposeUseCase;
    private final PackagePurposeCommandApiMapper mapper;

    /**
     * PackagePurposeCommandController 생성자
     *
     * @param createPackagePurposeUseCase PackagePurpose 생성 UseCase
     * @param updatePackagePurposeUseCase PackagePurpose 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public PackagePurposeCommandController(
            CreatePackagePurposeUseCase createPackagePurposeUseCase,
            UpdatePackagePurposeUseCase updatePackagePurposeUseCase,
            PackagePurposeCommandApiMapper mapper) {
        this.createPackagePurposeUseCase = createPackagePurposeUseCase;
        this.updatePackagePurposeUseCase = updatePackagePurposeUseCase;
        this.mapper = mapper;
    }

    /**
     * PackagePurpose 생성 API
     *
     * <p>새로운 패키지 목적을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 PackagePurpose ID
     */
    @Operation(summary = "PackagePurpose 생성", description = "새로운 패키지 목적을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 목적 코드")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PackagePurposeIdApiResponse>> create(
            @Valid @RequestBody CreatePackagePurposeApiRequest request) {

        CreatePackagePurposeCommand command = mapper.toCommand(request);
        Long id = createPackagePurposeUseCase.execute(command);

        PackagePurposeIdApiResponse response = PackagePurposeIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * PackagePurpose 수정 API
     *
     * <p>기존 패키지 목적의 정보를 수정합니다.
     *
     * @param packagePurposeId PackagePurpose ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "PackagePurpose 수정", description = "기존 패키지 목적의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "PackagePurpose을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 목적 코드")
    })
    @PatchMapping(PackagePurposeApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "PackagePurpose ID", required = true)
                    @PathVariable(PackagePurposeApiEndpoints.PATH_PACKAGE_PURPOSE_ID)
                    Long packagePurposeId,
            @Valid @RequestBody UpdatePackagePurposeApiRequest request) {

        UpdatePackagePurposeCommand command = mapper.toCommand(packagePurposeId, request);
        updatePackagePurposeUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
