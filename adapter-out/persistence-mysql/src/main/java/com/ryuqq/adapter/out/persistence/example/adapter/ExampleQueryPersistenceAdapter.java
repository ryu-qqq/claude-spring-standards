package com.ryuqq.adapter.out.persistence.example.adapter;

import com.ryuqq.adapter.out.persistence.example.entity.ExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.example.mapper.ExampleEntityMapper;
import com.ryuqq.adapter.out.persistence.example.querydsl.ExampleQueryDslRepository;
import com.ryuqq.adapter.out.persistence.example.repository.ExampleRepository;
import com.ryuqq.application.example.dto.query.SearchExampleQuery;
import com.ryuqq.application.example.port.out.ExampleQueryOutPort;
import com.ryuqq.domain.example.ExampleDomain;
import com.ryuqq.domain.example.exception.ExampleNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ExampleQueryPersistenceAdapter - Example Query Persistence Adapter
 *
 * <p>CQRS 패턴의 Query 작업을 처리하는 Persistence Adapter입니다.</p>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong></p>
 * <ul>
 *   <li>Outbound Adapter (Driven Adapter)</li>
 *   <li>Application Layer의 ExampleQueryOutPort 구현</li>
 *   <li>Infrastructure 세부사항 캡슐화 (JPA, QueryDSL, MySQL)</li>
 * </ul>
 *
 * <p><strong>CQRS 책임:</strong></p>
 * <ul>
 *   <li>Query 작업만 처리 (단건 조회, 목록 조회, 검색)</li>
 *   <li>읽기 전용 작업 담당</li>
 *   <li>Command 작업은 ExampleCommandPersistenceAdapter 사용</li>
 * </ul>
 *
 * <p><strong>트랜잭션 관리:</strong></p>
 * <ul>
 *   <li>트랜잭션은 Application Layer에서만 관리</li>
 *   <li>Persistence Layer는 트랜잭션 경계 없음</li>
 *   <li>Application Layer의 @Transactional(readOnly = true) 내에서 호출됨</li>
 * </ul>
 *
 * <p><strong>QueryDSL 활용:</strong></p>
 * <ul>
 *   <li>타입 안전 쿼리 작성</li>
 *   <li>동적 쿼리 구성 (검색 조건, 정렬)</li>
 *   <li>Offset/Cursor 기반 페이징 지원</li>
 *   <li>복잡한 쿼리 로직은 ExampleQueryDslRepository로 분리</li>
 * </ul>
 *
 * <p><strong>페이지네이션 전략:</strong></p>
 * <ul>
 *   <li>Offset 기반: search() + countBy() 조합</li>
 *   <li>Cursor 기반: searchByCursor() (size+1 조회)</li>
 * </ul>
 *
 * <p><strong>패키지 분리:</strong></p>
 * <ul>
 *   <li>Adapter: Port 구현 및 Entity-Domain 변환만 담당</li>
 *   <li>QueryDslRepository: 복잡한 QueryDSL 로직 분리</li>
 *   <li>관심사 분리로 유지보수성 향상</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Component
public class ExampleQueryPersistenceAdapter implements ExampleQueryOutPort {

    private final ExampleRepository exampleRepository;
    private final ExampleEntityMapper entityMapper;
    private final ExampleQueryDslRepository queryDslRepository;

    /**
     * ExampleQueryPersistenceAdapter 생성자
     *
     * <p>Constructor Injection을 통해 의존성을 주입받습니다.</p>
     *
     * @param exampleRepository Example JPA Repository
     * @param entityMapper Entity-Domain 변환 Mapper
     * @param queryDslRepository Example QueryDSL Repository (복잡한 쿼리 처리)
     */
    public ExampleQueryPersistenceAdapter(
        ExampleRepository exampleRepository,
        ExampleEntityMapper entityMapper,
        ExampleQueryDslRepository queryDslRepository
    ) {
        this.exampleRepository = exampleRepository;
        this.entityMapper = entityMapper;
        this.queryDslRepository = queryDslRepository;
    }

    /**
     * Example ID로 단건 조회
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>Repository.findById() 호출 (Spring Data JPA)</li>
     *   <li>Entity → Domain 변환</li>
     *   <li>Optional<Domain> 반환</li>
     * </ol>
     *
     * @param id Example ID
     * @return 조회된 Example 도메인 (Optional)
     */
    @Override
    public Optional<ExampleDomain> findById(Long id) {
        return exampleRepository.findById(id)
            .map(entityMapper::toDomain);
    }

    /**
     * Example ID로 단건 조회 (예외 발생 버전)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>Repository.findById() 호출 (Spring Data JPA)</li>
     *   <li>존재하지 않으면 ExampleNotFoundException 발생</li>
     *   <li>Entity → Domain 변환</li>
     * </ol>
     *
     * <p><strong>사용 시나리오:</strong></p>
     * <ul>
     *   <li>UseCase에서 Example이 반드시 존재해야 하는 경우</li>
     *   <li>Update, Delete 작업의 전제 조건으로 사용</li>
     *   <li>비즈니스 규칙상 존재하지 않으면 예외 처리 필요한 경우</li>
     * </ul>
     *
     * @param id Example ID
     * @return 조회된 Example 도메인
     * @throws ExampleNotFoundException Example을 찾을 수 없을 때
     */
    public ExampleDomain findByIdOrThrow(Long id) {
        return exampleRepository.findById(id)
            .map(entityMapper::toDomain)
            .orElseThrow(() -> new ExampleNotFoundException(id));
    }

    /**
     * Example 목록 조회 (Offset 기반 페이징)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>QueryDslRepository를 통한 Entity 조회</li>
     *   <li>Entity 목록 → Domain 목록 변환</li>
     * </ol>
     *
     * <p><strong>QueryDSL 로직:</strong></p>
     * <ul>
     *   <li>WHERE 조건, ORDER BY, OFFSET, LIMIT은 QueryDslRepository에서 처리</li>
     *   <li>Adapter는 변환만 담당 (관심사 분리)</li>
     * </ul>
     *
     * @param query 검색 조건 (page, size, sortBy, sortDirection, message, status)
     * @return Example 도메인 목록
     */
    @Override
    public List<ExampleDomain> search(SearchExampleQuery query) {
        // QueryDslRepository를 통한 Entity 조회
        List<ExampleJpaEntity> entities = queryDslRepository.search(query);

        // Entity 목록 → Domain 목록 변환
        return entities.stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * Example 총 개수 조회 (Offset 기반 페이징용)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ul>
     *   <li>QueryDslRepository를 통한 COUNT 쿼리 실행</li>
     *   <li>검색 조건만 적용 (page, size 무시)</li>
     * </ul>
     *
     * @param query 검색 조건 (message, status만 사용)
     * @return 검색 조건에 맞는 총 개수
     */
    @Override
    public long countBy(SearchExampleQuery query) {
        return queryDslRepository.countBy(query);
    }

    /**
     * Example 목록 조회 (Cursor 기반 페이징)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>QueryDslRepository를 통한 Entity 조회 (size+1개)</li>
     *   <li>Entity 목록 → Domain 목록 변환</li>
     * </ol>
     *
     * <p><strong>hasNext 판단:</strong></p>
     * <ul>
     *   <li>Service Layer에서 results.size() > query.size() 확인</li>
     *   <li>true면 마지막 1개 제거하여 반환</li>
     * </ul>
     *
     * @param query 검색 조건 (cursor, size, sortBy, sortDirection, message, status)
     * @return Example 도메인 목록 (size+1개 반환, hasNext 판단용)
     */
    @Override
    public List<ExampleDomain> searchByCursor(SearchExampleQuery query) {
        // QueryDslRepository를 통한 Entity 조회
        List<ExampleJpaEntity> entities = queryDslRepository.searchByCursor(query);

        // Entity 목록 → Domain 목록 변환
        return entities.stream()
            .map(entityMapper::toDomain)
            .collect(Collectors.toList());
    }
}
