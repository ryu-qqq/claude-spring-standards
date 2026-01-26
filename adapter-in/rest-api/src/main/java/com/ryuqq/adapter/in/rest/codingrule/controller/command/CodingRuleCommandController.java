package com.ryuqq.adapter.in.rest.codingrule.controller.command;

import com.ryuqq.adapter.in.rest.codingrule.CodingRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.CreateCodingRuleApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.UpdateCodingRuleApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleIdApiResponse;
import com.ryuqq.adapter.in.rest.codingrule.mapper.CodingRuleCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.port.in.CreateCodingRuleUseCase;
import com.ryuqq.application.codingrule.port.in.UpdateCodingRuleUseCase;
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
 * CodingRuleCommandController - CodingRule 생성/수정 API
 *
 * <p>코딩 규칙 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "CodingRule", description = "코딩 규칙 관리 API")
@RestController
@RequestMapping(CodingRuleApiEndpoints.BASE)
public class CodingRuleCommandController {

    private final CreateCodingRuleUseCase createCodingRuleUseCase;
    private final UpdateCodingRuleUseCase updateCodingRuleUseCase;
    private final CodingRuleCommandApiMapper mapper;

    /**
     * CodingRuleCommandController 생성자
     *
     * @param createCodingRuleUseCase CodingRule 생성 UseCase
     * @param updateCodingRuleUseCase CodingRule 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public CodingRuleCommandController(
            CreateCodingRuleUseCase createCodingRuleUseCase,
            UpdateCodingRuleUseCase updateCodingRuleUseCase,
            CodingRuleCommandApiMapper mapper) {
        this.createCodingRuleUseCase = createCodingRuleUseCase;
        this.updateCodingRuleUseCase = updateCodingRuleUseCase;
        this.mapper = mapper;
    }

    /**
     * CodingRule 생성 API
     *
     * <p>새로운 코딩 규칙을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 CodingRule ID
     */
    @Operation(summary = "CodingRule 생성", description = "새로운 코딩 규칙을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Convention을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 규칙 코드")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CodingRuleIdApiResponse>> create(
            @Valid @RequestBody CreateCodingRuleApiRequest request) {

        CreateCodingRuleCommand command = mapper.toCommand(request);
        Long id = createCodingRuleUseCase.execute(command);

        CodingRuleIdApiResponse response = CodingRuleIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * CodingRule 수정 API
     *
     * <p>기존 코딩 규칙의 정보를 수정합니다.
     *
     * @param codingRuleId CodingRule ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "CodingRule 수정", description = "기존 코딩 규칙의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "CodingRule을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 규칙 코드")
    })
    @PutMapping(CodingRuleApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "CodingRule ID", required = true)
                    @PathVariable(CodingRuleApiEndpoints.PATH_CODING_RULE_ID)
                    Long codingRuleId,
            @Valid @RequestBody UpdateCodingRuleApiRequest request) {

        UpdateCodingRuleCommand command = mapper.toCommand(codingRuleId, request);
        updateCodingRuleUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
