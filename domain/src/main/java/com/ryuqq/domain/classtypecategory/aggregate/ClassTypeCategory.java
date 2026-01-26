package com.ryuqq.domain.classtypecategory.aggregate;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.classtypecategory.vo.CategoryCode;
import com.ryuqq.domain.classtypecategory.vo.CategoryName;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Instant;

/**
 * ClassTypeCategory - 클래스 타입 카테고리 Aggregate Root
 *
 * <p>아키텍처별 클래스 타입의 카테고리를 관리합니다. (예: DOMAIN_TYPES, APPLICATION_TYPES)
 *
 * @author ryu-qqq
 */
public class ClassTypeCategory {

    private ClassTypeCategoryId id;
    private ArchitectureId architectureId;
    private CategoryCode code;
    private CategoryName name;
    private String description;
    private int orderIndex;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ClassTypeCategory() {
        this.createdAt = null;
    }

    private ClassTypeCategory(
            ClassTypeCategoryId id,
            ArchitectureId architectureId,
            CategoryCode code,
            CategoryName name,
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
     * @param code 카테고리 코드
     * @param name 카테고리 이름
     * @param description 카테고리 설명
     * @param orderIndex 정렬 순서
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ClassTypeCategory 인스턴스
     */
    public static ClassTypeCategory forNew(
            ArchitectureId architectureId,
            CategoryCode code,
            CategoryName name,
            String description,
            int orderIndex,
            Instant now) {
        return new ClassTypeCategory(
                ClassTypeCategoryId.forNew(),
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
     * @param id 카테고리 ID
     * @param architectureId 아키텍처 ID
     * @param code 카테고리 코드
     * @param name 카테고리 이름
     * @param description 카테고리 설명
     * @param orderIndex 정렬 순서
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return ClassTypeCategory 인스턴스
     */
    public static ClassTypeCategory of(
            ClassTypeCategoryId id,
            ArchitectureId architectureId,
            CategoryCode code,
            CategoryName name,
            String description,
            int orderIndex,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ClassTypeCategory(
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
     * @param id 카테고리 ID
     * @param architectureId 아키텍처 ID
     * @param code 카테고리 코드
     * @param name 카테고리 이름
     * @param description 카테고리 설명
     * @param orderIndex 정렬 순서
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 ClassTypeCategory 인스턴스
     */
    public static ClassTypeCategory reconstitute(
            ClassTypeCategoryId id,
            ArchitectureId architectureId,
            CategoryCode code,
            CategoryName name,
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
    public void assignId(ClassTypeCategoryId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 카테고리 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(ClassTypeCategoryUpdateData data, Instant now) {
        this.code = data.code();
        this.name = data.name();
        this.description = data.description();
        this.orderIndex = data.orderIndex();
        this.updatedAt = now;
    }

    /**
     * 카테고리 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 카테고리 복원
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
    public ClassTypeCategoryId id() {
        return id;
    }

    public ArchitectureId architectureId() {
        return architectureId;
    }

    public CategoryCode code() {
        return code;
    }

    public CategoryName name() {
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
     * @return 카테고리 코드 문자열
     */
    public String codeValue() {
        return code.value();
    }

    /**
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 카테고리 이름 문자열
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
