package com.ryuqq.application.zerotolerance.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleQueryPort;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.exception.ZeroToleranceRuleNotFoundException;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
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
 * ZeroToleranceRuleReadManager 단위 테스트
 *
 * <p>ZeroToleranceRule 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("ZeroToleranceRuleReadManager 단위 테스트")
class ZeroToleranceRuleReadManagerTest {

    @Mock private ZeroToleranceRuleQueryPort zeroToleranceRuleQueryPort;

    @Mock private ZeroToleranceRule zeroToleranceRule;

    @Mock private ZeroToleranceRuleSliceCriteria criteria;

    @Mock private ZeroToleranceRuleSliceResult sliceResult;

    private ZeroToleranceRuleReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new ZeroToleranceRuleReadManager(zeroToleranceRuleQueryPort);
    }

    @Nested
    @DisplayName("getById 메서드")
    class GetById {

        @Test
        @DisplayName("성공 - ID로 ZeroToleranceRule 조회")
        void getById_WithValidId_ShouldReturnZeroToleranceRule() {
            // given
            ZeroToleranceRuleId id = ZeroToleranceRuleId.of(1L);
            given(zeroToleranceRuleQueryPort.findById(id))
                    .willReturn(Optional.of(zeroToleranceRule));

            // when
            ZeroToleranceRule result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(zeroToleranceRule);
            then(zeroToleranceRuleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 예외 발생")
        void getById_WithNonExistentId_ShouldThrowException() {
            // given
            ZeroToleranceRuleId id = ZeroToleranceRuleId.of(999L);
            given(zeroToleranceRuleQueryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ZeroToleranceRuleNotFoundException.class);
            then(zeroToleranceRuleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 ZeroToleranceRule 조회")
        void findById_WithValidId_ShouldReturnOptional() {
            // given
            ZeroToleranceRuleId id = ZeroToleranceRuleId.of(1L);
            given(zeroToleranceRuleQueryPort.findById(id))
                    .willReturn(Optional.of(zeroToleranceRule));

            // when
            Optional<ZeroToleranceRule> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(zeroToleranceRule);
            then(zeroToleranceRuleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            ZeroToleranceRuleId id = ZeroToleranceRuleId.of(999L);
            given(zeroToleranceRuleQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<ZeroToleranceRule> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(zeroToleranceRuleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsByRuleId 메서드")
    class ExistsByRuleId {

        @Test
        @DisplayName("성공 - 코딩 규칙 ID로 존재 확인")
        void existsByRuleId_WhenExists_ShouldReturnTrue() {
            // given
            Long ruleId = 1L;
            given(zeroToleranceRuleQueryPort.existsByRuleId(ruleId)).willReturn(true);

            // when
            boolean result = sut.existsByRuleId(ruleId);

            // then
            assertThat(result).isTrue();
            then(zeroToleranceRuleQueryPort).should().existsByRuleId(ruleId);
        }
    }

    @Nested
    @DisplayName("findAllDetails 메서드")
    class FindAllDetails {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 상세 목록 조회")
        void findAllDetails_WithCriteria_ShouldReturnSliceResult() {
            // given
            given(zeroToleranceRuleQueryPort.findAllDetails(criteria)).willReturn(sliceResult);

            // when
            ZeroToleranceRuleSliceResult result = sut.findAllDetails(criteria);

            // then
            assertThat(result).isEqualTo(sliceResult);
            then(zeroToleranceRuleQueryPort).should().findAllDetails(criteria);
        }
    }
}
