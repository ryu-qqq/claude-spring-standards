package com.ryuqq.domain.feedbackqueue.vo;

/**
 * ReviewNotes - 리뷰 노트 Value Object
 *
 * <p>피드백 승인/거절 시 첨부되는 코멘트 또는 거절 사유를 캡슐화합니다.
 *
 * @param value 리뷰 노트 문자열 (nullable)
 * @author ryu-qqq
 */
public record ReviewNotes(String value) {

    /**
     * ReviewNotes 생성
     *
     * @param value 리뷰 노트 문자열
     * @return ReviewNotes
     */
    public static ReviewNotes of(String value) {
        return new ReviewNotes(value);
    }

    /**
     * 빈 리뷰 노트 생성
     *
     * @return 빈 ReviewNotes
     */
    public static ReviewNotes empty() {
        return new ReviewNotes(null);
    }

    /**
     * 빈 리뷰 노트인지 확인
     *
     * @return value가 null이거나 빈 문자열이면 true
     */
    public boolean isEmpty() {
        return value == null || value.isBlank();
    }

    /**
     * 리뷰 노트가 있는지 확인
     *
     * @return value가 존재하면 true
     */
    public boolean hasValue() {
        return !isEmpty();
    }
}
