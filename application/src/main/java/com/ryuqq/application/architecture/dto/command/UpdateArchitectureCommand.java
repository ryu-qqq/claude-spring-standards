package com.ryuqq.application.architecture.dto.command;

import java.util.List;

/**
 * UpdateArchitectureCommand - Architecture 수정 Command DTO
 *
 * <p>Architecture 수정에 필요한 데이터를 담습니다.
 *
 * <p>CDTO-001: Command DTO는 Record로 정의.
 *
 * <p>CDTO-003: 수정용은 Update{Domain}Command 네이밍.
 *
 * <p>CDTO-004: Update Command는 UpdateData 생성에 필요한 전체 필드 포함.
 *
 * <p>CDTO-006: Command DTO에 Validation 어노테이션 금지 → REST API Layer에서 검증.
 *
 * <p>CDTO-007: Command DTO는 Domain 타입 의존 금지.
 *
 * @param id 수정 대상 Architecture ID
 * @param name 아키텍처 이름
 * @param patternType 패턴 타입
 * @param patternDescription 패턴 설명 (nullable)
 * @param patternPrinciples 패턴 원칙 목록
 * @param referenceLinks 참조 링크 목록
 * @author ryu-qqq
 */
public record UpdateArchitectureCommand(
        Long id,
        String name,
        String patternType,
        String patternDescription,
        List<String> patternPrinciples,
        List<String> referenceLinks) {}
