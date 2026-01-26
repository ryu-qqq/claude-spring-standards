-- ============================================
-- Convention Hub - Database Schema V12
-- Create config_file_template table
-- AI 도구 설정 파일 템플릿 저장 (도구 독립적)
-- Created: 2026-01-24
-- ============================================

-- ============================================
-- 1. config_file_template 테이블 생성
-- ============================================
-- 용도: Claude, Cursor, Copilot 등 AI 도구의 설정 파일 템플릿을 저장
-- 특징: tool_type으로 도구 구분, 새 도구 추가 시 데이터만 추가하면 됨

CREATE TABLE `config_file_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tech_stack_id` BIGINT NOT NULL,
    `architecture_id` BIGINT NULL,

    -- 도구 식별 (확장 가능)
    -- CLAUDE, CURSOR, COPILOT, WINDSURF, GENERIC 등
    `tool_type` VARCHAR(50) NOT NULL,

    -- 파일 정보
    `file_path` VARCHAR(200) NOT NULL COMMENT '생성될 파일 경로 (예: .claude/CLAUDE.md)',
    `file_name` VARCHAR(100) NOT NULL COMMENT '파일명 (예: CLAUDE.md)',

    -- 내용
    `content` LONGTEXT NOT NULL COMMENT '파일 내용 (변수 치환 가능: {project_name}, {tech_stack_name} 등)',

    -- 메타데이터
    `category` VARCHAR(50) NULL COMMENT 'MAIN_CONFIG, SKILL, RULE, AGENT, HOOK',
    `description` TEXT NULL COMMENT '템플릿 설명',
    `variables` JSON NULL COMMENT '치환 가능한 변수 정의',

    -- 정렬 및 필수 여부
    `display_order` INT NULL DEFAULT 0,
    `is_required` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '필수 파일 여부',

    -- Audit
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6) NULL,

    PRIMARY KEY (`id`),

    -- 인덱스
    INDEX `idx_config_file_template_tech_stack` (`tech_stack_id`),
    INDEX `idx_config_file_template_architecture` (`architecture_id`),
    INDEX `idx_config_file_template_tool_type` (`tool_type`),
    INDEX `idx_config_file_template_category` (`category`),

    -- 외래키
    CONSTRAINT `fk_config_file_template_tech_stack`
        FOREIGN KEY (`tech_stack_id`) REFERENCES `tech_stack` (`id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_config_file_template_architecture`
        FOREIGN KEY (`architecture_id`) REFERENCES `architecture` (`id`)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. 코멘트 추가
-- ============================================
ALTER TABLE `config_file_template`
    COMMENT = 'AI 도구 설정 파일 템플릿. Convention Hub init 시 이 템플릿으로 파일 생성';
