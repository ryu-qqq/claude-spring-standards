package com.ryuqq.application.example.dto.command;

/**
 * UpdateExampleCommand - Example 수정 명령
 *
 * <p>CQRS 패턴의 Command 역할을 수행합니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>기존 Example 수정</li>
 *   <li>수정 시 필요한 필드 포함 (ID + 수정할 필드)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * UpdateExampleCommand command = UpdateExampleCommand.of(1L, "Updated Message");
 * ExampleResponse response = updateExampleUseCase.execute(command);
 * }</pre>
 *
 * @param id Example ID
 * @param message 수정할 메시지 내용
 * @author windsurf
 * @since 1.0.0
 */
public record UpdateExampleCommand(
    Long id,
    String message
) {

    /**
     * UpdateExampleCommand 생성
     *
     * @param id Example ID
     * @param message 수정할 메시지 내용
     * @return UpdateExampleCommand
     */
    public static UpdateExampleCommand of(Long id, String message) {
        return new UpdateExampleCommand(id, message);
    }
}
