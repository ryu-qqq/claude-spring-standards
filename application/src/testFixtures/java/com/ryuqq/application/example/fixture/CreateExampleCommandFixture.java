package com.ryuqq.application.example.fixture;

import com.ryuqq.application.example.dto.command.CreateExampleCommand;

/**
 * CreateExampleCommand 테스트 Fixture
 *
 * <p>테스트에서 CreateExampleCommand 객체를 쉽게 생성하기 위한 Factory 클래스입니다.</p>
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
 * CreateExampleCommand command = CreateExampleCommandFixture.create();
 *
 * // 특정 메시지로 생성
 * CreateExampleCommand command = CreateExampleCommandFixture.createWithMessage("Custom Message");
 * }</pre>
 *
 * @author Claude Code
 * @since 1.0.0
 * @see CreateExampleCommand
 */
public class CreateExampleCommandFixture {

    /**
     * 기본값으로 CreateExampleCommand 생성
     *
     * @return 기본 메시지 "Test Message"를 가진 CreateExampleCommand
     */
    public static CreateExampleCommand create() {
        return createWithMessage("Test Message");
    }

    /**
     * 특정 메시지로 CreateExampleCommand 생성
     *
     * @param message 메시지 내용
     * @return 지정된 메시지를 가진 CreateExampleCommand
     */
    public static CreateExampleCommand createWithMessage(String message) {
        return new CreateExampleCommand(message);
    }

    /**
     * 여러 개의 CreateExampleCommand 생성 (대량 생성 테스트용)
     *
     * @param count 생성할 개수
     * @return CreateExampleCommand 배열
     */
    public static CreateExampleCommand[] createMultiple(int count) {
        CreateExampleCommand[] commands = new CreateExampleCommand[count];
        for (int i = 0; i < count; i++) {
            commands[i] = createWithMessage("Test Message " + (i + 1));
        }
        return commands;
    }

    // Private 생성자 - Utility 클래스이므로 인스턴스화 방지
    private CreateExampleCommandFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
