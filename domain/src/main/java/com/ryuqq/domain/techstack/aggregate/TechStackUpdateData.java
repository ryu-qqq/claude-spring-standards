package com.ryuqq.domain.techstack.aggregate;

import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.vo.BuildConfigFile;
import com.ryuqq.domain.techstack.vo.BuildToolType;
import com.ryuqq.domain.techstack.vo.FrameworkModules;
import com.ryuqq.domain.techstack.vo.FrameworkType;
import com.ryuqq.domain.techstack.vo.FrameworkVersion;
import com.ryuqq.domain.techstack.vo.LanguageFeatures;
import com.ryuqq.domain.techstack.vo.LanguageType;
import com.ryuqq.domain.techstack.vo.LanguageVersion;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.RuntimeEnvironment;
import com.ryuqq.domain.techstack.vo.TechStackName;
import com.ryuqq.domain.techstack.vo.TechStackStatus;

/**
 * TechStackUpdateData - 기술 스택 수정 데이터
 *
 * @author ryu-qqq
 */
public record TechStackUpdateData(
        TechStackName name,
        TechStackStatus status,
        LanguageType languageType,
        LanguageVersion languageVersion,
        LanguageFeatures languageFeatures,
        FrameworkType frameworkType,
        FrameworkVersion frameworkVersion,
        FrameworkModules frameworkModules,
        PlatformType platformType,
        RuntimeEnvironment runtimeEnvironment,
        BuildToolType buildToolType,
        BuildConfigFile buildConfigFile,
        ReferenceLinks referenceLinks) {

    public TechStackUpdateData {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (languageType == null) {
            throw new IllegalArgumentException("languageType must not be null");
        }
        if (languageVersion == null) {
            throw new IllegalArgumentException("languageVersion must not be null");
        }
        if (frameworkType == null) {
            throw new IllegalArgumentException("frameworkType must not be null");
        }
        if (frameworkVersion == null) {
            throw new IllegalArgumentException("frameworkVersion must not be null");
        }
        if (platformType == null) {
            throw new IllegalArgumentException("platformType must not be null");
        }
        if (runtimeEnvironment == null) {
            throw new IllegalArgumentException("runtimeEnvironment must not be null");
        }
        if (buildToolType == null) {
            throw new IllegalArgumentException("buildToolType must not be null");
        }
        if (buildConfigFile == null) {
            throw new IllegalArgumentException("buildConfigFile must not be null");
        }
        if (languageFeatures == null) {
            languageFeatures = LanguageFeatures.empty();
        }
        if (frameworkModules == null) {
            frameworkModules = FrameworkModules.empty();
        }
        if (referenceLinks == null) {
            referenceLinks = ReferenceLinks.empty();
        }
    }
}
