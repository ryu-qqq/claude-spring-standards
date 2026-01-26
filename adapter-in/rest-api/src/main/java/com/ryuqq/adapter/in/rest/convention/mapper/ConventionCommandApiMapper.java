package com.ryuqq.adapter.in.rest.convention.mapper;

import com.ryuqq.adapter.in.rest.convention.dto.request.CreateConventionApiRequest;
import com.ryuqq.adapter.in.rest.convention.dto.request.UpdateConventionApiRequest;
import com.ryuqq.application.convention.dto.command.CreateConventionCommand;
import com.ryuqq.application.convention.dto.command.UpdateConventionCommand;
import org.springframework.stereotype.Component;

/**
 * ConventionCommandApiMapper - Convention Command API 변환 매퍼
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
public class ConventionCommandApiMapper {

    /**
     * CreateConventionApiRequest -> CreateConventionCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateConventionCommand toCommand(CreateConventionApiRequest request) {
        return new CreateConventionCommand(
                request.moduleId(), request.version(), request.description());
    }

    /**
     * UpdateConventionApiRequest + PathVariable ID -> UpdateConventionCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id Convention ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateConventionCommand toCommand(Long id, UpdateConventionApiRequest request) {
        return new UpdateConventionCommand(
                id, request.moduleId(), request.version(), request.description(), request.active());
    }
}
