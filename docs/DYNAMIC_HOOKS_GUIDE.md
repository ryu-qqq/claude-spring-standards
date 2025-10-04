# 🎣 Dynamic Hooks Guide - Claude Code 실시간 검증

이 프로젝트는 **두 가지 훅 시스템**을 사용합니다:

---

## 🔄 Two-Tier Hook System

### 1️⃣ **Claude Code Dynamic Hooks** (실시간 검증)
**위치**: `.claude/hooks/`

**실행 시점**: Claude가 코드를 생성하는 **실시간**

**목적**:
- Claude에게 모듈별 규칙 주입
- 코드 생성 직후 즉시 검증
- 위반사항 실시간 피드백

### 2️⃣ **Git Pre-Commit Hooks** (커밋 시점 검증)
**위치**: `hooks/` → `.git/hooks/`

**실행 시점**: `git commit` 실행 시

**목적**:
- 최종 게이트키퍼 역할
- ArchUnit 테스트 실행
- 전체 파일 통합 검증

---

## 🎯 Dynamic Hooks 상세

### Hook 1: `user-prompt-submit.sh`

**트리거**: 사용자가 요청을 제출할 때

**동작**:
```bash
사용자 요청 분석
    ↓
모듈 키워드 감지
  - "domain", "도메인" → Domain 가이드라인
  - "usecase", "서비스" → Application 가이드라인
  - "controller", "api" → Adapter 가이드라인
  - "repository", "jpa" → Persistence 가이드라인
    ↓
모듈별 규칙을 Claude에게 주입
    ↓
글로벌 규칙 리마인드 (Lombok 금지 등)
```

**예시**:
```bash
사용자: "Order 도메인 엔티티를 만들어줘"
    ↓
Hook 감지: "도메인" 키워드
    ↓
Claude에게 주입:
  ❌ NO Spring, NO JPA, NO Lombok
  ✅ Pure Java만 허용
  📝 Javadoc + @author 필수
  🧪 90% 커버리지 목표
```

---

### Hook 2: `after-tool-use.sh`

**트리거**: Claude가 Write/Edit 도구를 사용한 직후

**동작**:
```bash
파일 경로 분석
    ↓
모듈 감지 (domain/application/adapter)
    ↓
해당 모듈 validator 실행
  - domain-validator.sh
  - application-validator.sh
  - adapter-in-validator.sh
  - adapter-out-validator.sh
    ↓
공통 검증 (Javadoc, @author)
    ↓
데드코드 감지
    ↓
❌ 실패 시 경고 + 수정 요청
✅ 통과 시 계속 진행
```

**예시**:
```bash
Claude: Order.java 파일을 domain/에 생성
    ↓
Hook 트리거: after-tool-use (Write)
    ↓
파일 경로: domain/model/Order.java
    ↓
domain-validator.sh 실행
    ↓
검사 항목:
  ❌ Spring import 있나? → 차단
  ❌ Lombok annotation 있나? → 차단
  ❌ JPA annotation 있나? → 차단
  ✅ 모두 통과 → 성공
```

---

## 🎨 Hook이 주입하는 가이드라인

### Domain Module
```markdown
❌ ABSOLUTELY FORBIDDEN
- NO Spring Framework
- NO JPA/Hibernate
- NO Lombok
- NO Jackson annotations

✅ ALLOWED
- Pure Java
- Apache Commons Lang3
- Domain value objects

📝 REQUIRED
- Javadoc with @author
- 90%+ test coverage
- Manual getters/setters (no Lombok)
```

### Application Module
```markdown
❌ ABSOLUTELY FORBIDDEN
- NO Adapter imports
- NO Lombok
- NO direct JPA usage

✅ ALLOWED
- Domain imports
- Spring DI (@Service, @Transactional)
- Port interfaces

📝 REQUIRED
- UseCase suffix for use cases
- 80%+ test coverage
```

### Adapter Module
```markdown
❌ ABSOLUTELY FORBIDDEN
- NO Lombok
- NO business logic

✅ ALLOWED
- Spring Framework
- JPA, AWS SDK, etc.
- Infrastructure code

📝 REQUIRED
- Controller/Repository suffix
- 70%+ test coverage
- Testcontainers for integration tests
```

---

## 🚀 실전 시나리오

### 시나리오 1: Domain 엔티티 생성

```bash
# 사용자 요청
"Order 도메인 엔티티를 만들어줘"

# user-prompt-submit.sh 실행
→ "도메인" 키워드 감지
→ Domain 가이드라인 주입

# Claude 코드 생성
public class Order {
    private String id;
    // ... pure Java
}

# after-tool-use.sh 실행
→ domain/model/Order.java 감지
→ domain-validator.sh 실행
→ ✅ Spring/JPA/Lombok 없음 확인
→ ✅ 통과
```

---

### 시나리오 2: Lombok 사용 시도 (차단)

```bash
# 사용자 요청 (잘못된 요청)
"Order 엔티티를 Lombok으로 만들어줘"

# user-prompt-submit.sh 실행
→ Lombok 금지 경고 주입

# Claude가 순수 Java로 생성 (가이드라인 따름)
public class Order {
    private final String id;

    public Order(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

# 만약 Claude가 실수로 Lombok 사용했다면?
# after-tool-use.sh 실행
→ domain-validator.sh 실행
→ ❌ Lombok import 감지
→ ❌ 검증 실패, Claude에게 수정 요청
```

---

### 시나리오 3: Application → Adapter 의존성 시도 (차단)

```bash
# 사용자 요청
"CreateOrderService에서 OrderController를 호출해줘"

# user-prompt-submit.sh 실행
→ "서비스" 키워드 감지
→ Application 가이드라인 주입
→ "NO Adapter imports" 경고

# Claude가 올바르게 Port 사용
public class CreateOrderService {
    private final OrderRepository repository; // Port 인터페이스
    // Controller 호출 안함
}

# after-tool-use.sh 실행
→ application/service/ 감지
→ application-validator.sh 실행
→ ✅ Adapter import 없음 확인
→ ✅ 통과
```

---

## 📊 Hook 실행 흐름 다이어그램

```
┌─────────────────────────────────────────────┐
│  사용자: "Order 도메인 엔티티 만들어줘"      │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│  HOOK 1: user-prompt-submit.sh              │
│  - 키워드 분석: "도메인" → Domain context   │
│  - 가이드라인 주입                          │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│  Claude: 코드 생성                          │
│  - Domain 가이드라인 따라 순수 Java 작성   │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│  HOOK 2: after-tool-use.sh                  │
│  - 파일: domain/model/Order.java            │
│  - Validator: domain-validator.sh 실행      │
│  - 검증: Spring/JPA/Lombok 체크             │
└─────────────────────────────────────────────┘
                    ↓
            ✅ 통과 또는 ❌ 실패
```

---

## 🔧 설정 방법

### 1. Hook 활성화 확인

Claude Code는 자동으로 `.claude/hooks/` 디렉토리의 훅을 인식합니다.

**확인 방법**:
```bash
ls -la .claude/hooks/
# user-prompt-submit.sh (실행 권한 있어야 함)
# after-tool-use.sh (실행 권한 있어야 함)
```

### 2. 권한 설정

```bash
chmod +x .claude/hooks/*.sh
```

### 3. 테스트

```bash
# Claude에게 요청
"Order 도메인 엔티티를 만들어줘"

# Hook이 작동하는지 확인
# → 가이드라인이 주입된 메시지가 표시되어야 함
# → 코드 생성 후 검증 결과가 표시되어야 함
```

---

## 🎯 Benefits

### ✅ 실시간 피드백
- 커밋 전에 문제 발견
- Claude가 즉시 수정 가능

### ✅ 컨텍스트 주입
- 모듈별 규칙 자동 주입
- Claude가 규칙을 이해하고 따름

### ✅ 이중 안전망
- Dynamic Hook (실시간)
- Git Hook (커밋 시점)

### ✅ 개발자 경험 향상
- 명확한 에러 메시지
- 즉각적인 수정 가이드

---

## 📝 Hook 커스터마이징

### 모듈별 규칙 추가

`user-prompt-submit.sh`에서 규칙 수정:

```bash
case $MODULE_CONTEXT in
    domain)
        cat << 'EOF'
# 여기에 Domain 규칙 추가
- NEW RULE: 모든 엔티티는 ID를 가져야 함
EOF
        ;;
esac
```

### Validator 강화

`hooks/validators/domain-validator.sh`에서 검증 로직 추가:

```bash
# 새로운 검증 규칙 추가
if grep -q "public class.*Entity" "$file"; then
    if ! grep -q "private.*id" "$file"; then
        log_error "$file: Entity must have an 'id' field"
    fi
fi
```

---

## 🚨 Troubleshooting

### Hook이 실행되지 않을 때

```bash
# 1. 권한 확인
ls -la .claude/hooks/
# -rwxr-xr-x (x가 있어야 함)

# 2. 권한 부여
chmod +x .claude/hooks/*.sh

# 3. 스크립트 문법 확인
bash -n .claude/hooks/user-prompt-submit.sh
```

### Validator 실패 디버깅

```bash
# 직접 실행해보기
bash hooks/validators/domain-validator.sh domain/model/Order.java

# 상세 로그
set -x  # 스크립트 상단에 추가
```

---

## 🎉 결론

**Dynamic Hooks = Claude가 코드 작성 중 실시간으로 규칙을 따르게 만드는 시스템**

- **Before 코드 작성**: 규칙 주입
- **After 코드 작성**: 즉시 검증
- **커밋 전**: Git Hook으로 최종 확인

**이제 Claude는 항상 프로젝트 규칙을 따릅니다!** 🚀
