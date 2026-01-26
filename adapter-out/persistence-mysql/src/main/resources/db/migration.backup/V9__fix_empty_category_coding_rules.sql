-- V9: Fix coding_rule records with empty or invalid category values
-- Root cause: Some records have empty string ('') in category column
-- which causes IllegalArgumentException when JPA tries to map to RuleCategory enum

-- Strategy: Soft delete records with empty/invalid category to prevent query failures
-- These records were likely inserted through an error or legacy process

-- First, soft delete any coding_rules with empty category
UPDATE coding_rule
SET deleted_at = NOW()
WHERE (category = '' OR category IS NULL)
  AND deleted_at IS NULL;

-- Log the fix in a comment (MySQL doesn't support RAISE NOTICE like PostgreSQL)
-- Records with empty category have been soft-deleted to prevent enum parsing errors
