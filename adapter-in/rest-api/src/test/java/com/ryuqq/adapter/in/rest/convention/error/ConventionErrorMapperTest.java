package com.ryuqq.adapter.in.rest.convention.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.convention.exception.ConventionDuplicateException;
import com.ryuqq.domain.convention.exception.ConventionNotFoundException;
import com.ryuqq.domain.module.id.ModuleId;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ConventionErrorMapper 단위 테스트
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
@DisplayName("ConventionErrorMapper 단위 테스트")
class ConventionErrorMapperTest {

    private static final Long DEFAULT_MODULE_ID = 1L;

    private ConventionErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ConventionErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("ConventionNotFoundException은 지원함")
        void conventionNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ConventionNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ConventionDuplicateException은 지원함")
        void conventionDuplicateException_ShouldReturnTrue() {
            // Given
            DomainException ex =
                    new ConventionDuplicateException(ModuleId.of(DEFAULT_MODULE_ID), "1.0.0");

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
        @DisplayName("ConventionNotFoundException을 404로 변환")
        void conventionNotFoundException_ShouldMapTo404() {
            // Given
            ConventionNotFoundException ex = new ConventionNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("Convention Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/convention/not-found"));
        }

        @Test
        @DisplayName("ConventionDuplicateException을 409로 변환")
        void conventionDuplicateException_ShouldMapTo409() {
            // Given
            ConventionDuplicateException ex =
                    new ConventionDuplicateException(ModuleId.of(DEFAULT_MODULE_ID), "1.0.0");
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("Convention Duplicate");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/convention/duplicate"));
        }
    }
}
