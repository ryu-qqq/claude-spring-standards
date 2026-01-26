package com.ryuqq.application.codingrule.dto.command;

import java.util.List;

/**
 * CreateCodingRuleCommand - 코딩 규칙 생성 커맨드
 *
 * <p>코딩 규칙 생성에 필요한 데이터를 전달합니다.
 *
 * <p>Zero-Tolerance 여부는 ZeroToleranceRule 엔티티의 존재 여부로 판단하므로 이 커맨드에서 제외됩니다.
 *
 * @param conventionId 컨벤션 ID
 * @param structureId 패키지 구조 ID (nullable, 특정 패키지 구조에만 적용 시)
 * @param code 규칙 코드 (예: DOM-001, APP-001)
 * @param name 규칙 이름
 * @param severity 심각도 (BLOCKER, CRITICAL, MAJOR, MINOR, INFO)
 * @param category 카테고리 (ANNOTATION, BEHAVIOR, STRUCTURE 등)
 * @param description 상세 설명
 * @param rationale 근거
 * @param autoFixable 자동 수정 가능 여부
 * @param appliesTo 적용 대상 목록 (CLASS, METHOD, FIELD 등)
 * @param sdkArtifact SDK 아티팩트 (nullable)
 * @param sdkMinVersion SDK 최소 버전 (nullable)
 * @param sdkMaxVersion SDK 최대 버전 (nullable)
 * @author ryu-qqq
 */
public record CreateCodingRuleCommand(
        Long conventionId,
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
