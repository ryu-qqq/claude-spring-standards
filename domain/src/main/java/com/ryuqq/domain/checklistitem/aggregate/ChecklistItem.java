package com.ryuqq.domain.checklistitem.aggregate;

import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.vo.AutomationRuleId;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckDescription;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.checklistitem.vo.ChecklistSource;
import com.ryuqq.domain.checklistitem.vo.SequenceOrder;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Instant;

/**
 * ChecklistItem - 체크리스트 항목 Aggregate Root
 *
 * <p>코딩 규칙의 체크리스트 항목을 정의합니다.
 *
 * @author ryu-qqq
 */
public class ChecklistItem {

    private ChecklistItemId id;
    private CodingRuleId ruleId;
    private SequenceOrder sequenceOrder;
    private CheckDescription checkDescription;
    private CheckType checkType;
    private AutomationTool automationTool;
    private AutomationRuleId automationRuleId;
    private boolean critical;
    private ChecklistSource source;
    private Long feedbackId;
    private DeletionStatus deletionStatus;

    // Audit
    private final Instant createdAt;
    private Instant updatedAt;

    /** 프레임워크 호환용 기본 생성자 */
    protected ChecklistItem() {
        this.createdAt = null;
    }

    private ChecklistItem(
            ChecklistItemId id,
            CodingRuleId ruleId,
            SequenceOrder sequenceOrder,
            CheckDescription checkDescription,
            CheckType checkType,
            AutomationTool automationTool,
            AutomationRuleId automationRuleId,
            boolean critical,
            ChecklistSource source,
            Long feedbackId,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.ruleId = ruleId;
        this.sequenceOrder = sequenceOrder;
        this.checkDescription = checkDescription;
        this.checkType = checkType;
        this.automationTool = automationTool;
        this.automationRuleId =
                automationRuleId != null ? automationRuleId : AutomationRuleId.empty();
        this.critical = critical;
        this.source = source != null ? source : ChecklistSource.MANUAL;
        this.feedbackId = feedbackId;
        this.deletionStatus = deletionStatus != null ? deletionStatus : DeletionStatus.active();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 생성용 팩토리 메서드
     *
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @param checkDescription 체크 설명
     * @param checkType 체크 타입
     * @param automationTool 자동화 도구
     * @param automationRuleId 자동화 규칙 ID
     * @param critical 필수 여부
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 새로운 ChecklistItem 인스턴스
     */
    public static ChecklistItem forNew(
            CodingRuleId ruleId,
            SequenceOrder sequenceOrder,
            CheckDescription checkDescription,
            CheckType checkType,
            AutomationTool automationTool,
            AutomationRuleId automationRuleId,
            boolean critical,
            Instant now) {
        return new ChecklistItem(
                ChecklistItemId.forNew(),
                ruleId,
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                critical,
                ChecklistSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 에이전트 피드백에서 승격된 체크리스트 생성
     *
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @param checkDescription 체크 설명
     * @param checkType 체크 타입
     * @param automationTool 자동화 도구
     * @param automationRuleId 자동화 규칙 ID
     * @param critical 필수 여부
     * @param feedbackId 피드백 ID
     * @param now 현재 시각 (Instant.now() 외부 주입)
     * @return 피드백에서 승격된 ChecklistItem 인스턴스
     */
    public static ChecklistItem fromFeedback(
            CodingRuleId ruleId,
            SequenceOrder sequenceOrder,
            CheckDescription checkDescription,
            CheckType checkType,
            AutomationTool automationTool,
            AutomationRuleId automationRuleId,
            boolean critical,
            Long feedbackId,
            Instant now) {
        return new ChecklistItem(
                ChecklistItemId.forNew(),
                ruleId,
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                critical,
                ChecklistSource.AGENT_FEEDBACK,
                feedbackId,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기존 데이터 래핑용 팩토리 메서드
     *
     * @param id 체크리스트 항목 ID
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @param checkDescription 체크 설명
     * @param checkType 체크 타입
     * @param automationTool 자동화 도구
     * @param automationRuleId 자동화 규칙 ID
     * @param critical 필수 여부
     * @param source 체크리스트 소스
     * @param feedbackId 피드백 ID
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return ChecklistItem 인스턴스
     */
    public static ChecklistItem of(
            ChecklistItemId id,
            CodingRuleId ruleId,
            SequenceOrder sequenceOrder,
            CheckDescription checkDescription,
            CheckType checkType,
            AutomationTool automationTool,
            AutomationRuleId automationRuleId,
            boolean critical,
            ChecklistSource source,
            Long feedbackId,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return new ChecklistItem(
                id,
                ruleId,
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                critical,
                source,
                feedbackId,
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 영속성에서 복원용 팩토리 메서드
     *
     * @param id 체크리스트 항목 ID
     * @param ruleId 코딩 규칙 ID
     * @param sequenceOrder 순서
     * @param checkDescription 체크 설명
     * @param checkType 체크 타입
     * @param automationTool 자동화 도구
     * @param automationRuleId 자동화 규칙 ID
     * @param critical 필수 여부
     * @param source 체크리스트 소스
     * @param feedbackId 피드백 ID
     * @param deletionStatus 삭제 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @return 복원된 ChecklistItem 인스턴스
     */
    public static ChecklistItem reconstitute(
            ChecklistItemId id,
            CodingRuleId ruleId,
            SequenceOrder sequenceOrder,
            CheckDescription checkDescription,
            CheckType checkType,
            AutomationTool automationTool,
            AutomationRuleId automationRuleId,
            boolean critical,
            ChecklistSource source,
            Long feedbackId,
            DeletionStatus deletionStatus,
            Instant createdAt,
            Instant updatedAt) {
        return of(
                id,
                ruleId,
                sequenceOrder,
                checkDescription,
                checkType,
                automationTool,
                automationRuleId,
                critical,
                source,
                feedbackId,
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
    public void assignId(ChecklistItemId id) {
        if (!this.id.isNew()) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = id;
    }

    /** 자동화 검사 항목인지 확인 */
    public boolean isAutomated() {
        return checkType.isAutomated();
    }

    /** 수동 검사가 필요한지 확인 */
    public boolean requiresManualCheck() {
        return checkType.requiresManualCheck();
    }

    /** 필수 항목인지 확인 */
    public boolean isCritical() {
        return critical;
    }

    /** 피드백에서 승격된 항목인지 확인 */
    public boolean isFromFeedback() {
        return source == ChecklistSource.AGENT_FEEDBACK;
    }

    /**
     * 체크리스트 항목 삭제 (Soft Delete)
     *
     * @param now 삭제 발생 시각
     */
    public void delete(Instant now) {
        this.deletionStatus = DeletionStatus.deletedAt(now);
        this.updatedAt = now;
    }

    /**
     * 삭제된 체크리스트 항목 복원
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
     * 체크리스트 항목 업데이트
     *
     * <p>ChecklistItemUpdateData의 Optional 필드 중 present인 항목만 업데이트합니다.
     *
     * @param updateData 업데이트 데이터
     * @param now 업데이트 시각
     */
    public void update(ChecklistItemUpdateData updateData, Instant now) {
        if (updateData == null || !updateData.hasUpdates()) {
            return;
        }

        updateData.sequenceOrder().ifPresent(value -> this.sequenceOrder = value);
        updateData.checkDescription().ifPresent(value -> this.checkDescription = value);
        updateData.checkType().ifPresent(value -> this.checkType = value);
        updateData.automationTool().ifPresent(value -> this.automationTool = value);
        updateData.automationRuleId().ifPresent(value -> this.automationRuleId = value);
        updateData.critical().ifPresent(value -> this.critical = value);

        this.updatedAt = now;
    }

    // Getters
    public ChecklistItemId id() {
        return id;
    }

    public CodingRuleId ruleId() {
        return ruleId;
    }

    public SequenceOrder sequenceOrder() {
        return sequenceOrder;
    }

    public CheckDescription checkDescription() {
        return checkDescription;
    }

    public CheckType checkType() {
        return checkType;
    }

    public AutomationTool automationTool() {
        return automationTool;
    }

    public AutomationRuleId automationRuleId() {
        return automationRuleId;
    }

    public ChecklistSource source() {
        return source;
    }

    public Long feedbackId() {
        return feedbackId;
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
     * Rule ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Rule ID 값
     */
    public Long ruleIdValue() {
        return ruleId.value();
    }

    /**
     * Sequence Order 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 순서 값
     */
    public Integer sequenceOrderValue() {
        return sequenceOrder.value();
    }

    /**
     * Check Description 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return 체크 설명 문자열
     */
    public String checkDescriptionValue() {
        return checkDescription.value();
    }

    /**
     * Check Type 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Check Type enum 이름
     */
    public String checkTypeName() {
        return checkType.name();
    }

    /**
     * Automation Tool 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Automation Tool enum 이름 (nullable)
     */
    public String automationToolName() {
        return automationTool != null ? automationTool.name() : null;
    }

    /**
     * Automation Rule ID 원시값 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Automation Rule ID 문자열 (nullable)
     */
    public String automationRuleIdValue() {
        return automationRuleId != null && !automationRuleId.isEmpty()
                ? automationRuleId.value()
                : null;
    }

    /**
     * Source 이름 반환
     *
     * <p>AGG-014: Law of Demeter 준수를 위한 위임 메서드
     *
     * @return Source enum 이름 (nullable)
     */
    public String sourceName() {
        return source != null ? source.name() : null;
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
