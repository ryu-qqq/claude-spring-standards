-- ============================================
-- Spring Standards - Common VO Class Templates
-- V7: Domain Common VO 클래스 템플릿 추가
-- Created: 2026-01-20
-- ============================================
-- 변경 사항:
--   - class_template: 11개 Common VO 템플릿 추가
--     - CacheKey, LockKey, SortKey (인터페이스)
--     - SortDirection (enum)
--     - PageRequest, PageMeta, SliceMeta (페이지네이션)
--     - DateRange, DeletionStatus (유틸리티)
--     - CursorPageRequest, QueryContext (제네릭)
-- ============================================

-- ============================================
-- 1. class_template 테이블 - Common VO 템플릿
-- ============================================

-- CacheKey 인터페이스 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    8, 5, 'CACHE_KEY_INTERFACE',
    '/**
 * CacheKey - Redis 캐시 키 인터페이스
 *
 * <p>캐시 키 값을 제공하는 Value Object가 구현해야 하는 인터페이스입니다.
 * 각 도메인별 캐시 키는 이 인터페이스를 구현한 record로 정의합니다.
 *
 * <p>키 패턴: {@code cache:{domain}:{entity}:{id}}
 *
 * <p>구현 예시:
 * <pre>{@code
 * public record OrderCacheKey(Long orderId) implements CacheKey {
 *     @Override
 *     public String value() {
 *         return String.format("cache:order:order:%d", orderId);
 *     }
 * }
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface CacheKey {

    /**
     * 캐시 키 값을 반환합니다.
     *
     * <p>키 형식: {@code cache:{domain}:{entity}:{identifier}}
     *
     * @return Redis 캐시에서 사용할 키 문자열
     */
    String value();
}',
    '{Domain}CacheKey',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["value"]',
    NOW(), NOW(), NULL
);

-- LockKey 인터페이스 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    9, 5, 'LOCK_KEY_INTERFACE',
    '/**
 * LockKey - 분산 락 키 인터페이스
 *
 * <p>분산 락 키 값을 제공하는 Value Object가 구현해야 하는 인터페이스입니다.
 * 각 도메인별 락 키는 이 인터페이스를 구현한 record로 정의합니다.
 *
 * <p>키 패턴: {@code lock:{domain}:{entity}:{id}}
 *
 * <p>구현 예시:
 * <pre>{@code
 * public record OrderLockKey(Long orderId) implements LockKey {
 *     @Override
 *     public String value() {
 *         return String.format("lock:order:order:%d", orderId);
 *     }
 * }
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface LockKey {

    /**
     * 분산 락 키 값을 반환합니다.
     *
     * <p>키 형식: {@code lock:{domain}:{entity}:{identifier}}
     *
     * @return Redis 분산 락에서 사용할 키 문자열
     */
    String value();
}',
    '{Domain}LockKey',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["value"]',
    NOW(), NOW(), NULL
);

-- SortKey 인터페이스 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    10, 5, 'SORT_KEY_INTERFACE',
    '/**
 * SortKey - 정렬 키 마커 인터페이스
 *
 * <p>정렬 가능한 필드를 나타내는 enum이 구현해야 하는 마커 인터페이스입니다.
 * 각 BC(Bounded Context)별로 고유한 정렬 키 enum을 정의합니다.
 *
 * <p>구현 예시:
 * <pre>{@code
 * public enum OrderSortKey implements SortKey {
 *     ID("id"),
 *     CREATED_AT("createdAt"),
 *     TOTAL_AMOUNT("totalAmount");
 *
 *     private final String fieldName;
 *
 *     OrderSortKey(String fieldName) {
 *         this.fieldName = fieldName;
 *     }
 *
 *     @Override
 *     public String fieldName() {
 *         return fieldName;
 *     }
 * }
 * }</pre>
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface SortKey {

    /**
     * 정렬에 사용될 실제 필드명을 반환합니다.
     *
     * <p>이 값은 QueryDSL이나 JPA에서 정렬 시 사용됩니다.
     *
     * @return 엔티티의 실제 필드명
     */
    String fieldName();

    /**
     * 정렬 키의 이름을 반환합니다.
     *
     * <p>기본 구현은 enum의 name()을 반환합니다.
     *
     * @return 정렬 키 이름 (enum name)
     */
    default String name() {
        return this.toString();
    }
}',
    '{Domain}SortKey',
    NULL,
    '["lombok.*"]',
    '["SortKey"]',
    NULL,
    '["fieldName"]',
    NOW(), NOW(), NULL
);

-- SortDirection enum 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    11, 5, 'SORT_DIRECTION_ENUM',
    '/**
 * SortDirection - 정렬 방향 enum
 *
 * <p>정렬 방향(오름차순/내림차순)을 나타내는 공통 enum입니다.
 * 모든 도메인에서 재사용됩니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * SortDirection direction = SortDirection.fromString("desc");
 * boolean isAsc = direction.isAscending();
 * SortDirection reversed = direction.reverse();
 * }</pre>
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public enum SortDirection {

    /** 오름차순 정렬 */
    ASC,

    /** 내림차순 정렬 */
    DESC;

    /**
     * 기본 정렬 방향을 반환합니다.
     *
     * @return DESC (내림차순)
     */
    public static SortDirection defaultDirection() {
        return DESC;
    }

    /**
     * 오름차순인지 확인합니다.
     *
     * @return 오름차순이면 true
     */
    public boolean isAscending() {
        return this == ASC;
    }

    /**
     * 내림차순인지 확인합니다.
     *
     * @return 내림차순이면 true
     */
    public boolean isDescending() {
        return this == DESC;
    }

    /**
     * 반대 방향을 반환합니다.
     *
     * @return ASC면 DESC, DESC면 ASC
     */
    public SortDirection reverse() {
        return this == ASC ? DESC : ASC;
    }

    /**
     * 문자열에서 SortDirection을 파싱합니다.
     *
     * <p>대소문자를 구분하지 않습니다.
     *
     * @param value 파싱할 문자열 ("asc", "ASC", "desc", "DESC")
     * @return 해당하는 SortDirection, null이거나 빈 문자열이면 기본값(DESC)
     * @throws IllegalArgumentException 유효하지 않은 값인 경우
     */
    public static SortDirection fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultDirection();
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid sort direction: " + value + ". Use ASC or DESC.");
        }
    }

    /**
     * 표시용 이름을 반환합니다.
     *
     * @return "Ascending" 또는 "Descending"
     */
    public String displayName() {
        return this == ASC ? "Ascending" : "Descending";
    }
}',
    'SortDirection',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["isAscending", "isDescending", "reverse", "fromString"]',
    NOW(), NOW(), NULL
);

-- PageRequest record 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    12, 5, 'PAGE_REQUEST_RECORD',
    '/**
 * PageRequest - 오프셋 기반 페이지네이션 요청 VO
 *
 * <p>전통적인 오프셋 기반 페이지네이션을 위한 요청 정보를 담는 불변 Value Object입니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * PageRequest request = PageRequest.of(0, 20);
 * long offset = request.offset();
 * PageRequest next = request.next();
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param page 0-based 페이지 번호
 * @param size 페이지 당 항목 수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PageRequest(int page, int size) {

    /** 기본 페이지 크기 */
    public static final int DEFAULT_SIZE = 20;

    /** 최대 페이지 크기 */
    public static final int MAX_SIZE = 100;

    /**
     * Compact constructor - 유효성 검증
     */
    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be non-negative");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Size must be positive");
        }
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
    }

    /**
     * 페이지 번호와 크기로 PageRequest를 생성합니다.
     *
     * @param page 0-based 페이지 번호
     * @param size 페이지 당 항목 수
     * @return PageRequest 인스턴스
     */
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }

    /**
     * 첫 페이지 요청을 생성합니다.
     *
     * @param size 페이지 당 항목 수
     * @return 첫 페이지 PageRequest
     */
    public static PageRequest first(int size) {
        return new PageRequest(0, size);
    }

    /**
     * 기본 설정의 첫 페이지 요청을 생성합니다.
     *
     * @return 기본 크기의 첫 페이지 PageRequest
     */
    public static PageRequest defaultPage() {
        return new PageRequest(0, DEFAULT_SIZE);
    }

    /**
     * SQL OFFSET 값을 계산합니다.
     *
     * @return 오프셋 값
     */
    public long offset() {
        return (long) page * size;
    }

    /**
     * 다음 페이지 요청을 반환합니다.
     *
     * @return 다음 페이지 PageRequest
     */
    public PageRequest next() {
        return new PageRequest(page + 1, size);
    }

    /**
     * 이전 페이지 요청을 반환합니다.
     *
     * @return 이전 페이지 PageRequest, 첫 페이지면 자기 자신
     */
    public PageRequest previous() {
        return page == 0 ? this : new PageRequest(page - 1, size);
    }

    /**
     * 첫 페이지인지 확인합니다.
     *
     * @return 첫 페이지면 true
     */
    public boolean isFirst() {
        return page == 0;
    }

    /**
     * 전체 페이지 수를 계산합니다.
     *
     * @param totalElements 전체 항목 수
     * @return 전체 페이지 수
     */
    public int totalPages(long totalElements) {
        return (int) Math.ceil((double) totalElements / size);
    }

    /**
     * 마지막 페이지인지 확인합니다.
     *
     * @param totalElements 전체 항목 수
     * @return 마지막 페이지면 true
     */
    public boolean isLast(long totalElements) {
        return page >= totalPages(totalElements) - 1;
    }
}',
    'PageRequest',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["of", "first", "offset", "next", "previous", "isFirst"]',
    NOW(), NOW(), NULL
);

-- PageMeta record 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    13, 5, 'PAGE_META_RECORD',
    '/**
 * PageMeta - 오프셋 기반 페이지네이션 메타데이터 VO
 *
 * <p>페이지네이션 결과의 메타 정보를 담는 불변 Value Object입니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * PageMeta meta = PageMeta.of(0, 20, 150);
 * boolean hasNext = meta.hasNext();
 * int totalPages = meta.totalPages();
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param page 현재 페이지 번호 (0-based)
 * @param size 페이지 당 항목 수
 * @param totalElements 전체 항목 수
 * @param totalPages 전체 페이지 수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record PageMeta(int page, int size, long totalElements, int totalPages) {

    /**
     * Compact constructor - 유효성 검증
     */
    public PageMeta {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be non-negative");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Size must be positive");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("Total elements must be non-negative");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("Total pages must be non-negative");
        }
    }

    /**
     * 페이지 정보로 PageMeta를 생성합니다.
     *
     * <p>totalPages는 자동으로 계산됩니다.
     *
     * @param page 현재 페이지 번호
     * @param size 페이지 당 항목 수
     * @param totalElements 전체 항목 수
     * @return PageMeta 인스턴스
     */
    public static PageMeta of(int page, int size, long totalElements) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return new PageMeta(page, size, totalElements, totalPages);
    }

    /**
     * 빈 결과를 위한 PageMeta를 생성합니다.
     *
     * @param size 요청된 페이지 크기
     * @return 빈 PageMeta
     */
    public static PageMeta empty(int size) {
        return new PageMeta(0, size, 0, 0);
    }

    /**
     * 다음 페이지가 있는지 확인합니다.
     *
     * @return 다음 페이지가 있으면 true
     */
    public boolean hasNext() {
        return page < totalPages - 1;
    }

    /**
     * 이전 페이지가 있는지 확인합니다.
     *
     * @return 이전 페이지가 있으면 true
     */
    public boolean hasPrevious() {
        return page > 0;
    }

    /**
     * 첫 페이지인지 확인합니다.
     *
     * @return 첫 페이지면 true
     */
    public boolean isFirst() {
        return page == 0;
    }

    /**
     * 마지막 페이지인지 확인합니다.
     *
     * @return 마지막 페이지면 true
     */
    public boolean isLast() {
        return page >= totalPages - 1;
    }

    /**
     * 결과가 비어있는지 확인합니다.
     *
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return totalElements == 0;
    }

    /**
     * 현재 페이지의 시작 항목 번호를 반환합니다 (1-based).
     *
     * @return 시작 항목 번호
     */
    public long startElement() {
        return isEmpty() ? 0 : (long) page * size + 1;
    }

    /**
     * 현재 페이지의 마지막 항목 번호를 반환합니다 (1-based).
     *
     * @return 마지막 항목 번호
     */
    public long endElement() {
        return isEmpty() ? 0 : Math.min((long) (page + 1) * size, totalElements);
    }

    /**
     * SQL OFFSET 값을 계산합니다.
     *
     * @return 오프셋 값
     */
    public long offset() {
        return (long) page * size;
    }
}',
    'PageMeta',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["of", "empty", "hasNext", "hasPrevious", "isFirst", "isLast", "isEmpty"]',
    NOW(), NOW(), NULL
);

-- SliceMeta record 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    14, 5, 'SLICE_META_RECORD',
    '/**
 * SliceMeta - 커서 기반 페이지네이션 메타데이터 VO
 *
 * <p>무한 스크롤 등 커서 기반 페이지네이션 결과의 메타 정보를 담는 불변 Value Object입니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * SliceMeta meta = SliceMeta.of(20, true, "cursor123", 20);
 * boolean hasNext = meta.hasNext();
 * String nextCursor = meta.cursor();
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param size 요청된 페이지 크기
 * @param hasNext 다음 페이지 존재 여부
 * @param cursor 다음 페이지를 위한 커서 (nullable)
 * @param count 현재 페이지의 항목 수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record SliceMeta(int size, boolean hasNext, String cursor, int count) {

    /**
     * Compact constructor - 유효성 검증
     */
    public SliceMeta {
        if (size < 1) {
            throw new IllegalArgumentException("Size must be positive");
        }
        if (count < 0) {
            throw new IllegalArgumentException("Count must be non-negative");
        }
    }

    /**
     * SliceMeta를 생성합니다.
     *
     * @param size 요청된 페이지 크기
     * @param hasNext 다음 페이지 존재 여부
     * @param cursor 다음 페이지 커서
     * @param count 현재 페이지 항목 수
     * @return SliceMeta 인스턴스
     */
    public static SliceMeta of(int size, boolean hasNext, String cursor, int count) {
        return new SliceMeta(size, hasNext, cursor, count);
    }

    /**
     * 커서 없이 SliceMeta를 생성합니다.
     *
     * @param size 요청된 페이지 크기
     * @param hasNext 다음 페이지 존재 여부
     * @param count 현재 페이지 항목 수
     * @return SliceMeta 인스턴스
     */
    public static SliceMeta withCursor(int size, boolean hasNext, int count) {
        return new SliceMeta(size, hasNext, null, count);
    }

    /**
     * 빈 결과를 위한 SliceMeta를 생성합니다.
     *
     * @param size 요청된 페이지 크기
     * @return 빈 SliceMeta
     */
    public static SliceMeta empty(int size) {
        return new SliceMeta(size, false, null, 0);
    }

    /**
     * 커서가 있는지 확인합니다.
     *
     * @return 커서가 있으면 true
     */
    public boolean hasCursor() {
        return cursor != null && !cursor.isBlank();
    }

    /**
     * 마지막 페이지인지 확인합니다.
     *
     * @return 마지막 페이지면 true
     */
    public boolean isLast() {
        return !hasNext;
    }

    /**
     * 결과가 비어있는지 확인합니다.
     *
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * 커서를 Long으로 파싱합니다.
     *
     * @return 파싱된 Long 값, 커서가 없으면 null
     * @throws NumberFormatException 숫자로 변환 불가능한 경우
     */
    public Long cursorAsLong() {
        return hasCursor() ? Long.valueOf(cursor) : null;
    }

    /**
     * 다음 페이지 정보를 위한 SliceMeta를 생성합니다.
     *
     * @param newCursor 새 커서
     * @param newCount 새 항목 수
     * @param newHasNext 다음 페이지 존재 여부
     * @return 새 SliceMeta
     */
    public SliceMeta next(String newCursor, int newCount, boolean newHasNext) {
        return new SliceMeta(size, newHasNext, newCursor, newCount);
    }
}',
    'SliceMeta',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["of", "empty", "hasCursor", "isLast", "isEmpty"]',
    NOW(), NOW(), NULL
);

-- DateRange record 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    15, 5, 'DATE_RANGE_RECORD',
    '/**
 * DateRange - 날짜 범위 VO
 *
 * <p>시작일과 종료일로 구성된 날짜 범위를 나타내는 불변 Value Object입니다.
 * 조회 조건에서 기간 필터링에 사용됩니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * DateRange range = DateRange.lastDays(7);
 * DateRange thisMonth = DateRange.thisMonth();
 * boolean contains = range.contains(LocalDate.now());
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param startDate 시작일 (nullable - null이면 시작 제한 없음)
 * @param endDate 종료일 (nullable - null이면 종료 제한 없음)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {

    /**
     * Compact constructor - 유효성 검증
     */
    public DateRange {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
    }

    /**
     * 시작일과 종료일로 DateRange를 생성합니다.
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return DateRange 인스턴스
     */
    public static DateRange of(LocalDate startDate, LocalDate endDate) {
        return new DateRange(startDate, endDate);
    }

    /**
     * 최근 N일 범위를 생성합니다.
     *
     * @param days 일 수
     * @return 최근 N일 DateRange
     */
    public static DateRange lastDays(int days) {
        LocalDate today = LocalDate.now();
        return new DateRange(today.minusDays(days), today);
    }

    /**
     * 이번 달 범위를 생성합니다.
     *
     * @return 이번 달 DateRange
     */
    public static DateRange thisMonth() {
        LocalDate today = LocalDate.now();
        return new DateRange(today.withDayOfMonth(1), today);
    }

    /**
     * 지난 달 범위를 생성합니다.
     *
     * @return 지난 달 DateRange
     */
    public static DateRange lastMonth() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayLastMonth = today.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayLastMonth = today.withDayOfMonth(1).minusDays(1);
        return new DateRange(firstDayLastMonth, lastDayLastMonth);
    }

    /**
     * 종료일만 지정된 범위를 생성합니다.
     *
     * @param endDate 종료일
     * @return 시작 제한 없는 DateRange
     */
    public static DateRange until(LocalDate endDate) {
        return new DateRange(null, endDate);
    }

    /**
     * 시작일만 지정된 범위를 생성합니다.
     *
     * @param startDate 시작일
     * @return 종료 제한 없는 DateRange
     */
    public static DateRange from(LocalDate startDate) {
        return new DateRange(startDate, null);
    }

    /**
     * 시작일을 Instant로 변환합니다 (00:00:00 UTC).
     *
     * @return Instant, startDate가 null이면 null
     */
    public Instant startInstant() {
        return startDate != null ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    }

    /**
     * 종료일을 Instant로 변환합니다 (다음날 00:00:00 UTC - exclusive).
     *
     * @return Instant, endDate가 null이면 null
     */
    public Instant endInstant() {
        return endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;
    }

    /**
     * 범위가 비어있는지 (둘 다 null인지) 확인합니다.
     *
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return startDate == null && endDate == null;
    }

    /**
     * 특정 날짜가 범위 내에 있는지 확인합니다.
     *
     * @param date 확인할 날짜
     * @return 범위 내에 있으면 true
     */
    public boolean contains(LocalDate date) {
        if (date == null) {
            return false;
        }
        boolean afterStart = startDate == null || !date.isBefore(startDate);
        boolean beforeEnd = endDate == null || !date.isAfter(endDate);
        return afterStart && beforeEnd;
    }
}',
    'DateRange',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["of", "lastDays", "thisMonth", "isEmpty", "contains"]',
    NOW(), NOW(), NULL
);

-- DeletionStatus record 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    16, 5, 'DELETION_STATUS_RECORD',
    '/**
 * DeletionStatus - 소프트 삭제 상태 VO
 *
 * <p>엔티티의 삭제 상태(소프트 삭제)를 나타내는 불변 Value Object입니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * DeletionStatus active = DeletionStatus.active();
 * DeletionStatus deleted = DeletionStatus.deletedAt(Instant.now());
 * boolean isDeleted = status.isDeleted();
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param deleted 삭제 여부
 * @param deletedAt 삭제 시각 (삭제되지 않았으면 null)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record DeletionStatus(boolean deleted, Instant deletedAt) {

    /**
     * Compact constructor - 유효성 검증
     */
    public DeletionStatus {
        if (deleted && deletedAt == null) {
            throw new IllegalArgumentException("Deleted status requires deletedAt timestamp");
        }
        if (!deleted && deletedAt != null) {
            throw new IllegalArgumentException("Active status should not have deletedAt timestamp");
        }
    }

    /**
     * 활성 상태를 생성합니다.
     *
     * @return 활성 DeletionStatus
     */
    public static DeletionStatus active() {
        return new DeletionStatus(false, null);
    }

    /**
     * 삭제 상태를 생성합니다.
     *
     * @param deletedAt 삭제 시각
     * @return 삭제된 DeletionStatus
     */
    public static DeletionStatus deletedAt(Instant deletedAt) {
        return new DeletionStatus(true, deletedAt);
    }

    /**
     * DB에서 복원할 때 사용합니다.
     *
     * @param deleted 삭제 여부
     * @param deletedAt 삭제 시각
     * @return DeletionStatus 인스턴스
     */
    public static DeletionStatus reconstitute(boolean deleted, Instant deletedAt) {
        if (!deleted) {
            return active();
        }
        return deletedAt(deletedAt);
    }

    /**
     * 삭제되었는지 확인합니다.
     *
     * @return 삭제되었으면 true
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * 활성 상태인지 확인합니다.
     *
     * @return 활성 상태면 true
     */
    public boolean isActive() {
        return !deleted;
    }
}',
    'DeletionStatus',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["active", "deletedAt", "reconstitute", "isDeleted", "isActive"]',
    NOW(), NOW(), NULL
);

-- CursorPageRequest record 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    17, 5, 'CURSOR_PAGE_REQUEST_RECORD',
    '/**
 * CursorPageRequest - 제네릭 커서 기반 페이지네이션 요청 VO
 *
 * <p>타입 안전한 커서 기반 페이지네이션을 위한 요청 정보를 담는 불변 Value Object입니다.
 * 커서 타입을 제네릭으로 지정하여 다양한 타입의 커서를 지원합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * CursorPageRequest<Long> request = CursorPageRequest.afterId(100L);
 * CursorPageRequest<String> strRequest = CursorPageRequest.ofString("cursor123", 20);
 * boolean isFirst = request.isFirstPage();
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param <C> 커서 타입
 * @param cursor 현재 커서 값 (nullable - null이면 첫 페이지)
 * @param size 페이지 당 항목 수
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CursorPageRequest<C>(C cursor, int size) {

    /** 기본 페이지 크기 */
    public static final int DEFAULT_SIZE = 20;

    /** 최대 페이지 크기 */
    public static final int MAX_SIZE = 100;

    /**
     * Compact constructor - 유효성 검증
     */
    public CursorPageRequest {
        if (size < 1) {
            throw new IllegalArgumentException("Size must be positive");
        }
        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
    }

    /**
     * 커서와 크기로 CursorPageRequest를 생성합니다.
     *
     * @param <C> 커서 타입
     * @param cursor 커서 값
     * @param size 페이지 크기
     * @return CursorPageRequest 인스턴스
     */
    public static <C> CursorPageRequest<C> of(C cursor, int size) {
        return new CursorPageRequest<>(cursor, size);
    }

    /**
     * 첫 페이지 요청을 생성합니다.
     *
     * @param <C> 커서 타입
     * @param size 페이지 크기
     * @return 첫 페이지 CursorPageRequest
     */
    public static <C> CursorPageRequest<C> first(int size) {
        return new CursorPageRequest<>(null, size);
    }

    /**
     * 기본 설정의 첫 페이지 요청을 생성합니다.
     *
     * @param <C> 커서 타입
     * @return 기본 크기의 첫 페이지 CursorPageRequest
     */
    public static <C> CursorPageRequest<C> defaultPage() {
        return new CursorPageRequest<>(null, DEFAULT_SIZE);
    }

    /**
     * Long ID 기반 커서 요청을 생성합니다.
     *
     * @param afterId 이 ID 이후 항목 조회
     * @return Long 커서 CursorPageRequest
     */
    public static CursorPageRequest<Long> afterId(Long afterId) {
        return new CursorPageRequest<>(afterId, DEFAULT_SIZE);
    }

    /**
     * String 커서 기반 요청을 생성합니다.
     *
     * @param cursor 문자열 커서
     * @param size 페이지 크기
     * @return String 커서 CursorPageRequest
     */
    public static CursorPageRequest<String> ofString(String cursor, int size) {
        return new CursorPageRequest<>(cursor, size);
    }

    /**
     * 첫 페이지인지 확인합니다.
     *
     * @return 커서가 없으면 true
     */
    public boolean isFirstPage() {
        return cursor == null;
    }

    /**
     * 커서가 있는지 확인합니다.
     *
     * @return 커서가 있으면 true
     */
    public boolean hasCursor() {
        return cursor != null;
    }

    /**
     * 다음 페이지 요청을 생성합니다.
     *
     * @param nextCursor 다음 커서
     * @return 다음 페이지 CursorPageRequest
     */
    public CursorPageRequest<C> next(C nextCursor) {
        return new CursorPageRequest<>(nextCursor, size);
    }

    /**
     * 실제 조회할 크기를 반환합니다 (hasNext 판단을 위해 +1).
     *
     * @return size + 1
     */
    public int fetchSize() {
        return size + 1;
    }
}',
    'CursorPageRequest',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["of", "first", "afterId", "isFirstPage", "hasCursor", "next", "fetchSize"]',
    NOW(), NOW(), NULL
);

-- QueryContext record 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    18, 5, 'QUERY_CONTEXT_RECORD',
    '/**
 * QueryContext - 제네릭 조회 컨텍스트 VO
 *
 * <p>정렬 + 페이지네이션 정보를 결합한 조회 컨텍스트를 나타내는 불변 Value Object입니다.
 * SortKey 타입을 제네릭으로 지정하여 도메인별 정렬 키를 지원합니다.
 *
 * <p>사용 예시:
 * <pre>{@code
 * QueryContext<OrderSortKey> context = QueryContext.of(
 *     OrderSortKey.CREATED_AT,
 *     SortDirection.DESC,
 *     PageRequest.of(0, 20)
 * );
 * long offset = context.offset();
 * boolean isAsc = context.isAscending();
 * }</pre>
 *
 * <p>VO-001: Value Object는 불변이어야 합니다.
 *
 * <p>VO-002: Lombok 사용이 금지됩니다.
 *
 * @param <K> SortKey를 구현하는 정렬 키 타입
 * @param sortKey 정렬 키
 * @param sortDirection 정렬 방향
 * @param pageRequest 페이지 요청
 * @param includeDeleted 삭제된 항목 포함 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
public record QueryContext<K extends SortKey>(
        K sortKey,
        SortDirection sortDirection,
        PageRequest pageRequest,
        boolean includeDeleted) {

    /**
     * Compact constructor - 유효성 검증
     */
    public QueryContext {
        if (sortKey == null) {
            throw new IllegalArgumentException("SortKey must not be null");
        }
        if (sortDirection == null) {
            sortDirection = SortDirection.defaultDirection();
        }
        if (pageRequest == null) {
            pageRequest = PageRequest.defaultPage();
        }
    }

    /**
     * 모든 파라미터로 QueryContext를 생성합니다.
     *
     * @param <K> 정렬 키 타입
     * @param sortKey 정렬 키
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이지 요청
     * @param includeDeleted 삭제 항목 포함 여부
     * @return QueryContext 인스턴스
     */
    public static <K extends SortKey> QueryContext<K> of(
            K sortKey, SortDirection sortDirection, PageRequest pageRequest, boolean includeDeleted) {
        return new QueryContext<>(sortKey, sortDirection, pageRequest, includeDeleted);
    }

    /**
     * 기본 설정으로 QueryContext를 생성합니다.
     *
     * @param <K> 정렬 키 타입
     * @param sortKey 정렬 키
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이지 요청
     * @return 삭제 항목 미포함 QueryContext
     */
    public static <K extends SortKey> QueryContext<K> of(
            K sortKey, SortDirection sortDirection, PageRequest pageRequest) {
        return new QueryContext<>(sortKey, sortDirection, pageRequest, false);
    }

    /**
     * 기본 정렬 방향과 첫 페이지로 QueryContext를 생성합니다.
     *
     * @param <K> 정렬 키 타입
     * @param sortKey 정렬 키
     * @return 기본 설정 QueryContext
     */
    public static <K extends SortKey> QueryContext<K> defaultOf(K sortKey) {
        return new QueryContext<>(sortKey, SortDirection.defaultDirection(), PageRequest.defaultPage(), false);
    }

    /**
     * 첫 페이지 QueryContext를 생성합니다.
     *
     * @param <K> 정렬 키 타입
     * @param sortKey 정렬 키
     * @param sortDirection 정렬 방향
     * @param size 페이지 크기
     * @return 첫 페이지 QueryContext
     */
    public static <K extends SortKey> QueryContext<K> firstPage(
            K sortKey, SortDirection sortDirection, int size) {
        return new QueryContext<>(sortKey, sortDirection, PageRequest.first(size), false);
    }

    /**
     * 다음 페이지 QueryContext를 반환합니다.
     *
     * @return 다음 페이지 QueryContext
     */
    public QueryContext<K> nextPage() {
        return new QueryContext<>(sortKey, sortDirection, pageRequest.next(), includeDeleted);
    }

    /**
     * 이전 페이지 QueryContext를 반환합니다.
     *
     * @return 이전 페이지 QueryContext
     */
    public QueryContext<K> previousPage() {
        return new QueryContext<>(sortKey, sortDirection, pageRequest.previous(), includeDeleted);
    }

    /**
     * 정렬 방향을 반전한 QueryContext를 반환합니다.
     *
     * @return 정렬 방향 반전 QueryContext
     */
    public QueryContext<K> reverseSortDirection() {
        return new QueryContext<>(sortKey, sortDirection.reverse(), pageRequest, includeDeleted);
    }

    /**
     * 정렬 키를 변경한 QueryContext를 반환합니다.
     *
     * @param newSortKey 새 정렬 키
     * @return 정렬 키 변경 QueryContext
     */
    public QueryContext<K> withSortKey(K newSortKey) {
        return new QueryContext<>(newSortKey, sortDirection, pageRequest, includeDeleted);
    }

    /**
     * 페이지 크기를 변경한 QueryContext를 반환합니다.
     *
     * @param newSize 새 페이지 크기
     * @return 페이지 크기 변경 QueryContext
     */
    public QueryContext<K> withPageSize(int newSize) {
        return new QueryContext<>(sortKey, sortDirection, PageRequest.of(pageRequest.page(), newSize), includeDeleted);
    }

    /**
     * 삭제 항목 포함 여부를 변경한 QueryContext를 반환합니다.
     *
     * @param include 삭제 항목 포함 여부
     * @return 삭제 항목 포함 여부 변경 QueryContext
     */
    public QueryContext<K> withIncludeDeleted(boolean include) {
        return new QueryContext<>(sortKey, sortDirection, pageRequest, include);
    }

    /**
     * SQL OFFSET 값을 반환합니다.
     *
     * @return 오프셋 값
     */
    public long offset() {
        return pageRequest.offset();
    }

    /**
     * 페이지 크기를 반환합니다.
     *
     * @return 페이지 크기
     */
    public int size() {
        return pageRequest.size();
    }

    /**
     * 현재 페이지 번호를 반환합니다.
     *
     * @return 페이지 번호
     */
    public int page() {
        return pageRequest.page();
    }

    /**
     * 첫 페이지인지 확인합니다.
     *
     * @return 첫 페이지면 true
     */
    public boolean isFirstPage() {
        return pageRequest.isFirst();
    }

    /**
     * 오름차순인지 확인합니다.
     *
     * @return 오름차순이면 true
     */
    public boolean isAscending() {
        return sortDirection.isAscending();
    }
}',
    '{Domain}QueryContext',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["of", "defaultOf", "firstPage", "nextPage", "previousPage", "offset", "size", "page", "isFirstPage", "isAscending"]',
    NOW(), NOW(), NULL
);

-- ============================================
-- 완료
-- ============================================
-- 추가된 항목 요약:
--   - class_template: 11개 (id 8-18)
--     - CacheKey 인터페이스 (id 8)
--     - LockKey 인터페이스 (id 9)
--     - SortKey 인터페이스 (id 10)
--     - SortDirection enum (id 11)
--     - PageRequest record (id 12)
--     - PageMeta record (id 13)
--     - SliceMeta record (id 14)
--     - DateRange record (id 15)
--     - DeletionStatus record (id 16)
--     - CursorPageRequest record (id 17)
--     - QueryContext record (id 18)
-- ============================================
