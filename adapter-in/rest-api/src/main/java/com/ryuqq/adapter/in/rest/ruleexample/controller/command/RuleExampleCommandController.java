package com.ryuqq.adapter.in.rest.ruleexample.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.ruleexample.RuleExampleApiEndpoints;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.CreateRuleExampleApiRequest;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.UpdateRuleExampleApiRequest;
import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleIdApiResponse;
import com.ryuqq.adapter.in.rest.ruleexample.mapper.RuleExampleCommandApiMapper;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.application.ruleexample.port.in.CreateRuleExampleUseCase;
import com.ryuqq.application.ruleexample.port.in.UpdateRuleExampleUseCase;
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
 * RuleExampleCommandController - RuleExample 생성/수정 API
 *
 * <p>규칙 예시 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "RuleExample", description = "규칙 예시 관리 API")
@RestController
@RequestMapping(RuleExampleApiEndpoints.RULE_EXAMPLES)
public class RuleExampleCommandController {

    private final CreateRuleExampleUseCase createRuleExampleUseCase;
    private final UpdateRuleExampleUseCase updateRuleExampleUseCase;
    private final RuleExampleCommandApiMapper mapper;

    /**
     * RuleExampleCommandController 생성자
     *
     * @param createRuleExampleUseCase RuleExample 생성 UseCase
     * @param updateRuleExampleUseCase RuleExample 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public RuleExampleCommandController(
            CreateRuleExampleUseCase createRuleExampleUseCase,
            UpdateRuleExampleUseCase updateRuleExampleUseCase,
            RuleExampleCommandApiMapper mapper) {
        this.createRuleExampleUseCase = createRuleExampleUseCase;
        this.updateRuleExampleUseCase = updateRuleExampleUseCase;
        this.mapper = mapper;
    }

    /**
     * RuleExample 생성 API
     *
     * <p>새로운 규칙 예시를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 RuleExample ID
     */
    @Operation(summary = "RuleExample 생성", description = "새로운 규칙 예시를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "CodingRule을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<RuleExampleIdApiResponse>> create(
            @Valid @RequestBody CreateRuleExampleApiRequest request) {

        CreateRuleExampleCommand command = mapper.toCommand(request);
        Long id = createRuleExampleUseCase.execute(command);

        RuleExampleIdApiResponse response = RuleExampleIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * RuleExample 수정 API
     *
     * <p>기존 규칙 예시의 정보를 수정합니다.
     *
     * @param ruleExampleId RuleExample ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "RuleExample 수정", description = "기존 규칙 예시의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "RuleExample을 찾을 수 없음")
    })
    @PutMapping(RuleExampleApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "RuleExample ID", required = true)
                    @PathVariable(RuleExampleApiEndpoints.PATH_RULE_EXAMPLE_ID)
                    Long ruleExampleId,
            @Valid @RequestBody UpdateRuleExampleApiRequest request) {

        UpdateRuleExampleCommand command = mapper.toCommand(ruleExampleId, request);
        updateRuleExampleUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
