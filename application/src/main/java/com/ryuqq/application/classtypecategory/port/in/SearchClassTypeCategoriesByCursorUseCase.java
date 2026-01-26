package com.ryuqq.application.classtypecategory.port.in;

import com.ryuqq.application.classtypecategory.dto.query.ClassTypeCategorySearchParams;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;

/**
 * SearchClassTypeCategoriesByCursorUseCase - ClassTypeCategory 커서 기반 검색 UseCase
 *
 * <p>커서 기반 페이지네이션으로 ClassTypeCategory 목록을 조회합니다.
 *
 * <p>UC-001: UseCase는 반드시 interface로 정의.
 *
 * <p>UC-002: UseCase는 execute() 단일 메서드만 제공.
 *
 * <p>UC-007: Query UseCase는 Search/Fetch 접두어 + UseCase 네이밍.
 *
 * @author ryu-qqq
 */
public interface SearchClassTypeCategoriesByCursorUseCase {

    /**
     * ClassTypeCategory 커서 기반 검색 실행
     *
     * @param params 검색 파라미터
     * @return 슬라이스 조회 결과
     */
    ClassTypeCategorySliceResult execute(ClassTypeCategorySearchParams params);
}
