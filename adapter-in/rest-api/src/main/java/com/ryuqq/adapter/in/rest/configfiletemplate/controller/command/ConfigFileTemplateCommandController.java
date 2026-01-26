package com.ryuqq.adapter.in.rest.configfiletemplate.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.configfiletemplate.ConfigFileTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.CreateConfigFileTemplateApiRequest;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.UpdateConfigFileTemplateApiRequest;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.response.ConfigFileTemplateIdApiResponse;
import com.ryuqq.adapter.in.rest.configfiletemplate.mapper.ConfigFileTemplateCommandApiMapper;
import com.ryuqq.application.configfiletemplate.dto.command.CreateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.dto.command.UpdateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.port.in.CreateConfigFileTemplateUseCase;
import com.ryuqq.application.configfiletemplate.port.in.UpdateConfigFileTemplateUseCase;
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
 * ConfigFileTemplateCommandController - ConfigFileTemplate 생성/수정 API
 *
 * <p>설정 파일 템플릿 생성/수정 엔드포인트를 제공합니다.
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
@Tag(name = "ConfigFileTemplate", description = "AI 도구 설정 파일 템플릿 관리 API")
@RestController
@RequestMapping(ConfigFileTemplateApiEndpoints.CONFIG_FILES)
public class ConfigFileTemplateCommandController {

    private final CreateConfigFileTemplateUseCase createConfigFileTemplateUseCase;
    private final UpdateConfigFileTemplateUseCase updateConfigFileTemplateUseCase;
    private final ConfigFileTemplateCommandApiMapper mapper;

    /**
     * ConfigFileTemplateCommandController 생성자
     *
     * @param createConfigFileTemplateUseCase ConfigFileTemplate 생성 UseCase
     * @param updateConfigFileTemplateUseCase ConfigFileTemplate 수정 UseCase
     * @param mapper API 매퍼
     */
    public ConfigFileTemplateCommandController(
            CreateConfigFileTemplateUseCase createConfigFileTemplateUseCase,
            UpdateConfigFileTemplateUseCase updateConfigFileTemplateUseCase,
            ConfigFileTemplateCommandApiMapper mapper) {
        this.createConfigFileTemplateUseCase = createConfigFileTemplateUseCase;
        this.updateConfigFileTemplateUseCase = updateConfigFileTemplateUseCase;
        this.mapper = mapper;
    }

    /**
     * ConfigFileTemplate 생성 API
     *
     * <p>새로운 설정 파일 템플릿을 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 ConfigFileTemplate ID
     */
    @Operation(summary = "ConfigFileTemplate 생성", description = "새로운 AI 도구 설정 파일 템플릿을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "TechStack을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ConfigFileTemplateIdApiResponse>> create(
            @Valid @RequestBody CreateConfigFileTemplateApiRequest request) {

        CreateConfigFileTemplateCommand command = mapper.toCommand(request);
        Long id = createConfigFileTemplateUseCase.execute(command);

        ConfigFileTemplateIdApiResponse response = ConfigFileTemplateIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * ConfigFileTemplate 수정 API
     *
     * <p>기존 설정 파일 템플릿의 정보를 수정합니다.
     *
     * @param configFileTemplateId ConfigFileTemplate ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "ConfigFileTemplate 수정", description = "기존 AI 도구 설정 파일 템플릿의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "ConfigFileTemplate을 찾을 수 없음")
    })
    @PutMapping(ConfigFileTemplateApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "ConfigFileTemplate ID", required = true)
                    @PathVariable(ConfigFileTemplateApiEndpoints.PATH_CONFIG_FILE_TEMPLATE_ID)
                    Long configFileTemplateId,
            @Valid @RequestBody UpdateConfigFileTemplateApiRequest request) {

        UpdateConfigFileTemplateCommand command = mapper.toCommand(configFileTemplateId, request);
        updateConfigFileTemplateUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
