package com.ryuqq.adapter.in.rest.classtemplate.controller.command;

import com.ryuqq.adapter.in.rest.classtemplate.ClassTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.classtemplate.dto.request.CreateClassTemplateApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.request.UpdateClassTemplateApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.response.ClassTemplateIdApiResponse;
import com.ryuqq.adapter.in.rest.classtemplate.mapper.ClassTemplateCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.port.in.CreateClassTemplateUseCase;
import com.ryuqq.application.classtemplate.port.in.UpdateClassTemplateUseCase;
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
 * ClassTemplateCommandController - ClassTemplate 생성/수정 API
 *
 * <p>클래스 템플릿 생성 및 수정 엔드포인트를 제공합니다.
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
@Tag(name = "ClassTemplate", description = "클래스 템플릿 관리 API")
@RestController
@RequestMapping(ClassTemplateApiEndpoints.CLASS_TEMPLATES)
public class ClassTemplateCommandController {

    private final CreateClassTemplateUseCase createClassTemplateUseCase;
    private final UpdateClassTemplateUseCase updateClassTemplateUseCase;
    private final ClassTemplateCommandApiMapper mapper;

    /**
     * ClassTemplateCommandController 생성자
     *
     * @param createClassTemplateUseCase ClassTemplate 생성 UseCase
     * @param updateClassTemplateUseCase ClassTemplate 수정 UseCase
     * @param mapper Command API 매퍼
     */
    public ClassTemplateCommandController(
            CreateClassTemplateUseCase createClassTemplateUseCase,
            UpdateClassTemplateUseCase updateClassTemplateUseCase,
            ClassTemplateCommandApiMapper mapper) {
        this.createClassTemplateUseCase = createClassTemplateUseCase;
        this.updateClassTemplateUseCase = updateClassTemplateUseCase;
        this.mapper = mapper;
    }

    /**
     * ClassTemplate 생성 API
     *
     * <p>새로운 클래스 템플릿을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 ClassTemplate ID
     */
    @Operation(summary = "ClassTemplate 생성", description = "새로운 클래스 템플릿을 생성합니다.")
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
                description = "중복된 템플릿 코드")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ClassTemplateIdApiResponse>> create(
            @Valid @RequestBody CreateClassTemplateApiRequest request) {

        CreateClassTemplateCommand command = mapper.toCommand(request);
        Long id = createClassTemplateUseCase.execute(command);

        ClassTemplateIdApiResponse response = ClassTemplateIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * ClassTemplate 수정 API
     *
     * <p>기존 클래스 템플릿의 정보를 수정합니다.
     *
     * @param classTemplateId ClassTemplate ID
     * @param request 수정 요청 DTO
     * @return 빈 응답
     */
    @Operation(summary = "ClassTemplate 수정", description = "기존 클래스 템플릿의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "ClassTemplate을 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 템플릿 코드")
    })
    @PutMapping(ClassTemplateApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "ClassTemplate ID", required = true)
                    @PathVariable(ClassTemplateApiEndpoints.PATH_CLASS_TEMPLATE_ID)
                    Long classTemplateId,
            @Valid @RequestBody UpdateClassTemplateApiRequest request) {

        UpdateClassTemplateCommand command = mapper.toCommand(classTemplateId, request);
        updateClassTemplateUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
