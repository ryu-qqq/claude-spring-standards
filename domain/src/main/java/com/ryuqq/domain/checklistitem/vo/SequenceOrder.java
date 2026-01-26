package com.ryuqq.domain.checklistitem.vo;

/**
 * SequenceOrder - 체크 순서 Value Object
 *
 * <p>체크리스트 항목의 검사 순서를 정의합니다.
 *
 * @author ryu-qqq
 */
public record SequenceOrder(int value) {

    public SequenceOrder {
        if (value <= 0) {
            throw new IllegalArgumentException("SequenceOrder must be positive, but was: " + value);
        }
    }

    /**
     * 팩토리 메서드
     *
     * @param value 순서 값 (양수)
     * @return SequenceOrder 인스턴스
     */
    public static SequenceOrder of(int value) {
        return new SequenceOrder(value);
    }

    /**
     * 다음 순서 반환
     *
     * @return 현재 값 + 1인 SequenceOrder
     */
    public SequenceOrder next() {
        return new SequenceOrder(value + 1);
    }

    /**
     * 특정 순서보다 앞인지 확인
     *
     * @param other 비교 대상
     * @return 현재 순서가 더 작으면 true
     */
    public boolean isBefore(SequenceOrder other) {
        return this.value < other.value;
    }

    /**
     * 특정 순서보다 뒤인지 확인
     *
     * @param other 비교 대상
     * @return 현재 순서가 더 크면 true
     */
    public boolean isAfter(SequenceOrder other) {
        return this.value > other.value;
    }
}
