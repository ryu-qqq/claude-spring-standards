package com.ryuqq.application.ruleexample.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.ruleexample.port.out.RuleExampleQueryPort;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.ruleexample.aggregate.RuleExample;
import com.ryuqq.domain.ruleexample.exception.RuleExampleNotFoundException;
import com.ryuqq.domain.ruleexample.id.RuleExampleId;
import com.ryuqq.domain.ruleexample.query.RuleExampleSliceCriteria;
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
 * RuleExampleReadManager 단위 테스트
 *
 * <p>RuleExample 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("RuleExampleReadManager 단위 테스트")
class RuleExampleReadManagerTest {

    @Mock private RuleExampleQueryPort ruleExampleQueryPort;

    @Mock private RuleExample ruleExample;

    @Mock private RuleExampleSliceCriteria criteria;

    private RuleExampleReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new RuleExampleReadManager(ruleExampleQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 RuleExample 조회")
        void getById_WithValidId_ShouldReturnRuleExample() {
            // given
            RuleExampleId id = RuleExampleId.of(1L);
            given(ruleExampleQueryPort.findById(id)).willReturn(Optional.of(ruleExample));

            // when
            RuleExample result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(ruleExample);
            then(ruleExampleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            RuleExampleId id = RuleExampleId.of(999L);
            given(ruleExampleQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(RuleExampleNotFoundException.class);
            then(ruleExampleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 RuleExample 조회")
        void findById_WithValidId_ShouldReturnRuleExample() {
            // given
            RuleExampleId id = RuleExampleId.of(1L);
            given(ruleExampleQueryPort.findById(id)).willReturn(Optional.of(ruleExample));

            // when
            RuleExample result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(ruleExample);
            then(ruleExampleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 null 반환")
        void findById_WithNonExistentId_ShouldReturnNull() {
            // given
            RuleExampleId id = RuleExampleId.of(999L);
            given(ruleExampleQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            RuleExample result = sut.findById(id);

            // then
            assertThat(result).isNull();
            then(ruleExampleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<RuleExample> examples = List.of(ruleExample);
            given(ruleExampleQueryPort.findBySliceCriteria(criteria)).willReturn(examples);

            // when
            List<RuleExample> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(ruleExample);
            then(ruleExampleQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findByRuleId 메서드")
    class FindByRuleId {

        @Test
        @DisplayName("성공 - 코딩 규칙 ID로 규칙 예시 목록 조회")
        void findByRuleId_WithRuleId_ShouldReturnList() {
            // given
            CodingRuleId ruleId = CodingRuleId.of(1L);
            List<RuleExample> examples = List.of(ruleExample);
            given(ruleExampleQueryPort.findByRuleId(ruleId)).willReturn(examples);

            // when
            List<RuleExample> result = sut.findByRuleId(ruleId);

            // then
            assertThat(result).hasSize(1).containsExactly(ruleExample);
            then(ruleExampleQueryPort).should().findByRuleId(ruleId);
        }
    }
}
