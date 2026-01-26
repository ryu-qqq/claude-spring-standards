package com.ryuqq.adapter.in.rest.configfiletemplate.mapper;

import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.CreateConfigFileTemplateApiRequest;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.UpdateConfigFileTemplateApiRequest;
import com.ryuqq.application.configfiletemplate.dto.command.CreateConfigFileTemplateCommand;
import com.ryuqq.application.configfiletemplate.dto.command.UpdateConfigFileTemplateCommand;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateCommandApiMapper - ConfigFileTemplate Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-002: API Request -> Application Command 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConfigFileTemplateCommandApiMapper {

    /**
     * CreateConfigFileTemplateApiRequest -> CreateConfigFileTemplateCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateConfigFileTemplateCommand toCommand(CreateConfigFileTemplateApiRequest request) {
        return new CreateConfigFileTemplateCommand(
                request.techStackId(),
                request.architectureId(),
                request.toolType(),
                request.filePath(),
                request.fileName(),
                request.content(),
                request.category(),
                request.description(),
                request.variables(),
                request.displayOrder(),
                request.isRequired());
    }

    /**
     * UpdateConfigFileTemplateApiRequest + PathVariable ID -> UpdateConfigFileTemplateCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id ConfigFileTemplate ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateConfigFileTemplateCommand toCommand(
            Long id, UpdateConfigFileTemplateApiRequest request) {
        return new UpdateConfigFileTemplateCommand(
                id,
                request.toolType(),
                request.filePath(),
                request.fileName(),
                request.content(),
                request.category(),
                request.description(),
                request.variables(),
                request.displayOrder(),
                request.isRequired());
    }
}
