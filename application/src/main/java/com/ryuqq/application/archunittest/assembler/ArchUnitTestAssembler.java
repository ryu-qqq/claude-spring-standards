package com.ryuqq.application.archunittest.assembler;

import com.ryuqq.application.archunittest.dto.response.ArchUnitTestResult;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestAssembler - ArchUnit 테스트 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestAssembler {

    /**
     * ArchUnitTest 도메인 객체를 ArchUnitTestResult로 변환
     *
     * @param archUnitTest ArchUnit 테스트 도메인 객체
     * @return ArchUnitTestResult
     */
    public ArchUnitTestResult toResult(ArchUnitTest archUnitTest) {
        return new ArchUnitTestResult(
                archUnitTest.id().value(),
                archUnitTest.structureId().value(),
                archUnitTest.code(),
                archUnitTest.name().value(),
                archUnitTest.description() != null ? archUnitTest.description().value() : null,
                archUnitTest.testClassName(),
                archUnitTest.testMethodName(),
                archUnitTest.testCode().value(),
                archUnitTest.severity() != null ? archUnitTest.severity().name() : null,
                archUnitTest.createdAt(),
                archUnitTest.updatedAt());
    }

    /**
     * ArchUnitTest 목록을 ArchUnitTestResult 목록으로 변환
     *
     * @param archUnitTests ArchUnit 테스트 도메인 객체 목록
     * @return ArchUnitTestResult 목록
     */
    public List<ArchUnitTestResult> toResults(List<ArchUnitTest> archUnitTests) {
        return archUnitTests.stream().map(this::toResult).toList();
    }

    /**
     * ArchUnitTest 목록을 ArchUnitTestSliceResult로 변환
     *
     * @param archUnitTests ArchUnit 테스트 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return ArchUnitTestSliceResult
     */
    public ArchUnitTestSliceResult toSliceResult(
            List<ArchUnitTest> archUnitTests, int requestedSize) {
        boolean hasNext = archUnitTests.size() > requestedSize;
        List<ArchUnitTest> resultArchUnitTests =
                hasNext ? archUnitTests.subList(0, requestedSize) : archUnitTests;
        List<ArchUnitTestResult> results = toResults(resultArchUnitTests);
        return ArchUnitTestSliceResult.of(results, requestedSize, hasNext);
    }
}
