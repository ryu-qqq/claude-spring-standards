package com.ryuqq.domain.classtype.aggregate;

import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.classtype.vo.ClassTypeCode;
import com.ryuqq.domain.classtype.vo.ClassTypeName;
import com.ryuqq.domain.classtypecategory.id.ClassTypeCategoryId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Instant;

/**
 * ClassType - 클래스 타입 Aggregate Root
 *
 * <p>카테고리별 클래스 타입을 관리합니다. (예: AGGREGATE, VALUE_OBJECT, USE_CASE) 기존 ClassType Enum을 대체하는 동적 클래스
 * 타입입니다.
 *
 * @author ryu-qqq
 */
public class ClassType {

    private ClassTypeId id;
    private ClassTypeCategoryId categoryId;
    private ClassTypeCode code;
    private ClassTypeName name;
    private String description;
    private int orderIndex;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ClassType() {
        this.createdAt = null;
    }

    private ClassType(
            ClassTypeId id,
            ClassTypeCategoryId categoryId,
            ClassTypeCode code,
            ClassTypeName name,
            String description,
            int orderIndex,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.categoryId = categoryId;
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
     * @param categoryId 카테고리 ID
     * @param code 클래스 타입 코드
     * @param name 클래스 타입 이름
     * @param description 클래스 타입 설명
     * @param orderIndex 정렬 순서
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ClassType 인스턴스
     */
    public static ClassType forNew(
            ClassTypeCategoryId categoryId,
            ClassTypeCode code,
            ClassTypeName name,
            String description,
            int orderIndex,
            Instant now) {
        return new ClassType(
                ClassTypeId.forNew(),
                categoryId,
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
     * @param id 클래스 타입 ID
     * @param categoryId 카테고리 ID
     * @param code 클래스 타입 코드
     * @param name 클래스 타입 이름
     * @param description 클래스 타입 설명
     * @param orderIndex 정렬 순서
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return ClassType 인스턴스
     */
    public static ClassType of(
            ClassTypeId id,
            ClassTypeCategoryId categoryId,
            ClassTypeCode code,
            ClassTypeName name,
            String description,
            int orderIndex,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ClassType(
                id,
                categoryId,
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
     * @param id 클래스 타입 ID
     * @param categoryId 카테고리 ID
     * @param code 클래스 타입 코드
     * @param name 클래스 타입 이름
     * @param description 클래스 타입 설명
     * @param orderIndex 정렬 순서
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 ClassType 인스턴스
     */
    public static ClassType reconstitute(
            ClassTypeId id,
            ClassTypeCategoryId categoryId,
            ClassTypeCode code,
            ClassTypeName name,
            String description,
            int orderIndex,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                categoryId,
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
    public void assignId(ClassTypeId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 클래스 타입 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(ClassTypeUpdateData data, Instant now) {
        this.code = data.code();
        this.name = data.name();
        this.description = data.description();
        this.orderIndex = data.orderIndex();
        this.updatedAt = now;
    }

    /**
     * 클래스 타입 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 클래스 타입 복원
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
    public ClassTypeId id() {
        return id;
    }

    public ClassTypeCategoryId categoryId() {
        return categoryId;
    }

    public ClassTypeCode code() {
        return code;
    }

    public ClassTypeName name() {
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
     * Category ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Category ID 값
     */
    public Long categoryIdValue() {
        return categoryId.value();
    }

    /**
     * Code 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 클래스 타입 코드 문자열
     */
    public String codeValue() {
        return code.value();
    }

    /**
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 클래스 타입 이름 문자열
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
