package com.ryuqq.adapter.in.rest.layerdependency.mapper;

import com.ryuqq.adapter.in.rest.layerdependency.dto.request.CreateLayerDependencyRuleApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.UpdateLayerDependencyRuleApiRequest;
import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleCommandApiMapper - LayerDependencyRule Command API 변환 매퍼
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
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LayerDependencyRuleCommandApiMapper {

    /**
     * CreateLayerDependencyRuleApiRequest -> CreateLayerDependencyRuleCommand 변환
     *
     * @param architectureId Architecture ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateLayerDependencyRuleCommand toCommand(
            Long architectureId, CreateLayerDependencyRuleApiRequest request) {
        return new CreateLayerDependencyRuleCommand(
                architectureId,
                request.fromLayer(),
                request.toLayer(),
                request.dependencyType(),
                request.conditionDescription());
    }

    /**
     * UpdateLayerDependencyRuleApiRequest -> UpdateLayerDependencyRuleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param architectureId Architecture ID (PathVariable)
     * @param ldrId LayerDependencyRule ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateLayerDependencyRuleCommand toCommand(
            Long architectureId, Long ldrId, UpdateLayerDependencyRuleApiRequest request) {
        return new UpdateLayerDependencyRuleCommand(
                architectureId,
                ldrId,
                request.fromLayer(),
                request.toLayer(),
                request.dependencyType(),
                request.conditionDescription());
    }
}
