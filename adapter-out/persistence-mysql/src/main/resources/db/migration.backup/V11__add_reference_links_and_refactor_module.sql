-- ============================================
-- Spring Standards - Database Schema V11
-- Add reference_links to tech_stack/architecture
-- Refactor module: gradle_path → module_path + build_identifier
-- Created: 2026-01-24
-- ============================================

-- ============================================
-- 1. tech_stack: Add reference_links column
-- ============================================
ALTER TABLE `tech_stack`
    ADD COLUMN `reference_links` JSON AFTER `build_config_file`;

-- ============================================
-- 2. architecture: Add reference_links column
-- ============================================
ALTER TABLE `architecture`
    ADD COLUMN `reference_links` JSON AFTER `pattern_principles`;

-- ============================================
-- 3. module: Refactor gradle_path to module_path + build_identifier
-- ============================================

-- Step 1: Add new columns
ALTER TABLE `module`
    ADD COLUMN `module_path` VARCHAR(500) AFTER `description`,
    ADD COLUMN `build_identifier` VARCHAR(200) AFTER `module_path`;

-- Step 2: Migrate data from gradle_path to module_path
-- gradle_path 값을 module_path로 복사 (예: ":adapter-in:rest-api" → "adapter-in/rest-api")
UPDATE `module`
SET `module_path` = REPLACE(SUBSTRING(`gradle_path`, 2), ':', '/'),
    `build_identifier` = `gradle_path`
WHERE `gradle_path` IS NOT NULL;

-- Step 3: Set module_path as NOT NULL after migration
ALTER TABLE `module`
    MODIFY COLUMN `module_path` VARCHAR(500) NOT NULL;

-- Step 4: Drop old gradle_path column
ALTER TABLE `module`
    DROP COLUMN `gradle_path`;
