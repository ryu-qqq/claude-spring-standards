package com.ryuqq.domain.archunittest.aggregate;

import com.ryuqq.domain.archunittest.vo.ArchUnitTestDescription;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestName;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.archunittest.vo.TestCode;

/**
 * ArchUnitTestUpdateData - ArchUnit 테스트 수정 데이터
 *
 * <p>null 필드는 기존 값을 유지합니다.
 *
 * @author ryu-qqq
 */
public record ArchUnitTestUpdateData(
        String code,
        ArchUnitTestName name,
        ArchUnitTestDescription description,
        String testClassName,
        String testMethodName,
        TestCode testCode,
        ArchUnitTestSeverity severity) {

    /**
     * 빌더 패턴으로 UpdateData 생성
     *
     * @return Builder 인스턴스
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code;
        private ArchUnitTestName name;
        private ArchUnitTestDescription description;
        private String testClassName;
        private String testMethodName;
        private TestCode testCode;
        private ArchUnitTestSeverity severity;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(ArchUnitTestName name) {
            this.name = name;
            return this;
        }

        public Builder description(ArchUnitTestDescription description) {
            this.description = description;
            return this;
        }

        public Builder testClassName(String testClassName) {
            this.testClassName = testClassName;
            return this;
        }

        public Builder testMethodName(String testMethodName) {
            this.testMethodName = testMethodName;
            return this;
        }

        public Builder testCode(TestCode testCode) {
            this.testCode = testCode;
            return this;
        }

        public Builder severity(ArchUnitTestSeverity severity) {
            this.severity = severity;
            return this;
        }

        public ArchUnitTestUpdateData build() {
            return new ArchUnitTestUpdateData(
                    code, name, description, testClassName, testMethodName, testCode, severity);
        }
    }
}
