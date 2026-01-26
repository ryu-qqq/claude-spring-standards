package com.ryuqq.adapter.in.rest.packagestructure.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagestructure.exception.PackageStructureDuplicateException;
import com.ryuqq.domain.packagestructure.exception.PackageStructureNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackageStructureErrorMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>supports() 메서드 - 예외 타입 확인
 *   <li>map() 메서드 - 예외를 HTTP 응답으로 변환
 *   <li>HTTP Status Code 매핑
 *   <li>Error Type URI 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("PackageStructureErrorMapper 단위 테스트")
class PackageStructureErrorMapperTest {

    private PackageStructureErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackageStructureErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("PackageStructureNotFoundException은 지원함")
        void packageStructureNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new PackageStructureNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("PackageStructureDuplicateException은 지원함")
        void packageStructureDuplicateException_ShouldReturnTrue() {
            // Given
            DomainException ex = new PackageStructureDuplicateException(1L, "com.example");

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 예외는 지원하지 않음")
        void otherException_ShouldReturnFalse() {
            // Given
            DomainException ex =
                    new DomainException(ArchitectureErrorCode.ARCHITECTURE_NOT_FOUND) {};

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("map(DomainException, Locale)")
    class Map {

        @Test
        @DisplayName("PackageStructureNotFoundException을 404로 변환")
        void packageStructureNotFoundException_ShouldMapTo404() {
            // Given
            PackageStructureNotFoundException ex = new PackageStructureNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("PackageStructure Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/package-structure/not-found"));
        }

        @Test
        @DisplayName("PackageStructureDuplicateException을 409로 변환")
        void packageStructureDuplicateException_ShouldMapTo409() {
            // Given
            PackageStructureDuplicateException ex =
                    new PackageStructureDuplicateException(1L, "com.example");
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("PackageStructure Duplicate");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/package-structure/duplicate"));
        }

        @Test
        @DisplayName("알 수 없는 예외는 400으로 변환")
        void unknownException_ShouldMapTo400() {
            // Given
            DomainException ex =
                    new DomainException(ArchitectureErrorCode.ARCHITECTURE_NOT_FOUND) {};
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);
            assertThat(mappedError.title()).isEqualTo("PackageStructure Error");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/package-structure"));
        }
    }
}
