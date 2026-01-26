package com.ryuqq.domain.codingrule.aggregate;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.AppliesTo;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.codingrule.vo.RuleName;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.codingrule.vo.SdkConstraint;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.id.ConventionId;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.time.Instant;

/**
 * CodingRule - 코딩 규칙 Aggregate Root
 *
 * <p>코딩 컨벤션의 개별 규칙을 정의합니다.
 *
 * <p>Zero-Tolerance 여부는 ZeroToleranceRule 엔티티의 존재 여부로 판단합니다.
 *
 * @author ryu-qqq
 */
public class CodingRule {

    private CodingRuleId id;
    private ConventionId conventionId;
    private PackageStructureId structureId;
    private RuleCode code;
    private RuleName name;
    private RuleSeverity severity;
    private RuleCategory category;
    private String description;
    private String rationale;
    private boolean autoFixable;
    private AppliesTo appliesTo;
    private SdkConstraint sdkConstraint;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected CodingRule() {
        this.createdAt = null;
    }

    private CodingRule(
            CodingRuleId id,
            ConventionId conventionId,
            PackageStructureId structureId,
            RuleCode code,
            RuleName name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            AppliesTo appliesTo,
            SdkConstraint sdkConstraint,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.conventionId = conventionId;
        this.structureId = structureId;
        this.code = code;
        this.name = name;
        this.severity = severity;
        this.category = category;
        this.description = description;
        this.rationale = rationale;
        this.autoFixable = autoFixable;
        this.appliesTo = appliesTo != null ? appliesTo : AppliesTo.empty();
        this.sdkConstraint = sdkConstraint != null ? sdkConstraint : SdkConstraint.empty();
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param conventionId 컨벤션 ID
     * @param structureId 패키지 구조 ID
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @param description 설명
     * @param rationale 근거
     * @param autoFixable 자동 수정 가능 여부
     * @param appliesTo 적용 대상
     * @param sdkConstraint SDK 제약
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 CodingRule 인스턴스
     */
    public static CodingRule forNew(
            ConventionId conventionId,
            PackageStructureId structureId,
            RuleCode code,
            RuleName name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            AppliesTo appliesTo,
            SdkConstraint sdkConstraint,
            Instant now) {
        return new CodingRule(
                CodingRuleId.forNew(),
                conventionId,
                structureId,
                code,
                name,
                severity,
                category,
                description,
                rationale,
                autoFixable,
                appliesTo,
                sdkConstraint,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 코딩 규칙 ID
     * @param conventionId 컨벤션 ID
     * @param structureId 패키지 구조 ID
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @param description 설명
     * @param rationale 근거
     * @param autoFixable 자동 수정 가능 여부
     * @param appliesTo 적용 대상
     * @param sdkConstraint SDK 제약
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return CodingRule 인스턴스
     */
    public static CodingRule of(
            CodingRuleId id,
            ConventionId conventionId,
            PackageStructureId structureId,
            RuleCode code,
            RuleName name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            AppliesTo appliesTo,
            SdkConstraint sdkConstraint,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new CodingRule(
                id,
                conventionId,
                structureId,
                code,
                name,
                severity,
                category,
                description,
                rationale,
                autoFixable,
                appliesTo,
                sdkConstraint,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 코딩 규칙 ID
     * @param conventionId 컨벤션 ID
     * @param structureId 패키지 구조 ID
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @param description 설명
     * @param rationale 근거
     * @param autoFixable 자동 수정 가능 여부
     * @param appliesTo 적용 대상
     * @param sdkConstraint SDK 제약
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 CodingRule 인스턴스
     */
    public static CodingRule reconstitute(
            CodingRuleId id,
            ConventionId conventionId,
            PackageStructureId structureId,
            RuleCode code,
            RuleName name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            AppliesTo appliesTo,
            SdkConstraint sdkConstraint,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                conventionId,
                structureId,
                code,
                name,
                severity,
                category,
                description,
                rationale,
                autoFixable,
                appliesTo,
                sdkConstraint,
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
    public void assignId(CodingRuleId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /** 자동 수정 가능한 규칙인지 확인 */
    public boolean isAutoFixable() {
        return autoFixable;
    }

    /** 차단 수준의 심각도인지 확인 */
    public boolean isBlockerSeverity() {
        return severity.isBlocker();
    }

    /** SDK 제약이 있는지 확인 */
    public boolean hasSdkConstraint() {
        return !sdkConstraint.isEmpty();
    }

    /**
     * 코딩 규칙 수정
     *
     * @param updateData 수정 데이터
     * @param now 수정 시각
     */
    public void update(CodingRuleUpdateData updateData, Instant now) {
        this.structureId = updateData.structureId();
        this.code = updateData.code();
        this.name = updateData.name();
        this.severity = updateData.severity();
        this.category = updateData.category();
        this.description = updateData.description();
        this.rationale = updateData.rationale();
        this.autoFixable = updateData.autoFixable();
        this.appliesTo = updateData.appliesTo();
        this.sdkConstraint = updateData.sdkConstraint();
        this.updatedAt = now;
    }

    /**
     * 코딩 규칙 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 코딩 규칙 복원
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
    public CodingRuleId id() {
        return id;
    }

    public ConventionId conventionId() {
        return conventionId;
    }

    public PackageStructureId structureId() {
        return structureId;
    }

    public RuleCode code() {
        return code;
    }

    public RuleName name() {
        return name;
    }

    public RuleSeverity severity() {
        return severity;
    }

    public RuleCategory category() {
        return category;
    }

    public String description() {
        return description;
    }

    public String rationale() {
        return rationale;
    }

    public AppliesTo appliesTo() {
        return appliesTo;
    }

    public SdkConstraint sdkConstraint() {
        return sdkConstraint;
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
     * Convention ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Convention ID 값
     */
    public Long conventionIdValue() {
        return conventionId.value();
    }

    /**
     * Code 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 규칙 코드 문자열
     */
    public String codeValue() {
        return code.value();
    }

    /**
     * Name 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 규칙 이름 문자열
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
