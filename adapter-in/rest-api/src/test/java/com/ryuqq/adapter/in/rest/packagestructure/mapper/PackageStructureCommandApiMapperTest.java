package com.ryuqq.adapter.in.rest.packagestructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreatePackageStructureApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdatePackageStructureApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagestructure.dto.request.CreatePackageStructureApiRequest;
import com.ryuqq.adapter.in.rest.packagestructure.dto.request.UpdatePackageStructureApiRequest;
import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackageStructureCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>null 처리 (allowedClassTypes)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("PackageStructureCommandApiMapper 단위 테스트")
class PackageStructureCommandApiMapperTest {

    private PackageStructureCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackageStructureCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreatePackageStructureApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreatePackageStructureApiRequest request =
                    CreatePackageStructureApiRequestFixture.valid();

            // When
            CreatePackageStructureCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.moduleId()).isEqualTo(1L);
            assertThat(command.pathPattern()).isEqualTo("{base}.domain.{bc}.aggregate");
            assertThat(command.allowedClassTypes()).containsExactly("CLASS", "RECORD");
            assertThat(command.namingPattern()).isEqualTo(".*Aggregate");
            assertThat(command.namingSuffix()).isEqualTo("Aggregate");
            assertThat(command.description()).isEqualTo("Aggregate Root 패키지");
        }

        @Test
        @DisplayName("null 필드 처리 - allowedClassTypes가 null이면 빈 리스트")
        void nullAllowedClassTypes_ShouldReturnEmptyList() {
            // Given
            CreatePackageStructureApiRequest request =
                    CreatePackageStructureApiRequestFixture.validMinimal();

            // When
            CreatePackageStructureCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.allowedClassTypes()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdatePackageStructureApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long packageStructureId = 1L;
            UpdatePackageStructureApiRequest request =
                    UpdatePackageStructureApiRequestFixture.valid();

            // When
            UpdatePackageStructureCommand command = mapper.toCommand(packageStructureId, request);

            // Then
            assertThat(command.packageStructureId()).isEqualTo(packageStructureId);
            assertThat(command.pathPattern()).isEqualTo("{base}.domain.{bc}.aggregate");
            assertThat(command.allowedClassTypes()).containsExactly("CLASS", "RECORD");
        }
    }
}
