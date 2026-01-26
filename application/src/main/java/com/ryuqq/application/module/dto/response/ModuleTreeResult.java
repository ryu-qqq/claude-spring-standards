package com.ryuqq.application.module.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * ModuleTreeResult - Module 트리 구조 조회 결과
 *
 * <p>Module 트리 구조를 표현하는 DTO입니다. children 필드를 통해 중첩 구조를 표현합니다.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param moduleId 모듈 ID
 * @param layerId 레이어 ID
 * @param parentModuleId 부모 모듈 ID (nullable)
 * @param name 모듈 이름
 * @param description 모듈 설명
 * @param modulePath 모듈 파일 시스템 경로
 * @param buildIdentifier 빌드 시스템 식별자 (nullable)
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 * @param children 자식 모듈 목록 (트리 구조)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ModuleTreeResult(
        Long moduleId,
        Long layerId,
        Long parentModuleId,
        String name,
        String description,
        String modulePath,
        String buildIdentifier,
        Instant createdAt,
        Instant updatedAt,
        List<ModuleTreeResult> children) {

    /**
     * 빈 children 리스트를 가진 ModuleTreeResult 생성
     *
     * @param moduleId 모듈 ID
     * @param layerId 레이어 ID
     * @param parentModuleId 부모 모듈 ID
     * @param name 모듈 이름
     * @param description 모듈 설명
     * @param modulePath 모듈 파일 시스템 경로
     * @param buildIdentifier 빌드 시스템 식별자 (nullable)
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return ModuleTreeResult (children는 빈 리스트)
     */
    public static ModuleTreeResult of(
            Long moduleId,
            Long layerId,
            Long parentModuleId,
            String name,
            String description,
            String modulePath,
            String buildIdentifier,
            Instant createdAt,
            Instant updatedAt) {
        return new ModuleTreeResult(
                moduleId,
                layerId,
                parentModuleId,
                name,
                description,
                modulePath,
                buildIdentifier,
                createdAt,
                updatedAt,
                List.of());
    }

    /**
     * children를 포함한 ModuleTreeResult 생성
     *
     * @param moduleId 모듈 ID
     * @param layerId 레이어 ID
     * @param parentModuleId 부모 모듈 ID
     * @param name 모듈 이름
     * @param description 모듈 설명
     * @param modulePath 모듈 파일 시스템 경로
     * @param buildIdentifier 빌드 시스템 식별자 (nullable)
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @param children 자식 모듈 목록
     * @return ModuleTreeResult
     */
    public static ModuleTreeResult withChildren(
            Long moduleId,
            Long layerId,
            Long parentModuleId,
            String name,
            String description,
            String modulePath,
            String buildIdentifier,
            Instant createdAt,
            Instant updatedAt,
            List<ModuleTreeResult> children) {
        return new ModuleTreeResult(
                moduleId,
                layerId,
                parentModuleId,
                name,
                description,
                modulePath,
                buildIdentifier,
                createdAt,
                updatedAt,
                children);
    }
}
