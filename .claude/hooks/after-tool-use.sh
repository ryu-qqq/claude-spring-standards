#!/bin/bash

# =====================================================
# Claude Code Hook: after-tool-use
# Trigger: Write/Edit 도구 사용 직후
# Strategy: Cache-based validation with validation-helper.py
# =====================================================

# 로그 디렉토리
LOG_DIR=".claude/hooks/logs"
mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/hook-execution.log"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

# 입력 읽기 (Claude Code가 전달하는 JSON)
TOOL_DATA=$(cat)

# 파일 경로 추출 (공백 허용)
FILE_PATH=$(echo "$TOOL_DATA" | grep -oE '"file_path"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"\([^"]*\)"/\1/')

if [[ -z "$FILE_PATH" ]]; then
    # 파일 경로가 없으면 스킵
    exit 0
fi

# 로그 기록
echo "[$TIMESTAMP] after-tool-use triggered" >> "$LOG_FILE"
echo "File: $FILE_PATH" >> "$LOG_FILE"

# 파일이 실제로 존재하는지 확인
if [[ ! -f "$FILE_PATH" ]]; then
    echo "  → File not found, skipping validation" >> "$LOG_FILE"
    exit 0
fi

# =====================================================
# Phase 1: Layer 감지 (파일 경로 기반)
# =====================================================

LAYER="unknown"

if echo "$FILE_PATH" | grep -q "domain/.*model"; then
    LAYER="domain"
    echo "  → Detected Layer: DOMAIN" >> "$LOG_FILE"
elif echo "$FILE_PATH" | grep -q "adapter/in/web"; then
    LAYER="adapter-rest"
    echo "  → Detected Layer: ADAPTER-REST" >> "$LOG_FILE"
elif echo "$FILE_PATH" | grep -q "adapter/out/persistence"; then
    LAYER="adapter-persistence"
    echo "  → Detected Layer: ADAPTER-PERSISTENCE" >> "$LOG_FILE"
elif echo "$FILE_PATH" | grep -q "application/"; then
    LAYER="application"
    echo "  → Detected Layer: APPLICATION" >> "$LOG_FILE"
elif echo "$FILE_PATH" | grep -q "test/"; then
    LAYER="testing"
    echo "  → Detected Layer: TESTING" >> "$LOG_FILE"
fi

# =====================================================
# Phase 2: Cache-based Validation (validation-helper.py)
# =====================================================

VALIDATOR_SCRIPT=".claude/hooks/scripts/validation-helper.py"

if [[ -f "$VALIDATOR_SCRIPT" && "$LAYER" != "unknown" ]]; then
    echo "  → Running cache-based validation for layer: $LAYER" >> "$LOG_FILE"

    # Python 검증기 실행
    python3 "$VALIDATOR_SCRIPT" "$FILE_PATH" "$LAYER" 2>> "$LOG_FILE"

    # 검증 성공 여부는 Python 스크립트 출력으로 판단
else
    echo "  → Fallback to basic validation (validator not found or unknown layer)" >> "$LOG_FILE"

    # ===== Fallback: Basic Critical Validators =====

    VALIDATION_FAILED=false

    # 1. Lombok 금지 검증
    if grep -qE "@(Data|Builder|Getter|Setter|AllArgsConstructor|NoArgsConstructor|RequiredArgsConstructor)" "$FILE_PATH"; then
        echo "  ❌ FAILED: Lombok annotation detected!" >> "$LOG_FILE"
        VALIDATION_FAILED=true
        cat << EOF

---
⚠️ **Validation Failed: Lombok 사용 감지**

**위반 파일**: \`$FILE_PATH\`

**문제**: Lombok 어노테이션이 발견되었습니다.
- @Data, @Builder, @Getter, @Setter 등 모든 Lombok 사용 금지

**해결 방법**:
1. Lombok 어노테이션 제거
2. Pure Java getter/setter 수동 작성

**참고**: \`docs/coding_convention/\` - Zero-Tolerance 규칙

---

EOF
    else
        echo "  ✅ PASSED: No Lombok" >> "$LOG_FILE"
    fi

    # 2. Javadoc 검증
    if ! grep -q "@author" "$FILE_PATH"; then
        echo "  ❌ FAILED: Missing @author in Javadoc!" >> "$LOG_FILE"
        VALIDATION_FAILED=true
        cat << EOF

---
⚠️ **Validation Failed: Javadoc @author 누락**

**위반 파일**: \`$FILE_PATH\`

**문제**: @author 태그가 없습니다.

**해결 방법**:
\`\`\`java
/**
 * 클래스 설명
 *
 * @author Claude
 * @since $(date '+%Y-%m-%d')
 */
\`\`\`

---

EOF
    else
        echo "  ✅ PASSED: Javadoc @author present" >> "$LOG_FILE"
    fi

    # 3. Layer-Specific: Domain 레이어
    if [[ "$LAYER" == "domain" ]]; then
        echo "  → Running domain validators..." >> "$LOG_FILE"

        # Spring/JPA annotation 검증
        if grep -qE "@(Entity|Table|Column|Service|Repository|Transactional)" "$FILE_PATH"; then
            echo "  ❌ FAILED: Spring/JPA annotation in domain!" >> "$LOG_FILE"
            VALIDATION_FAILED=true
            cat << EOF

---
⚠️ **Validation Failed: Domain에서 Spring/JPA 사용 감지**

**위반 파일**: \`$FILE_PATH\`

**문제**: Domain 레이어는 순수 Java만 사용해야 합니다.

**금지**:
- @Entity, @Table, @Column (JPA)
- @Service, @Repository, @Transactional (Spring)

**참고**: Domain은 인프라에 의존하지 않습니다.

---

EOF
        else
            echo "  ✅ PASSED: Pure Java (no Spring/JPA)" >> "$LOG_FILE"
        fi
    fi

    # 최종 결과
    if [[ "$VALIDATION_FAILED" == true ]]; then
        echo "  → FINAL RESULT: VALIDATION FAILED ❌" >> "$LOG_FILE"
        echo ""
        echo "💡 코드를 수정한 후 다시 시도하세요."
        echo ""
    else
        echo "  → FINAL RESULT: ALL VALIDATIONS PASSED ✅" >> "$LOG_FILE"
        cat << EOF

---
✅ **Validation Passed**

파일: \`$FILE_PATH\`

모든 규칙을 준수합니다!

---

EOF
    fi
fi
