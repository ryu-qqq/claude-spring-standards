#!/bin/bash

# =====================================================
# Claude Code Hook: user-prompt-submit (JSON Logging)
# Trigger: 사용자가 프롬프트를 제출할 때
# Strategy: Keyword → Layer → inject-rules.py (Cache-based)
# Bash 3.2 호환 (macOS 기본 Bash)
# =====================================================

# 로그 헬퍼 경로
LOG_HELPER=".claude/hooks/scripts/log-helper.py"

# 세션 ID
SESSION_ID="$(date +%s)-$$"
PROJECT_NAME=$(basename "$(pwd)")

# 입력 읽기
USER_INPUT=$(cat)

# JSON 로그 함수
log_event() {
    echo "$2" | python3 "$LOG_HELPER" "$1" 2>/dev/null
}

# 세션 시작
log_event "session_start" "{\"session_id\":\"$SESSION_ID\",\"project\":\"$PROJECT_NAME\",\"hook\":\"user-prompt-submit\",\"user_command\":\"$USER_INPUT\"}"

# ==================== Context 분석 ====================

CONTEXT_SCORE=0
DETECTED_LAYERS=()
DETECTED_KEYWORDS=()
PRIORITY_FILTER=""

# Keyword → Layer 매핑 함수 (Bash 3.2 호환)
get_layer_from_keyword() {
    local keyword="$1"

    case "$keyword" in
        # Domain layer
        aggregate|entity|value*object|domain*event)
            echo "domain"
            ;;
        # Application layer
        usecase|service|command|query|transaction)
            echo "application"
            ;;
        # Adapter-REST layer (adapter-in 포함)
        controller|rest*api|endpoint|adapter*in)
            echo "adapter-rest"
            ;;
        # Adapter-Persistence layer (adapter-out, persistence-mysql 포함)
        repository|jpa|entity*mapping|adapter*out|persistence*mysql|persistence*postgresql|persistence*mongo)
            echo "adapter-persistence"
            ;;
        # Testing
        test)
            echo "testing"
            ;;
        # Java21
        record|sealed)
            echo "java21"
            ;;
        # Enterprise
        dto|mapper)
            echo "enterprise"
            ;;
        # Error handling
        exception|error)
            echo "error-handling"
            ;;
        *)
            echo ""
            ;;
    esac
}

# 키워드 목록 (프로젝트별 패키지 구조 포함)
KEYWORDS="aggregate entity value.object value_object valueobject domain.event domain_event domainevent usecase service command query transaction controller rest.api rest_api restapi endpoint adapter-in adapter_in adapterin repository jpa entity.mapping entity_mapping entitymapping adapter-out adapter_out adapterout persistence-mysql persistence_mysql persistencemysql persistence-postgresql persistence-mongo test record sealed dto mapper exception error"

# Primary Keywords 검색 (30점)
for keyword in $KEYWORDS; do
    # . 와 _ 를 공백으로도 매칭되도록 패턴 변환
    pattern=$(echo "$keyword" | sed 's/[._]/ /g')

    if echo "$USER_INPUT" | grep -qiE "$pattern"; then
        layer=$(get_layer_from_keyword "$keyword")

        if [ -n "$layer" ]; then
            CONTEXT_SCORE=$((CONTEXT_SCORE + 30))

            # 레이어 중복 방지
            if [[ ! " ${DETECTED_LAYERS[@]} " =~ " ${layer} " ]]; then
                DETECTED_LAYERS+=("$layer")
            fi

            DETECTED_KEYWORDS+=("$keyword")
        fi
    fi
done

# 한글 키워드 검색
if echo "$USER_INPUT" | grep -q "애그리게이트"; then
    CONTEXT_SCORE=$((CONTEXT_SCORE + 30))
    if [[ ! " ${DETECTED_LAYERS[@]} " =~ " domain " ]]; then
        DETECTED_LAYERS+=("domain")
    fi
    DETECTED_KEYWORDS+=("애그리게이트")
fi

if echo "$USER_INPUT" | grep -q "컨트롤러"; then
    CONTEXT_SCORE=$((CONTEXT_SCORE + 30))
    if [[ ! " ${DETECTED_LAYERS[@]} " =~ " adapter-rest " ]]; then
        DETECTED_LAYERS+=("adapter-rest")
    fi
    DETECTED_KEYWORDS+=("컨트롤러")
fi

if echo "$USER_INPUT" | grep -q "테스트"; then
    CONTEXT_SCORE=$((CONTEXT_SCORE + 30))
    if [[ ! " ${DETECTED_LAYERS[@]} " =~ " testing " ]]; then
        DETECTED_LAYERS+=("testing")
    fi
    DETECTED_KEYWORDS+=("테스트")
fi

# Secondary Keywords (15점)
if echo "$USER_INPUT" | grep -qiE "(domain|도메인)"; then
    CONTEXT_SCORE=$((CONTEXT_SCORE + 15))
    DETECTED_KEYWORDS+=("domain_context")
fi

if echo "$USER_INPUT" | grep -qiE "(api|rest)"; then
    CONTEXT_SCORE=$((CONTEXT_SCORE + 15))
    DETECTED_KEYWORDS+=("api_context")
fi

# Zero-Tolerance Keywords (20점)
if echo "$USER_INPUT" | grep -qiE "(lombok|getter.chaining|@transactional|zero.tolerance)"; then
    PRIORITY_FILTER="critical"
    CONTEXT_SCORE=$((CONTEXT_SCORE + 20))
    DETECTED_KEYWORDS+=("zero_tolerance")
fi

# 키워드 분석 결과 로그
LAYERS_JSON=$(printf '%s\n' "${DETECTED_LAYERS[@]}" | jq -R . | jq -s . 2>/dev/null || echo "[]")
KEYWORDS_JSON=$(printf '%s\n' "${DETECTED_KEYWORDS[@]}" | jq -R . | jq -s . 2>/dev/null || echo "[]")

log_event "keyword_analysis" "{\"session_id\":\"$SESSION_ID\",\"context_score\":$CONTEXT_SCORE,\"threshold\":25,\"detected_layers\":$LAYERS_JSON,\"detected_keywords\":$KEYWORDS_JSON,\"priority_filter\":\"$PRIORITY_FILTER\"}"

# ==================== 규칙 주입 ====================

if [[ $CONTEXT_SCORE -ge 25 ]]; then
    log_event "decision" "{\"session_id\":\"$SESSION_ID\",\"action\":\"cache_injection\",\"reason\":\"score_above_threshold\"}"

    INJECT_SCRIPT=".claude/commands/lib/inject-rules.py"

    if [[ -f "$INJECT_SCRIPT" ]]; then
        for layer in "${DETECTED_LAYERS[@]}"; do
            # inject-rules.py 호출 (별도로 자체 JSON 로그 작성)
            if [[ -n "$PRIORITY_FILTER" ]]; then
                python3 "$INJECT_SCRIPT" "$layer" "$PRIORITY_FILTER"
            else
                python3 "$INJECT_SCRIPT" "$layer"
            fi
        done

        log_event "cache_injection_complete" "{\"session_id\":\"$SESSION_ID\",\"layers_count\":${#DETECTED_LAYERS[@]}}"

        # 레이어가 없으면 일반 규칙
        if [[ ${#DETECTED_LAYERS[@]} -eq 0 ]]; then
            cat << 'EOF'

---

## 🎯 기본 프로젝트 규칙 (자동 주입됨)

### ❌ Zero-Tolerance 규칙
- **Lombok 절대 금지**: @Data, @Builder, @Getter, @Setter 등 모든 Lombok 어노테이션
- **Javadoc 필수**: 모든 public 클래스/메서드에 @author, @since 포함
- **트랜잭션 경계**: @Transactional 내 외부 API 호출 절대 금지

### ✅ 필수 규칙
- **Pure Java**: Domain 레이어는 순수 Java만 사용
- **Law of Demeter**: Getter 체이닝 금지

---

EOF
        fi
    else
        log_event "error" "{\"session_id\":\"$SESSION_ID\",\"message\":\"inject-rules.py not found\"}"
    fi
else
    log_event "decision" "{\"session_id\":\"$SESSION_ID\",\"action\":\"skip_injection\",\"reason\":\"score_below_threshold\"}"
fi

# 원본 입력 반환
echo "$USER_INPUT"
