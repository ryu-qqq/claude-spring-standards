package com.ryuqq.application.checklistitem.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.checklistitem.dto.response.ChecklistItemResult;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.fixture.ChecklistItemFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ChecklistItemAssembler 단위 테스트
 *
 * <p>ChecklistItem 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("ChecklistItemAssembler 단위 테스트")
class ChecklistItemAssemblerTest {

    private ChecklistItemAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ChecklistItemAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - ChecklistItem을 ChecklistItemResult로 변환")
        void toResult_WithValidChecklistItem_ShouldReturnResult() {
            // given
            ChecklistItem checklistItem = ChecklistItemFixture.reconstitute();

            // when
            ChecklistItemResult result = sut.toResult(checklistItem);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - ChecklistItem 목록을 ChecklistItemResult 목록으로 변환")
        void toResults_WithValidChecklistItems_ShouldReturnResults() {
            // given
            ChecklistItem checklistItem1 = ChecklistItemFixture.reconstitute();
            ChecklistItem checklistItem2 = ChecklistItemFixture.reconstitute();
            List<ChecklistItem> checklistItems = List.of(checklistItem1, checklistItem2);

            // when
            List<ChecklistItemResult> results = sut.toResults(checklistItems);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<ChecklistItem> checklistItems = List.of();

            // when
            List<ChecklistItemResult> results = sut.toResults(checklistItems);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            ChecklistItem checklistItem1 = ChecklistItemFixture.reconstitute();
            ChecklistItem checklistItem2 = ChecklistItemFixture.reconstitute();
            ChecklistItem checklistItem3 = ChecklistItemFixture.reconstitute();
            List<ChecklistItem> checklistItems =
                    List.of(checklistItem1, checklistItem2, checklistItem3);
            int requestedSize = 2;

            // when
            ChecklistItemSliceResult result = sut.toSliceResult(checklistItems, requestedSize);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.checklistItems()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            ChecklistItem checklistItem1 = ChecklistItemFixture.reconstitute();
            List<ChecklistItem> checklistItems = List.of(checklistItem1);
            int requestedSize = 10;

            // when
            ChecklistItemSliceResult result = sut.toSliceResult(checklistItems, requestedSize);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.checklistItems()).hasSize(1);
        }
    }
}
