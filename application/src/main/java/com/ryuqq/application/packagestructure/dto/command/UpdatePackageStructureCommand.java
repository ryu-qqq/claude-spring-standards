package com.ryuqq.application.packagestructure.dto.command;

import java.util.List;

/**
 * UpdatePackageStructureCommand - 패키지 구조 수정 커맨드
 *
 * <p>패키지 구조 수정에 필요한 데이터를 전달합니다.
 *
 * @param packageStructureId 수정할 패키지 구조 ID
 * @param pathPattern 경로 패턴
 * @param allowedClassTypes 허용 클래스 타입 목록
 * @param namingPattern 네이밍 패턴 (nullable)
 * @param namingSuffix 네이밍 접미사 (nullable)
 * @param description 설명 (nullable)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdatePackageStructureCommand(
        Long packageStructureId,
        String pathPattern,
        List<String> allowedClassTypes,
        String namingPattern,
        String namingSuffix,
        String description) {}
