-- ============================================
-- Spring Standards - Layer Code 변경
-- V4: Layer 코드를 헥사고날 아키텍처에 맞게 수정
-- Created: 2026-01-20
-- ============================================
-- 변경 사항:
--   - PERSISTENCE → ADAPTER_OUT
--   - REST_API → ADAPTER_IN
--   - BOOTSTRAP 레이어 신규 추가
-- ============================================

-- ============================================
-- 1. layer 테이블 업데이트
-- ============================================

-- PERSISTENCE → ADAPTER_OUT
UPDATE `layer` SET
    `code` = 'ADAPTER_OUT',
    `name` = 'Adapter-Out Layer',
    `description` = '아웃바운드 어댑터 레이어. 외부 시스템(DB, 외부 API, 메시지 큐 등)과의 통신을 담당합니다. Port-Out 인터페이스의 구현체가 위치합니다.',
    `updated_at` = NOW()
WHERE `id` = 3;

-- REST_API → ADAPTER_IN
UPDATE `layer` SET
    `code` = 'ADAPTER_IN',
    `name` = 'Adapter-In Layer',
    `description` = '인바운드 어댑터 레이어. 외부 요청(REST API, gRPC, CLI 등)을 받아 애플리케이션으로 전달합니다. Controller, API DTO, Mapper를 포함합니다.',
    `updated_at` = NOW()
WHERE `id` = 4;

-- BOOTSTRAP 레이어 신규 추가
INSERT INTO `layer` (
    `id`, `architecture_id`, `code`, `name`,
    `description`, `order_index`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    5, 1, 'BOOTSTRAP', 'Bootstrap Layer',
    '애플리케이션 실행 진입점 레이어. main() 메서드, Spring Boot Application 클래스, 설정 파일이 위치합니다. 모든 모듈을 조립하여 실행 가능한 애플리케이션을 구성합니다.',
    5, NOW(), NOW(), NULL
);

-- ============================================
-- 2. layer_dependency_rule 테이블 업데이트
-- ============================================

-- 기존 PERSISTENCE 참조 → ADAPTER_OUT으로 변경
UPDATE `layer_dependency_rule` SET
    `from_layer` = 'ADAPTER_OUT',
    `condition_description` = 'Adapter-Out 레이어는 Domain 레이어에 의존할 수 있습니다.',
    `updated_at` = NOW()
WHERE `from_layer` = 'PERSISTENCE';

UPDATE `layer_dependency_rule` SET
    `to_layer` = 'ADAPTER_OUT',
    `condition_description` = 'Domain 레이어는 Adapter-Out 레이어에 의존할 수 없습니다.',
    `updated_at` = NOW()
WHERE `to_layer` = 'PERSISTENCE';

-- 기존 REST_API 참조 → ADAPTER_IN으로 변경
UPDATE `layer_dependency_rule` SET
    `from_layer` = 'ADAPTER_IN',
    `condition_description` = 'Adapter-In 레이어는 Application 레이어에 의존할 수 있습니다.',
    `updated_at` = NOW()
WHERE `from_layer` = 'REST_API';

UPDATE `layer_dependency_rule` SET
    `to_layer` = 'ADAPTER_IN',
    `condition_description` = 'Domain 레이어는 Adapter-In 레이어에 의존할 수 없습니다.',
    `updated_at` = NOW()
WHERE `to_layer` = 'REST_API';

-- BOOTSTRAP 의존성 규칙 추가
INSERT INTO `layer_dependency_rule` (
    `id`, `architecture_id`, `from_layer`, `to_layer`,
    `dependency_type`, `condition_description`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (7, 1, 'BOOTSTRAP', 'ADAPTER_IN', 'ALLOWED', 'Bootstrap 레이어는 Adapter-In 레이어에 의존할 수 있습니다.', NOW(), NOW(), NULL),
    (8, 1, 'BOOTSTRAP', 'ADAPTER_OUT', 'ALLOWED', 'Bootstrap 레이어는 Adapter-Out 레이어에 의존할 수 있습니다.', NOW(), NOW(), NULL),
    (9, 1, 'BOOTSTRAP', 'APPLICATION', 'ALLOWED', 'Bootstrap 레이어는 Application 레이어에 의존할 수 있습니다.', NOW(), NOW(), NULL),
    (10, 1, 'BOOTSTRAP', 'DOMAIN', 'ALLOWED', 'Bootstrap 레이어는 Domain 레이어에 의존할 수 있습니다.', NOW(), NOW(), NULL),
    (11, 1, 'DOMAIN', 'BOOTSTRAP', 'FORBIDDEN', 'Domain 레이어는 Bootstrap 레이어에 의존할 수 없습니다.', NOW(), NOW(), NULL),
    (12, 1, 'APPLICATION', 'BOOTSTRAP', 'FORBIDDEN', 'Application 레이어는 Bootstrap 레이어에 의존할 수 없습니다.', NOW(), NOW(), NULL),
    (13, 1, 'ADAPTER_IN', 'BOOTSTRAP', 'FORBIDDEN', 'Adapter-In 레이어는 Bootstrap 레이어에 의존할 수 없습니다.', NOW(), NOW(), NULL),
    (14, 1, 'ADAPTER_OUT', 'BOOTSTRAP', 'FORBIDDEN', 'Adapter-Out 레이어는 Bootstrap 레이어에 의존할 수 없습니다.', NOW(), NOW(), NULL);

-- ============================================
-- 3. convention 테이블 - BOOTSTRAP용 컨벤션 추가
-- ============================================
INSERT INTO `convention` (
    `id`, `layer_id`, `version`,
    `description`, `is_active`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (5, 5, '1.0.0', 'Bootstrap Layer 코딩 컨벤션', TRUE, NOW(), NOW(), NULL);

-- ============================================
-- 4. module 테이블 - 신규 모듈 추가
-- ============================================
INSERT INTO `module` (
    `id`, `layer_id`, `parent_module_id`, `name`,
    `description`, `gradle_path`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    -- Bootstrap 부모 모듈
    (7, 5, NULL, 'bootstrap', 'Bootstrap 부모 모듈', ':bootstrap', NOW(), NOW(), NULL),
    -- Bootstrap Web API 모듈
    (8, 5, 7, 'bootstrap-web-api', 'Web API 실행 모듈', ':bootstrap:bootstrap-web-api', NOW(), NOW(), NULL),
    -- Bootstrap Scheduler 모듈
    (9, 5, 7, 'bootstrap-scheduler', 'Scheduler 실행 모듈', ':bootstrap:bootstrap-scheduler', NOW(), NOW(), NULL),
    -- Bootstrap Worker 모듈
    (16, 5, 7, 'bootstrap-worker', '메시지 리스너 및 백그라운드 작업 실행 모듈', ':bootstrap:bootstrap-worker', NOW(), NOW(), NULL),

    -- Adapter-Out 추가 모듈 (parent: adapter-out, id=3)
    (10, 3, 3, 'persistence-redis', 'Redis 캐시 어댑터', ':adapter-out:persistence-redis', NOW(), NOW(), NULL),
    (11, 3, 3, 'sqs-publisher', 'AWS SQS 메시지 발행 어댑터', ':adapter-out:sqs-publisher', NOW(), NOW(), NULL),
    -- Client 부모 모듈 (parent: adapter-out, id=3)
    (14, 3, 3, 'client', '외부 클라이언트 부모 모듈', ':adapter-out:client', NOW(), NOW(), NULL),
    -- Client 하위 모듈 (parent: client, id=14)
    (15, 3, 14, 'distributed-lock-redis', 'Redis 기반 분산락 클라이언트', ':adapter-out:client:distributed-lock-redis', NOW(), NOW(), NULL),

    -- Adapter-In 추가 모듈 (parent: adapter-in, id=5)
    (12, 4, 5, 'sqs-consumer', 'AWS SQS 메시지 소비 어댑터', ':adapter-in:sqs-consumer', NOW(), NOW(), NULL),
    (13, 4, 5, 'scheduler', '스케줄 작업 어댑터', ':adapter-in:scheduler', NOW(), NOW(), NULL);

-- ============================================
-- 완료
-- ============================================
