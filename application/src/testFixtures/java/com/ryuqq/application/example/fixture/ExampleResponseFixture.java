package com.ryuqq.application.example.fixture;

import com.ryuqq.application.example.dto.response.ExampleResponse;

/**
 * ExampleResponse 테스트 Fixture
 *
 * <p>테스트에서 ExampleResponse 객체를 쉽게 생성하기 위한 Factory 클래스입니다.</p>
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
 * ExampleResponse response = ExampleResponseFixture.create();
 *
 * // 특정 ID와 메시지로 생성
 * ExampleResponse response = ExampleResponseFixture.createWithIdAndMessage(123L, "Custom Message");
 * }</pre>
 *
 * @author Claude Code
 * @since 1.0.0
 * @see ExampleResponse
 */
public class ExampleResponseFixture {

    /**
     * 기본값으로 ExampleResponse 생성
     *
     * @return 기본 ID 1L, 메시지 "Test Message"를 가진 ExampleResponse
     */
    public static ExampleResponse create() {
        return createWithIdAndMessage(1L, "Test Message");
    }

    /**
     * 특정 ID로 ExampleResponse 생성
     *
     * @param id Example ID
     * @return 지정된 ID와 기본 메시지를 가진 ExampleResponse
     */
    public static ExampleResponse createWithId(Long id) {
        return createWithIdAndMessage(id, "Test Message");
    }

    /**
     * 특정 메시지로 ExampleResponse 생성
     *
     * @param message 메시지 내용
     * @return 기본 ID와 지정된 메시지를 가진 ExampleResponse
     */
    public static ExampleResponse createWithMessage(String message) {
        return createWithIdAndMessage(1L, message);
    }

    /**
     * 특정 ID와 메시지로 ExampleResponse 생성
     *
     * @param id Example ID
     * @param message 메시지 내용
     * @return 지정된 ID와 메시지를 가진 ExampleResponse
     */
    public static ExampleResponse createWithIdAndMessage(Long id, String message) {
        return new ExampleResponse(id, message);
    }

    /**
     * 여러 개의 ExampleResponse 생성 (목록 테스트용)
     *
     * @param startId 시작 ID
     * @param count 생성할 개수
     * @return ExampleResponse 배열
     */
    public static ExampleResponse[] createMultiple(long startId, int count) {
        ExampleResponse[] responses = new ExampleResponse[count];
        for (int i = 0; i < count; i++) {
            responses[i] = createWithIdAndMessage(
                startId + i,
                "Test Message " + (i + 1)
            );
        }
        return responses;
    }

    // Private 생성자 - Utility 클래스이므로 인스턴스화 방지
    private ExampleResponseFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
