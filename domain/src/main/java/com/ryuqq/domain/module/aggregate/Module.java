package com.ryuqq.domain.module.aggregate;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.BuildIdentifier;
import com.ryuqq.domain.module.vo.ModuleDescription;
import com.ryuqq.domain.module.vo.ModuleName;
import com.ryuqq.domain.module.vo.ModulePath;
import java.time.Instant;

/**
 * Module - 모듈 Aggregate Root
 *
 * <p>빌드 시스템에 독립적인 모듈 정의를 관리합니다.
 *
 * <p>Layer를 FK로 참조하여 동적 레이어 구성을 지원합니다.
 *
 * @author ryu-qqq
 */
public class Module {

    private ModuleId id;
    private LayerId layerId;
    private ModuleId parentModuleId;
    private ModuleName name;
    private ModuleDescription description;
    private ModulePath modulePath;
    private BuildIdentifier buildIdentifier;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected Module() {
        this.createdAt = null;
    }

    private Module(
            ModuleId id,
            LayerId layerId,
            ModuleId parentModuleId,
            ModuleName name,
            ModuleDescription description,
            ModulePath modulePath,
            BuildIdentifier buildIdentifier,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.layerId = layerId;
        this.parentModuleId = parentModuleId;
        this.name = name;
        this.description = description != null ? description : ModuleDescription.empty();
        this.modulePath = modulePath;
        this.buildIdentifier = buildIdentifier != null ? buildIdentifier : BuildIdentifier.empty();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param layerId 레이어 ID
     * @param parentModuleId 부모 모듈 ID (nullable)
     * @param name 모듈 이름
     * @param description 모듈 설명
     * @param modulePath 모듈 파일시스템 경로
     * @param buildIdentifier 빌드 시스템 식별자
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 Module 인스턴스
     */
    public static Module forNew(
            LayerId layerId,
            ModuleId parentModuleId,
            ModuleName name,
            ModuleDescription description,
            ModulePath modulePath,
            BuildIdentifier buildIdentifier,
            Instant now) {
        return new Module(
                ModuleId.forNew(),
                layerId,
                parentModuleId,
                name,
                description,
                modulePath,
                buildIdentifier,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 모듈 ID
     * @param layerId 레이어 ID
     * @param parentModuleId 부모 모듈 ID (nullable)
     * @param name 모듈 이름
     * @param description 모듈 설명
     * @param modulePath 모듈 파일시스템 경로
     * @param buildIdentifier 빌드 시스템 식별자
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return Module 인스턴스
     */
    public static Module of(
            ModuleId id,
            LayerId layerId,
            ModuleId parentModuleId,
            ModuleName name,
            ModuleDescription description,
            ModulePath modulePath,
            BuildIdentifier buildIdentifier,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Module(
                id,
                layerId,
                parentModuleId,
                name,
                description,
                modulePath,
                buildIdentifier,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 모듈 ID
     * @param layerId 레이어 ID
     * @param parentModuleId 부모 모듈 ID (nullable)
     * @param name 모듈 이름
     * @param description 모듈 설명
     * @param modulePath 모듈 파일시스템 경로
     * @param buildIdentifier 빌드 시스템 식별자
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 Module 인스턴스
     */
    public static Module reconstitute(
            ModuleId id,
            LayerId layerId,
            ModuleId parentModuleId,
            ModuleName name,
            ModuleDescription description,
            ModulePath modulePath,
            BuildIdentifier buildIdentifier,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                layerId,
                parentModuleId,
                name,
                description,
                modulePath,
                buildIdentifier,
                deletionStatus,
                createdAt,
                updatedAt);
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
    public void assignId(ModuleId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 모듈 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(ModuleUpdateData data, Instant now) {
        this.parentModuleId = data.parentModuleId();
        this.name = data.name();
        this.description = data.description();
        this.modulePath = data.modulePath();
        this.buildIdentifier = data.buildIdentifier();
        this.updatedAt = now;
    }

    /** 부모 모듈 여부 확인 */
    public boolean hasParent() {
        return parentModuleId != null;
    }

    /** 루트 모듈 여부 확인 */
    public boolean isRoot() {
        return parentModuleId == null;
    }

    /**
     * 모듈 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 모듈 복원
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

    // Getters
    public ModuleId id() {
        return id;
    }

    public LayerId layerId() {
        return layerId;
    }

    public ModuleId parentModuleId() {
        return parentModuleId;
    }

    public ModuleName name() {
        return name;
    }

    public ModuleDescription description() {
        return description;
    }

    public ModulePath modulePath() {
        return modulePath;
    }

    public BuildIdentifier buildIdentifier() {
        return buildIdentifier;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    // Value Object 위임 메서드 (Law of Demeter 준수)
    // Persistence Layer Mapper에서 체이닝 방지: domain.id().value() 대신 domain.idValue() 사용

    /**
     * ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return ID 값 (nullable)
     */
    public Long idValue() {
        return id != null ? id.value() : null;
    }

    /**
     * Layer ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Layer ID 값
     */
    public Long layerIdValue() {
        return layerId.value();
    }

    /**
     * Parent Module ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Parent Module ID 값 (nullable)
     */
    public Long parentModuleIdValue() {
        return parentModuleId != null ? parentModuleId.value() : null;
    }

    /**
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 모듈 이름 문자열
     */
    public String nameValue() {
        return name.value();
    }

    /**
     * Description 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 모듈 설명 문자열 (nullable)
     */
    public String descriptionValue() {
        return description != null ? description.value() : null;
    }

    /**
     * Module Path 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 모듈 파일시스템 경로 문자열
     */
    public String modulePathValue() {
        return modulePath.value();
    }

    /**
     * Build Identifier 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 빌드 시스템 식별자 문자열 (nullable)
     */
    public String buildIdentifierValue() {
        return buildIdentifier != null ? buildIdentifier.value() : null;
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
