package com.ryuqq.application.classtype.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.classtype.assembler.ClassTypeAssembler;
import com.ryuqq.application.classtype.dto.query.ClassTypeSearchParams;
import com.ryuqq.application.classtype.dto.response.ClassTypeResult;
import com.ryuqq.application.classtype.dto.response.ClassTypeSliceResult;
import com.ryuqq.application.classtype.factory.query.ClassTypeQueryFactory;
import com.ryuqq.application.classtype.manager.ClassTypeReadManager;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import com.ryuqq.domain.classtype.fixture.ClassTypeFixture;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import com.ryuqq.domain.common.vo.CursorPageRequest;
import com.ryuqq.domain.common.vo.SliceMeta;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("SearchClassTypesByCursorService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SearchClassTypesByCursorServiceTest {

    @Mock private ClassTypeQueryFactory classTypeQueryFactory;

    @Mock private ClassTypeReadManager classTypeReadManager;

    @Mock private ClassTypeAssembler classTypeAssembler;

    private SearchClassTypesByCursorService sut;

    @BeforeEach
    void setUp() {
        sut =
                new SearchClassTypesByCursorService(
                        classTypeQueryFactory, classTypeReadManager, classTypeAssembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 커서 기반 ClassType 검색")
        void execute_WithValidParams_ShouldReturnSliceResult() {
            // given
            ClassTypeSearchParams params =
                    ClassTypeSearchParams.of(null, null, null, null, null, null, 20);
            ClassTypeSliceCriteria criteria =
                    ClassTypeSliceCriteria.of(
                            CursorPageRequest.first(20), null, null, null, null, null);
            ClassType classType = ClassTypeFixture.defaultExistingClassType();
            List<ClassType> classTypes = List.of(classType);

            ClassTypeResult classTypeResult =
                    new ClassTypeResult(1L, 1L, "AGGREGATE", "Aggregate Root", "설명", 1, null, null);
            ClassTypeSliceResult sliceResult =
                    new ClassTypeSliceResult(List.of(classTypeResult), SliceMeta.of(20, false, 1));

            given(classTypeQueryFactory.createSliceCriteria(params)).willReturn(criteria);
            given(classTypeReadManager.findBySliceCriteria(any())).willReturn(classTypes);
            given(classTypeAssembler.toSliceResult(classTypes, 20)).willReturn(sliceResult);

            // when
            ClassTypeSliceResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            then(classTypeQueryFactory).should().createSliceCriteria(params);
            then(classTypeReadManager).should().findBySliceCriteria(any());
            then(classTypeAssembler).should().toSliceResult(classTypes, 20);
        }

        @Test
        @DisplayName("성공 - 빈 결과")
        void execute_WithNoResult_ShouldReturnEmptySlice() {
            // given
            ClassTypeSearchParams params =
                    ClassTypeSearchParams.of(null, null, null, null, null, null, 20);
            ClassTypeSliceCriteria criteria =
                    ClassTypeSliceCriteria.of(
                            CursorPageRequest.first(20), null, null, null, null, null);
            List<ClassType> classTypes = List.of();
            ClassTypeSliceResult sliceResult =
                    new ClassTypeSliceResult(List.of(), SliceMeta.empty(20));

            given(classTypeQueryFactory.createSliceCriteria(params)).willReturn(criteria);
            given(classTypeReadManager.findBySliceCriteria(any())).willReturn(classTypes);
            given(classTypeAssembler.toSliceResult(classTypes, 20)).willReturn(sliceResult);

            // when
            ClassTypeSliceResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
        }
    }
}
