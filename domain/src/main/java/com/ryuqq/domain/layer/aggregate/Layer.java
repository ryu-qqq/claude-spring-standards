package com.ryuqq.domain.layer.aggregate;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.layer.vo.LayerCode;
import com.ryuqq.domain.layer.vo.LayerName;
import java.time.Instant;

/**
 * Layer - 레이어 Aggregate Root
 *
 * <p>아키텍처 내의 레이어 정의를 관리합니다. 기존 ConventionLayer, ModuleType enum을 대체하는 동적 레이어입니다.
 *
 * @author ryu-qqq
 */
public class Layer {

    private LayerId id;
    private ArchitectureId architectureId;
    private LayerCode code;
    private LayerName name;
    private String description;
    private int orderIndex;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected Layer() {
        this.createdAt = null;
    }

    private Layer(
            LayerId id,
            ArchitectureId architectureId,
            LayerCode code,
            LayerName name,
            String description,
            int orderIndex,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.architectureId = architectureId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.orderIndex = orderIndex;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param architectureId 아키텍처 ID
     * @param code 레이어 코드
     * @param name 레이어 이름
     * @param description 레이어 설명
     * @param orderIndex 정렬 순서
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 Layer 인스턴스
     */
    public static Layer forNew(
            ArchitectureId architectureId,
            LayerCode code,
            LayerName name,
            String description,
            int orderIndex,
            Instant now) {
        return new Layer(
                LayerId.forNew(),
                architectureId,
                code,
                name,
                description,
                orderIndex,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 레이어 ID
     * @param architectureId 아키텍처 ID
     * @param code 레이어 코드
     * @param name 레이어 이름
     * @param description 레이어 설명
     * @param orderIndex 정렬 순서
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return Layer 인스턴스
     */
    public static Layer of(
            LayerId id,
            ArchitectureId architectureId,
            LayerCode code,
            LayerName name,
            String description,
            int orderIndex,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Layer(
                id,
                architectureId,
                code,
                name,
                description,
                orderIndex,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 레이어 ID
     * @param architectureId 아키텍처 ID
     * @param code 레이어 코드
     * @param name 레이어 이름
     * @param description 레이어 설명
     * @param orderIndex 정렬 순서
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 Layer 인스턴스
     */
    public static Layer reconstitute(
            LayerId id,
            ArchitectureId architectureId,
            LayerCode code,
            LayerName name,
            String description,
            int orderIndex,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                architectureId,
                code,
                name,
                description,
                orderIndex,
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
    public void assignId(LayerId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 레이어 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(LayerUpdateData data, Instant now) {
        this.code = data.code();
        this.name = data.name();
        this.description = data.description();
        this.orderIndex = data.orderIndex();
        this.updatedAt = now;
    }

    /**
     * 레이어 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 레이어 복원
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
    public LayerId id() {
        return id;
    }

    public ArchitectureId architectureId() {
        return architectureId;
    }

    public LayerCode code() {
        return code;
    }

    public LayerName name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int orderIndex() {
        return orderIndex;
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
     * @return ID 값 (신규인 경우 null)
     */
    public Long idValue() {
        return id != null ? id.value() : null;
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
     * Code 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 레이어 코드 문자열
     */
    public String codeValue() {
        return code.value();
    }

    /**
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 레이어 이름 문자열
     */
    public String nameValue() {
        return name.value();
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
