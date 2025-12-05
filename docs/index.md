---
layout: default
title: Spring Standards Documentation
---

# Spring Standards - Coding Conventions

> **Spring Boot 3.5.x + Java 21 헥사고날 아키텍처 엔터프라이즈 표준**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](https://alistair.cockburn.us/hexagonal-architecture/)
[![TDD](https://img.shields.io/badge/TDD-Kent%20Beck-red.svg)](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)

---

## 📚 코딩 컨벤션 (Coding Conventions)

**총 88개 규칙** - Spring Boot 3.5 + Java 21 기반 헥사고날 아키텍처 표준

### 레이어별 규칙

#### 🏗️ [00. Project Setup](coding_convention/00-project-setup/)
프로젝트 구조 및 버전 관리 (2개 규칙)
- [Multi-Module Structure](coding_convention/00-project-setup/multi-module-structure.md)
- [Version Management](coding_convention/00-project-setup/version-management.md)

#### 🌐 [01. Adapter-In Layer (REST API)](coding_convention/01-adapter-in-layer/rest-api/)
REST API 설계 및 구현 (22개 규칙)
- [Controller Guide](coding_convention/01-adapter-in-layer/rest-api/controller/controller-guide.md)
- [DTO Patterns](coding_convention/01-adapter-in-layer/rest-api/dto/)
- [Error Handling](coding_convention/01-adapter-in-layer/rest-api/error/)
- [REST API Guide](coding_convention/01-adapter-in-layer/rest-api/rest-api-guide.md)

#### 🎯 [02. Domain Layer](coding_convention/02-domain-layer/)
핵심 비즈니스 로직 및 DDD (12개 규칙)
- [Aggregate Guide](coding_convention/02-domain-layer/aggregate/guide.md)
- [Value Object Guide](coding_convention/02-domain-layer/vo/guide.md)
- [Domain Exception](coding_convention/02-domain-layer/exception/)
- [Domain Guide](coding_convention/02-domain-layer/domain-guide.md)

#### 🔧 [03. Application Layer](coding_convention/03-application-layer/)
UseCase 및 비즈니스 조율 (26개 규칙)
- [Facade Pattern](coding_convention/03-application-layer/facade/)
- [Transaction Manager](coding_convention/03-application-layer/manager/)
- [Port Interfaces](coding_convention/03-application-layer/port/)
- [Assembler Pattern](coding_convention/03-application-layer/assembler/)
- [Application Guide](coding_convention/03-application-layer/application-guide.md)

#### 💾 [04. Persistence Layer](coding_convention/04-persistence-layer/)
데이터 영속성 및 저장소 (23개 규칙)
- [MySQL Persistence](coding_convention/04-persistence-layer/mysql/)
  - [JPA Entity](coding_convention/04-persistence-layer/mysql/entity/)
  - [QueryDSL Repository](coding_convention/04-persistence-layer/mysql/repository/)
  - [Command/Query Adapter](coding_convention/04-persistence-layer/mysql/adapter/)
- [Redis Persistence](coding_convention/04-persistence-layer/redis/)

#### 🧪 [05. Testing](coding_convention/05-testing/)
테스트 전략 및 픽스처 (3개 규칙)
- [Integration Testing](coding_convention/05-testing/integration-testing/)
- [Test Fixtures](coding_convention/05-testing/test-fixtures/)

---

## 🚨 Zero-Tolerance Rules

절대 위반 불가 규칙:

1. **Lombok 금지** - Domain, JPA Entity, Orchestration Layer
2. **Law of Demeter** - Getter 체이닝 금지 (`order.getCustomer().getAddress()` ❌)
3. **Long FK 전략** - JPA 관계 어노테이션 금지, `Long userId` 사용
4. **Transaction 경계** - `@Transactional` 내 외부 API 호출 절대 금지
5. **Orchestration Pattern** - `executeInternal()` @Async 필수, Command Record 패턴

---

## 📖 Kent Beck TDD + Tidy First 철학

### TDD 3단계
```
Red (테스트 작성) → Green (최소 구현) → Refactor (구조 개선)
         ↓                ↓                  ↓
    실패하는 테스트     테스트 통과         코드 개선
         ↓                ↓                  ↓
     test: 커밋        feat: 커밋        struct: 커밋
```

### Tidy First 핵심
**구조적 변경(Structural)**과 **동작 변경(Behavioral)**을 절대 섞지 말 것!

---

## 🔗 Links

- **GitHub Repository**: [ryu-qqq/claude-spring-standards](https://github.com/ryu-qqq/claude-spring-standards)
- **Main README**: [README.md](https://github.com/ryu-qqq/claude-spring-standards/blob/main/README.md)
- **Coding Conventions**: [Full Documentation](coding_convention/)

---

## 📝 License

This project documentation is open source and available under the MIT License.

---

**Last Updated**: 2025-12-05
**Version**: 3.0
