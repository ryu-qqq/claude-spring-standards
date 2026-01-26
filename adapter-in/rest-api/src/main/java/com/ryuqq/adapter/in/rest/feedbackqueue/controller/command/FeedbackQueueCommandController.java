package com.ryuqq.adapter.in.rest.feedbackqueue.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.FeedbackQueueApiEndpoints;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.CreateFeedbackApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.RejectFeedbackApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueIdApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.mapper.FeedbackQueueCommandApiMapper;
import com.ryuqq.adapter.in.rest.feedbackqueue.mapper.FeedbackQueueQueryApiMapper;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.port.in.CreateFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.port.in.MergeFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.port.in.ProcessFeedbackUseCase;
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
 * FeedbackQueueCommandController - FeedbackQueue 생성/수정/삭제 API
 *
 * <p>피드백 큐 CUD(Create, Update, Delete) 엔드포인트를 제공합니다.
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
@Tag(name = "FeedbackQueue", description = "피드백 큐 관리 API")
@RestController
@RequestMapping(FeedbackQueueApiEndpoints.BASE)
public class FeedbackQueueCommandController {

    private final CreateFeedbackUseCase createFeedbackUseCase;
    private final ProcessFeedbackUseCase processFeedbackUseCase;
    private final MergeFeedbackUseCase mergeFeedbackUseCase;
    private final FeedbackQueueCommandApiMapper commandMapper;
    private final FeedbackQueueQueryApiMapper queryMapper;

    /**
     * FeedbackQueueCommandController 생성자
     *
     * @param createFeedbackUseCase 피드백 생성 UseCase
     * @param processFeedbackUseCase 피드백 처리 통합 UseCase (승인/거절)
     * @param mergeFeedbackUseCase 머지 UseCase
     * @param commandMapper Command API 매퍼
     * @param queryMapper Query API 매퍼
     */
    public FeedbackQueueCommandController(
            CreateFeedbackUseCase createFeedbackUseCase,
            ProcessFeedbackUseCase processFeedbackUseCase,
            MergeFeedbackUseCase mergeFeedbackUseCase,
            FeedbackQueueCommandApiMapper commandMapper,
            FeedbackQueueQueryApiMapper queryMapper) {
        this.createFeedbackUseCase = createFeedbackUseCase;
        this.processFeedbackUseCase = processFeedbackUseCase;
        this.mergeFeedbackUseCase = mergeFeedbackUseCase;
        this.commandMapper = commandMapper;
        this.queryMapper = queryMapper;
    }

    /**
     * FeedbackQueue 생성 API
     *
     * <p>새로운 피드백을 큐에 등록합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 FeedbackQueue 정보
     */
    @Operation(summary = "피드백 생성", description = "새로운 피드백을 큐에 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackQueueIdApiResponse>> create(
            @Valid @RequestBody CreateFeedbackApiRequest request) {

        CreateFeedbackCommand command = commandMapper.toCommand(request);
        Long feedbackQueueId = createFeedbackUseCase.execute(command);

        FeedbackQueueIdApiResponse response = FeedbackQueueIdApiResponse.of(feedbackQueueId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * LLM 1차 승인 API
     *
     * <p>PENDING 상태의 피드백을 LLM이 1차 승인합니다.
     *
     * @param feedbackQueueId FeedbackQueue ID
     * @return 승인된 FeedbackQueue 정보
     */
    @Operation(summary = "LLM 1차 승인", description = "PENDING 상태의 피드백을 LLM이 1차 승인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "승인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "FeedbackQueue를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "유효하지 않은 상태 전이")
    })
    @PatchMapping(FeedbackQueueApiEndpoints.ID_LLM_APPROVE)
    public ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> llmApprove(
            @Parameter(description = "FeedbackQueue ID", required = true)
                    @PathVariable(FeedbackQueueApiEndpoints.PATH_FEEDBACK_QUEUE_ID)
                    Long feedbackQueueId) {

        ProcessFeedbackCommand command = commandMapper.toLlmApproveCommand(feedbackQueueId);
        FeedbackQueueResult result = processFeedbackUseCase.execute(command);

        FeedbackQueueApiResponse response = queryMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * LLM 1차 거절 API
     *
     * <p>PENDING 상태의 피드백을 LLM이 1차 거절합니다.
     *
     * @param feedbackQueueId FeedbackQueue ID
     * @param request 거절 요청 DTO (거절 사유 포함)
     * @return 거절된 FeedbackQueue 정보
     */
    @Operation(summary = "LLM 1차 거절", description = "PENDING 상태의 피드백을 LLM이 1차 거절합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "거절 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "FeedbackQueue를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "유효하지 않은 상태 전이")
    })
    @PatchMapping(FeedbackQueueApiEndpoints.ID_LLM_REJECT)
    public ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> llmReject(
            @Parameter(description = "FeedbackQueue ID", required = true)
                    @PathVariable(FeedbackQueueApiEndpoints.PATH_FEEDBACK_QUEUE_ID)
                    Long feedbackQueueId,
            @Valid @RequestBody(required = false) RejectFeedbackApiRequest request) {

        ProcessFeedbackCommand command = commandMapper.toLlmRejectCommand(feedbackQueueId, request);
        FeedbackQueueResult result = processFeedbackUseCase.execute(command);

        FeedbackQueueApiResponse response = queryMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * Human 2차 승인 API
     *
     * <p>LLM_APPROVED 상태의 Medium 리스크 피드백을 Human이 2차 승인합니다.
     *
     * @param feedbackQueueId FeedbackQueue ID
     * @return 승인된 FeedbackQueue 정보
     */
    @Operation(
            summary = "Human 2차 승인",
            description = "LLM_APPROVED 상태의 Medium 리스크 피드백을 Human이 2차 승인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "승인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "FeedbackQueue를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "유효하지 않은 상태 전이")
    })
    @PatchMapping(FeedbackQueueApiEndpoints.ID_HUMAN_APPROVE)
    public ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> humanApprove(
            @Parameter(description = "FeedbackQueue ID", required = true)
                    @PathVariable(FeedbackQueueApiEndpoints.PATH_FEEDBACK_QUEUE_ID)
                    Long feedbackQueueId) {

        ProcessFeedbackCommand command = commandMapper.toHumanApproveCommand(feedbackQueueId);
        FeedbackQueueResult result = processFeedbackUseCase.execute(command);

        FeedbackQueueApiResponse response = queryMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * Human 2차 거절 API
     *
     * <p>LLM_APPROVED 상태의 Medium 리스크 피드백을 Human이 2차 거절합니다.
     *
     * @param feedbackQueueId FeedbackQueue ID
     * @param request 거절 요청 DTO (거절 사유 포함)
     * @return 거절된 FeedbackQueue 정보
     */
    @Operation(
            summary = "Human 2차 거절",
            description = "LLM_APPROVED 상태의 Medium 리스크 피드백을 Human이 2차 거절합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "거절 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "FeedbackQueue를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "유효하지 않은 상태 전이")
    })
    @PatchMapping(FeedbackQueueApiEndpoints.ID_HUMAN_REJECT)
    public ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> humanReject(
            @Parameter(description = "FeedbackQueue ID", required = true)
                    @PathVariable(FeedbackQueueApiEndpoints.PATH_FEEDBACK_QUEUE_ID)
                    Long feedbackQueueId,
            @Valid @RequestBody(required = false) RejectFeedbackApiRequest request) {

        ProcessFeedbackCommand command =
                commandMapper.toHumanRejectCommand(feedbackQueueId, request);
        FeedbackQueueResult result = processFeedbackUseCase.execute(command);

        FeedbackQueueApiResponse response = queryMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * 피드백 머지 API
     *
     * <p>승인된 피드백을 실제 대상 테이블에 반영합니다.
     *
     * @param feedbackQueueId FeedbackQueue ID
     * @return 머지된 FeedbackQueue 정보
     */
    @Operation(summary = "피드백 머지", description = "승인된 피드백을 실제 대상 테이블에 반영합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "머지 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "FeedbackQueue를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "유효하지 않은 상태 전이")
    })
    @PostMapping(FeedbackQueueApiEndpoints.ID_MERGE)
    public ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> merge(
            @Parameter(description = "FeedbackQueue ID", required = true)
                    @PathVariable(FeedbackQueueApiEndpoints.PATH_FEEDBACK_QUEUE_ID)
                    Long feedbackQueueId) {

        MergeFeedbackCommand command = commandMapper.toMergeCommand(feedbackQueueId);
        FeedbackQueueResult result = mergeFeedbackUseCase.execute(command);

        FeedbackQueueApiResponse response = queryMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
