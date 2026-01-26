package com.ryuqq.application.classtemplate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.factory.command.ClassTemplateCommandFactory;
import com.ryuqq.application.classtemplate.manager.ClassTemplatePersistenceManager;
import com.ryuqq.application.classtemplate.validator.ClassTemplateValidator;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateDuplicateCodeException;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
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
 * CreateClassTemplateService 단위 테스트
 *
 * <p>ClassTemplate 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateClassTemplateService 단위 테스트")
class CreateClassTemplateServiceTest {

    @Mock private ClassTemplateValidator classTemplateValidator;

    @Mock private ClassTemplateCommandFactory classTemplateCommandFactory;

    @Mock private ClassTemplatePersistenceManager classTemplatePersistenceManager;

    @Mock private ClassTemplate classTemplate;

    private CreateClassTemplateService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateClassTemplateService(
                        classTemplateValidator,
                        classTemplateCommandFactory,
                        classTemplatePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ClassTemplate 생성")
        void execute_WithValidCommand_ShouldCreateClassTemplate() {
            // given
            CreateClassTemplateCommand command = createDefaultCommand();
            PackageStructureId structureId = PackageStructureId.of(command.structureId());
            TemplateCode templateCode = TemplateCode.of(command.templateCode());
            ClassTemplateId savedId = ClassTemplateId.of(1L);

            willDoNothing()
                    .given(classTemplateValidator)
                    .validateNotDuplicate(structureId, templateCode);
            given(classTemplateCommandFactory.create(command)).willReturn(classTemplate);
            given(classTemplatePersistenceManager.persist(classTemplate)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(classTemplateValidator).should().validateNotDuplicate(structureId, templateCode);
            then(classTemplateCommandFactory).should().create(command);
            then(classTemplatePersistenceManager).should().persist(classTemplate);
        }

        @Test
        @DisplayName("실패 - 중복된 템플릿 코드인 경우")
        void execute_WhenCodeDuplicate_ShouldThrowException() {
            // given
            CreateClassTemplateCommand command = createDefaultCommand();
            PackageStructureId structureId = PackageStructureId.of(command.structureId());
            TemplateCode templateCode = TemplateCode.of(command.templateCode());

            willThrow(new ClassTemplateDuplicateCodeException(structureId, templateCode))
                    .given(classTemplateValidator)
                    .validateNotDuplicate(structureId, templateCode);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ClassTemplateDuplicateCodeException.class);

            then(classTemplateCommandFactory).shouldHaveNoInteractions();
            then(classTemplatePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateClassTemplateCommand createDefaultCommand() {
        return new CreateClassTemplateCommand(
                1L,
                1L, // classTypeId (AGGREGATE)
                "AGG-TPL-001",
                "{Name}",
                "Aggregate Root template",
                List.of(),
                List.of("@Data", "@Getter"),
                List.of(),
                List.of(),
                List.of());
    }
}
