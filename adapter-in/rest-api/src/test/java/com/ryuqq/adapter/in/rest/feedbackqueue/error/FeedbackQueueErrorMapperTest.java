package com.ryuqq.adapter.in.rest.feedbackqueue.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError;
import com.ryuqq.domain.architecture.exception.ArchitectureErrorCode;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.feedbackqueue.exception.FeedbackQueueNotFoundException;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackStatusTransitionException;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackStatus;
import java.net.URI;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * FeedbackQueueErrorMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>supports() 메서드 - 예외 타입 확인
 *   <li>map() 메서드 - 예외를 HTTP 응답으로 변환
 *   <li>HTTP Status Code 매핑
 *   <li>Error Type URI 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("FeedbackQueueErrorMapper 단위 테스트")
class FeedbackQueueErrorMapperTest {

    private FeedbackQueueErrorMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FeedbackQueueErrorMapper();
    }

    @Nested
    @DisplayName("supports(DomainException)")
    class Supports {

        @Test
        @DisplayName("FeedbackQueueNotFoundException은 지원함")
        void feedbackQueueNotFoundException_ShouldReturnTrue() {
            // Given
            DomainException ex = new FeedbackQueueNotFoundException(1L);

            // When
            boolean result = mapper.supports(ex);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("InvalidFeedbackStatusTransitionException은 지원함")
        void invalidFeedbackStatusTransitionException_ShouldReturnTrue() {
            // Given
            DomainException ex =
                    new InvalidFeedbackStatusTransitionException(
                            1L, FeedbackStatus.PENDING, "llmApprove");

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
        @DisplayName("FeedbackQueueNotFoundException을 404로 변환")
        void feedbackQueueNotFoundException_ShouldMapTo404() {
            // Given
            FeedbackQueueNotFoundException ex = new FeedbackQueueNotFoundException(1L);
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(mappedError.title()).isEqualTo("FeedbackQueue Not Found");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/feedback-queue/not-found"));
        }

        @Test
        @DisplayName("InvalidFeedbackStatusTransitionException을 409로 변환")
        void invalidFeedbackStatusTransitionException_ShouldMapTo409() {
            // Given
            InvalidFeedbackStatusTransitionException ex =
                    new InvalidFeedbackStatusTransitionException(
                            1L, FeedbackStatus.PENDING, "llmApprove");
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(mappedError.title()).isEqualTo("Invalid Status Transition");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type())
                    .isEqualTo(URI.create("/errors/feedback-queue/invalid-status-transition"));
        }

        @Test
        @DisplayName("알 수 없는 예외는 400으로 변환")
        void unknownException_ShouldMapTo400() {
            // Given
            DomainException ex =
                    new DomainException(ArchitectureErrorCode.ARCHITECTURE_NOT_FOUND) {};
            Locale locale = Locale.KOREAN;

            // When
            MappedError mappedError = mapper.map(ex, locale);

            // Then
            assertThat(mappedError.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(mappedError.title()).isEqualTo("FeedbackQueue Error");
            assertThat(mappedError.detail()).isEqualTo(ex.getMessage());
            assertThat(mappedError.type()).isEqualTo(URI.create("/errors/feedback-queue"));
        }
    }
}
