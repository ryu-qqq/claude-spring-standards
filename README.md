# Claude Spring Standards

> **Spring Boot 3.5.x + Java 21 헥사고날 아키텍처 템플릿**
> Dynamic Hooks + Cache 시스템을 통한 AI 기반 코딩 표준 자동화

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](https://alistair.cockburn.us/hexagonal-architecture/)

---

## 🎯 핵심 차별점

**Dynamic Hooks + Cache System**: 키워드 기반 Layer 감지 → JSON Cache 조회 → 규칙 자동 주입 → 실시간 검증

| 기능 | 기존 방식 | 이 프로젝트 |
|------|----------|------------|
| 코딩 표준 | 수동 리뷰 | 자동 주입 + 검증 |
| 규칙 로딩 | 전체 문서 | JSON Cache |
| 검증 속도 | 561ms | 148ms |

---

## 📖 목차

- [빠른 시작](#-빠른-시작)
- [Cache 시스템](#-cache-시스템)
- [코딩 표준](#-코딩-표준)
- [개발 워크플로우](#-개발-워크플로우)
- [로그 시스템](#-로그-시스템)
- [문제 해결](#-문제-해결)

---

## 🚀 빠른 시작

### 사전 요구사항

- Java 21+
- Gradle 8.5+
- Python 3.8+
- jq (JSON 파싱용)

### 설치

```bash
# 1. 클론
git clone https://github.com/your-org/claude-spring-standards.git
cd claude-spring-standards

# 2. Cache 빌드 (90개 규칙 → JSON, 약 5초)
python3 .claude/hooks/scripts/build-rule-cache.py

# 3. Git Hooks 설정
ln -s ../../hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# 4. 빌드
./gradlew build
```

### 첫 코드 생성

```bash
# Claude Code 실행
claude code

# Domain Aggregate 생성
> "/code-gen-domain Order"
```

**자동 수행**:
1. `user-prompt-submit.sh`: "aggregate" 키워드 감지 → domain 규칙 주입
2. Claude: Zero-Tolerance 규칙이 적용된 `Order.java` 생성
3. `after-tool-use.sh`: 실시간 검증
4. 결과: ✅ 통과 또는 ⚠️ 실패 (수정 가이드 포함)

---

## 🔥 Cache 시스템

### 작동 원리

**Before (기존 방식)**:
```
90개 마크다운 문서 전체 로딩
→ 예제, 설명 포함 전체 내용
→ 50,000+ 토큰 소비
```

**After (JSON Cache)**:
```
90개 JSON 파일 (핵심만 구조화)
→ 필요한 규칙만 O(1) 조회
→ 500-1,000 토큰 (선택적 로딩)
```

### Cache 구조

```json
{
  "id": "domain-layer-law-of-demeter-01",
  "metadata": {
    "layer": "domain",
    "priority": "critical",
    "keywords": {
      "anti": ["order.getCustomer().getAddress()"]
    }
  },
  "rules": {
    "prohibited": ["❌ Getter chaining"],
    "required": ["✅ Tell, Don't Ask"]
  }
}
```

**핵심 이점**:
- **토큰 효율**: 핵심 규칙만 JSON화하여 불필요한 예제/설명 제거
- **빠른 검색**: index.json을 통한 O(1) Layer → 규칙 매핑
- **선택적 로딩**: 감지된 Layer의 규칙만 로딩

### 키워드 → Layer 매핑

| 키워드 | Layer | 점수 |
|---------|-------|-------|
| aggregate | domain | 30 |
| controller | adapter-rest | 30 |
| usecase, service | application | 30 |
| repository, jpa | adapter-persistence | 30 |

**예시**:
```
입력: "Create Order aggregate"
→ "aggregate" 감지 (+30)
→ Layer: domain
→ 로드: domain-layer-*.json (13개 규칙)
→ 주입: Critical 우선순위 규칙
```

---

## 📚 코딩 표준

### Zero-Tolerance 규칙 (자동 검증)

#### 1. Lombok 금지

```java
// ❌ 금지
@Data
public class Order { }

// ✅ 필수
public class Order {
    private final OrderId id;

    public Order(OrderId id) {
        this.id = id;
    }

    public OrderId getId() {
        return this.id;
    }
}
```

#### 2. Law of Demeter

```java
// ❌ Getter chaining
String zip = order.getCustomer().getAddress().getZip();

// ✅ Tell, Don't Ask
String zip = order.getCustomerZip();
```

#### 3. Transaction 경계

```java
// ❌ Transaction 내 외부 API 호출
@Transactional
public Order create() {
    ExternalData data = apiClient.fetch(); // ❌
    return save(data);
}

// ✅ Transaction 외부에서 호출
public Order create() {
    ExternalData data = apiClient.fetch(); // ✅
    return saveInTransaction(data);
}

@Transactional
protected Order saveInTransaction(ExternalData data) {
    return save(data);
}
```

#### 4. Javadoc 필수

```java
/**
 * Order Aggregate Root
 *
 * @author YourName
 * @since 2025-10-17
 */
public class Order { }
```

---

## 💻 개발 워크플로우

```bash
# 1. Feature 브랜치
git checkout -b feature/order

# 2. Domain 생성
/code-gen-domain Order

# 3. Application 생성
/code-gen-usecase PlaceOrder

# 4. Adapter 생성
/code-gen-controller Order

# 5. 테스트
./gradlew test

# 6. 커밋 (pre-commit hook 자동 검증)
git add .
git commit -m "feat: order management"
```

---

## 📊 로그 시스템

### JSON 로그 구조

**위치**: `.claude/hooks/logs/hook-execution.jsonl`

**형식**: JSONL (JSON Lines, 1줄 = 1개 JSON 이벤트)

```json
{"timestamp":"2025-10-17T14:30:15","event":"session_start","project":"claude-spring-standards","hook":"user-prompt-submit","user_command":"domain aggregate"}
{"timestamp":"2025-10-17T14:30:15","event":"keyword_analysis","session_id":"1729152615-12345","context_score":45,"detected_layers":["domain"],"detected_keywords":["aggregate"]}
{"timestamp":"2025-10-17T14:30:15","event":"cache_injection","layer":"domain","rules_loaded":5,"total_rules_available":15,"estimated_tokens":2500}
{"timestamp":"2025-10-17T14:30:16","event":"validation_complete","file":"Order.java","status":"passed","total_rules":5,"validation_time_ms":148}
```

### 로그 뷰어

```bash
# 실시간 모니터링
./.claude/hooks/scripts/view-logs.sh -f

# 통계 정보
./.claude/hooks/scripts/view-logs.sh -s

# 특정 이벤트만
./.claude/hooks/scripts/view-logs.sh -e validation_complete

# 원본 JSON
./.claude/hooks/scripts/view-logs.sh -r
```

**출력 예시**:
```
[14:30:15] 🚀 SESSION_START | project=claude-spring-standards | command=domain aggregate
[14:30:15] 🔍 KEYWORD_ANALYSIS | score=45 | layers=domain | keywords=aggregate
[14:30:15] 💉 CACHE_INJECTION | layer=domain | rules=5/15 | tokens=2500
[14:30:16] ✅ VALIDATION_PASSED | file=Order.java | rules=5 | time=148ms
```

### 이벤트 타입

- `SESSION_START`: Hook 실행 시작
- `KEYWORD_ANALYSIS`: 키워드 분석 결과
- `DECISION`: 규칙 주입 결정
- `CACHE_INJECTION`: Cache 로딩
- `VALIDATION_START`: 검증 시작
- `VALIDATION_PASSED/FAILED`: 검증 결과
- `VALIDATION_ERROR`: 검증 오류

### 로그 활용 계획

**현재**: 모든 Hook 실행을 JSON으로 로깅

**향후 계획**:
1. **효과성 분석**: 실제 Layer 감지 정확도 측정
2. **토큰 사용량 분석**: Cache 시스템의 실제 토큰 절감량 확인
3. **검증 패턴 분석**: 자주 위반되는 규칙 파악 및 개선
4. **성능 최적화**: 검증 시간 병목 지점 찾기
5. **AWS CloudWatch 연동**: 로그 집계 및 실시간 모니터링 (검토 중)

---

## 🐛 문제 해결

### Cache 파일 없음

```bash
# Cache 재빌드
python3 .claude/hooks/scripts/build-rule-cache.py
```

### Hooks 미실행

```bash
# 권한 확인
ls -la .claude/hooks/*.sh

# 권한 부여
chmod +x .claude/hooks/*.sh

# 로그 확인
./.claude/hooks/scripts/view-logs.sh -f
```

### 검증 실패

```bash
# 로그에서 실패 원인 확인
./.claude/hooks/scripts/view-logs.sh -e validation_complete

# 수동 검증
python3 .claude/hooks/scripts/validation-helper.py YourFile.java layer
```

---

## 📚 문서

### 핵심 가이드
- [Getting Started](docs/tutorials/01-getting-started.md) - 5분 튜토리얼
- [Dynamic Hooks 가이드](docs/DYNAMIC_HOOKS_GUIDE.md) - 시스템 전체 설명
- [Cache README](.claude/cache/rules/README.md) - JSON Cache 상세

### 전문 주제
- [DDD Aggregate Migration](docs/DDD_AGGREGATE_MIGRATION_GUIDE.md)
- [DTO Patterns](docs/DTO_PATTERNS_GUIDE.md)
- [Exception Handling](docs/EXCEPTION_HANDLING_GUIDE.md)

---

## 📊 기술 스택

| 카테고리 | 기술 |
|----------|-----------|
| **언어** | Java 21 |
| **프레임워크** | Spring Boot 3.5.x |
| **아키텍처** | Hexagonal (Ports & Adapters) |
| **설계 패턴** | DDD, CQRS |
| **ORM** | JPA + QueryDSL |
| **테스팅** | JUnit 5, Mockito, Testcontainers, ArchUnit |
| **AI 통합** | Claude Code + Dynamic Hooks |

---

## 📄 라이선스

© 2024 Ryu-qqq. All Rights Reserved.

---

*최종 업데이트: 2025-10-17*
