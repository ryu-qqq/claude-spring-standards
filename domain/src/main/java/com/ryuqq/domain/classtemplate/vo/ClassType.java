package com.ryuqq.domain.classtemplate.vo;

/**
 * ClassType - 클래스 템플릿 유형
 *
 * <p>각 레이어에서 사용되는 클래스 유형을 정의합니다.
 *
 * @author ryu-qqq
 */
public enum ClassType {

    // Domain Layer
    AGGREGATE("Aggregate Root 클래스"),
    AGGREGATE_ROOT("Aggregate Root 클래스 (별칭)"),
    VALUE_OBJECT("Value Object 클래스"),
    DOMAIN_EVENT("도메인 이벤트"),
    DOMAIN_EVENT_INTERFACE("도메인 이벤트 인터페이스"),
    DOMAIN_EXCEPTION("도메인 예외"),
    DOMAIN_CRITERIA("도메인 검색 조건"),
    ERROR_CODE("에러 코드"),
    ERROR_CODE_INTERFACE("에러 코드 인터페이스"),

    // Application Layer
    USE_CASE("UseCase 인터페이스"),
    COMMAND_SERVICE("Command Service 구현체"),
    QUERY_SERVICE("Query Service 구현체"),
    PORT_IN("Port-In 인터페이스"),
    PORT_OUT("Port-Out 인터페이스"),
    FACADE("Facade 컴포넌트"),
    MANAGER("Manager 컴포넌트"),
    FACTORY("Factory 컴포넌트"),
    ASSEMBLER("Assembler 컴포넌트"),
    EVENT_LISTENER("이벤트 리스너"),
    SCHEDULER("스케줄러"),

    // Persistence Layer
    ENTITY("JPA Entity"),
    JPA_REPOSITORY("JPA Repository 인터페이스"),
    QUERYDSL_REPOSITORY("QueryDSL Repository"),
    COMMAND_ADAPTER("Command Adapter"),
    QUERY_ADAPTER("Query Adapter"),
    ENTITY_MAPPER("Entity Mapper"),

    // REST API Layer
    REST_CONTROLLER("REST Controller"),
    COMMAND_CONTROLLER("Command Controller"),
    QUERY_CONTROLLER("Query Controller"),
    REQUEST_DTO("Request DTO"),
    RESPONSE_DTO("Response DTO"),
    API_MAPPER("API Mapper"),
    ERROR_HANDLER("에러 핸들러"),

    // Common
    CONFIG("설정 클래스"),
    COMMON_VO("공통 Value Object"),

    // Common VO Templates
    AUTO_INCREMENT_ID("자동 증가 ID"),
    GENERATED_LONG_ID("생성된 Long ID"),
    GENERATED_STRING_ID("생성된 String ID"),
    UPDATE_DATA("업데이트 데이터"),
    DELETION_STATUS_RECORD("삭제 상태 레코드"),
    CACHE_KEY_INTERFACE("캐시 키 인터페이스"),
    LOCK_KEY_INTERFACE("락 키 인터페이스"),
    SORT_KEY_INTERFACE("정렬 키 인터페이스"),
    SORT_DIRECTION_ENUM("정렬 방향 Enum"),

    // Pagination & Query Templates
    PAGE_REQUEST_RECORD("페이지 요청 레코드"),
    PAGE_META_RECORD("페이지 메타 레코드"),
    PAGE_CRITERIA_RECORD("페이지 검색 조건 레코드"),
    SLICE_META_RECORD("슬라이스 메타 레코드"),
    CURSOR_PAGE_REQUEST_RECORD("커서 페이지 요청 레코드"),
    CURSOR_CRITERIA_RECORD("커서 검색 조건 레코드"),
    CURSOR_QUERY_CONTEXT_RECORD("커서 쿼리 컨텍스트 레코드"),
    QUERY_CONTEXT_RECORD("쿼리 컨텍스트 레코드"),
    DATE_RANGE_RECORD("날짜 범위 레코드");

    private final String description;

    ClassType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
