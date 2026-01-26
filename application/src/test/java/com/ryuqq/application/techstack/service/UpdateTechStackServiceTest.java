package com.ryuqq.application.techstack.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import com.ryuqq.application.techstack.factory.command.TechStackCommandFactory;
import com.ryuqq.application.techstack.fixture.UpdateTechStackCommandFixture;
import com.ryuqq.application.techstack.manager.TechStackPersistenceManager;
import com.ryuqq.application.techstack.validator.TechStackValidator;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.aggregate.TechStackUpdateData;
import com.ryuqq.domain.techstack.exception.TechStackDuplicateNameException;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import com.ryuqq.domain.techstack.fixture.TechStackFixture;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.TechStackName;
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
 * UpdateTechStackService 단위 테스트
 *
 * <p>TechStack 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateTechStackService 단위 테스트")
class UpdateTechStackServiceTest {

    @Mock private TechStackValidator techStackValidator;

    @Mock private TechStackCommandFactory techStackCommandFactory;

    @Mock private TechStackPersistenceManager techStackPersistenceManager;

    @Mock private TechStackUpdateData updateData;

    private UpdateTechStackService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateTechStackService(
                        techStackValidator, techStackCommandFactory, techStackPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 TechStack 수정")
        void execute_WithValidCommand_ShouldUpdateTechStack() {
            // given
            UpdateTechStackCommand command = UpdateTechStackCommandFixture.defaultCommand();
            TechStackId techStackId = TechStackId.of(command.id());
            TechStackName techStackName = TechStackName.of(command.name());
            TechStack techStack = TechStackFixture.defaultExistingTechStack();
            Instant changedAt = Instant.now();

            UpdateContext<TechStackId, TechStackUpdateData> context =
                    new UpdateContext<>(techStackId, updateData, changedAt);

            given(techStackCommandFactory.createUpdateContext(command)).willReturn(context);
            willDoNothing()
                    .given(techStackValidator)
                    .validateNameNotDuplicateExcluding(techStackName, techStackId);
            given(techStackValidator.findExistingOrThrow(techStackId)).willReturn(techStack);

            // when
            sut.execute(command);

            // then
            then(techStackCommandFactory).should().createUpdateContext(command);
            then(techStackValidator)
                    .should()
                    .validateNameNotDuplicateExcluding(techStackName, techStackId);
            then(techStackValidator).should().findExistingOrThrow(techStackId);
            then(techStackPersistenceManager).should().persist(techStack);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 TechStack인 경우")
        void execute_WhenNotFound_ShouldThrowException() {
            // given
            UpdateTechStackCommand command = UpdateTechStackCommandFixture.defaultCommand();
            TechStackId techStackId = TechStackId.of(command.id());
            TechStackName techStackName = TechStackName.of(command.name());
            Instant changedAt = Instant.now();

            UpdateContext<TechStackId, TechStackUpdateData> context =
                    new UpdateContext<>(techStackId, updateData, changedAt);

            given(techStackCommandFactory.createUpdateContext(command)).willReturn(context);
            willDoNothing()
                    .given(techStackValidator)
                    .validateNameNotDuplicateExcluding(techStackName, techStackId);
            willThrow(new TechStackNotFoundException(command.id()))
                    .given(techStackValidator)
                    .findExistingOrThrow(techStackId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TechStackNotFoundException.class);

            then(techStackPersistenceManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우")
        void execute_WhenNameDuplicate_ShouldThrowException() {
            // given
            UpdateTechStackCommand command = UpdateTechStackCommandFixture.defaultCommand();
            TechStackId techStackId = TechStackId.of(command.id());
            TechStackName techStackName = TechStackName.of(command.name());
            Instant changedAt = Instant.now();

            UpdateContext<TechStackId, TechStackUpdateData> context =
                    new UpdateContext<>(techStackId, updateData, changedAt);

            given(techStackCommandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new TechStackDuplicateNameException(command.name()))
                    .given(techStackValidator)
                    .validateNameNotDuplicateExcluding(techStackName, techStackId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TechStackDuplicateNameException.class);

            then(techStackValidator).shouldHaveNoMoreInteractions();
            then(techStackPersistenceManager).shouldHaveNoInteractions();
        }
    }
}
