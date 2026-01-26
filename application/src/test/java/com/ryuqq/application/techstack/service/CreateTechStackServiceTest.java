package com.ryuqq.application.techstack.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.factory.command.TechStackCommandFactory;
import com.ryuqq.application.techstack.manager.TechStackPersistenceManager;
import com.ryuqq.application.techstack.validator.TechStackValidator;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.exception.TechStackDuplicateNameException;
import com.ryuqq.domain.techstack.vo.TechStackName;
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
 * CreateTechStackService 단위 테스트
 *
 * <p>TechStack 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateTechStackService 단위 테스트")
class CreateTechStackServiceTest {

    @Mock private TechStackValidator techStackValidator;

    @Mock private TechStackCommandFactory techStackCommandFactory;

    @Mock private TechStackPersistenceManager techStackPersistenceManager;

    @Mock private TechStack techStack;

    private CreateTechStackService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateTechStackService(
                        techStackValidator, techStackCommandFactory, techStackPersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 TechStack 생성")
        void execute_WithValidCommand_ShouldCreateTechStack() {
            // given
            CreateTechStackCommand command = createDefaultCommand();
            TechStackName techStackName = TechStackName.of(command.name());
            Long expectedId = 1L;

            willDoNothing().given(techStackValidator).validateNameNotDuplicate(techStackName);
            given(techStackCommandFactory.create(command)).willReturn(techStack);
            given(techStackPersistenceManager.persist(techStack)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(techStackValidator).should().validateNameNotDuplicate(techStackName);
            then(techStackCommandFactory).should().create(command);
            then(techStackPersistenceManager).should().persist(techStack);
        }

        @Test
        @DisplayName("실패 - 중복된 이름인 경우")
        void execute_WhenNameDuplicate_ShouldThrowException() {
            // given
            CreateTechStackCommand command = createDefaultCommand();
            TechStackName techStackName = TechStackName.of(command.name());

            willThrow(new TechStackDuplicateNameException(command.name()))
                    .given(techStackValidator)
                    .validateNameNotDuplicate(techStackName);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TechStackDuplicateNameException.class);

            then(techStackCommandFactory).shouldHaveNoInteractions();
            then(techStackPersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateTechStackCommand createDefaultCommand() {
        return new CreateTechStackCommand(
                "Spring Boot 3.5",
                "JAVA",
                "21",
                List.of("records", "sealed-classes"),
                "SPRING_BOOT",
                "3.5.0",
                List.of("spring-web", "spring-data-jpa"),
                "JVM",
                "JVM",
                "GRADLE",
                "build.gradle",
                List.of());
    }
}
