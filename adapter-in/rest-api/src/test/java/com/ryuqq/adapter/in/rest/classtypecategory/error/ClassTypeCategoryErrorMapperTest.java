package com.ryuqq.adapter.in.rest.classtypecategory.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryDuplicateCodeException;
import com.ryuqq.domain.classtypecategory.exception.ClassTypeCategoryNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ClassTypeCategoryErrorMapper 단위 테스트
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
@DisplayName("ClassTypeCategoryErrorMapper 단위 테스트")
class ClassTypeCategoryErrorMapperTest {

    private ClassTypeCategoryErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClassTypeCategoryErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("ClassTypeCategoryNotFoundException은 지원함")
        void classTypeCategoryNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ClassTypeCategoryNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ClassTypeCategoryDuplicateCodeException은 지원함")
        void classTypeCategoryDuplicateCodeException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ClassTypeCategoryDuplicateCodeException("DOMAIN", 1L);

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
        @DisplayName("ClassTypeCategoryNotFoundException을 404로 변환")
        void classTypeCategoryNotFoundException_ShouldMapTo404() {
            // Given
            ClassTypeCategoryNotFoundException ex = new ClassTypeCategoryNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("ClassTypeCategory Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/class-type-category/not-found"));
        }

        @Test
        @DisplayName("ClassTypeCategoryDuplicateCodeException을 409로 변환")
        void classTypeCategoryDuplicateCodeException_ShouldMapTo409() {
            // Given
            ClassTypeCategoryDuplicateCodeException ex =
                    new ClassTypeCategoryDuplicateCodeException("DOMAIN", 1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("ClassTypeCategory Code Already Exists");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/class-type-category/duplicate-code"));
        }
    }
}
