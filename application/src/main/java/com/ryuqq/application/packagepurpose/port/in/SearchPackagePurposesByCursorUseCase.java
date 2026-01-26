package com.ryuqq.application.packagepurpose.port.in;

import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;

/**
 * SearchPackagePurposesByCursorUseCase - PackagePurpose 복합 조건 조회 UseCase (커서 기반)
 *
 * <p>PackagePurpose 목록을 커서 기반으로 복합 조건(구조 ID, 검색 조건)으로 조회합니다.
 *
 * @author ryu-qqq
 */
public interface SearchPackagePurposesByCursorUseCase {

    PackagePurposeSliceResult execute(PackagePurposeSearchParams searchParams);
}
