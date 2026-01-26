package com.ryuqq.domain.configfiletemplate.vo;

/**
 * DisplayOrder - 표시 순서 Value Object
 *
 * <p>템플릿의 정렬 순서입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record DisplayOrder(Integer value) {

    public static DisplayOrder of(Integer value) {
        return new DisplayOrder(value != null ? value : 0);
    }

    public static DisplayOrder defaultOrder() {
        return new DisplayOrder(0);
    }
}
