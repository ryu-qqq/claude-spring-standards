package com.ryuqq.adapter.in.rest.zerotolerance.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.ZeroToleranceRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.CreateZeroToleranceRuleApiRequest;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.UpdateZeroToleranceRuleApiRequest;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.response.ZeroToleranceRuleIdApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.mapper.ZeroToleranceRuleCommandApiMapper;
import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.port.in.CreateZeroToleranceRuleUseCase;
import com.ryuqq.application.zerotolerance.port.in.UpdateZeroToleranceRuleUseCase;
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
 * ZeroToleranceRuleCommandController - Zero-Tolerance 규칙 생성/수정 API
 *
 * <p>Zero-Tolerance 규칙 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "ZeroToleranceRule", description = "Zero-Tolerance 규칙 관리 API")
@RestController
@RequestMapping(ZeroToleranceRuleApiEndpoints.BASE)
public class ZeroToleranceRuleCommandController {

    private final CreateZeroToleranceRuleUseCase createZeroToleranceRuleUseCase;
    private final UpdateZeroToleranceRuleUseCase updateZeroToleranceRuleUseCase;
    private final ZeroToleranceRuleCommandApiMapper mapper;

    /**
     * ZeroToleranceRuleCommandController 생성자
     *
     * @param createZeroToleranceRuleUseCase Zero-Tolerance 규칙 생성 UseCase
     * @param updateZeroToleranceRuleUseCase Zero-Tolerance 규칙 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public ZeroToleranceRuleCommandController(
            CreateZeroToleranceRuleUseCase createZeroToleranceRuleUseCase,
            UpdateZeroToleranceRuleUseCase updateZeroToleranceRuleUseCase,
            ZeroToleranceRuleCommandApiMapper mapper) {
        this.createZeroToleranceRuleUseCase = createZeroToleranceRuleUseCase;
        this.updateZeroToleranceRuleUseCase = updateZeroToleranceRuleUseCase;
        this.mapper = mapper;
    }

    /**
     * Zero-Tolerance 규칙 생성 API
     *
     * <p>새로운 Zero-Tolerance 규칙을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 Zero-Tolerance 규칙 ID
     */
    @Operation(summary = "Zero-Tolerance 규칙 생성", description = "새로운 Zero-Tolerance 규칙을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "CodingRule을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 Zero-Tolerance 규칙")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ZeroToleranceRuleIdApiResponse>> create(
            @Valid @RequestBody CreateZeroToleranceRuleApiRequest request) {

        CreateZeroToleranceRuleCommand command = mapper.toCommand(request);
        Long id = createZeroToleranceRuleUseCase.execute(command);

        ZeroToleranceRuleIdApiResponse response = ZeroToleranceRuleIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * Zero-Tolerance 규칙 수정 API
     *
     * <p>기존 Zero-Tolerance 규칙의 정보를 수정합니다.
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "Zero-Tolerance 규칙 수정", description = "기존 Zero-Tolerance 규칙의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Zero-Tolerance 규칙을 찾을 수 없음")
    })
    @PutMapping(ZeroToleranceRuleApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "Zero-Tolerance 규칙 ID", required = true)
                    @PathVariable(ZeroToleranceRuleApiEndpoints.PATH_ZERO_TOLERANCE_RULE_ID)
                    Long zeroToleranceRuleId,
            @Valid @RequestBody UpdateZeroToleranceRuleApiRequest request) {

        UpdateZeroToleranceRuleCommand command = mapper.toCommand(zeroToleranceRuleId, request);
        updateZeroToleranceRuleUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
