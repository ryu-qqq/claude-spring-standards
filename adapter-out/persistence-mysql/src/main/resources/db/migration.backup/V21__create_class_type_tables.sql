-- ============================================
-- Spring Standards - ClassType Table Migration V21
-- ClassType Enum(77개)을 별도 테이블로 분리
-- Created: 2026-01-26
-- ============================================

-- ============================================
-- 1. class_type_category (architecture 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `class_type_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `architecture_id` BIGINT NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `order_index` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_class_type_category_architecture_id` (`architecture_id`),
    UNIQUE KEY `uk_class_type_category_arch_code` (`architecture_id`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. class_type (category 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `class_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `category_id` BIGINT NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `order_index` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_class_type_category_id` (`category_id`),
    UNIQUE KEY `uk_class_type_category_code` (`category_id`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. class_template 테이블에 class_type_id 컬럼 추가
-- ============================================
ALTER TABLE `class_template` ADD COLUMN `class_type_id` BIGINT AFTER `structure_id`;
CREATE INDEX `idx_class_template_class_type_id` ON `class_template` (`class_type_id`);

-- ============================================
-- 4. 시드 데이터 - class_type_category
-- ============================================
INSERT INTO `class_type_category` (`architecture_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(1, 'DOMAIN_TYPES', 'Domain Layer Types', '도메인 레이어에서 사용되는 클래스 유형', 1, NOW(6), NOW(6)),
(1, 'APPLICATION_TYPES', 'Application Layer Types', '애플리케이션 레이어에서 사용되는 클래스 유형', 2, NOW(6), NOW(6)),
(1, 'PERSISTENCE_TYPES', 'Persistence Layer Types', '영속성 레이어에서 사용되는 클래스 유형', 3, NOW(6), NOW(6)),
(1, 'REST_API_TYPES', 'REST API Layer Types', 'REST API 레이어에서 사용되는 클래스 유형', 4, NOW(6), NOW(6)),
(1, 'COMMON_TYPES', 'Common Types', '공통으로 사용되는 클래스 유형', 5, NOW(6), NOW(6)),
(1, 'COMMON_VO_TYPES', 'Common VO Types', '공통 Value Object 템플릿 유형', 6, NOW(6), NOW(6)),
(1, 'PAGINATION_TYPES', 'Pagination & Query Types', '페이지네이션 및 쿼리 관련 템플릿 유형', 7, NOW(6), NOW(6));

-- ============================================
-- 5. 시드 데이터 - class_type (Domain Layer)
-- ============================================
SET @domain_cat_id = (SELECT id FROM class_type_category WHERE code = 'DOMAIN_TYPES' AND architecture_id = 1);

INSERT INTO `class_type` (`category_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(@domain_cat_id, 'AGGREGATE', 'Aggregate', 'Aggregate Root 클래스', 1, NOW(6), NOW(6)),
(@domain_cat_id, 'AGGREGATE_ROOT', 'Aggregate Root', 'Aggregate Root 클래스 (별칭)', 2, NOW(6), NOW(6)),
(@domain_cat_id, 'VALUE_OBJECT', 'Value Object', 'Value Object 클래스', 3, NOW(6), NOW(6)),
(@domain_cat_id, 'DOMAIN_EVENT', 'Domain Event', '도메인 이벤트', 4, NOW(6), NOW(6)),
(@domain_cat_id, 'DOMAIN_EVENT_INTERFACE', 'Domain Event Interface', '도메인 이벤트 인터페이스', 5, NOW(6), NOW(6)),
(@domain_cat_id, 'DOMAIN_EXCEPTION', 'Domain Exception', '도메인 예외', 6, NOW(6), NOW(6)),
(@domain_cat_id, 'DOMAIN_CRITERIA', 'Domain Criteria', '도메인 검색 조건', 7, NOW(6), NOW(6)),
(@domain_cat_id, 'ERROR_CODE', 'Error Code', '에러 코드', 8, NOW(6), NOW(6)),
(@domain_cat_id, 'ERROR_CODE_INTERFACE', 'Error Code Interface', '에러 코드 인터페이스', 9, NOW(6), NOW(6));

-- ============================================
-- 6. 시드 데이터 - class_type (Application Layer)
-- ============================================
SET @app_cat_id = (SELECT id FROM class_type_category WHERE code = 'APPLICATION_TYPES' AND architecture_id = 1);

INSERT INTO `class_type` (`category_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(@app_cat_id, 'USE_CASE', 'UseCase', 'UseCase 인터페이스', 1, NOW(6), NOW(6)),
(@app_cat_id, 'COMMAND_SERVICE', 'Command Service', 'Command Service 구현체', 2, NOW(6), NOW(6)),
(@app_cat_id, 'QUERY_SERVICE', 'Query Service', 'Query Service 구현체', 3, NOW(6), NOW(6)),
(@app_cat_id, 'PORT_IN', 'Port-In', 'Port-In 인터페이스', 4, NOW(6), NOW(6)),
(@app_cat_id, 'PORT_OUT', 'Port-Out', 'Port-Out 인터페이스', 5, NOW(6), NOW(6)),
(@app_cat_id, 'FACADE', 'Facade', 'Facade 컴포넌트', 6, NOW(6), NOW(6)),
(@app_cat_id, 'MANAGER', 'Manager', 'Manager 컴포넌트', 7, NOW(6), NOW(6)),
(@app_cat_id, 'FACTORY', 'Factory', 'Factory 컴포넌트', 8, NOW(6), NOW(6)),
(@app_cat_id, 'ASSEMBLER', 'Assembler', 'Assembler 컴포넌트', 9, NOW(6), NOW(6)),
(@app_cat_id, 'EVENT_LISTENER', 'Event Listener', '이벤트 리스너', 10, NOW(6), NOW(6)),
(@app_cat_id, 'SCHEDULER', 'Scheduler', '스케줄러', 11, NOW(6), NOW(6));

-- ============================================
-- 7. 시드 데이터 - class_type (Persistence Layer)
-- ============================================
SET @persistence_cat_id = (SELECT id FROM class_type_category WHERE code = 'PERSISTENCE_TYPES' AND architecture_id = 1);

INSERT INTO `class_type` (`category_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(@persistence_cat_id, 'ENTITY', 'Entity', 'JPA Entity', 1, NOW(6), NOW(6)),
(@persistence_cat_id, 'JPA_REPOSITORY', 'JPA Repository', 'JPA Repository 인터페이스', 2, NOW(6), NOW(6)),
(@persistence_cat_id, 'QUERYDSL_REPOSITORY', 'QueryDSL Repository', 'QueryDSL Repository', 3, NOW(6), NOW(6)),
(@persistence_cat_id, 'COMMAND_ADAPTER', 'Command Adapter', 'Command Adapter', 4, NOW(6), NOW(6)),
(@persistence_cat_id, 'QUERY_ADAPTER', 'Query Adapter', 'Query Adapter', 5, NOW(6), NOW(6)),
(@persistence_cat_id, 'ENTITY_MAPPER', 'Entity Mapper', 'Entity Mapper', 6, NOW(6), NOW(6));

-- ============================================
-- 8. 시드 데이터 - class_type (REST API Layer)
-- ============================================
SET @rest_cat_id = (SELECT id FROM class_type_category WHERE code = 'REST_API_TYPES' AND architecture_id = 1);

INSERT INTO `class_type` (`category_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(@rest_cat_id, 'REST_CONTROLLER', 'REST Controller', 'REST Controller', 1, NOW(6), NOW(6)),
(@rest_cat_id, 'COMMAND_CONTROLLER', 'Command Controller', 'Command Controller', 2, NOW(6), NOW(6)),
(@rest_cat_id, 'QUERY_CONTROLLER', 'Query Controller', 'Query Controller', 3, NOW(6), NOW(6)),
(@rest_cat_id, 'REQUEST_DTO', 'Request DTO', 'Request DTO', 4, NOW(6), NOW(6)),
(@rest_cat_id, 'RESPONSE_DTO', 'Response DTO', 'Response DTO', 5, NOW(6), NOW(6)),
(@rest_cat_id, 'API_MAPPER', 'API Mapper', 'API Mapper', 6, NOW(6), NOW(6)),
(@rest_cat_id, 'ERROR_HANDLER', 'Error Handler', '에러 핸들러', 7, NOW(6), NOW(6));

-- ============================================
-- 9. 시드 데이터 - class_type (Common)
-- ============================================
SET @common_cat_id = (SELECT id FROM class_type_category WHERE code = 'COMMON_TYPES' AND architecture_id = 1);

INSERT INTO `class_type` (`category_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(@common_cat_id, 'CONFIG', 'Config', '설정 클래스', 1, NOW(6), NOW(6)),
(@common_cat_id, 'COMMON_VO', 'Common VO', '공통 Value Object', 2, NOW(6), NOW(6));

-- ============================================
-- 10. 시드 데이터 - class_type (Common VO Templates)
-- ============================================
SET @common_vo_cat_id = (SELECT id FROM class_type_category WHERE code = 'COMMON_VO_TYPES' AND architecture_id = 1);

INSERT INTO `class_type` (`category_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(@common_vo_cat_id, 'AUTO_INCREMENT_ID', 'Auto Increment ID', '자동 증가 ID', 1, NOW(6), NOW(6)),
(@common_vo_cat_id, 'GENERATED_LONG_ID', 'Generated Long ID', '생성된 Long ID', 2, NOW(6), NOW(6)),
(@common_vo_cat_id, 'GENERATED_STRING_ID', 'Generated String ID', '생성된 String ID', 3, NOW(6), NOW(6)),
(@common_vo_cat_id, 'UPDATE_DATA', 'Update Data', '업데이트 데이터', 4, NOW(6), NOW(6)),
(@common_vo_cat_id, 'DELETION_STATUS_RECORD', 'Deletion Status Record', '삭제 상태 레코드', 5, NOW(6), NOW(6)),
(@common_vo_cat_id, 'CACHE_KEY_INTERFACE', 'Cache Key Interface', '캐시 키 인터페이스', 6, NOW(6), NOW(6)),
(@common_vo_cat_id, 'LOCK_KEY_INTERFACE', 'Lock Key Interface', '락 키 인터페이스', 7, NOW(6), NOW(6)),
(@common_vo_cat_id, 'SORT_KEY_INTERFACE', 'Sort Key Interface', '정렬 키 인터페이스', 8, NOW(6), NOW(6)),
(@common_vo_cat_id, 'SORT_DIRECTION_ENUM', 'Sort Direction Enum', '정렬 방향 Enum', 9, NOW(6), NOW(6));

-- ============================================
-- 11. 시드 데이터 - class_type (Pagination & Query)
-- ============================================
SET @pagination_cat_id = (SELECT id FROM class_type_category WHERE code = 'PAGINATION_TYPES' AND architecture_id = 1);

INSERT INTO `class_type` (`category_id`, `code`, `name`, `description`, `order_index`, `created_at`, `updated_at`) VALUES
(@pagination_cat_id, 'PAGE_REQUEST_RECORD', 'Page Request Record', '페이지 요청 레코드', 1, NOW(6), NOW(6)),
(@pagination_cat_id, 'PAGE_META_RECORD', 'Page Meta Record', '페이지 메타 레코드', 2, NOW(6), NOW(6)),
(@pagination_cat_id, 'PAGE_CRITERIA_RECORD', 'Page Criteria Record', '페이지 검색 조건 레코드', 3, NOW(6), NOW(6)),
(@pagination_cat_id, 'SLICE_META_RECORD', 'Slice Meta Record', '슬라이스 메타 레코드', 4, NOW(6), NOW(6)),
(@pagination_cat_id, 'CURSOR_PAGE_REQUEST_RECORD', 'Cursor Page Request Record', '커서 페이지 요청 레코드', 5, NOW(6), NOW(6)),
(@pagination_cat_id, 'CURSOR_CRITERIA_RECORD', 'Cursor Criteria Record', '커서 검색 조건 레코드', 6, NOW(6), NOW(6)),
(@pagination_cat_id, 'CURSOR_QUERY_CONTEXT_RECORD', 'Cursor Query Context Record', '커서 쿼리 컨텍스트 레코드', 7, NOW(6), NOW(6)),
(@pagination_cat_id, 'QUERY_CONTEXT_RECORD', 'Query Context Record', '쿼리 컨텍스트 레코드', 8, NOW(6), NOW(6)),
(@pagination_cat_id, 'DATE_RANGE_RECORD', 'Date Range Record', '날짜 범위 레코드', 9, NOW(6), NOW(6));

-- ============================================
-- 12. 기존 class_template 데이터 마이그레이션
-- ============================================
UPDATE `class_template` ct
JOIN `class_type` ctype ON ct.class_type = ctype.code
SET ct.class_type_id = ctype.id
WHERE ct.class_type_id IS NULL;

-- ============================================
-- 13. class_type_id NOT NULL 제약 추가 및 class_type 컬럼 삭제
-- ============================================
-- 주의: 모든 데이터 마이그레이션이 완료된 후 실행
-- ALTER TABLE `class_template` MODIFY COLUMN `class_type_id` BIGINT NOT NULL;
-- ALTER TABLE `class_template` DROP COLUMN `class_type`;
