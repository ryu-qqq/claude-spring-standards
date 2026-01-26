package com.ryuqq.domain.checklistitem.fixture;

import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.checklistitem.vo.ChecklistSource;
import com.ryuqq.domain.checklistitem.vo.SequenceOrder;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * ChecklistItem Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 ChecklistItem 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class ChecklistItemFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private ChecklistItemFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 신규 ChecklistItem 생성 (ID 미할당)
     *
     * @return 신규 ChecklistItem
     */
    public static ChecklistItem forNew() {
        return ChecklistItem.forNew(
                ChecklistItemVoFixtures.fixedCodingRuleId(),
                SequenceOrder.of(1),
                ChecklistItemVoFixtures.defaultCheckDescription(),
                ChecklistItemVoFixtures.defaultCheckType(),
                ChecklistItemVoFixtures.defaultAutomationTool(),
                ChecklistItemVoFixtures.defaultAutomationRuleId(),
                false,
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 ChecklistItem 복원 (기본 설정)
     *
     * @return 복원된 ChecklistItem
     */
    public static ChecklistItem reconstitute() {
        return reconstitute(ChecklistItemVoFixtures.nextChecklistItemId());
    }

    /**
     * 지정된 ID로 ChecklistItem 복원
     *
     * @param id ChecklistItemId
     * @return 복원된 ChecklistItem
     */
    public static ChecklistItem reconstitute(ChecklistItemId id) {
        Instant now = FIXED_CLOCK.instant();
        return ChecklistItem.reconstitute(
                id,
                ChecklistItemVoFixtures.fixedCodingRuleId(),
                SequenceOrder.of(1),
                ChecklistItemVoFixtures.defaultCheckDescription(),
                ChecklistItemVoFixtures.defaultCheckType(),
                ChecklistItemVoFixtures.defaultAutomationTool(),
                ChecklistItemVoFixtures.defaultAutomationRuleId(),
                false,
                ChecklistSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기본 기존 ChecklistItem (저장된 상태)
     *
     * @return 기존 ChecklistItem
     */
    public static ChecklistItem defaultExistingChecklistItem() {
        Instant now = FIXED_CLOCK.instant();
        return ChecklistItem.of(
                ChecklistItemVoFixtures.nextChecklistItemId(),
                ChecklistItemVoFixtures.fixedCodingRuleId(),
                SequenceOrder.of(1),
                ChecklistItemVoFixtures.defaultCheckDescription(),
                ChecklistItemVoFixtures.defaultCheckType(),
                ChecklistItemVoFixtures.defaultAutomationTool(),
                ChecklistItemVoFixtures.defaultAutomationRuleId(),
                false,
                ChecklistSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 필수 항목 ChecklistItem
     *
     * @return 필수 ChecklistItem
     */
    public static ChecklistItem criticalChecklistItem() {
        Instant now = FIXED_CLOCK.instant();
        return ChecklistItem.reconstitute(
                ChecklistItemVoFixtures.nextChecklistItemId(),
                ChecklistItemVoFixtures.fixedCodingRuleId(),
                SequenceOrder.of(1),
                ChecklistItemVoFixtures.defaultCheckDescription(),
                ChecklistItemVoFixtures.defaultCheckType(),
                ChecklistItemVoFixtures.defaultAutomationTool(),
                ChecklistItemVoFixtures.defaultAutomationRuleId(),
                true,
                ChecklistSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 피드백에서 승격된 ChecklistItem
     *
     * @return 피드백에서 승격된 ChecklistItem
     */
    public static ChecklistItem fromFeedbackChecklistItem() {
        Instant now = FIXED_CLOCK.instant();
        return ChecklistItem.fromFeedback(
                ChecklistItemVoFixtures.fixedCodingRuleId(),
                SequenceOrder.of(1),
                ChecklistItemVoFixtures.defaultCheckDescription(),
                ChecklistItemVoFixtures.defaultCheckType(),
                ChecklistItemVoFixtures.defaultAutomationTool(),
                ChecklistItemVoFixtures.defaultAutomationRuleId(),
                false,
                999L,
                now);
    }

    /**
     * 삭제된 ChecklistItem
     *
     * @return 삭제된 ChecklistItem
     */
    public static ChecklistItem deletedChecklistItem() {
        Instant now = FIXED_CLOCK.instant();
        return ChecklistItem.reconstitute(
                ChecklistItemVoFixtures.nextChecklistItemId(),
                ChecklistItemVoFixtures.fixedCodingRuleId(),
                SequenceOrder.of(1),
                ChecklistItemVoFixtures.defaultCheckDescription(),
                ChecklistItemVoFixtures.defaultCheckType(),
                ChecklistItemVoFixtures.defaultAutomationTool(),
                ChecklistItemVoFixtures.defaultAutomationRuleId(),
                false,
                ChecklistSource.MANUAL,
                null,
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /**
     * 수동 검사 ChecklistItem
     *
     * @return 수동 검사 ChecklistItem
     */
    public static ChecklistItem manualChecklistItem() {
        Instant now = FIXED_CLOCK.instant();
        return ChecklistItem.reconstitute(
                ChecklistItemVoFixtures.nextChecklistItemId(),
                ChecklistItemVoFixtures.fixedCodingRuleId(),
                SequenceOrder.of(1),
                ChecklistItemVoFixtures.defaultCheckDescription(),
                CheckType.MANUAL,
                ChecklistItemVoFixtures.defaultAutomationTool(),
                ChecklistItemVoFixtures.emptyAutomationRuleId(),
                false,
                ChecklistSource.MANUAL,
                null,
                DeletionStatus.active(),
                now,
                now);
    }
}
