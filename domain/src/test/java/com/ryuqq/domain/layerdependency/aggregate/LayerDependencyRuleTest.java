package com.ryuqq.domain.layerdependency.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layerdependency.fixture.LayerDependencyRuleFixture;
import com.ryuqq.domain.layerdependency.fixture.LayerDependencyRuleVoFixtures;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.ConditionDescription;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * LayerDependencyRule Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("LayerDependencyRule Aggregate")
class LayerDependencyRuleTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateLayerDependencyRule {

        @Test
        @DisplayName("신규 LayerDependencyRule 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            ArchitectureId architectureId = LayerDependencyRuleVoFixtures.fixedArchitectureId();
            LayerType fromLayer = LayerDependencyRuleVoFixtures.defaultFromLayer();
            LayerType toLayer = LayerDependencyRuleVoFixtures.defaultToLayer();
            DependencyType dependencyType = LayerDependencyRuleVoFixtures.defaultDependencyType();
            ConditionDescription conditionDescription =
                    LayerDependencyRuleVoFixtures.emptyConditionDescription();
            Instant now = FIXED_CLOCK.instant();

            // when
            LayerDependencyRule rule =
                    LayerDependencyRule.forNew(
                            architectureId,
                            fromLayer,
                            toLayer,
                            dependencyType,
                            conditionDescription,
                            now);

            // then
            assertThat(rule.isNew()).isTrue();
            assertThat(rule.architectureId()).isEqualTo(architectureId);
            assertThat(rule.fromLayer()).isEqualTo(fromLayer);
            assertThat(rule.toLayer()).isEqualTo(toLayer);
            assertThat(rule.dependencyType()).isEqualTo(dependencyType);
            assertThat(rule.conditionDescription()).isEqualTo(conditionDescription);
            assertThat(rule.deletionStatus().isDeleted()).isFalse();
            assertThat(rule.createdAt()).isEqualTo(now);
            assertThat(rule.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 LayerDependencyRule은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            LayerDependencyRule rule = LayerDependencyRuleFixture.forNew();

            // then
            assertThat(rule.id().isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.forNew();
            LayerDependencyRuleId id = LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId();

            // when
            rule.assignId(id);

            // then
            assertThat(rule.id()).isEqualTo(id);
            assertThat(rule.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            LayerDependencyRule rule =
                    LayerDependencyRuleFixture.defaultExistingLayerDependencyRule();
            LayerDependencyRuleId newId = LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId();

            // when & then
            assertThatThrownBy(() -> rule.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("비즈니스 로직")
    class BusinessLogic {

        @Test
        @DisplayName("허용된 의존성인지 확인")
        void isAllowed_WithAllowedType_ShouldReturnTrue() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.allowedRule();

            // when & then
            assertThat(rule.isAllowed()).isTrue();
        }

        @Test
        @DisplayName("금지된 의존성인지 확인")
        void isForbidden_WithForbiddenType_ShouldReturnTrue() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.forbiddenRule();

            // when & then
            assertThat(rule.isForbidden()).isTrue();
        }

        @Test
        @DisplayName("조건부 의존성인지 확인")
        void isConditional_WithConditionalType_ShouldReturnTrue() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.conditionalRule();

            // when & then
            assertThat(rule.isConditional()).isTrue();
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateLayerDependencyRule {

        @Test
        @DisplayName("LayerDependencyRule 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            LayerDependencyRule rule =
                    LayerDependencyRuleFixture.defaultExistingLayerDependencyRule();
            LayerType newFromLayer = LayerType.ADAPTER_IN;
            LayerType newToLayer = LayerType.DOMAIN;
            DependencyType newDependencyType = DependencyType.FORBIDDEN;
            ConditionDescription newConditionDescription = ConditionDescription.of("업데이트된 조건 설명");
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            LayerDependencyRuleUpdateData updateData =
                    new LayerDependencyRuleUpdateData(
                            newFromLayer, newToLayer, newDependencyType, newConditionDescription);

            // when
            rule.update(updateData, updateTime);

            // then
            assertThat(rule.fromLayer()).isEqualTo(newFromLayer);
            assertThat(rule.toLayer()).isEqualTo(newToLayer);
            assertThat(rule.dependencyType()).isEqualTo(newDependencyType);
            assertThat(rule.conditionDescription()).isEqualTo(newConditionDescription);
            assertThat(rule.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("빈 조건 설명으로 수정 성공")
        void update_WithEmptyConditionDescription_ShouldSucceed() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.conditionalRule();
            LayerType newFromLayer = LayerType.DOMAIN;
            LayerType newToLayer = LayerType.APPLICATION;
            DependencyType newDependencyType = DependencyType.ALLOWED;
            ConditionDescription newConditionDescription = ConditionDescription.empty();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            LayerDependencyRuleUpdateData updateData =
                    new LayerDependencyRuleUpdateData(
                            newFromLayer, newToLayer, newDependencyType, newConditionDescription);

            // when
            rule.update(updateData, updateTime);

            // then
            assertThat(rule.conditionDescription().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteLayerDependencyRule {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            LayerDependencyRule rule =
                    LayerDependencyRuleFixture.defaultExistingLayerDependencyRule();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            rule.delete(deleteTime);

            // then
            assertThat(rule.isDeleted()).isTrue();
            assertThat(rule.deletionStatus().isDeleted()).isTrue();
            assertThat(rule.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(rule.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 LayerDependencyRule 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            LayerDependencyRule rule = LayerDependencyRuleFixture.deletedRule();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            rule.restore(restoreTime);

            // then
            assertThat(rule.isDeleted()).isFalse();
            assertThat(rule.deletionStatus().isActive()).isTrue();
            assertThat(rule.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            LayerDependencyRule activeRule =
                    LayerDependencyRuleFixture.defaultExistingLayerDependencyRule();
            LayerDependencyRule deletedRule = LayerDependencyRuleFixture.deletedRule();

            // when & then
            assertThat(activeRule.isDeleted()).isFalse();
            assertThat(deletedRule.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteLayerDependencyRule {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            LayerDependencyRuleId id = LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId();
            ArchitectureId architectureId = LayerDependencyRuleVoFixtures.fixedArchitectureId();
            LayerType fromLayer = LayerDependencyRuleVoFixtures.defaultFromLayer();
            LayerType toLayer = LayerDependencyRuleVoFixtures.defaultToLayer();
            DependencyType dependencyType = LayerDependencyRuleVoFixtures.defaultDependencyType();
            ConditionDescription conditionDescription =
                    LayerDependencyRuleVoFixtures.emptyConditionDescription();
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            LayerDependencyRule rule =
                    LayerDependencyRule.reconstitute(
                            id,
                            architectureId,
                            fromLayer,
                            toLayer,
                            dependencyType,
                            conditionDescription,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(rule.id()).isEqualTo(id);
            assertThat(rule.architectureId()).isEqualTo(architectureId);
            assertThat(rule.fromLayer()).isEqualTo(fromLayer);
            assertThat(rule.toLayer()).isEqualTo(toLayer);
            assertThat(rule.dependencyType()).isEqualTo(dependencyType);
            assertThat(rule.conditionDescription()).isEqualTo(conditionDescription);
            assertThat(rule.createdAt()).isEqualTo(createdAt);
            assertThat(rule.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
