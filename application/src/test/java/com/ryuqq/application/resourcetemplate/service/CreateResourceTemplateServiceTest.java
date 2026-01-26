package com.ryuqq.application.resourcetemplate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.factory.command.ResourceTemplateCommandFactory;
import com.ryuqq.application.resourcetemplate.manager.ResourceTemplatePersistenceManager;
import com.ryuqq.domain.resourcetemplate.aggregate.ResourceTemplate;
import com.ryuqq.domain.resourcetemplate.id.ResourceTemplateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateResourceTemplateService 단위 테스트
 *
 * <p>ResourceTemplate 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateResourceTemplateService 단위 테스트")
class CreateResourceTemplateServiceTest {

    @Mock private ResourceTemplateCommandFactory resourceTemplateCommandFactory;

    @Mock private ResourceTemplatePersistenceManager resourceTemplatePersistenceManager;

    @Mock private ResourceTemplate resourceTemplate;

    private CreateResourceTemplateService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateResourceTemplateService(
                        resourceTemplateCommandFactory, resourceTemplatePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ResourceTemplate 생성")
        void execute_WithValidCommand_ShouldCreateResourceTemplate() {
            // given
            CreateResourceTemplateCommand command = createDefaultCommand();
            ResourceTemplateId savedId = ResourceTemplateId.of(1L);

            given(resourceTemplateCommandFactory.create(command)).willReturn(resourceTemplate);
            given(resourceTemplatePersistenceManager.persist(resourceTemplate)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(resourceTemplateCommandFactory).should().create(command);
            then(resourceTemplatePersistenceManager).should().persist(resourceTemplate);
        }
    }

    private CreateResourceTemplateCommand createDefaultCommand() {
        return new CreateResourceTemplateCommand(
                1L,
                "CONFIG",
                "application.yml",
                "YAML",
                "Application configuration",
                "spring:\n  application:",
                true);
    }
}
