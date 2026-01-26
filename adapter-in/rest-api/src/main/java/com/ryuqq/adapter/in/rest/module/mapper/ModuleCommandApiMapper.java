package com.ryuqq.adapter.in.rest.module.mapper;

import com.ryuqq.adapter.in.rest.module.dto.request.CreateModuleApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.request.UpdateModuleApiRequest;
import com.ryuqq.application.module.dto.command.CreateModuleCommand;
import com.ryuqq.application.module.dto.command.UpdateModuleCommand;
import org.springframework.stereotype.Component;

/**
 * ModuleCommandApiMapper - Module Command API 변환 매퍼
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
public class ModuleCommandApiMapper {

    /**
     * CreateModuleApiRequest -> CreateModuleCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateModuleCommand toCommand(CreateModuleApiRequest request) {
        return new CreateModuleCommand(
                request.layerId(),
                request.parentModuleId(),
                request.name(),
                request.description(),
                request.modulePath(),
                request.buildIdentifier());
    }

    /**
     * UpdateModuleApiRequest + PathVariable ID -> UpdateModuleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param moduleId Module ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateModuleCommand toCommand(Long moduleId, UpdateModuleApiRequest request) {
        return new UpdateModuleCommand(
                moduleId,
                request.parentModuleId(),
                request.name(),
                request.description(),
                request.modulePath(),
                request.buildIdentifier());
    }
}
