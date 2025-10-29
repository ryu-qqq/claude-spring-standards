package com.ryuqq.domain.example;

/**
 * Example 도메인 에러 코드
 *
 * <p>Example 도메인에서 발생하는 모든 예외의 에러 코드를 중앙에서 관리합니다.</p>
 *
 * <p><strong>사용 방법:</strong></p>
 * <pre>{@code
 * // 예외 발생 시
 * throw new DomainException(
 *     ExampleErrorCode.EXAMPLE_NOT_FOUND.getCode(),
 *     ExampleErrorCode.EXAMPLE_NOT_FOUND.getMessage()
 * );
 *
 * // 또는 전용 예외 클래스 사용
 * throw new ExampleNotFoundException(exampleId);
 * }</pre>
 *
 * <p><strong>에러 코드 네이밍 규칙:</strong></p>
 * <ul>
 *   <li>도메인명_상태_동사 형태 (예: EXAMPLE_NOT_FOUND)</li>
 *   <li>모두 대문자, 단어는 언더스코어로 구분</li>
 *   <li>도메인명을 prefix로 사용 (다른 도메인과 충돌 방지)</li>
 * </ul>
 *
 * <p><strong>HTTP 상태 코드 매핑:</strong></p>
 * <ul>
 *   <li>404: 리소스를 찾을 수 없음</li>
 *   <li>400: 잘못된 요청 (검증 실패)</li>
 *   <li>409: 충돌 (비즈니스 규칙 위반)</li>
 *   <li>500: 내부 서버 오류</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
public enum ExampleErrorCode {

    /**
     * Example을 찾을 수 없음 (404 Not Found)
     */
    EXAMPLE_NOT_FOUND("EXAMPLE_NOT_FOUND", 404, "Example을 찾을 수 없습니다"),

    /**
     * Example이 이미 존재함 (409 Conflict)
     */
    EXAMPLE_ALREADY_EXISTS("EXAMPLE_ALREADY_EXISTS", 409, "이미 존재하는 Example입니다"),

    /**
     * Example의 상태가 유효하지 않음 (400 Bad Request)
     */
    EXAMPLE_INVALID_STATUS("EXAMPLE_INVALID_STATUS", 400, "유효하지 않은 Example 상태입니다"),

    /**
     * Example 메시지가 비어있음 (400 Bad Request)
     */
    EXAMPLE_EMPTY_MESSAGE("EXAMPLE_EMPTY_MESSAGE", 400, "Example 메시지는 비어있을 수 없습니다"),

    /**
     * Example 메시지가 너무 긺 (400 Bad Request)
     */
    EXAMPLE_MESSAGE_TOO_LONG("EXAMPLE_MESSAGE_TOO_LONG", 400, "Example 메시지가 최대 길이를 초과했습니다"),

    /**
     * Example 삭제 불가 (409 Conflict)
     */
    EXAMPLE_CANNOT_DELETE("EXAMPLE_CANNOT_DELETE", 409, "삭제할 수 없는 Example입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    /**
     * ExampleErrorCode 생성자
     *
     * @param code 에러 코드 (예: "EXAMPLE_NOT_FOUND")
     * @param httpStatus HTTP 상태 코드 (예: 404, 400, 409, 500)
     * @param message 기본 에러 메시지
     */
    ExampleErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /**
     * 에러 코드 반환
     *
     * @return 에러 코드 문자열
     */
    public String getCode() {
        return code;
    }

    /**
     * HTTP 상태 코드 반환
     *
     * <p>GlobalExceptionHandler에서 HTTP 응답 상태 코드를 결정할 때 사용됩니다.</p>
     *
     * @return HTTP 상태 코드 (404, 400, 409, 500 등)
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * 기본 에러 메시지 반환
     *
     * @return 에러 메시지
     */
    public String getMessage() {
        return message;
    }
}
