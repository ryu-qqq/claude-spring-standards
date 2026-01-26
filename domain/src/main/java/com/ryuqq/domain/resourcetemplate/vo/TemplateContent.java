package com.ryuqq.domain.resourcetemplate.vo;

/**
 * TemplateContent - 템플릿 내용 Value Object
 *
 * <p>리소스 템플릿의 실제 내용을 관리합니다. nullable 허용.
 *
 * @param value 템플릿 내용 (nullable)
 * @author ryu-qqq
 */
public record TemplateContent(String value) {

    private static final TemplateContent EMPTY = new TemplateContent(null);

    /**
     * 빈 템플릿 내용 반환
     *
     * @return 빈 TemplateContent
     */
    public static TemplateContent empty() {
        return EMPTY;
    }

    /**
     * TemplateContent 생성 팩토리 메서드
     *
     * @param value 템플릿 내용 (nullable)
     * @return TemplateContent 인스턴스
     */
    public static TemplateContent of(String value) {
        if (value == null || value.isBlank()) {
            return EMPTY;
        }
        return new TemplateContent(value);
    }

    /**
     * 내용이 비어있는지 확인
     *
     * @return 내용이 없거나 공백이면 true
     */
    public boolean isEmpty() {
        return value == null || value.isBlank();
    }

    /**
     * 내용이 있는지 확인
     *
     * @return 내용이 있으면 true
     */
    public boolean hasContent() {
        return !isEmpty();
    }

    /**
     * 내용의 길이를 반환
     *
     * @return 내용 길이 (null이면 0)
     */
    public int length() {
        return value == null ? 0 : value.length();
    }
}
