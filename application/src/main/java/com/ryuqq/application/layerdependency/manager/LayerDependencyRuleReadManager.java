package com.ryuqq.application.layerdependency.manager;

import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleQueryPort;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.exception.LayerDependencyRuleNotFoundException;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * LayerDependencyRuleReadManager - 레이어 의존성 규칙 조회 관리자
 *
 * <p>레이어 의존성 규칙 조회 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class LayerDependencyRuleReadManager {

    private final LayerDependencyRuleQueryPort layerDependencyRuleQueryPort;

    public LayerDependencyRuleReadManager(
            LayerDependencyRuleQueryPort layerDependencyRuleQueryPort) {
        this.layerDependencyRuleQueryPort = layerDependencyRuleQueryPort;
    }

    /**
     * ID로 레이어 의존성 규칙 조회 (존재하지 않으면 예외)
     *
     * @param layerDependencyRuleId 레이어 의존성 규칙 ID
     * @return 레이어 의존성 규칙
     * @throws LayerDependencyRuleNotFoundException 레이어 의존성 규칙이 존재하지 않으면
     */
    @Transactional(readOnly = true)
    public LayerDependencyRule getById(LayerDependencyRuleId layerDependencyRuleId) {
        return layerDependencyRuleQueryPort
                .findById(layerDependencyRuleId)
                .orElseThrow(
                        () ->
                                new LayerDependencyRuleNotFoundException(
                                        layerDependencyRuleId.value()));
    }

    /**
     * 아키텍처 ID로 레이어 의존성 규칙 목록 조회
     *
     * @param architectureId 아키텍처 ID
     * @return 레이어 의존성 규칙 목록
     */
    @Transactional(readOnly = true)
    public List<LayerDependencyRule> findByArchitectureId(ArchitectureId architectureId) {
        return layerDependencyRuleQueryPort.findByArchitectureId(architectureId);
    }
}
