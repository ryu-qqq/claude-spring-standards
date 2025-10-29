package com.ryuqq.domain.example.exception;

import java.util.Map;

import com.ryuqq.domain.common.DomainException;

/**
 * Example 도메인 예외 추상 클래스 (Sealed)
 *
 * <p>Example 도메인에서 발생하는 모든 예외의 부모 클래스입니다.</p>
 * <p>Java 21의 sealed class를 사용하여 허용된 하위 예외만 정의할 수 있습니다.</p>
 *
 * <p><strong>Sealed Class의 장점:</strong></p>
 * <ul>
 *   <li>컴파일 타임에 모든 하위 예외 타입을 알 수 있음</li>
 *   <li>IDE의 자동완성과 타입 체크 지원</li>
 *   <li>Switch Expression의 완전성 검증 가능</li>
 *   <li>예기치 않은 하위 예외 생성 방지</li>
 * </ul>
 *
 * <p><strong>허용된 하위 예외:</strong></p>
 * <ul>
 *   <li>{@link ExampleNotFoundException} - Example을 찾을 수 없음</li>
 *   <li>{@link ExampleAlreadyExistsException} - Example이 이미 존재함</li>
 *   <li>{@link ExampleInvalidStatusException} - Example 상태가 유효하지 않음</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
public sealed abstract class ExampleException extends DomainException
    permits ExampleNotFoundException, ExampleAlreadyExistsException, ExampleInvalidStatusException {

    /**
     * ExampleException 생성자
     *
     * @param code 에러 코드
     * @param message 에러 메시지
     */
    protected ExampleException(String code, String message) {
        super(code, message);
    }

    /**
     * ExampleException 생성자 (args 포함)
     *
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param args 메시지 템플릿 파라미터
     */
    protected ExampleException(String code, String message, Map<String, Object> args) {
        super(code, message, args);
    }
}
