package com.ryuqq.adapter.out.persistence.archunittest.entity;

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
 * ArchUnitTestJpaEntity - ArchUnit 테스트 JPA 엔티티
 *
 * <p>archunit_test 테이블과 매핑됩니다.
 *
 * <p>PackageStructure의 하위 엔티티입니다 (structureId FK).
 *
 * @author ryu-qqq
 */
@Entity
@Table(name = "archunit_test")
public class ArchUnitTestJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "structure_id", nullable = false)
    private Long structureId;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "test_class_name", length = 255)
    private String testClassName;

    @Column(name = "test_method_name", length = 255)
    private String testMethodName;

    @Column(name = "test_code", columnDefinition = "TEXT", nullable = false)
    private String testCode;

    @Column(name = "severity", length = 20)
    private String severity;

    protected ArchUnitTestJpaEntity() {}

    private ArchUnitTestJpaEntity(
            Long id,
            Long structureId,
            String code,
            String name,
            String description,
            String testClassName,
            String testMethodName,
            String testCode,
            String severity,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.structureId = structureId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.testClassName = testClassName;
        this.testMethodName = testMethodName;
        this.testCode = testCode;
        this.severity = severity;
    }

    public static ArchUnitTestJpaEntity of(
            Long id,
            Long structureId,
            String code,
            String name,
            String description,
            String testClassName,
            String testMethodName,
            String testCode,
            String severity,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ArchUnitTestJpaEntity(
                id,
                structureId,
                code,
                name,
                description,
                testClassName,
                testMethodName,
                testCode,
                severity,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getStructureId() {
        return structureId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public String getTestMethodName() {
        return testMethodName;
    }

    public String getTestCode() {
        return testCode;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchUnitTestJpaEntity that = (ArchUnitTestJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
