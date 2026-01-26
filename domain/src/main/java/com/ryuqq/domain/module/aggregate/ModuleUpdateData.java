package com.ryuqq.domain.module.aggregate;

import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.module.vo.BuildIdentifier;
import com.ryuqq.domain.module.vo.ModuleDescription;
import com.ryuqq.domain.module.vo.ModuleName;
import com.ryuqq.domain.module.vo.ModulePath;

/**
 * ModuleUpdateData - 모듈 수정 데이터
 *
 * <p>Layer는 모듈 생성 시점에 결정되므로 수정 대상에서 제외됩니다.
 *
 * @author ryu-qqq
 */
public record ModuleUpdateData(
        ModuleId parentModuleId,
        ModuleName name,
        ModuleDescription description,
        ModulePath modulePath,
        BuildIdentifier buildIdentifier) {

    public ModuleUpdateData {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (modulePath == null) {
            throw new IllegalArgumentException("modulePath must not be null");
        }
        if (description == null) {
            description = ModuleDescription.empty();
        }
        if (buildIdentifier == null) {
            buildIdentifier = BuildIdentifier.empty();
        }
        // parentModuleId는 nullable
    }
}
