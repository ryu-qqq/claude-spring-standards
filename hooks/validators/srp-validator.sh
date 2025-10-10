#!/bin/bash
# ========================================
# Single Responsibility Principle Validator
# ========================================
# 단일 책임 원칙 (Single Responsibility Principle) 검증
# - 메서드 개수 제한
# - 클래스 라인 수 제한
# - 의심스러운 네이밍 감지
# - 레이어별 차등 적용
#
# @author Sangwon Ryu (ryu@company.com)
# @since 2025-01-10
# ========================================

set -e

FILE="$1"

# Java 파일만 검증
if [[ ! "$FILE" =~ \.java$ ]]; then
    exit 0
fi

# Colors
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

log_error() {
    echo -e "${RED}❌ [SRP] $1${NC}" >&2
}

log_warning() {
    echo -e "${YELLOW}⚠️  [SRP] $1${NC}" >&2
}

log_info() {
    echo -e "${GREEN}ℹ️  [SRP] $1${NC}" >&2
}

VIOLATIONS=0

# ========================================
# 레이어 감지
# ========================================

LAYER="unknown"
if [[ "$FILE" == *"/domain/"* ]]; then
    LAYER="domain"
    MAX_METHODS=7
    MAX_LINES=200
elif [[ "$FILE" == *"/application/"* ]]; then
    LAYER="application"
    MAX_METHODS=5
    MAX_LINES=150
elif [[ "$FILE" == *"/adapter/"* ]]; then
    LAYER="adapter"
    MAX_METHODS=10
    MAX_LINES=300
else
    # 기본값
    MAX_METHODS=10
    MAX_LINES=300
fi

log_info "Checking SRP in: $(basename "$FILE") [$LAYER layer]"

# ========================================
# 패턴 1: 클래스 라인 수 체크
# ========================================

CLASS_LINES=$(wc -l < "$FILE" | tr -d ' ')

if [ "$CLASS_LINES" -gt "$MAX_LINES" ]; then
    log_error "Class too long: $CLASS_LINES lines (max: $MAX_LINES for $LAYER)"

    cat << EOF

⚠️  단일 책임 원칙 위반: 클래스가 너무 김

파일: $FILE
현재: $CLASS_LINES lines
최대: $MAX_LINES lines ($LAYER layer)

수정 방법:
1. 클래스를 여러 개로 분리
2. 책임별로 클래스 나누기
3. Extract Class 리팩토링 적용

예시:
  // BEFORE (400 lines)
  class UserOrderManager {
      // User 관련 메서드 200 lines
      // Order 관련 메서드 200 lines
  }

  // AFTER
  class UserManager {  // 200 lines
      // User 관련만
  }
  class OrderManager { // 200 lines
      // Order 관련만
  }

EOF
    VIOLATIONS=$((VIOLATIONS + 1))
fi

# ========================================
# 패턴 2: Public 메서드 개수 체크
# ========================================

# public으로 시작하는 메서드 (생성자, equals, hashCode, toString 제외)
PUBLIC_METHOD_COUNT=$(grep -c "^\s*public\s\+[^c]\w\+\s\+\w\+\s*(" "$FILE" || true)

# equals, hashCode, toString 제외
PUBLIC_METHOD_COUNT=$((PUBLIC_METHOD_COUNT - $(grep -c "public.*\(equals\|hashCode\|toString\)" "$FILE" || true)))

if [ "$PUBLIC_METHOD_COUNT" -gt "$MAX_METHODS" ]; then
    log_error "Too many public methods: $PUBLIC_METHOD_COUNT (max: $MAX_METHODS for $LAYER)"

    cat << EOF

⚠️  단일 책임 원칙 위반: 메서드가 너무 많음

파일: $FILE
현재: $PUBLIC_METHOD_COUNT public methods
최대: $MAX_METHODS methods ($LAYER layer)

이유:
- 많은 메서드 = 여러 책임을 담고 있을 가능성
- 클래스 응집도가 낮을 수 있음

수정 방법:
1. 관련된 메서드끼리 묶어 새 클래스로 추출
2. Strategy 패턴 적용
3. 도메인 개념별로 클래스 분리

예시 ($LAYER layer):
EOF

    if [[ "$LAYER" == "domain" ]]; then
        cat << EOF
  // BEFORE
  class Order {
      // 주문 생성/수정/삭제
      // 결제 처리
      // 배송 관리
      // 알림 발송
      // 10+ 메서드
  }

  // AFTER
  class Order {
      // 주문 생애주기만 (≤ 7 메서드)
  }
  class Payment { }
  class Delivery { }
  class Notification { }
EOF
    elif [[ "$LAYER" == "application" ]]; then
        cat << EOF
  // BEFORE
  class OrderService {
      createOrder()
      updateOrder()
      cancelOrder()
      processPayment()
      shipOrder()
      // 8+ 메서드 = 여러 UseCase 혼재
  }

  // AFTER
  class CreateOrderUseCase { execute() }
  class UpdateOrderUseCase { execute() }
  class CancelOrderUseCase { execute() }
  class ProcessPaymentUseCase { execute() }
  // 각각 ≤ 5 메서드
EOF
    fi

    echo ""
    VIOLATIONS=$((VIOLATIONS + 1))
fi

# ========================================
# 패턴 3: 의심스러운 네이밍
# ========================================

BASENAME=$(basename "$FILE" .java)

# God Object 의심 네이밍
if [[ "$BASENAME" =~ (Manager|Helper|Util|Handler|Processor|Controller|Service)$ ]]; then
    # Controller, Service는 레이어 이름으로 허용
    if [[ "$BASENAME" =~ (Manager|Helper|Util|Handler|Processor)$ ]]; then
        log_warning "Suspicious class name: $BASENAME"

        cat << EOF

⚠️  의심스러운 클래스 네이밍

파일: $FILE
클래스명: $BASENAME

문제:
- "Manager", "Helper", "Util" 등은 God Class 징후
- 명확한 책임을 나타내지 못함

수정 방법:
1. 구체적인 도메인 개념으로 이름 변경
2. 실제로 하는 일을 이름에 반영

예시:
  ❌ UserManager
  ✅ UserRegistrar, UserAuthenticator, UserProfileUpdater

  ❌ OrderHelper
  ✅ OrderValidator, OrderCalculator, OrderNotifier

  ❌ DataUtil
  ✅ DataFormatter, DataValidator, DataTransformer

EOF
        VIOLATIONS=$((VIOLATIONS + 1))
    fi
fi

# "And" 포함 = 여러 책임
if [[ "$BASENAME" =~ And ]]; then
    log_error "Class name contains 'And': $BASENAME - multiple responsibilities"

    cat << EOF

⚠️  클래스 이름에 "And" 포함

파일: $FILE
클래스명: $BASENAME

문제:
- "And"는 여러 책임을 암시
- 하나의 클래스가 두 가지 일을 함

수정 방법:
- 두 개의 클래스로 분리

예시:
  ❌ UserAndOrderManager
  ✅ UserManager + OrderManager

  ❌ ValidateAndSaveService
  ✅ Validator + Saver

EOF
    VIOLATIONS=$((VIOLATIONS + 1))
fi

# ========================================
# 패턴 4: 과도한 필드 개수
# ========================================

# private 필드 개수 (static 제외)
FIELD_COUNT=$(grep -c "^\s*private\s\+[^s]\w\+\s\+\w\+\s*;" "$FILE" || true)
FIELD_COUNT=$((FIELD_COUNT - $(grep -c "private\s\+static" "$FILE" || true)))

MAX_FIELDS=7
if [ "$FIELD_COUNT" -gt "$MAX_FIELDS" ]; then
    log_warning "Too many fields: $FIELD_COUNT (max: $MAX_FIELDS)"

    cat << EOF

⚠️  필드가 너무 많음 (응집도 문제 의심)

파일: $FILE
현재: $FIELD_COUNT instance fields
권장: ≤ $MAX_FIELDS fields

이유:
- 많은 필드 = 여러 관심사 혼재 가능성
- LCOM (Lack of Cohesion) 높을 수 있음

확인 사항:
1. 모든 메서드가 모든 필드를 사용하는가?
2. 일부 메서드만 특정 필드를 사용하는가?
3. 필드를 그룹으로 묶을 수 있는가?

수정 방법:
- 관련 필드끼리 묶어 Value Object로 추출

예시:
  // BEFORE
  class User {
      String street;
      String city;
      String zipCode;
      String country;
      // + 다른 필드들
  }

  // AFTER
  class User {
      Address address;  // Value Object
      // + 다른 필드들
  }

  class Address {
      String street;
      String city;
      String zipCode;
      String country;
  }

EOF
fi

# ========================================
# 허용 패턴 확인
# ========================================

# Record는 필드 많아도 OK (Data Transfer Object)
if grep -q "public record" "$FILE"; then
    log_info "Record detected (Data class allowed)"
    exit 0
fi

# Interface는 메서드 많아도 OK
if grep -q "public interface" "$FILE"; then
    log_info "Interface detected (Multiple methods allowed)"
    exit 0
fi

# Exception 클래스는 OK
if [[ "$BASENAME" =~ Exception$ ]]; then
    log_info "Exception class detected (allowed)"
    exit 0
fi

# ========================================
# 결과 반환
# ========================================

if [ $VIOLATIONS -gt 0 ]; then
    log_error "Found $VIOLATIONS SRP violation(s)"
    log_warning "Please refactor to follow Single Responsibility Principle"

    cat << EOF

📚 추천 리팩토링 전략:

1. Extract Class (클래스 추출)
   - 관련된 메서드와 필드를 새 클래스로 이동

2. Extract Method (메서드 추출)
   - 긴 메서드를 작은 메서드로 분리
   - 각 메서드는 한 가지 일만

3. 도메인 개념 명확화
   - 클래스 이름이 하는 일을 정확히 표현
   - 추상적인 이름(Manager, Util) 금지

4. LCOM 측정
   - PMD의 GodClass 규칙 활용
   - 응집도 높이기

참고 문서:
- config/pmd/pmd-ruleset.xml (PMD 규칙)
- SingleResponsibilityTest.java (ArchUnit 테스트)
EOF

    exit 1
fi

log_info "✅ Single Responsibility Principle validation passed"
exit 0
