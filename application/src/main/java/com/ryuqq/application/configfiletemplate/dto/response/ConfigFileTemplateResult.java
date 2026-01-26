package com.ryuqq.application.configfiletemplate.dto.response;

import java.time.Instant;

/**
 * ConfigFileTemplateResult - ConfigFileTemplate 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 ConfigFileTemplate 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param id ConfigFileTemplate ID
 * @param techStackId 기술 스택 ID (FK)
 * @param architectureId 아키텍처 ID (FK, nullable)
 * @param toolType 도구 타입
 * @param filePath 파일 경로
 * @param fileName 파일명
 * @param content 파일 내용
 * @param category 카테고리
 * @param description 템플릿 설명
 * @param variables 치환 가능한 변수 정의 (JSON)
 * @param displayOrder 정렬 순서
 * @param isRequired 필수 파일 여부
 * @param deleted 삭제 여부
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ConfigFileTemplateResult(
        Long id,
        Long techStackId,
        Long architectureId,
        String toolType,
        String filePath,
        String fileName,
        String content,
        String category,
        String description,
        String variables,
        Integer displayOrder,
        Boolean isRequired,
        boolean deleted,
        Instant createdAt,
        Instant updatedAt) {}
