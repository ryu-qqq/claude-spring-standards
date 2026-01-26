package com.ryuqq.application.classtype.dto.query;

import java.util.List;

/**
 * ClassTypeSearchParams - ClassType 검색 파라미터
 *
 * <p>ClassType 검색 시 사용되는 파라미터를 담는 불변 객체입니다.
 *
 * @param ids ID 목록 (특정 ID 조회, nullable)
 * @param categoryIds Category ID 목록 (nullable)
 * @param architectureIds Architecture ID 목록 (nullable)
 * @param searchField 검색 필드 (CODE, NAME, DESCRIPTION, nullable)
 * @param searchWord 검색어 (nullable)
 * @param cursor 커서 (nullable)
 * @param size 페이지 크기
 * @author ryu-qqq
 */
public record ClassTypeSearchParams(
        List<Long> ids,
        List<Long> categoryIds,
        List<Long> architectureIds,
        String searchField,
        String searchWord,
        Long cursor,
        int size) {

    public static ClassTypeSearchParams of(
            List<Long> ids,
            List<Long> categoryIds,
            List<Long> architectureIds,
            String searchField,
            String searchWord,
            Long cursor,
            int size) {
        return new ClassTypeSearchParams(
                ids, categoryIds, architectureIds, searchField, searchWord, cursor, size);
    }
}
