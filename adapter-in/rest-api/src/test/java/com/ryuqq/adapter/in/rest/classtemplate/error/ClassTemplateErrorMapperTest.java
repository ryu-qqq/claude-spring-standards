package com.ryuqq.adapter.in.rest.classtemplate.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateDuplicateCodeException;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateNotFoundException;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ClassTemplateErrorMapper 단위 테스트
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
@DisplayName("ClassTemplateErrorMapper 단위 테스트")
class ClassTemplateErrorMapperTest {

    private ClassTemplateErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClassTemplateErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("ClassTemplateNotFoundException은 지원함")
        void classTemplateNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ClassTemplateNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ClassTemplateDuplicateCodeException은 지원함")
        void classTemplateDuplicateCodeException_ShouldReturnTrue() {
            // Given
            DomainException ex =
                    new ClassTemplateDuplicateCodeException(
                            PackageStructureId.of(1L), TemplateCode.of("AGGREGATE"));

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
        @DisplayName("ClassTemplateNotFoundException을 404로 변환")
        void classTemplateNotFoundException_ShouldMapTo404() {
            // Given
            ClassTemplateNotFoundException ex = new ClassTemplateNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("ClassTemplate Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/class-template/not-found"));
        }

        @Test
        @DisplayName("ClassTemplateDuplicateCodeException을 409로 변환")
        void classTemplateDuplicateCodeException_ShouldMapTo409() {
            // Given
            ClassTemplateDuplicateCodeException ex =
                    new ClassTemplateDuplicateCodeException(
                            PackageStructureId.of(1L), TemplateCode.of("AGGREGATE"));
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("ClassTemplate Code Already Exists");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/class-template/duplicate-code"));
        }
    }
}
