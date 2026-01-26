package com.ryuqq.adapter.in.rest.classtypecategory.controller.command;

import com.ryuqq.adapter.in.rest.classtypecategory.ClassTypeCategoryApiEndpoints;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.CreateClassTypeCategoryApiRequest;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.UpdateClassTypeCategoryApiRequest;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.response.ClassTypeCategoryIdApiResponse;
import com.ryuqq.adapter.in.rest.classtypecategory.mapper.ClassTypeCategoryCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.application.classtypecategory.dto.command.CreateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.port.in.CreateClassTypeCategoryUseCase;
import com.ryuqq.application.classtypecategory.port.in.UpdateClassTypeCategoryUseCase;
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
 * ClassTypeCategoryCommandController - ClassTypeCategory 생성/수정 API
 *
 * <p>ClassTypeCategory CU(Create, Update) 엔드포인트를 제공합니다.
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
@Tag(name = "ClassTypeCategory", description = "클래스 타입 카테고리 관리 API")
@RestController
@RequestMapping(ClassTypeCategoryApiEndpoints.CLASS_TYPE_CATEGORIES)
public class ClassTypeCategoryCommandController {

    private final CreateClassTypeCategoryUseCase createClassTypeCategoryUseCase;
    private final UpdateClassTypeCategoryUseCase updateClassTypeCategoryUseCase;
    private final ClassTypeCategoryCommandApiMapper mapper;

    /**
     * ClassTypeCategoryCommandController 생성자
     *
     * @param createClassTypeCategoryUseCase ClassTypeCategory 생성 UseCase
     * @param updateClassTypeCategoryUseCase ClassTypeCategory 수정 UseCase
     * @param mapper API 매퍼
     */
    public ClassTypeCategoryCommandController(
            CreateClassTypeCategoryUseCase createClassTypeCategoryUseCase,
            UpdateClassTypeCategoryUseCase updateClassTypeCategoryUseCase,
            ClassTypeCategoryCommandApiMapper mapper) {
        this.createClassTypeCategoryUseCase = createClassTypeCategoryUseCase;
        this.updateClassTypeCategoryUseCase = updateClassTypeCategoryUseCase;
        this.mapper = mapper;
    }

    /**
     * ClassTypeCategory 생성 API
     *
     * <p>새로운 클래스 타입 카테고리를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 ClassTypeCategory ID
     */
    @Operation(summary = "ClassTypeCategory 생성", description = "새로운 클래스 타입 카테고리를 생성합니다.")
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
                description = "중복된 카테고리 코드")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ClassTypeCategoryIdApiResponse>> create(
            @Valid @RequestBody CreateClassTypeCategoryApiRequest request) {

        CreateClassTypeCategoryCommand command = mapper.toCommand(request);
        Long id = createClassTypeCategoryUseCase.execute(command);

        ClassTypeCategoryIdApiResponse response = ClassTypeCategoryIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * ClassTypeCategory 수정 API
     *
     * <p>기존 클래스 타입 카테고리의 정보를 수정합니다.
     *
     * @param categoryId ClassTypeCategory ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "ClassTypeCategory 수정", description = "기존 클래스 타입 카테고리의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "ClassTypeCategory를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 카테고리 코드")
    })
    @PutMapping(ClassTypeCategoryApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "ClassTypeCategory ID", required = true)
                    @PathVariable(ClassTypeCategoryApiEndpoints.PATH_CATEGORY_ID)
                    Long categoryId,
            @Valid @RequestBody UpdateClassTypeCategoryApiRequest request) {

        UpdateClassTypeCategoryCommand command = mapper.toCommand(categoryId, request);
        updateClassTypeCategoryUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
