package com.ryuqq.application.example.port.out;

import com.ryuqq.domain.example.ExampleDomain;

/**
 * ExampleCommandOutPort - Example Command 작업 OutPort
 *
 * <p>CQRS 패턴의 Command 작업을 Persistence Layer로 전달하는 Outbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Example 저장 (생성/수정)</li>
 *   <li>Example 삭제</li>
 *   <li>데이터 변경 작업 담당</li>
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong></p>
 * <ul>
 *   <li>Application Layer (Core) → Persistence Layer (Adapter) 의존성 역전</li>
 *   <li>Interface는 Application Layer에 위치</li>
 *   <li>Implementation은 Persistence Layer에 위치</li>
 * </ul>
 *
 * <p><strong>CQRS 원칙:</strong></p>
 * <ul>
 *   <li>Command와 Query 책임 분리</li>
 *   <li>Command는 데이터 변경만 담당</li>
 *   <li>조회 작업은 ExampleQueryOutPort 사용</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 * @see ExampleQueryOutPort
 */
public interface ExampleCommandOutPort {

    /**
     * Example 저장 (생성, 수정, 삭제 모두 처리)
     *
     * <p><strong>처리 시나리오:</strong></p>
     * <ul>
     *   <li><strong>생성:</strong> ID가 null인 Domain → 신규 INSERT</li>
     *   <li><strong>수정:</strong> ID가 있고 status가 ACTIVE/INACTIVE → UPDATE</li>
     *   <li><strong>삭제:</strong> ID가 있고 status가 DELETED → UPDATE (논리적 삭제)</li>
     * </ul>
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>Domain 객체 검증</li>
     *   <li>Entity 변환 및 저장</li>
     *   <li>저장된 Entity를 Domain으로 변환하여 반환</li>
     * </ol>
     *
     * <p><strong>삭제 처리 예시 (Application Layer):</strong></p>
     * <pre>{@code
     * // 1. 조회
     * ExampleDomain domain = queryOutPort.findById(id).orElseThrow();
     *
     * // 2. 비즈니스 로직 (상태를 DELETED로 변경)
     * ExampleDomain deleted = domain.delete();
     *
     * // 3. 저장 (save 메서드로 통일)
     * commandOutPort.save(deleted);
     * }</pre>
     *
     * @param domain 저장할 Example 도메인 (ID=null이면 신규 생성, ID가 있으면 수정)
     * @return 저장된 Example 도메인 (ID 할당됨)
     */
    ExampleDomain save(ExampleDomain domain);
}
