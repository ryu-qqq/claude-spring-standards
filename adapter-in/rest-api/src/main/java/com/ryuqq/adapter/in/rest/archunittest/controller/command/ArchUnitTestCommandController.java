package com.ryuqq.adapter.in.rest.archunittest.controller.command;

import com.ryuqq.adapter.in.rest.archunittest.ArchUnitTestApiEndpoints;
import com.ryuqq.adapter.in.rest.archunittest.dto.request.CreateArchUnitTestApiRequest;
import com.ryuqq.adapter.in.rest.archunittest.dto.request.UpdateArchUnitTestApiRequest;
import com.ryuqq.adapter.in.rest.archunittest.dto.response.ArchUnitTestIdApiResponse;
import com.ryuqq.adapter.in.rest.archunittest.mapper.ArchUnitTestCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.archunittest.port.in.CreateArchUnitTestUseCase;
import com.ryuqq.application.archunittest.port.in.UpdateArchUnitTestUseCase;
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
 * ArchUnitTestCommandController - ArchUnitTest 생성/수정 API
 *
 * <p>ArchUnit 테스트 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "ArchUnitTest", description = "ArchUnit 테스트 관리 API")
@RestController
@RequestMapping(ArchUnitTestApiEndpoints.BASE)
public class ArchUnitTestCommandController {

    private final CreateArchUnitTestUseCase createArchUnitTestUseCase;
    private final UpdateArchUnitTestUseCase updateArchUnitTestUseCase;
    private final ArchUnitTestCommandApiMapper mapper;

    /**
     * ArchUnitTestCommandController 생성자
     *
     * @param createArchUnitTestUseCase ArchUnitTest 생성 UseCase
     * @param updateArchUnitTestUseCase ArchUnitTest 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public ArchUnitTestCommandController(
            CreateArchUnitTestUseCase createArchUnitTestUseCase,
            UpdateArchUnitTestUseCase updateArchUnitTestUseCase,
            ArchUnitTestCommandApiMapper mapper) {
        this.createArchUnitTestUseCase = createArchUnitTestUseCase;
        this.updateArchUnitTestUseCase = updateArchUnitTestUseCase;
        this.mapper = mapper;
    }

    /**
     * ArchUnitTest 생성 API
     *
     * <p>새로운 ArchUnit 테스트를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 ArchUnitTest ID
     */
    @Operation(summary = "ArchUnitTest 생성", description = "새로운 ArchUnit 테스트를 생성합니다.")
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
                description = "중복된 테스트 코드")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ArchUnitTestIdApiResponse>> create(
            @Valid @RequestBody CreateArchUnitTestApiRequest request) {

        CreateArchUnitTestCommand command = mapper.toCommand(request);
        Long id = createArchUnitTestUseCase.execute(command);

        ArchUnitTestIdApiResponse response = ArchUnitTestIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * ArchUnitTest 수정 API
     *
     * <p>기존 ArchUnit 테스트의 정보를 수정합니다.
     *
     * @param archUnitTestId ArchUnitTest ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "ArchUnitTest 수정", description = "기존 ArchUnit 테스트의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "ArchUnitTest를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 테스트 코드")
    })
    @PutMapping(ArchUnitTestApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "ArchUnitTest ID", required = true)
                    @PathVariable(ArchUnitTestApiEndpoints.PATH_ARCH_UNIT_TEST_ID)
                    Long archUnitTestId,
            @Valid @RequestBody UpdateArchUnitTestApiRequest request) {

        UpdateArchUnitTestCommand command = mapper.toCommand(archUnitTestId, request);
        updateArchUnitTestUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
