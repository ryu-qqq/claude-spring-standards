-- ===============================================
-- Flyway Migration V1: Create Example Table
-- ===============================================
-- 작성자: windsurf
-- 작성일: 2025-10-28
-- 설명: Example 도메인의 초기 테이블 생성
-- ===============================================

-- ===============================================
-- Example 테이블 생성
-- ===============================================
CREATE TABLE IF NOT EXISTS example (
    -- 기본 키 (AUTO_INCREMENT)
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Example ID (기본 키)',
    
    -- 비즈니스 데이터
    message VARCHAR(500) NOT NULL COMMENT '메시지 내용',
    
    -- 상태 관리
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE, INACTIVE, DELETED)',
    
    -- 감사 정보 (Audit)
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',
    
    -- 기본 키 제약조건
    PRIMARY KEY (id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Example 테이블 - 예제 도메인';

-- ===============================================
-- 인덱스 생성
-- ===============================================

-- 상태별 조회 최적화
CREATE INDEX idx_example_status ON example(status);

-- 생성일시 기준 정렬 최적화
CREATE INDEX idx_example_created_at ON example(created_at DESC);

-- 상태 + 생성일시 복합 조회 최적화 (활성 데이터 조회 시)
CREATE INDEX idx_example_status_created_at ON example(status, created_at DESC);

-- ===============================================
-- 초기 데이터 삽입 (선택 사항)
-- ===============================================

-- 테스트용 샘플 데이터
INSERT INTO example (message, status, created_at, updated_at)
VALUES 
    ('Welcome to Spring Standards!', 'ACTIVE', NOW(6), NOW(6)),
    ('This is a sample message', 'ACTIVE', NOW(6), NOW(6)),
    ('Example inactive message', 'INACTIVE', NOW(6), NOW(6));

-- ===============================================
-- 마이그레이션 완료
-- ===============================================
-- 이 스크립트는 Flyway에 의해 자동으로 실행됩니다.
-- flyway_schema_history 테이블에 버전 정보가 기록됩니다.
-- ===============================================
