package com.ryuqq.application.example.assembler;

import com.ryuqq.application.example.dto.command.CreateExampleCommand;
import com.ryuqq.application.example.dto.command.ExampleCommand;
import com.ryuqq.application.example.dto.response.ExampleDetailResponse;
import com.ryuqq.application.example.dto.response.ExampleResponse;
import com.ryuqq.domain.example.ExampleDomain;

import org.springframework.stereotype.Component;

/**
 * ExampleAssembler - Example 도메인과 DTO 간 변환
 *
 * <p>Hexagonal Architecture의 Assembler 패턴을 적용하여
 * Command/Query DTO를 Domain 객체로, Domain 객체를 Response DTO로 변환합니다.</p>
 *
 * <p><strong>CQRS 패턴 적용:</strong></p>
 * <ul>
 *   <li>Command 변환: CreateExampleCommand → ExampleDomain</li>
 *   <li>Query 응답 변환: ExampleDomain → Response DTOs</li>
 * </ul>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>레이어 간 데이터 변환 (Application ↔ Domain)</li>
 *   <li>도메인 객체 생성 로직 캡슐화</li>
 *   <li>DTO 변환 로직 중앙 관리</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Component
public class ExampleAssembler {

    /**
     * CreateExampleCommand를 ExampleDomain으로 변환
     *
     * <p>신규 생성이므로 ID는 null로 설정합니다.
     * OutPort에서 저장 후 ID가 할당됩니다.</p>
     *
     * @param command Example 생성 명령
     * @return ExampleDomain (ID=null, status=ACTIVE, 현재 시각)
     */
    public ExampleDomain toDomain(CreateExampleCommand command) {
        return ExampleDomain.create(command.message());
    }

    /**
     * ExampleCommand를 ExampleDomain으로 변환 (Legacy)
     *
     * <p>기존 ExampleService와의 호환성을 위해 유지합니다.</p>
     *
     * @param exampleCommand Example 명령
     * @return ExampleDomain
     * @deprecated CreateExampleCommand 사용 권장
     */
    @Deprecated(since = "1.0.0", forRemoval = true)
    public ExampleDomain toDomain(ExampleCommand exampleCommand) {
        return ExampleDomain.of(
            exampleCommand.id(),
            exampleCommand.message()
        );
    }

    /**
     * ExampleDomain을 ExampleResponse로 변환 (Legacy)
     *
     * <p>기존 ExampleService와의 호환성을 위해 유지합니다.</p>
     *
     * @param exampleDomain Example 도메인
     * @return ExampleResponse
     * @deprecated ExampleDetailResponse 사용 권장
     */
    @Deprecated(since = "1.0.0", forRemoval = true)
    public ExampleResponse toResponse(ExampleDomain exampleDomain) {
        return ExampleResponse.of(
            exampleDomain.getId(),
            exampleDomain.getMessage()
        );
    }

    /**
     * ExampleDomain을 ExampleDetailResponse로 변환
     *
     * <p>조회 결과를 상세 응답 DTO로 변환합니다.
     * CQRS Query 응답에 사용됩니다.</p>
     *
     * @param domain Example 도메인
     * @return ExampleDetailResponse
     */
    public ExampleDetailResponse toDetailResponse(ExampleDomain domain) {
        return ExampleDetailResponse.of(
            domain.getId(),
            domain.getMessage(),
            domain.getStatus(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }

}
