package com.ryuqq.domain.zerotolerance.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceRuleSearchField;
import java.util.List;

/**
 * ZeroToleranceRuleSliceCriteria - Zero-Tolerance 규칙 슬라이스 조회 조건 (커서 기반)
 *
 * <p>Zero-Tolerance 규칙 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param conventionIds 필터링할 컨벤션 ID 목록 (optional)
 * @param detectionTypes 필터링할 탐지 방식 목록 (optional)
 * @param searchField 검색 필드 (optional)
 * @param searchWord 검색어 (optional)
 * @param autoRejectPr PR 자동 거부 여부 필터 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ZeroToleranceRuleSliceCriteria(
        List<ConventionId> conventionIds,
        List<DetectionType> detectionTypes,
        ZeroToleranceRuleSearchField searchField,
        String searchWord,
        Boolean autoRejectPr,
        CursorPageRequest<Long> cursorPageRequest) {

    public ZeroToleranceRuleSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 전체 조회)
     *
     * @param size 슬라이스 크기
     * @return ZeroToleranceRuleSliceCriteria
     */
    public static ZeroToleranceRuleSliceCriteria first(int size) {
        return new ZeroToleranceRuleSliceCriteria(
                null, null, null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ZeroToleranceRuleSliceCriteria
     */
    public static ZeroToleranceRuleSliceCriteria afterId(Long cursorId, int size) {
        return new ZeroToleranceRuleSliceCriteria(
                null, null, null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 커서 기반 페이징 요청 생성
     *
     * @param conventionIds 컨벤션 ID 목록 (nullable)
     * @param detectionTypes 탐지 방식 목록 (nullable)
     * @param searchField 검색 필드 (nullable)
     * @param searchWord 검색어 (nullable)
     * @param autoRejectPr PR 자동 거부 여부 필터 (nullable)
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return ZeroToleranceRuleSliceCriteria
     */
    public static ZeroToleranceRuleSliceCriteria of(
            List<ConventionId> conventionIds,
            List<DetectionType> detectionTypes,
            ZeroToleranceRuleSearchField searchField,
            String searchWord,
            Boolean autoRejectPr,
            CursorPageRequest<Long> cursorPageRequest) {
        return new ZeroToleranceRuleSliceCriteria(
                conventionIds,
                detectionTypes,
                searchField,
                searchWord,
                autoRejectPr,
                cursorPageRequest);
    }

    /**
     * 컨벤션 ID 필터 존재 여부 확인
     *
     * @return conventionIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasConventionFilter() {
        return conventionIds != null && !conventionIds.isEmpty();
    }

    /**
     * 탐지 방식 필터 존재 여부 확인
     *
     * @return detectionTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasDetectionTypeFilter() {
        return detectionTypes != null && !detectionTypes.isEmpty();
    }

    /**
     * 검색 조건 존재 여부 확인
     *
     * @return searchField와 searchWord가 모두 있으면 true
     */
    public boolean hasSearch() {
        return searchField != null && searchWord != null && !searchWord.isBlank();
    }

    /**
     * PR 자동 거부 필터 존재 여부 확인
     *
     * @return autoRejectPr이 null이 아니면 true
     */
    public boolean hasAutoRejectPrFilter() {
        return autoRejectPr != null;
    }

    /**
     * 첫 페이지 요청인지 확인
     *
     * @return cursor가 null이면 true
     */
    public boolean isFirstPage() {
        return cursorPageRequest.cursor() == null;
    }

    /**
     * 커서가 있는지 확인
     *
     * @return 커서가 있으면 true
     */
    public boolean hasCursor() {
        return cursorPageRequest.cursor() != null;
    }

    /**
     * 슬라이스 크기 반환 (편의 메서드)
     *
     * @return size
     */
    public int size() {
        return cursorPageRequest.size();
    }

    /**
     * 실제 조회 크기 반환 (hasNext 판단용 +1)
     *
     * @return size + 1
     */
    public int fetchSize() {
        return cursorPageRequest.fetchSize();
    }
}
