# /validate-architecture - 아키텍처 규칙 검증

프로젝트 전체의 아키텍처 규칙 준수 여부를 검증합니다.

## 사용법

```bash
/validate-architecture [directory]
```

## 예제

```bash
# 전체 프로젝트 검증
/validate-architecture

# 특정 모듈만 검증
/validate-architecture domain
/validate-architecture application
```

## 작업 수행

당신은 **아키텍처 검증 전문가**입니다. 다음 단계를 수행하세요:

1. **검증 범위 결정**
   - 인자가 없으면 전체 프로젝트 검증
   - 디렉토리가 지정되면 해당 모듈만 검증

2. **레이어별 파일 수집**
   ```bash
   # Domain 레이어
   find domain/src/main/java -name "*.java"

   # Application 레이어
   find application/src/main/java -name "*.java"

   # Adapter-REST 레이어
   find adapter/in/web/src/main/java -name "*.java"
   ```

3. **각 파일 검증**
   - 파일 경로로 레이어 자동 감지
   - validation-helper.py 실행
   - 검증 결과 수집

4. **통계 및 요약**
   - 총 검증 파일 수
   - 통과 파일 수
   - 실패 파일 수
   - 레이어별 통계

5. **실패 파일 상세 정보**
   - 각 실패 파일의 위반 규칙
   - 수정 방법 제안

## 검증 규칙 (레이어별)

### Domain 레이어
- ❌ Spring/JPA 의존 금지
- ❌ Getter 체이닝 금지
- ✅ Pure Java only

### Application 레이어
- ❌ @Transactional 내 외부 API 호출 금지
- ✅ UseCase 패턴 준수
- ✅ Command/Query 분리

### Adapter-REST 레이어
- ❌ 비즈니스 로직 포함 금지
- ✅ @Valid 검증 필수
- ✅ Mapper 패턴 준수

## 출력 예시

### 성공 케이스

```
---

## 🏗️ 아키텍처 검증 결과

### ✅ 전체 통계

- **검증 파일 수**: 45
- **통과**: 45 (100%)
- **실패**: 0 (0%)

### 📊 레이어별 통계

| 레이어 | 파일 수 | 통과 | 실패 |
|--------|---------|------|------|
| Domain | 12 | 12 | 0 |
| Application | 18 | 18 | 0 |
| Adapter-REST | 15 | 15 | 0 |

✅ **모든 파일이 아키텍처 규칙을 준수합니다!**

---
```

### 실패 케이스

```
---

## 🏗️ 아키텍처 검증 결과

### ⚠️ 전체 통계

- **검증 파일 수**: 45
- **통과**: 42 (93%)
- **실패**: 3 (7%)

### 📊 레이어별 통계

| 레이어 | 파일 수 | 통과 | 실패 |
|--------|---------|------|------|
| Domain | 12 | 10 | 2 |
| Application | 18 | 18 | 0 |
| Adapter-REST | 15 | 14 | 1 |

### ❌ 실패 파일 상세

#### 1. domain/.../Customer.java

**규칙 위반**: Spring 의존 감지
**문제**: @Entity annotation detected
**해결 방법**:
- @Entity 제거
- JPA Entity는 adapter/out/persistence에 위치

#### 2. domain/.../Order.java

**규칙 위반**: Law of Demeter 위반
**문제**: order.getCustomer().getAddress() detected
**해결 방법**:
- Getter 체이닝 제거
- Tell, Don't Ask 패턴 적용

#### 3. adapter/.../OrderController.java

**규칙 위반**: 비즈니스 로직 포함
**문제**: Controller 내 가격 계산 로직
**해결 방법**:
- 비즈니스 로직을 Domain으로 이동
- Controller는 UseCase 호출만

---

💡 **위 문제를 수정한 후 다시 검증하세요.**

---
```

## 성능

- 검증 속도: ~50ms per file
- 전체 프로젝트 (100 files): ~5초
- Cache 기반 검증으로 빠른 실행

## 참고

- 모든 레이어의 규칙을 통합 검증
- CI/CD 파이프라인에 통합 가능
- Pre-commit hook으로 자동 실행 가능

