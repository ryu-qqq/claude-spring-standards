package com.ryuqq.application.classtemplate.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.fixture.CreateClassTemplateCommandFixture;
import com.ryuqq.application.classtemplate.fixture.UpdateClassTemplateCommandFixture;
import com.ryuqq.application.common.dto.command.UpdateContext;
import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplateUpdateData;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
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
 * ClassTemplateCommandFactory 단위 테스트
 *
 * <p>Command DTO를 Domain 객체로 변환하는 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("ClassTemplateCommandFactory 단위 테스트")
class ClassTemplateCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ClassTemplateCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2024-01-15T10:00:00Z");

    @BeforeEach
    void setUp() {
        sut = new ClassTemplateCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공 - CreateClassTemplateCommand로 ClassTemplate 생성")
        void create_WithValidCommand_ShouldReturnClassTemplate() {
            // given
            CreateClassTemplateCommand command = CreateClassTemplateCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            ClassTemplate result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.structureId().value()).isEqualTo(command.structureId());
            assertThat(result.classTypeIdValue()).isEqualTo(command.classTypeId());
            assertThat(result.templateCode().value()).isEqualTo(command.templateCode());
            assertThat(result.namingPattern().value()).isEqualTo(command.namingPattern());
        }
    }

    @Nested
    @DisplayName("toUpdateData 메서드")
    class ToUpdateData {

        @Test
        @DisplayName("성공 - UpdateClassTemplateCommand로 ClassTemplateUpdateData 생성")
        void toUpdateData_WithValidCommand_ShouldReturnUpdateData() {
            // given
            UpdateClassTemplateCommand command = UpdateClassTemplateCommandFixture.defaultCommand();

            // when
            ClassTemplateUpdateData result = sut.toUpdateData(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.namingPattern().value()).isEqualTo(command.namingPattern());
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공 - UpdateClassTemplateCommand로 UpdateContext 생성")
        void createUpdateContext_WithValidCommand_ShouldReturnUpdateContext() {
            // given
            UpdateClassTemplateCommand command = UpdateClassTemplateCommandFixture.defaultCommand();
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<ClassTemplateId, ClassTemplateUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.classTemplateId());
            assertThat(result.updateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
