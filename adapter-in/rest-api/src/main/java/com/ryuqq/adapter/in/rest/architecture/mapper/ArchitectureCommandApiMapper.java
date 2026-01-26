package com.ryuqq.adapter.in.rest.architecture.mapper;

import com.ryuqq.adapter.in.rest.architecture.dto.request.CreateArchitectureApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.request.UpdateArchitectureApiRequest;
import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import org.springframework.stereotype.Component;

/**
 * ArchitectureCommandApiMapper - Architecture Command API 변환 매퍼
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
public class ArchitectureCommandApiMapper {

    /**
     * CreateArchitectureApiRequest -> CreateArchitectureCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateArchitectureCommand toCommand(CreateArchitectureApiRequest request) {
        return new CreateArchitectureCommand(
                request.techStackId(),
                request.name(),
                request.patternType(),
                request.patternDescription(),
                request.patternPrinciples(),
                request.referenceLinks());
    }

    /**
     * UpdateArchitectureApiRequest + PathVariable ID -> UpdateArchitectureCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id Architecture ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateArchitectureCommand toCommand(Long id, UpdateArchitectureApiRequest request) {
        return new UpdateArchitectureCommand(
                id,
                request.name(),
                request.patternType(),
                request.patternDescription(),
                request.patternPrinciples(),
                request.referenceLinks());
    }
}
