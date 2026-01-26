package com.ryuqq.adapter.out.persistence.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

/**
 * BaseAuditEntity - 감사 정보 공통 추상 클래스
 *
 * <p>모든 JPA 엔티티의 공통 감사 필드(생성일시, 수정일시)를 제공합니다.
 *
 * @author ryu-qqq
 */
@MappedSuperclass
public abstract class BaseAuditEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BaseAuditEntity() {}

    protected BaseAuditEntity(Instant createdAt, Instant updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
