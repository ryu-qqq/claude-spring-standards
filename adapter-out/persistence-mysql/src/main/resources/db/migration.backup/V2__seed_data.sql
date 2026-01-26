-- ============================================
-- Spring Standards - Seed Data V2
-- 초기 데이터 마이그레이션 템플릿
-- Created: 2026-01-20
-- ============================================
-- 참고: 레거시 시드 데이터는 db/legacy/V1__seed_data.sql에 백업되어 있습니다.
-- ============================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ============================================
-- 1. tech_stack (기술 스택)
-- ============================================
LOCK TABLES `tech_stack` WRITE;
/*!40000 ALTER TABLE `tech_stack` DISABLE KEYS */;
INSERT INTO `tech_stack` (
    `id`, `name`, `status`,
    `language_type`, `language_version`, `language_features`,
    `framework_type`, `framework_version`, `framework_modules`,
    `platform_type`, `runtime_environment`,
    `build_tool_type`, `build_config_file`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    1, 'java21-springboot35-backend', 'ACTIVE',
    'JAVA', '21', '["VIRTUAL_THREAD", "RECORD", "SEALED_CLASS", "PATTERN_MATCHING", "SEQUENCED_COLLECTION"]',
    'SPRING_BOOT', '3.5.x', '["WEB", "JPA", "VALIDATION", "SECURITY", "ACTUATOR"]',
    'BACKEND', 'JVM',
    'GRADLE', 'build.gradle.kts',
    NOW(), NOW(), NULL
);
/*!40000 ALTER TABLE `tech_stack` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 2. architecture (아키텍처)
-- ============================================
LOCK TABLES `architecture` WRITE;
/*!40000 ALTER TABLE `architecture` DISABLE KEYS */;
INSERT INTO `architecture` (
    `id`, `tech_stack_id`, `name`,
    `pattern_type`, `pattern_description`, `pattern_principles`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES (
    1, 1, 'hexagonal-multimodule',
    'HEXAGONAL',
    '포트와 어댑터 패턴 기반 멀티모듈 아키텍처. Domain 중심 설계로 외부 의존성을 격리하고, CQRS 패턴을 적용하여 Command/Query를 분리합니다.',
    '["DIP", "SRP", "OCP", "ISP", "CQRS", "DDD"]',
    NOW(), NOW(), NULL
);
/*!40000 ALTER TABLE `architecture` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 3. layer (레이어) - 신규 테이블
-- ============================================
LOCK TABLES `layer` WRITE;
/*!40000 ALTER TABLE `layer` DISABLE KEYS */;
INSERT INTO `layer` (
    `id`, `architecture_id`, `code`, `name`,
    `description`, `order_index`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (1, 1, 'DOMAIN', 'Domain Layer',
     '비즈니스 로직과 도메인 모델을 포함하는 핵심 레이어. Aggregate, Value Object, Domain Event, Domain Exception을 정의합니다.',
     1, NOW(), NOW(), NULL),
    (2, 1, 'APPLICATION', 'Application Layer',
     '애플리케이션 비즈니스 로직을 조율하는 레이어. UseCase(Port-In), Service, Manager, Port-Out을 정의합니다.',
     2, NOW(), NOW(), NULL),
    (3, 1, 'PERSISTENCE', 'Persistence Layer',
     '데이터 영속성을 담당하는 어댑터 레이어. JPA Entity, Repository, QueryDSL을 사용합니다.',
     3, NOW(), NOW(), NULL),
    (4, 1, 'REST_API', 'REST API Layer',
     'HTTP 요청을 처리하는 인바운드 어댑터 레이어. Controller, API DTO, Mapper를 정의합니다.',
     4, NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `layer` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 4. convention (컨벤션)
-- ============================================
LOCK TABLES `convention` WRITE;
/*!40000 ALTER TABLE `convention` DISABLE KEYS */;
INSERT INTO `convention` (
    `id`, `layer_id`, `version`,
    `description`, `is_active`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (1, 1, '1.0.0', 'Domain Layer 코딩 컨벤션', TRUE, NOW(), NOW(), NULL),
    (2, 2, '1.0.0', 'Application Layer 코딩 컨벤션', TRUE, NOW(), NOW(), NULL),
    (3, 3, '1.0.0', 'Persistence Layer 코딩 컨벤션', TRUE, NOW(), NOW(), NULL),
    (4, 4, '1.0.0', 'REST API Layer 코딩 컨벤션', TRUE, NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `convention` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 5. layer_dependency_rule (레이어 의존성 규칙)
-- ============================================
LOCK TABLES `layer_dependency_rule` WRITE;
/*!40000 ALTER TABLE `layer_dependency_rule` DISABLE KEYS */;
INSERT INTO `layer_dependency_rule` (
    `id`, `architecture_id`, `from_layer`, `to_layer`,
    `dependency_type`, `condition_description`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (1, 1, 'APPLICATION', 'DOMAIN', 'ALLOWED', 'Application 레이어는 Domain 레이어에 의존할 수 있습니다.', NOW(), NOW(), NULL),
    (2, 1, 'PERSISTENCE', 'DOMAIN', 'ALLOWED', 'Persistence 레이어는 Domain 레이어에 의존할 수 있습니다.', NOW(), NOW(), NULL),
    (3, 1, 'REST_API', 'APPLICATION', 'ALLOWED', 'REST API 레이어는 Application 레이어에 의존할 수 있습니다.', NOW(), NOW(), NULL),
    (4, 1, 'DOMAIN', 'APPLICATION', 'FORBIDDEN', 'Domain 레이어는 Application 레이어에 의존할 수 없습니다.', NOW(), NOW(), NULL),
    (5, 1, 'DOMAIN', 'PERSISTENCE', 'FORBIDDEN', 'Domain 레이어는 Persistence 레이어에 의존할 수 없습니다.', NOW(), NOW(), NULL),
    (6, 1, 'DOMAIN', 'REST_API', 'FORBIDDEN', 'Domain 레이어는 REST API 레이어에 의존할 수 없습니다.', NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `layer_dependency_rule` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 6. module (모듈)
-- ============================================
LOCK TABLES `module` WRITE;
/*!40000 ALTER TABLE `module` DISABLE KEYS */;
INSERT INTO `module` (
    `id`, `layer_id`, `parent_module_id`, `name`,
    `description`, `gradle_path`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    -- Domain 모듈
    (1, 1, NULL, 'domain', 'Domain 핵심 모듈', ':domain', NOW(), NOW(), NULL),
    -- Application 모듈
    (2, 2, NULL, 'application', 'Application 서비스 모듈', ':application', NOW(), NOW(), NULL),
    -- Persistence 모듈
    (3, 3, NULL, 'adapter-out', 'Outbound Adapter 부모 모듈', ':adapter-out', NOW(), NOW(), NULL),
    (4, 3, 3, 'persistence-mysql', 'MySQL Persistence 어댑터', ':adapter-out:persistence-mysql', NOW(), NOW(), NULL),
    -- REST API 모듈
    (5, 4, NULL, 'adapter-in', 'Inbound Adapter 부모 모듈', ':adapter-in', NOW(), NOW(), NULL),
    (6, 4, 5, 'rest-api', 'REST API 어댑터', ':adapter-in:rest-api', NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `module` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 7. package_purpose (패키지 목적 - package_structure 참조)
-- ============================================
LOCK TABLES `package_purpose` WRITE;
/*!40000 ALTER TABLE `package_purpose` DISABLE KEYS */;
INSERT INTO `package_purpose` (
    `id`, `structure_id`, `code`, `name`,
    `description`, `default_allowed_class_types`,
    `default_naming_pattern`, `default_naming_suffix`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    -- Domain Aggregate Structure Purposes (structure_id=1)
    (1, 1, 'AGGREGATE', 'Aggregate 패키지', 'DDD Aggregate Root와 엔티티를 포함합니다.', '["AGGREGATE_ROOT", "ENTITY"]', NULL, NULL, NOW(), NOW(), NULL),
    -- Domain VO Structure Purposes (structure_id=2)
    (2, 2, 'VO', 'Value Object 패키지', '불변 값 객체를 포함합니다.', '["VALUE_OBJECT", "RECORD"]', NULL, NULL, NOW(), NOW(), NULL),
    -- Domain ID Structure Purposes (structure_id=3)
    (3, 3, 'ID', 'ID Value Object 패키지', '식별자 값 객체를 포함합니다.', '["RECORD"]', '*Id', 'Id', NOW(), NOW(), NULL),
    -- Domain Exception Structure Purposes (structure_id=4)
    (4, 4, 'EXCEPTION', 'Exception 패키지', '도메인 예외를 포함합니다.', '["EXCEPTION"]', '*Exception', 'Exception', NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `package_purpose` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 8. package_structure (패키지 구조)
-- ============================================
LOCK TABLES `package_structure` WRITE;
/*!40000 ALTER TABLE `package_structure` DISABLE KEYS */;
-- 샘플 데이터: 필요시 레거시 데이터 참조하여 추가
INSERT INTO `package_structure` (
    `id`, `module_id`, `path_pattern`,
    `allowed_class_types`, `naming_pattern`, `naming_suffix`,
    `description`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (1, 1, 'com.ryuqq.domain.{domain}.aggregate', '["AGGREGATE_ROOT", "ENTITY"]', NULL, NULL, 'Domain Aggregate 패키지', NOW(), NOW(), NULL),
    (2, 1, 'com.ryuqq.domain.{domain}.vo', '["VALUE_OBJECT", "RECORD"]', NULL, NULL, 'Domain Value Object 패키지', NOW(), NOW(), NULL),
    (3, 1, 'com.ryuqq.domain.{domain}.id', '["RECORD"]', '*Id', 'Id', 'Domain ID 패키지', NOW(), NOW(), NULL),
    (4, 1, 'com.ryuqq.domain.{domain}.exception', '["EXCEPTION"]', '*Exception', 'Exception', 'Domain Exception 패키지', NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `package_structure` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 9. class_template (클래스 템플릿)
-- ============================================
LOCK TABLES `class_template` WRITE;
/*!40000 ALTER TABLE `class_template` DISABLE KEYS */;
-- 샘플 데이터: 레거시 db/legacy/V1__seed_data.sql 참조하여 추가
-- INSERT INTO `class_template` (...) VALUES (...);
/*!40000 ALTER TABLE `class_template` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 10. archunit_test (ArchUnit 테스트)
-- ============================================
LOCK TABLES `archunit_test` WRITE;
/*!40000 ALTER TABLE `archunit_test` DISABLE KEYS */;
-- 샘플 데이터: 레거시 db/legacy/V1__seed_data.sql 참조하여 추가
-- INSERT INTO `archunit_test` (...) VALUES (...);
/*!40000 ALTER TABLE `archunit_test` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 11. resource_template (리소스 템플릿)
-- ============================================
LOCK TABLES `resource_template` WRITE;
/*!40000 ALTER TABLE `resource_template` DISABLE KEYS */;
-- 샘플 데이터: 레거시 db/legacy/V1__seed_data.sql 참조하여 추가
-- INSERT INTO `resource_template` (...) VALUES (...);
/*!40000 ALTER TABLE `resource_template` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 12. coding_rule (코딩 규칙)
-- ============================================
LOCK TABLES `coding_rule` WRITE;
/*!40000 ALTER TABLE `coding_rule` DISABLE KEYS */;
-- 샘플 데이터: Zero-Tolerance 규칙 예시
INSERT INTO `coding_rule` (
    `id`, `convention_id`, `code`, `name`,
    `severity`, `category`,
    `description`, `rationale`,
    `auto_fixable`, `applies_to`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (1, 1, 'DOM-001', 'Lombok 금지',
     'BLOCKER', 'ANNOTATION',
     '모든 Domain 클래스에서 Lombok 어노테이션 사용을 금지합니다.',
     '도메인 로직의 명시성과 제어를 위해 Lombok 대신 명시적 구현을 사용합니다.',
     FALSE, 'DOMAIN', NOW(), NOW(), NULL),
    (2, 1, 'DOM-002', 'Getter 체이닝 금지 (Law of Demeter)',
     'BLOCKER', 'BEHAVIOR',
     'getter().getter() 형태의 체이닝을 금지합니다.',
     'Law of Demeter 원칙을 준수하여 객체 간 결합도를 낮춥니다.',
     FALSE, 'DOMAIN', NOW(), NOW(), NULL),
    (3, 2, 'APP-001', 'UseCase 인터페이스 필수',
     'CRITICAL', 'STRUCTURE',
     '모든 Service는 UseCase 인터페이스를 구현해야 합니다.',
     '포트와 어댑터 패턴을 준수하여 의존성 역전을 보장합니다.',
     FALSE, 'APPLICATION', NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `coding_rule` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 13. rule_example (규칙 예제)
-- ============================================
LOCK TABLES `rule_example` WRITE;
/*!40000 ALTER TABLE `rule_example` DISABLE KEYS */;
-- 샘플 데이터: 레거시 db/legacy/V1__seed_data.sql 참조하여 추가
INSERT INTO `rule_example` (
    `id`, `rule_id`, `example_type`, `code`,
    `language`, `explanation`, `highlight_lines`,
    `source`, `feedback_id`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (1, 1, 'BAD', '@Data\npublic class Order {\n    private Long id;\n}',
     'java', 'Lombok @Data 어노테이션 사용은 금지됩니다.', '[1]',
     'MANUAL', NULL, NOW(), NOW(), NULL),
    (2, 1, 'GOOD', 'public class Order {\n    private final Long id;\n\n    public Order(Long id) {\n        this.id = id;\n    }\n\n    public Long id() {\n        return id;\n    }\n}',
     'java', '명시적으로 생성자와 접근자를 구현합니다.', '[1, 4, 8]',
     'MANUAL', NULL, NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `rule_example` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 14. checklist_item (체크리스트 항목)
-- ============================================
LOCK TABLES `checklist_item` WRITE;
/*!40000 ALTER TABLE `checklist_item` DISABLE KEYS */;
-- 샘플 데이터: 레거시 db/legacy/V1__seed_data.sql 참조하여 추가
-- INSERT INTO `checklist_item` (...) VALUES (...);
/*!40000 ALTER TABLE `checklist_item` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 15. zero_tolerance_rule (Zero-Tolerance 규칙)
-- ============================================
LOCK TABLES `zero_tolerance_rule` WRITE;
/*!40000 ALTER TABLE `zero_tolerance_rule` DISABLE KEYS */;
INSERT INTO `zero_tolerance_rule` (
    `id`, `rule_id`, `type`,
    `detection_pattern`, `detection_type`,
    `auto_reject_pr`, `error_message`,
    `created_at`, `updated_at`, `deleted_at`
) VALUES
    (1, 1, 'LOMBOK_USAGE',
     '@(Data|Getter|Setter|Builder|AllArgsConstructor|NoArgsConstructor|RequiredArgsConstructor)', 'REGEX',
     TRUE, 'Lombok 어노테이션 사용이 감지되었습니다. Domain 레이어에서는 Lombok을 사용할 수 없습니다.',
     NOW(), NOW(), NULL),
    (2, 2, 'GETTER_CHAINING',
     '\\.get[A-Z]\\w*\\(\\)\\.get[A-Z]\\w*\\(\\)', 'REGEX',
     TRUE, 'Getter 체이닝이 감지되었습니다. Law of Demeter를 위반합니다.',
     NOW(), NOW(), NULL);
/*!40000 ALTER TABLE `zero_tolerance_rule` ENABLE KEYS */;
UNLOCK TABLES;

-- ============================================
-- 복원 설정
-- ============================================
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- ============================================
-- 참고사항
-- ============================================
-- 1. 전체 시드 데이터는 db/legacy/V1__seed_data.sql 참조
-- 2. class_template, archunit_test 등은 필요시 추가
-- 3. NOW() 함수는 Flyway 실행 시점의 시간으로 설정됨
-- ============================================
