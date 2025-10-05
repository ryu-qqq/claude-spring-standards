#!/bin/bash

# init-session.sh - Claude Code SessionStart Hook
# 세션 시작 시 Git 브랜치에서 Jira 태스크를 파싱하고 핵심 규칙을 주입합니다.

set -euo pipefail

# ===== 브랜치 및 Jira 태스크 파싱 =====
BRANCH=$(git branch --show-current 2>/dev/null || echo "unknown")
JIRA_TASK=$(echo "$BRANCH" | grep -oE 'FF-[0-9]+' || echo "")

# ===== 규칙 문서 경로 =====
RULES_DOC="docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md"
SESSION_CONTEXT="/tmp/claude-session-context.md"

# ===== 핵심 규칙 추출 함수 =====
extract_critical_rules() {
    if [ ! -f "$RULES_DOC" ]; then
        echo "⚠️ 경고: $RULES_DOC 파일을 찾을 수 없습니다."
        return 1
    fi

    # Domain Layer 핵심 규칙 (🔴 CRITICAL만 추출)
    DOMAIN_RULES=$(grep -A 2 "^### D-.*🔴 CRITICAL" "$RULES_DOC" | grep "^###" | head -5 | sed 's/^### /- /')

    # Application Layer 핵심 규칙
    APP_RULES=$(grep -A 2 "^### A-.*🔴 CRITICAL" "$RULES_DOC" | grep "^###" | head -5 | sed 's/^### /- /')

    # Adapter Layer 핵심 규칙
    ADAPTER_RULES=$(grep -A 2 "^### A[IO]-.*🔴 CRITICAL" "$RULES_DOC" | grep "^###" | head -5 | sed 's/^### /- /')

    echo "$DOMAIN_RULES"
    echo "$APP_RULES"
    echo "$ADAPTER_RULES"
}

# ===== 세션 컨텍스트 생성 =====
cat > "$SESSION_CONTEXT" <<CONTEXT
# 🚀 Claude Code Session Context

## 📋 현재 작업 정보
- **Git Branch**: \`$BRANCH\`
- **Jira Task**: ${JIRA_TASK:-"N/A (브랜치명에 FF-XXX 패턴 없음)"}
- **Project**: claude-spring-standards (Hexagonal Architecture)
- **Stack**: Spring Boot 3.3.x + Java 21

---

## 🔴 CRITICAL 규칙 요약

### Domain Layer
❌ **ABSOLUTELY FORBIDDEN**:
- NO Spring Framework imports (\`org.springframework.*\`)
- NO JPA/Hibernate annotations (\`@Entity\`, \`@Table\`, etc.)
- NO Lombok (\`@Getter\`, \`@Setter\`, \`@Data\`, etc.)
- NO mutable fields (모든 필드는 \`private final\`)

✅ **REQUIRED**:
- Pure Java only (Apache Commons Lang3만 허용)
- Immutable value objects with factory methods
- Javadoc + \`@author\` 태그 필수
- 90%+ test coverage

### Application Layer
❌ **ABSOLUTELY FORBIDDEN**:
- NO Adapter 직접 참조 (Controller, Repository 구현체)
- NO Lombok
- NO JPA entities in use case logic

✅ **REQUIRED**:
- UseCase suffix for use case classes
- Port interfaces only (의존성 역전)
- \`@Service\`, \`@Transactional\` 허용
- 80%+ test coverage

### Adapter Layer
❌ **ABSOLUTELY FORBIDDEN**:
- NO Lombok
- NO business logic (비즈니스 로직은 Domain/Application)
- NO direct domain manipulation

✅ **REQUIRED**:
- Controller/Repository suffix
- Testcontainers for integration tests
- 70%+ test coverage

---

## 🚨 금지어 (즉시 거부해야 하는 표현)
- "일단", "나중에", "TODO로 남기고"
- "임시로", "테스트는 나중에"
- "Lombok으로 간단하게", "setter 추가"

---

## 🎯 품질 게이트
- **ArchUnit 테스트**: \`HexagonalArchitectureTest.java\` 필수 통과
- **Checkstyle**: Javadoc + @author 검증
- **Git Pre-commit Hooks**: 8개 validator 자동 실행
- **커버리지**: Domain 90%, Application 80%, Adapter 70%

---

## 📚 참고 문서
- **규칙 전체**: \`$RULES_DOC\` (87개 규칙, 2850줄)
- **Dynamic Hooks**: \`.claude/hooks/README.md\`
- **Git Hooks**: \`hooks/README.md\`
- **TODO 추적**: \`TODO_IMPLEMENTATION.md\`

---

**⚡ 이 컨텍스트는 세션 시작 시 자동 생성되었습니다.**
**📍 변경사항 있을 시 수동으로 \`$RULES_DOC\`를 참조하세요.**

CONTEXT

# ===== 출력 메시지 =====
echo ""
echo "✅ 세션 초기화 완료"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📋 브랜치: $BRANCH"
if [ -n "$JIRA_TASK" ]; then
    echo "🎫 Jira Task: $JIRA_TASK"
else
    echo "⚠️  Jira Task: 없음 (브랜치명에 FF-XXX 패턴 필요)"
fi
echo "📄 세션 컨텍스트: $SESSION_CONTEXT"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

exit 0
