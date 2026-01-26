package com.ryuqq.domain.layerdependency.aggregate;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.ConditionDescription;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.time.Instant;

/**
 * LayerDependencyRule - 레이어 의존성 규칙 Aggregate Root
 *
 * <p>레이어 간 의존성 허용/금지 규칙을 정의합니다.
 *
 * @author ryu-qqq
 */
public class LayerDependencyRule {

    private LayerDependencyRuleId id;
    private ArchitectureId architectureId;
    private LayerType fromLayer;
    private LayerType toLayer;
    private DependencyType dependencyType;
    private ConditionDescription conditionDescription;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected LayerDependencyRule() {
        this.createdAt = null;
    }

    private LayerDependencyRule(
            LayerDependencyRuleId id,
            ArchitectureId architectureId,
            LayerType fromLayer,
            LayerType toLayer,
            DependencyType dependencyType,
            ConditionDescription conditionDescription,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.architectureId = architectureId;
        this.fromLayer = fromLayer;
        this.toLayer = toLayer;
        this.dependencyType = dependencyType;
        this.conditionDescription =
                conditionDescription != null ? conditionDescription : ConditionDescription.empty();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param architectureId 아키텍처 ID
     * @param fromLayer 소스 레이어
     * @param toLayer 타겟 레이어
     * @param dependencyType 의존성 타입
     * @param conditionDescription 조건 설명
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 LayerDependencyRule 인스턴스
     */
    public static LayerDependencyRule forNew(
            ArchitectureId architectureId,
            LayerType fromLayer,
            LayerType toLayer,
            DependencyType dependencyType,
            ConditionDescription conditionDescription,
            Instant now) {
        return new LayerDependencyRule(
                LayerDependencyRuleId.forNew(),
                architectureId,
                fromLayer,
                toLayer,
                dependencyType,
                conditionDescription,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 레이어 의존성 규칙 ID
     * @param architectureId 아키텍처 ID
     * @param fromLayer 소스 레이어
     * @param toLayer 타겟 레이어
     * @param dependencyType 의존성 타입
     * @param conditionDescription 조건 설명
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return LayerDependencyRule 인스턴스
     */
    public static LayerDependencyRule of(
            LayerDependencyRuleId id,
            ArchitectureId architectureId,
            LayerType fromLayer,
            LayerType toLayer,
            DependencyType dependencyType,
            ConditionDescription conditionDescription,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new LayerDependencyRule(
                id,
                architectureId,
                fromLayer,
                toLayer,
                dependencyType,
                conditionDescription,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 레이어 의존성 규칙 ID
     * @param architectureId 아키텍처 ID
     * @param fromLayer 소스 레이어
     * @param toLayer 타겟 레이어
     * @param dependencyType 의존성 타입
     * @param conditionDescription 조건 설명
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 LayerDependencyRule 인스턴스
     */
    public static LayerDependencyRule reconstitute(
            LayerDependencyRuleId id,
            ArchitectureId architectureId,
            LayerType fromLayer,
            LayerType toLayer,
            DependencyType dependencyType,
            ConditionDescription conditionDescription,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                architectureId,
                fromLayer,
                toLayer,
                dependencyType,
                conditionDescription,
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
    public void assignId(LayerDependencyRuleId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /** 허용된 의존성인지 확인 */
    public boolean isAllowed() {
        return dependencyType == DependencyType.ALLOWED;
    }

    /** 금지된 의존성인지 확인 */
    public boolean isForbidden() {
        return dependencyType == DependencyType.FORBIDDEN;
    }

    /** 조건부 의존성인지 확인 */
    public boolean isConditional() {
        return dependencyType == DependencyType.CONDITIONAL;
    }

    /**
     * 레이어 의존성 규칙 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 레이어 의존성 규칙 복원
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
     * 레이어 의존성 규칙 수정
     *
     * @param updateData 수정 데이터
     * @param now 수정 시각
     */
    public void update(LayerDependencyRuleUpdateData updateData, Instant now) {
        this.fromLayer = updateData.fromLayer();
        this.toLayer = updateData.toLayer();
        this.dependencyType = updateData.dependencyType();
        this.conditionDescription = updateData.conditionDescription();
        this.updatedAt = now;
    }

    // Getters
    public LayerDependencyRuleId id() {
        return id;
    }

    public ArchitectureId architectureId() {
        return architectureId;
    }

    public LayerType fromLayer() {
        return fromLayer;
    }

    public LayerType toLayer() {
        return toLayer;
    }

    public DependencyType dependencyType() {
        return dependencyType;
    }

    public ConditionDescription conditionDescription() {
        return conditionDescription;
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
     * @return ID 값
     */
    public Long idValue() {
        return id.value();
    }

    /**
     * Architecture ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Architecture ID 값
     */
    public Long architectureIdValue() {
        return architectureId.value();
    }

    /**
     * From Layer 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return From Layer enum 이름
     */
    public String fromLayerName() {
        return fromLayer.name();
    }

    /**
     * To Layer 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return To Layer enum 이름
     */
    public String toLayerName() {
        return toLayer.name();
    }

    /**
     * Dependency Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Dependency Type enum 이름
     */
    public String dependencyTypeName() {
        return dependencyType.name();
    }

    /**
     * Condition Description 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 조건 설명 문자열 (nullable)
     */
    public String conditionDescriptionValue() {
        return conditionDescription != null ? conditionDescription.value() : null;
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
