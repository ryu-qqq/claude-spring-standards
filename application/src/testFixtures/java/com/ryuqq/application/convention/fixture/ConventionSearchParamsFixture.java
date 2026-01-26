package com.ryuqq.application.convention.fixture;

import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;

/**
 * ConventionSearchParams Test Fixture
 *
 * <p>Application 레이어 테스트에서 사용하는 SearchParams DTO 생성 유틸리티
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ConventionSearchParamsFixture {

    private static final int DEFAULT_SIZE = 20;

    private ConventionSearchParamsFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /** 기본 SearchParams (첫 페이지, 기본 사이즈) */
    public static ConventionSearchParams defaultSearchParams() {
        return ConventionSearchParams.of(CommonCursorParams.defaultPage());
    }

    /** 첫 페이지 SearchParams with 사이즈 */
    public static ConventionSearchParams firstPageSearchParams(int size) {
        return ConventionSearchParams.of(CommonCursorParams.first(size));
    }

    /** 커서 기반 SearchParams */
    public static ConventionSearchParams searchParamsWithCursor(Long cursor) {
        String cursorStr = cursor != null ? String.valueOf(cursor) : null;
        return ConventionSearchParams.of(CommonCursorParams.of(cursorStr, DEFAULT_SIZE));
    }

    /** 커서와 사이즈가 있는 SearchParams */
    public static ConventionSearchParams searchParamsWithCursorAndSize(Long cursor, int size) {
        String cursorStr = cursor != null ? String.valueOf(cursor) : null;
        return ConventionSearchParams.of(CommonCursorParams.of(cursorStr, size));
    }

    /** 커스텀 SearchParams 생성 */
    public static ConventionSearchParams customSearchParams(Long cursor, Integer size) {
        String cursorStr = cursor != null ? String.valueOf(cursor) : null;
        return ConventionSearchParams.of(CommonCursorParams.of(cursorStr, size));
    }
}
