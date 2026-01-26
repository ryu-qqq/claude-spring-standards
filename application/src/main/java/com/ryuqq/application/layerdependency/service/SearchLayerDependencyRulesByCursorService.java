package com.ryuqq.application.layerdependency.service;

import com.ryuqq.application.layerdependency.assembler.LayerDependencyRuleAssembler;
import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleResult;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleSliceResult;
import com.ryuqq.application.layerdependency.factory.query.LayerDependencyRuleQueryFactory;
import com.ryuqq.application.layerdependency.port.in.SearchLayerDependencyRulesByCursorUseCase;
import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleQueryPort;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchLayerDependencyRulesByCursorService - LayerDependencyRule 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>LayerDependencyRule 목록을 커서 기반으로 복합 조건(아키텍처 ID, 의존성 타입, 검색 필드/검색어)으로 조회합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-002: Service는 UseCase 인터페이스 구현.
 *
 * <p>SVC-003: Service는 비즈니스 로직 없이 UseCase 위임만 수행.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class SearchLayerDependencyRulesByCursorService
        implements SearchLayerDependencyRulesByCursorUseCase {

    private final LayerDependencyRuleQueryFactory queryFactory;
    private final LayerDependencyRuleQueryPort queryPort;
    private final LayerDependencyRuleAssembler assembler;

    public SearchLayerDependencyRulesByCursorService(
            LayerDependencyRuleQueryFactory queryFactory,
            LayerDependencyRuleQueryPort queryPort,
            LayerDependencyRuleAssembler assembler) {
        this.queryFactory = queryFactory;
        this.queryPort = queryPort;
        this.assembler = assembler;
    }

    @Override
    public LayerDependencyRuleSliceResult execute(LayerDependencyRuleSearchParams searchParams) {
        var criteria = queryFactory.createSliceCriteria(searchParams);
        List<LayerDependencyRule> rules = queryPort.findBySliceCriteria(criteria);

        boolean hasNext = rules.size() > criteria.size();
        List<LayerDependencyRule> actualRules = hasNext ? rules.subList(0, criteria.size()) : rules;

        List<LayerDependencyRuleResult> results =
                actualRules.stream().map(assembler::toResult).toList();

        return LayerDependencyRuleSliceResult.of(results, criteria.size(), hasNext);
    }
}
