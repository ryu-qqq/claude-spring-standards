package com.ryuqq.adapter.in.rest.classtemplate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.classtemplate.dto.request.CreateClassTemplateApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.request.UpdateClassTemplateApiRequest;
import com.ryuqq.adapter.in.rest.fixture.request.CreateClassTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateClassTemplateApiRequestFixture;
import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ClassTemplateCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>null 처리 (리스트 필드)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ClassTemplateCommandApiMapper 단위 테스트")
class ClassTemplateCommandApiMapperTest {

    private ClassTemplateCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClassTemplateCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateClassTemplateApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateClassTemplateApiRequest request = CreateClassTemplateApiRequestFixture.valid();

            // When
            CreateClassTemplateCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.structureId()).isEqualTo(1L);
            assertThat(command.classTypeId()).isEqualTo(1L);
            assertThat(command.templateCode()).isEqualTo("public class {ClassName} { ... }");
            assertThat(command.namingPattern()).isEqualTo(".*Aggregate");
            assertThat(command.description()).isEqualTo("Aggregate Root 클래스 템플릿");
            assertThat(command.requiredAnnotations()).containsExactly("@Entity");
            assertThat(command.forbiddenAnnotations()).containsExactly("@Data");
        }

        @Test
        @DisplayName("null 필드 처리 - 리스트 필드가 null이면 빈 리스트")
        void nullListFields_ShouldReturnEmptyList() {
            // Given
            CreateClassTemplateApiRequest request =
                    CreateClassTemplateApiRequestFixture.validMinimal();

            // When
            CreateClassTemplateCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.requiredAnnotations()).isEmpty();
            assertThat(command.forbiddenAnnotations()).isEmpty();
            assertThat(command.requiredInterfaces()).isEmpty();
            assertThat(command.forbiddenInheritance()).isEmpty();
            assertThat(command.requiredMethods()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateClassTemplateApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long classTemplateId = 1L;
            UpdateClassTemplateApiRequest request = UpdateClassTemplateApiRequestFixture.valid();

            // When
            UpdateClassTemplateCommand command = mapper.toCommand(classTemplateId, request);

            // Then
            assertThat(command.classTemplateId()).isEqualTo(classTemplateId);
            assertThat(command.classTypeId()).isNotNull();
        }
    }
}
