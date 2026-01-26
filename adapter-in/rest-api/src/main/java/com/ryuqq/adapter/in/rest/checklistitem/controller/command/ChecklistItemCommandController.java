package com.ryuqq.adapter.in.rest.checklistitem.controller.command;

import com.ryuqq.adapter.in.rest.checklistitem.ChecklistItemApiEndpoints;
import com.ryuqq.adapter.in.rest.checklistitem.dto.request.CreateChecklistItemApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.request.UpdateChecklistItemApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemIdApiResponse;
import com.ryuqq.adapter.in.rest.checklistitem.mapper.ChecklistItemCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.checklistitem.port.in.CreateChecklistItemUseCase;
import com.ryuqq.application.checklistitem.port.in.UpdateChecklistItemUseCase;
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
 * ChecklistItemCommandController - ChecklistItem 생성/수정 API
 *
 * <p>체크리스트 항목 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "ChecklistItem", description = "체크리스트 항목 관리 API")
@RestController
@RequestMapping(ChecklistItemApiEndpoints.CHECKLIST_ITEMS)
public class ChecklistItemCommandController {

    private final CreateChecklistItemUseCase createChecklistItemUseCase;
    private final UpdateChecklistItemUseCase updateChecklistItemUseCase;
    private final ChecklistItemCommandApiMapper mapper;

    /**
     * ChecklistItemCommandController 생성자
     *
     * @param createChecklistItemUseCase ChecklistItem 생성 UseCase
     * @param updateChecklistItemUseCase ChecklistItem 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public ChecklistItemCommandController(
            CreateChecklistItemUseCase createChecklistItemUseCase,
            UpdateChecklistItemUseCase updateChecklistItemUseCase,
            ChecklistItemCommandApiMapper mapper) {
        this.createChecklistItemUseCase = createChecklistItemUseCase;
        this.updateChecklistItemUseCase = updateChecklistItemUseCase;
        this.mapper = mapper;
    }

    /**
     * ChecklistItem 생성 API
     *
     * <p>새로운 체크리스트 항목을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 ChecklistItem ID
     */
    @Operation(summary = "ChecklistItem 생성", description = "새로운 체크리스트 항목을 생성합니다.")
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
    public ResponseEntity<ApiResponse<ChecklistItemIdApiResponse>> create(
            @Valid @RequestBody CreateChecklistItemApiRequest request) {

        CreateChecklistItemCommand command = mapper.toCommand(request);
        Long id = createChecklistItemUseCase.execute(command);

        ChecklistItemIdApiResponse response = ChecklistItemIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * ChecklistItem 수정 API
     *
     * <p>기존 체크리스트 항목의 정보를 수정합니다.
     *
     * @param id ChecklistItem ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "ChecklistItem 수정", description = "기존 체크리스트 항목의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "ChecklistItem을 찾을 수 없음")
    })
    @PutMapping(ChecklistItemApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "ChecklistItem ID", required = true)
                    @PathVariable(ChecklistItemApiEndpoints.PATH_ID)
                    Long id,
            @Valid @RequestBody UpdateChecklistItemApiRequest request) {

        UpdateChecklistItemCommand command = mapper.toCommand(id, request);
        updateChecklistItemUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
