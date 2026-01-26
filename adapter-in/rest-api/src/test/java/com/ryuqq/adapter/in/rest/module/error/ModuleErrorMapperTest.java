package com.ryuqq.adapter.in.rest.module.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.exception.ModuleDuplicateNameException;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import com.ryuqq.domain.module.vo.ModuleName;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ModuleErrorMapper 단위 테스트
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
@DisplayName("ModuleErrorMapper 단위 테스트")
class ModuleErrorMapperTest {

    private ModuleErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ModuleErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("ModuleNotFoundException은 지원함")
        void moduleNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ModuleNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ModuleDuplicateNameException은 지원함")
        void moduleDuplicateNameException_ShouldReturnTrue() {
            // Given
            DomainException ex =
                    new ModuleDuplicateNameException(LayerId.of(1L), ModuleName.of("module-name"));

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
        @DisplayName("ModuleNotFoundException을 404로 변환")
        void moduleNotFoundException_ShouldMapTo404() {
            // Given
            ModuleNotFoundException ex = new ModuleNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("Module Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/module/not-found"));
        }

        @Test
        @DisplayName("ModuleDuplicateNameException을 409로 변환")
        void moduleDuplicateNameException_ShouldMapTo409() {
            // Given
            ModuleDuplicateNameException ex =
                    new ModuleDuplicateNameException(LayerId.of(1L), ModuleName.of("module-name"));
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("Module Duplicate Name");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/module/duplicate-name"));
        }
    }
}
