package com.ryuqq.application.example.port.in;

import com.ryuqq.application.example.dto.command.UpdateExampleCommand;
import com.ryuqq.application.example.dto.response.ExampleResponse;

/**
 * UpdateExampleUseCase - Example 수정 UseCase
 *
 * <p>CQRS 패턴의 Command 처리를 담당하는 Inbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Example 수정 요청 처리</li>
 *   <li>수정 결과 반환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * @RestController
 * public class ExampleController {
 *     private final UpdateExampleUseCase updateExampleUseCase;
 *
 *     @PutMapping("/api/v1/examples/{id}")
 *     public ResponseEntity<ApiResponse<ExampleResponse>> update(
 *             @PathVariable Long id,
 *             @RequestBody @Valid ExampleRequest request) {
 *         UpdateExampleCommand command = UpdateExampleCommand.of(id, request.message());
 *         ExampleResponse response = updateExampleUseCase.execute(command);
 *         return ResponseEntity.ok(ApiResponse.ofSuccess(response));
 *     }
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
public interface UpdateExampleUseCase {

    /**
     * Example 수정 실행
     *
     * @param command 수정 명령
     * @return 수정된 Example 응답
     */
    ExampleResponse execute(UpdateExampleCommand command);
}
