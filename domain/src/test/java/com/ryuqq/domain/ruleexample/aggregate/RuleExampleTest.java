package com.ryuqq.domain.ruleexample.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.ruleexample.fixture.RuleExampleFixture;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.vo.ExampleCode;
import com.ryuqq.domain.ruleexample.vo.ExampleLanguage;
import com.ryuqq.domain.ruleexample.vo.ExampleSource;
import com.ryuqq.domain.ruleexample.vo.ExampleType;
import com.ryuqq.domain.ruleexample.vo.HighlightLines;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * RuleExample Aggregate 단위 테스트
 *
 * @author development-team
 */
@DisplayName("RuleExample Aggregate")
class RuleExampleTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @Nested
    @DisplayName("생성")
    class CreateRuleExample {

        @Test
        @DisplayName("신규 RuleExample 생성 성공")
        void forNew_WithValidData_ShouldSucceed() {
            // given
            CodingRuleId ruleId = RuleExampleFixture.fixedCodingRuleId();
            ExampleType exampleType = ExampleType.GOOD;
            ExampleCode code = ExampleCode.of("public class Order { }");
            ExampleLanguage language = ExampleLanguage.JAVA;
            String explanation = "올바른 예시 설명";
            HighlightLines highlightLines = HighlightLines.of(List.of(1, 2));
            Instant now = FIXED_CLOCK.instant();

            // when
            RuleExample ruleExample =
                    RuleExample.forNew(
                            ruleId, exampleType, code, language, explanation, highlightLines, now);

            // then
            assertThat(ruleExample.isNew()).isTrue();
            assertThat(ruleExample.ruleId()).isEqualTo(ruleId);
            assertThat(ruleExample.exampleType()).isEqualTo(exampleType);
            assertThat(ruleExample.code()).isEqualTo(code);
            assertThat(ruleExample.language()).isEqualTo(language);
            assertThat(ruleExample.explanation()).isEqualTo(explanation);
            assertThat(ruleExample.highlightLines()).isEqualTo(highlightLines);
            assertThat(ruleExample.source()).isEqualTo(ExampleSource.MANUAL);
            assertThat(ruleExample.feedbackId()).isNull();
            assertThat(ruleExample.deletionStatus().isDeleted()).isFalse();
            assertThat(ruleExample.createdAt()).isEqualTo(now);
            assertThat(ruleExample.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 RuleExample은 null ID를 가짐")
        void forNew_ShouldHaveNullId() {
            // when
            RuleExample ruleExample = RuleExampleFixture.forNew();

            // then
            assertThat(ruleExample.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("피드백에서 승격된 RuleExample 생성 성공")
        void fromFeedback_WithValidData_ShouldSucceed() {
            // given
            CodingRuleId ruleId = RuleExampleFixture.fixedCodingRuleId();
            ExampleType exampleType = ExampleType.GOOD;
            ExampleCode code = ExampleCode.of("public class Order { }");
            ExampleLanguage language = ExampleLanguage.JAVA;
            String explanation = "피드백에서 승격된 예시";
            HighlightLines highlightLines = HighlightLines.of(List.of(1));
            Long feedbackId = 999L;
            Instant now = FIXED_CLOCK.instant();

            // when
            RuleExample ruleExample =
                    RuleExample.fromFeedback(
                            ruleId,
                            exampleType,
                            code,
                            language,
                            explanation,
                            highlightLines,
                            feedbackId,
                            now);

            // then
            assertThat(ruleExample.isNew()).isTrue();
            assertThat(ruleExample.source()).isEqualTo(ExampleSource.AGENT_FEEDBACK);
            assertThat(ruleExample.feedbackId()).isEqualTo(feedbackId);
            assertThat(ruleExample.isFromFeedback()).isTrue();
        }
    }

    @Nested
    @DisplayName("ID 할당")
    class AssignId {

        @Test
        @DisplayName("신규 엔티티에 ID 할당 성공")
        void assignId_WhenNew_ShouldSucceed() {
            // given
            RuleExample ruleExample = RuleExampleFixture.forNew();
            RuleExampleId id = RuleExampleFixture.nextRuleExampleId();

            // when
            ruleExample.assignId(id);

            // then
            assertThat(ruleExample.id()).isEqualTo(id);
            assertThat(ruleExample.isNew()).isFalse();
        }

        @Test
        @DisplayName("이미 ID가 할당된 엔티티에 ID 할당 시 예외")
        void assignId_WhenAlreadyAssigned_ShouldThrow() {
            // given
            RuleExample ruleExample = RuleExampleFixture.defaultExistingRuleExample();
            RuleExampleId newId = RuleExampleFixture.nextRuleExampleId();

            // when & then
            assertThatThrownBy(() -> ruleExample.assignId(newId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id already assigned");
        }
    }

    @Nested
    @DisplayName("비즈니스 로직")
    class BusinessLogic {

        @Test
        @DisplayName("올바른 예시인지 확인")
        void isGoodExample_WithGoodType_ShouldReturnTrue() {
            // given
            RuleExample ruleExample = RuleExampleFixture.goodExample();

            // when & then
            assertThat(ruleExample.isGoodExample()).isTrue();
        }

        @Test
        @DisplayName("잘못된 예시인지 확인")
        void isBadExample_WithBadType_ShouldReturnTrue() {
            // given
            RuleExample ruleExample = RuleExampleFixture.badExample();

            // when & then
            assertThat(ruleExample.isBadExample()).isTrue();
        }

        @Test
        @DisplayName("피드백에서 승격된 예시인지 확인")
        void isFromFeedback_WithFeedbackSource_ShouldReturnTrue() {
            // given
            RuleExample ruleExample = RuleExampleFixture.fromFeedbackExample();

            // when & then
            assertThat(ruleExample.isFromFeedback()).isTrue();
        }
    }

    @Nested
    @DisplayName("삭제")
    class DeleteRuleExample {

        @Test
        @DisplayName("Soft Delete 성공")
        void delete_ShouldMarkAsDeleted() {
            // given
            RuleExample ruleExample = RuleExampleFixture.defaultExistingRuleExample();
            Instant deleteTime = FIXED_CLOCK.instant().plusSeconds(3600);

            // when
            ruleExample.delete(deleteTime);

            // then
            assertThat(ruleExample.isDeleted()).isTrue();
            assertThat(ruleExample.deletionStatus().isDeleted()).isTrue();
            assertThat(ruleExample.deletionStatus().deletedAt()).isEqualTo(deleteTime);
            assertThat(ruleExample.updatedAt()).isEqualTo(deleteTime);
        }

        @Test
        @DisplayName("삭제된 RuleExample 복원 성공")
        void restore_WhenDeleted_ShouldRestore() {
            // given
            RuleExample ruleExample = RuleExampleFixture.deletedRuleExample();
            Instant restoreTime = FIXED_CLOCK.instant().plusSeconds(7200);

            // when
            ruleExample.restore(restoreTime);

            // then
            assertThat(ruleExample.isDeleted()).isFalse();
            assertThat(ruleExample.deletionStatus().isActive()).isTrue();
            assertThat(ruleExample.updatedAt()).isEqualTo(restoreTime);
        }

        @Test
        @DisplayName("삭제 여부 확인")
        void isDeleted_ShouldReturnCorrectStatus() {
            // given
            RuleExample activeRuleExample = RuleExampleFixture.defaultExistingRuleExample();
            RuleExample deletedRuleExample = RuleExampleFixture.deletedRuleExample();

            // when & then
            assertThat(activeRuleExample.isDeleted()).isFalse();
            assertThat(deletedRuleExample.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("복원")
    class ReconstituteRuleExample {

        @Test
        @DisplayName("영속성에서 복원 성공")
        void reconstitute_WithValidData_ShouldSucceed() {
            // given
            RuleExampleId id = RuleExampleFixture.nextRuleExampleId();
            CodingRuleId ruleId = RuleExampleFixture.fixedCodingRuleId();
            ExampleType exampleType = ExampleType.GOOD;
            ExampleCode code = ExampleCode.of("public class Order { }");
            ExampleLanguage language = ExampleLanguage.JAVA;
            String explanation = "올바른 예시 설명";
            HighlightLines highlightLines = HighlightLines.of(List.of(1, 2));
            ExampleSource source = ExampleSource.MANUAL;
            Long feedbackId = null;
            DeletionStatus deletionStatus = DeletionStatus.active();
            Instant createdAt = FIXED_CLOCK.instant();
            Instant updatedAt = FIXED_CLOCK.instant();

            // when
            RuleExample ruleExample =
                    RuleExample.reconstitute(
                            id,
                            ruleId,
                            exampleType,
                            code,
                            language,
                            explanation,
                            highlightLines,
                            source,
                            feedbackId,
                            deletionStatus,
                            createdAt,
                            updatedAt);

            // then
            assertThat(ruleExample.id()).isEqualTo(id);
            assertThat(ruleExample.ruleId()).isEqualTo(ruleId);
            assertThat(ruleExample.exampleType()).isEqualTo(exampleType);
            assertThat(ruleExample.code()).isEqualTo(code);
            assertThat(ruleExample.language()).isEqualTo(language);
            assertThat(ruleExample.explanation()).isEqualTo(explanation);
            assertThat(ruleExample.highlightLines()).isEqualTo(highlightLines);
            assertThat(ruleExample.source()).isEqualTo(source);
            assertThat(ruleExample.feedbackId()).isEqualTo(feedbackId);
            assertThat(ruleExample.createdAt()).isEqualTo(createdAt);
            assertThat(ruleExample.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
