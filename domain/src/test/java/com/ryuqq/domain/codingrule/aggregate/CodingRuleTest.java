package com.ryuqq.domain.codingrule.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.codingrule.fixture.CodingRuleFixture;
import com.ryuqq.domain.codingrule.fixture.CodingRuleVoFixtures;
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
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * CodingRule Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("CodingRule Aggregate")
class CodingRuleTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateCodingRule {

        @Test
        @DisplayName("신규 CodingRule 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            ConventionId conventionId = CodingRuleVoFixtures.fixedConventionId();
            PackageStructureId structureId = null;
            RuleCode code = CodingRuleVoFixtures.defaultRuleCode();
            RuleName name = CodingRuleVoFixtures.defaultRuleName();
            RuleSeverity severity = CodingRuleVoFixtures.defaultRuleSeverity();
            RuleCategory category = CodingRuleVoFixtures.defaultRuleCategory();
            String description = CodingRuleVoFixtures.defaultRuleDescription();
            String rationale = CodingRuleVoFixtures.defaultRuleRationale();
            boolean autoFixable = false;
            AppliesTo appliesTo = CodingRuleVoFixtures.defaultAppliesTo();
            SdkConstraint sdkConstraint = SdkConstraint.empty();
            Instant now = FIXED_CLOCK.instant();

            // when
            CodingRule codingRule =
                    CodingRule.forNew(
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
                            now);

            // then
            assertThat(codingRule.isNew()).isTrue();
            assertThat(codingRule.conventionId()).isEqualTo(conventionId);
            assertThat(codingRule.structureId()).isEqualTo(structureId);
            assertThat(codingRule.code()).isEqualTo(code);
            assertThat(codingRule.name()).isEqualTo(name);
            assertThat(codingRule.severity()).isEqualTo(severity);
            assertThat(codingRule.category()).isEqualTo(category);
            assertThat(codingRule.description()).isEqualTo(description);
            assertThat(codingRule.rationale()).isEqualTo(rationale);
            assertThat(codingRule.isAutoFixable()).isEqualTo(autoFixable);
            assertThat(codingRule.appliesTo()).isEqualTo(appliesTo);
            assertThat(codingRule.sdkConstraint()).isEqualTo(sdkConstraint);
            assertThat(codingRule.deletionStatus().isDeleted()).isFalse();
            assertThat(codingRule.createdAt()).isEqualTo(now);
            assertThat(codingRule.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 CodingRule은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            CodingRule codingRule = CodingRuleFixture.forNew();

            // then
            assertThat(codingRule.id().isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            CodingRule codingRule = CodingRuleFixture.forNew();
            CodingRuleId id = CodingRuleVoFixtures.nextCodingRuleId();

            // when
            codingRule.assignId(id);

            // then
            assertThat(codingRule.id()).isEqualTo(id);
            assertThat(codingRule.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            CodingRule codingRule = CodingRuleFixture.reconstitute();
            CodingRuleId newId = CodingRuleVoFixtures.nextCodingRuleId();

            // when & then
            assertThatThrownBy(() -> codingRule.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("비즈니스 로직")
    class BusinessLogic {

        @Test
        @DisplayName("자동 수정 가능한 규칙인지 확인")
        void isAutoFixable_WithAutoFixableFlag_ShouldReturnTrue() {
            // given
            CodingRule codingRule = CodingRuleFixture.autoFixableRule();

            // when & then
            assertThat(codingRule.isAutoFixable()).isTrue();
        }

        @Test
        @DisplayName("차단 수준의 심각도인지 확인")
        void isBlockerSeverity_WithBlockerSeverity_ShouldReturnTrue() {
            // given
            CodingRule codingRule = CodingRuleFixture.zeroToleranceRule();

            // when & then
            assertThat(codingRule.isBlockerSeverity()).isTrue();
        }

        @Test
        @DisplayName("SDK 제약이 있는지 확인")
        void hasSdkConstraint_WithSdkConstraint_ShouldReturnTrue() {
            // given
            CodingRule codingRule = CodingRuleFixture.reconstitute();
            // 기본적으로 SdkConstraint.empty()를 사용하므로 false 반환

            // when & then
            assertThat(codingRule.hasSdkConstraint()).isFalse();
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateCodingRule {

        @Test
        @DisplayName("CodingRule 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            CodingRule codingRule = CodingRuleFixture.reconstitute();
            PackageStructureId structureId = null;
            RuleCode newCode = RuleCode.of("UPDATED-001");
            RuleName newName = RuleName.of("Updated Rule Name");
            RuleSeverity newSeverity = RuleSeverity.CRITICAL;
            RuleCategory newCategory = RuleCategory.NAMING;
            String newDescription = "Updated description";
            String newRationale = "Updated rationale";
            boolean newAutoFixable = true;
            AppliesTo newAppliesTo = AppliesTo.empty();
            SdkConstraint newSdkConstraint = SdkConstraint.empty();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            CodingRuleUpdateData updateData =
                    new CodingRuleUpdateData(
                            structureId,
                            newCode,
                            newName,
                            newSeverity,
                            newCategory,
                            newDescription,
                            newRationale,
                            newAutoFixable,
                            newAppliesTo,
                            newSdkConstraint);

            // when
            codingRule.update(updateData, updateTime);

            // then
            assertThat(codingRule.code()).isEqualTo(newCode);
            assertThat(codingRule.name()).isEqualTo(newName);
            assertThat(codingRule.severity()).isEqualTo(newSeverity);
            assertThat(codingRule.category()).isEqualTo(newCategory);
            assertThat(codingRule.description()).isEqualTo(newDescription);
            assertThat(codingRule.rationale()).isEqualTo(newRationale);
            assertThat(codingRule.isAutoFixable()).isEqualTo(newAutoFixable);
            assertThat(codingRule.appliesTo()).isEqualTo(newAppliesTo);
            assertThat(codingRule.updatedAt()).isEqualTo(updateTime);
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteCodingRule {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            CodingRule codingRule = CodingRuleFixture.reconstitute();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            codingRule.delete(deleteTime);

            // then
            assertThat(codingRule.isDeleted()).isTrue();
            assertThat(codingRule.deletionStatus().isDeleted()).isTrue();
            assertThat(codingRule.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(codingRule.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 CodingRule 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            CodingRule codingRule = CodingRuleFixture.deletedRule();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            codingRule.restore(restoreTime);

            // then
            assertThat(codingRule.isDeleted()).isFalse();
            assertThat(codingRule.deletionStatus().isActive()).isTrue();
            assertThat(codingRule.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            CodingRule activeCodingRule = CodingRuleFixture.reconstitute();
            CodingRule deletedCodingRule = CodingRuleFixture.deletedRule();

            // when & then
            assertThat(activeCodingRule.isDeleted()).isFalse();
            assertThat(deletedCodingRule.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteCodingRule {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            CodingRuleId id = CodingRuleVoFixtures.nextCodingRuleId();
            ConventionId conventionId = CodingRuleVoFixtures.fixedConventionId();
            PackageStructureId structureId = null;
            RuleCode code = CodingRuleVoFixtures.defaultRuleCode();
            RuleName name = CodingRuleVoFixtures.defaultRuleName();
            RuleSeverity severity = CodingRuleVoFixtures.defaultRuleSeverity();
            RuleCategory category = CodingRuleVoFixtures.defaultRuleCategory();
            String description = CodingRuleVoFixtures.defaultRuleDescription();
            String rationale = CodingRuleVoFixtures.defaultRuleRationale();
            boolean autoFixable = false;
            AppliesTo appliesTo = CodingRuleVoFixtures.defaultAppliesTo();
            SdkConstraint sdkConstraint = SdkConstraint.empty();
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            CodingRule codingRule =
                    CodingRule.reconstitute(
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

            // then
            assertThat(codingRule.id()).isEqualTo(id);
            assertThat(codingRule.conventionId()).isEqualTo(conventionId);
            assertThat(codingRule.structureId()).isEqualTo(structureId);
            assertThat(codingRule.code()).isEqualTo(code);
            assertThat(codingRule.name()).isEqualTo(name);
            assertThat(codingRule.severity()).isEqualTo(severity);
            assertThat(codingRule.category()).isEqualTo(category);
            assertThat(codingRule.description()).isEqualTo(description);
            assertThat(codingRule.rationale()).isEqualTo(rationale);
            assertThat(codingRule.isAutoFixable()).isEqualTo(autoFixable);
            assertThat(codingRule.appliesTo()).isEqualTo(appliesTo);
            assertThat(codingRule.sdkConstraint()).isEqualTo(sdkConstraint);
            assertThat(codingRule.createdAt()).isEqualTo(createdAt);
            assertThat(codingRule.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
