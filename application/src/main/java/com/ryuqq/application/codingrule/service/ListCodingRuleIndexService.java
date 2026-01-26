package com.ryuqq.application.codingrule.service;

import com.ryuqq.application.codingrule.dto.query.CodingRuleIndexSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.application.codingrule.factory.query.CodingRuleQueryFactory;
import com.ryuqq.application.codingrule.manager.CodingRuleReadManager;
import com.ryuqq.application.codingrule.port.in.ListCodingRuleIndexUseCase;
import com.ryuqq.domain.codingrule.query.CodingRuleIndexCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * ListCodingRuleIndexService - 코딩 규칙 인덱스 조회 서비스
 *
 * <p>규칙 인덱스(code, name, severity, category)만 조회합니다.
 *
 * <p>SVC-001: Service는 UseCase(Port-In)를 구현합니다.
 *
 * <p>SVC-002: Service는 오케스트레이션만 담당 (Factory → Manager).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class ListCodingRuleIndexService implements ListCodingRuleIndexUseCase {

    private final CodingRuleQueryFactory codingRuleQueryFactory;
    private final CodingRuleReadManager codingRuleReadManager;

    public ListCodingRuleIndexService(
            CodingRuleQueryFactory codingRuleQueryFactory,
            CodingRuleReadManager codingRuleReadManager) {
        this.codingRuleQueryFactory = codingRuleQueryFactory;
        this.codingRuleReadManager = codingRuleReadManager;
    }

    /**
     * 코딩 규칙 인덱스 조회 실행
     *
     * @param searchParams 조회 파라미터
     * @return 규칙 인덱스 목록
     */
    @Override
    public List<CodingRuleIndexItem> execute(CodingRuleIndexSearchParams searchParams) {
        CodingRuleIndexCriteria criteria = codingRuleQueryFactory.createIndexCriteria(searchParams);
        return codingRuleReadManager.findRuleIndex(criteria);
    }
}
