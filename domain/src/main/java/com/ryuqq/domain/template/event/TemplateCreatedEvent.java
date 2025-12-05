package com.ryuqq.domain.template.event;

import com.ryuqq.domain.common.event.DomainEvent;
import com.ryuqq.domain.template.aggregate.template.Template;
import com.ryuqq.domain.template.vo.TemplateId;
import com.ryuqq.domain.template.vo.TemplateStatus;
import com.ryuqq.domain.template.vo.TemplateTitle;
import java.time.Instant;
import java.util.Objects;

/** Template 생성 이벤트. */
public record TemplateCreatedEvent(
        TemplateId templateId, TemplateTitle title, TemplateStatus status, Instant occurredAt)
        implements DomainEvent {

    public TemplateCreatedEvent {
        Objects.requireNonNull(templateId, "templateId must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }

    public static TemplateCreatedEvent from(Template template, Instant occurredAt) {
        Objects.requireNonNull(template, "template must not be null");
        return new TemplateCreatedEvent(
                template.id(), template.title(), template.status(), occurredAt);
    }

    public static TemplateCreatedEvent of(
            TemplateId templateId, TemplateTitle title, TemplateStatus status, Instant occurredAt) {
        return new TemplateCreatedEvent(templateId, title, status, occurredAt);
    }
}
