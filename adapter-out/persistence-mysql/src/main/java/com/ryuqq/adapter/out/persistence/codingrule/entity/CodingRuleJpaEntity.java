package com.ryuqq.adapter.out.persistence.codingrule.entity;

import com.ryuqq.adapter.out.persistence.common.entity.SoftDeletableEntity;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * CodingRuleJpaEntity - 코딩 규칙 JPA 엔티티
 *
 * <p>coding_rule 테이블과 매핑됩니다.
 *
 * <p>Long FK 전략을 사용하여 JPA 관계 어노테이션을 사용하지 않습니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "coding_rule")
public class CodingRuleJpaEntity extends SoftDeletableEntity {

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "convention_id", nullable = false)
    private Long conventionId;

    @Column(name = "code", length = 20, nullable = false)
    private String code;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 20, nullable = false)
    private RuleSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    private RuleCategory category;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "rationale", columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "auto_fixable", nullable = false)
    private boolean autoFixable;

    @Column(name = "applies_to", length = 500)
    private String appliesTo;

    protected CodingRuleJpaEntity() {}

    private CodingRuleJpaEntity(
            Long id,
            Long conventionId,
            String code,
            String name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            String appliesTo,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.conventionId = conventionId;
        this.code = code;
        this.name = name;
        this.severity = severity;
        this.category = category;
        this.description = description;
        this.rationale = rationale;
        this.autoFixable = autoFixable;
        this.appliesTo = appliesTo;
    }

    /**
     * 정적 팩토리 메서드 (LocalDateTime 기반 - 테스트 호환용)
     *
     * @param id 코딩 규칙 ID
     * @param conventionId 컨벤션 ID (Long FK)
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @param description 설명
     * @param rationale 근거
     * @param autoFixable 자동 수정 가능 여부
     * @param appliesTo 적용 대상 (쉼표 구분 문자열)
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @param deletedAt 삭제 일시
     * @return CodingRuleJpaEntity 인스턴스
     */
    public static CodingRuleJpaEntity of(
            Long id,
            Long conventionId,
            String code,
            String name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            String appliesTo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt) {
        return new CodingRuleJpaEntity(
                id,
                conventionId,
                code,
                name,
                severity,
                category,
                description,
                rationale,
                autoFixable,
                appliesTo,
                toInstant(createdAt),
                toInstant(updatedAt),
                toInstant(deletedAt));
    }

    /**
     * 정적 팩토리 메서드 (Instant 기반 - Mapper 사용)
     *
     * @param id 코딩 규칙 ID
     * @param conventionId 컨벤션 ID (Long FK)
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @param description 설명
     * @param rationale 근거
     * @param autoFixable 자동 수정 가능 여부
     * @param appliesTo 적용 대상 (쉼표 구분 문자열)
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @param deletedAt 삭제 일시
     * @return CodingRuleJpaEntity 인스턴스
     */
    public static CodingRuleJpaEntity ofInstant(
            Long id,
            Long conventionId,
            String code,
            String name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            String appliesTo,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new CodingRuleJpaEntity(
                id,
                conventionId,
                code,
                name,
                severity,
                category,
                description,
                rationale,
                autoFixable,
                appliesTo,
                createdAt,
                updatedAt,
                deletedAt);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(SYSTEM_ZONE).toInstant();
    }

    public Long getId() {
        return id;
    }

    public Long getConventionId() {
        return conventionId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public RuleSeverity getSeverity() {
        return severity;
    }

    public RuleCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getRationale() {
        return rationale;
    }

    public boolean isAutoFixable() {
        return autoFixable;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodingRuleJpaEntity that = (CodingRuleJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
