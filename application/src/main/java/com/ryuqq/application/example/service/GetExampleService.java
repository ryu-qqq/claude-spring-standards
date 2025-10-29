package com.ryuqq.application.example.service;

import com.ryuqq.application.example.assembler.ExampleAssembler;
import com.ryuqq.application.example.dto.query.GetExampleQuery;
import com.ryuqq.application.example.dto.response.ExampleDetailResponse;
import com.ryuqq.application.example.port.in.GetExampleQueryService;
import com.ryuqq.application.example.port.out.ExampleQueryOutPort;
import com.ryuqq.domain.example.ExampleDomain;
import com.ryuqq.domain.example.exception.ExampleNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetExampleService - Example 단건 조회 서비스
 *
 * <p>CQRS 패턴의 Query 처리를 담당하는 Application Service입니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Example ID로 단건 조회</li>
 *   <li>읽기 전용 트랜잭션 관리 (@Transactional(readOnly = true))</li>
 *   <li>도메인 객체를 상세 응답 DTO로 변환</li>
 * </ul>
 *
 * <p><strong>Query 전략:</strong></p>
 * <ul>
 *   <li>읽기 전용 트랜잭션으로 성능 최적화</li>
 *   <li>데이터 변경 불가 (Command와 분리)</li>
 *   <li>조회 결과 없을 시 Domain Exception 발생</li>
 * </ul>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>GetExampleQuery로 ID 추출</li>
 *   <li>OutPort를 통해 Domain 조회 (Persistence Layer 호출)</li>
 *   <li>ExampleDomain → ExampleDetailResponse 변환 (Assembler)</li>
 * </ol>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Service
public class GetExampleService implements GetExampleQueryService {

    private final ExampleAssembler exampleAssembler;
    private final ExampleQueryOutPort queryOutPort;

    /**
     * GetExampleService 생성자
     *
     * <p>Constructor Injection을 통해 의존성을 주입받습니다.</p>
     *
     * @param exampleAssembler Domain-DTO 변환 Assembler
     * @param queryOutPort Example 조회 Query OutPort
     */
    public GetExampleService(
            ExampleAssembler exampleAssembler,
            ExampleQueryOutPort queryOutPort) {
        this.exampleAssembler = exampleAssembler;
        this.queryOutPort = queryOutPort;
    }

    /**
     * Example ID로 단건 조회
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>Query 파라미터 추출</li>
     *   <li>Domain 조회 (Database Read)</li>
     *   <li>Domain → DetailResponse 변환</li>
     * </ul>
     *
     * <p><strong>예외 처리:</strong></p>
     * <ul>
     *   <li>조회 결과 없음 → ExampleNotFoundException 발생</li>
     *   <li>Domain Layer에서 예외 발생 → 그대로 전파</li>
     * </ul>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>readOnly = true로 성능 최적화</li>
     *   <li>데이터 변경 작업 금지</li>
     *   <li>Command와 명확히 분리</li>
     * </ul>
     *
     * @param query Example 조회 쿼리 (ID 포함)
     * @return Example 상세 응답
     * @throws ExampleNotFoundException Example을 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    @Override
    public ExampleDetailResponse getById(GetExampleQuery query) {
        // 1. Query에서 ID 추출
        Long id = query.id();

        // 2. QueryOutPort를 통해 Domain 조회
        ExampleDomain domain = queryOutPort.findById(id)
                .orElseThrow(() -> new ExampleNotFoundException(id));

        // 3. Domain → DetailResponse 변환 (Assembler)
        return exampleAssembler.toDetailResponse(domain);
    }
}
