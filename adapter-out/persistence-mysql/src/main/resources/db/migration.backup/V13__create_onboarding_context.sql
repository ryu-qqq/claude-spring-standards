-- ============================================
-- Convention Hub - Database Schema V13
-- Create onboarding_context table
-- Serena 온보딩 시 제공할 컨텍스트 정보
-- Created: 2026-01-24
-- ============================================

-- ============================================
-- 1. onboarding_context 테이블 생성
-- ============================================
-- 용도: Serena onboarding 시 제공할 컨벤션 요약 정보
-- 특징: 가벼운 요약 정보만 저장, 상세 규칙은 기존 테이블 활용

CREATE TABLE `onboarding_context` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `tech_stack_id` BIGINT NOT NULL,
    `architecture_id` BIGINT NULL,

    -- 컨텍스트 유형
    -- SUMMARY: 프로젝트 개요
    -- ZERO_TOLERANCE: Zero-Tolerance 규칙 목록
    -- RULES_INDEX: 레이어별 규칙 인덱스 (메타 정보)
    -- MCP_USAGE: MCP 사용법 가이드
    `context_type` VARCHAR(50) NOT NULL,

    -- 내용
    `title` VARCHAR(100) NOT NULL COMMENT '컨텍스트 제목',
    `content` TEXT NOT NULL COMMENT '컨텍스트 내용 (Markdown 지원)',

    -- 정렬
    `priority` INT NULL DEFAULT 0 COMMENT '온보딩 시 표시 순서 (낮을수록 먼저)',

    -- Audit
    `created_at` DATETIME(6) NOT NULL,
    `updated_at` DATETIME(6) NOT NULL,
    `deleted_at` DATETIME(6) NULL,

    PRIMARY KEY (`id`),

    -- 인덱스
    INDEX `idx_onboarding_context_tech_stack` (`tech_stack_id`),
    INDEX `idx_onboarding_context_architecture` (`architecture_id`),
    INDEX `idx_onboarding_context_type` (`context_type`),
    INDEX `idx_onboarding_context_priority` (`priority`),

    -- 외래키
    CONSTRAINT `fk_onboarding_context_tech_stack`
        FOREIGN KEY (`tech_stack_id`) REFERENCES `tech_stack` (`id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_onboarding_context_architecture`
        FOREIGN KEY (`architecture_id`) REFERENCES `architecture` (`id`)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. 코멘트 추가
-- ============================================
ALTER TABLE `onboarding_context`
    COMMENT = 'Serena 온보딩용 컨벤션 요약 정보. 세션 시작 시 이 정보로 컨텍스트 로드';
