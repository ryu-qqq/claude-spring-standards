package com.ryuqq.application.layerdependency.port.out;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.query.LayerDependencyRuleSliceCriteria;
import java.util.List;
import java.util.Optional;

/**
 * LayerDependencyRuleQueryPort - 레이어 의존성 규칙 조회 Port
 *
 * <p>영속성 계층으로의 LayerDependencyRule 조회 아웃바운드 포트입니다.
 *
 * @author ryu-qqq
 */
public interface LayerDependencyRuleQueryPort {

    /**
     * ID로 레이어 의존성 규칙 조회
     *
     * @param id 레이어 의존성 규칙 ID
     * @return LayerDependencyRule (Optional)
     */
    Optional<LayerDependencyRule> findById(LayerDependencyRuleId id);

    /**
     * 아키텍처 ID로 레이어 의존성 규칙 목록 조회
     *
     * @param architectureId 아키텍처 ID
     * @return 레이어 의존성 규칙 목록
     */
    List<LayerDependencyRule> findByArchitectureId(ArchitectureId architectureId);

    /**
     * 슬라이스 조건으로 레이어 의존성 규칙 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return 레이어 의존성 규칙 목록
     */
    List<LayerDependencyRule> findBySliceCriteria(LayerDependencyRuleSliceCriteria criteria);

    /**
     * ID 존재 여부 확인
     *
     * @param id 레이어 의존성 규칙 ID
     * @return 존재 여부
     */
    boolean existsById(LayerDependencyRuleId id);
}
