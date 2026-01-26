
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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;
SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `structure_id` bigint NOT NULL,
  `class_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
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
  KEY `idx_class_template_structure_id` (`structure_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=217 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_purpose` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `structure_id` bigint NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `default_allowed_class_types` json DEFAULT NULL,
  `default_naming_pattern` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `default_naming_suffix` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_package_purpose_structure_id` (`structure_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `package_structure` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `module_id` bigint NOT NULL,
  `path_pattern` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL,
  `allowed_class_types` json NOT NULL,
  `naming_pattern` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `naming_suffix` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_package_structure_module_id` (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

