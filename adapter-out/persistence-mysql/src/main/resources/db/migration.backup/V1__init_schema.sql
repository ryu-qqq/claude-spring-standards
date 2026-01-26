-- ============================================
-- Spring Standards - Database Schema V1
-- 현재 JPA 엔티티 기준 새로운 스키마
-- Created: 2026-01-20
-- ============================================

-- ============================================
-- 1. tech_stack (독립 테이블)
-- ============================================
CREATE TABLE IF NOT EXISTS `tech_stack` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `status` VARCHAR(20) NOT NULL,
    `language_type` VARCHAR(50) NOT NULL,
    `language_version` VARCHAR(20) NOT NULL,
    `language_features` JSON,
    `framework_type` VARCHAR(50) NOT NULL,
    `framework_version` VARCHAR(20) NOT NULL,
    `framework_modules` JSON,
    `platform_type` VARCHAR(50) NOT NULL,
    `runtime_environment` VARCHAR(50) NOT NULL,
    `build_tool_type` VARCHAR(50) NOT NULL,
    `build_config_file` VARCHAR(100) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. architecture (tech_stack 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `architecture` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tech_stack_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `pattern_type` VARCHAR(50) NOT NULL,
    `pattern_description` TEXT,
    `pattern_principles` JSON,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_architecture_tech_stack_id` (`tech_stack_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. layer (architecture 참조) - 신규 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `layer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `architecture_id` BIGINT NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `order_index` INT NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_layer_architecture_id` (`architecture_id`),
    UNIQUE KEY `uk_layer_architecture_code` (`architecture_id`, `code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. convention (layer 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `convention` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `layer_id` BIGINT NOT NULL,
    `version` VARCHAR(20) NOT NULL,
    `description` TEXT,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_convention_layer_id` (`layer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. layer_dependency_rule (architecture 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `layer_dependency_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `architecture_id` BIGINT NOT NULL,
    `from_layer` VARCHAR(50) NOT NULL,
    `to_layer` VARCHAR(50) NOT NULL,
    `dependency_type` VARCHAR(30) NOT NULL,
    `condition_description` TEXT,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_layer_dependency_rule_architecture_id` (`architecture_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 6. module (layer 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `module` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `layer_id` BIGINT NOT NULL,
    `parent_module_id` BIGINT,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `gradle_path` VARCHAR(200) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_module_layer_id` (`layer_id`),
    INDEX `idx_module_parent_module_id` (`parent_module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 7. package_purpose (package_structure 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `package_purpose` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `structure_id` BIGINT NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `default_allowed_class_types` JSON,
    `default_naming_pattern` VARCHAR(200),
    `default_naming_suffix` VARCHAR(50),
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_package_purpose_structure_id` (`structure_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 8. package_structure (module 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `package_structure` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `module_id` BIGINT NOT NULL,
    `path_pattern` VARCHAR(300) NOT NULL,
    `allowed_class_types` JSON NOT NULL,
    `naming_pattern` VARCHAR(200),
    `naming_suffix` VARCHAR(50),
    `description` TEXT,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_package_structure_module_id` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 9. class_template (structure 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `class_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `structure_id` BIGINT NOT NULL,
    `class_type` VARCHAR(50) NOT NULL,
    `template_code` TEXT NOT NULL,
    `naming_pattern` VARCHAR(200),
    `required_annotations` JSON,
    `forbidden_annotations` JSON,
    `required_interfaces` JSON,
    `forbidden_inheritance` JSON,
    `required_methods` JSON,
    `description` TEXT,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_class_template_structure_id` (`structure_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 10. archunit_test (structure 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `archunit_test` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `structure_id` BIGINT NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `test_code` TEXT NOT NULL,
    `test_class_name` VARCHAR(255),
    `test_method_name` VARCHAR(255),
    `severity` VARCHAR(20),
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_archunit_test_structure_id` (`structure_id`),
    UNIQUE KEY `uk_archunit_test_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 11. resource_template (module 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `resource_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `module_id` BIGINT NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `file_path` VARCHAR(255) NOT NULL,
    `file_type` VARCHAR(20) NOT NULL,
    `description` TEXT,
    `template_content` TEXT,
    `required` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_resource_template_module_id` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 12. coding_rule (convention 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `coding_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `convention_id` BIGINT NOT NULL,
    `code` VARCHAR(20) NOT NULL,
    `name` VARCHAR(200) NOT NULL,
    `severity` ENUM('BLOCKER', 'CRITICAL', 'MAJOR', 'MINOR', 'INFO') NOT NULL,
    `category` ENUM('NAMING', 'STRUCTURE', 'STYLE', 'DEPENDENCY', 'ANNOTATION', 'BEHAVIOR', 'DOCUMENTATION', 'PERFORMANCE', 'SECURITY', 'TESTING', 'LOCATION') NOT NULL,
    `description` TEXT NOT NULL,
    `rationale` TEXT,
    `auto_fixable` BOOLEAN NOT NULL DEFAULT FALSE,
    `applies_to` VARCHAR(500),
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_coding_rule_convention_id` (`convention_id`),
    UNIQUE KEY `uk_coding_rule_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 13. rule_example (coding_rule 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `rule_example` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `rule_id` BIGINT NOT NULL,
    `example_type` VARCHAR(10) NOT NULL,
    `code` TEXT NOT NULL,
    `language` VARCHAR(20) NOT NULL,
    `explanation` TEXT,
    `highlight_lines` JSON,
    `source` VARCHAR(50),
    `feedback_id` BIGINT,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_rule_example_rule_id` (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 14. checklist_item (coding_rule 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `checklist_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `rule_id` BIGINT NOT NULL,
    `sequence_order` INT NOT NULL,
    `check_description` VARCHAR(500) NOT NULL,
    `check_type` VARCHAR(20) NOT NULL,
    `automation_tool` VARCHAR(50),
    `automation_rule_id` VARCHAR(100),
    `is_critical` BOOLEAN NOT NULL DEFAULT FALSE,
    `source` VARCHAR(50),
    `feedback_id` BIGINT,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_checklist_item_rule_id` (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 15. zero_tolerance_rule (coding_rule 참조)
-- ============================================
CREATE TABLE IF NOT EXISTS `zero_tolerance_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `rule_id` BIGINT NOT NULL,
    `type` VARCHAR(50) NOT NULL,
    `detection_pattern` VARCHAR(500) NOT NULL,
    `detection_type` VARCHAR(20) NOT NULL,
    `auto_reject_pr` BOOLEAN NOT NULL DEFAULT TRUE,
    `error_message` VARCHAR(500) NOT NULL,
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6),
    PRIMARY KEY (`id`),
    INDEX `idx_zero_tolerance_rule_rule_id` (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
