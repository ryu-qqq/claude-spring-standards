package com.ryuqq.application.classtypecategory.dto.response;

import com.ryuqq.domain.common.vo.SliceMeta;
import java.util.List;

/**
 * ClassTypeCategorySliceResult - ClassTypeCategory 슬라이스 조회 결과
 *
 * <p>커서 기반 페이지네이션을 지원하는 슬라이스 조회 결과입니다.
 *
 * @param content 조회된 카테고리 목록
 * @param sliceMeta 슬라이스 메타 정보
 * @author ryu-qqq
 */
public record ClassTypeCategorySliceResult(
        List<ClassTypeCategoryResult> content, SliceMeta sliceMeta) {

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 ClassTypeCategorySliceResult
     */
    public static ClassTypeCategorySliceResult empty(int size) {
        return new ClassTypeCategorySliceResult(List.of(), SliceMeta.empty(size));
    }
}
