package com.ryuqq.domain.resourcetemplate.query;

import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import java.util.List;

/**
 * ResourceTemplateSliceCriteria - ResourceTemplate 슬라이스 조회 조건 (커서 기반)
 *
 * <p>ResourceTemplate 목록을 커서 기반으로 조회할 때 사용하는 조건입니다.
 *
 * <p><strong>커서 전략:</strong>
 *
 * <ul>
 *   <li>ID 기반: 마지막 항목의 ResourceTemplate ID를 커서로 사용
 *   <li>정렬: ID 내림차순 (DESC)으로 조회
 * </ul>
 *
 * @param moduleIds 필터링할 모듈 ID 목록 (optional)
 * @param categories 필터링할 카테고리 목록 (optional)
 * @param fileTypes 필터링할 파일 타입 목록 (optional)
 * @param cursorPageRequest 커서 기반 페이징 요청
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ResourceTemplateSliceCriteria(
        List<ModuleId> moduleIds,
        List<TemplateCategory> categories,
        List<FileType> fileTypes,
        CursorPageRequest<Long> cursorPageRequest) {

    public ResourceTemplateSliceCriteria {
        if (cursorPageRequest == null) {
            throw new IllegalArgumentException("cursorPageRequest must not be null");
        }
    }

    /**
     * 기본 슬라이스 조건 생성 (첫 페이지, 전체 조회)
     *
     * @param size 슬라이스 크기
     * @return ResourceTemplateSliceCriteria
     */
    public static ResourceTemplateSliceCriteria first(int size) {
        return new ResourceTemplateSliceCriteria(null, null, null, CursorPageRequest.first(size));
    }

    /**
     * ID 기반 커서로 슬라이스 조건 생성
     *
     * @param cursorId 커서 ID (마지막 항목의 ID)
     * @param size 슬라이스 크기
     * @return ResourceTemplateSliceCriteria
     */
    public static ResourceTemplateSliceCriteria afterId(Long cursorId, int size) {
        return new ResourceTemplateSliceCriteria(
                null, null, null, CursorPageRequest.afterId(cursorId, size));
    }

    /**
     * 전체 조건으로 슬라이스 조건 생성
     *
     * @param moduleIds 모듈 ID 목록 (nullable)
     * @param categories 카테고리 목록 (nullable)
     * @param fileTypes 파일 타입 목록 (nullable)
     * @param cursorPageRequest 커서 기반 페이징 요청
     * @return ResourceTemplateSliceCriteria
     */
    public static ResourceTemplateSliceCriteria of(
            List<ModuleId> moduleIds,
            List<TemplateCategory> categories,
            List<FileType> fileTypes,
            CursorPageRequest<Long> cursorPageRequest) {
        return new ResourceTemplateSliceCriteria(
                moduleIds, categories, fileTypes, cursorPageRequest);
    }

    /**
     * 모듈 ID 필터 존재 여부 확인
     *
     * @return moduleIds가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasModuleFilter() {
        return moduleIds != null && !moduleIds.isEmpty();
    }

    /**
     * 카테고리 필터 존재 여부 확인
     *
     * @return categories가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasCategoryFilter() {
        return categories != null && !categories.isEmpty();
    }

    /**
     * 파일 타입 필터 존재 여부 확인
     *
     * @return fileTypes가 null이 아니고 비어있지 않으면 true
     */
    public boolean hasFileTypeFilter() {
        return fileTypes != null && !fileTypes.isEmpty();
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
