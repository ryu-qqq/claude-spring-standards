package com.ryuqq.domain.example.fixture;

import com.ryuqq.domain.example.ExampleContent;
import com.ryuqq.domain.example.ExampleDomain;
import com.ryuqq.domain.example.ExampleId;

import java.time.LocalDateTime;

/**
 * ExampleDomain 테스트 Fixture
 *
 * <p>테스트에서 ExampleDomain 객체를 쉽게 생성하기 위한 Factory 클래스입니다.</p>
 *
 * <h3>네이밍 규칙:</h3>
 * <ul>
 *   <li>클래스명: {@code *Fixture} 접미사 필수</li>
 *   <li>기본 생성 메서드: {@code create*()} - 기본값으로 객체 생성</li>
 *   <li>커스터마이징 메서드: {@code create*With*()} - 특정 값 지정하여 생성</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * // 기본값으로 생성
 * ExampleDomain example = ExampleDomainFixture.create();
 *
 * // 특정 메시지로 생성
 * ExampleDomain example = ExampleDomainFixture.createWithMessage("Custom Message");
 *
 * // ID 포함하여 생성 (조회 시나리오)
 * ExampleDomain example = ExampleDomainFixture.createWithId(123L, "Message");
 * }</pre>
 *
 * @author Claude Code
 * @since 1.0.0
 * @see ExampleDomain
 */
public class ExampleDomainFixture {

    /**
     * 기본값으로 ExampleDomain 생성
     *
     * @return 기본 메시지 "Test Message"를 가진 ExampleDomain
     */
    public static ExampleDomain create() {
        return createWithMessage("Test Message");
    }

    /**
     * 특정 메시지로 ExampleDomain 생성
     *
     * @param message 메시지 내용
     * @return 지정된 메시지를 가진 ExampleDomain
     */
    public static ExampleDomain createWithMessage(String message) {
        return ExampleDomain.create(message);
    }

    /**
     * ID 포함하여 ExampleDomain 생성 (조회 시나리오용)
     *
     * <p>영속화된 상태의 Domain 객체를 테스트할 때 사용합니다.</p>
     *
     * @param id Example ID
     * @param message 메시지 내용
     * @return ID를 가진 ExampleDomain
     */
    public static ExampleDomain createWithId(Long id, String message) {
        LocalDateTime now = LocalDateTime.now();
        return ExampleDomain.of(
            id,
            message,
            "ACTIVE",
            now,  // createdAt
            now   // updatedAt
        );
    }

    /**
     * 여러 개의 ExampleDomain 생성 (목록 테스트용)
     *
     * @param count 생성할 개수
     * @return ExampleDomain 배열
     */
    public static ExampleDomain[] createMultiple(int count) {
        ExampleDomain[] examples = new ExampleDomain[count];
        for (int i = 0; i < count; i++) {
            examples[i] = createWithMessage("Test Message " + (i + 1));
        }
        return examples;
    }

    /**
     * ID를 포함한 여러 개의 ExampleDomain 생성
     *
     * @param startId 시작 ID
     * @param count 생성할 개수
     * @return ExampleDomain 배열
     */
    public static ExampleDomain[] createMultipleWithId(long startId, int count) {
        ExampleDomain[] examples = new ExampleDomain[count];
        for (int i = 0; i < count; i++) {
            examples[i] = createWithId(startId + i, "Test Message " + (i + 1));
        }
        return examples;
    }

    // Private 생성자 - Utility 클래스이므로 인스턴스화 방지
    private ExampleDomainFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
