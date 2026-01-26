package com.ryuqq.application.classtemplate.dto.response;

import java.util.List;

/**
 * ClassTemplateSliceResult - 클래스 템플릿 슬라이스 조회 결과
 *
 * <p>커서 기반 페이징으로 조회된 클래스 템플릿 목록을 담습니다.
 *
 * @param classTemplates 클래스 템플릿 목록
 * @param hasNext 다음 페이지 존재 여부
 * @param nextCursor 다음 페이지 커서 (다음 페이지가 없으면 null)
 * @author ryu-qqq
 */
public record ClassTemplateSliceResult(
        List<ClassTemplateResult> classTemplates, boolean hasNext, Long nextCursor) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 ClassTemplateSliceResult
     */
    public static ClassTemplateSliceResult empty() {
        return new ClassTemplateSliceResult(List.of(), false, null);
    }

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param classTemplates 클래스 템플릿 목록
     * @param hasNext 다음 페이지 존재 여부
     * @return ClassTemplateSliceResult
     */
    public static ClassTemplateSliceResult of(
            List<ClassTemplateResult> classTemplates, boolean hasNext) {
        Long nextCursor = null;
        if (hasNext && !classTemplates.isEmpty()) {
            nextCursor = classTemplates.get(classTemplates.size() - 1).id();
        }
        return new ClassTemplateSliceResult(classTemplates, hasNext, nextCursor);
    }

    /**
     * 결과가 비어있는지 확인
     *
     * @return 클래스 템플릿 목록이 비어있으면 true
     */
    public boolean isEmpty() {
        return classTemplates.isEmpty();
    }

    /**
     * 클래스 템플릿 개수 반환
     *
     * @return 클래스 템플릿 개수
     */
    public int size() {
        return classTemplates.size();
    }
}
