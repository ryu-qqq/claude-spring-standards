package com.ryuqq.adapter.out.persistence.example.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.example.entity.ExampleJpaEntity;
import com.ryuqq.adapter.out.persistence.example.entity.QExampleJpaEntity;
import com.ryuqq.application.example.dto.query.SearchExampleQuery;
import com.ryuqq.domain.example.ExampleStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ExampleQueryDslRepository - Example QueryDSL Repository
 *
 * <p>QueryDSL 기반 복잡한 쿼리를 처리하는 전용 Repository입니다.</p>
 *
 * <p><strong>패키지 분리 이유:</strong></p>
 * <ul>
 *   <li>관심사 분리: 복잡한 QueryDSL 로직을 Adapter와 분리</li>
 *   <li>유지보수성: 쿼리 로직만 독립적으로 관리</li>
 *   <li>테스트 용이성: QueryDSL 로직만 단위 테스트 가능</li>
 * </ul>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>동적 쿼리 구성 (BooleanExpression)</li>
 *   <li>정렬 조건 구성 (OrderSpecifier)</li>
 *   <li>Cursor/Offset 기반 페이징</li>
 *   <li>검색 조건 빌더 메서드</li>
 * </ul>
 *
 * <p><strong>사용 패턴:</strong></p>
 * <pre>{@code
 * @Component
 * public class ExampleQueryPersistenceAdapter {
 *     private final ExampleQueryDslRepository queryDslRepository;
 *
 *     public List<ExampleDomain> search(SearchExampleQuery query) {
 *         List<ExampleJpaEntity> entities = queryDslRepository.search(query);
 *         return entities.stream().map(mapper::toDomain).toList();
 *     }
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 * @see com.ryuqq.adapter.out.persistence.example.adapter.ExampleQueryPersistenceAdapter
 */
@Repository
public class ExampleQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * QueryDSL QType - Example Entity
     */
    private static final QExampleJpaEntity qExample = QExampleJpaEntity.exampleJpaEntity;

    /**
     * ExampleQueryDslRepository 생성자
     *
     * @param queryFactory JPAQueryFactory (QueryDSL)
     */
    public ExampleQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * Example 목록 조회 (Offset 기반 페이징)
     *
     * <p><strong>QueryDSL 동적 쿼리 구성:</strong></p>
     * <ol>
     *   <li>WHERE 조건 구성 (message 검색, status 필터)</li>
     *   <li>ORDER BY 구성 (sortBy, sortDirection)</li>
     *   <li>OFFSET, LIMIT 적용 (page, size)</li>
     * </ol>
     *
     * <p><strong>Offset 계산:</strong></p>
     * <pre>{@code
     * offset = page * size
     * // 예: page=1, size=20 → offset=20 (21번째 레코드부터 조회)
     * }</pre>
     *
     * @param query 검색 조건 (page, size, sortBy, sortDirection, message, status)
     * @return ExampleJpaEntity 목록
     */
    public List<ExampleJpaEntity> search(SearchExampleQuery query) {
        return queryFactory
            .selectFrom(qExample)
            .where(
                buildSearchConditions(query)
            )
            .orderBy(
                buildOrderSpecifier(query)
            )
            .offset((long) query.page() * query.size())
            .limit(query.size())
            .fetch();
    }

    /**
     * Example 총 개수 조회 (Offset 기반 페이징용)
     *
     * <p><strong>COUNT 쿼리:</strong></p>
     * <ul>
     *   <li>검색 조건만 적용 (page, size 무시)</li>
     *   <li>정렬 조건 불필요</li>
     *   <li>성능 비용 존재 (대량 데이터 시 느림)</li>
     * </ul>
     *
     * @param query 검색 조건 (message, status만 사용)
     * @return 검색 조건에 맞는 총 개수
     */
    public long countBy(SearchExampleQuery query) {
        Long count = queryFactory
            .select(qExample.count())
            .from(qExample)
            .where(
                buildSearchConditions(query)
            )
            .fetchOne();

        return count != null ? count : 0L;
    }

    /**
     * Example 목록 조회 (Cursor 기반 페이징)
     *
     * <p><strong>Cursor 기반 페이징 전략:</strong></p>
     * <ol>
     *   <li>size+1개 조회 (hasNext 판단용)</li>
     *   <li>cursor(마지막 ID)보다 큰 ID만 조회</li>
     *   <li>ID 오름차순 정렬</li>
     *   <li>Service Layer에서 hasNext 판단</li>
     * </ol>
     *
     * <p><strong>hasNext 판단 로직 (Service Layer):</strong></p>
     * <pre>{@code
     * List<ExampleDomain> results = queryOutPort.searchByCursor(query); // size+1개 반환
     * boolean hasNext = results.size() > query.size();
     * if (hasNext) {
     *     results = results.subList(0, query.size());  // 마지막 1개 제거
     * }
     * }</pre>
     *
     * <p><strong>성능 이점:</strong></p>
     * <ul>
     *   <li>COUNT 쿼리 불필요 (성능 향상)</li>
     *   <li>인덱스 활용 효율적 (ID 기반 조회)</li>
     *   <li>대량 데이터에 유리</li>
     * </ul>
     *
     * @param query 검색 조건 (cursor, size, sortBy, sortDirection, message, status)
     * @return ExampleJpaEntity 목록 (size+1개 반환, hasNext 판단용)
     */
    public List<ExampleJpaEntity> searchByCursor(SearchExampleQuery query) {
        return queryFactory
            .selectFrom(qExample)
            .where(
                buildSearchConditions(query),
                buildCursorCondition(query)
            )
            .orderBy(
                buildOrderSpecifier(query)
            )
            .limit(query.size() + 1)  // size+1개 조회 (hasNext 판단용)
            .fetch();
    }

    /**
     * 검색 조건 구성 (WHERE 절)
     *
     * <p><strong>동적 조건:</strong></p>
     * <ul>
     *   <li>message: LIKE 검색 ("%message%")</li>
     *   <li>status: 정확히 일치 (Enum 비교)</li>
     * </ul>
     *
     * @param query 검색 조건
     * @return BooleanExpression (null 가능 - 조건 없음)
     */
    private BooleanExpression buildSearchConditions(SearchExampleQuery query) {
        BooleanExpression expression = null;

        // message 검색 (LIKE)
        if (query.message() != null && !query.message().isBlank()) {
            expression = qExample.message.containsIgnoreCase(query.message());
        }

        // status 필터 (정확히 일치)
        if (query.status() != null && !query.status().isBlank()) {
            try {
                ExampleStatus status = ExampleStatus.valueOf(query.status().toUpperCase());
                BooleanExpression statusCondition = qExample.status.eq(status);
                expression = expression != null ? expression.and(statusCondition) : statusCondition;
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 status는 무시 (빈 결과 반환)
            }
        }

        return expression;
    }

    /**
     * Cursor 조건 구성 (Cursor 기반 페이징용)
     *
     * <p>cursor(마지막 ID)보다 큰 ID만 조회</p>
     *
     * @param query 검색 조건 (cursor 포함)
     * @return BooleanExpression (cursor가 없으면 null)
     */
    private BooleanExpression buildCursorCondition(SearchExampleQuery query) {
        if (query.cursor() == null || query.cursor().isBlank()) {
            return null;
        }

        try {
            Long cursorId = Long.parseLong(query.cursor());
            return qExample.id.gt(cursorId);  // ID > cursor
        } catch (NumberFormatException e) {
            // 유효하지 않은 cursor는 무시
            return null;
        }
    }

    /**
     * 정렬 조건 구성 (ORDER BY 절)
     *
     * <p><strong>지원 정렬 필드:</strong></p>
     * <ul>
     *   <li>"id": ID 기준 정렬</li>
     *   <li>"message": 메시지 기준 정렬</li>
     *   <li>"createdAt": 생성일시 기준 정렬</li>
     *   <li>"updatedAt": 수정일시 기준 정렬 (기본값)</li>
     * </ul>
     *
     * <p><strong>정렬 방향:</strong></p>
     * <ul>
     *   <li>"ASC": 오름차순</li>
     *   <li>"DESC": 내림차순 (기본값)</li>
     * </ul>
     *
     * @param query 검색 조건 (sortBy, sortDirection)
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> buildOrderSpecifier(SearchExampleQuery query) {
        String sortBy = query.sortBy() != null ? query.sortBy() : "updatedAt";
        boolean isAsc = "ASC".equalsIgnoreCase(query.sortDirection());

        return switch (sortBy.toLowerCase()) {
            case "id" -> isAsc ? qExample.id.asc() : qExample.id.desc();
            case "message" -> isAsc ? qExample.message.asc() : qExample.message.desc();
            case "createdat" -> isAsc ? qExample.createdAt.asc() : qExample.createdAt.desc();
            default -> isAsc ? qExample.updatedAt.asc() : qExample.updatedAt.desc();
        };
    }
}
