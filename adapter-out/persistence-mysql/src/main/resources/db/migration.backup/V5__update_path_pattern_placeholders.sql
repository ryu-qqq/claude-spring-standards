-- ============================================
-- Spring Standards - Path Pattern Placeholder 변경
-- V5: path_pattern에서 하드코딩된 패키지를 placeholder로 변경
-- Created: 2026-01-20
-- ============================================
-- 변경 사항:
--   - com.ryuqq.domain → {base_package}
--   - LLM이 대상 프로젝트의 기존 코드 구조를 보고 base_package를 추론
--   - 예: com.ryuqq.domain, com.connectly.domain 등
-- ============================================

-- ============================================
-- 1. package_structure 테이블 - path_pattern 업데이트
-- ============================================

-- Domain Aggregate 패키지
UPDATE `package_structure` SET
    `path_pattern` = '{base_package}.{domain}.aggregate',
    `updated_at` = NOW()
WHERE `id` = 1;

-- Domain Value Object 패키지
UPDATE `package_structure` SET
    `path_pattern` = '{base_package}.{domain}.vo',
    `updated_at` = NOW()
WHERE `id` = 2;

-- Domain ID 패키지
UPDATE `package_structure` SET
    `path_pattern` = '{base_package}.{domain}.id',
    `updated_at` = NOW()
WHERE `id` = 3;

-- Domain Exception 패키지
UPDATE `package_structure` SET
    `path_pattern` = '{base_package}.{domain}.exception',
    `updated_at` = NOW()
WHERE `id` = 4;

-- ============================================
-- 완료
-- ============================================
-- Placeholder 설명:
--   {base_package}: 프로젝트의 모듈 기본 패키지 (예: com.ryuqq.domain)
--                   LLM이 대상 프로젝트의 기존 코드 구조를 분석하여 추론
--   {domain}: 비즈니스 도메인명 (예: order, payment, user)
-- ============================================
