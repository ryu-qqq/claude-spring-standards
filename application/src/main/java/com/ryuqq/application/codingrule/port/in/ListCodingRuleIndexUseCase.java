package com.ryuqq.application.codingrule.port.in;

import com.ryuqq.application.codingrule.dto.query.CodingRuleIndexSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import java.util.List;

/**
 * ListCodingRuleIndexUseCase - 코딩 규칙 인덱스 조회 UseCase
 *
 * <p>규칙 인덱스(code, name, severity, category)만 조회합니다.
 *
 * <p>UC-001: UseCase는 반드시 interface로 정의.
 *
 * <p>UC-002: UseCase는 execute() 단일 메서드만 제공.
 *
 * <p>UC-007: Query UseCase는 조회 접두어 + UseCase 네이밍.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ListCodingRuleIndexUseCase {

    /**
     * 코딩 규칙 인덱스 조회 실행
     *
     * @param searchParams 조회 파라미터
     * @return 규칙 인덱스 목록
     */
    List<CodingRuleIndexItem> execute(CodingRuleIndexSearchParams searchParams);
}
