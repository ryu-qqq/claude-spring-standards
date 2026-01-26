package com.ryuqq.adapter.in.rest.techstack.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.techstack.exception.TechStackDuplicateNameException;
import com.ryuqq.domain.techstack.exception.TechStackHasChildrenException;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TechStackErrorMapper 단위 테스트
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
@DisplayName("TechStackErrorMapper 단위 테스트")
class TechStackErrorMapperTest {

    private TechStackErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TechStackErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("TechStackNotFoundException은 지원함")
        void techStackNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new TechStackNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("TechStackDuplicateNameException은 지원함")
        void techStackDuplicateNameException_ShouldReturnTrue() {
            // Given
            DomainException ex = new TechStackDuplicateNameException("tech-stack-name");

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("TechStackHasChildrenException은 지원함")
        void techStackHasChildrenException_ShouldReturnTrue() {
            // Given
            DomainException ex = new TechStackHasChildrenException(1L);

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
        @DisplayName("TechStackNotFoundException을 404로 변환")
        void techStackNotFoundException_ShouldMapTo404() {
            // Given
            TechStackNotFoundException ex = new TechStackNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("TechStack Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/tech-stack/not-found"));
        }

        @Test
        @DisplayName("TechStackDuplicateNameException을 409로 변환")
        void techStackDuplicateNameException_ShouldMapTo409() {
            // Given
            TechStackDuplicateNameException ex =
                    new TechStackDuplicateNameException("tech-stack-name");
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("TechStack Duplicate Name");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/tech-stack/duplicate-name"));
        }

        @Test
        @DisplayName("TechStackHasChildrenException을 409로 변환")
        void techStackHasChildrenException_ShouldMapTo409() {
            // Given
            TechStackHasChildrenException ex = new TechStackHasChildrenException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("TechStack Has Children");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/tech-stack/has-children"));
        }
    }
}
