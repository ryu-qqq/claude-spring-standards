package com.ryuqq.domain.techstack.vo;

/**
 * RuntimeEnvironment - 런타임 환경 열거형
 *
 * @author ryu-qqq
 */
public enum RuntimeEnvironment {
    JVM("JVM"),
    GRAALVM("GraalVM"),
    V8("V8"),
    NODE("Node.js"),
    DENO("Deno"),
    BUN("Bun"),
    PYTHON_INTERPRETER("Python Interpreter"),
    GO_RUNTIME("Go Runtime");

    private final String displayName;

    RuntimeEnvironment(String displayName) {
        this.displayName = displayName;
    }

    /** 표시용 이름 반환 */
    public String displayName() {
        return displayName;
    }
}
