# Getting Started - Dynamic Hooks + Cache 시스템 시작하기

이 튜토리얼에서는 **Dynamic Hooks + Cache 시스템**을 처음 사용하는 방법을 단계별로 안내합니다.

---

## 📋 사전 요구사항

### 필수 도구

- **Python 3.8+**: Cache 빌드 및 검증 스크립트 실행
- **Git**: 버전 관리
- **Claude Code**: AI 기반 코드 생성 (선택 사항)

### 확인

```bash
# Python 버전 확인
python3 --version  # Python 3.8 이상

# Git 확인
git --version

# Claude Code 확인 (선택)
claude --version
```

---

## 🚀 1단계: 프로젝트 클론 및 초기 설정

### 프로젝트 클론

```bash
git clone https://github.com/your-org/claude-spring-standards.git
cd claude-spring-standards
```

### Cache 빌드

```bash
# 코딩 규칙을 JSON Cache로 변환
python3 .claude/hooks/scripts/build-rule-cache.py
```

**출력 예시**:
```
✅ Processing: docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md
✅ Generated: .claude/cache/rules/domain-layer-law-of-demeter-01_getter-chaining-prohibition.json
...
✅ Index file created: .claude/cache/rules/index.json

📊 Cache Build Complete
- Total Rules: 90
- Build Time: ~5s
```

### Hook 권한 설정

```bash
chmod +x .claude/hooks/*.sh
```

---

## 🎯 2단계: 첫 번째 코드 생성 테스트

### Domain Aggregate 생성

Claude Code 또는 수동으로 Domain Aggregate를 생성합니다.

#### 방법 1: Claude Code 사용 (권장)

```bash
# Claude Code 실행
claude code

# Order Aggregate 생성 요청
> "Create an Order aggregate in the domain layer"
```

#### 방법 2: 수동 생성

```bash
# 디렉토리 생성
mkdir -p domain/src/main/java/com/company/template/order/domain/model

# Order.java 생성 (Pure Java, No Lombok)
vim domain/src/main/java/com/company/template/order/domain/model/Order.java
```

**Order.java 예시**:
```java
package com.company.template.order.domain.model;

/**
 * Order Aggregate Root
 *
 * @author YourName
 * @since 2025-10-17
 */
public class Order {
    private final OrderId orderId;
    private final CustomerId customerId;

    private Order(OrderId orderId, CustomerId customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public static Order create(OrderId orderId, CustomerId customerId) {
        return new Order(orderId, customerId);
    }

    // Getters (No Setter)
    public OrderId getOrderId() {
        return this.orderId;
    }

    public CustomerId getCustomerId() {
        return this.customerId;
    }
}
```

---

## ✅ 3단계: 코드 검증

### validation-helper.py 실행

```bash
python3 .claude/hooks/scripts/validation-helper.py \
  domain/src/main/java/com/company/template/order/domain/model/Order.java \
  domain
```

**성공 시 출력**:
```
---

✅ **Validation Passed**

파일: `domain/src/main/java/.../Order.java`

모든 규칙을 준수합니다!

---
```

**실패 시 출력**:
```
---

⚠️ **Validation Failed**

**파일**: `domain/src/main/java/.../Order.java`

**규칙 위반**: Lombok 사용 금지

**문제**: Prohibited annotation: @Data

**금지 사항**:
- ❌ @Data
- ❌ @Builder
- ❌ @Getter

**참고**: `docs/coding_convention/02-domain-layer/...`

💡 코드를 수정한 후 다시 시도하세요.

---
```

---

## 🧪 4단계: 실전 시나리오

### 시나리오 1: Law of Demeter 위반 테스트

**나쁜 코드 (Getter 체이닝)**:
```java
public String getCustomerZipCode(Order order) {
    // ❌ Law of Demeter 위반
    return order.getCustomer().getAddress().getZipCode();
}
```

**검증 실행**:
```bash
python3 .claude/hooks/scripts/validation-helper.py Order.java domain
```

**출력**:
```
⚠️ **Validation Failed**

**규칙 위반**: Law of Demeter - Getter 체이닝 금지

**문제**: Anti-pattern detected: order.getCustomer().getAddress()
```

**좋은 코드 (Tell, Don't Ask)**:
```java
public String getCustomerZipCode(Order order) {
    // ✅ Order에게 직접 요청
    return order.getCustomerZipCode();
}

// Order 클래스 내부
public String getCustomerZipCode() {
    return this.customer.getZipCode();
}
```

### 시나리오 2: Lombok 사용 금지 테스트

**나쁜 코드**:
```java
@Data  // ❌ Lombok 금지
public class Order {
    private OrderId orderId;
    private CustomerId customerId;
}
```

**검증 실행**:
```bash
python3 .claude/hooks/scripts/validation-helper.py Order.java domain
```

**출력**:
```
⚠️ **Validation Failed**

**규칙 위반**: Lombok 사용 금지

**문제**: Prohibited annotation: @Data
```

**좋은 코드**:
```java
// ✅ Pure Java
public class Order {
    private final OrderId orderId;
    private final CustomerId customerId;

    public Order(OrderId orderId, CustomerId customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public OrderId getOrderId() {
        return this.orderId;
    }

    public CustomerId getCustomerId() {
        return this.customerId;
    }
}
```

---

## 📚 5단계: Slash Commands 사용

### /validate-domain

```bash
# Claude Code에서 실행
/validate-domain domain/src/main/java/.../Order.java
```

### /code-gen-domain

```bash
# Claude Code에서 실행
/code-gen-domain Order
```

**자동으로 수행되는 작업**:
1. Domain 레이어 규칙 주입 (inject-rules.py)
2. Order.java 생성
3. 실시간 검증 (validation-helper.py)
4. 검증 통과 확인

---

## 🔄 6단계: Cache 업데이트

### 규칙 문서 수정 후

```bash
# 1. 규칙 문서 수정
vim docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md

# 2. Cache 재빌드
python3 .claude/hooks/scripts/build-rule-cache.py

# 3. 확인
cat .claude/cache/rules/domain-layer-law-of-demeter-01_getter-chaining-prohibition.json
```

---

## 🎓 다음 단계

### 추가 튜토리얼

- [02-slash-commands.md](./02-slash-commands.md) - Slash Commands 상세 가이드
- [03-custom-rules.md](./03-custom-rules.md) - 커스텀 규칙 추가 방법

### 상세 문서

- [DYNAMIC_HOOKS_GUIDE.md](../DYNAMIC_HOOKS_GUIDE.md) - 전체 시스템 가이드
- [Cache README](./.claude/cache/rules/README.md) - Cache 시스템 상세

---

## 🛠️ 트러블슈팅

### 문제 1: Cache 파일이 없음

**증상**:
```
FileNotFoundError: .claude/cache/rules/index.json
```

**해결**:
```bash
python3 .claude/hooks/scripts/build-rule-cache.py
```

### 문제 2: 검증이 실행되지 않음

**증상**:
- validation-helper.py 실행해도 반응 없음

**해결**:
```bash
# Python 버전 확인
python3 --version  # 3.8 이상

# 스크립트 권한 확인
chmod +x .claude/hooks/scripts/validation-helper.py

# 수동 실행
python3 .claude/hooks/scripts/validation-helper.py Order.java domain
```

### 문제 3: Hook이 트리거되지 않음

**증상**:
- user-prompt-submit.sh가 실행되지 않음

**해결**:
```bash
# Hook 권한 확인
ls -la .claude/hooks/*.sh

# 권한 설정
chmod +x .claude/hooks/*.sh

# 로그 확인
tail -50 .claude/hooks/logs/hook-execution.log
```

---

## 🎉 축하합니다!

이제 Dynamic Hooks + Cache 시스템을 사용할 준비가 되었습니다!

다음 단계로 [02-slash-commands.md](./02-slash-commands.md)를 확인하세요.
