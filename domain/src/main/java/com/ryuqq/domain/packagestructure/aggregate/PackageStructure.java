package com.ryuqq.domain.packagestructure.aggregate;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.time.Instant;

/**
 * PackageStructure - 패키지 구조 Aggregate Root
 *
 * <p>모듈 내 패키지 구조를 정의합니다.
 *
 * @author ryu-qqq
 */
public class PackageStructure {

    private PackageStructureId id;
    private ModuleId moduleId;
    private PathPattern pathPattern;
    private String description;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected PackageStructure() {
        this.createdAt = null;
    }

    private PackageStructure(
            PackageStructureId id,
            ModuleId moduleId,
            PathPattern pathPattern,
            String description,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.moduleId = moduleId;
        this.pathPattern = pathPattern;
        this.description = description;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param description 설명
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 PackageStructure 인스턴스
     */
    public static PackageStructure forNew(
            ModuleId moduleId, PathPattern pathPattern, String description, Instant now) {
        return new PackageStructure(
                PackageStructureId.forNew(),
                moduleId,
                pathPattern,
                description,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 패키지 구조 ID
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param description 설명
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return PackageStructure 인스턴스
     */
    public static PackageStructure of(
            PackageStructureId id,
            ModuleId moduleId,
            PathPattern pathPattern,
            String description,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new PackageStructure(
                id, moduleId, pathPattern, description, deletionStatus, createdAt, updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 패키지 구조 ID
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param description 설명
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 PackageStructure 인스턴스
     */
    public static PackageStructure reconstitute(
            PackageStructureId id,
            ModuleId moduleId,
            PathPattern pathPattern,
            String description,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(id, moduleId, pathPattern, description, deletionStatus, createdAt, updatedAt);
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
    public void assignId(PackageStructureId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    // Getters
    public PackageStructureId id() {
        return id;
    }

    public ModuleId moduleId() {
        return moduleId;
    }

    public PathPattern pathPattern() {
        return pathPattern;
    }

    public String description() {
        return description;
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

    /**
     * 소프트 삭제
     *
     * @param now 삭제 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제 복원
     *
     * @param now 복원 시각
     */
    public void restore(Instant now) {
        this.deletionStatus = DeletionStatus.active();
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

    /**
     * 패키지 구조 수정
     *
     * <p>요청으로 들어온 데이터를 기반으로 필드를 업데이트합니다. JPA의 더티체킹을 활용하여 변경사항을 자동으로 감지합니다.
     *
     * @param updateData 수정할 데이터 (모든 필드 필수)
     * @param now 수정 시각
     */
    public void update(PackageStructureUpdateData updateData, Instant now) {
        this.pathPattern = updateData.pathPattern();
        this.description = updateData.description();
        this.updatedAt = now;
    }

    // Value Object 위임 메서드 (Law of Demeter 준수)
    // Persistence Layer Mapper에서 체이닝 방지: domain.id().value() 대신 domain.idValue() 사용

    /**
     * ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return ID 값 (신규인 경우 null)
     */
    public Long idValue() {
        return id != null ? id.value() : null;
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
     * PathPattern 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 경로 패턴 문자열
     */
    public String pathPatternValue() {
        return pathPattern.value();
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
