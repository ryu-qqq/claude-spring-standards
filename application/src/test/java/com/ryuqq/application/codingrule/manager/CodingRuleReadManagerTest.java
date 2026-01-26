package com.ryuqq.application.codingrule.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.codingrule.port.out.CodingRuleQueryPort;
import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.query.CodingRuleSliceCriteria;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.convention.id.ConventionId;
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
 * CodingRuleReadManager 단위 테스트
 *
 * <p>CodingRule 조회 관리자의 QueryPort 위임 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("manager")
@Tag("application-layer")
@DisplayName("CodingRuleReadManager 단위 테스트")
class CodingRuleReadManagerTest {

    @Mock private CodingRuleQueryPort codingRuleQueryPort;

    @Mock private CodingRule codingRule;

    @Mock private CodingRuleSliceCriteria criteria;

    private CodingRuleReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new CodingRuleReadManager(codingRuleQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공 - ID로 CodingRule 조회")
        void findById_WithValidId_ShouldReturnCodingRule() {
            // given
            CodingRuleId id = CodingRuleId.of(1L);
            given(codingRuleQueryPort.findById(id)).willReturn(Optional.of(codingRule));

            // when
            Optional<CodingRule> result = sut.findById(id);

            // then
            assertThat(result).isPresent().contains(codingRule);
            then(codingRuleQueryPort).should().findById(id);
        }

        @Test
        @DisplayName("성공 - 존재하지 않는 ID 조회 시 빈 Optional 반환")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            // given
            CodingRuleId id = CodingRuleId.of(999L);
            given(codingRuleQueryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<CodingRule> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
            then(codingRuleQueryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("성공 - 존재하는 ID 확인")
        void existsById_WhenExists_ShouldReturnTrue() {
            // given
            CodingRuleId id = CodingRuleId.of(1L);
            given(codingRuleQueryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
            then(codingRuleQueryPort).should().existsById(id);
        }
    }

    @Nested
    @DisplayName("findBySliceCriteria 메서드")
    class FindBySliceCriteria {

        @Test
        @DisplayName("성공 - 슬라이스 조건으로 목록 조회")
        void findBySliceCriteria_WithCriteria_ShouldReturnList() {
            // given
            List<CodingRule> codingRules = List.of(codingRule);
            given(codingRuleQueryPort.findBySliceCriteria(criteria)).willReturn(codingRules);

            // when
            List<CodingRule> result = sut.findBySliceCriteria(criteria);

            // then
            assertThat(result).hasSize(1).containsExactly(codingRule);
            then(codingRuleQueryPort).should().findBySliceCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByConventionIdAndCode 메서드")
    class ExistsByConventionIdAndCode {

        @Test
        @DisplayName("성공 - 컨벤션 내 규칙 코드 존재 확인")
        void existsByConventionIdAndCode_WhenExists_ShouldReturnTrue() {
            // given
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode code = RuleCode.of("DOM-001");
            given(codingRuleQueryPort.existsByConventionIdAndCode(conventionId, code))
                    .willReturn(true);

            // when
            boolean result = sut.existsByConventionIdAndCode(conventionId, code);

            // then
            assertThat(result).isTrue();
            then(codingRuleQueryPort).should().existsByConventionIdAndCode(conventionId, code);
        }
    }

    @Nested
    @DisplayName("existsByConventionIdAndCodeExcluding 메서드")
    class ExistsByConventionIdAndCodeExcluding {

        @Test
        @DisplayName("성공 - 특정 규칙 제외하고 코드 존재 확인")
        void existsByConventionIdAndCodeExcluding_WhenExists_ShouldReturnTrue() {
            // given
            ConventionId conventionId = ConventionId.of(1L);
            RuleCode code = RuleCode.of("DOM-001");
            CodingRuleId excludeId = CodingRuleId.of(1L);
            given(
                            codingRuleQueryPort.existsByConventionIdAndCodeExcluding(
                                    conventionId, code, excludeId))
                    .willReturn(true);

            // when
            boolean result =
                    sut.existsByConventionIdAndCodeExcluding(conventionId, code, excludeId);

            // then
            assertThat(result).isTrue();
            then(codingRuleQueryPort)
                    .should()
                    .existsByConventionIdAndCodeExcluding(conventionId, code, excludeId);
        }
    }

    @Nested
    @DisplayName("findByConventionId 메서드")
    class FindByConventionId {

        @Test
        @DisplayName("성공 - 컨벤션 ID로 코딩 규칙 목록 조회")
        void findByConventionId_WithConventionId_ShouldReturnList() {
            // given
            ConventionId conventionId = ConventionId.of(1L);
            List<CodingRule> codingRules = List.of(codingRule);
            given(codingRuleQueryPort.findByConventionId(conventionId)).willReturn(codingRules);

            // when
            List<CodingRule> result = sut.findByConventionId(conventionId);

            // then
            assertThat(result).hasSize(1).containsExactly(codingRule);
            then(codingRuleQueryPort).should().findByConventionId(conventionId);
        }
    }

    @Nested
    @DisplayName("searchByKeyword 메서드")
    class SearchByKeyword {

        @Test
        @DisplayName("성공 - 키워드로 코딩 규칙 검색")
        void searchByKeyword_WithKeyword_ShouldReturnList() {
            // given
            String keyword = "Lombok";
            ConventionId conventionId = ConventionId.of(1L);
            List<CodingRule> codingRules = List.of(codingRule);
            given(codingRuleQueryPort.searchByKeyword(keyword, conventionId))
                    .willReturn(codingRules);

            // when
            List<CodingRule> result = sut.searchByKeyword(keyword, conventionId);

            // then
            assertThat(result).hasSize(1).containsExactly(codingRule);
            then(codingRuleQueryPort).should().searchByKeyword(keyword, conventionId);
        }
    }
}
