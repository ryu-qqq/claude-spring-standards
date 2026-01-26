package com.ryuqq.adapter.in.rest.architecture.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ArchitectureErrorMapper 단위 테스트
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
@DisplayName("ArchitectureErrorMapper 단위 테스트")
class ArchitectureErrorMapperTest {

    private ArchitectureErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArchitectureErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("ArchitectureNotFoundException은 지원함")
        void architectureNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new ArchitectureNotFoundException(1L);

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
        @DisplayName("ArchitectureNotFoundException을 404로 변환")
        void architectureNotFoundException_ShouldMapTo404() {
            // Given
            ArchitectureNotFoundException ex = new ArchitectureNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status())
                    .isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("Architecture Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/architecture/not-found"));
        }
    }
}
