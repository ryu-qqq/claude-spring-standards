package com.ryuqq.application.checklistitem.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.checklistitem.port.out.ChecklistItemQueryPort;
import com.ryuqq.domain.checklistitem.aggregate.ChecklistItem;
import com.ryuqq.domain.checklistitem.exception.ChecklistItemNotFoundException;
import com.ryuqq.domain.checklistitem.id.ChecklistItemId;
import com.ryuqq.domain.checklistitem.query.ChecklistItemSliceCriteria;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ChecklistItemReadManager 단위 테스트
 *
 * <p>ChecklistItem 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ChecklistItemReadManager 단위 테스트")
class ChecklistItemReadManagerTest {

    @Mock private ChecklistItemQueryPort checklistItemQueryPort;

    @Mock private ChecklistItem checklistItem;

    @Mock private ChecklistItemSliceCriteria criteria;

    private ChecklistItemReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ChecklistItemReadManager(checklistItemQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 ChecklistItem 조회")
        void getById_WithValidId_ShouldReturnChecklistItem() {
            // given
            ChecklistItemId id = ChecklistItemId.of(1L);
            given(checklistItemQueryPort.findById(id)).willReturn(Optional.of(checklistItem));

            // when
            ChecklistItem result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(checklistItem);
            then(checklistItemQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            ChecklistItemId id = ChecklistItemId.of(999L);
            given(checklistItemQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ChecklistItemNotFoundException.class);
            then(checklistItemQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ChecklistItem 조회")
        void findById_WithValidId_ShouldReturnChecklistItem() {
            // given
            ChecklistItemId id = ChecklistItemId.of(1L);
            given(checklistItemQueryPort.findById(id)).willReturn(Optional.of(checklistItem));

            // when
            ChecklistItem result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(checklistItem);
            then(checklistItemQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 null 반환")
        void findById_WithNonExistentId_ShouldReturnNull() {
            // given
            ChecklistItemId id = ChecklistItemId.of(999L);
            given(checklistItemQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            ChecklistItem result = sut.findById(id);

            // then
            assertThat(result).isNull();
            then(checklistItemQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<ChecklistItem> items = List.of(checklistItem);
            given(checklistItemQueryPort.findBySliceCriteria(criteria)).willReturn(items);

            // when
            List<ChecklistItem> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(checklistItem);
            then(checklistItemQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findByRuleId 메서드")
    class FindByRuleId {

        @Test
        @DisplayName("성공 - 코딩 규칙 ID로 체크리스트 목록 조회")
        void findByRuleId_WithRuleId_ShouldReturnList() {
            // given
            CodingRuleId ruleId = CodingRuleId.of(1L);
            List<ChecklistItem> items = List.of(checklistItem);
            given(checklistItemQueryPort.findByRuleId(ruleId)).willReturn(items);

            // when
            List<ChecklistItem> result = sut.findByRuleId(ruleId);

            // then
            assertThat(result).hasSize(1).containsExactly(checklistItem);
            then(checklistItemQueryPort).should().findByRuleId(ruleId);
        }
    }

    @Nested
    @DisplayName("existsByRuleIdAndSequenceOrder 메서드")
    class ExistsByRuleIdAndSequenceOrder {

        @Test
        @DisplayName("성공 - 규칙 ID와 순서로 존재 확인")
        void existsByRuleIdAndSequenceOrder_WhenExists_ShouldReturnTrue() {
            // given
            CodingRuleId ruleId = CodingRuleId.of(1L);
            int sequenceOrder = 1;
            given(
                            checklistItemQueryPort.existsByRuleIdAndSequenceOrder(
                                    ruleId.value(), sequenceOrder))
                    .willReturn(true);

            // when
            boolean result = sut.existsByRuleIdAndSequenceOrder(ruleId, sequenceOrder);

            // then
            assertThat(result).isTrue();
            then(checklistItemQueryPort)
                    .should()
                    .existsByRuleIdAndSequenceOrder(ruleId.value(), sequenceOrder);
        }
    }
}
