package com.ryuqq.application.archunittest.dto.command;

/**
 * UpdateArchUnitTestCommand - ArchUnit 테스트 수정 커맨드
 *
 * <p>ArchUnit 테스트 수정에 필요한 데이터를 전달합니다.
 *
 * @param archUnitTestId 수정할 ArchUnit 테스트 ID
 * @param code 테스트 코드 식별자 (예: ARCH-001)
 * @param name 테스트 이름
 * @param description 테스트 설명
 * @param testClassName 테스트 클래스 이름
 * @param testMethodName 테스트 메서드 이름
 * @param testCode 테스트 코드 내용
 * @param severity 심각도 (BLOCKER, CRITICAL, MAJOR, MINOR, INFO)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record UpdateArchUnitTestCommand(
        Long archUnitTestId,
        String code,
        String name,
        String description,
        String testClassName,
        String testMethodName,
        String testCode,
        String severity) {}
