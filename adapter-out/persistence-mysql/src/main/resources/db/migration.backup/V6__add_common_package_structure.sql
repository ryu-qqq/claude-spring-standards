-- ============================================
-- Spring Standards - Common Package Structure
-- V6: Domain Common 패키지 구조 및 템플릿 추가
-- Created: 2026-01-20
-- ============================================
-- 변경 사항:
--   - package_structure: common.vo, common.exception, common.event 추가
--   - package_purpose: 각 패키지 용도 정의
--   - class_template: DomainException, ErrorCode, DomainEvent 참조 코드
--   - coding_rule: 예외 및 이벤트 사용 규칙
-- ============================================

-- ============================================
-- 1. package_structure 테이블 - common 패키지 추가
-- ============================================

-- Common Value Object 패키지 (business domain과 달리 {domain} placeholder 없음)
INSERT IGNORE INTO `package_structure` (
    `id`, `module_id`, `path_pattern`, `allowed_class_types`,
    `naming_pattern`, `naming_suffix`, `description`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    5, 1, '{base_package}.common.vo',
    '["VALUE_OBJECT", "RECORD", "INTERFACE", "ENUM"]',
    NULL, NULL,
    '도메인 공통 Value Object 패키지. 여러 도메인에서 재사용되는 불변 값 객체가 위치합니다.',
    NOW(), NOW(), NULL
);

-- Common Exception 패키지
INSERT IGNORE INTO `package_structure` (
    `id`, `module_id`, `path_pattern`, `allowed_class_types`,
    `naming_pattern`, `naming_suffix`, `description`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    6, 1, '{base_package}.common.exception',
    '["EXCEPTION", "INTERFACE", "ENUM"]',
    '*Exception', 'Exception',
    '도메인 공통 예외 패키지. DomainException, ErrorCode 인터페이스가 위치합니다.',
    NOW(), NOW(), NULL
);

-- Common Event 패키지
INSERT IGNORE INTO `package_structure` (
    `id`, `module_id`, `path_pattern`, `allowed_class_types`,
    `naming_pattern`, `naming_suffix`, `description`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    7, 1, '{base_package}.common.event',
    '["INTERFACE", "RECORD"]',
    '*Event', 'Event',
    '도메인 공통 이벤트 패키지. DomainEvent 마커 인터페이스가 위치합니다.',
    NOW(), NOW(), NULL
);

-- ============================================
-- 2. package_purpose 테이블 - 각 패키지 용도 정의
-- ============================================

-- Common VO 패키지 용도
INSERT INTO `package_purpose` (
    `id`, `structure_id`, `code`, `name`, `description`,
    `default_allowed_class_types`, `default_naming_pattern`, `default_naming_suffix`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    16, 5, 'COMMON_VO', '공통 Value Object 패키지',
    '여러 도메인에서 공통으로 사용되는 불변 Value Object를 정의합니다. 페이지네이션(PageMeta, SliceMeta), 정렬(SortKey), 날짜 범위(DateRange), 분산락(LockKey) 등이 포함됩니다.',
    '["VALUE_OBJECT", "RECORD", "INTERFACE", "ENUM"]', NULL, NULL,
    NOW(), NOW(), NULL
);

-- Common Exception 패키지 용도
INSERT INTO `package_purpose` (
    `id`, `structure_id`, `code`, `name`, `description`,
    `default_allowed_class_types`, `default_naming_pattern`, `default_naming_suffix`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    17, 6, 'COMMON_EXCEPTION', '공통 Exception 패키지',
    '도메인 예외의 기본 구조를 정의합니다. ErrorCode 인터페이스와 DomainException 추상 클래스를 제공하며, 각 도메인별 예외는 반드시 이 구조를 따라야 합니다.',
    '["EXCEPTION", "INTERFACE", "ENUM"]', '*Exception', 'Exception',
    NOW(), NOW(), NULL
);

-- Common Event 패키지 용도
INSERT INTO `package_purpose` (
    `id`, `structure_id`, `code`, `name`, `description`,
    `default_allowed_class_types`, `default_naming_pattern`, `default_naming_suffix`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    18, 7, 'COMMON_EVENT', '공통 Event 패키지',
    '도메인 이벤트의 기본 구조를 정의합니다. DomainEvent 마커 인터페이스를 제공하며, 모든 도메인 이벤트는 반드시 이 인터페이스를 구현해야 합니다.',
    '["INTERFACE", "RECORD"]', '*Event', 'Event',
    NOW(), NOW(), NULL
);

-- ============================================
-- 3. class_template 테이블 - 참조 코드 템플릿
-- ============================================

-- ErrorCode 인터페이스 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `description`, `created_at`, `updated_at`, `deleted_at`
) VALUES (
    5, 6, 'ERROR_CODE_INTERFACE',
    '/**
 * ErrorCode - 도메인 에러 코드 인터페이스
 *
 * <p>모든 도메인별 ErrorCode enum은 이 인터페이스를 구현해야 합니다.
 *
 * <p>구현 예시:
 * <pre>{@code
 * public enum OrderErrorCode implements ErrorCode {
 *     ORDER_NOT_FOUND("ORD-001", 404, "주문을 찾을 수 없습니다"),
 *     INVALID_ORDER_STATUS("ORD-002", 400, "유효하지 않은 주문 상태입니다");
 *
 *     private final String code;
 *     private final int httpStatus;
 *     private final String message;
 *
 *     // constructor and getters...
 * }
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ErrorCode {

    /**
     * 에러 코드를 반환합니다.
     *
     * <p>형식: {DOMAIN}-{NUMBER} (예: ORD-001, PAY-002)
     *
     * @return 도메인 접두사와 번호로 구성된 에러 코드
     */
    String getCode();

    /**
     * HTTP 상태 코드를 반환합니다.
     *
     * @return HTTP 상태 코드 (400, 404, 409, 500 등)
     */
    int getHttpStatus();

    /**
     * 에러 메시지를 반환합니다.
     *
     * @return 사용자에게 표시할 에러 메시지
     */
    String getMessage();
}',
    '{Domain}ErrorCode',
    NULL,
    '["lombok.*"]',
    NULL,
    NULL,
    '["getCode", "getHttpStatus", "getMessage"]',
    'ErrorCode 인터페이스 - 도메인별 예외 코드 정의를 위한 기본 인터페이스',
    NOW(), NOW(), NULL
);

-- DomainException 클래스 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `description`, `created_at`, `updated_at`, `deleted_at`
) VALUES (
    6, 6, 'DOMAIN_EXCEPTION',
    '/**
 * DomainException - 도메인 예외 기본 클래스
 *
 * <p>모든 도메인 예외의 부모 클래스입니다.
 * ErrorCode를 통해 구조화된 예외 정보를 제공합니다.
 *
 * <p>구현 예시:
 * <pre>{@code
 * public class OrderNotFoundException extends DomainException {
 *     public OrderNotFoundException(Long orderId) {
 *         super(OrderErrorCode.ORDER_NOT_FOUND,
 *               String.format("주문 ID: %d", orderId),
 *               Map.of("orderId", orderId));
 *     }
 * }
 * }</pre>
 *
 * <p>AGG-001: Lombok 사용 금지 - 모든 필드와 메서드를 명시적으로 작성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> args;

    protected DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = Collections.emptyMap();
    }

    protected DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = Collections.emptyMap();
    }

    protected DomainException(ErrorCode errorCode, String message, Map<String, Object> args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args != null ? Map.copyOf(args) : Collections.emptyMap();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getArgs() {
        return args;
    }
}',
    '{Domain}Exception',
    NULL,
    '["lombok.*"]',
    NULL,
    '["DomainException"]',
    '["getErrorCode", "getArgs"]',
    'DomainException 클래스 - 모든 도메인 예외의 부모 클래스',
    NOW(), NOW(), NULL
);

-- DomainEvent 인터페이스 템플릿
INSERT INTO `class_template` (
    `id`, `structure_id`, `class_type`, `template_code`,
    `naming_pattern`, `required_annotations`, `forbidden_annotations`,
    `required_interfaces`, `forbidden_inheritance`, `required_methods`,
    `description`, `created_at`, `updated_at`, `deleted_at`
) VALUES (
    7, 7, 'DOMAIN_EVENT_INTERFACE',
    '/**
 * DomainEvent - 도메인 이벤트 마커 인터페이스
 *
 * <p>모든 도메인 이벤트가 구현해야 하는 기본 인터페이스입니다.
 * 이벤트 발생 시각과 이벤트 타입을 제공합니다.
 *
 * <p>구현 예시 (record 사용 권장):
 * <pre>{@code
 * public record OrderCreatedEvent(
 *     Long orderId,
 *     Long customerId,
 *     BigDecimal totalAmount,
 *     Instant occurredAt
 * ) implements DomainEvent {
 *
 *     public OrderCreatedEvent(Long orderId, Long customerId, BigDecimal totalAmount) {
 *         this(orderId, customerId, totalAmount, Instant.now());
 *     }
 * }
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface DomainEvent {

    /**
     * 이벤트 발생 시각을 반환합니다.
     *
     * @return 이벤트가 발생한 시각 (UTC)
     */
    Instant occurredAt();

    /**
     * 이벤트 타입을 반환합니다.
     *
     * <p>기본 구현은 클래스 이름을 반환합니다.
     *
     * @return 이벤트 타입 문자열
     */
    default String eventType() {
        return this.getClass().getSimpleName();
    }
}',
    '{Domain}{Action}Event',
    NULL,
    '["lombok.*"]',
    '["DomainEvent"]',
    NULL,
    '["occurredAt"]',
    'DomainEvent 인터페이스 - 모든 도메인 이벤트가 구현해야 하는 마커 인터페이스',
    NOW(), NOW(), NULL
);

-- ============================================
-- 4. coding_rule 테이블 - 예외 및 이벤트 규칙
-- ============================================

-- DOM-EXC-001: ErrorCode enum 구현 규칙
INSERT INTO `coding_rule` (
    `convention_id`, `code`, `name`, `severity`, `category`,
    `description`, `rationale`, `auto_fixable`, `applies_to`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    1, 'DOM-EXC-001', 'ErrorCode enum 구현 필수',
    'BLOCKER', 'STRUCTURE',
    '모든 도메인별 예외는 해당 도메인의 ErrorCode enum을 정의해야 합니다. ErrorCode는 반드시 ErrorCode 인터페이스를 구현하고, {DOMAIN}-{NUMBER} 형식의 코드를 사용해야 합니다.',
    '일관된 에러 코드 체계를 통해 API 응답의 일관성을 보장하고, 클라이언트에서 에러 처리를 용이하게 합니다.',
    FALSE, 'enum,ErrorCode',
    NOW(), NOW(), NULL
);

-- DOM-EXC-002: DomainException 상속 규칙
INSERT INTO `coding_rule` (
    `convention_id`, `code`, `name`, `severity`, `category`,
    `description`, `rationale`, `auto_fixable`, `applies_to`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    1, 'DOM-EXC-002', 'DomainException 상속 필수',
    'BLOCKER', 'STRUCTURE',
    '도메인 레이어의 모든 예외 클래스는 DomainException을 상속받아야 합니다. RuntimeException이나 Exception을 직접 상속받는 것은 금지됩니다.',
    '공통 예외 처리 로직을 적용하고, ErrorCode 기반의 구조화된 에러 응답을 보장합니다.',
    FALSE, 'class,Exception',
    NOW(), NOW(), NULL
);

-- DOM-EVT-001: DomainEvent 구현 규칙
INSERT INTO `coding_rule` (
    `convention_id`, `code`, `name`, `severity`, `category`,
    `description`, `rationale`, `auto_fixable`, `applies_to`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    1, 'DOM-EVT-001', 'DomainEvent 인터페이스 구현 필수',
    'BLOCKER', 'STRUCTURE',
    '모든 도메인 이벤트는 DomainEvent 인터페이스를 구현해야 합니다. 이벤트는 record로 정의하는 것을 권장하며, occurredAt 필드를 반드시 포함해야 합니다.',
    '이벤트 기반 아키텍처에서 일관된 이벤트 처리와 추적을 보장합니다.',
    FALSE, 'record,Event',
    NOW(), NOW(), NULL
);

-- DOM-EVT-002: 이벤트 불변성 규칙
INSERT INTO `coding_rule` (
    `convention_id`, `code`, `name`, `severity`, `category`,
    `description`, `rationale`, `auto_fixable`, `applies_to`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    1, 'DOM-EVT-002', '이벤트 불변성 보장',
    'BLOCKER', 'BEHAVIOR',
    '도메인 이벤트는 반드시 불변(immutable)이어야 합니다. record 사용을 권장하며, 클래스로 구현 시 모든 필드는 final이어야 합니다.',
    '이벤트의 불변성을 통해 이벤트 소싱, 감사 로그, 디버깅의 신뢰성을 보장합니다.',
    FALSE, 'record,class,Event',
    NOW(), NOW(), NULL
);

-- ============================================
-- 완료
-- ============================================
-- 추가된 항목 요약:
--   - package_structure: 3개 (id 5-7)
--     - common.vo, common.exception, common.event
--   - package_purpose: 3개 (id 16-18)
--   - class_template: 3개 (id 5-7)
--     - ErrorCode 인터페이스, DomainException 클래스, DomainEvent 인터페이스
--   - coding_rule: 4개 (auto-increment)
--     - DOM-EXC-001, DOM-EXC-002, DOM-EVT-001, DOM-EVT-002
-- ============================================
