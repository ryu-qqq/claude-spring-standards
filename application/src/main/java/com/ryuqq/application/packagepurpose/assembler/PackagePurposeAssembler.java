package com.ryuqq.application.packagepurpose.assembler;

import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeResult;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeAssembler - 패키지 목적 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class PackagePurposeAssembler {

    /**
     * PackagePurpose 도메인 객체를 PackagePurposeResult로 변환
     *
     * @param packagePurpose 패키지 목적 도메인 객체
     * @return PackagePurposeResult
     */
    public PackagePurposeResult toResult(PackagePurpose packagePurpose) {
        return PackagePurposeResult.from(packagePurpose);
    }

    /**
     * PackagePurpose 목록을 PackagePurposeResult 목록으로 변환
     *
     * @param packagePurposes 패키지 목적 도메인 객체 목록
     * @return PackagePurposeResult 목록
     */
    public List<PackagePurposeResult> toResults(List<PackagePurpose> packagePurposes) {
        return packagePurposes.stream().map(this::toResult).toList();
    }

    /**
     * PackagePurpose 목록을 PackagePurposeSliceResult로 변환
     *
     * @param packagePurposes 패키지 목적 도메인 객체 목록
     * @param requestedSize 요청한 슬라이스 크기
     * @return PackagePurposeSliceResult
     */
    public PackagePurposeSliceResult toSliceResult(
            List<PackagePurpose> packagePurposes, int requestedSize) {
        boolean hasNext = packagePurposes.size() > requestedSize;
        List<PackagePurpose> resultPurposes =
                hasNext ? packagePurposes.subList(0, requestedSize) : packagePurposes;
        List<PackagePurposeResult> results = toResults(resultPurposes);
        return PackagePurposeSliceResult.of(results, hasNext);
    }
}
