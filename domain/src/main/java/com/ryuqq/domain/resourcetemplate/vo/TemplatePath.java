package com.ryuqq.domain.resourcetemplate.vo;

/**
 * TemplatePath - 템플릿 파일 경로 Value Object
 *
 * <p>리소스 템플릿의 파일 경로를 검증하고 관리합니다.
 *
 * @param value 파일 경로 (not blank, max 255)
 * @author ryu-qqq
 */
public record TemplatePath(String value) {

    private static final int MAX_LENGTH = 255;

    public TemplatePath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TemplatePath value must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "TemplatePath value must not exceed " + MAX_LENGTH + " characters");
        }
    }

    /**
     * TemplatePath 생성 팩토리 메서드
     *
     * @param value 파일 경로
     * @return TemplatePath 인스턴스
     */
    public static TemplatePath of(String value) {
        return new TemplatePath(value);
    }

    /**
     * 파일명만 추출합니다.
     *
     * @return 파일명
     */
    public String fileName() {
        int lastSlash = value.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < value.length() - 1) {
            return value.substring(lastSlash + 1);
        }
        return value;
    }

    /**
     * 디렉토리 경로만 추출합니다.
     *
     * @return 디렉토리 경로 (파일명 제외)
     */
    public String directory() {
        int lastSlash = value.lastIndexOf('/');
        if (lastSlash > 0) {
            return value.substring(0, lastSlash);
        }
        return "";
    }

    /**
     * 확장자를 추출합니다.
     *
     * @return 확장자 (점 포함, 없으면 빈 문자열)
     */
    public String extension() {
        String name = fileName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(lastDot);
        }
        return "";
    }
}
