package com.ryuqq.application.packagestructure.assembler;

import com.ryuqq.application.packagestructure.dto.response.PackageStructureResult;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackageStructureAssembler - 패키지 구조 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructureAssembler {

    /**
     * PackageStructure 도메인 객체를 PackageStructureResult로 변환
     *
     * @param packageStructure 패키지 구조 도메인 객체
     * @return PackageStructureResult
     */
    public PackageStructureResult toResult(PackageStructure packageStructure) {
        return PackageStructureResult.from(packageStructure);
    }

    /**
     * PackageStructure 목록을 PackageStructureResult 목록으로 변환
     *
     * @param packageStructures 패키지 구조 도메인 객체 목록
     * @return PackageStructureResult 목록
     */
    public List<PackageStructureResult> toResults(List<PackageStructure> packageStructures) {
        return packageStructures.stream().map(this::toResult).toList();
    }

    /**
     * PackageStructure 목록을 PackageStructureSliceResult로 변환
     *
     * @param packageStructures 패키지 구조 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return PackageStructureSliceResult
     */
    public PackageStructureSliceResult toSliceResult(
            List<PackageStructure> packageStructures, int requestedSize) {
        boolean hasNext = packageStructures.size() > requestedSize;
        List<PackageStructure> resultPackageStructures =
                hasNext ? packageStructures.subList(0, requestedSize) : packageStructures;
        List<PackageStructureResult> results = toResults(resultPackageStructures);
        return PackageStructureSliceResult.of(results, hasNext);
    }
}
