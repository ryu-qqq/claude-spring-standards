package com.ryuqq.adapter.in.rest.classtype.controller.command;

import com.ryuqq.adapter.in.rest.classtype.ClassTypeApiEndpoints;
import com.ryuqq.adapter.in.rest.classtype.dto.request.CreateClassTypeApiRequest;
import com.ryuqq.adapter.in.rest.classtype.dto.request.UpdateClassTypeApiRequest;
import com.ryuqq.adapter.in.rest.classtype.dto.response.ClassTypeIdApiResponse;
import com.ryuqq.adapter.in.rest.classtype.mapper.ClassTypeCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;
import com.ryuqq.application.classtype.dto.command.UpdateClassTypeCommand;
import com.ryuqq.application.classtype.port.in.CreateClassTypeUseCase;
import com.ryuqq.application.classtype.port.in.UpdateClassTypeUseCase;
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
 * ClassTypeCommandController - ClassType 생성/수정 API
 *
 * <p>ClassType CU(Create, Update) 엔드포인트를 제공합니다.
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
@Tag(name = "ClassType", description = "클래스 타입 관리 API")
@RestController
@RequestMapping(ClassTypeApiEndpoints.CLASS_TYPES)
public class ClassTypeCommandController {

    private final CreateClassTypeUseCase createClassTypeUseCase;
    private final UpdateClassTypeUseCase updateClassTypeUseCase;
    private final ClassTypeCommandApiMapper mapper;

    /**
     * ClassTypeCommandController 생성자
     *
     * @param createClassTypeUseCase ClassType 생성 UseCase
     * @param updateClassTypeUseCase ClassType 수정 UseCase
     * @param mapper API 매퍼
     */
    public ClassTypeCommandController(
            CreateClassTypeUseCase createClassTypeUseCase,
            UpdateClassTypeUseCase updateClassTypeUseCase,
            ClassTypeCommandApiMapper mapper) {
        this.createClassTypeUseCase = createClassTypeUseCase;
        this.updateClassTypeUseCase = updateClassTypeUseCase;
        this.mapper = mapper;
    }

    /**
     * ClassType 생성 API
     *
     * <p>새로운 클래스 타입을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 ClassType ID
     */
    @Operation(summary = "ClassType 생성", description = "새로운 클래스 타입을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Category를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 클래스 타입 코드")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ClassTypeIdApiResponse>> create(
            @Valid @RequestBody CreateClassTypeApiRequest request) {

        CreateClassTypeCommand command = mapper.toCommand(request);
        Long id = createClassTypeUseCase.execute(command);

        ClassTypeIdApiResponse response = ClassTypeIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * ClassType 수정 API
     *
     * <p>기존 클래스 타입의 정보를 수정합니다.
     *
     * @param classTypeId ClassType ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "ClassType 수정", description = "기존 클래스 타입의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "ClassType을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 클래스 타입 코드")
    })
    @PutMapping(ClassTypeApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "ClassType ID", required = true)
                    @PathVariable(ClassTypeApiEndpoints.PATH_CLASS_TYPE_ID)
                    Long classTypeId,
            @Valid @RequestBody UpdateClassTypeApiRequest request) {

        UpdateClassTypeCommand command = mapper.toCommand(classTypeId, request);
        updateClassTypeUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
