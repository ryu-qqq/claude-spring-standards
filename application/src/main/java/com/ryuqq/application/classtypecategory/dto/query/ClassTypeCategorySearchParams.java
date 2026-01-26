package com.ryuqq.application.classtypecategory.dto.query;

import java.util.List;

/**
 * ClassTypeCategorySearchParams - ClassTypeCategory 검색 파라미터
 *
 * <p>ClassTypeCategory 검색 시 사용되는 파라미터를 담는 불변 객체입니다.
 *
 * @param ids ID 목록 (특정 ID 조회, nullable)
 * @param architectureIds Architecture ID 목록 (nullable)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION, nullable)
 * @param searchWord 검색어 (nullable)
 * @param cursor 커서 (nullable)
 * @param size 페이지 크기
 * @author ryu-qqq
 */
public record ClassTypeCategorySearchParams(
        List<Long> ids,
        List<Long> architectureIds,
        String searchField,
        String searchWord,
        Long cursor,
        int size) {

    public static ClassTypeCategorySearchParams of(
            List<Long> ids,
            List<Long> architectureIds,
            String searchField,
            String searchWord,
            Long cursor,
            int size) {
        return new ClassTypeCategorySearchParams(
                ids, architectureIds, searchField, searchWord, cursor, size);
    }
}
