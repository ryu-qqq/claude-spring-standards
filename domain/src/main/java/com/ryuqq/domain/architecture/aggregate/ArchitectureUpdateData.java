package com.ryuqq.domain.architecture.aggregate;

import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.architecture.vo.PatternDescription;
import com.ryuqq.domain.architecture.vo.PatternPrinciples;
import com.ryuqq.domain.architecture.vo.PatternType;
import com.ryuqq.domain.common.vo.ReferenceLinks;

/**
 * ArchitectureUpdateData - 아키텍처 수정 데이터
 *
 * @author ryu-qqq
 */
public record ArchitectureUpdateData(
        ArchitectureName name,
        PatternType patternType,
        PatternDescription patternDescription,
        PatternPrinciples patternPrinciples,
        ReferenceLinks referenceLinks) {

    public ArchitectureUpdateData {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (patternType == null) {
            throw new IllegalArgumentException("patternType must not be null");
        }
        if (patternDescription == null) {
            patternDescription = PatternDescription.empty();
        }
        if (patternPrinciples == null) {
            patternPrinciples = PatternPrinciples.empty();
        }
        if (referenceLinks == null) {
            referenceLinks = ReferenceLinks.empty();
        }
    }
}
