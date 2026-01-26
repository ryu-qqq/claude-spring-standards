-- ============================================
-- V19: Hook 템플릿 제거
-- rule-checker-hook.sh, session-tracker.sh 삭제
-- settings.local.json에서 hooks 참조 제거
--
-- 이유:
-- - rule-checker-hook: validation_context()와 중복 + 레이어 코드 하드코딩
-- - session-tracker: 불필요한 안내 메시지
-- ============================================

-- 1. Hook 템플릿 soft delete
UPDATE config_file_template
SET deleted_at = NOW(), updated_at = NOW()
WHERE id IN (14, 15);

-- 2. settings.local.json (id=2) - hooks 제거, 빈 설정으로 변경
UPDATE config_file_template
SET content = '{
  "hooks": {}
}',
    description = 'Claude Code 로컬 설정 (hooks 비활성화)',
    updated_at = NOW()
WHERE id = 2;
