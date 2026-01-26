package com.ryuqq.adapter.in.rest.layer.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.layer.exception.LayerDuplicateCodeException;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * LayerErrorMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("LayerErrorMapper 단위 테스트")
class LayerErrorMapperTest {

    private LayerErrorMapper mapper;
    private Locale locale;

    @BeforeEach
    void setUp() {
        mapper = new LayerErrorMapper();
        locale = Locale.KOREA;
    }

    @Nested
    @DisplayName("supports()")
    class Supports {

        @Test
        @DisplayName("성공 - LayerNotFoundException 지원")
        void shouldSupportLayerNotFoundException() {
            // Given
            LayerNotFoundException ex = new LayerNotFoundException(1L);

            // When & Then
            assertThat(mapper.supports(ex)).isTrue();
        }

        @Test
        @DisplayName("성공 - LayerDuplicateCodeException 지원")
        void shouldSupportLayerDuplicateCodeException() {
            // Given
            LayerDuplicateCodeException ex = new LayerDuplicateCodeException("DOMAIN", 1L);

            // When & Then
            assertThat(mapper.supports(ex)).isTrue();
        }
    }

    @Nested
    @DisplayName("map()")
    class Map {

        @Test
        @DisplayName("성공 - LayerNotFoundException을 404로 매핑")
        void shouldMapLayerNotFoundExceptionTo404() {
            // Given
            LayerNotFoundException ex = new LayerNotFoundException(1L);

            // When
            MappedError error = mapper.map(ex, locale);

            // Then
            assertThat(error.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(error.title()).isEqualTo("Layer Not Found");
            assertThat(error.detail()).contains("1");
            assertThat(error.type().toString()).isEqualTo("/errors/layer/not-found");
        }

        @Test
        @DisplayName("성공 - LayerDuplicateCodeException을 409로 매핑")
        void shouldMapLayerDuplicateCodeExceptionTo409() {
            // Given
            LayerDuplicateCodeException ex = new LayerDuplicateCodeException("DOMAIN", 1L);

            // When
            MappedError error = mapper.map(ex, locale);

            // Then
            assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(error.title()).isEqualTo("Layer Duplicate Code");
            assertThat(error.detail()).contains("DOMAIN");
            assertThat(error.type().toString()).isEqualTo("/errors/layer/duplicate-code");
        }
    }
}
