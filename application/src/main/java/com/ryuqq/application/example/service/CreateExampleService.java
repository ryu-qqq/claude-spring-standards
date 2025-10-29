package com.ryuqq.application.example.service;

import com.ryuqq.application.example.assembler.ExampleAssembler;
import com.ryuqq.application.example.dto.command.CreateExampleCommand;
import com.ryuqq.application.example.dto.response.ExampleResponse;
import com.ryuqq.application.example.port.in.CreateExampleUseCase;
import com.ryuqq.application.example.port.out.ExampleCommandOutPort;
import com.ryuqq.domain.example.ExampleDomain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateExampleService - Example 생성 서비스
 *
 * <p>CQRS 패턴의 Command 처리를 담당하는 Application Service입니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Example 생성 비즈니스 로직 처리</li>
 *   <li>트랜잭션 경계 관리 (@Transactional)</li>
 *   <li>도메인 객체와 DTO 변환 조율 (Assembler 활용)</li>
 * </ul>
 *
 * <p><strong>트랜잭션 전략:</strong></p>
 * <ul>
 *   <li>Command 작업은 @Transactional 필수</li>
 *   <li>외부 API 호출은 트랜잭션 밖에서 처리</li>
 *   <li>읽기 전용이 아닌 쓰기 작업</li>
 * </ul>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>CreateExampleCommand → ExampleDomain 변환 (Assembler)</li>
 *   <li>ExampleDomain 저장 (OutPort를 통해 Persistence Layer 호출)</li>
 *   <li>저장된 ExampleDomain → ExampleResponse 변환 (Assembler)</li>
 * </ol>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Service
public class CreateExampleService implements CreateExampleUseCase {

    private final ExampleAssembler exampleAssembler;
    private final ExampleCommandOutPort commandOutPort;

    /**
     * CreateExampleService 생성자
     *
     * <p>Constructor Injection을 통해 의존성을 주입받습니다.</p>
     *
     * @param exampleAssembler Domain-DTO 변환 Assembler
     * @param commandOutPort Example 저장 Command OutPort
     */
    public CreateExampleService(
            ExampleAssembler exampleAssembler,
            ExampleCommandOutPort commandOutPort) {
        this.exampleAssembler = exampleAssembler;
        this.commandOutPort = commandOutPort;
    }

    /**
     * Example 생성 실행
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>Command → Domain 변환</li>
     *   <li>Domain 저장 (Database Write)</li>
     *   <li>Domain → Response 변환</li>
     * </ul>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>@Transactional 내에서 외부 API 호출 금지</li>
     *   <li>트랜잭션은 짧게 유지</li>
     *   <li>Long FK 전략 준수 (JPA 관계 어노테이션 사용 금지)</li>
     * </ul>
     *
     * @param command Example 생성 명령
     * @return 생성된 Example 응답
     */
    @Transactional
    @Override
    public ExampleResponse execute(CreateExampleCommand command) {
        // 1. Command → Domain 변환 (Assembler)
        ExampleDomain domain = exampleAssembler.toDomain(command);

        // 2. Domain 저장 (CommandOutPort를 통해 Persistence Layer 호출)
        ExampleDomain savedDomain = commandOutPort.save(domain);

        // 3. Domain → Response 변환 (Assembler)
        return exampleAssembler.toResponse(savedDomain);
    }
}
