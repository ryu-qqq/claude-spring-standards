package com.ryuqq.application.zerotolerance.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.zerotolerance.dto.command.CreateZeroToleranceRuleCommand;
import com.ryuqq.application.zerotolerance.dto.command.UpdateZeroToleranceRuleCommand;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRuleUpdateData;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.vo.DetectionType;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ZeroToleranceRuleCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ZeroToleranceRuleCommandFactory 단위 테스트")
class ZeroToleranceRuleCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ZeroToleranceRuleCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ZeroToleranceRuleCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateZeroToleranceRuleCommand로 ZeroToleranceRule 생성")
        void create_WithValidCommand_ShouldReturnZeroToleranceRule() {
            // given
            CreateZeroToleranceRuleCommand command =
                    new CreateZeroToleranceRuleCommand(
                            1L,
                            "LOMBOK_USAGE",
                            "@(Data|Getter|Setter|Builder)",
                            DetectionType.REGEX,
                            true,
                            "Lombok 사용 금지");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ZeroToleranceRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.ruleId().value()).isEqualTo(command.ruleId());
            assertThat(result.type().value()).isEqualTo(command.type());
            assertThat(result.detectionPattern().value()).isEqualTo(command.detectionPattern());
            assertThat(result.detectionType()).isEqualTo(command.detectionType());
            assertThat(result.autoRejectPr()).isEqualTo(command.autoRejectPr());
            assertThat(result.errorMessage().value()).isEqualTo(command.errorMessage());
        }

        @Test
        @DisplayName("성공 - AST 탐지 타입으로 ZeroToleranceRule 생성")
        void create_WithAstDetectionType_ShouldReturnZeroToleranceRule() {
            // given
            CreateZeroToleranceRuleCommand command =
                    new CreateZeroToleranceRuleCommand(
                            2L,
                            "DIRECT_FIELD_ACCESS",
                            "FieldAccess",
                            DetectionType.AST,
                            false,
                            "직접 필드 접근 금지");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ZeroToleranceRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.detectionType()).isEqualTo(DetectionType.AST);
            assertThat(result.autoRejectPr()).isFalse();
        }

        @Test
        @DisplayName("성공 - Instant를 직접 전달하여 ZeroToleranceRule 생성")
        void create_WithExplicitTime_ShouldReturnZeroToleranceRuleWithGivenTime() {
            // given
            CreateZeroToleranceRuleCommand command =
                    new CreateZeroToleranceRuleCommand(
                            1L, "TEST_TYPE", "pattern", DetectionType.REGEX, true, "테스트 에러 메시지");
            Instant customTime = Instant.parse("2024-06-01T12:00:00Z");

            // when
            ZeroToleranceRule result = sut.create(command, customTime);

            // then
            assertThat(result).isNotNull();
            assertThat(result.createdAt()).isEqualTo(customTime);
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdateZeroToleranceRuleCommand로 ZeroToleranceRuleUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateZeroToleranceRuleCommand command =
                    new UpdateZeroToleranceRuleCommand(
                            1L,
                            "UPDATED_TYPE",
                            "updated_pattern",
                            DetectionType.REGEX,
                            false,
                            "수정된 에러 메시지");

            // when
            ZeroToleranceRuleUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.type().value()).isEqualTo(command.type());
            assertThat(result.detectionPattern().value()).isEqualTo(command.detectionPattern());
            assertThat(result.detectionType()).isEqualTo(command.detectionType());
            assertThat(result.autoRejectPr()).isEqualTo(command.autoRejectPr());
            assertThat(result.errorMessage().value()).isEqualTo(command.errorMessage());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateZeroToleranceRuleCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateZeroToleranceRuleCommand command =
                    new UpdateZeroToleranceRuleCommand(
                            1L, "TYPE", "pattern", DetectionType.REGEX, true, "에러 메시지");
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ZeroToleranceRuleId, ZeroToleranceRuleUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.zeroToleranceRuleId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
