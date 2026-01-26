package com.ryuqq.domain.codingrule.vo;

/**
 * CodingRuleIndexData - 코딩 규칙 인덱스 데이터 VO
 *
 * <p>규칙의 핵심 정보만 포함하는 경량 VO입니다.
 *
 * <p>VO-001: VO는 불변 객체입니다.
 *
 * @param code 규칙 코드
 * @param name 규칙 이름
 * @param severity 심각도
 * @param category 카테고리
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CodingRuleIndexData(String code, String name, String severity, String category) {

    /**
     * CodingRuleIndexData 생성
     *
     * @param code 규칙 코드
     * @param name 규칙 이름
     * @param severity 심각도
     * @param category 카테고리
     * @return CodingRuleIndexData
     */
    public static CodingRuleIndexData of(
            String code, String name, String severity, String category) {
        return new CodingRuleIndexData(code, name, severity, category);
    }
}
