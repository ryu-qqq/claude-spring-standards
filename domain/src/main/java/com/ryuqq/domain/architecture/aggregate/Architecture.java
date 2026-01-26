package com.ryuqq.domain.architecture.aggregate;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.architecture.vo.PatternDescription;
import com.ryuqq.domain.architecture.vo.PatternPrinciples;
import com.ryuqq.domain.architecture.vo.PatternType;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.time.Instant;

/**
 * Architecture - 아키텍처 Aggregate Root
 *
 * <p>아키텍처 패턴 정의를 관리합니다.
 *
 * @author ryu-qqq
 */
public class Architecture {

    private ArchitectureId id;
    private TechStackId techStackId;
    private ArchitectureName name;
    private PatternType patternType;
    private PatternDescription patternDescription;
    private PatternPrinciples patternPrinciples;
    private ReferenceLinks referenceLinks;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected Architecture() {
        this.createdAt = null;
    }

    private Architecture(
            ArchitectureId id,
            TechStackId techStackId,
            ArchitectureName name,
            PatternType patternType,
            PatternDescription patternDescription,
            PatternPrinciples patternPrinciples,
            ReferenceLinks referenceLinks,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.techStackId = techStackId;
        this.name = name;
        this.patternType = patternType;
        this.patternDescription =
                patternDescription != null ? patternDescription : PatternDescription.empty();
        this.patternPrinciples =
                patternPrinciples != null ? patternPrinciples : PatternPrinciples.empty();
        this.referenceLinks = referenceLinks != null ? referenceLinks : ReferenceLinks.empty();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param techStackId 기술 스택 ID
     * @param name 아키텍처 이름
     * @param patternType 패턴 타입
     * @param patternDescription 패턴 설명
     * @param patternPrinciples 패턴 원칙
     * @param referenceLinks 참조 링크 목록
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 Architecture 인스턴스
     */
    public static Architecture forNew(
            TechStackId techStackId,
            ArchitectureName name,
            PatternType patternType,
            PatternDescription patternDescription,
            PatternPrinciples patternPrinciples,
            ReferenceLinks referenceLinks,
            Instant now) {
        return new Architecture(
                ArchitectureId.forNew(),
                techStackId,
                name,
                patternType,
                patternDescription,
                patternPrinciples,
                referenceLinks,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 아키텍처 ID
     * @param techStackId 기술 스택 ID
     * @param name 아키텍처 이름
     * @param patternType 패턴 타입
     * @param patternDescription 패턴 설명
     * @param patternPrinciples 패턴 원칙
     * @param referenceLinks 참조 링크 목록
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return Architecture 인스턴스
     */
    public static Architecture of(
            ArchitectureId id,
            TechStackId techStackId,
            ArchitectureName name,
            PatternType patternType,
            PatternDescription patternDescription,
            PatternPrinciples patternPrinciples,
            ReferenceLinks referenceLinks,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new Architecture(
                id,
                techStackId,
                name,
                patternType,
                patternDescription,
                patternPrinciples,
                referenceLinks,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 아키텍처 ID
     * @param techStackId 기술 스택 ID
     * @param name 아키텍처 이름
     * @param patternType 패턴 타입
     * @param patternDescription 패턴 설명
     * @param patternPrinciples 패턴 원칙
     * @param referenceLinks 참조 링크 목록
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 Architecture 인스턴스
     */
    public static Architecture reconstitute(
            ArchitectureId id,
            TechStackId techStackId,
            ArchitectureName name,
            PatternType patternType,
            PatternDescription patternDescription,
            PatternPrinciples patternPrinciples,
            ReferenceLinks referenceLinks,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                techStackId,
                name,
                patternType,
                patternDescription,
                patternPrinciples,
                referenceLinks,
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
    public void assignId(ArchitectureId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /**
     * 아키텍처 정보 수정
     *
     * @param data 수정 데이터
     * @param now 현재 시각 (Instant.now() 외부 주입)
     */
    public void update(ArchitectureUpdateData data, Instant now) {
        this.name = data.name();
        this.patternType = data.patternType();
        this.patternDescription = data.patternDescription();
        this.patternPrinciples = data.patternPrinciples();
        this.updatedAt = now;
    }

    /**
     * 아키텍처 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 아키텍처 복원
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
    public ArchitectureId id() {
        return id;
    }

    public TechStackId techStackId() {
        return techStackId;
    }

    public ArchitectureName name() {
        return name;
    }

    public PatternType patternType() {
        return patternType;
    }

    public PatternDescription patternDescription() {
        return patternDescription;
    }

    public PatternPrinciples patternPrinciples() {
        return patternPrinciples;
    }

    public ReferenceLinks referenceLinks() {
        return referenceLinks;
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
     * Tech Stack ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Tech Stack ID 값
     */
    public Long techStackIdValue() {
        return techStackId.value();
    }

    /**
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 아키텍처 이름 문자열
     */
    public String nameValue() {
        return name.value();
    }

    /**
     * Pattern Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Pattern Type enum 이름
     */
    public String patternTypeName() {
        return patternType.name();
    }

    /**
     * Pattern Description 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 패턴 설명 문자열
     */
    public String patternDescriptionValue() {
        return patternDescription.value();
    }

    /**
     * Reference Links 원시값 목록 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 참조 링크 문자열 목록
     */
    public java.util.List<String> referenceLinkValues() {
        return referenceLinks.values();
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
