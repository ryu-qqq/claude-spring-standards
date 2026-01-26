package com.ryuqq.adapter.in.rest.resourcetemplate.mapper;

import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.CreateResourceTemplateApiRequest;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.UpdateResourceTemplateApiRequest;
import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateCommandApiMapper - ResourceTemplate Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component로 등록.
 *
 * <p>MAP-002: Mapper에서 Static 메서드 금지.
 *
 * <p>MAP-004: Mapper는 필드 매핑만 수행.
 *
 * <p>MAP-006: Mapper에서 Domain 객체 직접 사용 금지.
 *
 * <p>MAP-013: Mapper CQRS 분리 권장.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ResourceTemplateCommandApiMapper {

    /**
     * CreateResourceTemplateApiRequest -> CreateResourceTemplateCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateResourceTemplateCommand toCommand(CreateResourceTemplateApiRequest request) {
        return new CreateResourceTemplateCommand(
                request.moduleId(),
                request.category(),
                request.filePath(),
                request.fileType(),
                request.description(),
                request.templateContent(),
                nullSafeRequired(request.required()));
    }

    /**
     * UpdateResourceTemplateApiRequest + PathVariable ID -> UpdateResourceTemplateCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param resourceTemplateId ResourceTemplate ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateResourceTemplateCommand toCommand(
            Long resourceTemplateId, UpdateResourceTemplateApiRequest request) {
        return new UpdateResourceTemplateCommand(
                resourceTemplateId,
                request.category(),
                request.filePath(),
                request.fileType(),
                request.description(),
                request.templateContent(),
                request.required());
    }

    private Boolean nullSafeRequired(Boolean required) {
        return required != null ? required : Boolean.TRUE;
    }
}
