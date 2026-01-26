package com.ryuqq.domain.common.vo;

import java.util.List;

/**
 * ReferenceLinks - 참조 링크 목록 Value Object
 *
 * <p>공식 문서, 가이드, 아키텍처 패턴 참조 URL 목록을 관리합니다.
 *
 * <p>예: ["https://docs.spring.io/...", "https://github.com/..."]
 *
 * @author ryu-qqq
 */
public record ReferenceLinks(List<String> values) {

    private static final int MAX_LINK_LENGTH = 2048;
    private static final int MAX_LINKS_COUNT = 10;

    public ReferenceLinks {
        values = values != null ? List.copyOf(values) : List.of();

        if (values.size() > MAX_LINKS_COUNT) {
            throw new IllegalArgumentException(
                    "ReferenceLinks must not exceed " + MAX_LINKS_COUNT + " links");
        }

        for (String link : values) {
            if (link == null || link.isBlank()) {
                throw new IllegalArgumentException("Reference link must not be blank");
            }
            if (link.length() > MAX_LINK_LENGTH) {
                throw new IllegalArgumentException(
                        "Reference link must not exceed " + MAX_LINK_LENGTH + " characters");
            }
        }
    }

    public static ReferenceLinks of(List<String> values) {
        return new ReferenceLinks(values);
    }

    public static ReferenceLinks of(String... values) {
        return new ReferenceLinks(List.of(values));
    }

    public static ReferenceLinks empty() {
        return new ReferenceLinks(List.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

    public boolean contains(String link) {
        return values.contains(link);
    }

    /**
     * 새 링크 추가한 새 인스턴스 반환 (불변)
     *
     * @param link 추가할 링크
     * @return 새 ReferenceLinks 인스턴스
     */
    public ReferenceLinks add(String link) {
        List<String> newValues = new java.util.ArrayList<>(values);
        newValues.add(link);
        return new ReferenceLinks(newValues);
    }
}
