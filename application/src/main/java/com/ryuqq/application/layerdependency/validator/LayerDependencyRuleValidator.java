package com.ryuqq.application.layerdependency.validator;

import com.ryuqq.application.architecture.port.out.ArchitectureQueryPort;
import com.ryuqq.application.layerdependency.manager.LayerDependencyRuleReadManager;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.exception.LayerDependencyRuleNotFoundException;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleValidator - 레이어 의존성 규칙 유효성 검증기
 *
 * <p>레이어 의존성 규칙 생성/수정 시 유효성을 검증합니다.
 *
 * <p>VLD-001: Validator는 ReadManager만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class LayerDependencyRuleValidator {

    private final ArchitectureQueryPort architectureQueryPort;
    private final LayerDependencyRuleReadManager layerDependencyRuleReadManager;

    public LayerDependencyRuleValidator(
            ArchitectureQueryPort architectureQueryPort,
            LayerDependencyRuleReadManager layerDependencyRuleReadManager) {
        this.architectureQueryPort = architectureQueryPort;
        this.layerDependencyRuleReadManager = layerDependencyRuleReadManager;
    }

    /**
     * 레이어 의존성 규칙 존재 여부 검증 후 반환 (조회 + 검증 통합)
     *
     * <p>APP-VAL-001: Validator.findExistingOrThrow()로 조회 + 검증 통합.
     *
     * @param layerDependencyRuleId 레이어 의존성 규칙 ID
     * @return 존재하는 LayerDependencyRule
     * @throws LayerDependencyRuleNotFoundException 규칙이 존재하지 않으면
     */
    public LayerDependencyRule findExistingOrThrow(LayerDependencyRuleId layerDependencyRuleId) {
        return layerDependencyRuleReadManager.getById(layerDependencyRuleId);
    }

    /**
     * 아키텍처 존재 여부 검증
     *
     * @param architectureId 아키텍처 ID
     * @throws ArchitectureNotFoundException 아키텍처가 존재하지 않으면
     */
    public void validateArchitectureExists(ArchitectureId architectureId) {
        if (!architectureQueryPort.existsById(architectureId)) {
            throw new ArchitectureNotFoundException(architectureId.value());
        }
    }
}
