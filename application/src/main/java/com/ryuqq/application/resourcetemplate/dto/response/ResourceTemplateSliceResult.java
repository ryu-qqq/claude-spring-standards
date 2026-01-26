package com.ryuqq.application.resourcetemplate.dto.response;

import java.util.List;

/**
 * ResourceTemplateSliceResult - 리소스 템플릿 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 리소스 템플릿 목록을 담습니다.
 *
 * @param resourceTemplates 리소스 템플릿 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ResourceTemplateSliceResult(
        List<ResourceTemplateResult> resourceTemplates, boolean hasNext, Long nextCursor) {

    /** 방어적 복사를 위한 정규 생성자 */
    public ResourceTemplateSliceResult {
        resourceTemplates = List.copyOf(resourceTemplates);
    }

    /**
     * 빈 결과 생성
     *
     * @return 빈 ResourceTemplateSliceResult
     */
    public static ResourceTemplateSliceResult empty() {
        return new ResourceTemplateSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param resourceTemplates 리소스 템플릿 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return ResourceTemplateSliceResult
     */
    public static ResourceTemplateSliceResult of(
            List<ResourceTemplateResult> resourceTemplates, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !resourceTemplates.isEmpty()) {
            nextCursor = resourceTemplates.get(resourceTemplates.size() - 1).id();
        }
        return new ResourceTemplateSliceResult(resourceTemplates, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 리소스 템플릿 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return resourceTemplates.isEmpty();
    }

    /**
     * 리소스 템플릿 개수 반환
     *
     * @return 리소스 템플릿 개수
     */
    public int size() {
        return resourceTemplates.size();
    }
}
