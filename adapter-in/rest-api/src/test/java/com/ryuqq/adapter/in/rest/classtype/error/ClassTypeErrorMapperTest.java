package com.ryuqq.adapter.in.rest.classtype.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.classtype.exception.ClassTypeDuplicateCodeException;
import com.ryuqq.domain.classtype.exception.ClassTypeNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ClassTypeErrorMapper 단위 테스트
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
@DisplayName("ClassTypeErrorMapper 단위 테스트")
class ClassTypeErrorMapperTest {

    private ClassTypeErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClassTypeErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("ClassTypeNotFoundException은 지원함")
        void classTypeNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ClassTypeNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ClassTypeDuplicateCodeException은 지원함")
        void classTypeDuplicateCodeException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ClassTypeDuplicateCodeException("AGGREGATE", 1L);

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
        @DisplayName("ClassTypeNotFoundException을 404로 변환")
        void classTypeNotFoundException_ShouldMapTo404() {
            // Given
            ClassTypeNotFoundException ex = new ClassTypeNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("ClassType Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/class-type/not-found"));
        }

        @Test
        @DisplayName("ClassTypeDuplicateCodeException을 409로 변환")
        void classTypeDuplicateCodeException_ShouldMapTo409() {
            // Given
            ClassTypeDuplicateCodeException ex =
                    new ClassTypeDuplicateCodeException("AGGREGATE", 1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("ClassType Code Already Exists");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/class-type/duplicate-code"));
        }
    }
}
