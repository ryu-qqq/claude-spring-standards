package com.ryuqq.domain.packagestructure.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;

/**
 * PackageStructureSliceCriteria - PackageStructure 슬라이스 조회 조건 (커서 기반)
 *
 * <p>PackageStructure 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p>모듈 ID 필터링을 지원합니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 PackageStructure ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param moduleIds 모듈 ID 필터 목록 (필터링 용도, nullable)
 * @param cursorPageRequest 커서 기반 페이징 요청 (ID 기반: Long)
 * @author ryu-qqq
 */
public record PackageStructureSliceCriteria(
        List<ModuleId> moduleIds, CursorPageRequest<Long> cursorPageRequest) {

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return PackageStructureSliceCriteria
     */
    public static PackageStructureSliceCriteria first(int size) {
        return new PackageStructureSliceCriteria(null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return PackageStructureSliceCriteria
     */
    public static PackageStructureSliceCriteria afterId(Long cursorId, int size) {
        return new PackageStructureSliceCriteria(null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * PackageStructureSliceCriteria 생성 (static factory method)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return PackageStructureSliceCriteria 인스턴스
     */
    public static PackageStructureSliceCriteria of(CursorPageRequest<Long> cursorPageRequest) {
        return new PackageStructureSliceCriteria(null, cursorPageRequest);
    }

    /**
     * PackageStructureSliceCriteria 생성 (필터 포함)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param moduleIds 모듈 ID 필터 목록
     * @return PackageStructureSliceCriteria 인스턴스
     */
    public static PackageStructureSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest, List<ModuleId> moduleIds) {
        return new PackageStructureSliceCriteria(moduleIds, cursorPageRequest);
    }

    /**
     * 모듈 ID 필터가 있는지 확인
     *
     * @return moduleIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasModuleIds() {
        return moduleIds != null && !moduleIds.isEmpty();
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
