package com.ryuqq.adapter.in.rest.packagepurpose.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.fixture.request.CreatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.CreatePackagePurposeApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.UpdatePackagePurposeApiRequest;
import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackagePurposeCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Command DTO 변환
 *   <li>null 처리 (defaultAllowedClassTypes)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("PackagePurposeCommandApiMapper 단위 테스트")
class PackagePurposeCommandApiMapperTest {

    private PackagePurposeCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackagePurposeCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreatePackagePurposeApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreatePackagePurposeApiRequest request = CreatePackagePurposeApiRequestFixture.valid();

            // When
            CreatePackagePurposeCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.structureId()).isEqualTo(1L);
            assertThat(command.code()).isEqualTo("AGGREGATE");
            assertThat(command.name()).isEqualTo("Aggregate Root");
            assertThat(command.description()).isEqualTo("DDD Aggregate Root 패키지");
            assertThat(command.defaultAllowedClassTypes()).containsExactly("CLASS", "RECORD");
            assertThat(command.defaultNamingPattern()).isEqualTo("^[A-Z][a-zA-Z0-9]*$");
            assertThat(command.defaultNamingSuffix()).isEqualTo("Aggregate");
        }

        @Test
        @DisplayName("null 필드 처리 - defaultAllowedClassTypes가 null이면 빈 리스트")
        void nullAllowedClassTypes_ShouldReturnEmptyList() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.validMinimal();

            // When
            CreatePackagePurposeCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.defaultAllowedClassTypes()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdatePackagePurposeApiRequest)")
    class ToUpdateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            Long packagePurposeId = 1L;
            UpdatePackagePurposeApiRequest request = UpdatePackagePurposeApiRequestFixture.valid();

            // When
            UpdatePackagePurposeCommand command = mapper.toCommand(packagePurposeId, request);

            // Then
            assertThat(command.packagePurposeId()).isEqualTo(packagePurposeId);
            assertThat(command.code()).isEqualTo("AGGREGATE");
            assertThat(command.name()).isEqualTo("Aggregate Root");
            assertThat(command.description()).isEqualTo("DDD Aggregate Root 패키지");
            assertThat(command.defaultAllowedClassTypes()).containsExactly("CLASS", "RECORD");
            assertThat(command.defaultNamingPattern()).isEqualTo("^[A-Z][a-zA-Z0-9]*$");
            assertThat(command.defaultNamingSuffix()).isEqualTo("Aggregate");
        }

        @Test
        @DisplayName("null 필드 처리 - defaultAllowedClassTypes가 null이면 빈 리스트")
        void nullAllowedClassTypes_ShouldReturnEmptyList() {
            // Given
            Long packagePurposeId = 1L;
            UpdatePackagePurposeApiRequest request =
                    UpdatePackagePurposeApiRequestFixture.validMinimal();

            // When
            UpdatePackagePurposeCommand command = mapper.toCommand(packagePurposeId, request);

            // Then
            assertThat(command.defaultAllowedClassTypes()).isEmpty();
        }
    }
}
