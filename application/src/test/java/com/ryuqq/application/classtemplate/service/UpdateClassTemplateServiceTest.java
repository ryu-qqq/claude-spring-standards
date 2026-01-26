package com.ryuqq.application.classtemplate.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.factory.command.ClassTemplateCommandFactory;
import com.ryuqq.application.classtemplate.manager.ClassTemplatePersistenceManager;
import com.ryuqq.application.classtemplate.validator.ClassTemplateValidator;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplateUpdateData;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateDuplicateCodeException;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
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
 * UpdateClassTemplateService 단위 테스트
 *
 * <p>ClassTemplate 수정 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("UpdateClassTemplateService 단위 테스트")
class UpdateClassTemplateServiceTest {

    @Mock private ClassTemplateValidator classTemplateValidator;

    @Mock private ClassTemplateCommandFactory classTemplateCommandFactory;

    @Mock private ClassTemplatePersistenceManager classTemplatePersistenceManager;

    @Mock private ClassTemplate classTemplate;

    @Mock private ClassTemplateUpdateData updateData;

    private UpdateClassTemplateService sut;

    @BeforeEach
    void setUp() {
        sut =
                new UpdateClassTemplateService(
                        classTemplateValidator,
                        classTemplateCommandFactory,
                        classTemplatePersistenceManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 ClassTemplate 수정")
        void execute_WithValidCommand_ShouldUpdateClassTemplate() {
            // given
            UpdateClassTemplateCommand command = createDefaultCommand();
            ClassTemplateId classTemplateId = ClassTemplateId.of(command.classTemplateId());
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of(command.templateCode());
            Instant changedAt = Instant.now();
            UpdateContext<ClassTemplateId, ClassTemplateUpdateData> context =
                    new UpdateContext<>(classTemplateId, updateData, changedAt);

            given(classTemplateCommandFactory.createUpdateContext(command)).willReturn(context);
            given(classTemplateValidator.findExistingOrThrow(classTemplateId))
                    .willReturn(classTemplate);
            given(classTemplate.structureId()).willReturn(structureId);
            given(updateData.templateCode()).willReturn(templateCode);
            willDoNothing()
                    .given(classTemplateValidator)
                    .validateNotDuplicateExcluding(
                            any(PackageStructureId.class),
                            any(TemplateCode.class),
                            any(ClassTemplateId.class));
            willDoNothing().given(classTemplate).update(updateData, changedAt);
            given(classTemplatePersistenceManager.persist(classTemplate))
                    .willReturn(classTemplateId);

            // when & then
            assertDoesNotThrow(() -> sut.execute(command));

            then(classTemplateCommandFactory).should().createUpdateContext(command);
            then(classTemplateValidator).should().findExistingOrThrow(classTemplateId);
            then(classTemplateValidator)
                    .should()
                    .validateNotDuplicateExcluding(structureId, templateCode, classTemplateId);
            then(classTemplate).should().update(updateData, changedAt);
            then(classTemplatePersistenceManager).should().persist(classTemplate);
        }

        @Test
        @DisplayName("실패 - 템플릿 코드가 중복되는 경우")
        void execute_WhenTemplateCodeDuplicate_ShouldThrowException() {
            // given
            UpdateClassTemplateCommand command = createDefaultCommand();
            ClassTemplateId classTemplateId = ClassTemplateId.of(command.classTemplateId());
            PackageStructureId structureId = PackageStructureId.of(1L);
            TemplateCode templateCode = TemplateCode.of(command.templateCode());
            Instant changedAt = Instant.now();
            UpdateContext<ClassTemplateId, ClassTemplateUpdateData> context =
                    new UpdateContext<>(classTemplateId, updateData, changedAt);

            given(classTemplateCommandFactory.createUpdateContext(command)).willReturn(context);
            given(classTemplateValidator.findExistingOrThrow(classTemplateId))
                    .willReturn(classTemplate);
            given(classTemplate.structureId()).willReturn(structureId);
            given(updateData.templateCode()).willReturn(templateCode);
            willThrow(new ClassTemplateDuplicateCodeException(structureId, templateCode))
                    .given(classTemplateValidator)
                    .validateNotDuplicateExcluding(
                            any(PackageStructureId.class),
                            any(TemplateCode.class),
                            any(ClassTemplateId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ClassTemplateDuplicateCodeException.class);

            then(classTemplatePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private UpdateClassTemplateCommand createDefaultCommand() {
        return new UpdateClassTemplateCommand(
                1L,
                1L, // classTypeId (AGGREGATE)
                "AGG-TPL-001",
                "{Name}",
                "Aggregate Root 템플릿",
                List.of(),
                List.of("@Data", "@Builder"),
                List.of(),
                List.of(),
                List.of("validate"));
    }
}
