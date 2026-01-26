package com.ryuqq.adapter.out.persistence.mcp.dto;

/**
 * ModuleLayerConventionRow - Module + Layer + Convention JOIN 결과 DTO
 *
 * <p>QueryDSL Projection용 DTO입니다.
 *
 * @param moduleId 모듈 ID
 * @param moduleName 모듈 이름
 * @param moduleDescription 모듈 설명
 * @param layerId 레이어 ID
 * @param layerCode 레이어 코드
 * @param layerName 레이어 이름
 * @param conventionId 컨벤션 ID (nullable)
 * @param conventionVersion 컨벤션 버전 (nullable)
 * @param conventionDescription 컨벤션 설명 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleLayerConventionRow(
        Long moduleId,
        String moduleName,
        String moduleDescription,
        Long layerId,
        String layerCode,
        String layerName,
        Long conventionId,
        String conventionVersion,
        String conventionDescription) {}
