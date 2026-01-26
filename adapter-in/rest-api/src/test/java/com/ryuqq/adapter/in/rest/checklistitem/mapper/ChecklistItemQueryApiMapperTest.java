package com.ryuqq.adapter.in.rest.checklistitem.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.checklistitem.dto.request.SearchChecklistItemsCursorApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchChecklistItemsCursorApiRequestFixture;
import com.ryuqq.application.checklistitem.dto.query.ChecklistItemSearchParams;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ChecklistItemQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO → Query DTO 변환
 *   <li>Result DTO → Response DTO 변환
 *   <li>SliceResult → SliceApiResponse 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("ChecklistItemQueryApiMapper 단위 테스트")
class ChecklistItemQueryApiMapperTest {

    private ChecklistItemQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ChecklistItemQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchChecklistItemsCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchChecklistItemsCursorApiRequest request =
                    SearchChecklistItemsCursorApiRequestFixture.valid();

            // When
            ChecklistItemSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toResponse(ChecklistItemResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new ChecklistItemResult(
                            1L,
                            1L,
                            1,
                            "Lombok 어노테이션 사용 여부 확인",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "AGG-001-CHECK-1",
                            true,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            ChecklistItemApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.ruleId()).isEqualTo(1L);
            assertThat(response.sequenceOrder()).isEqualTo(1);
            assertThat(response.checkDescription()).isEqualTo("Lombok 어노테이션 사용 여부 확인");
            assertThat(response.checkType()).isEqualTo("AUTOMATED");
            assertThat(response.automationTool()).isEqualTo("ARCHUNIT");
            assertThat(response.automationRuleId()).isEqualTo("AGG-001-CHECK-1");
            assertThat(response.critical()).isTrue();
            assertThat(response.source()).isEqualTo("MANUAL");
            assertThat(response.feedbackId()).isNull();
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses(List<ChecklistItemResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new ChecklistItemResult(
                            1L,
                            1L,
                            1,
                            "Description 1",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "RULE-1",
                            true,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var result2 =
                    new ChecklistItemResult(
                            2L,
                            1L,
                            2,
                            "Description 2",
                            "MANUAL",
                            null,
                            null,
                            false,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));

            // When
            List<ChecklistItemApiResponse> responses =
                    mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toSliceResponse(ChecklistItemSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new ChecklistItemResult(
                            1L,
                            1L,
                            1,
                            "Description",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "RULE-1",
                            true,
                            "MANUAL",
                            null,
                            Instant.parse("2024-01-01T09:00:00+09:00"),
                            Instant.parse("2024-01-01T09:00:00+09:00"));
            var sliceResult = ChecklistItemSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<ChecklistItemApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }
    }
}
