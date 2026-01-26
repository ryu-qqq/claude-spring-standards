-- ============================================
-- Spring Standards - Feedback Queue Table V3
-- 피드백 큐 테이블 생성
-- Created: 2026-01-20
-- ============================================

-- ============================================
-- feedback_queue (피드백 큐)
-- MCP 피드백 시스템을 위한 큐 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS `feedback_queue` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `target_type` VARCHAR(50) NOT NULL COMMENT '피드백 대상 타입 (RULE_EXAMPLE, CLASS_TEMPLATE, CODING_RULE, CHECKLIST_ITEM, ARCH_UNIT_TEST)',
    `target_id` BIGINT NULL COMMENT '피드백 대상 ID (ADD 타입의 경우 NULL 가능)',
    `feedback_type` VARCHAR(20) NOT NULL COMMENT '피드백 유형 (ADD, MODIFY, DELETE)',
    `risk_level` VARCHAR(20) NOT NULL COMMENT '위험도 레벨 (SAFE, MEDIUM)',
    `payload` TEXT NOT NULL COMMENT '피드백 내용 (JSON)',
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '처리 상태 (PENDING, LLM_APPROVED, LLM_REJECTED, HUMAN_APPROVED, HUMAN_REJECTED, MERGED)',
    `review_notes` VARCHAR(1000) NULL COMMENT '검토 노트',
    `created_at` DATETIME(6) NOT NULL COMMENT '생성 일시',
    `updated_at` DATETIME(6) NOT NULL COMMENT '수정 일시',
    PRIMARY KEY (`id`),
    INDEX `idx_feedback_queue_status` (`status`),
    INDEX `idx_feedback_queue_target_type` (`target_type`),
    INDEX `idx_feedback_queue_risk_level` (`risk_level`),
    INDEX `idx_feedback_queue_target` (`target_type`, `target_id`),
    INDEX `idx_feedback_queue_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MCP 피드백 큐';
