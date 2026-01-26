package com.ryuqq.domain.convention.aggregate;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.convention.vo.ConventionVersion;
import com.ryuqq.domain.module.id.ModuleId;
import java.time.Instant;

/**
 * Convention - 코딩 컨벤션 Aggregate Root
 *
 * <p>모듈별 코딩 컨벤션을 정의합니다.
 *
 * <p>AGG-001: Lombok 금지 - 모든 접근자 메서드 직접 구현.
 *
 * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 제공.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class Convention {

    private ConventionId id;
    private ModuleId moduleId;
    private ConventionVersion version;
    private String description;
    private boolean active;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected Convention() {
        this.createdAt = null;
    }

    private Convention(
            ConventionId id,
            ModuleId moduleId,
            ConventionVersion version,
            String description,
            boolean active,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.moduleId = moduleId;
        this.version = version != null ? version : ConventionVersion.defaultVersion();
        this.description = description;
        this.active = active;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param moduleId 모듈 ID
     * @param version 컨벤션 버전
     * @param description 컨벤션 설명
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 Convention 인스턴스
     */
    public static Convention forNew(
            ModuleId moduleId, ConventionVersion version, String description, Instant now) {
        return new Convention(
                ConventionId.forNew(),
                moduleId,
                version,
                description,
                true,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 컨벤션 ID
     * @param moduleId 모듈 ID
     * @param version 컨벤션 버전
     * @param description 컨벤션 설명
     * @param active 활성화 여부
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return Convention 인스턴스
     */
    public static Convention of(
            ConventionId id,
            ModuleId moduleId,
            ConventionVersion version,
            String description,
            boolean active,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Convention(
                id, moduleId, version, description, active, deletionStatus, createdAt, updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 컨벤션 ID
     * @param moduleId 모듈 ID
     * @param version 컨벤션 버전
     * @param description 컨벤션 설명
     * @param active 활성화 여부
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 Convention 인스턴스
     */
    public static Convention reconstitute(
            ConventionId id,
            ModuleId moduleId,
            ConventionVersion version,
            String description,
            boolean active,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(id, moduleId, version, description, active, deletionStatus, createdAt, updatedAt);
    }

    /**
     * 신규 엔티티 여부 확인
     *
     * @return ID가 null이면 true
     */
    public boolean isNew() {
        return id.isNew();
    }

    /**
     * ID 할당 (영속화 후 호출)
     *
     * @param id 할당할 ID
     */
    public void assignId(ConventionId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 컨벤션 활성화
     *
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void activate(Instant now) {
        this.active = true;
        this.updatedAt = now;
    }

    /**
     * 컨벤션 비활성화
     *
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    /**
     * 컨벤션 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 컨벤션 복원
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
        this.updatedAt = now;
    }

    /**
     * 컨벤션 수정
     *
     * <p>ConventionUpdateData의 모든 필드를 사용하여 컨벤션을 업데이트합니다.
     *
     * @param updateData 수정 데이터
     * @param now 수정 시각
     */
    public void update(ConventionUpdateData updateData, Instant now) {
        this.moduleId = updateData.moduleId();
        this.version = updateData.version();
        this.description = updateData.description();
        this.active = updateData.active();
        this.updatedAt = now;
    }

    /**
     * 삭제 여부 확인
     *
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    // Getters
    public ConventionId id() {
        return id;
    }

    public ModuleId moduleId() {
        return moduleId;
    }

    public ConventionVersion version() {
        return version;
    }

    public String description() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    // Value Object 위임 메서드 (Law of Demeter 준수)
    // Persistence Layer Mapper에서 체이닝 방지: domain.id().value() 대신 domain.idValue() 사용

    /**
     * ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return ID 값
     */
    public Long idValue() {
        return id.value();
    }

    /**
     * Module ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Module ID 값
     */
    public Long moduleIdValue() {
        return moduleId.value();
    }

    /**
     * Version 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 버전 문자열
     */
    public String versionValue() {
        return version.value();
    }

    /**
     * 삭제 시각 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 삭제 시각 (활성 상태인 경우 null)
     */
    public Instant deletedAt() {
        return deletionStatus.deletedAt();
    }
}
