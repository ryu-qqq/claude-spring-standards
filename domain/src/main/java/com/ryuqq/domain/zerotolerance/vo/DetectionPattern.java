package com.ryuqq.domain.zerotolerance.vo;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * DetectionPattern - 탐지 패턴 Value Object
 *
 * <p>Zero Tolerance 규칙 위반을 탐지하기 위한 정규식 패턴입니다.
 *
 * @author ryu-qqq
 */
public record DetectionPattern(String value) {

    private static final int MAX_LENGTH = 500;

    /** Compact Constructor - 유효성 검증 */
    public DetectionPattern {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DetectionPattern must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "DetectionPattern must not exceed " + MAX_LENGTH + " characters");
        }
    }

    /** 정적 팩토리 메서드 */
    public static DetectionPattern of(String value) {
        return new DetectionPattern(value);
    }

    /** 빈 패턴 생성 (ArchUnit 기반 탐지의 경우 패턴이 필요 없음) */
    public static DetectionPattern empty() {
        return new DetectionPattern(".*");
    }

    /**
     * 정규식 유효성 검증
     *
     * @return 정규식이 유효하면 true
     */
    public boolean isValidRegex() {
        try {
            Pattern.compile(value);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    /**
     * 주어진 텍스트와 패턴 매칭
     *
     * @param text 검사할 텍스트
     * @return 패턴에 매칭되면 true
     */
    public boolean matches(String text) {
        if (text == null) {
            return false;
        }
        return Pattern.compile(value).matcher(text).find();
    }
}
