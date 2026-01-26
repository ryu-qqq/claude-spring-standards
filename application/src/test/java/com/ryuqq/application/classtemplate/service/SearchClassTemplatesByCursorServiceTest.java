package com.ryuqq.application.classtemplate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.classtemplate.assembler.ClassTemplateAssembler;
import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.application.classtemplate.factory.query.ClassTemplateQueryFactory;
import com.ryuqq.application.classtemplate.manager.ClassTemplateReadManager;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
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
 * SearchClassTemplatesByCursorService 단위 테스트
 *
 * <p>ClassTemplate 커서 기반 조회 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchClassTemplatesByCursorService 단위 테스트")
class SearchClassTemplatesByCursorServiceTest {

    @Mock private ClassTemplateQueryFactory classTemplateQueryFactory;

    @Mock private ClassTemplateReadManager classTemplateReadManager;

    @Mock private ClassTemplateAssembler classTemplateAssembler;

    @Mock private ClassTemplateSliceCriteria criteria;

    @Mock private ClassTemplate classTemplate;

    @Mock private ClassTemplateSliceResult sliceResult;

    private SearchClassTemplatesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchClassTemplatesByCursorService(
                        classTemplateQueryFactory,
                        classTemplateReadManager,
                        classTemplateAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 SearchParams로 ClassTemplate 목록 조회")
        void execute_WithValidSearchParams_ShouldReturnSliceResult() {
            // given
            ClassTemplateSearchParams searchParams = createDefaultSearchParams();
            List<ClassTemplate> classTemplates = List.of(classTemplate);

            given(classTemplateQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(classTemplateReadManager.findBySliceCriteria(criteria))
                    .willReturn(classTemplates);
            given(classTemplateAssembler.toSliceResult(classTemplates, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ClassTemplateSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(classTemplateQueryFactory).should().createSliceCriteria(searchParams);
            then(classTemplateReadManager).should().findBySliceCriteria(criteria);
            then(classTemplateAssembler)
                    .should()
                    .toSliceResult(classTemplates, searchParams.size());
        }

        @Test
        @DisplayName("성공 - 빈 결과 조회")
        void execute_WhenNoResults_ShouldReturnEmptySliceResult() {
            // given
            ClassTemplateSearchParams searchParams = createDefaultSearchParams();
            List<ClassTemplate> emptyList = List.of();

            given(classTemplateQueryFactory.createSliceCriteria(searchParams)).willReturn(criteria);
            given(classTemplateReadManager.findBySliceCriteria(criteria)).willReturn(emptyList);
            given(classTemplateAssembler.toSliceResult(emptyList, searchParams.size()))
                    .willReturn(sliceResult);

            // when
            ClassTemplateSliceResult result = sut.execute(searchParams);

            // then
            assertThat(result).isEqualTo(sliceResult);

            then(classTemplateQueryFactory).should().createSliceCriteria(searchParams);
            then(classTemplateReadManager).should().findBySliceCriteria(criteria);
            then(classTemplateAssembler).should().toSliceResult(emptyList, searchParams.size());
        }
    }

    private ClassTemplateSearchParams createDefaultSearchParams() {
        CommonCursorParams cursorParams = CommonCursorParams.of(null, 20);
        return ClassTemplateSearchParams.of(cursorParams);
    }
}
