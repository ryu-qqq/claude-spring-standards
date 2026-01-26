package com.ryuqq.application.zerotolerance.manager;

import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.port.out.ZeroToleranceRuleQueryPort;
import com.ryuqq.domain.zerotolerance.aggregate.ZeroToleranceRule;
import com.ryuqq.domain.zerotolerance.exception.ZeroToleranceRuleNotFoundException;
import com.ryuqq.domain.zerotolerance.id.ZeroToleranceRuleId;
import com.ryuqq.domain.zerotolerance.query.ZeroToleranceRuleSliceCriteria;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleReadManager - Zero-Tolerance 규칙 조회 매니저
 *
 * <p>Zero-Tolerance 규칙 조회 로직을 담당합니다.
 *
 * <p>MGR-001: Manager는 ReadManager/PersistenceManager로 분리.
 *
 * <p>MGR-002: ReadManager는 QueryPort만 의존.
 *
 * @author ryu-qqq
 */
@Component
public class ZeroToleranceRuleReadManager {

    private final ZeroToleranceRuleQueryPort zeroToleranceRuleQueryPort;

    public ZeroToleranceRuleReadManager(ZeroToleranceRuleQueryPort zeroToleranceRuleQueryPort) {
        this.zeroToleranceRuleQueryPort = zeroToleranceRuleQueryPort;
    }

    /**
     * ID로 Zero-Tolerance 규칙 조회 (없으면 예외)
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @return ZeroToleranceRule
     * @throws ZeroToleranceRuleNotFoundException 규칙이 존재하지 않으면
     */
    public ZeroToleranceRule getById(ZeroToleranceRuleId zeroToleranceRuleId) {
        return findById(zeroToleranceRuleId)
                .orElseThrow(() -> new ZeroToleranceRuleNotFoundException(zeroToleranceRuleId));
    }

    /**
     * ID로 Zero-Tolerance 규칙 조회 (Optional)
     *
     * @param zeroToleranceRuleId Zero-Tolerance 규칙 ID
     * @return ZeroToleranceRule Optional
     */
    public Optional<ZeroToleranceRule> findById(ZeroToleranceRuleId zeroToleranceRuleId) {
        return zeroToleranceRuleQueryPort.findById(zeroToleranceRuleId);
    }

    /**
     * CodingRuleId로 Zero-Tolerance 규칙 존재 여부 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @return 존재하면 true
     */
    public boolean existsByRuleId(Long ruleId) {
        return zeroToleranceRuleQueryPort.existsByRuleId(ruleId);
    }

    /**
     * 슬라이스 조건으로 Zero-Tolerance 규칙 상세 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return Zero-Tolerance 규칙 슬라이스 결과
     */
    public ZeroToleranceRuleSliceResult findAllDetails(ZeroToleranceRuleSliceCriteria criteria) {
        return zeroToleranceRuleQueryPort.findAllDetails(criteria);
    }
}
