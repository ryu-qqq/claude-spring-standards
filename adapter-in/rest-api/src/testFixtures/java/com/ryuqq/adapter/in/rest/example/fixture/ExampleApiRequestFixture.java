package com.ryuqq.adapter.in.rest.example.fixture;

import com.ryuqq.adapter.in.rest.example.dto.request.ExampleApiRequest;

/**
 * ExampleApiRequest 테스트 Fixture
 *
 * <p>테스트에서 ExampleApiRequest 객체를 쉽게 생성하기 위한 Factory 클래스입니다.</p>
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
 * ExampleApiRequest request = ExampleApiRequestFixture.create();
 *
 * // 특정 메시지로 생성
 * ExampleApiRequest request = ExampleApiRequestFixture.createWithMessage("Custom Message");
 * }</pre>
 *
 * @author Claude Code
 * @since 1.0.0
 * @see ExampleApiRequest
 */
public class ExampleApiRequestFixture {

    /**
     * 기본값으로 ExampleApiRequest 생성
     *
     * @return 기본 메시지 "Test Message"를 가진 ExampleApiRequest
     */
    public static ExampleApiRequest create() {
        return createWithMessage("Test Message");
    }

    /**
     * 특정 메시지로 ExampleApiRequest 생성
     *
     * @param message 메시지 내용
     * @return 지정된 메시지를 가진 ExampleApiRequest
     */
    public static ExampleApiRequest createWithMessage(String message) {
        return new ExampleApiRequest(message);
    }

    /**
     * 여러 개의 ExampleApiRequest 생성 (대량 요청 테스트용)
     *
     * @param count 생성할 개수
     * @return ExampleApiRequest 배열
     */
    public static ExampleApiRequest[] createMultiple(int count) {
        ExampleApiRequest[] requests = new ExampleApiRequest[count];
        for (int i = 0; i < count; i++) {
            requests[i] = createWithMessage("Test Message " + (i + 1));
        }
        return requests;
    }

    // Private 생성자 - Utility 클래스이므로 인스턴스화 방지
    private ExampleApiRequestFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
