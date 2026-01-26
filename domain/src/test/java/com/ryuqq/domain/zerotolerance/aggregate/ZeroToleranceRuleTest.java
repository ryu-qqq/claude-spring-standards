package com.ryuqq.domain.zerotolerance.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.zerotolerance.fixture.ZeroToleranceRuleFixture;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionPattern;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import com.ryuqq.domain.zerotolerance.vo.ErrorMessage;
import com.ryuqq.domain.zerotolerance.vo.ZeroToleranceType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ZeroToleranceRule Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("ZeroToleranceRule Aggregate")
class ZeroToleranceRuleTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateZeroToleranceRule {

        @Test
        @DisplayName("신규 ZeroToleranceRule 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            CodingRuleId ruleId = ZeroToleranceRuleFixture.fixedCodingRuleId();
            ZeroToleranceType type = ZeroToleranceType.lombokInDomain();
            DetectionPattern detectionPattern = DetectionPattern.of("@Data|@Getter|@Setter");
            DetectionType detectionType = DetectionType.REGEX;
            boolean autoRejectPr = false;
            ErrorMessage errorMessage = ErrorMessage.of("Lombok 사용 금지");
            Instant now = FIXED_CLOCK.instant();

            // when
            ZeroToleranceRule rule =
                    ZeroToleranceRule.forNew(
                            ruleId,
                            type,
                            detectionPattern,
                            detectionType,
                            autoRejectPr,
                            errorMessage,
                            now);

            // then
            assertThat(rule.isNew()).isTrue();
            assertThat(rule.ruleId()).isEqualTo(ruleId);
            assertThat(rule.type()).isEqualTo(type);
            assertThat(rule.detectionPattern()).isEqualTo(detectionPattern);
            assertThat(rule.detectionType()).isEqualTo(detectionType);
            assertThat(rule.autoRejectPr()).isEqualTo(autoRejectPr);
            assertThat(rule.errorMessage()).isEqualTo(errorMessage);
            assertThat(rule.deletionStatus().isDeleted()).isFalse();
            assertThat(rule.createdAt()).isEqualTo(now);
            assertThat(rule.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 ZeroToleranceRule은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.forNew();

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
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.forNew();
            ZeroToleranceRuleId id = ZeroToleranceRuleFixture.nextZeroToleranceRuleId();

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
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.defaultExistingZeroToleranceRule();
            ZeroToleranceRuleId newId = ZeroToleranceRuleFixture.nextZeroToleranceRuleId();

            // when & then
            assertThatThrownBy(() -> rule.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("수정")
    class UpdateZeroToleranceRule {

        @Test
        @DisplayName("ZeroToleranceRule 정보 수정 성공")
        void update_WithValidData_ShouldSucceed() {
            // given
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.defaultExistingZeroToleranceRule();
            ZeroToleranceRuleUpdateData updateData = ZeroToleranceRuleFixture.defaultUpdateData();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            rule.update(updateData, updateTime);

            // then
            assertThat(rule.type()).isEqualTo(updateData.type());
            assertThat(rule.detectionPattern()).isEqualTo(updateData.detectionPattern());
            assertThat(rule.detectionType()).isEqualTo(updateData.detectionType());
            assertThat(rule.autoRejectPr()).isEqualTo(updateData.autoRejectPr());
            assertThat(rule.errorMessage()).isEqualTo(updateData.errorMessage());
            assertThat(rule.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("PR 자동 거부 활성화 성공")
        void enableAutoReject_ShouldSetAutoRejectPrToTrue() {
            // given
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.defaultExistingZeroToleranceRule();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            rule.enableAutoReject(updateTime);

            // then
            assertThat(rule.autoRejectPr()).isTrue();
            assertThat(rule.updatedAt()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("PR 자동 거부 비활성화 성공")
        void disableAutoReject_ShouldSetAutoRejectPrToFalse() {
            // given
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.withAutoRejectPr();
            Instant updateTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            rule.disableAutoReject(updateTime);

            // then
            assertThat(rule.autoRejectPr()).isFalse();
            assertThat(rule.updatedAt()).isEqualTo(updateTime);
        }
    }

    @Nested
    @DisplayName("위반 탐지")
    class DetectViolation {

        @Test
        @DisplayName("정규식 기반 위반 탐지 성공")
        void detectViolation_WithRegexPattern_ShouldReturnTrue() {
            // given
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.defaultExistingZeroToleranceRule();
            String codeText = "@Data\npublic class Order { }";

            // when
            boolean detected = rule.detectViolation(codeText);

            // then
            assertThat(detected).isTrue();
        }

        @Test
        @DisplayName("위반 없는 코드 탐지 실패")
        void detectViolation_WithNoViolation_ShouldReturnFalse() {
            // given
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.defaultExistingZeroToleranceRule();
            String codeText = "public class Order { }";

            // when
            boolean detected = rule.detectViolation(codeText);

            // then
            assertThat(detected).isFalse();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteZeroToleranceRule {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.defaultExistingZeroToleranceRule();
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
        @DisplayName("삭제된 ZeroToleranceRule 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            ZeroToleranceRule rule = ZeroToleranceRuleFixture.deletedZeroToleranceRule();
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
            ZeroToleranceRule activeRule =
                    ZeroToleranceRuleFixture.defaultExistingZeroToleranceRule();
            ZeroToleranceRule deletedRule = ZeroToleranceRuleFixture.deletedZeroToleranceRule();

            // when & then
            assertThat(activeRule.isDeleted()).isFalse();
            assertThat(deletedRule.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteZeroToleranceRule {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            ZeroToleranceRuleId id = ZeroToleranceRuleFixture.nextZeroToleranceRuleId();
            CodingRuleId ruleId = ZeroToleranceRuleFixture.fixedCodingRuleId();
            ZeroToleranceType type = ZeroToleranceType.lombokInDomain();
            DetectionPattern detectionPattern = DetectionPattern.of("@Data|@Getter|@Setter");
            DetectionType detectionType = DetectionType.REGEX;
            boolean autoRejectPr = false;
            ErrorMessage errorMessage = ErrorMessage.of("Lombok 사용 금지");
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            ZeroToleranceRule rule =
                    ZeroToleranceRule.reconstitute(
                            id,
                            ruleId,
                            type,
                            detectionPattern,
                            detectionType,
                            autoRejectPr,
                            errorMessage,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(rule.id()).isEqualTo(id);
            assertThat(rule.ruleId()).isEqualTo(ruleId);
            assertThat(rule.type()).isEqualTo(type);
            assertThat(rule.detectionPattern()).isEqualTo(detectionPattern);
            assertThat(rule.detectionType()).isEqualTo(detectionType);
            assertThat(rule.autoRejectPr()).isEqualTo(autoRejectPr);
            assertThat(rule.errorMessage()).isEqualTo(errorMessage);
            assertThat(rule.createdAt()).isEqualTo(createdAt);
            assertThat(rule.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
