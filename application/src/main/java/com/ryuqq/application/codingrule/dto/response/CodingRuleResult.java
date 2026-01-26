package com.ryuqq.application.codingrule.dto.response;

import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import java.time.Instant;
import java.util.List;

/**
 * CodingRuleResult - 코딩 규칙 조회 결과 DTO
 *
 * <p>Application Layer의 결과 DTO입니다.
 *
 * <p>Zero-Tolerance 여부는 ZeroToleranceRule 엔티티의 존재 여부로 판단하므로 이 Result에서 제외됩니다.
 *
 * @param id 규칙 ID
 * @param conventionId 컨벤션 ID
 * @param structureId 패키지 구조 ID (nullable)
 * @param code 규칙 코드
 * @param name 규칙 이름
 * @param severity 심각도
 * @param category 카테고리
 * @param description 설명
 * @param rationale 근거
 * @param autoFixable 자동 수정 가능 여부
 * @param appliesTo 적용 대상 목록
 * @param sdkConstraint SDK 제약 정보 (nullable)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record CodingRuleResult(
        Long id,
        Long conventionId,
        Long structureId,
        String code,
        String name,
        RuleSeverity severity,
        RuleCategory category,
        String description,
        String rationale,
        boolean autoFixable,
        List<String> appliesTo,
        SdkConstraintResult sdkConstraint,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Domain 객체로부터 Result 생성
     *
     * @param rule CodingRule 도메인 객체
     * @return CodingRuleResult
     */
    public static CodingRuleResult from(CodingRule rule) {
        return new CodingRuleResult(
                rule.id().value(),
                rule.conventionId().value(),
                rule.structureId() != null ? rule.structureId().value() : null,
                rule.code().value(),
                rule.name().value(),
                rule.severity(),
                rule.category(),
                rule.description(),
                rule.rationale(),
                rule.isAutoFixable(),
                rule.appliesTo().targets(),
                SdkConstraintResult.from(rule.sdkConstraint()),
                rule.createdAt(),
                rule.updatedAt());
    }

    /**
     * SdkConstraintResult - SDK 제약 조건 결과
     *
     * @param artifact SDK 아티팩트
     * @param minVersion 최소 버전
     * @param maxVersion 최대 버전
     */
    public record SdkConstraintResult(String artifact, String minVersion, String maxVersion) {
        public static SdkConstraintResult from(
                com.ryuqq.domain.codingrule.vo.SdkConstraint constraint) {
            if (constraint == null || constraint.isEmpty()) {
                return null;
            }
            return new SdkConstraintResult(
                    constraint.artifact(), constraint.minVersion(), constraint.maxVersion());
        }
    }
}
