package com.ryuqq.application.checklistitem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.checklistitem.assembler.ChecklistItemAssembler;
import com.ryuqq.application.checklistitem.dto.query.ChecklistItemSearchParams;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import com.ryuqq.application.checklistitem.factory.query.ChecklistItemQueryFactory;
import com.ryuqq.application.checklistitem.manager.ChecklistItemReadManager;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SearchChecklistItemsByCursorService 단위 테스트
 *
 * <p>ChecklistItem 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchChecklistItemsByCursorService 단위 테스트")
class SearchChecklistItemsByCursorServiceTest {

    @Mock private ChecklistItemQueryFactory checklistItemQueryFactory;

    @Mock private ChecklistItemReadManager checklistItemReadManager;

    @Mock private ChecklistItemAssembler checklistItemAssembler;

    @Mock private ChecklistItemSliceCriteria criteria;

    @Mock private ChecklistItem checklistItem;

    @Mock private ChecklistItemSliceResult sliceResult;

    private SearchChecklistItemsByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchChecklistItemsByCursorService(
                        checklistItemQueryFactory,
                        checklistItemReadManager,
                        checklistItemAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 ChecklistItem 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ChecklistItemSearchParams searchParams = createDefaultSearchParams();
            List<ChecklistItem> checklistItems = List.of(checklistItem);

            given(checklistItemQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(checklistItemReadManager.findBySliceCriteria(criteria))
                    .willReturn(checklistItems);
            given(checklistItemAssembler.toSliceResult(checklistItems, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ChecklistItemSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(checklistItemQueryFactory).should().createSliceCriteria(searchParams);
            then(checklistItemReadManager).should().findBySliceCriteria(criteria);
            then(checklistItemAssembler)
                    .should()
                    .toSliceResult(checklistItems, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            ChecklistItemSearchParams searchParams = createDefaultSearchParams();
            List<ChecklistItem> emptyList = List.of();

            given(checklistItemQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(checklistItemReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(checklistItemAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ChecklistItemSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(checklistItemQueryFactory).should().createSliceCriteria(searchParams);
            then(checklistItemReadManager).should().findBySliceCriteria(criteria);
            then(checklistItemAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private ChecklistItemSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ChecklistItemSearchParams.of(cursorParams);
    }
}
