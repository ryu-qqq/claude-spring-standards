package com.ryuqq.domain.checklistitem.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.checklistitem.fixture.ChecklistItemFixture;
import com.ryuqq.domain.checklistitem.fixture.ChecklistItemVoFixtures;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.vo.AutomationRuleId;
import com.ryuqq.domain.checklistitem.vo.AutomationTool;
import com.ryuqq.domain.checklistitem.vo.CheckDescription;
import com.ryuqq.domain.checklistitem.vo.CheckType;
import com.ryuqq.domain.checklistitem.vo.ChecklistSource;
import com.ryuqq.domain.checklistitem.vo.SequenceOrder;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ChecklistItem Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("ChecklistItem Aggregate")
class ChecklistItemTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateChecklistItem {

        @Test
        @DisplayName("신규 ChecklistItem 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            CodingRuleId ruleId = ChecklistItemVoFixtures.fixedCodingRuleId();
            SequenceOrder sequenceOrder = SequenceOrder.of(1);
            CheckDescription checkDescription = ChecklistItemVoFixtures.defaultCheckDescription();
            CheckType checkType = ChecklistItemVoFixtures.defaultCheckType();
            AutomationTool automationTool = ChecklistItemVoFixtures.defaultAutomationTool();
            AutomationRuleId automationRuleId = ChecklistItemVoFixtures.defaultAutomationRuleId();
            boolean critical = false;
            Instant now = FIXED_CLOCK.instant();

            // when
            ChecklistItem checklistItem =
                    ChecklistItem.forNew(
                            ruleId,
                            sequenceOrder,
                            checkDescription,
                            checkType,
                            automationTool,
                            automationRuleId,
                            critical,
                            now);

            // then
            assertThat(checklistItem.isNew()).isTrue();
            assertThat(checklistItem.ruleId()).isEqualTo(ruleId);
            assertThat(checklistItem.sequenceOrder()).isEqualTo(sequenceOrder);
            assertThat(checklistItem.checkDescription()).isEqualTo(checkDescription);
            assertThat(checklistItem.checkType()).isEqualTo(checkType);
            assertThat(checklistItem.automationTool()).isEqualTo(automationTool);
            assertThat(checklistItem.automationRuleId()).isEqualTo(automationRuleId);
            assertThat(checklistItem.isCritical()).isFalse();
            assertThat(checklistItem.source()).isEqualTo(ChecklistSource.MANUAL);
            assertThat(checklistItem.feedbackId()).isNull();
            assertThat(checklistItem.deletionStatus().isDeleted()).isFalse();
            assertThat(checklistItem.createdAt()).isEqualTo(now);
            assertThat(checklistItem.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 ChecklistItem은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            ChecklistItem checklistItem = ChecklistItemFixture.forNew();

            // then
            assertThat(checklistItem.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("피드백에서 승격된 ChecklistItem 생성 성공")
        void fromFeedback_WithValidData_ShouldSucceed() {
            // given
            CodingRuleId ruleId = ChecklistItemVoFixtures.fixedCodingRuleId();
            SequenceOrder sequenceOrder = SequenceOrder.of(1);
            CheckDescription checkDescription = ChecklistItemVoFixtures.defaultCheckDescription();
            CheckType checkType = ChecklistItemVoFixtures.defaultCheckType();
            AutomationTool automationTool = ChecklistItemVoFixtures.defaultAutomationTool();
            AutomationRuleId automationRuleId = ChecklistItemVoFixtures.defaultAutomationRuleId();
            boolean critical = false;
            Long feedbackId = 999L;
            Instant now = FIXED_CLOCK.instant();

            // when
            ChecklistItem checklistItem =
                    ChecklistItem.fromFeedback(
                            ruleId,
                            sequenceOrder,
                            checkDescription,
                            checkType,
                            automationTool,
                            automationRuleId,
                            critical,
                            feedbackId,
                            now);

            // then
            assertThat(checklistItem.isNew()).isTrue();
            assertThat(checklistItem.source()).isEqualTo(ChecklistSource.AGENT_FEEDBACK);
            assertThat(checklistItem.feedbackId()).isEqualTo(feedbackId);
            assertThat(checklistItem.isFromFeedback()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.forNew();
            ChecklistItemId id = ChecklistItemVoFixtures.nextChecklistItemId();

            // when
            checklistItem.assignId(id);

            // then
            assertThat(checklistItem.id()).isEqualTo(id);
            assertThat(checklistItem.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.defaultExistingChecklistItem();
            ChecklistItemId newId = ChecklistItemVoFixtures.nextChecklistItemId();

            // when & then
            assertThatThrownBy(() -> checklistItem.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("비즈니스 로직")
    class BusinessLogic {

        @Test
        @DisplayName("자동화 검사 항목인지 확인")
        void isAutomated_WithAutomatedType_ShouldReturnTrue() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.defaultExistingChecklistItem();

            // when & then
            assertThat(checklistItem.isAutomated()).isTrue();
        }

        @Test
        @DisplayName("수동 검사가 필요한지 확인")
        void requiresManualCheck_WithManualType_ShouldReturnTrue() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.manualChecklistItem();

            // when & then
            assertThat(checklistItem.requiresManualCheck()).isTrue();
        }

        @Test
        @DisplayName("필수 항목인지 확인")
        void isCritical_WithCriticalFlag_ShouldReturnTrue() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.criticalChecklistItem();

            // when & then
            assertThat(checklistItem.isCritical()).isTrue();
        }

        @Test
        @DisplayName("피드백에서 승격된 항목인지 확인")
        void isFromFeedback_WithFeedbackSource_ShouldReturnTrue() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.fromFeedbackChecklistItem();

            // when & then
            assertThat(checklistItem.isFromFeedback()).isTrue();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteChecklistItem {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.defaultExistingChecklistItem();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            checklistItem.delete(deleteTime);

            // then
            assertThat(checklistItem.isDeleted()).isTrue();
            assertThat(checklistItem.deletionStatus().isDeleted()).isTrue();
            assertThat(checklistItem.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(checklistItem.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 ChecklistItem 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.deletedChecklistItem();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            checklistItem.restore(restoreTime);

            // then
            assertThat(checklistItem.isDeleted()).isFalse();
            assertThat(checklistItem.deletionStatus().isActive()).isTrue();
            assertThat(checklistItem.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            ChecklistItem activeChecklistItem = ChecklistItemFixture.defaultExistingChecklistItem();
            ChecklistItem deletedChecklistItem = ChecklistItemFixture.deletedChecklistItem();

            // when & then
            assertThat(activeChecklistItem.isDeleted()).isFalse();
            assertThat(deletedChecklistItem.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteChecklistItem {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ChecklistItemId id = ChecklistItemVoFixtures.nextChecklistItemId();
            CodingRuleId ruleId = ChecklistItemVoFixtures.fixedCodingRuleId();
            SequenceOrder sequenceOrder = SequenceOrder.of(1);
            CheckDescription checkDescription = ChecklistItemVoFixtures.defaultCheckDescription();
            CheckType checkType = ChecklistItemVoFixtures.defaultCheckType();
            AutomationTool automationTool = ChecklistItemVoFixtures.defaultAutomationTool();
            AutomationRuleId automationRuleId = ChecklistItemVoFixtures.defaultAutomationRuleId();
            boolean critical = false;
            ChecklistSource source = ChecklistSource.MANUAL;
            Long feedbackId = null;
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            ChecklistItem checklistItem =
                    ChecklistItem.reconstitute(
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

            // then
            assertThat(checklistItem.id()).isEqualTo(id);
            assertThat(checklistItem.ruleId()).isEqualTo(ruleId);
            assertThat(checklistItem.sequenceOrder()).isEqualTo(sequenceOrder);
            assertThat(checklistItem.checkDescription()).isEqualTo(checkDescription);
            assertThat(checklistItem.checkType()).isEqualTo(checkType);
            assertThat(checklistItem.automationTool()).isEqualTo(automationTool);
            assertThat(checklistItem.automationRuleId()).isEqualTo(automationRuleId);
            assertThat(checklistItem.isCritical()).isEqualTo(critical);
            assertThat(checklistItem.source()).isEqualTo(source);
            assertThat(checklistItem.feedbackId()).isEqualTo(feedbackId);
            assertThat(checklistItem.createdAt()).isEqualTo(createdAt);
            assertThat(checklistItem.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
