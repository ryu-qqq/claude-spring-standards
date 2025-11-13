#!/bin/bash

# =====================================================
# TDD Cycle Tracker + LangFuse Integration
# Trigger: Bash 도구 사용 시 (git commit, ./gradlew test 등)
# Purpose: Kent Beck TDD 사이클 추적 및 메트릭 수집
# =====================================================

OUTPUT="$1"

# LangFuse 로거 경로
LANGFUSE_LOGGER=".claude/scripts/log-to-langfuse.py"

# 프로젝트 정보
PROJECT_NAME=$(basename "$(pwd)")
TIMESTAMP=$(date -u +%Y-%m-%dT%H:%M:%SZ)

# LangFuse 로깅 함수
log_to_langfuse() {
    local event_type="$1"
    local data="$2"

    if [[ -f "$LANGFUSE_LOGGER" ]]; then
        python3 "$LANGFUSE_LOGGER" \
            --event-type "$event_type" \
            --data "$data" 2>/dev/null
    fi
}

# ==================== Git Commit 감지 ====================

if echo "$OUTPUT" | grep -qE "(git commit|git-commit)"; then
    # 커밋 정보 추출
    COMMIT_MSG=$(git log -1 --pretty=%B 2>/dev/null || echo "N/A")
    COMMIT_HASH=$(git log -1 --pretty=%h 2>/dev/null || echo "N/A")
    FILES_CHANGED=$(git diff --stat HEAD~1 HEAD 2>/dev/null | tail -1 | grep -oE '[0-9]+ files? changed' || echo "0 files changed")
    LINES_CHANGED=$(git diff --stat HEAD~1 HEAD 2>/dev/null | tail -1 | grep -oE '[0-9]+ insertions?' || echo "0 insertions")

    # TDD Phase 감지 (커밋 메시지 패턴)
    TDD_PHASE="unknown"
    if echo "$COMMIT_MSG" | grep -qiE "(test|red|failing)"; then
        TDD_PHASE="red"
    elif echo "$COMMIT_MSG" | grep -qiE "(impl|implement|green|pass)"; then
        TDD_PHASE="green"
    elif echo "$COMMIT_MSG" | grep -qiE "(refactor|clean|improve)"; then
        TDD_PHASE="refactor"
    fi

    # LangFuse에 커밋 이벤트 전송
    log_to_langfuse "tdd_commit" "{
        \"project\": \"$PROJECT_NAME\",
        \"commit_hash\": \"$COMMIT_HASH\",
        \"commit_msg\": \"$COMMIT_MSG\",
        \"tdd_phase\": \"$TDD_PHASE\",
        \"files_changed\": \"$FILES_CHANGED\",
        \"lines_changed\": \"$LINES_CHANGED\",
        \"timestamp\": \"$TIMESTAMP\"
    }"

    # 디버깅 출력 (선택적)
    # echo "[TDD Tracker] Commit detected: $COMMIT_HASH ($TDD_PHASE phase)" >&2
fi

# ==================== 테스트 실행 감지 ====================

if echo "$OUTPUT" | grep -qE "(gradlew.*test|gradle.*test|mvn.*test)"; then
    # 테스트 결과 파싱
    TEST_PASSED=$(echo "$OUTPUT" | grep -oE "[0-9]+ tests? passed" | grep -oE "[0-9]+" || echo "0")
    TEST_FAILED=$(echo "$OUTPUT" | grep -oE "[0-9]+ tests? failed" | grep -oE "[0-9]+" || echo "0")
    TEST_DURATION=$(echo "$OUTPUT" | grep -oE "BUILD SUCCESSFUL in [0-9]+s" | grep -oE "[0-9]+" || echo "0")

    # 테스트 성공 여부
    TEST_STATUS="success"
    if [[ "$TEST_FAILED" -gt 0 ]] || echo "$OUTPUT" | grep -q "BUILD FAILED"; then
        TEST_STATUS="failed"
    fi

    # LangFuse에 테스트 이벤트 전송
    log_to_langfuse "tdd_test" "{
        \"project\": \"$PROJECT_NAME\",
        \"test_status\": \"$TEST_STATUS\",
        \"tests_passed\": \"$TEST_PASSED\",
        \"tests_failed\": \"$TEST_FAILED\",
        \"duration_seconds\": \"$TEST_DURATION\",
        \"timestamp\": \"$TIMESTAMP\"
    }"

    # 디버깅 출력 (선택적)
    # echo "[TDD Tracker] Test detected: $TEST_STATUS ($TEST_PASSED passed, $TEST_FAILED failed)" >&2
fi

# ==================== ArchUnit 실행 감지 ====================

if echo "$OUTPUT" | grep -qE "ArchUnit|Architecture"; then
    # ArchUnit 결과 파싱
    ARCHUNIT_STATUS="success"
    if echo "$OUTPUT" | grep -qE "(violation|failed|error)"; then
        ARCHUNIT_STATUS="failed"
    fi

    VIOLATIONS=$(echo "$OUTPUT" | grep -c "violation" || echo "0")

    # LangFuse에 ArchUnit 이벤트 전송
    log_to_langfuse "archunit_check" "{
        \"project\": \"$PROJECT_NAME\",
        \"status\": \"$ARCHUNIT_STATUS\",
        \"violations\": \"$VIOLATIONS\",
        \"timestamp\": \"$TIMESTAMP\"
    }"
fi

# 원본 OUTPUT 그대로 출력 (파이프라인 유지)
echo "$OUTPUT"
