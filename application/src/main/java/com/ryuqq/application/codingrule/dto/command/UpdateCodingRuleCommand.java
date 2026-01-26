package com.ryuqq.application.codingrule.dto.command;

import java.util.List;

/**
 * UpdateCodingRuleCommand - 코딩 규칙 수정 커맨드
 *
 * <p>코딩 규칙 수정에 필요한 데이터를 전달합니다.
 *
 * <p>Zero-Tolerance 여부는 ZeroToleranceRule 엔티티의 존재 여부로 판단하므로 이 커맨드에서 제외됩니다.
 *
 * @param codingRuleId 수정할 코딩 규칙 ID
 * @param structureId 패키지 구조 ID (nullable)
 * @param code 규칙 코드
 * @param name 규칙 이름
 * @param severity 심각도
 * @param category 카테고리
 * @param description 상세 설명
 * @param rationale 근거
 * @param autoFixable 자동 수정 가능 여부
 * @param appliesTo 적용 대상 목록
 * @param sdkArtifact SDK 아티팩트 (nullable)
 * @param sdkMinVersion SDK 최소 버전 (nullable)
 * @param sdkMaxVersion SDK 최대 버전 (nullable)
 * @author ryu-qqq
 */
public record UpdateCodingRuleCommand(
        Long codingRuleId,
        Long structureId,
        String code,
        String name,
        String severity,
        String category,
        String description,
        String rationale,
        boolean autoFixable,
        List<String> appliesTo,
        String sdkArtifact,
        String sdkMinVersion,
        String sdkMaxVersion) {}
