package com.ryuqq.application.ruleexample.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.ruleexample.dto.command.CreateRuleExampleCommand;
import com.ryuqq.application.ruleexample.dto.command.UpdateRuleExampleCommand;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.aggregate.RuleExampleUpdateData;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RuleExampleCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("RuleExampleCommandFactory 단위 테스트")
class RuleExampleCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private RuleExampleCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new RuleExampleCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateRuleExampleCommand로 RuleExample 생성")
        void create_WithValidCommand_ShouldReturnRuleExample() {
            // given
            CreateRuleExampleCommand command =
                    new CreateRuleExampleCommand(
                            1L, "GOOD", "public class Good {}", "JAVA", "올바른 예제", null);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            RuleExample result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.ruleId().value()).isEqualTo(command.ruleId());
            assertThat(result.exampleType().name()).isEqualTo(command.exampleType());
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.language().name()).isEqualTo(command.language());
            assertThat(result.explanation()).isEqualTo(command.explanation());
        }

        @Test
        @DisplayName("성공 - 하이라이트 라인이 포함된 RuleExample 생성")
        void create_WithHighlightLines_ShouldReturnRuleExampleWithHighlightLines() {
            // given
            CreateRuleExampleCommand command =
                    new CreateRuleExampleCommand(
                            1L, "BAD", "public class Bad {}", "JAVA", "잘못된 예제", List.of(1, 3, 5));
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            RuleExample result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.highlightLines().lines()).containsExactly(1, 3, 5);
        }

        @Test
        @DisplayName("성공 - 하이라이트 라인 없이 RuleExample 생성")
        void create_WithoutHighlightLines_ShouldReturnRuleExampleWithEmptyHighlightLines() {
            // given
            CreateRuleExampleCommand command =
                    new CreateRuleExampleCommand(
                            1L, "GOOD", "public class Good {}", "JAVA", "예제", null);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            RuleExample result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.highlightLines().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdateRuleExampleCommand로 RuleExampleUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateRuleExampleCommand command =
                    new UpdateRuleExampleCommand(
                            1L, "BAD", "modified code", "KOTLIN", "수정된 설명", List.of(2, 4));

            // when
            RuleExampleUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.exampleType()).isPresent();
            assertThat(result.code()).isPresent();
            assertThat(result.language()).isPresent();
        }

        @Test
        @DisplayName("성공 - 부분 업데이트 Command로 RuleExampleUpdateData 생성")
        void toUpdateData_WithPartialCommand_ShouldReturnPartialUpdateData() {
            // given
            UpdateRuleExampleCommand command =
                    new UpdateRuleExampleCommand(1L, null, "수정된 코드만", null, null, null);

            // when
            RuleExampleUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.exampleType()).isEmpty();
            assertThat(result.code()).isPresent();
            assertThat(result.language()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateRuleExampleCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateRuleExampleCommand command =
                    new UpdateRuleExampleCommand(1L, "GOOD", "code", "JAVA", "설명", List.of(1));
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<RuleExampleId, RuleExampleUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.ruleExampleId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
