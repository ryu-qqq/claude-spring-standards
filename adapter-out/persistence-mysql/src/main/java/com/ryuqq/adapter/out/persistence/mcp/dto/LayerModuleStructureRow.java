package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * LayerModuleStructureRow - Layer + Module + PackageStructure JOIN 결과 DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * <p>Flat 구조로 조회 후 메모리에서 그룹핑합니다.
 *
 * @param layerCode 레이어 코드
 * @param layerName 레이어 이름
 * @param layerDescription 레이어 설명
 * @param moduleId 모듈 ID
 * @param moduleName 모듈 이름
 * @param moduleDescription 모듈 설명
 * @param structureId 패키지 구조 ID (nullable)
 * @param pathPattern 경로 패턴 (nullable)
 * @param purposeDescription 패키지 목적 설명 (nullable)
 * @param allowedClassTypes 허용 클래스 타입 목록 - 콤마 구분 (nullable)
 * @param templateCount 템플릿 개수
 * @param ruleCount 규칙 개수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LayerModuleStructureRow(
        String layerCode,
        String layerName,
        String layerDescription,
        Long moduleId,
        String moduleName,
        String moduleDescription,
        Long structureId,
        String pathPattern,
        String purposeDescription,
        String allowedClassTypes,
        int templateCount,
        int ruleCount) {}
