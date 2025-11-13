# 📐 Checkstyle 설정 가이드

## 개요

이 프로젝트의 Checkstyle은 **DDD/헥사고날 아키텍처에 최적화된 경량 린트 설정**입니다.

**설계 철학:**
- ✅ **핵심 규칙만 강제**: Lombok 금지, Law of Demeter 휴리스틱
- ✅ **PMD/ArchUnit과 역할 분담**: 중복 검증 제거
- ✅ **전략적 예외 처리**: Adapter/Mapper 레이어에서 체이닝 허용

## 설정 파일

```
config/checkstyle/
├── checkstyle.xml              # 메인 룰셋
└── checkstyle-suppressions.xml # 예외 정의 (Adapter/Mapper)
```

- **적용 범위**: 모든 서브프로젝트의 Java 소스 코드
- **실행 시점**: 빌드 시 자동 실행 (`./gradlew build`)
- **실패 정책**: `isIgnoreFailures = false`, `maxWarnings = 0` (Zero-tolerance)

---

## 주요 검증 규칙

### 1. 🚫 Lombok 전면 금지 (CRITICAL)

```java
// ❌ STRICTLY PROHIBITED
import lombok.Data;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
```

**검증 방식:**
```xml
<module name="IllegalImport">
    <property name="illegalPkgs" value="lombok"/>
</module>
```

**위반 시:**
- 빌드 즉시 실패
- 모든 Lombok 관련 import 차단

**이유:**
- Domain Layer 순수성 유지
- 숨겨진 동작 제거 (디버깅 용이)
- IDE 독립적 코드베이스 유지

**대안:**
- Plain Java (Entity, Value Object)
- Java Record (DTO, Request/Response)

---

### 2. 🔗 Law of Demeter 휴리스틱 검출

```java
// ❌ WARNING - 메서드 체이닝 의심
order.getCustomer().getAddress().getZipCode()

// ✅ RECOMMENDED - 위임 메서드
order.getCustomerZipCode()
```

**검증 방식:**
```xml
<module name="RegexpSinglelineJava">
    <property name="format" value="\)\.[A-Za-z_][A-Za-z0-9_]*\(\)\."/>
    <property name="message" value="메서드 체이닝 과다 의심 (검토 필요)"/>
</module>
```

**특징:**
- **심각도**: `warning` (경고, 빌드는 통과)
- **보완**: PMD의 `LawOfDemeter` 룰과 함께 사용
- **목적**: `.method1().method2()` 패턴 감지

**예외 처리 (Suppression):**
```xml
<!-- adapter-in-*, adapter-out-*, *Mapper.java는 체이닝 허용 -->
<suppress files=".*/adapter-in-.*" checks="RegexpSinglelineJava"/>
<suppress files=".*/adapter-out-.*" checks="RegexpSinglelineJava"/>
<suppress files=".*Mapper\.java" checks="RegexpSinglelineJava"/>
```

**근거:**
- Adapter/Mapper는 "변환 계층"으로 DTO 조합 시 체이닝 불가피
- Domain Layer의 순수성만 보호하면 충분

---

### 3. 📝 코드 스타일 기본 규칙

#### 화이트스페이스 & 포맷팅
- ✅ **Tab 문자 금지** (`FileTabCharacter`)
- ✅ **파일 끝 줄바꿈 필수** (`NewlineAtEndOfFile`)
- ✅ **라인 길이 제한**: 140자
  - 예외: `package`, `import`, URL 링크

#### Import 관리
- ✅ **Star Import 금지** (`AvoidStarImport`)
  ```java
  // ❌
  import java.util.*;

  // ✅
  import java.util.List;
  import java.util.Map;
  ```
- ✅ **미사용 Import 제거** (`UnusedImports`)
- ✅ **중복 Import 제거** (`RedundantImport`)

#### 코드 구조
- ✅ **중괄호 필수** (`NeedBraces`)
  ```java
  // ❌
  if (condition)
      doSomething();

  // ✅
  if (condition) {
      doSomething();
  }
  ```
- ✅ **중괄호 위치** (`LeftCurly`, `RightCurly`)
- ✅ **공백 규칙** (`WhitespaceAfter`, `WhitespaceAround`)

---

## 비활성화된 규칙 (의도적)

### Public Setter 검증 (주석 처리됨)

```xml
<!--
<module name="Regexp">
  <property name="format" value="public\s+void\s+set[A-Z]"/>
  <property name="message" value="도메인 public setter 지양"/>
</module>
-->
```

**비활성화 이유:**
- **ArchUnit이 더 정확**: Entity/Value Object 타입별 검증 가능
- **Checkstyle 한계**: 정규식으로는 컨텍스트 판단 불가
- **중복 방지**: ArchUnit + PMD로 충분히 커버됨

**대안:**
```java
// ArchUnit 테스트에서 검증
@ArchTest
static final ArchRule no_setters_in_entities =
    noClasses().that().haveSimpleNameEndingWith("Entity")
        .should().haveModifier(JavaModifier.PUBLIC)
        .andShould().haveNameMatching("set.*");
```

---

## 도구 역할 분담

### Checkstyle (현재 설정)
- ✅ **Lombok 금지** (컴파일 타임 차단)
- ✅ **메서드 체이닝 휴리스틱** (의심 케이스 경고)
- ✅ **기본 코드 스타일** (화이트스페이스, Import)

### PMD (`config/pmd/pmd-ruleset.xml`)
- ✅ **Law of Demeter 정밀 검증** (XPath 기반)
- ✅ **복잡도 분석** (GodClass, ExcessiveImports, CyclomaticComplexity)

### ArchUnit (테스트 코드)
- ✅ **아키텍처 레이어 검증** (의존성 방향)
- ✅ **네이밍 규칙** (Port, Adapter, UseCase)
- ✅ **Entity Setter 금지** (타입별 정밀 검증)

### Spotless (`build.gradle.kts`)
- ✅ **자동 포맷팅** (Google Java Format AOSP)
- ✅ **Import 정렬** (자동 수정)

---

## 실행 방법

### 전체 검사
```bash
./gradlew checkstyleMain
./gradlew checkstyleTest
```

### 특정 모듈만 검사
```bash
./gradlew :domain:checkstyleMain
./gradlew :application:checkstyleMain
./gradlew :adapter-in-web:checkstyleMain
```

### 빌드 시 자동 실행
```bash
./gradlew build
# Checkstyle이 자동으로 실행되며, 위반 시 빌드 실패
```

### 전체 코드 품질 검증
```bash
./gradlew check
# Checkstyle + PMD + SpotBugs + ArchUnit + JaCoCo 모두 실행
```

---

## 리포트 확인

**HTML 리포트 위치:**
```
domain/build/reports/checkstyle/main.html
application/build/reports/checkstyle/main.html
adapter-in-web/build/reports/checkstyle/main.html
```

각 모듈의 `build/reports/checkstyle/` 디렉토리에서 상세한 위반 내역을 확인할 수 있습니다.

---

## 설정 커스터마이징

### Checkstyle 버전 변경
`gradle/libs.versions.toml`:
```toml
[versions]
checkstyle = "10.12.5"  # 원하는 버전으로 변경
```

### 규칙 추가/제외
`config/checkstyle/checkstyle.xml`:
```xml
<!-- 규칙 추가 예시: Magic Number 검출 -->
<module name="TreeWalker">
    <module name="MagicNumber">
        <property name="ignoreHashCodeMethod" value="true"/>
        <property name="ignoreAnnotation" value="true"/>
    </module>
</module>
```

### Suppression 예외 추가
`config/checkstyle/checkstyle-suppressions.xml`:
```xml
<!-- 테스트 코드에서 체이닝 허용 예시 -->
<suppress files=".*Test\.java" checks="RegexpSinglelineJava"/>
```

### 경고를 오류로 처리 비활성화 (⚠️ 권장하지 않음)
`build.gradle.kts`:
```kotlin
checkstyle {
    isIgnoreFailures = true  // ⚠️ 품질 저하 주의
    maxWarnings = 10         // ⚠️ Zero-tolerance 해제
}
```

---

## 문제 해결

### 자주 발생하는 위반

#### 1. Lombok Import 감지
```bash
Error: Illegal import - lombok
```

**해결:**
```java
// ❌ 제거
import lombok.Data;

// ✅ Plain Java 또는 Record 사용
public record UserResponse(Long id, String name) {}
```

#### 2. 메서드 체이닝 경고
```bash
Warning: 메서드 체이닝 과다 의심 (검토 필요)
```

**해결 (Domain Layer):**
```java
// ❌ 체이닝
String zipCode = order.getCustomer().getAddress().getZipCode();

// ✅ 위임 메서드
String zipCode = order.getCustomerZipCode();
```

**해결 (Adapter/Mapper):**
```java
// ✅ Adapter/Mapper에서는 허용됨 (Suppression)
return OrderResponse.builder()
    .id(order.getId())
    .customerName(order.getCustomer().getName())
    .build();
```

#### 3. Star Import
```bash
Error: Using the '.*' form of import should be avoided
```

**해결:**
- IntelliJ: Settings → Editor → Code Style → Java → Imports
  - "Use single class import" 선택
  - Ctrl+Alt+O (Import 자동 정렬)

#### 4. Tab 문자 사용
```bash
Error: File contains tab characters (this is the first instance)
```

**해결:**
- IntelliJ: Settings → Editor → Code Style → Java → Tabs and Indents
  - "Use tab character" 해제
  - "Tab size: 4", "Indent: 4" 설정

#### 5. 파일 끝 줄바꿈 누락
```bash
Error: File does not end with a newline
```

**해결:**
- IntelliJ: Settings → Editor → General
  - "Ensure every saved file ends with a line break" 체크

---

## @SuppressWarnings 사용

특정 코드 블록에서 Checkstyle 규칙을 억제할 수 있습니다:

```java
@SuppressWarnings("checkstyle:RegexpSinglelineJava")
public String extractNestedValue() {
    // 특별한 경우에만 체이닝 허용
    return obj.getA().getB().getC();
}
```

**주의:**
- 남용하지 말 것
- 반드시 주석으로 이유 설명

---

## 통합 도구

### IntelliJ IDEA

#### Checkstyle 플러그인 설정
1. Settings → Plugins → "Checkstyle-IDEA" 설치
2. Settings → Tools → Checkstyle
3. Configuration File 추가:
   - `+` 버튼 → "Use a local Checkstyle file"
   - `config/checkstyle/checkstyle.xml` 선택
   - "Active" 체크

#### 실시간 검증
- File → Settings → Tools → Checkstyle
- "Scan Scope: All sources (including tests)" 선택
- 하단 Checkstyle 탭에서 실시간 위반 확인

### VS Code

#### Checkstyle Extension
1. Extension: "Checkstyle for Java" 설치
2. Settings (JSON):
```json
{
  "java.checkstyle.configuration": "${workspaceFolder}/config/checkstyle/checkstyle.xml",
  "java.checkstyle.version": "10.12.5"
}
```

---

## 참고 자료

### 프로젝트 문서
- [CODING_STANDARDS.md](../../docs/CODING_STANDARDS.md) - 전체 코딩 표준
- [CODING_STANDARDS_SUMMARY.md](../../docs/CODING_STANDARDS_SUMMARY.md) - 핵심 원칙 요약
- [Law of Demeter 가이드](../../docs/coding_convention/02-domain-layer/legacy/law-of-demeter/)

### 외부 자료
- [Checkstyle 공식 문서](https://checkstyle.org/)
- [Checkstyle 모듈 레퍼런스](https://checkstyle.org/checks.html)

---

## 버전 히스토리

| 버전 | 날짜 | 변경 내역 |
|------|------|-----------|
| 1.0 | 2025-01-XX | 초기 설정 (경량 DDD/헥사고날 최적화) |
| | | - Lombok 금지 |
| | | - Law of Demeter 휴리스틱 |
| | | - 기본 코드 스타일 규칙 |
| | | - Adapter/Mapper Suppression |

---

**✅ 이 설정은 DDD/헥사고날 아키텍처 프로젝트에 최적화되어 있으며, PMD/ArchUnit과 함께 사용하도록 설계되었습니다.**
