# ADAPTER 패키지 가이드

> 시스템 경계(입·출력)를 담당하는 구현 레이어. 프레임워크 의존 허용.

## 디렉터리 구조
```
adapter/
├─ in/
│  └─ rest/
│     ├─ controller/
│     ├─ dto/
│     │  ├─ request/
│     │  └─ response/
│     └─ mapper/     # REST DTO ↔ Application DTO(Command/Response)
└─ out/
   ├─ persistence/   # JPA/Redis 등 인프라 구현체
   ├─ messaging/     # Kafka/RabbitMQ 등
   └─ external/      # 외부 HTTP/S3 등 API 클라이언트
```

## 포함할 객체 & 역할
- **Controller/Consumer/Scheduler**: 진입점
- **Dto/Mapper**: REST DTO ↔ Application DTO 매핑
- **Persistence Adapter**: RepositoryPort 구현(JPA 등)
- **External Adapter**: 외부 API 클라이언트 구현
- **Configuration**: 스프링/프레임워크 설정

## 허용/금지 의존
- **허용**: `application..` 의존(UseCase 호출), 프레임워크 모듈
- **금지**: `domain..`에 프레임워크 어노테이션 전파, 어댑터에서 도메인 규칙 구현

## 네이밍 규약
- Controller: `XxxController`
- Mapper: `XxxDtoMapper`
- Persistence Adapter: `XxxRepositoryAdapter`
- External Adapter: `XxxExternalAdapter` or `XxxClient`

## Do / Don't
**Do**
- 입력 검증(@Valid), 예외 → 표준 ErrorResponse로 변환
- DTO 기본값/포맷 변환은 여기서
- UseCase만 호출(도메인 직접 조작 금지)

**Don't**
- 도메인 규칙 구현
- Application 서비스 우회(Port 무시)하여 인프라 직접 접근
- DTO를 도메인에 직접 전달

## ArchUnit 룰 스니펫
```java
classes().that().resideInAPackage("..adapter..in..rest..controller..")
  .and().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
  .should().haveSimpleNameEndingWith("Controller");
noClasses().that().resideInAPackage("..adapter..")
  .should().beAccessedByAnyClassThat().resideInAPackage("..domain..");
```
