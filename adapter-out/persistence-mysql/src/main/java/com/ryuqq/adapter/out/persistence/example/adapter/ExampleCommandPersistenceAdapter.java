package com.ryuqq.adapter.out.persistence.example.adapter;

import com.ryuqq.adapter.out.persistence.example.entity.ExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.example.mapper.ExampleEntityMapper;
import com.ryuqq.adapter.out.persistence.example.repository.ExampleRepository;
import com.ryuqq.application.example.port.out.ExampleCommandOutPort;
import com.ryuqq.domain.example.ExampleDomain;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ExampleCommandPersistenceAdapter - Example Command Persistence Adapter
 *
 * <p>CQRS 패턴의 Command 작업을 처리하는 Persistence Adapter입니다.</p>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong></p>
 * <ul>
 *   <li>Outbound Adapter (Driven Adapter)</li>
 *   <li>Application Layer의 ExampleCommandOutPort 구현</li>
 *   <li>Infrastructure 세부사항 캡슐화 (JPA, MySQL)</li>
 * </ul>
 *
 * <p><strong>CQRS 책임:</strong></p>
 * <ul>
 *   <li>Command 작업만 처리 (저장, 수정, 삭제)</li>
 *   <li>데이터 변경 작업 담당</li>
 *   <li>Query 작업은 ExampleQueryPersistenceAdapter 사용</li>
 * </ul>
 *
 * <p><strong>트랜잭션 관리:</strong></p>
 * <ul>
 *   <li>트랜잭션은 Application Layer에서만 관리</li>
 *   <li>Persistence Layer는 트랜잭션 경계 없음</li>
 *   <li>Application Layer의 @Transactional 내에서 호출됨</li>
 * </ul>
 *
 * <p><strong>불변 Entity 전략:</strong></p>
 * <ul>
 *   <li>JPA Entity에 setter 메서드 없음</li>
 *   <li>수정 시 Domain → Entity 변환 후 save()</li>
 *   <li>불변성 원칙 준수</li>
 * </ul>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>Domain → Entity 변환 (Mapper)</li>
 *   <li>Repository를 통한 데이터베이스 작업</li>
 *   <li>Entity → Domain 변환 (Mapper)</li>
 *   <li>Application Layer로 반환</li>
 * </ol>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Component
public class ExampleCommandPersistenceAdapter implements ExampleCommandOutPort {

    private final ExampleRepository exampleRepository;
    private final ExampleEntityMapper entityMapper;

    /**
     * ExampleCommandPersistenceAdapter 생성자
     *
     * <p>Constructor Injection을 통해 의존성을 주입받습니다.</p>
     *
     * @param exampleRepository Example JPA Repository
     * @param entityMapper Entity-Domain 변환 Mapper
     */
    public ExampleCommandPersistenceAdapter(
        ExampleRepository exampleRepository,
        ExampleEntityMapper entityMapper
    ) {
        this.exampleRepository = exampleRepository;
        this.entityMapper = entityMapper;
    }

    /**
     * Example 저장 (생성, 수정, 삭제 모두 처리)
     *
     * <p><strong>처리 시나리오:</strong></p>
     * <ul>
     *   <li><strong>생성:</strong> ID가 null → JPA persist (INSERT)</li>
     *   <li><strong>수정:</strong> ID가 있고 status가 ACTIVE/INACTIVE → JPA merge (UPDATE)</li>
     *   <li><strong>삭제:</strong> ID가 있고 status가 DELETED → JPA merge (UPDATE, 논리적 삭제)</li>
     * </ul>
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>Domain을 Entity로 변환</li>
     *   <li>Repository.save() 호출 (JPA persist 또는 merge)</li>
     *   <li>저장된 Entity를 Domain으로 변환하여 반환</li>
     * </ol>
     *
     * <p><strong>ID 할당 처리:</strong></p>
     * <ul>
     *   <li>ID가 null이면 신규 생성 → JPA IDENTITY 전략으로 ID 자동 할당</li>
     *   <li>ID가 있으면 수정 → 기존 레코드 업데이트</li>
     * </ul>
     *
     * <p><strong>트랜잭션 컨텍스트:</strong></p>
     * <ul>
     *   <li>Application Layer의 @Transactional 내에서 호출</li>
     *   <li>이 메서드 실행 후 트랜잭션 커밋 시 실제 DB 반영</li>
     * </ul>
     *
     * <p><strong>불변성 원칙:</strong></p>
     * <ul>
     *   <li>모든 상태 변경은 Domain Layer에서 처리</li>
     *   <li>Persistence Layer는 단순히 변환 및 저장만 담당</li>
     *   <li>Entity에 setter 메서드 없음 (불변 패턴)</li>
     * </ul>
     *
     * @param domain 저장할 Example 도메인 (생성/수정/삭제 모두 가능)
     * @return 저장된 Example 도메인 (ID 할당됨)
     */
    @Override
    public ExampleDomain save(ExampleDomain domain) {
        // 1. Domain → Entity 변환
        ExampleJpaEntity entity = entityMapper.toEntity(domain);

        // 2. Entity 저장 (신규 생성 또는 수정)
        ExampleJpaEntity savedEntity = exampleRepository.save(entity);

        // 3. Entity → Domain 변환하여 반환
        return entityMapper.toDomain(savedEntity);
    }
}
