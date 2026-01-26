package com.ryuqq.adapter.in.rest.packagepurpose.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeDuplicateCodeException;
import com.ryuqq.domain.packagepurpose.exception.PackagePurposeNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PackagePurposeErrorMapper 단위 테스트
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
@DisplayName("PackagePurposeErrorMapper 단위 테스트")
class PackagePurposeErrorMapperTest {

    private PackagePurposeErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PackagePurposeErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("PackagePurposeNotFoundException은 지원함")
        void packagePurposeNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new PackagePurposeNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("PackagePurposeDuplicateCodeException은 지원함")
        void packagePurposeDuplicateCodeException_ShouldReturnTrue() {
            // Given
            DomainException ex = new PackagePurposeDuplicateCodeException(1L, "AGGREGATE");

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
        @DisplayName("PackagePurposeNotFoundException을 404로 변환")
        void packagePurposeNotFoundException_ShouldMapTo404() {
            // Given
            PackagePurposeNotFoundException ex = new PackagePurposeNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("PackagePurpose Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/package-purpose/not-found"));
        }

        @Test
        @DisplayName("PackagePurposeDuplicateCodeException을 409로 변환")
        void packagePurposeDuplicateCodeException_ShouldMapTo409() {
            // Given
            PackagePurposeDuplicateCodeException ex =
                    new PackagePurposeDuplicateCodeException(1L, "AGGREGATE");
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("PackagePurpose Duplicate Code");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/package-purpose/duplicate-code"));
        }
    }
}
