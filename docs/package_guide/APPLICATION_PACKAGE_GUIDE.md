# APPLICATION 패키지 가이드

> 유즈케이스 구현, 트랜잭션, 포트 호출, 도메인 조립(Assembler). **프레임워크 의존 최소화**.

## 디렉터리 구조
```
application/[context]/
├─ dto/           # Command/Response (전송 독립, @Valid 금지)
├─ port/
│  ├─ in/         # UseCase 인터페이스 (비즈니스 API)
│  └─ out/        # 외부 의존 포트 (Repository, 외부 API 등)
├─ assembler/     # Command → Domain 변환기
└─ service/
   ├─ command/    # 쓰기 유즈케이스 구현 (@Transactional)
   └─ query/      # 읽기 유즈케이스 구현 (@Transactional(readOnly=true))
```

## 포함할 객체 & 역할
- **UseCase(in)**: 애플리케이션 경계(비즈니스 기능)
- **Service(Command/Query)**: 유즈케이스 구현, 트랜잭션, 포트 호출
- **Port(out)**: 외부 의존 추상화(영속성/메시징/외부 API)
- **Assembler**: Command → Domain 조립(VO 변환/정규화)
- **DTO**: 애플리케이션 입출력 모델(어댑터 DTO와 구분)

## 허용/금지 의존
- **허용**: `domain..`, `application..port.out..`
- **금지**: `adapter..` 의존, 프레임워크 강한 결합(가능한 최소화)

## 네이밍 규약
- UseCase: `CreateXxxUseCase`, `GetXxxUseCase` 등
- Service: `XxxCommandService`, `XxxQueryService` 등
- Port: `XxxCommandPort`, `XxxQueryPort`, `ExternalApiPort` 등
- Assembler: `CreateXxxAssembler`, `XxxDtoAssembler` 등

## Do / Don't
**Do**
- 트랜잭션 경계 설정 (`@Transactional`)
- 외부 I/O는 트랜잭션 **밖**에서 실행
- 조회 필요 시 다른 서비스 직접 호출 대신 **QueryPort** 사용
- 스프링 프록시 한계에 대해 생각할것 

**Don't**
- 다른 Application 서비스 직접 호출(순환 의존 위험)
- REST/JPA 어노테이션 사용 (어댑터 레이어로 이동)
- 도메인 규칙을 여기서 구현(도메인으로 이동)
- 셀프 인보케이션
- 트랜잭션 어노테이션이 붙은 메서드에 내부 호출 및 프라이빗 메서드 호출

## ArchUnit 룰 스니펫
```java
noClasses().that().resideInAPackage("..application..")
  .should().dependOnClassesThat().resideInAnyPackage("..adapter..");
classes().that().resideInAPackage("..application..port.in..")
  .should().haveSimpleNameEndingWith("UseCase");
```
