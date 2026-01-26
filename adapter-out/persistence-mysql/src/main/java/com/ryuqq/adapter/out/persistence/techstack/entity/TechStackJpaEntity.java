package com.ryuqq.adapter.out.persistence.techstack.entity;

import com.ryuqq.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

/**
 * TechStackJpaEntity - 기술 스택 JPA 엔티티
 *
 * <p>tech_stack 테이블과 매핑됩니다.
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "tech_stack")
public class TechStackJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "language_type", length = 50, nullable = false)
    private String languageType;

    @Column(name = "language_version", length = 20, nullable = false)
    private String languageVersion;

    @Column(name = "language_features", columnDefinition = "JSON")
    private String languageFeatures;

    @Column(name = "framework_type", length = 50, nullable = false)
    private String frameworkType;

    @Column(name = "framework_version", length = 20, nullable = false)
    private String frameworkVersion;

    @Column(name = "framework_modules", columnDefinition = "JSON")
    private String frameworkModules;

    @Column(name = "platform_type", length = 50, nullable = false)
    private String platformType;

    @Column(name = "runtime_environment", length = 50, nullable = false)
    private String runtimeEnvironment;

    @Column(name = "build_tool_type", length = 50, nullable = false)
    private String buildToolType;

    @Column(name = "build_config_file", length = 100, nullable = false)
    private String buildConfigFile;

    @Column(name = "reference_links", columnDefinition = "JSON")
    private String referenceLinks;

    protected TechStackJpaEntity() {}

    private TechStackJpaEntity(
            Long id,
            String name,
            String status,
            String languageType,
            String languageVersion,
            String languageFeatures,
            String frameworkType,
            String frameworkVersion,
            String frameworkModules,
            String platformType,
            String runtimeEnvironment,
            String buildToolType,
            String buildConfigFile,
            String referenceLinks,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.name = name;
        this.status = status;
        this.languageType = languageType;
        this.languageVersion = languageVersion;
        this.languageFeatures = languageFeatures;
        this.frameworkType = frameworkType;
        this.frameworkVersion = frameworkVersion;
        this.frameworkModules = frameworkModules;
        this.platformType = platformType;
        this.runtimeEnvironment = runtimeEnvironment;
        this.buildToolType = buildToolType;
        this.buildConfigFile = buildConfigFile;
        this.referenceLinks = referenceLinks;
    }

    public static TechStackJpaEntity of(
            Long id,
            String name,
            String status,
            String languageType,
            String languageVersion,
            String languageFeatures,
            String frameworkType,
            String frameworkVersion,
            String frameworkModules,
            String platformType,
            String runtimeEnvironment,
            String buildToolType,
            String buildConfigFile,
            String referenceLinks,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new TechStackJpaEntity(
                id,
                name,
                status,
                languageType,
                languageVersion,
                languageFeatures,
                frameworkType,
                frameworkVersion,
                frameworkModules,
                platformType,
                runtimeEnvironment,
                buildToolType,
                buildConfigFile,
                referenceLinks,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getLanguageType() {
        return languageType;
    }

    public String getLanguageVersion() {
        return languageVersion;
    }

    public String getLanguageFeatures() {
        return languageFeatures;
    }

    public String getFrameworkType() {
        return frameworkType;
    }

    public String getFrameworkVersion() {
        return frameworkVersion;
    }

    public String getFrameworkModules() {
        return frameworkModules;
    }

    public String getPlatformType() {
        return platformType;
    }

    public String getRuntimeEnvironment() {
        return runtimeEnvironment;
    }

    public String getBuildToolType() {
        return buildToolType;
    }

    public String getBuildConfigFile() {
        return buildConfigFile;
    }

    public String getReferenceLinks() {
        return referenceLinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TechStackJpaEntity that = (TechStackJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
