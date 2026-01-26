package com.ryuqq.domain.ruleexample.vo;

import java.util.Collections;
import java.util.List;

/**
 * HighlightLines - 강조 라인 번호 Value Object
 *
 * <p>예시 코드에서 강조할 라인 번호 목록
 *
 * @author ryu-qqq
 */
public record HighlightLines(List<Integer> lines) {

    public HighlightLines {
        lines = lines != null ? Collections.unmodifiableList(lines) : Collections.emptyList();
    }

    public static HighlightLines of(List<Integer> lines) {
        return new HighlightLines(lines);
    }

    public static HighlightLines empty() {
        return new HighlightLines(Collections.emptyList());
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public boolean containsLine(int lineNumber) {
        return lines.contains(lineNumber);
    }
}
