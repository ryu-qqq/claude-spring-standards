package com.ryuqq.application.configfiletemplate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.configfiletemplate.assembler.ConfigFileTemplateAssembler;
import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateResult;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;
import com.ryuqq.application.configfiletemplate.factory.query.ConfigFileTemplateQueryFactory;
import com.ryuqq.application.configfiletemplate.manager.ConfigFileTemplateReadManager;
import com.ryuqq.domain.common.vo.SliceMeta;
import com.ryuqq.domain.configfiletemplate.aggregate.ConfigFileTemplate;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
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
 * SearchConfigFileTemplatesByCursorService 단위 테스트
 *
 * <p>ConfigFileTemplate 복합 조건 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchConfigFileTemplatesByCursorService 단위 테스트")
class SearchConfigFileTemplatesByCursorServiceTest {

    @Mock private ConfigFileTemplateReadManager configFileTemplateReadManager;

    @Mock private ConfigFileTemplateQueryFactory configFileTemplateQueryFactory;

    @Mock private ConfigFileTemplateAssembler configFileTemplateAssembler;

    @Mock private ConfigFileTemplate configFileTemplate;

    @Mock private ConfigFileTemplateSliceCriteria criteria;

    @Mock private ConfigFileTemplateResult configFileTemplateResult;

    private SearchConfigFileTemplatesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchConfigFileTemplatesByCursorService(
                        configFileTemplateReadManager,
                        configFileTemplateQueryFactory,
                        configFileTemplateAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 검색 파라미터로 ConfigFileTemplate 목록 조회")
        void execute_WithSearchParams_ShouldReturnSliceResult() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(cursorParams);

            List<ConfigFileTemplate> configFileTemplates = List.of(configFileTemplate);
            List<ConfigFileTemplateResult> results = List.of(configFileTemplateResult);
            ConfigFileTemplateSliceResult expectedResult =
                    new ConfigFileTemplateSliceResult(results, SliceMeta.of(20, false));

            given(configFileTemplateQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(configFileTemplateReadManager.findBySliceCriteria(criteria))
                    .willReturn(configFileTemplates);
            given(configFileTemplateAssembler.toSliceResult(configFileTemplates, 20))
                    .willReturn(expectedResult);

            // when
            ConfigFileTemplateSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(expectedResult);

            then(configFileTemplateQueryFactory).should().createSliceCriteria(searchParams);
            then(configFileTemplateReadManager).should().findBySliceCriteria(criteria);
            then(configFileTemplateAssembler).should().toSliceResult(configFileTemplates, 20);
        }

        @Test
        @DisplayName("성공 - 빈 결과 반환")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            CommonCursorParams cursorParams = CommonCursorParams.first(20);
            ConfigFileTemplateSearchParams searchParams =
                    ConfigFileTemplateSearchParams.of(cursorParams);

            List<ConfigFileTemplate> emptyList = List.of();
            ConfigFileTemplateSliceResult expectedResult =
                    new ConfigFileTemplateSliceResult(List.of(), SliceMeta.of(20, false));

            given(configFileTemplateQueryFactory.createSliceCriteria(searchParams))
                    .willReturn(criteria);
            given(configFileTemplateReadManager.findBySliceCriteria(criteria))
                    .willReturn(emptyList);
            given(configFileTemplateAssembler.toSliceResult(emptyList, 20))
                    .willReturn(expectedResult);

            // when
            ConfigFileTemplateSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.sliceMeta().hasNext()).isFalse();
        }
    }
}
