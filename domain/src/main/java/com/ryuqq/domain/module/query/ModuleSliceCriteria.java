package com.ryuqq.domain.module.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.layer.id.LayerId;
import java.util.List;

/**
 * ModuleSliceCriteria - Module 슬라이스 조회 조건 (커서 기반)
 *
 * <p>Module 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p>레이어 ID 필터링을 지원합니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 Module ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param layerIds 필터링할 레이어 ID 목록 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleSliceCriteria(
        List<LayerId> layerIds, CursorPageRequest<Long> cursorPageRequest) {

    public ModuleSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return ModuleSliceCriteria
     */
    public static ModuleSliceCriteria first(int size) {
        return new ModuleSliceCriteria(null, CursorPageRequest.first(size));
    }

    /**
     * 레이어 ID로 필터링된 슬라이스 조건 생성 (첫 페이지)
     *
     * @param layerId 레이어 ID
     * @param size 슬라이스 크기
     * @return ModuleSliceCriteria
     */
    public static ModuleSliceCriteria byLayer(LayerId layerId, int size) {
        return new ModuleSliceCriteria(List.of(layerId), CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성 (필터 없음)
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ModuleSliceCriteria
     */
    public static ModuleSliceCriteria afterId(Long cursorId, int size) {
        return new ModuleSliceCriteria(null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 레이어 ID와 커서로 슬라이스 조건 생성
     *
     * @param layerId 레이어 ID
     * @param cursorId 커서 ID
     * @param size 슬라이스 크기
     * @return ModuleSliceCriteria
     */
    public static ModuleSliceCriteria byLayerAfterCursor(LayerId layerId, Long cursorId, int size) {
        return new ModuleSliceCriteria(List.of(layerId), CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * ModuleSliceCriteria 생성 (static factory method)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return ModuleSliceCriteria 인스턴스
     */
    public static ModuleSliceCriteria of(CursorPageRequest<Long> cursorPageRequest) {
        return new ModuleSliceCriteria(null, cursorPageRequest);
    }

    /**
     * ModuleSliceCriteria 생성 (필터 포함)
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param layerIds 레이어 ID 목록
     * @return ModuleSliceCriteria 인스턴스
     */
    public static ModuleSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest, List<LayerId> layerIds) {
        return new ModuleSliceCriteria(layerIds, cursorPageRequest);
    }

    /**
     * 레이어 ID 필터가 있는지 확인
     *
     * @return layerIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasLayerIds() {
        return layerIds != null && !layerIds.isEmpty();
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
