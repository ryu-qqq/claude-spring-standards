package com.ryuqq.application.example.port.in;

import com.ryuqq.application.example.dto.command.CreateExampleCommand;
import com.ryuqq.application.example.dto.response.ExampleResponse;

/**
 * CreateExampleUseCase - Example 생성 UseCase
 *
 * <p>CQRS 패턴의 Command 처리를 담당하는 Inbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Example 생성 요청 처리</li>
 *   <li>생성 결과 반환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * @RestController
 * public class ExampleController {
 *     private final CreateExampleUseCase createExampleUseCase;
 *
 *     @PostMapping("/api/v1/examples")
 *     public ResponseEntity<ApiResponse<ExampleResponse>> create(@RequestBody @Valid ExampleRequest request) {
 *         CreateExampleCommand command = CreateExampleCommand.of(request.message());
 *         ExampleResponse response = createExampleUseCase.execute(command);
 *         return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(response));
 *     }
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
public interface CreateExampleUseCase {

    /**
     * Example 생성 실행
     *
     * @param command 생성 명령
     * @return 생성된 Example 응답
     */
    ExampleResponse execute(CreateExampleCommand command);
}
