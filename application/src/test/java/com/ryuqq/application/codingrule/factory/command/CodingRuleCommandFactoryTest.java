package com.ryuqq.application.codingrule.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.codingrule.dto.command.CreateCodingRuleCommand;
import com.ryuqq.application.codingrule.dto.command.UpdateCodingRuleCommand;
import com.ryuqq.application.codingrule.fixture.CreateCodingRuleCommandFixture;
import com.ryuqq.application.codingrule.fixture.UpdateCodingRuleCommandFixture;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.aggregate.CodingRuleUpdateData;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
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
 * CodingRuleCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("CodingRuleCommandFactory 단위 테스트")
class CodingRuleCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private CodingRuleCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new CodingRuleCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateCodingRuleCommand로 CodingRule 생성")
        void create_WithValidCommand_ShouldReturnCodingRule() {
            // given
            CreateCodingRuleCommand command = CreateCodingRuleCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            CodingRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.severity().name()).isEqualTo(command.severity());
            assertThat(result.category().name()).isEqualTo(command.category());
            assertThat(result.description()).isEqualTo(command.description());
            assertThat(result.rationale()).isEqualTo(command.rationale());
            assertThat(result.isAutoFixable()).isEqualTo(command.autoFixable());
        }

        @Test
        @DisplayName("성공 - 자동 수정 가능한 CodingRule 생성")
        void create_WithAutoFixable_ShouldReturnAutoFixableCodingRule() {
            // given
            CreateCodingRuleCommand command =
                    new CreateCodingRuleCommand(
                            1L,
                            null,
                            "DOM-002",
                            "자동 수정 규칙",
                            "MINOR",
                            "STYLE",
                            "자동 수정 가능한 규칙",
                            "스타일 일관성",
                            true,
                            List.of("CLASS"),
                            null,
                            null,
                            null);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            CodingRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isAutoFixable()).isTrue();
        }

        @Test
        @DisplayName("성공 - PackageStructureId가 있는 CodingRule 생성")
        void create_WithStructureId_ShouldReturnCodingRuleWithStructureId() {
            // given
            CreateCodingRuleCommand command =
                    new CreateCodingRuleCommand(
                            1L,
                            5L,
                            "DOM-003",
                            "구조 규칙",
                            "MAJOR",
                            "STRUCTURE",
                            "구조 관련 규칙",
                            "아키텍처 일관성",
                            false,
                            List.of("CLASS"),
                            null,
                            null,
                            null);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            CodingRule result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.structureId()).isNotNull();
            assertThat(result.structureId().value()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("createUpdateData 메서드")
    class CreateUpdateData {

        @Test
        @DisplayName("성공 - UpdateCodingRuleCommand로 CodingRuleUpdateData 생성")
        void createUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateCodingRuleCommand command = UpdateCodingRuleCommandFixture.defaultCommand();

            // when
            CodingRuleUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.code().value()).isEqualTo(command.code());
            assertThat(result.name().value()).isEqualTo(command.name());
            assertThat(result.severity().name()).isEqualTo(command.severity());
            assertThat(result.category().name()).isEqualTo(command.category());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateCodingRuleCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateCodingRuleCommand command = UpdateCodingRuleCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<CodingRuleId, CodingRuleUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.codingRuleId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
