# 패키지 구조 가이드 (개요)

본 문서는 프로젝트 전반의 **패키지 구조 규약**을 요약합니다. 각 레이어(어댑터, 애플리케이션, 도메인)의 상세 가이드는 하위 문서를 참조하세요.

## 레이어 개요
- **adapter**: 시스템의 입·출력(REST, 메시징, 영속화, 외부 API)을 담당하는 어댑터 구현.
- **application**: UseCase(비즈니스 흐름) 오케스트레이션, 트랜잭션, 포트 호출.
- **domain**: 순수 도메인 모델(애그리게이트/VO/도메인 서비스/정책/이벤트/예외). 프레임워크 의존 금지.

## 최상위 패키지 구조 (템플릿)
```
com.company.project
├─ adapter
│  ├─ in
│  │  └─ rest
│  │     ├─ controller/
│  │     ├─ dto/
│  │     │  ├─ request/
│  │     │  └─ response/
│  │     └─ mapper/
│  └─ out
│     ├─ persistence/
│     ├─ messaging/
│     └─ external/
│
├─ application
│  ├─ [context]/
│  │  ├─ dto/
│  │  ├─ port/
│  │  │  ├─ in/          # UseCase 인터페이스
│  │  │  └─ out/         # 외부 의존 (DB, API, Cache 등)
│  │  ├─ assembler/      # Command → Domain 변환기
│  │  └─ service/
│  │     ├─ command/     # 쓰기 UseCase 구현 (@Transactional)
│  │     └─ query/       # 읽기 UseCase 구현 (@Transactional(readOnly=true))
│
└─ domain
   ├─ shared/
   │  ├─ id/
   │  ├─ vo/
   │  ├─ event/
   │  ├─ exception/
   │  └─ policy/
   └─ [boundedContext]/
      ├─ [aggregateName]/
      │  ├─ [AggregateRoot].java
      │  ├─ vo/
      │  ├─ event/
      │  ├─ exception/
      │  ├─ policy/
      │  ├─ service/
      │  ├─ repository/   # 인터페이스(Port-Out)
      │  └─ factory/
```

## 문서 구성
- `DOMAIN_PACKAGE_GUIDE.md` — 도메인 레이어 전용 규약
- `APPLICATION_PACKAGE_GUIDE.md` — 애플리케이션 레이어 전용 규약
- `ADAPTER_PACKAGE_GUIDE.md` — 어댑터 레이어 전용 규약

각 문서는 **담는 객체**, **역할/경계**, **허용/금지 의존**, **네이밍 규약**, **하지 말아야 할 일(Do/Don't)**, **ArchUnit 룰 스니펫**을 포함합니다.
