package com.ryuqq.application.architecture.dto.command;

import java.util.List;

/**
 * CreateArchitectureCommand - Architecture 생성 Command DTO
 *
 * <p>Architecture 생성에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-002: 생성용은 Create{Domain}Command 네이밍.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * @param techStackId 기술 스택 ID (FK)
 * @param name 아키텍처 이름 (예: "hexagonal-multimodule")
 * @param patternType 패턴 타입 (HEXAGONAL, LAYERED, CLEAN 등)
 * @param patternDescription 패턴 설명 (nullable)
 * @param patternPrinciples 패턴 원칙 목록 (예: ["DIP", "SRP", "OCP"])
 * @param referenceLinks 참조 링크 목록 (공식 문서, 아키텍처 가이드 등)
 * @author ryu-qqq
 */
public record CreateArchitectureCommand(
        Long techStackId,
        String name,
        String patternType,
        String patternDescription,
        List<String> patternPrinciples,
        List<String> referenceLinks) {}
