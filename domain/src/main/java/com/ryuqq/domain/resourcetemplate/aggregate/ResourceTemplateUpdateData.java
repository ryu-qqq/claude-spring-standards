package com.ryuqq.domain.resourcetemplate.aggregate;

import com.ryuqq.domain.resourcetemplate.vo.FileType;
import com.ryuqq.domain.resourcetemplate.vo.TemplateCategory;
import com.ryuqq.domain.resourcetemplate.vo.TemplateContent;
import com.ryuqq.domain.resourcetemplate.vo.TemplatePath;
import java.util.Optional;

/**
 * ResourceTemplateUpdateData - 리소스 템플릿 수정 데이터
 *
 * <p>Partial Update 지원을 위해 Optional 필드를 사용합니다.
 *
 * @author ryu-qqq
 */
public final class ResourceTemplateUpdateData {

    private final TemplateCategory category;
    private final TemplatePath filePath;
    private final FileType fileType;
    private final String description;
    private final TemplateContent templateContent;
    private final Boolean required;

    private ResourceTemplateUpdateData(Builder builder) {
        this.category = builder.category;
        this.filePath = builder.filePath;
        this.fileType = builder.fileType;
        this.description = builder.description;
        this.templateContent = builder.templateContent;
        this.required = builder.required;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<TemplateCategory> category() {
        return Optional.ofNullable(category);
    }

    public Optional<TemplatePath> filePath() {
        return Optional.ofNullable(filePath);
    }

    public Optional<FileType> fileType() {
        return Optional.ofNullable(fileType);
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public Optional<TemplateContent> templateContent() {
        return Optional.ofNullable(templateContent);
    }

    public Optional<Boolean> required() {
        return Optional.ofNullable(required);
    }

    /**
     * 업데이트할 내용이 있는지 확인
     *
     * @return 하나라도 값이 있으면 true
     */
    public boolean hasUpdates() {
        return category != null
                || filePath != null
                || fileType != null
                || description != null
                || templateContent != null
                || required != null;
    }

    public static final class Builder {

        private TemplateCategory category;
        private TemplatePath filePath;
        private FileType fileType;
        private String description;
        private TemplateContent templateContent;
        private Boolean required;

        private Builder() {}

        public Builder category(TemplateCategory category) {
            this.category = category;
            return this;
        }

        public Builder filePath(TemplatePath filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileType(FileType fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder templateContent(TemplateContent templateContent) {
            this.templateContent = templateContent;
            return this;
        }

        public Builder required(Boolean required) {
            this.required = required;
            return this;
        }

        public ResourceTemplateUpdateData build() {
            return new ResourceTemplateUpdateData(this);
        }
    }
}
