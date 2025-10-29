package com.ryuqq.application.example.dto.command;

/**
 * CreateExampleCommand - Example 생성 명령
 *
 * <p>CQRS 패턴의 Command 역할을 수행합니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>새로운 Example 생성</li>
 *   <li>생성 시 필요한 필드만 포함 (ID 제외)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * CreateExampleCommand command = CreateExampleCommand.of("Hello World");
 * ExampleResponse response = createExampleUseCase.execute(command);
 * }</pre>
 *
 * @param message 메시지 내용
 * @author windsurf
 * @since 1.0.0
 */
public record CreateExampleCommand(
    String message
) {

    /**
     * CreateExampleCommand 생성
     *
     * @param message 메시지 내용
     * @return CreateExampleCommand
     */
    public static CreateExampleCommand of(String message) {
        return new CreateExampleCommand(message);
    }
}
