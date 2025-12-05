package com.ryuqq.domain.template.vo;

/** Template 상태 VO. */
public enum TemplateStatus {
    DRAFT(true),
    PUBLISHED(false),
    ARCHIVED(false);

    private final boolean editable;

    TemplateStatus(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }
}
