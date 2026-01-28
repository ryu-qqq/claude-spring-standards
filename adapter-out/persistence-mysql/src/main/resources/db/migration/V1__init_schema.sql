-- Spring Standards Convention Hub - Schema
-- Generated from production database

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Table: architecture
DROP TABLE IF EXISTS `architecture`;
CREATE TABLE `architecture` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tech_stack_id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pattern_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pattern_description` text COLLATE utf8mb4_unicode_ci,
  `pattern_principles` json DEFAULT NULL,
  `reference_links` json DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_architecture_tech_stack_id` (`tech_stack_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: archunit_test
DROP TABLE IF EXISTS `archunit_test`;
CREATE TABLE `archunit_test` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `structure_id` bigint NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `test_code` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `test_class_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `test_method_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `severity` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_archunit_test_code` (`code`),
  KEY `idx_archunit_test_structure_id` (`structure_id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: checklist_item
DROP TABLE IF EXISTS `checklist_item`;
CREATE TABLE `checklist_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rule_id` bigint NOT NULL,
  `sequence_order` int NOT NULL,
  `check_description` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `check_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `automation_tool` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `automation_rule_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_critical` tinyint(1) NOT NULL DEFAULT '0',
  `source` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `feedback_id` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_checklist_item_rule_id` (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=405 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: class_template
DROP TABLE IF EXISTS `class_template`;
CREATE TABLE `class_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `structure_id` bigint NOT NULL,
  `class_type_id` bigint NOT NULL,
  `template_code` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `naming_pattern` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `required_annotations` json DEFAULT NULL,
  `forbidden_annotations` json DEFAULT NULL,
  `required_interfaces` json DEFAULT NULL,
  `forbidden_inheritance` json DEFAULT NULL,
  `required_methods` json DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_class_template_structure_id` (`structure_id`),
  KEY `idx_class_template_class_type_id` (`class_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: class_type
DROP TABLE IF EXISTS `class_type`;
CREATE TABLE `class_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `order_index` int NOT NULL DEFAULT '0',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_type_category_code` (`category_id`,`code`),
  KEY `idx_class_type_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: class_type_category
DROP TABLE IF EXISTS `class_type_category`;
CREATE TABLE `class_type_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `architecture_id` bigint NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `order_index` int NOT NULL DEFAULT '0',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_type_category_arch_code` (`architecture_id`,`code`),
  KEY `idx_class_type_category_architecture_id` (`architecture_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: coding_rule
DROP TABLE IF EXISTS `coding_rule`;
CREATE TABLE `coding_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `convention_id` bigint NOT NULL,
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `severity` enum('BLOCKER','CRITICAL','MAJOR','MINOR','INFO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` enum('NAMING','STRUCTURE','STYLE','DEPENDENCY','ANNOTATION','BEHAVIOR','DOCUMENTATION','PERFORMANCE','SECURITY','TESTING','LOCATION') COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `rationale` text COLLATE utf8mb4_unicode_ci,
  `auto_fixable` tinyint(1) NOT NULL DEFAULT '0',
  `applies_to` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coding_rule_code` (`code`),
  KEY `idx_coding_rule_convention_id` (`convention_id`)
) ENGINE=InnoDB AUTO_INCREMENT=228 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: config_file_template
DROP TABLE IF EXISTS `config_file_template`;
CREATE TABLE `config_file_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tech_stack_id` bigint NOT NULL,
  `architecture_id` bigint DEFAULT NULL,
  `tool_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_path` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '생성될 파일 경로',
  `file_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '파일명',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '파일 내용',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MAIN_CONFIG, SKILL, RULE, AGENT, HOOK',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '템플릿 설명',
  `variables` json DEFAULT NULL COMMENT '치환 가능한 변수 정의',
  `display_order` int DEFAULT '0',
  `is_required` tinyint(1) NOT NULL DEFAULT '1' COMMENT '필수 파일 여부',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_config_file_template_tech_stack` (`tech_stack_id`),
  KEY `idx_config_file_template_architecture` (`architecture_id`),
  KEY `idx_config_file_template_tool_type` (`tool_type`),
  KEY `idx_config_file_template_category` (`category`),
  CONSTRAINT `fk_config_file_template_architecture` FOREIGN KEY (`architecture_id`) REFERENCES `architecture` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_config_file_template_tech_stack` FOREIGN KEY (`tech_stack_id`) REFERENCES `tech_stack` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 도구 설정 파일 템플릿';

-- Table: convention
DROP TABLE IF EXISTS `convention`;
CREATE TABLE `convention` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `module_id` bigint DEFAULT NULL,
  `version` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_convention_module_id` (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: feedback_queue
DROP TABLE IF EXISTS `feedback_queue`;
CREATE TABLE `feedback_queue` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '피드백 대상 타입 (RULE_EXAMPLE, CLASS_TEMPLATE, CODING_RULE, CHECKLIST_ITEM, ARCH_UNIT_TEST)',
  `target_id` bigint DEFAULT NULL COMMENT '피드백 대상 ID (ADD 타입의 경우 NULL 가능)',
  `feedback_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '피드백 유형 (ADD, MODIFY, DELETE)',
  `risk_level` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '위험도 레벨 (SAFE, MEDIUM)',
  `payload` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '피드백 내용 (JSON)',
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '처리 상태 (PENDING, LLM_APPROVED, LLM_REJECTED, HUMAN_APPROVED, HUMAN_REJECTED, MERGED)',
  `review_notes` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '검토 노트',
  `created_at` datetime(6) NOT NULL COMMENT '생성 일시',
  `updated_at` datetime(6) NOT NULL COMMENT '수정 일시',
  PRIMARY KEY (`id`),
  KEY `idx_feedback_queue_status` (`status`),
  KEY `idx_feedback_queue_target_type` (`target_type`),
  KEY `idx_feedback_queue_risk_level` (`risk_level`),
  KEY `idx_feedback_queue_target` (`target_type`,`target_id`),
  KEY `idx_feedback_queue_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MCP 피드백 큐';

-- Table: layer
DROP TABLE IF EXISTS `layer`;
CREATE TABLE `layer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `architecture_id` bigint NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `order_index` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_layer_architecture_code` (`architecture_id`,`code`),
  KEY `idx_layer_architecture_id` (`architecture_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: layer_dependency_rule
DROP TABLE IF EXISTS `layer_dependency_rule`;
CREATE TABLE `layer_dependency_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `architecture_id` bigint NOT NULL,
  `from_layer` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `to_layer` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dependency_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `condition_description` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_layer_dependency_rule_architecture_id` (`architecture_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: module
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `layer_id` bigint NOT NULL,
  `parent_module_id` bigint DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `module_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `build_identifier` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_module_layer_id` (`layer_id`),
  KEY `idx_module_parent_module_id` (`parent_module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: onboarding_context
DROP TABLE IF EXISTS `onboarding_context`;
CREATE TABLE `onboarding_context` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tech_stack_id` bigint NOT NULL,
  `architecture_id` bigint DEFAULT NULL,
  `context_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '컨텍스트 제목',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '컨텍스트 내용',
  `priority` int DEFAULT '0' COMMENT '온보딩 시 표시 순서',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_onboarding_context_tech_stack` (`tech_stack_id`),
  KEY `idx_onboarding_context_architecture` (`architecture_id`),
  KEY `idx_onboarding_context_type` (`context_type`),
  KEY `idx_onboarding_context_priority` (`priority`),
  CONSTRAINT `fk_onboarding_context_architecture` FOREIGN KEY (`architecture_id`) REFERENCES `architecture` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_onboarding_context_tech_stack` FOREIGN KEY (`tech_stack_id`) REFERENCES `tech_stack` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Serena 온보딩용 컨벤션 요약 정보';

-- Table: package_purpose
DROP TABLE IF EXISTS `package_purpose`;
CREATE TABLE `package_purpose` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `structure_id` bigint NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_package_purpose_structure_id` (`structure_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: package_structure
DROP TABLE IF EXISTS `package_structure`;
CREATE TABLE `package_structure` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `module_id` bigint NOT NULL,
  `path_pattern` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_package_structure_module_id` (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: resource_template
DROP TABLE IF EXISTS `resource_template`;
CREATE TABLE `resource_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `module_id` bigint NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `template_content` text COLLATE utf8mb4_unicode_ci,
  `required` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_resource_template_module_id` (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: rule_example
DROP TABLE IF EXISTS `rule_example`;
CREATE TABLE `rule_example` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rule_id` bigint NOT NULL,
  `example_type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `explanation` text COLLATE utf8mb4_unicode_ci,
  `highlight_lines` json DEFAULT NULL,
  `source` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `feedback_id` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rule_example_rule_id` (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=347 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: tech_stack
DROP TABLE IF EXISTS `tech_stack`;
CREATE TABLE `tech_stack` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language_version` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language_features` json DEFAULT NULL,
  `framework_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `framework_version` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `framework_modules` json DEFAULT NULL,
  `platform_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `runtime_environment` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `build_tool_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `build_config_file` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `reference_links` json DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: zero_tolerance_rule
DROP TABLE IF EXISTS `zero_tolerance_rule`;
CREATE TABLE `zero_tolerance_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rule_id` bigint NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `detection_pattern` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `detection_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `auto_reject_pr` tinyint(1) NOT NULL DEFAULT '1',
  `error_message` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_zero_tolerance_rule_rule_id` (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
