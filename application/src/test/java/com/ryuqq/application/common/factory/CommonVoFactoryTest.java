package com.ryuqq.application.common.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.domain.common.vo.DateRange;
import com.ryuqq.domain.common.vo.PageRequest;
import com.ryuqq.domain.common.vo.QueryContext;
import com.ryuqq.domain.common.vo.SortDirection;
import com.ryuqq.domain.common.vo.SortKey;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CommonVoFactory 단위 테스트
 *
 * <p>공통 VO 생성 Factory 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("factory")
@Tag("application-layer")
@DisplayName("CommonVoFactory 단위 테스트")
class CommonVoFactoryTest {

    private CommonVoFactory sut;

    @BeforeEach
    void setUp() {
        sut = new CommonVoFactory();
    }

    @Nested
    @DisplayName("createDateRange 메서드")
    class CreateDateRange {

        @Test
        @DisplayName("성공 - 시작일과 종료일로 DateRange 생성")
        void createDateRange_WithValidDates_ShouldReturnDateRange() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);

            // when
            DateRange result = sut.createDateRange(startDate, endDate);

            // then
            assertThat(result).isNotNull();
            assertThat(result.startDate()).isEqualTo(startDate);
            assertThat(result.endDate()).isEqualTo(endDate);
        }

        @Test
        @DisplayName("성공 - 시작일만으로 DateRange 생성")
        void createDateRange_WithOnlyStartDate_ShouldReturnDateRange() {
            // given
            LocalDate startDate = LocalDate.of(2024, 1, 1);

            // when
            DateRange result = sut.createDateRange(startDate, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.startDate()).isEqualTo(startDate);
            assertThat(result.endDate()).isNull();
        }

        @Test
        @DisplayName("성공 - 종료일만으로 DateRange 생성")
        void createDateRange_WithOnlyEndDate_ShouldReturnDateRange() {
            // given
            LocalDate endDate = LocalDate.of(2024, 12, 31);

            // when
            DateRange result = sut.createDateRange(null, endDate);

            // then
            assertThat(result).isNotNull();
            assertThat(result.startDate()).isNull();
            assertThat(result.endDate()).isEqualTo(endDate);
        }
    }

    @Nested
    @DisplayName("createPageRequest 메서드")
    class CreatePageRequest {

        @Test
        @DisplayName("성공 - 페이지 번호와 크기로 PageRequest 생성")
        void createPageRequest_WithValidParams_ShouldReturnPageRequest() {
            // given
            Integer page = 0;
            Integer size = 20;

            // when
            PageRequest result = sut.createPageRequest(page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.page()).isEqualTo(page);
            assertThat(result.size()).isEqualTo(size);
        }

        @Test
        @DisplayName("성공 - 두 번째 페이지 PageRequest 생성")
        void createPageRequest_WithSecondPage_ShouldReturnPageRequest() {
            // given
            Integer page = 1;
            Integer size = 10;

            // when
            PageRequest result = sut.createPageRequest(page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("parseSortDirection 메서드")
    class ParseSortDirection {

        @Test
        @DisplayName("성공 - ASC 문자열을 SortDirection으로 변환")
        void parseSortDirection_WithAsc_ShouldReturnAscDirection() {
            // given
            String direction = "ASC";

            // when
            SortDirection result = sut.parseSortDirection(direction);

            // then
            assertThat(result).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("성공 - DESC 문자열을 SortDirection으로 변환")
        void parseSortDirection_WithDesc_ShouldReturnDescDirection() {
            // given
            String direction = "DESC";

            // when
            SortDirection result = sut.parseSortDirection(direction);

            // then
            assertThat(result).isEqualTo(SortDirection.DESC);
        }
    }

    @Nested
    @DisplayName("createQueryContext 메서드")
    class CreateQueryContext {

        @Test
        @DisplayName("성공 - 기본 QueryContext 생성")
        void createQueryContext_WithValidParams_ShouldReturnQueryContext() {
            // given
            TestSortKey sortKey = TestSortKey.CREATED_AT;
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);

            // when
            QueryContext<TestSortKey> result =
                    sut.createQueryContext(sortKey, sortDirection, pageRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sortKey()).isEqualTo(sortKey);
            assertThat(result.sortDirection()).isEqualTo(sortDirection);
            assertThat(result.pageRequest()).isEqualTo(pageRequest);
            assertThat(result.includeDeleted()).isFalse();
        }

        @Test
        @DisplayName("성공 - includeDeleted 포함 QueryContext 생성")
        void createQueryContext_WithIncludeDeleted_ShouldReturnQueryContextWithIncludeDeleted() {
            // given
            TestSortKey sortKey = TestSortKey.ID;
            SortDirection sortDirection = SortDirection.ASC;
            PageRequest pageRequest = PageRequest.of(0, 10);
            boolean includeDeleted = true;

            // when
            QueryContext<TestSortKey> result =
                    sut.createQueryContext(sortKey, sortDirection, pageRequest, includeDeleted);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sortKey()).isEqualTo(sortKey);
            assertThat(result.sortDirection()).isEqualTo(sortDirection);
            assertThat(result.pageRequest()).isEqualTo(pageRequest);
            assertThat(result.includeDeleted()).isTrue();
        }

        @Test
        @DisplayName("성공 - includeDeleted false로 QueryContext 생성")
        void createQueryContext_WithIncludeDeletedFalse_ShouldReturnQueryContextWithoutDeleted() {
            // given
            TestSortKey sortKey = TestSortKey.NAME;
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(1, 15);
            boolean includeDeleted = false;

            // when
            QueryContext<TestSortKey> result =
                    sut.createQueryContext(sortKey, sortDirection, pageRequest, includeDeleted);

            // then
            assertThat(result).isNotNull();
            assertThat(result.includeDeleted()).isFalse();
        }
    }

    /** 테스트용 SortKey enum */
    private enum TestSortKey implements SortKey {
        ID("id"),
        NAME("name"),
        CREATED_AT("createdAt");

        private final String fieldName;

        TestSortKey(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String fieldName() {
            return fieldName;
        }
    }
}
