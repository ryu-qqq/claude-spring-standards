package com.ryuqq.domain.template.aggregate.template;

import com.ryuqq.domain.common.event.DomainEvent;
import com.ryuqq.domain.template.event.TemplateCreatedEvent;
import com.ryuqq.domain.template.exception.TemplateConstraintViolationException;
import com.ryuqq.domain.template.exception.TemplateErrorCode;
import com.ryuqq.domain.template.exception.TemplateNotEditableException;
import com.ryuqq.domain.template.exception.TemplateSectionNotFoundException;
import com.ryuqq.domain.template.exception.TemplateStateException;
import com.ryuqq.domain.template.vo.TemplateBody;
import com.ryuqq.domain.template.vo.TemplateDescription;
import com.ryuqq.domain.template.vo.TemplateId;
import com.ryuqq.domain.template.vo.TemplateSectionId;
import com.ryuqq.domain.template.vo.TemplateStatus;
import com.ryuqq.domain.template.vo.TemplateTitle;
import com.ryuqq.domain.template.vo.TemplateVersion;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** 템플릿 Aggregate Root. */
public class Template {

    private static final int MAX_SECTION_COUNT = 50;

    private final Clock clock;
    private final TemplateId id;
    private TemplateTitle title;
    private TemplateDescription description;
    private TemplateStatus status;
    private TemplateVersion version;
    private final List<TemplateSection> sections;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private final Instant createdAt;
    private Instant updatedAt;

    private Template(
            Clock clock,
            TemplateId id,
            TemplateTitle title,
            TemplateDescription description,
            TemplateStatus status,
            TemplateVersion version,
            List<TemplateSection> sections,
            Instant createdAt,
            Instant updatedAt) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
        this.id = Objects.requireNonNull(id, "templateId must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.version = Objects.requireNonNull(version, "version must not be null");
        this.sections =
                new ArrayList<>(Objects.requireNonNull(sections, "sections must not be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        validateSectionConstraints(this.sections);
    }

    public static Template forNew(
            Clock clock, TemplateTitle title, TemplateDescription description) {
        return forNew(clock, title, description, List.of());
    }

    public static Template forNew(
            Clock clock,
            TemplateTitle title,
            TemplateDescription description,
            List<TemplateSection> sections) {
        Objects.requireNonNull(clock, "clock must not be null");
        Instant now = clock.instant();
        List<TemplateSection> normalizedSections = normalizeSectionsForCreation(sections);
        Template template =
                new Template(
                        clock,
                        TemplateId.newId(),
                        title,
                        description,
                        TemplateStatus.DRAFT,
                        TemplateVersion.initial(),
                        normalizedSections,
                        now,
                        now);
        template.registerEvent(TemplateCreatedEvent.from(template, now));
        return template;
    }

    public static Template of(
            Clock clock,
            TemplateId id,
            TemplateTitle title,
            TemplateDescription description,
            TemplateStatus status,
            TemplateVersion version,
            List<TemplateSection> sections,
            Instant createdAt,
            Instant updatedAt) {
        requireIdentifier(id);
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Template(
                clock, id, title, description, status, version, sections, createdAt, updatedAt);
    }

    public static Template reconstitute(
            Clock clock,
            TemplateId id,
            TemplateTitle title,
            TemplateDescription description,
            TemplateStatus status,
            TemplateVersion version,
            List<TemplateSection> sections,
            Instant createdAt,
            Instant updatedAt) {
        return of(clock, id, title, description, status, version, sections, createdAt, updatedAt);
    }

    public TemplateId id() {
        return id;
    }

    public TemplateTitle title() {
        return title;
    }

    public TemplateDescription description() {
        return description;
    }

    public TemplateStatus status() {
        return status;
    }

    public TemplateVersion version() {
        return version;
    }

    public int sectionCount() {
        return sections.size();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public List<TemplateSection> sections() {
        return List.copyOf(sections);
    }

    public boolean isEditable() {
        return status.isEditable();
    }

    public void rename(TemplateTitle newTitle) {
        Objects.requireNonNull(newTitle, "newTitle must not be null");
        this.title = newTitle;
        touch();
    }

    public void changeDescription(TemplateDescription newDescription) {
        Objects.requireNonNull(newDescription, "newDescription must not be null");
        this.description = newDescription;
        touch();
    }

    public TemplateSection addSection(TemplateTitle sectionTitle, TemplateBody body) {
        ensureEditable();
        Objects.requireNonNull(sectionTitle, "sectionTitle must not be null");
        Objects.requireNonNull(body, "body must not be null");
        ensureSectionLimit(sections.size() + 1);
        ensureUniqueTitle(sectionTitle, null);

        TemplateSection newSection =
                TemplateSection.forNew(sectionTitle, body, sections.size() + 1);
        sections.add(newSection);
        touch();
        return newSection;
    }

    public void rewriteSection(
            TemplateSectionId sectionId, TemplateTitle newTitle, TemplateBody newBody) {
        ensureEditable();
        Objects.requireNonNull(sectionId, "sectionId must not be null");
        Objects.requireNonNull(newTitle, "newTitle must not be null");
        Objects.requireNonNull(newBody, "newBody must not be null");

        TemplateSection existing = findSection(sectionId);
        ensureUniqueTitle(newTitle, sectionId);

        TemplateSection updated = existing.rename(newTitle).rewrite(newBody);
        replaceSection(existing, updated);
        touch();
    }

    public void removeSection(TemplateSectionId sectionId) {
        ensureEditable();
        Objects.requireNonNull(sectionId, "sectionId must not be null");
        TemplateSection existing = findSection(sectionId);
        sections.remove(existing);
        reindexSections();
        touch();
    }

    public void reorderSection(TemplateSectionId sectionId, int newOrdering) {
        ensureEditable();
        Objects.requireNonNull(sectionId, "sectionId must not be null");
        if (newOrdering < 1 || newOrdering > sections.size()) {
            throw new TemplateConstraintViolationException(
                    TemplateErrorCode.TEMPLATE_SECTION_ORDER_CONFLICT,
                    "Section ordering must be between 1 and current section count",
                    Map.of("requestedOrdering", newOrdering, "sectionCount", sections.size()));
        }
        TemplateSection existing = findSection(sectionId);
        sections.remove(existing);
        sections.add(newOrdering - 1, existing.withOrdering(newOrdering));
        reindexSections();
        touch();
    }

    public void publish() {
        if (status == TemplateStatus.PUBLISHED) {
            return;
        }
        if (status != TemplateStatus.DRAFT) {
            throw new TemplateStateException(
                    TemplateErrorCode.TEMPLATE_STATE_TRANSITION_NOT_ALLOWED,
                    "Only draft templates can be published",
                    this.id);
        }
        ensureHasAtLeastOneSection();
        this.status = TemplateStatus.PUBLISHED;
        touch();
    }

    public void archive() {
        if (status == TemplateStatus.ARCHIVED) {
            return;
        }
        if (status == TemplateStatus.DRAFT) {
            throw new TemplateStateException(
                    TemplateErrorCode.TEMPLATE_STATE_TRANSITION_NOT_ALLOWED,
                    "Draft templates cannot be archived directly",
                    this.id);
        }
        this.status = TemplateStatus.ARCHIVED;
        touch();
    }

    public void revertToDraft() {
        if (status == TemplateStatus.DRAFT) {
            return;
        }
        this.status = TemplateStatus.DRAFT;
        touch();
    }

    public TemplateSection section(TemplateSectionId sectionId) {
        return findSection(sectionId);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void touch() {
        this.updatedAt = clock.instant();
        this.version = version.next();
    }

    private TemplateSection findSection(TemplateSectionId sectionId) {
        return sections.stream()
                .filter(section -> section.id().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new TemplateSectionNotFoundException(id, sectionId));
    }

    private void replaceSection(TemplateSection existing, TemplateSection updated) {
        int index = sections.indexOf(existing);
        sections.set(index, updated.withOrdering(existing.ordering()));
    }

    private void ensureEditable() {
        if (!status.isEditable()) {
            throw new TemplateNotEditableException(id, status);
        }
    }

    private void ensureHasAtLeastOneSection() {
        if (sections.isEmpty()) {
            throw new TemplateConstraintViolationException(
                    TemplateErrorCode.TEMPLATE_SECTION_REQUIRED_FOR_PUBLICATION,
                    "Template must have at least one section before publication",
                    Map.of("templateId", id.asString()));
        }
    }

    private static void ensureSectionLimit(int newSize) {
        if (newSize > MAX_SECTION_COUNT) {
            throw new TemplateConstraintViolationException(
                    TemplateErrorCode.TEMPLATE_SECTION_LIMIT_EXCEEDED,
                    "Template allows up to " + MAX_SECTION_COUNT + " sections",
                    Map.of("attemptedCount", newSize));
        }
    }

    private void ensureUniqueTitle(TemplateTitle candidate, TemplateSectionId exclude) {
        boolean duplicated =
                sections.stream()
                        .filter(section -> exclude == null || !section.id().equals(exclude))
                        .anyMatch(section -> section.title().equals(candidate));
        if (duplicated) {
            throw new TemplateConstraintViolationException(
                    TemplateErrorCode.TEMPLATE_TITLE_DUPLICATED,
                    "Section title must be unique within a template",
                    Map.of("title", candidate.value()));
        }
    }

    private void registerEvent(DomainEvent domainEvent) {
        domainEvents.add(domainEvent);
    }

    private void reindexSections() {
        List<TemplateSection> reindexed = new ArrayList<>(sections.size());
        int order = 1;
        List<TemplateSection> sorted =
                sections.stream()
                        .sorted(Comparator.comparingInt(TemplateSection::ordering))
                        .collect(Collectors.toList());
        for (TemplateSection section : sorted) {
            reindexed.add(section.withOrdering(order++));
        }
        sections.clear();
        sections.addAll(reindexed);
    }

    private static List<TemplateSection> normalizeSectionsForCreation(
            List<TemplateSection> sections) {
        Objects.requireNonNull(sections, "sections must not be null");
        ensureSectionLimit(sections.size());
        List<TemplateSection> normalized = new ArrayList<>(sections);
        normalized.sort(Comparator.comparingInt(TemplateSection::ordering));
        List<TemplateSection> sanitized = new ArrayList<>(normalized.size());
        int order = 1;
        for (TemplateSection section : normalized) {
            sanitized.add(
                    TemplateSection.reconstitute(
                            section.id(), section.title(), section.body(), order++));
        }
        return sanitized;
    }

    private static void requireIdentifier(TemplateId id) {
        Objects.requireNonNull(id, "templateId must not be null");
    }

    private static void validateSectionConstraints(List<TemplateSection> sections) {
        ensureSectionLimit(sections.size());
        ensureSequentialOrder(sections);
        ensureUniqueTitles(sections);
    }

    private static void ensureSequentialOrder(List<TemplateSection> sections) {
        Set<Integer> orderings =
                sections.stream().map(TemplateSection::ordering).collect(Collectors.toSet());
        if (orderings.size() != sections.size()) {
            throw new TemplateConstraintViolationException(
                    TemplateErrorCode.TEMPLATE_SECTION_ORDER_CONFLICT,
                    "Section ordering must be unique per template");
        }
        int minOrder = sections.stream().mapToInt(TemplateSection::ordering).min().orElse(1);
        int maxOrder = sections.stream().mapToInt(TemplateSection::ordering).max().orElse(0);
        if (minOrder != 1 || maxOrder != sections.size()) {
            throw new TemplateConstraintViolationException(
                    TemplateErrorCode.TEMPLATE_SECTION_ORDER_CONFLICT,
                    "Section ordering must be contiguous and start from 1");
        }
    }

    private static void ensureUniqueTitles(List<TemplateSection> sections) {
        Set<String> titles =
                sections.stream()
                        .map(section -> section.title().value().toLowerCase(Locale.ROOT))
                        .collect(Collectors.toSet());
        if (titles.size() != sections.size()) {
            throw new TemplateConstraintViolationException(
                    TemplateErrorCode.TEMPLATE_TITLE_DUPLICATED,
                    "Section titles must be unique per template");
        }
    }
}
