# /code-gen-orchestrator - Orchestration Pattern Boilerplate 자동 생성

## 목적

Orchestration Pattern Boilerplate를 자동 생성하여 외부 API 호출을 안전하게 처리합니다.

**핵심 기능**:
- ✅ 멱등성 보장 (Idempotency)
- ✅ 크래시 복구 (Write-Ahead Log)
- ✅ 재시도 로직 (Exponential Backoff)
- ✅ 타입 안전 결과 처리 (Outcome Modeling)

---

## 사용법

```bash
/code-gen-orchestrator <Domain> <EventType>
```

**파라미터**:
- `<Domain>`: Domain 이름 (PascalCase, 예: `Payment`, `FileUpload`)
- `<EventType>`: Event 타입 (PascalCase, 예: `PaymentRequested`, `FileUploadRequested`)

**예시**:
```bash
/code-gen-orchestrator Payment PaymentRequested
/code-gen-orchestrator FileUpload FileUploadRequested
/code-gen-orchestrator Notification NotificationSent
```

---

## 생성 파일 구조

```
application/
├── orchestrator/
│   ├── payment/
│   │   ├── PaymentCommand.java         ✅ 자동 (100%)
│   │   ├── PaymentOrchestrator.java    ⚠️ 골격 (executeInternal 구현 필요)
│   │   ├── PaymentFinalizer.java       ✅ 자동 (100%)
│   │   └── PaymentReaper.java          ✅ 자동 (100%)
│
adapter-out/
├── persistence-mysql/
│   ├── orchestration/
│   │   ├── OperationEntity.java        ✅ 자동 (100%)
│   │   ├── WriteAheadLogEntity.java    ✅ 자동 (100%)
│   │   ├── OperationRepository.java    ✅ 자동 (100%)
│   │   └── WriteAheadLogRepository.java ✅ 자동 (100%)
│
adapter-in/
├── web/
│   ├── payment/
│   │   └── PaymentController.java      ⚠️ 골격 (Response DTO 매핑 필요)
│
tests/
├── orchestrator/
│   ├── PaymentOrchestratorTest.java    ✅ 자동 (90%, 비즈니스 로직 테스트 TODO)
```

**자동화율**: **80-85%** (비즈니스 로직 제외)

---

## 자동 생성 코드 예제

### 1. PaymentCommand.java (100% 자동)

```java
package com.example.application.orchestrator.payment;

import com.example.common.orchestration.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 결제 요청 Command
 *
 * @param orderId 주문 ID
 * @param idempotencyKey 멱등성 키 (클라이언트 생성)
 * @param amount 결제 금액
 * @author coding-convention-09
 * @since 1.0
 */
public record PaymentCommand(
    String orderId,
    String idempotencyKey,
    BigDecimal amount
) {
    public PaymentCommand {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");
        Objects.requireNonNull(amount, "amount must not be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }
}
```

### 2. PaymentOrchestrator.java (70% 자동, executeInternal 구현 필요)

```java
package com.example.application.orchestrator.payment;

import com.example.common.orchestration.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 결제 Orchestrator
 *
 * <p>Accept → Execute → Finalize 3단계로 결제를 안전하게 처리합니다.</p>
 *
 * @author coding-convention-09
 * @since 1.0
 */
@Service
public class PaymentOrchestrator extends BaseOrchestrator<PaymentCommand> {

    // TODO: Inject PaymentGateway
    // private final PaymentGateway paymentGateway;

    public PaymentOrchestrator(
        OperationRepository operationRepository,
        WriteAheadLogRepository walRepository
    ) {
        super(operationRepository, walRepository);
    }

    @Override
    protected Domain domain() {
        return Domain.PAYMENT;
    }

    @Override
    protected EventType eventType() {
        return EventType.PAYMENT_REQUESTED;
    }

    /**
     * 결제 실행 (외부 API 호출)
     *
     * <p><b>⚠️ 개발자 구현 필요</b>:</p>
     * <ul>
     *   <li>외부 결제 게이트웨이 호출</li>
     *   <li>성공 시: Outcome.ok() 반환</li>
     *   <li>일시적 오류 시: Outcome.retry() 반환</li>
     *   <li>영구적 오류 시: Outcome.fail() 반환</li>
     * </ul>
     *
     * @param opId Operation ID
     * @param cmd 결제 Command
     * @return 실행 결과
     */
    @Override
    @Async
    protected Outcome executeInternal(OpId opId, PaymentCommand cmd) {
        // TODO: Implement business logic
        //
        // Example:
        // try {
        //     String txId = paymentGateway.charge(cmd.orderId(), cmd.amount());
        //     return Outcome.ok(opId, "Payment completed: " + txId);
        // } catch (TransientException e) {
        //     return Outcome.retry(e.getMessage(), 1, calculateBackoff(1));
        // } catch (PermanentException e) {
        //     return Outcome.fail(e.getErrorCode(), e.getMessage(), "N/A");
        // }

        throw new UnsupportedOperationException(
            "executeInternal() must be implemented by developer"
        );
    }

    /**
     * Exponential Backoff 계산
     *
     * @param attemptCount 시도 횟수
     * @return 다음 재시도까지 대기 시간 (밀리초)
     */
    private long calculateBackoff(int attemptCount) {
        // 5초, 10초, 20초, 40초, ...
        return 5000L * (long) Math.pow(2, attemptCount - 1);
    }
}
```

### 3. PaymentController.java (60% 자동, Response DTO 매핑 필요)

```java
package com.example.adapter.in.web.payment;

import com.example.application.orchestrator.payment.PaymentOrchestrator;
import com.example.application.orchestrator.payment.PaymentCommand;
import com.example.common.orchestration.Outcome;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 결제 REST API Controller
 *
 * @author coding-convention-09
 * @since 1.0
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentOrchestrator paymentOrchestrator;

    public PaymentController(PaymentOrchestrator paymentOrchestrator) {
        this.paymentOrchestrator = paymentOrchestrator;
    }

    /**
     * 결제 요청
     *
     * @param request 결제 요청 DTO
     * @return 202 Accepted (Operation ID 반환)
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
        @RequestBody PaymentRequest request
    ) {
        PaymentCommand command = new PaymentCommand(
            request.orderId(),
            request.idempotencyKey(),
            request.amount()
        );

        Outcome outcome = paymentOrchestrator.accept(command);

        // TODO: Map Outcome to Response DTO
        return switch (outcome) {
            case Outcome.Ok(var opId, var msg) ->
                ResponseEntity.accepted()
                    .body(new PaymentResponse(opId.value(), "ACCEPTED", msg));

            case Outcome.Fail(var code, var msg, var cause) ->
                ResponseEntity.badRequest()
                    .body(new PaymentResponse(null, "FAILED", msg));

            default ->
                ResponseEntity.internalServerError()
                    .body(new PaymentResponse(null, "ERROR", "Unexpected outcome"));
        };
    }
}
```

### 4. PaymentOrchestratorTest.java (90% 자동)

```java
package com.example.application.orchestrator.payment;

import com.example.common.orchestration.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * PaymentOrchestrator 테스트
 *
 * @author coding-convention-09
 * @since 1.0
 */
@SpringBootTest
class PaymentOrchestratorTest {

    @Autowired
    private PaymentOrchestrator orchestrator;

    @Autowired
    private OperationRepository operationRepository;

    @Test
    void accept_ShouldReturnOk_WhenValidCommand() {
        // Given
        PaymentCommand command = new PaymentCommand(
            "ORDER-001",
            "IDEM-12345",
            BigDecimal.valueOf(10000)
        );

        // When
        Outcome outcome = orchestrator.accept(command);

        // Then
        assertThat(outcome).isInstanceOf(Outcome.Ok.class);
        OpId opId = ((Outcome.Ok) outcome).opId();
        assertThat(opId).isNotNull();
    }

    @Test
    void accept_ShouldReturnSameOpId_WhenDuplicateIdemKey() {
        // Given
        PaymentCommand command = new PaymentCommand(
            "ORDER-002",
            "IDEM-67890",
            BigDecimal.valueOf(5000)
        );

        // When
        Outcome first = orchestrator.accept(command);
        Outcome second = orchestrator.accept(command);

        // Then
        assertThat(first).isInstanceOf(Outcome.Ok.class);
        assertThat(second).isInstanceOf(Outcome.Ok.class);

        OpId firstOpId = ((Outcome.Ok) first).opId();
        OpId secondOpId = ((Outcome.Ok) second).opId();

        assertThat(firstOpId).isEqualTo(secondOpId);
    }

    // TODO: Add more tests:
    // - Retry logic (executeInternal에 비즈니스 로직 구현 후)
    // - WAL recovery (Finalizer 테스트)
    // - Timeout handling (Reaper 테스트)
    // - Concurrent requests (Race condition)
}
```

---

## 개발자 TODO

생성된 코드에서 **반드시 구현해야 할 부분**:

### 1️⃣ executeInternal() 구현 (필수)

```java
@Override
@Async
protected Outcome executeInternal(OpId opId, PaymentCommand cmd) {
    try {
        // 외부 API 호출
        String transactionId = paymentGateway.charge(
            cmd.orderId(),
            cmd.amount()
        );

        return Outcome.ok(opId, "Payment completed: " + transactionId);

    } catch (PaymentGatewayException e) {
        if (e.isRetryable()) {
            // 일시적 오류 → 재시도
            return Outcome.retry(
                e.getMessage(),
                1,
                calculateBackoff(1)
            );
        } else {
            // 영구적 오류 → 실패
            return Outcome.fail(
                e.getErrorCode(),
                e.getMessage(),
                e.getCause() != null ? e.getCause().toString() : "N/A"
            );
        }
    }
}
```

### 2️⃣ Dependencies 주입 (필수)

```java
private final PaymentGateway paymentGateway;

public PaymentOrchestrator(
    OperationRepository operationRepository,
    WriteAheadLogRepository walRepository,
    PaymentGateway paymentGateway  // 추가
) {
    super(operationRepository, walRepository);
    this.paymentGateway = paymentGateway;
}
```

### 3️⃣ Response DTO 매핑 (선택)

```java
// PaymentResponse.java
public record PaymentResponse(
    String opId,
    String status,
    String message
) {}

// Controller에서 사용
return ResponseEntity.accepted()
    .body(new PaymentResponse(opId.value(), "ACCEPTED", msg));
```

### 4️⃣ 추가 테스트 작성 (권장)

```java
@Test
void execute_ShouldRetry_WhenTransientError() {
    // Given: PaymentGateway가 일시적 오류
    // When: Execute
    // Then: Retry 횟수 확인
}

@Test
void finalizer_ShouldRecoverPendingWal() {
    // Given: PENDING WAL 존재
    // When: Finalizer 실행
    // Then: WAL이 COMPLETED로 변경
}
```

---

## 컨벤션 자동 검증

생성된 코드는 즉시 검증됩니다:

| 검증 항목 | 도구 | 설명 |
|----------|------|------|
| ✅ Transaction 경계 | validation-helper.py | executeInternal이 @Async인지 확인 |
| ✅ Lombok 금지 | validation-helper.py | Record 패턴 사용 확인 |
| ✅ Javadoc 필수 | validation-helper.py | @author, @since 확인 |
| ✅ Long FK 전략 | validation-helper.py | Entity에 관계 어노테이션 없는지 확인 |
| ✅ Idempotency | validation-helper.py | IdemKey Unique 제약 확인 |

**위반 시**: 즉시 경고 + 구체적인 수정 방법 제시

---

## 실행 예시

### 1. 명령어 실행

```bash
/code-gen-orchestrator Payment PaymentRequested
```

### 2. 자동 실행 흐름

```
1️⃣ 키워드 감지 (user-prompt-submit.sh)
   → "orchestrator" 키워드 감지 (40점)
   → Layer: "orchestration" 매핑

2️⃣ Serena Memory 로드
   → read_memory("coding_convention_orchestration_layer")

3️⃣ Cache 규칙 주입 (inject-rules.py)
   → orchestration-pattern-overview.json
   → command-pattern.json
   → idempotency-handling.json
   → write-ahead-log-pattern.json
   → outcome-modeling.json

4️⃣ 코드 생성 (Claude Code)
   → PaymentCommand.java (100%)
   → PaymentOrchestrator.java (70%, executeInternal TODO)
   → Entities (100%)
   → Repositories (100%)
   → Schedulers (100%)
   → Controller (60%, Response DTO TODO)
   → Tests (90%, 비즈니스 로직 테스트 TODO)

5️⃣ 실시간 검증 (validation-helper.py)
   → ✅ Transaction 경계: OK
   → ✅ Lombok 금지: OK
   → ✅ Javadoc 필수: OK
   → ✅ Long FK 전략: OK
   → ✅ Idempotency: OK
```

### 3. 출력 메시지

```
✅ Orchestration Pattern Boilerplate 생성 완료!

📁 생성된 파일:
  - application/orchestrator/payment/PaymentCommand.java
  - application/orchestrator/payment/PaymentOrchestrator.java (⚠️ executeInternal 구현 필요)
  - application/orchestrator/payment/PaymentFinalizer.java
  - application/orchestrator/payment/PaymentReaper.java
  - adapter-out/persistence-mysql/orchestration/OperationEntity.java
  - adapter-out/persistence-mysql/orchestration/WriteAheadLogEntity.java
  - adapter-out/persistence-mysql/orchestration/OperationRepository.java
  - adapter-out/persistence-mysql/orchestration/WriteAheadLogRepository.java
  - adapter-in/web/payment/PaymentController.java (⚠️ Response DTO 매핑 필요)
  - tests/orchestrator/PaymentOrchestratorTest.java

📋 개발자 TODO:
  1. PaymentOrchestrator.executeInternal() 구현 (필수)
  2. PaymentGateway 주입 (필수)
  3. PaymentResponse DTO 매핑 (선택)
  4. 추가 테스트 작성 (권장)

📝 다음 단계:
  1. executeInternal() 구현
  2. ./gradlew test 실행
  3. /validate-architecture 실행
```

---

## 예상 소요 시간

| 단계 | 소요 시간 | 담당 |
|------|----------|------|
| Boilerplate 생성 | **30초** | Claude Code |
| executeInternal() 구현 | **10-20분** | 개발자 |
| 테스트 작성 | **자동 생성** | Claude Code |
| 검증 및 PR | **5분** | Claude Code |
| **총계** | **15-30분** | |

**기존 방식**: 2-3시간 → **70-80% 시간 단축**

---

## 참고 문서

### 패턴 상세
- [00_orchestration-pattern-overview.md](../../docs/coding_convention/09-orchestration-patterns/00_orchestration-pattern-overview.md)
- [01_command-pattern.md](../../docs/coding_convention/09-orchestration-patterns/01_command-pattern.md)
- [02_idempotency-handling.md](../../docs/coding_convention/09-orchestration-patterns/02_idempotency-handling.md)
- [03_write-ahead-log-pattern.md](../../docs/coding_convention/09-orchestration-patterns/03_write-ahead-log-pattern.md)
- [04_outcome-modeling.md](../../docs/coding_convention/09-orchestration-patterns/04_outcome-modeling.md)

### 추가 가이드
- [05_quick-start-guide.md](../../docs/coding_convention/09-orchestration-patterns/05_quick-start-guide.md)
- [06_security-guide.md](../../docs/coding_convention/09-orchestration-patterns/06_security-guide.md)
- [07_automation-analysis.md](../../docs/coding_convention/09-orchestration-patterns/07_automation-analysis.md)

---

**✅ 핵심**: 80-85% 자동 생성 + 90-95% 컨벤션 준수율
