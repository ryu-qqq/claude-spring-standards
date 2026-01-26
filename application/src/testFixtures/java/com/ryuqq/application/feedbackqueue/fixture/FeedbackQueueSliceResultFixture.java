package com.ryuqq.application.feedbackqueue.fixture;

import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import java.util.List;

/**
 * FeedbackQueueSliceResult Test Fixture
 *
 * @author development-team
 */
public final class FeedbackQueueSliceResultFixture {

    private FeedbackQueueSliceResultFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 기본 FeedbackQueueSliceResult 생성 (3개 항목, hasNext=true)
     *
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult defaultSlice() {
        return sliceWithContent(3, true);
    }

    /**
     * 빈 FeedbackQueueSliceResult 생성
     *
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult emptySlice() {
        return FeedbackQueueSliceResult.empty();
    }

    /**
     * 마지막 페이지 FeedbackQueueSliceResult 생성
     *
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult lastPageSlice() {
        return sliceWithContent(2, false);
    }

    /**
     * 지정된 개수와 hasNext로 FeedbackQueueSliceResult 생성
     *
     * @param count 항목 개수
     * @param hasNext 다음 페이지 존재 여부
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult sliceWithContent(int count, boolean hasNext) {
        List<FeedbackQueueResult> content =
                java.util.stream.IntStream.rangeClosed(1, count)
                        .mapToObj(FeedbackQueueSliceResultFixture::createResultWithId)
                        .toList();
        return FeedbackQueueSliceResult.of(content, hasNext);
    }

    /**
     * PENDING 상태만 포함된 슬라이스 생성
     *
     * @param count 항목 개수
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult pendingSlice(int count) {
        List<FeedbackQueueResult> content =
                java.util.stream.IntStream.rangeClosed(1, count)
                        .mapToObj(
                                i ->
                                        FeedbackQueueResultFixture.withParams(
                                                (long) i,
                                                "RULE_EXAMPLE",
                                                null,
                                                "ADD",
                                                "SAFE",
                                                "{\"code\": \"example-" + i + "\"}",
                                                "PENDING",
                                                null))
                        .toList();
        return FeedbackQueueSliceResult.of(content, false);
    }

    /**
     * 다양한 상태가 혼합된 슬라이스 생성
     *
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult mixedStatusSlice() {
        List<FeedbackQueueResult> content =
                List.of(
                        FeedbackQueueResultFixture.pendingResult(),
                        FeedbackQueueResultFixture.llmApprovedResult(),
                        FeedbackQueueResultFixture.llmRejectedResult(),
                        FeedbackQueueResultFixture.humanApprovedResult(),
                        FeedbackQueueResultFixture.mergedResult());
        return FeedbackQueueSliceResult.of(content, false);
    }

    /**
     * 특정 위험 레벨만 포함된 슬라이스 생성
     *
     * @param riskLevel 위험 레벨 (SAFE, MEDIUM, DANGEROUS)
     * @param count 항목 개수
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult sliceByRiskLevel(String riskLevel, int count) {
        List<FeedbackQueueResult> content =
                java.util.stream.IntStream.rangeClosed(1, count)
                        .mapToObj(
                                i ->
                                        FeedbackQueueResultFixture.withParams(
                                                (long) i,
                                                "RULE_EXAMPLE",
                                                null,
                                                "ADD",
                                                riskLevel,
                                                "{\"code\": \"example-" + i + "\"}",
                                                "PENDING",
                                                null))
                        .toList();
        return FeedbackQueueSliceResult.of(content, false);
    }

    /**
     * 특정 대상 타입만 포함된 슬라이스 생성
     *
     * @param targetType 대상 타입
     * @param count 항목 개수
     * @return FeedbackQueueSliceResult
     */
    public static FeedbackQueueSliceResult sliceByTargetType(String targetType, int count) {
        List<FeedbackQueueResult> content =
                java.util.stream.IntStream.rangeClosed(1, count)
                        .mapToObj(
                                i ->
                                        FeedbackQueueResultFixture.withParams(
                                                (long) i,
                                                targetType,
                                                null,
                                                "ADD",
                                                "SAFE",
                                                "{\"data\": \"test-" + i + "\"}",
                                                "PENDING",
                                                null))
                        .toList();
        return FeedbackQueueSliceResult.of(content, false);
    }

    private static FeedbackQueueResult createResultWithId(int id) {
        return FeedbackQueueResultFixture.withParams(
                (long) id,
                "RULE_EXAMPLE",
                null,
                "ADD",
                "SAFE",
                "{\"code\": \"example-" + id + "\"}",
                "PENDING",
                null);
    }
}
