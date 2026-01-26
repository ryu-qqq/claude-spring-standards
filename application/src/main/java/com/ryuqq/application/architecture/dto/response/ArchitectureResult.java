package com.ryuqq.application.architecture.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * ArchitectureResult - Architecture 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 Architecture 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param id Architecture ID
 * @param techStackId 기술 스택 ID (FK)
 * @param name 아키텍처 이름
 * @param patternType 패턴 타입
 * @param patternDescription 패턴 설명
 * @param patternPrinciples 패턴 원칙 목록
 * @param referenceLinks 참조 링크 목록
 * @param deleted 삭제 여부
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 */
public record ArchitectureResult(
        Long id,
        Long techStackId,
        String name,
        String patternType,
        String patternDescription,
        List<String> patternPrinciples,
        List<String> referenceLinks,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt) {}
