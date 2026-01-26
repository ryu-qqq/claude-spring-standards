package com.ryuqq.domain.resourcetemplate.vo;

/**
 * FileType - 리소스 템플릿 파일 타입 enum
 *
 * <p>리소스 템플릿 파일의 확장자/타입을 정의합니다.
 *
 * @author ryu-qqq
 */
public enum FileType {

    /** YAML 설정 파일 */
    YAML("yaml", "YAML"),

    /** Properties 설정 파일 */
    PROPERTIES("properties", "Properties"),

    /** JSON 파일 */
    JSON("json", "JSON"),

    /** Gradle 빌드 파일 */
    GRADLE("gradle", "Gradle"),

    /** 기타 파일 */
    OTHER("other", "Other");

    private final String extension;
    private final String displayName;

    FileType(String extension, String displayName) {
        this.extension = extension;
        this.displayName = displayName;
    }

    public String extension() {
        return extension;
    }

    public String displayName() {
        return displayName;
    }

    /**
     * 확장자로 FileType 찾기
     *
     * @param extension 파일 확장자
     * @return FileType (없으면 OTHER)
     */
    public static FileType fromExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            return OTHER;
        }
        String ext = extension.toLowerCase(java.util.Locale.ROOT).replace(".", "");
        for (FileType type : values()) {
            if (type.extension.equals(ext)) {
                return type;
            }
        }
        // yml도 YAML로 처리
        if ("yml".equals(ext)) {
            return YAML;
        }
        return OTHER;
    }

    public boolean isYaml() {
        return this == YAML;
    }

    public boolean isProperties() {
        return this == PROPERTIES;
    }

    public boolean isJson() {
        return this == JSON;
    }

    public boolean isGradle() {
        return this == GRADLE;
    }
}
