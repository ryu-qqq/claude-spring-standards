package com.ryuqq.domain.template.aggregate.template;

import com.ryuqq.domain.template.vo.TemplateBody;
import com.ryuqq.domain.template.vo.TemplateSectionId;
import com.ryuqq.domain.template.vo.TemplateTitle;
import java.util.Objects;

/** Template Section Entity. */
public record TemplateSection(
        TemplateSectionId id, TemplateTitle title, TemplateBody body, int ordering) {

    private static final int MIN_ORDER = 1;

    public TemplateSection {
        Objects.requireNonNull(id, "TemplateSection id must not be null");
        Objects.requireNonNull(title, "TemplateSection title must not be null");
        Objects.requireNonNull(body, "TemplateSection body must not be null");
        if (ordering < MIN_ORDER) {
            throw new IllegalArgumentException("Template section ordering must be >= " + MIN_ORDER);
        }
    }

    public static TemplateSection forNew(TemplateTitle title, TemplateBody body, int ordering) {
        return new TemplateSection(TemplateSectionId.newId(), title, body, ordering);
    }

    public static TemplateSection reconstitute(
            TemplateSectionId id, TemplateTitle title, TemplateBody body, int ordering) {
        return new TemplateSection(id, title, body, ordering);
    }

    public TemplateSection withOrdering(int ordering) {
        return new TemplateSection(this.id, this.title, this.body, ordering);
    }

    public TemplateSection rename(TemplateTitle newTitle) {
        return new TemplateSection(this.id, newTitle, this.body, this.ordering);
    }

    public TemplateSection rewrite(TemplateBody newBody) {
        return new TemplateSection(this.id, this.title, newBody, this.ordering);
    }
}
