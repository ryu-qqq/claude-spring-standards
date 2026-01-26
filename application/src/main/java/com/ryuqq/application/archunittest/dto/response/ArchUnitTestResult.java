package com.ryuqq.application.archunittest.dto.response;

import java.time.Instant;

/**
 * ArchUnitTestResult - ArchUnit 테스트 조회 결과
 *
 * <p>ArchUnit 테스트 도메인 데이터를 Application 레이어에서 전달하기 위한 DTO입니다.
 *
 * @param archUnitTestId ArchUnit 테스트 ID
 * @param structureId 패키지 구조 ID (필수)
 * @param code 테스트 코드 식별자
 * @param name 테스트 이름
 * @param description 테스트 설명 (nullable)
 * @param testClassName 테스트 클래스 이름 (nullable)
 * @param testMethodName 테스트 메서드 이름 (nullable)
 * @param testCode 테스트 코드 내용
 * @param severity 심각도 (nullable)
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 * @author ryu-qqq
 * @since 1.0.0
 */
public record ArchUnitTestResult(
        Long archUnitTestId,
        Long structureId,
        String code,
        String name,
        String description,
        String testClassName,
        String testMethodName,
        String testCode,
        String severity,
        Instant createdAt,
        Instant updatedAt) {}
