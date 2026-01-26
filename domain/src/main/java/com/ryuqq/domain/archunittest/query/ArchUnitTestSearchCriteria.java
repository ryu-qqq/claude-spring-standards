package com.ryuqq.domain.archunittest.query;

import com.ryuqq.domain.archunittest.vo.ArchUnitTestSortKey;
import com.ryuqq.domain.common.vo.PageRequest;
import com.ryuqq.domain.common.vo.QueryContext;
import com.ryuqq.domain.common.vo.SortDirection;
import com.ryuqq.domain.convention.id.ConventionId;

/**
 * ArchUnitTestSearchCriteria - ArchUnit 테스트 검색 조건
 *
 * <p>ArchUnit 테스트 목록 조회 시 사용되는 검색 조건입니다.
 *
 * @author ryu-qqq
 * @since 2026-01-06
 */
public record ArchUnitTestSearchCriteria(
        String keyword,
        ConventionId conventionId,
        String severity,
        boolean includeDeleted,
        QueryContext<ArchUnitTestSortKey> queryContext) {

    public ArchUnitTestSearchCriteria {
        if (queryContext == null) {
            throw new IllegalArgumentException("queryContext must not be null");
        }
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        if (severity != null && severity.isBlank()) {
            severity = null;
        }
    }

    /**
     * 기본 검색 조건 생성
     *
     * @return 기본 ArchUnitTestSearchCriteria
     */
    public static ArchUnitTestSearchCriteria defaultCriteria() {
        return new ArchUnitTestSearchCriteria(
                null, null, null, false, QueryContext.defaultOf(ArchUnitTestSortKey.defaultKey()));
    }

    /**
     * 검색 조건 생성 (전체 파라미터)
     *
     * @param keyword 검색 키워드
     * @param conventionId 컨벤션 ID 필터
     * @param severity 심각도 필터
     * @param includeDeleted 삭제 포함 여부
     * @param queryContext 정렬 + 페이징
     * @return ArchUnitTestSearchCriteria
     */
    public static ArchUnitTestSearchCriteria of(
            String keyword,
            ConventionId conventionId,
            String severity,
            boolean includeDeleted,
            QueryContext<ArchUnitTestSortKey> queryContext) {
        return new ArchUnitTestSearchCriteria(
                keyword,
                conventionId,
                severity,
                includeDeleted,
                queryContext != null
                        ? queryContext
                        : QueryContext.defaultOf(ArchUnitTestSortKey.defaultKey()));
    }

    /**
     * 컨벤션별 검색 조건 생성
     *
     * @param conventionId 컨벤션 ID
     * @param pageRequest 페이징
     * @return ArchUnitTestSearchCriteria
     */
    public static ArchUnitTestSearchCriteria byConvention(
            ConventionId conventionId, PageRequest pageRequest) {
        return new ArchUnitTestSearchCriteria(
                null,
                conventionId,
                null,
                false,
                QueryContext.of(
                        ArchUnitTestSortKey.defaultKey(),
                        SortDirection.defaultDirection(),
                        pageRequest));
    }

    /**
     * 심각도별 검색 조건 생성
     *
     * @param severity 심각도
     * @param pageRequest 페이징
     * @return ArchUnitTestSearchCriteria
     */
    public static ArchUnitTestSearchCriteria bySeverity(String severity, PageRequest pageRequest) {
        return new ArchUnitTestSearchCriteria(
                null,
                null,
                severity,
                false,
                QueryContext.of(
                        ArchUnitTestSortKey.defaultKey(),
                        SortDirection.defaultDirection(),
                        pageRequest));
    }

    /**
     * 키워드 검색 조건 생성
     *
     * @param keyword 검색 키워드
     * @param pageRequest 페이징
     * @return ArchUnitTestSearchCriteria
     */
    public static ArchUnitTestSearchCriteria byKeyword(String keyword, PageRequest pageRequest) {
        return new ArchUnitTestSearchCriteria(
                keyword,
                null,
                null,
                false,
                QueryContext.of(
                        ArchUnitTestSortKey.defaultKey(),
                        SortDirection.defaultDirection(),
                        pageRequest));
    }

    /**
     * 키워드 검색이 있는지 확인
     *
     * @return 키워드가 있으면 true
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }

    /**
     * 컨벤션 필터가 있는지 확인
     *
     * @return 컨벤션 필터가 있으면 true
     */
    public boolean hasConventionFilter() {
        return conventionId != null;
    }

    /**
     * 심각도 필터가 있는지 확인
     *
     * @return 심각도 필터가 있으면 true
     */
    public boolean hasSeverityFilter() {
        return severity != null && !severity.isBlank();
    }

    /**
     * SQL OFFSET 값 반환 (편의 메서드)
     *
     * @return offset
     */
    public long offset() {
        return queryContext.offset();
    }

    /**
     * 페이지 크기 반환 (편의 메서드)
     *
     * @return size
     */
    public int size() {
        return queryContext.size();
    }
}
