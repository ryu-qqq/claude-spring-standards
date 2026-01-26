-- =====================================================
-- V14: ChecklistItem 시드 데이터
-- 총 162개 coding_rule에 대한 체크리스트 (약 300개)
-- automation_rule_id 형식: {RULE_CODE}-{SEQUENCE}
-- =====================================================

-- =====================================================
-- DOMAIN Layer (63개 규칙)
-- =====================================================

-- DOM-AGG-004: forNew() 팩토리 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Aggregate에 static forNew(..., Instant now) 팩토리 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-004';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'forNew()에서 ID는 null(Long) 또는 외부 주입(String)으로 처리되는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-004';

-- DOM-AGG-005: reconstitute() 팩토리 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Aggregate에 static reconstitute(...) 팩토리 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-005';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'reconstitute()에서 비즈니스 검증 없이 객체를 복원하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-005';

-- DOM-AGG-006: protected 기본 생성자
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Aggregate에 protected 기본 생성자가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-006';

-- DOM-AGG-007: Aggregate ID는 ID VO 사용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Aggregate의 ID 필드가 전용 ID VO(예: OrderId)를 사용하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-007';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Long 원시 타입 ID를 직접 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-007';

-- DOM-AGG-008: isNew() 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Aggregate에 isNew() 메서드가 구현되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-008';

-- DOM-AGG-009: Aggregate 시간 필드는 Instant 타입 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '시간 필드(createdAt, updatedAt 등)가 java.time.Instant 타입인가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-009';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'LocalDateTime, Date, Calendar 등을 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-009';

-- DOM-AGG-010: Instant.now() 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Aggregate 내부에서 Instant.now() 직접 호출이 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-010';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '시간이 필요한 메서드는 Instant 파라미터로 받는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-010';

-- DOM-AGG-011: 상태 변경 시 updatedAt 갱신
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '상태 변경 메서드에서 updatedAt을 갱신하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-011';

-- DOM-AGG-012: Setter 메서드 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'public void setXxx() 형태의 Setter 메서드가 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-012';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '상태 변경은 비즈니스 의미가 담긴 메서드(cancel, approve 등)를 통하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-012';

-- DOM-AGG-013: Aggregate Getter 최소화
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '불필요한 Getter가 없고 Tell, Don''t Ask 원칙을 따르는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-013';

-- DOM-AGG-014: Law of Demeter - Getter 체이닝 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'getXxx().getYyy() 형태의 Getter 체이닝이 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-014';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '필요한 정보는 Aggregate가 직접 제공하는 메서드를 통해 접근하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-014';

-- DOM-AGG-015: Tell Don't Ask 원칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '외부에서 상태를 물어보고 판단하는 대신 Aggregate에게 판단을 위임하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-015';

-- DOM-AGG-016: 복잡한 비즈니스 규칙은 VO로 위임
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '복잡한 비즈니스 규칙이 Value Object로 분리되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-016';

-- DOM-AGG-017: 상태 변경 시 도메인 이벤트 발행
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '중요한 상태 변경 시 도메인 이벤트를 발행하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-017';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'registerEvent() 메서드를 통해 이벤트를 등록하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-017';

-- DOM-AGG-018: registerEvent() protected 메서드
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'registerEvent() 메서드가 protected로 선언되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-018';

-- DOM-AGG-019: pollEvents() 메서드
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'pollEvents() 메서드가 public으로 구현되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-019';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'pollEvents() 호출 후 내부 이벤트 목록이 비워지는가?', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-019';

-- DOM-AGG-020: Command 메서드는 동사로 시작
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '상태 변경 메서드가 동사로 시작하는가? (cancel, approve, update 등)', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-020';

-- DOM-AGG-021: Query 메서드는 get/is/has/can으로 시작
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '조회 메서드가 get, is, has, can으로 시작하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-021';

-- DOM-AGG-022: 판단 메서드는 boolean 반환
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'is, has, can으로 시작하는 메서드가 boolean을 반환하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-022';

-- DOM-AGG-025: 불변식(Invariant) 검증
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '생성 시점과 상태 변경 시점에 불변식을 검증하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-025';

-- DOM-AGG-026: equals/hashCode는 ID 기반
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'equals/hashCode가 ID 필드만을 기반으로 구현되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-026';

-- DOM-AGG-027: 불변 필드는 final 선언
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'id, createdAt 등 불변 필드가 final로 선언되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-AGG-027';

-- DOM-VO-001: VO Record 타입 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Value Object가 Java Record로 정의되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'class 대신 record 키워드를 사용하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-001';

-- DOM-VO-002: VO of() 정적 팩토리 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Value Object에 of() 정적 팩토리 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-002';

-- DOM-VO-003: VO Compact Constructor 검증 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Record의 Compact Constructor에서 필수 검증을 수행하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-003';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'null 체크, 범위 검증 등이 포함되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-003';

-- DOM-VO-004: Enum VO displayName() 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Enum 타입의 VO에 displayName() 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-004';

-- DOM-VO-005: LockKey 인터페이스 구현 (선택적)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '분산락이 필요한 VO가 LockKey 인터페이스를 구현하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-005';

-- DOM-VO-006: CacheKey 인터페이스 구현 (선택적)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '캐싱이 필요한 VO가 CacheKey 인터페이스를 구현하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-VO-006';

-- DOM-ID-001: ID VO *Id 네이밍 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ID VO가 {Domain}Id 형태로 네이밍되어 있는가? (OrderId, CustomerId)', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-001';

-- DOM-ID-002: ID VO Record 타입 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ID VO가 Java Record로 정의되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-002';

-- DOM-ID-003: ID VO of() 정적 팩토리 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ID VO에 of() 정적 팩토리 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-003';

-- DOM-ID-004: Long ID forNew() 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Long 타입 ID VO에 forNew() 정적 팩토리 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-004';

-- DOM-ID-005: Long ID forNew()는 null 반환
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'forNew()가 value가 null인 ID를 반환하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-005';

-- DOM-ID-006: Long ID isNew() 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Long 타입 ID VO에 isNew() 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-006';

-- DOM-ID-007: String ID isNew() 금지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'String 타입 ID VO에 isNew() 메서드가 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-007';

-- DOM-ID-008: String ID는 외부에서 주입
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'String ID가 Application Layer(Factory)에서 생성되어 주입되는가?', 'REVIEW', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-008';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Domain에서 UUID.randomUUID() 호출이 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-ID-008';

-- DOM-EVT-001: DomainEvent 인터페이스 구현 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 이벤트가 DomainEvent 인터페이스를 구현하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EVT-001';

-- DOM-EVT-002: Event Record 타입 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 이벤트가 Java Record로 정의되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EVT-002';

-- DOM-EVT-003: Event occurredAt 필드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 이벤트에 occurredAt(Instant) 필드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EVT-003';

-- DOM-EVT-004: Event from() 팩토리 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 이벤트에 from(Aggregate, Instant) 정적 팩토리 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EVT-004';

-- DOM-EVT-005: Event 과거형 네이밍 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 이벤트가 과거형으로 네이밍되어 있는가? (OrderCreatedEvent)', 'REVIEW', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EVT-005';

-- DOM-EVT-006: Event 패키지 위치
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 이벤트가 domain.{bc}.event 패키지에 위치하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EVT-006';

-- DOM-EXC-001: ErrorCode 인터페이스 구현 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ErrorCode enum이 ErrorCode 인터페이스를 구현하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-001';

-- DOM-EXC-002: ErrorCode 패키지 위치
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ErrorCode가 domain.{bc}.exception 패키지에 위치하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-002';

-- DOM-EXC-004: ErrorCode public 접근 제어자 (Zero-Tolerance - Lombok 금지)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Exception에서 Lombok(@Getter 등)을 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-004';

-- DOM-EXC-005: ErrorCode getCode() 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ErrorCode에 getCode() 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-005';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'getCode() 반환 형식이 {DOMAIN}-{NUMBER}인가? (ORD-001)', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-005';

-- DOM-EXC-006: ErrorCode getHttpStatus() int 반환
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ErrorCode의 getHttpStatus()가 int 타입을 반환하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-006';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Spring HttpStatus를 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-006';

-- DOM-EXC-007: ErrorCode getMessage() 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ErrorCode에 getMessage() 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-007';

-- DOM-EXC-009: DomainException 상속 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 예외 클래스가 DomainException을 상속받는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-009';

-- DOM-EXC-010: Exception 패키지 위치
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 예외가 domain.{bc}.exception 패키지에 위치하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-010';

-- DOM-EXC-014: Exception public 클래스
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 예외가 public 클래스로 선언되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-014';

-- DOM-EXC-015: Exception RuntimeException 계층
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 예외가 RuntimeException 계층인가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-015';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Checked Exception을 사용하지 않는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-015';

-- DOM-EXC-018: Exception 비즈니스 네이밍
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 예외가 비즈니스 의미가 명확한 이름인가? (OrderNotFoundException)', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-018';

-- DOM-EXC-019: DomainException common 패키지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'DomainException 추상 클래스가 domain.common.exception 패키지에 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-019';

-- DOM-EXC-020: ErrorCode 인터페이스 common 패키지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ErrorCode 인터페이스가 domain.common.exception 패키지에 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-EXC-020';

-- DOM-CRI-001: Criteria 패키지 위치 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Criteria가 domain.{bc}.query.criteria 패키지에 위치하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CRI-001';

-- DOM-CRI-002: Criteria 네이밍 규칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Criteria가 {Domain}SliceCriteria 또는 {Domain}SearchCriteria 형태인가?', 'REVIEW', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CRI-002';

-- DOM-CRI-003: Criteria public 접근 제어자
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Criteria가 public으로 선언되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CRI-003';

-- DOM-CRI-004: Criteria Record 타입 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Criteria가 Java Record로 정의되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CRI-004';

-- DOM-CRI-005: Criteria of() 팩토리 메서드 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Criteria에 of() 정적 팩토리 메서드가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CRI-005';

-- DOM-CRI-010: Criteria 공통 VO 사용 권장 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Criteria에서 JPA/Spring 어노테이션을 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CRI-010';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'DateRange, CursorQueryContext 등 공통 VO를 활용하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CRI-010';

-- DOM-CMN-001: 순수 자바 객체 원칙 (POJO)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 객체가 순수 자바 객체(POJO)인가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CMN-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Lombok, JPA, Spring 어노테이션을 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CMN-001';

-- DOM-CMN-002: 외부 레이어 의존 금지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 객체가 Application, Persistence, REST API 레이어를 의존하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CMN-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Repository, Port, Service, Controller, Entity, DTO를 import하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'DOM-CMN-002';

-- =====================================================
-- APPLICATION Layer (37개 규칙)
-- =====================================================

-- APP-TRX-001: Service @Transactional 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Service 클래스에 @Transactional 어노테이션이 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TRX-001';

-- APP-TRX-002: Manager @Transactional 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'CommandManager/QueryManager에 @Transactional이 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TRX-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'QueryManager는 @Transactional(readOnly=true)를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TRX-002';

-- APP-TRX-003: ClientManager @Transactional 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ClientManager에 @Transactional 어노테이션이 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TRX-003';

-- APP-TRX-004: CommandFacade @Transactional 선택
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'CommandFacade에서 DB 원자성 필요 시에만 @Transactional을 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TRX-004';

-- APP-TRX-005: QueryFacade @Transactional(readOnly=true)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'QueryFacade에 @Transactional(readOnly=true)가 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TRX-005';

-- APP-DEP-001: Service → Facade/Manager 의존
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Service가 Facade 또는 Manager에 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Manager 2개 이상 조합 시 Facade를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-001';

-- APP-DEP-002: Facade → Manager 의존 (같은 CQRS)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Facade가 같은 CQRS 범위의 Manager만 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'CommandFacade가 CommandManager만 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-002';

-- APP-DEP-003: CommandFacade → QueryManager 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'CommandFacade에서 QueryManager를 의존하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-003';

-- APP-DEP-004: Validator → 단일 도메인 ReadManager만
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Validator가 자기 도메인의 ReadManager만 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-004';

-- APP-DEP-005: Internal → Manager 의존 (Port 직접 금지)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Calculator, Resolver 등이 Manager를 통해 데이터에 접근하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-005';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Port를 직접 의존하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DEP-005';

-- APP-DTO-001: Command/Query Record 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Command/Query DTO가 Java Record로 정의되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DTO-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Lombok(@Data, @Builder 등)을 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DTO-001';

-- APP-DTO-002: Command/Query 인스턴스 메서드 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Command/Query에 인스턴스 메서드가 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DTO-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Compact Constructor에 로직이 없는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DTO-002';

-- APP-DTO-003: SearchParams CommonSearchParams 포함 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '*SearchParams가 CommonSearchParams를 포함하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DTO-003';

-- APP-DTO-004: CursorParams CommonCursorParams 포함 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '*CursorParams가 CommonCursorParams를 포함하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DTO-004';

-- APP-DTO-005: Bundle withId() 패턴
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Bundle DTO에 withId() 메서드가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-DTO-005';

-- APP-PRT-001: CommandPort persist/persistAll만
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'CommandPort에 persist, persistAll 메서드만 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-PRT-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'update 메서드가 없는가? (Dirty Checking 사용)', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-PRT-001';

-- APP-PRT-002: QueryPort findAll 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'QueryPort에 findAll() 메서드가 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-PRT-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '전체 데이터 로딩 대신 findBySliceCriteria를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-PRT-002';

-- APP-PRT-003: QueryPort 네이밍 규칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'QueryPort 메서드가 findById, findBy*, existsBy*, countBy* 패턴을 따르는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-PRT-003';

-- APP-PRT-004: Port 파라미터 Domain VO 사용 원칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Port 파라미터가 원시타입 대신 Domain VO를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-PRT-004';

-- APP-EVT-001: ApplicationEventPublisher 직접 주입 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Service에 ApplicationEventPublisher가 직접 주입되지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-EVT-001';

-- APP-EVT-002: TransactionEventRegistry 사용 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인 이벤트 발행 시 TransactionEventRegistry를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-EVT-002';

-- APP-TIM-001: TimeProvider Factory에서만 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'TimeProvider.now()가 Factory에서만 호출되는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TIM-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Service에서 TimeProvider를 직접 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-TIM-001';

-- APP-FAC-001: Factory 사용 기준
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Factory가 복잡한 객체 생성과 TimeProvider 필요 작업에만 사용되는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-FAC-001';

-- APP-VAL-001: Validator Domain 반환
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Validator의 validate*Exists 메서드가 Domain 객체를 반환하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-VAL-001';

-- APP-VAL-002: Validator 도메인 전용 예외
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Validator가 도메인 전용 예외(OrderNotFoundException 등)를 발생시키는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-VAL-002';

-- APP-ASM-001: 도메인별 구체 Result 클래스
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Assembler가 도메인별 구체 Result 클래스(OrderSliceResult)를 반환하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-ASM-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '제네릭 래퍼(SliceResult<T>)를 사용하지 않는가?', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-ASM-001';

-- APP-ASM-002: 생성 응답 원시타입 ID
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '생성(Create) UseCase가 원시타입(Long)만 반환하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-ASM-002';

-- APP-SVC-001: UseCase 1:1 Service 구현
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '1 UseCase = 1 Service로 구현되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-SVC-001';

-- APP-SVC-002: UseCase 네이밍 규칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'UseCase 네이밍이 Create*, Update*, Get*, Search* 패턴을 따르는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-SVC-002';

-- APP-EXC-001: 도메인 전용 예외 클래스 사용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '범용 예외 클래스(EntityNotFoundException) 대신 도메인 전용 예외를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-EXC-001';

-- APP-EXC-002: DomainException 직접 사용 금지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'DomainException을 직접 throw하지 않고 상속 클래스를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-EXC-002';

-- APP-LSN-001: EventListener @Async 권장
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'EventListener에 @Async가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-LSN-001';

-- APP-LSN-002: EventListener Manager 의존
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'EventListener가 Manager에 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-LSN-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'Port를 직접 의존하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-LSN-002';

-- APP-UC-001: UseCase 조회 네이밍 규칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '커서 기반 조회가 Search{Domain}ByCursorUseCase 네이밍인가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-UC-001';

-- APP-UC-002: Delete 네이밍 금지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Delete 네이밍 대신 Archive/Deactivate/Disable을 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'APP-UC-002';

-- FAC-002: Factory 메서드에 DTO 통째로 전달
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Factory 메서드에 DTO를 통째로 전달하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'FAC-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '개별 파라미터로 풀어서 전달하지 않는가?', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'FAC-002';

-- FAC-008: Factory에서 UpdateContext로 한 번에 생성
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Factory에서 UpdateContext(id, updateData, changedAt)를 한 번에 생성하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'FAC-008';

-- =====================================================
-- PERSISTENCE Layer (ADAPTER_OUT) - 15개 규칙
-- =====================================================

-- PER-REP-001: JpaRepository save/saveAll만 사용 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'JpaRepository에서 save, saveAll 메서드만 사용하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-REP-001';

-- PER-REP-002: JpaRepository 커스텀 메서드 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'JpaRepository에 커스텀 쿼리 메서드(@Query, findBy* 등)가 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-REP-002';

-- PER-REP-003: 모든 조회는 QueryDslRepository (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '모든 조회 쿼리가 QueryDslRepository에서 구현되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-REP-003';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'JPQL, Native Query를 사용하지 않는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-REP-003';

-- PER-ADP-001: CommandAdapter는 JpaRepository만 의존 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'CommandAdapter가 JpaRepository만 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ADP-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'QueryDslRepository를 주입받지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ADP-001';

-- PER-ADP-002: QueryAdapter는 QueryDslRepository만 의존 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'QueryAdapter가 QueryDslRepository만 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ADP-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'JpaRepository를 주입받지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ADP-002';

-- PER-ENT-001: JPA 관계 어노테이션 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '@OneToMany, @ManyToOne, @OneToOne, @ManyToMany 어노테이션이 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ENT-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'FK는 Long 타입으로 관리하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ENT-001';

-- PER-ENT-002: Entity는 BaseAuditEntity 상속
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'JpaEntity가 BaseAuditEntity 또는 SoftDeletableEntity를 상속하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ENT-002';

-- PER-CFG-001: OSIV 비활성화 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'spring.jpa.open-in-view=false 설정이 있는가?', 'MANUAL', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-CFG-001';

-- PER-CFG-002: DDL-AUTO는 validate만 허용 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'spring.jpa.hibernate.ddl-auto=validate 설정이 있는가?', 'MANUAL', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-CFG-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'create, update, create-drop을 사용하지 않는가?', 'MANUAL', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-CFG-002';

-- PER-CND-001: BooleanExpression은 ConditionBuilder로 분리
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'QueryDslRepository의 where절 조건이 ConditionBuilder로 분리되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-CND-001';

-- PER-CND-002: deletedAt null 조건 필수 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Soft Delete 테이블 조회 시 deletedAt IS NULL 조건이 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-CND-002';

-- PER-MAP-001: EntityMapper 양방향 변환
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'EntityMapper에 toDomain(Entity), toEntity(Domain) 양방향 변환 메서드가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-MAP-001';

-- PER-FTS-001: FullText는 FunctionContributor 등록
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'MySQL MATCH AGAINST 사용 시 Hibernate FunctionContributor가 등록되어 있는가?', 'MANUAL', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-FTS-001';

-- PER-ADM-001: Admin 복잡 쿼리는 persistence-mysql-admin 모듈 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '조인, 서브쿼리 등 복잡한 쿼리가 persistence-mysql-admin 모듈에만 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ADM-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '도메인용 persistence-mysql에서는 단일 테이블 쿼리만 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ADM-001';

-- PER-ADM-002: Admin 모듈은 조인 허용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'persistence-mysql-admin 모듈에서 조인 사용 시 DTO Projection을 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'PER-ADM-002';

-- =====================================================
-- REST_API Layer (ADAPTER_IN) - 47개 규칙
-- =====================================================

-- API-CTR-001: @RestController 어노테이션 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Controller 클래스에 @RestController 어노테이션이 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-001';

-- API-CTR-002: DELETE 메서드 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'HTTP DELETE 메서드를 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-002';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'soft delete는 PATCH /{id}/delete로 구현되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-002';

-- API-CTR-003: UseCase 인터페이스 의존
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Controller가 UseCase(Port-In) 인터페이스에만 의존하는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-003';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, '구체 Service 클래스를 직접 의존하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-003';

-- API-CTR-004: ResponseEntity + ApiResponse 래핑
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '모든 응답이 ResponseEntity<ApiResponse<T>> 형태로 래핑되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-004';

-- API-CTR-005: Controller @Transactional 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Controller에 @Transactional 어노테이션이 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-005';

-- API-CTR-006: OpenAPI 어노테이션 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '@Tag, @Operation, @ApiResponses 어노테이션이 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-006';

-- API-CTR-007: Controller 비즈니스 로직 금지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Controller에 비즈니스 로직이 없고 Mapper에 위임하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-007';

-- API-CTR-008: Endpoints 상수 사용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '경로를 하드코딩하지 않고 *ApiEndpoints 상수 클래스를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-008';

-- API-CTR-009: @Valid 어노테이션 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Request DTO 파라미터에 @Valid 어노테이션이 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-009';

-- API-CTR-010: CQRS Controller 분리
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Command(CUD)와 Query(R) Controller가 별도 클래스로 분리되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-010';

-- API-CTR-011: List 직접 반환 금지 (Zero-Tolerance)
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '목록 조회 시 List를 직접 반환하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-011';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'SliceApiResponse 또는 PageApiResponse를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), TRUE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-011';

-- API-CTR-012: URL 경로 규칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'URL 경로가 소문자 + 복수형을 사용하는가? (/conventions, /modules)', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-012';

-- API-CTR-013: Controller 조회 메서드명 규칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '조회 메서드가 search{Bc}, search{Bc}ByCursor, list{Bc} 패턴을 따르는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CTR-013';

-- API-DTO-001: Record 타입 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Request/Response DTO가 Java Record로 정의되어 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-001';

-- API-DTO-002: DTO 불변성 보장
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'DTO가 불변 객체이고 Setter가 없는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-002';

-- API-DTO-003: Validation 어노테이션
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Request DTO에 Jakarta Validation 어노테이션(@NotNull, @NotBlank 등)이 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-003';

-- API-DTO-004: createdAt/updatedAt 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Response DTO에 createdAt, updatedAt 필드가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-004';

-- API-DTO-005: 날짜 String 변환 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Instant 타입이 DateTimeFormatUtils.formatIso8601()로 String 변환되는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-005';

-- API-DTO-006: Nested Record 허용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '복잡한 구조가 중첩 Record로 표현되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-006';

-- API-DTO-007: @Schema 어노테이션 권장
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'OpenAPI 문서화를 위해 @Schema 어노테이션이 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-007';

-- API-DTO-008: Optional 대신 Nullable
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Optional 대신 @Nullable 또는 기본값을 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-008';

-- API-DTO-009: List 필드 불변 복사
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'List 필드가 생성자에서 List.copyOf()로 방어적 복사되는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-009';

-- API-DTO-010: Request DTO 조회 네이밍 규칙
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Request DTO가 Search{Bc}ApiRequest, Search{Bc}CursorApiRequest, List{Bc}ApiRequest 패턴을 따르는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-DTO-010';

-- API-MAP-001: @Component 필수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Mapper 클래스에 @Component 어노테이션이 있는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-MAP-001';

-- API-MAP-002: 양방향 변환 지원
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Mapper가 Request→Query/Command, Result→Response 양방향 변환을 지원하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-MAP-002';

-- API-MAP-003: 날짜 포맷팅 담당
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Mapper에서 DateTimeFormatUtils.formatIso8601()로 날짜를 포맷팅하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-MAP-003';

-- API-MAP-004: Slice/Page 변환 지원
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Mapper에 SliceResult/PageResult를 SliceApiResponse/PageApiResponse로 변환하는 메서드가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-MAP-004';

-- API-MAP-005: 순수 변환 로직만
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Mapper가 순수 변환 로직만 담당하고 비즈니스 로직이 없는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-MAP-005';

-- API-MAP-006: 기본값 처리 담당
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Request에서 null인 필드의 기본값 처리를 Mapper에서 수행하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-MAP-006';

-- API-ERR-001: ErrorMapper 패턴
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '도메인별 ErrorMapper가 구현되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-ERR-001';
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 2, 'ErrorMapper가 supports() + map() 메서드를 구현하는가?', 'REVIEW', NULL, CONCAT(code, '-02'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-ERR-001';

-- API-ERR-002: ProblemDetail 응답
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '에러 응답이 RFC 7807 ProblemDetail 형식을 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-ERR-002';

-- API-ERR-003: x-error-code 헤더
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ProblemDetail에 x-error-code 확장 헤더가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-ERR-003';

-- API-ERR-004: GlobalExceptionHandler 사용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'GlobalExceptionHandler를 통한 전역 예외 처리가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-ERR-004';

-- API-ERR-005: application/problem+json
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '에러 응답의 Content-Type이 application/problem+json인가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-ERR-005';

-- API-CFG-001: JacksonConfig 설정
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'JacksonConfig에 SNAKE_CASE, JavaTimeModule이 설정되어 있는가?', 'MANUAL', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CFG-001';

-- API-CFG-002: OpenApiConfig 설정
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'OpenApiConfig에 GroupedOpenApi가 설정되어 있는가?', 'MANUAL', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CFG-002';

-- API-CFG-003: WebMvcConfig 설정
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'WebMvcConfig에 CORS, Interceptor, ArgumentResolver가 설정되어 있는가?', 'MANUAL', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CFG-003';

-- API-CFG-004: 서버 프리픽스 경로
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'server.servlet.context-path가 설정되어 있는가?', 'MANUAL', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-CFG-004';

-- API-END-001: Endpoints final class
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'Endpoints 상수 클래스가 final class + private 생성자인가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-END-001';

-- API-END-002: static final 상수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '모든 경로 상수가 public static final String으로 선언되어 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-END-002';

-- API-END-003: Path Variable 상수
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'PathVariable 이름도 상수로 관리되는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-END-003';

-- API-END-004: 도메인별 Endpoints 분리
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '각 도메인별로 별도의 *ApiEndpoints 클래스가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-END-004';

-- API-TST-001: MockMvc 금지
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '테스트에서 MockMvc를 사용하지 않는가?', 'AUTOMATED', 'archunit', CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-TST-001';

-- API-TST-002: TestRestTemplate 사용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '@SpringBootTest(webEnvironment = RANDOM_PORT) + TestRestTemplate을 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-TST-002';

-- API-TST-003: ParameterizedTypeReference 사용
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ApiResponse<T> 역직렬화 시 ParameterizedTypeReference를 사용하는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-TST-003';

-- API-TST-004: Fixture 패턴
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, '테스트 데이터 생성이 *Fixture 클래스의 static factory 메서드로 제공되는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-TST-004';

-- API-TST-005: ErrorMapper 단위 테스트
INSERT INTO checklist_item (rule_id, sequence_order, check_description, check_type, automation_tool, automation_rule_id, is_critical, source, created_at, updated_at)
SELECT id, 1, 'ErrorMapper에 supports() + map() 메서드 단위 테스트가 있는가?', 'REVIEW', NULL, CONCAT(code, '-01'), FALSE, 'CONVENTION_HUB', NOW(), NOW() FROM coding_rule WHERE code = 'API-TST-005';
