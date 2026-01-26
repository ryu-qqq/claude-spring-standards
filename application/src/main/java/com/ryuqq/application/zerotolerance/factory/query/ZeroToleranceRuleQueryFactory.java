package com.ryuqq.application.zerotolerance.factory.query;

import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceRuleSearchField;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleQueryFactory - Zero-Tolerance 규칙 쿼리 팩토리
 *
 * <p>조회에 필요한 도메인 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleQueryFactory {

    /**
     * ZeroToleranceRuleSearchParams로부터 ZeroToleranceRuleSliceCriteria 생성
     *
     * @param searchParams 조회 SearchParams
     * @return ZeroToleranceRuleSliceCriteria
     */
    public ZeroToleranceRuleSliceCriteria createSliceCriteria(
            ZeroToleranceRuleSearchParams searchParams) {
        CursorPageRequest<Long> cursorPageRequest;

        if (searchParams.isFirstPage()) {
            cursorPageRequest = CursorPageRequest.first(searchParams.size());
        } else {
            Long cursorId = Long.parseLong(searchParams.cursor());
            cursorPageRequest = CursorPageRequest.afterId(cursorId, searchParams.size());
        }

        List<ConventionId> conventionIds = null;
        if (searchParams.hasConventionIds()) {
            conventionIds =
                    searchParams.conventionIds().stream()
                            .map(ConventionId::of)
                            .collect(Collectors.toList());
        }

        List<DetectionType> detectionTypes = null;
        if (searchParams.hasDetectionTypes()) {
            detectionTypes =
                    searchParams.detectionTypes().stream().map(DetectionType::valueOf).toList();
        }

        ZeroToleranceRuleSearchField searchField = null;
        String searchWord = null;
        if (searchParams.hasSearch()) {
            searchField = ZeroToleranceRuleSearchField.valueOf(searchParams.searchField());
            searchWord = searchParams.searchWord();
        }

        return ZeroToleranceRuleSliceCriteria.of(
                conventionIds,
                detectionTypes,
                searchField,
                searchWord,
                searchParams.autoRejectPr(),
                cursorPageRequest);
    }

    /**
     * Long ID를 ConventionId로 변환
     *
     * @param conventionId 컨벤션 ID
     * @return ConventionId (nullable)
     */
    public ConventionId toConventionId(Long conventionId) {
        return conventionId != null ? ConventionId.of(conventionId) : null;
    }
}
