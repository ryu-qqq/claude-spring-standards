package com.ryuqq.domain.packagepurpose.aggregate;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.AllowedClassTypes;
import com.ryuqq.domain.packagepurpose.vo.NamingPattern;
import com.ryuqq.domain.packagepurpose.vo.NamingSuffix;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagepurpose.vo.PurposeName;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;

/**
 * PackagePurpose - 패키지 목적 Aggregate Root
 *
 * <p>패키지의 역할과 목적을 정의합니다.
 *
 * @author ryu-qqq
 */
public class PackagePurpose {

    private PackagePurposeId id;
    private PackageStructureId structureId;
    private PurposeCode code;
    private PurposeName name;
    private String description;
    private AllowedClassTypes defaultAllowedClassTypes;
    private NamingPattern defaultNamingPattern;
    private NamingSuffix defaultNamingSuffix;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected PackagePurpose() {
        this.createdAt = null;
    }

    private PackagePurpose(
            PackagePurposeId id,
            PackageStructureId structureId,
            PurposeCode code,
            PurposeName name,
            String description,
            AllowedClassTypes defaultAllowedClassTypes,
            NamingPattern defaultNamingPattern,
            NamingSuffix defaultNamingSuffix,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.structureId = structureId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.defaultAllowedClassTypes =
                defaultAllowedClassTypes != null
                        ? defaultAllowedClassTypes
                        : AllowedClassTypes.empty();
        this.defaultNamingPattern =
                defaultNamingPattern != null ? defaultNamingPattern : NamingPattern.empty();
        this.defaultNamingSuffix =
                defaultNamingSuffix != null ? defaultNamingSuffix : NamingSuffix.empty();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param name 목적 이름
     * @param description 설명
     * @param defaultAllowedClassTypes 허용 클래스 타입
     * @param defaultNamingPattern 네이밍 패턴
     * @param defaultNamingSuffix 네이밍 접미사
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 PackagePurpose 인스턴스
     */
    public static PackagePurpose forNew(
            PackageStructureId structureId,
            PurposeCode code,
            PurposeName name,
            String description,
            AllowedClassTypes defaultAllowedClassTypes,
            NamingPattern defaultNamingPattern,
            NamingSuffix defaultNamingSuffix,
            Instant now) {
        return new PackagePurpose(
                PackagePurposeId.forNew(),
                structureId,
                code,
                name,
                description,
                defaultAllowedClassTypes,
                defaultNamingPattern,
                defaultNamingSuffix,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 패키지 목적 ID
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param name 목적 이름
     * @param description 설명
     * @param defaultAllowedClassTypes 허용 클래스 타입
     * @param defaultNamingPattern 네이밍 패턴
     * @param defaultNamingSuffix 네이밍 접미사
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return PackagePurpose 인스턴스
     */
    public static PackagePurpose of(
            PackagePurposeId id,
            PackageStructureId structureId,
            PurposeCode code,
            PurposeName name,
            String description,
            AllowedClassTypes defaultAllowedClassTypes,
            NamingPattern defaultNamingPattern,
            NamingSuffix defaultNamingSuffix,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new PackagePurpose(
                id,
                structureId,
                code,
                name,
                description,
                defaultAllowedClassTypes,
                defaultNamingPattern,
                defaultNamingSuffix,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 패키지 목적 ID
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param name 목적 이름
     * @param description 설명
     * @param defaultAllowedClassTypes 허용 클래스 타입
     * @param defaultNamingPattern 네이밍 패턴
     * @param defaultNamingSuffix 네이밍 접미사
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 PackagePurpose 인스턴스
     */
    public static PackagePurpose reconstitute(
            PackagePurposeId id,
            PackageStructureId structureId,
            PurposeCode code,
            PurposeName name,
            String description,
            AllowedClassTypes defaultAllowedClassTypes,
            NamingPattern defaultNamingPattern,
            NamingSuffix defaultNamingSuffix,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                structureId,
                code,
                name,
                description,
                defaultAllowedClassTypes,
                defaultNamingPattern,
                defaultNamingSuffix,
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
    public void assignId(PackagePurposeId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    // Getters
    public PackagePurposeId id() {
        return id;
    }

    public PackageStructureId structureId() {
        return structureId;
    }

    public PurposeCode code() {
        return code;
    }

    public PurposeName name() {
        return name;
    }

    public String description() {
        return description;
    }

    public AllowedClassTypes defaultAllowedClassTypes() {
        return defaultAllowedClassTypes;
    }

    public NamingPattern defaultNamingPattern() {
        return defaultNamingPattern;
    }

    public NamingSuffix defaultNamingSuffix() {
        return defaultNamingSuffix;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    /**
     * 패키지 목적 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 패키지 목적 복원
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
     * 패키지 목적 정보 수정
     *
     * <p>요청으로 들어온 데이터를 기반으로 필드를 업데이트합니다. JPA의 더티체킹을 활용하여 변경사항을 자동으로 감지합니다.
     *
     * @param updateData 수정할 데이터 (모든 필드 필수)
     * @param now 수정 시각
     */
    public void update(PackagePurposeUpdateData updateData, Instant now) {
        this.code = updateData.code();
        this.name = updateData.name();
        this.description = updateData.description();
        this.defaultAllowedClassTypes = updateData.defaultAllowedClassTypes();
        this.defaultNamingPattern = updateData.defaultNamingPattern();
        this.defaultNamingSuffix = updateData.defaultNamingSuffix();
        this.updatedAt = now;
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
     * Structure ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Structure ID 값
     */
    public Long structureIdValue() {
        return structureId.value();
    }

    /**
     * Code 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 목적 코드 문자열
     */
    public String codeValue() {
        return code.value();
    }

    /**
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 목적 이름 문자열
     */
    public String nameValue() {
        return name.value();
    }

    /**
     * Default Naming Pattern 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 기본 네이밍 패턴 문자열 (nullable)
     */
    public String defaultNamingPatternValue() {
        return defaultNamingPattern != null && !defaultNamingPattern.isEmpty()
                ? defaultNamingPattern.value()
                : null;
    }

    /**
     * Default Naming Suffix 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 기본 네이밍 접미사 문자열 (nullable)
     */
    public String defaultNamingSuffixValue() {
        return defaultNamingSuffix != null && !defaultNamingSuffix.isEmpty()
                ? defaultNamingSuffix.value()
                : null;
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
