-- ============================================
-- Spring Standards - Query Package & Criteria Templates
-- V8: 도메인 Query 패키지 구조 및 Criteria 템플릿 추가
-- Created: 2026-01-20
-- ============================================
-- 변경 사항:
--   - package_structure: {domain}.query 패키지 추가
--   - package_purpose: DOMAIN_QUERY 용도 정의
--   - class_template: CursorQueryContext, PageCriteria, CursorCriteria 추가
--   - coding_rule: Criteria 네이밍 및 사용 규칙
-- ============================================

-- ============================================
-- 1. package_structure - query 패키지 추가
-- ============================================

INSERT IGNORE INTO `package_structure` (
    `id`, `module_id`, `path_pattern`, `allowed_class_types`,
    `naming_pattern`, `naming_suffix`, `description`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    8, 1, '{base_package}.{domain}.query',
    '["RECORD", "CLASS"]',
    '*Criteria', 'Criteria',
    '도메인 조회 조건 패키지. PageCriteria 또는 CursorCriteria로 끝나는 조회 조건 객체가 위치합니다.',
    NOW(), NOW(), NULL
);

-- ============================================
-- 2. package_purpose - DOMAIN_QUERY 용도 정의
-- ============================================

INSERT INTO `package_purpose` (
    `id`, `structure_id`, `code`, `name`, `description`,
    `default_allowed_class_types`, `default_naming_pattern`, `default_naming_suffix`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    19, 8, 'DOMAIN_QUERY', '도메인 Query 패키지',
    '도메인별 조회 조건(Criteria)을 정의합니다. 페이지 기반은 *PageCriteria, 커서 기반은 *CursorCriteria 네이밍을 따릅니다. QueryContext 또는 CursorQueryContext를 필수로 포함하며, DateRange 등 공통 VO를 활용할 수 있습니다.',
    '["RECORD", "CLASS"]', '*Criteria', 'Criteria',
    NOW(), NOW(), NULL
);

-- ============================================
-- 3. class_template - CursorQueryContext 추가
-- ============================================

INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `description`, `created_at`, `updated_at`, `deleted_at`
) VALUES (
    19, 5, 'CURSOR_QUERY_CONTEXT_RECORD',
    '/**
 * CursorQueryContext - 커서 기반 조회 컨텍스트 VO
 *
 * <p>정렬 + 커서 페이지네이션 정보를 결합한 조회 컨텍스트를 나타내는 불변 Value Object입니다.
 * SortKey 타입과 Cursor 타입을 제네릭으로 지정하여 도메인별 정렬 키와 커서를 지원합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * CursorQueryContext<OrderSortKey, Long> context = CursorQueryContext.of(
 *     OrderSortKey.CREATED_AT,
 *     SortDirection.DESC,
 *     CursorPageRequest.afterId(100L)
 * );
 * boolean isFirst = context.isFirstPage();
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param <K> SortKey를 구현하는 정렬 키 타입
 * @param <C> 커서 타입
 * @param sortKey 정렬 키
 * @param sortDirection 정렬 방향
 * @param cursorPageRequest 커서 페이지 요청
 * @param includeDeleted 삭제된 항목 포함 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CursorQueryContext<K extends SortKey, C>(
        K sortKey,
        SortDirection sortDirection,
        CursorPageRequest<C> cursorPageRequest,
        boolean includeDeleted) {

    /**
     * Compact constructor - 유효성 검증
     */
    public CursorQueryContext {
        if (sortKey == null) {
            throw new IllegalArgumentException("SortKey must not be null");
        }
        if (sortDirection == null) {
            sortDirection = SortDirection.defaultDirection();
        }
        if (cursorPageRequest == null) {
            cursorPageRequest = CursorPageRequest.defaultPage();
        }
    }

    /**
     * 모든 파라미터로 CursorQueryContext를 생성합니다.
     */
    public static <K extends SortKey, C> CursorQueryContext<K, C> of(
            K sortKey, SortDirection sortDirection, CursorPageRequest<C> cursorPageRequest, boolean includeDeleted) {
        return new CursorQueryContext<>(sortKey, sortDirection, cursorPageRequest, includeDeleted);
    }

    /**
     * 기본 설정으로 CursorQueryContext를 생성합니다.
     */
    public static <K extends SortKey, C> CursorQueryContext<K, C> of(
            K sortKey, SortDirection sortDirection, CursorPageRequest<C> cursorPageRequest) {
        return new CursorQueryContext<>(sortKey, sortDirection, cursorPageRequest, false);
    }

    /**
     * 기본 정렬 방향과 첫 페이지로 CursorQueryContext를 생성합니다.
     */
    public static <K extends SortKey, C> CursorQueryContext<K, C> defaultOf(K sortKey) {
        return new CursorQueryContext<>(sortKey, SortDirection.defaultDirection(), CursorPageRequest.defaultPage(), false);
    }

    /**
     * 다음 페이지 CursorQueryContext를 반환합니다.
     */
    public CursorQueryContext<K, C> nextPage(C nextCursor) {
        return new CursorQueryContext<>(sortKey, sortDirection, cursorPageRequest.next(nextCursor), includeDeleted);
    }

    /**
     * 첫 페이지인지 확인합니다.
     */
    public boolean isFirstPage() {
        return cursorPageRequest.isFirstPage();
    }

    /**
     * 커서가 있는지 확인합니다.
     */
    public boolean hasCursor() {
        return cursorPageRequest.hasCursor();
    }

    /**
     * 커서 값을 반환합니다.
     */
    public C cursor() {
        return cursorPageRequest.cursor();
    }

    /**
     * 페이지 크기를 반환합니다.
     */
    public int size() {
        return cursorPageRequest.size();
    }

    /**
     * 실제 조회할 크기를 반환합니다 (hasNext 판단을 위해 +1).
     */
    public int fetchSize() {
        return cursorPageRequest.fetchSize();
    }

    /**
     * 오름차순인지 확인합니다.
     */
    public boolean isAscending() {
        return sortDirection.isAscending();
    }
}',
    'CursorQueryContext',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["sortKey", "sortDirection", "cursorPageRequest", "isFirstPage", "hasCursor", "cursor", "size"]',
    'CursorQueryContext - 커서 기반 조회 컨텍스트. CursorPageRequest를 포함합니다.',
    NOW(), NOW(), NULL
);

-- ============================================
-- 4. class_template - PageCriteria 템플릿
-- ============================================

INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `description`, `created_at`, `updated_at`, `deleted_at`
) VALUES (
    20, 8, 'PAGE_CRITERIA_RECORD',
    '/**
 * {Domain}PageCriteria - 페이지 기반 조회 조건 VO
 *
 * <p>페이지 기반 조회를 위한 조건을 담는 불변 Value Object입니다.
 * QueryContext를 필수로 포함하며, 도메인별 필터 조건을 추가합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * OrderPageCriteria criteria = OrderPageCriteria.of(
 *     queryContext,
 *     OrderStatus.COMPLETED,
 *     DateRange.lastDays(30)
 * );
 * }</pre>
 *
 * <p>QRY-001: Criteria는 반드시 QueryContext 또는 CursorQueryContext를 포함해야 합니다.
 *
 * <p>QRY-002: 날짜 범위 필터는 반드시 DateRange VO를 사용해야 합니다.
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param queryContext 조회 컨텍스트 (정렬 + 페이지네이션)
 * @param status 상태 필터 (선택)
 * @param dateRange 날짜 범위 필터 (선택)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record {Domain}PageCriteria<K extends SortKey>(
        QueryContext<K> queryContext,
        {Domain}Status status,
        DateRange dateRange) {

    /**
     * Compact constructor - 유효성 검증
     */
    public {Domain}PageCriteria {
        if (queryContext == null) {
            throw new IllegalArgumentException("QueryContext must not be null");
        }
    }

    /**
     * QueryContext만으로 Criteria를 생성합니다.
     */
    public static <K extends SortKey> {Domain}PageCriteria<K> of(QueryContext<K> queryContext) {
        return new {Domain}PageCriteria<>(queryContext, null, null);
    }

    /**
     * 모든 필터 조건으로 Criteria를 생성합니다.
     */
    public static <K extends SortKey> {Domain}PageCriteria<K> of(
            QueryContext<K> queryContext, {Domain}Status status, DateRange dateRange) {
        return new {Domain}PageCriteria<>(queryContext, status, dateRange);
    }

    /**
     * 상태 필터가 있는지 확인합니다.
     */
    public boolean hasStatusFilter() {
        return status != null;
    }

    /**
     * 날짜 범위 필터가 있는지 확인합니다.
     */
    public boolean hasDateRangeFilter() {
        return dateRange != null;
    }

    // QueryContext 위임 메서드
    public long offset() { return queryContext.offset(); }
    public int size() { return queryContext.size(); }
    public int page() { return queryContext.page(); }
    public boolean isFirstPage() { return queryContext.isFirstPage(); }
    public boolean isAscending() { return queryContext.isAscending(); }
}',
    '{Domain}PageCriteria',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["queryContext"]',
    'PageCriteria 템플릿 - 페이지 기반 조회 조건. QueryContext를 필수로 포함합니다.',
    NOW(), NOW(), NULL
);

-- ============================================
-- 5. class_template - CursorCriteria 템플릿
-- ============================================

INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `description`, `created_at`, `updated_at`, `deleted_at`
) VALUES (
    21, 8, 'CURSOR_CRITERIA_RECORD',
    '/**
 * {Domain}CursorCriteria - 커서 기반 조회 조건 VO
 *
 * <p>커서 기반 조회를 위한 조건을 담는 불변 Value Object입니다.
 * CursorQueryContext를 필수로 포함하며, 도메인별 필터 조건을 추가합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * OrderCursorCriteria criteria = OrderCursorCriteria.of(
 *     cursorQueryContext,
 *     OrderStatus.PENDING,
 *     DateRange.lastWeek()
 * );
 * }</pre>
 *
 * <p>QRY-001: Criteria는 반드시 QueryContext 또는 CursorQueryContext를 포함해야 합니다.
 *
 * <p>QRY-002: 날짜 범위 필터는 반드시 DateRange VO를 사용해야 합니다.
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param cursorQueryContext 커서 조회 컨텍스트 (정렬 + 커서 페이지네이션)
 * @param status 상태 필터 (선택)
 * @param dateRange 날짜 범위 필터 (선택)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record {Domain}CursorCriteria<K extends SortKey, C>(
        CursorQueryContext<K, C> cursorQueryContext,
        {Domain}Status status,
        DateRange dateRange) {

    /**
     * Compact constructor - 유효성 검증
     */
    public {Domain}CursorCriteria {
        if (cursorQueryContext == null) {
            throw new IllegalArgumentException("CursorQueryContext must not be null");
        }
    }

    /**
     * CursorQueryContext만으로 Criteria를 생성합니다.
     */
    public static <K extends SortKey, C> {Domain}CursorCriteria<K, C> of(
            CursorQueryContext<K, C> cursorQueryContext) {
        return new {Domain}CursorCriteria<>(cursorQueryContext, null, null);
    }

    /**
     * 모든 필터 조건으로 Criteria를 생성합니다.
     */
    public static <K extends SortKey, C> {Domain}CursorCriteria<K, C> of(
            CursorQueryContext<K, C> cursorQueryContext, {Domain}Status status, DateRange dateRange) {
        return new {Domain}CursorCriteria<>(cursorQueryContext, status, dateRange);
    }

    /**
     * 상태 필터가 있는지 확인합니다.
     */
    public boolean hasStatusFilter() {
        return status != null;
    }

    /**
     * 날짜 범위 필터가 있는지 확인합니다.
     */
    public boolean hasDateRangeFilter() {
        return dateRange != null;
    }

    // CursorQueryContext 위임 메서드
    public boolean isFirstPage() { return cursorQueryContext.isFirstPage(); }
    public boolean hasCursor() { return cursorQueryContext.hasCursor(); }
    public C cursor() { return cursorQueryContext.cursor(); }
    public int size() { return cursorQueryContext.size(); }
    public int fetchSize() { return cursorQueryContext.fetchSize(); }
    public boolean isAscending() { return cursorQueryContext.isAscending(); }
}',
    '{Domain}CursorCriteria',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["cursorQueryContext"]',
    'CursorCriteria 템플릿 - 커서 기반 조회 조건. CursorQueryContext를 필수로 포함합니다.',
    NOW(), NOW(), NULL
);

-- ============================================
-- 6. coding_rule - Query/Criteria 규칙
-- ============================================

INSERT INTO `coding_rule` (
    `convention_id`, `code`, `name`, `severity`, `category`,
    `description`, `rationale`, `auto_fixable`, `applies_to`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
(1, 'QRY-001', 'Criteria는 QueryContext 필수 포함',
 'BLOCKER', 'STRUCTURE',
 'PageCriteria는 QueryContext를, CursorCriteria는 CursorQueryContext를 반드시 포함해야 합니다. 페이지네이션과 정렬 정보를 일관되게 관리합니다.',
 '조회 조건의 일관성을 보장하고, 페이지네이션/정렬 로직의 중복을 방지합니다.',
 FALSE, 'record,Criteria',
 NOW(), NOW(), NULL),

(1, 'QRY-002', 'Criteria 네이밍 규칙',
 'BLOCKER', 'NAMING',
 'query 패키지 내 클래스는 반드시 *PageCriteria 또는 *CursorCriteria로 끝나야 합니다. 페이지 기반 조회는 PageCriteria, 커서 기반 조회는 CursorCriteria를 사용합니다.',
 '조회 방식을 네이밍으로 명확히 구분하여 코드 가독성과 유지보수성을 향상시킵니다.',
 FALSE, 'record,class,query',
 NOW(), NOW(), NULL),

(1, 'QRY-003', '날짜 범위는 DateRange 사용 필수',
 'BLOCKER', 'STRUCTURE',
 'Criteria에서 날짜 범위 필터가 필요한 경우 반드시 DateRange VO를 사용해야 합니다. 시작/종료 날짜를 개별 필드로 정의하는 것은 금지됩니다.',
 '날짜 범위 처리의 일관성을 보장하고, 유효성 검증 로직의 중복을 방지합니다.',
 FALSE, 'record,Criteria',
 NOW(), NOW(), NULL),

(1, 'QRY-004', 'Criteria는 Record로 정의',
 'BLOCKER', 'STRUCTURE',
 'Criteria는 반드시 record로 정의해야 합니다. 불변성을 보장하고, equals/hashCode를 자동으로 제공받습니다.',
 '조회 조건의 불변성을 보장하고, 캐싱 및 비교 연산의 일관성을 확보합니다.',
 FALSE, 'record,class,Criteria',
 NOW(), NOW(), NULL);

-- ============================================
-- 완료
-- ============================================
-- 추가된 항목 요약:
--   - package_structure: 1개 (id 8) - {domain}.query
--   - package_purpose: 1개 (id 19) - DOMAIN_QUERY
--   - class_template: 3개 (id 19-21)
--     - CursorQueryContext, PageCriteria, CursorCriteria
--   - coding_rule: 4개
--     - QRY-001, QRY-002, QRY-003, QRY-004
-- ============================================
