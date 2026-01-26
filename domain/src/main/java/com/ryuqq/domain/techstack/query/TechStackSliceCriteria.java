package com.ryuqq.domain.techstack.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.util.List;

/**
 * TechStackSliceCriteria - TechStack 슬라이스 조회 조건 (커서 기반)
 *
 * <p>TechStack 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p>TechStackSearchCriteria와 달리 커서 기반 페이징을 사용합니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 TechStack ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * <p><strong>필터링:</strong>
 *
 * <ul>
 *   <li>status: 상태 필터 (ACTIVE, DEPRECATED, ARCHIVED)
 *   <li>platformTypes: 플랫폼 타입 필터 목록 (BACKEND, FRONTEND, FULLSTACK, SDK)
 * </ul>
 *
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @param status 상태 필터 (null이면 전체 조회)
 * @param platformTypes 플랫폼 타입 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record TechStackSliceCriteria(
        CursorPageRequest<Long> cursorPageRequest,
        TechStackStatus status,
        List<PlatformType> platformTypes) {

    public TechStackSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return TechStackSliceCriteria
     */
    public static TechStackSliceCriteria first(int size) {
        return new TechStackSliceCriteria(CursorPageRequest.first(size), null, null);
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return TechStackSliceCriteria
     */
    public static TechStackSliceCriteria afterId(Long cursorId, int size) {
        return new TechStackSliceCriteria(CursorPageRequest.afterId(cursorId, size), null, null);
    }

    /**
     * 커서 기반 페이징 요청으로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return TechStackSliceCriteria
     */
    public static TechStackSliceCriteria of(CursorPageRequest<Long> cursorPageRequest) {
        return new TechStackSliceCriteria(cursorPageRequest, null, null);
    }

    /**
     * 커서 기반 페이징 요청과 필터로 슬라이스 조건 생성
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param status 상태 필터
     * @param platformTypes 플랫폼 타입 필터 목록
     * @return TechStackSliceCriteria
     */
    public static TechStackSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest,
            TechStackStatus status,
            List<PlatformType> platformTypes) {
        return new TechStackSliceCriteria(cursorPageRequest, status, platformTypes);
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
     * 상태 필터가 있는지 확인
     *
     * @return status가 null이 아니면 true
     */
    public boolean hasStatus() {
        return status != null;
    }

    /**
     * 플랫폼 타입 필터가 있는지 확인
     *
     * @return platformTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasPlatformTypes() {
        return platformTypes != null && !platformTypes.isEmpty();
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
