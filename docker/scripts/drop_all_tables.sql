-- ============================================================
-- DROP ALL TABLES Script
-- 포트포워딩 후 실행: mysql -h 127.0.0.1 -P 13307 -u admin -p < drop_all_tables.sql
-- ============================================================

-- 외래키 체크 비활성화 (순서 무관하게 드랍 가능)
SET FOREIGN_KEY_CHECKS = 0;

-- 기존 테이블 모두 드랍
DROP TABLE IF EXISTS class_template;
DROP TABLE IF EXISTS zero_tolerance_rule;
DROP TABLE IF EXISTS checklist_item;
DROP TABLE IF EXISTS rule_example;
DROP TABLE IF EXISTS coding_rule;
DROP TABLE IF EXISTS convention;
DROP TABLE IF EXISTS layer_dependency;
DROP TABLE IF EXISTS layer_dependency_rule;
DROP TABLE IF EXISTS package_structure;
DROP TABLE IF EXISTS package_purpose;
DROP TABLE IF EXISTS module;
DROP TABLE IF EXISTS architecture;
DROP TABLE IF EXISTS tech_stack;

-- 추가로 생성된 테이블들도 드랍
DROP TABLE IF EXISTS development_pattern;
DROP TABLE IF EXISTS rule_reference;
DROP TABLE IF EXISTS rule_history;
DROP TABLE IF EXISTS archunit_test;

-- 인프라 테이블 드랍
DROP TABLE IF EXISTS cache_config;
DROP TABLE IF EXISTS scheduled_task;
DROP TABLE IF EXISTS audit_trail;

-- Flyway 히스토리 드랍 (새로 시작)
DROP TABLE IF EXISTS flyway_schema_history;

-- 외래키 체크 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'All tables dropped successfully!' AS result;
