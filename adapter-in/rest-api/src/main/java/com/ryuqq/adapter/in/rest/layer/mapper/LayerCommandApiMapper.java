package com.ryuqq.adapter.in.rest.layer.mapper;

import com.ryuqq.adapter.in.rest.layer.dto.request.CreateLayerApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.request.UpdateLayerApiRequest;
import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import org.springframework.stereotype.Component;

/**
 * LayerCommandApiMapper - Layer Command API 변환 매퍼
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
public class LayerCommandApiMapper {

    /**
     * CreateLayerApiRequest -> CreateLayerCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateLayerCommand toCommand(CreateLayerApiRequest request) {
        return new CreateLayerCommand(
                request.architectureId(),
                request.code(),
                request.name(),
                request.description(),
                request.orderIndex());
    }

    /**
     * UpdateLayerApiRequest + PathVariable ID -> UpdateLayerCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param id Layer ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateLayerCommand toCommand(Long id, UpdateLayerApiRequest request) {
        return new UpdateLayerCommand(
                id, request.code(), request.name(), request.description(), request.orderIndex());
    }
}
