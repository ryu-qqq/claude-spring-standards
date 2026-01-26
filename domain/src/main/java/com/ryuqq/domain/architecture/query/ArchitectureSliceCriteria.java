package com.ryuqq.domain.architecture.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;

/**
 * ArchitectureSliceCriteria - Architecture 슬라이스 조회 조건 (커서 기반)
 *
 * <p>Architecture 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 Architecture ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * <p><strong>필터링:</strong>
 *
 * <ul>
 *   <li>techStackIds: TechStack ID 필터 목록
 * </ul>
 *
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @param techStackIds TechStack ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ArchitectureSliceCriteria(
        CursorPageRequest<Long> cursorPageRequest, List<TechStackId> techStackIds) {

    public ArchitectureSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return ArchitectureSliceCriteria
     */
    public static ArchitectureSliceCriteria first(int size) {
        return new ArchitectureSliceCriteria(CursorPageRequest.first(size), null);
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ArchitectureSliceCriteria
     */
    public static ArchitectureSliceCriteria afterId(Long cursorId, int size) {
        return new ArchitectureSliceCriteria(CursorPageRequest.afterId(cursorId, size), null);
    }

    /**
     * 커서 기반 페이징 요청으로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return ArchitectureSliceCriteria
     */
    public static ArchitectureSliceCriteria of(CursorPageRequest<Long> cursorPageRequest) {
        return new ArchitectureSliceCriteria(cursorPageRequest, null);
    }

    /**
     * 커서 기반 페이징 요청과 필터로 슬라이스 조건 생성
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param techStackIds TechStack ID 필터 목록
     * @return ArchitectureSliceCriteria
     */
    public static ArchitectureSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest, List<TechStackId> techStackIds) {
        return new ArchitectureSliceCriteria(cursorPageRequest, techStackIds);
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
     * TechStack ID 필터가 있는지 확인
     *
     * @return techStackIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasTechStackIds() {
        return techStackIds != null && !techStackIds.isEmpty();
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
