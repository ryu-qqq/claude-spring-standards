package com.ryuqq.adapter.in.rest.archunittest.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestDuplicateCodeException;
import com.ryuqq.domain.archunittest.exception.ArchUnitTestNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ArchUnitTestErrorMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>supports() 메서드 - 예외 타입 확인
 *   <li>map() 메서드 - 예외를 HTTP 응답으로 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ArchUnitTestErrorMapper 단위 테스트")
class ArchUnitTestErrorMapperTest {

    private ArchUnitTestErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchUnitTestErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("ArchUnitTestNotFoundException은 지원함")
        void archUnitTestNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ArchUnitTestNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ArchUnitTestDuplicateCodeException은 지원함")
        void archUnitTestDuplicateCodeException_ShouldReturnTrue() {
            // Given
            DomainException ex =
                    new ArchUnitTestDuplicateCodeException(PackageStructureId.of(1L), "ARCH-001");

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
        @DisplayName("ArchUnitTestNotFoundException을 404로 변환")
        void archUnitTestNotFoundException_ShouldMapTo404() {
            // Given
            ArchUnitTestNotFoundException ex = new ArchUnitTestNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("ArchUnitTest Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/arch-unit-test/not-found"));
        }

        @Test
        @DisplayName("ArchUnitTestDuplicateCodeException을 409로 변환")
        void archUnitTestDuplicateCodeException_ShouldMapTo409() {
            // Given
            ArchUnitTestDuplicateCodeException ex =
                    new ArchUnitTestDuplicateCodeException(PackageStructureId.of(1L), "ARCH-001");
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("ArchUnitTest Code Already Exists");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/arch-unit-test/duplicate-code"));
        }
    }
}
