package com.ryuqq.domain.module.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.layer.id.LayerId;
import com.ryuqq.domain.module.vo.ModuleName;
import java.util.Map;

/**
 * ModuleDuplicateNameException - 모듈 이름 중복 예외
 *
 * <p>동일한 레이어 내에서 모듈 이름이 이미 존재할 때 발생합니다.
 *
 * @author ryu-qqq
 */
public class ModuleDuplicateNameException extends DomainException {

    public ModuleDuplicateNameException(LayerId layerId, ModuleName name) {
        super(
                ModuleErrorCode.MODULE_DUPLICATE_NAME,
                String.format(
                        "Module name '%s' already exists in layer: %d",
                        name.value(), layerId.value()),
                Map.of("layerId", layerId.value(), "moduleName", name.value()));
    }
}
