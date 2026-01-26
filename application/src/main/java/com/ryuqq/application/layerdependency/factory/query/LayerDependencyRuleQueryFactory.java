package com.ryuqq.application.layerdependency.factory.query;

import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.layerdependency.query.LayerDependencyRuleSliceCriteria;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerDependencyRuleSearchField;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleQueryFactory - 레이어 의존성 규칙 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LayerDependencyRuleQueryFactory {

    /**
     * LayerDependencyRuleSearchParams로부터 LayerDependencyRuleSliceCriteria 생성
     *
     * @param searchParams 조회 SearchParams
     * @return LayerDependencyRuleSliceCriteria
     */
    public LayerDependencyRuleSliceCriteria createSliceCriteria(
            LayerDependencyRuleSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<ArchitectureId> architectureIds = null;
        if (searchParams.hasArchitectureIds()) {
            architectureIds =
                    searchParams.architectureIds().stream()
                            .map(ArchitectureId::of)
                            .collect(Collectors.toList());
        }

        List<DependencyType> dependencyTypes = null;
        if (searchParams.hasDependencyTypes()) {
            dependencyTypes =
                    searchParams.dependencyTypes().stream().map(DependencyType::valueOf).toList();
        }

        LayerDependencyRuleSearchField searchField = null;
        String searchWord = null;
        if (searchParams.hasSearch()) {
            searchField = LayerDependencyRuleSearchField.valueOf(searchParams.searchField());
            searchWord = searchParams.searchWord();
        }

        return LayerDependencyRuleSliceCriteria.of(
                architectureIds, dependencyTypes, searchField, searchWord, cursorPageRequest);
    }

    /**
     * Long ID를 ArchitectureId로 변환
     *
     * @param architectureId 아키텍처 ID
     * @return ArchitectureId (nullable)
     */
    public ArchitectureId toArchitectureId(Long architectureId) {
        return architectureId != null ? ArchitectureId.of(architectureId) : null;
    }
}
