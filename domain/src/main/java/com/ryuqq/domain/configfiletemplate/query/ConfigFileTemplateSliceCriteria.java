package com.ryuqq.domain.configfiletemplate.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.configfiletemplate.vo.TemplateCategory;
import com.ryuqq.domain.configfiletemplate.vo.ToolType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;

/**
 * ConfigFileTemplateSliceCriteria - ConfigFileTemplate 슬라이스 조회 조건 (커서 기반)
 *
 * <p>ConfigFileTemplate 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 ConfigFileTemplate ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * <p><strong>필터링:</strong>
 *
 * <ul>
 *   <li>techStackIds: TechStack ID 필터 목록
 *   <li>toolTypes: Tool Type 필터 목록
 *   <li>categories: Template Category 필터 목록
 * </ul>
 *
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @param techStackIds TechStack ID 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param toolTypes Tool Type 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @param categories Template Category 필터 목록 (null 또는 빈 리스트면 전체 조회)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFileTemplateSliceCriteria(
        CursorPageRequest<Long> cursorPageRequest,
        List<TechStackId> techStackIds,
        List<ToolType> toolTypes,
        List<TemplateCategory> categories) {

    public ConfigFileTemplateSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
        techStackIds = techStackIds != null ? List.copyOf(techStackIds) : null;
        toolTypes = toolTypes != null ? List.copyOf(toolTypes) : null;
        categories = categories != null ? List.copyOf(categories) : null;
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 필터 없음)
     *
     * @param size 슬라이스 크기
     * @return ConfigFileTemplateSliceCriteria
     */
    public static ConfigFileTemplateSliceCriteria first(int size) {
        return new ConfigFileTemplateSliceCriteria(CursorPageRequest.first(size), null, null, null);
    }

    /**
     * 커서 기반 페이징 요청과 필터로 슬라이스 조건 생성
     *
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @param techStackIds TechStack ID 필터 목록
     * @param toolTypes Tool Type 필터 목록
     * @param categories Template Category 필터 목록
     * @return ConfigFileTemplateSliceCriteria
     */
    public static ConfigFileTemplateSliceCriteria of(
            CursorPageRequest<Long> cursorPageRequest,
            List<TechStackId> techStackIds,
            List<ToolType> toolTypes,
            List<TemplateCategory> categories) {
        return new ConfigFileTemplateSliceCriteria(
                cursorPageRequest, techStackIds, toolTypes, categories);
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
     * Tool Type 필터가 있는지 확인
     *
     * @return toolTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasToolTypes() {
        return toolTypes != null && !toolTypes.isEmpty();
    }

    /**
     * Category 필터가 있는지 확인
     *
     * @return categories가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
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
