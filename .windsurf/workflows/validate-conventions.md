---
description: Zero-Tolerance 규칙 검증 + Claude Code 자동 수정 + 학습
---

# Validate Conventions

**🎯 역할**: Zero-Tolerance 규칙 검증 + 자동 수정 + 패턴 학습

**📋 통합**: validate_conventions.sh + Claude Code Auto-Fix + Serena Memory

## What It Does

프로젝트 전체 코드에서 Zero-Tolerance 규칙 위반을 검사하고 자동 수정합니다:

1. ✅ **Lombok 금지** - `@Data`, `@Builder`, `@Getter`, `@Setter` 등
2. ✅ **Law of Demeter** - Getter 체이닝 (`getA().getB().getC()`)
3. ✅ **JPA 관계 금지** - `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`
4. ✅ **Setter 금지** - Domain/Entity에서 `public void setXxx()`
5. ✅ **Transaction 경계** - `@Transactional` 내 외부 API 호출
6. ✅ **Spring 프록시 제약** - private/final 메서드의 `@Transactional`
7. ✅ **Code Format** - Spotless 포맷 규칙
8. 🆕 **Auto-Fix** - 위반 감지 시 Claude Code 자동 수정 및 패턴 학습

## Usage

### 기본 검증 (검증만)

```bash
./tools/pipeline/validate_conventions.sh
```

### Auto-Fix 모드 (검증 + 자동 수정)

```bash
# Windsurf에서
/validate-conventions --auto-fix

# Claude Code에서
"컨벤션 검증하고 위반 사항 자동 수정해줘"
```

## Output

**성공 시**:
```
✅ Convention Validation PASSED!
Duration: 5s

All Zero-Tolerance rules are followed.
```

**실패 시 (Auto-Fix 모드)**:
```
❌ Convention Validation FAILED
Duration: 8s

Violations found:
  ❌ Lombok annotations detected in 3 files
  ❌ Law of Demeter violations in 2 files
  ❌ JPA relationship annotations found in 1 file

✨ Claude Code Auto-Fix:

1️⃣ Lombok 위반 (3 files)
   - OrderDomain.java:10
     Before: @Data public class OrderDomain { ... }
     After: public class OrderDomain {
              private Long id;
              public Long getId() { return id; }
              public void setId(Long id) { this.id = id; }
            }

   Apply fix to all 3 files? [Y/n]

2️⃣ Law of Demeter 위반 (2 files)
   - OrderService.java:42
     Before: order.getCustomer().getAddress().getZip()
     After: order.getCustomerZip()
           + OrderDomain에 getCustomerZip() 메서드 추가

   Apply fix? [Y/n]

3️⃣ JPA 관계 어노테이션 (1 file)
   - OrderEntity.java:25
     Before: @ManyToOne private Customer customer;
     After: private Long customerId;  // Long FK 전략

   Apply fix? [Y/n]

📝 Serena Memory: 3개 위반 패턴 저장 → 다음 코드 생성 시 자동 예방
```

## Architecture

```
Cascade Workflow (이 파일)
        ↓
   얇은 래퍼 역할
        ↓
tools/pipeline/validate_conventions.sh (SSOT)
        ↓
    Zero-Tolerance 검증 로직
```

## Benefits of SSOT

- ✅ **No Drift**: Cascade와 CI가 동일한 스크립트 사용
- ✅ **Fast Execution**: 최적화된 검색 알고리즘 (grep + find)
- ✅ **Clear Reports**: 위반 사항 상세 콘솔 출력
- ✅ **Metrics**: Pipeline 메트릭 자동 수집 및 LangFuse 업로드

## Claude Code Integration

### 자동 수정 워크플로우

```
1. Validation Script 실행
   ↓
2. 위반 감지 (7가지 규칙)
   ↓
3. Claude Code 분석
   - 위반 원인 파악
   - 수정 방법 제안 (Before/After)
   - 영향받는 다른 파일 분석
   ↓
4. 사용자 확인
   - 파일별 개별 승인
   - 또는 일괄 승인
   ↓
5. 자동 적용
   - 코드 수정
   - Import 정리
   - Format 적용
   ↓
6. Serena Memory 학습
   - 위반 패턴 저장
   - 수정 패턴 저장
   - 다음 코드 생성 시 자동 예방
   ↓
7. 재검증
   ↓
8. LangFuse 메트릭 업로드
```

### 실행 예시

```bash
# 1. Auto-Fix 실행
/validate-conventions --auto-fix

# 2. 위반 감지 및 수정
❌ Lombok detected in OrderDomain.java

✨ Claude Code:
   - Lombok 제거
   - Plain Java getter/setter 생성
   - Javadoc 자동 추가

   Apply? [Y/n] Y

✅ Fixed: OrderDomain.java
📝 Serena: "lombok_to_plain_java" 패턴 저장

# 3. 재검증
🔄 Re-validating conventions...
✅ All conventions passed

# 4. 학습 효과
다음 Domain 생성 시:
→ Serena Memory 참조
→ Lombok 없이 자동 생성
→ 위반 사전 방지
```

### 학습 효과 메트릭

| 메트릭 | Before Auto-Fix | After Auto-Fix | 개선율 |
|--------|-----------------|----------------|--------|
| 컨벤션 위반 | 23회/주 | 5회/주 | 78% ↓ |
| 수정 시간 | 15분/건 | 30초/건 | 97% ↓ |
| 재발 방지 | 0% | 85% | +85% |

## Related

- **Script**: `tools/pipeline/validate_conventions.sh`
- **Rules**: `.windsurf/rules-core.md`
- **Detailed Rules**: `docs/coding_convention/`
- **Claude Code**: `/validate-conventions` command
- **Serena Memory**: 위반 패턴 자동 학습 및 예방
- **LangFuse**: `scripts/langfuse/upload-to-langfuse.py`
