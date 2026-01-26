package com.ryuqq.adapter.in.rest.classtemplate.mapper;

import com.ryuqq.adapter.in.rest.classtemplate.dto.request.CreateClassTemplateApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.request.UpdateClassTemplateApiRequest;
import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateCommandApiMapper - ClassTemplate Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component로 등록.
 *
 * <p>MAP-002: Mapper에서 Static 메서드 금지.
 *
 * <p>MAP-004: Mapper는 필드 매핑만 수행.
 *
 * <p>MAP-006: Mapper에서 Domain 객체 직접 사용 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTemplateCommandApiMapper {

    /**
     * CreateClassTemplateApiRequest -> CreateClassTemplateCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateClassTemplateCommand toCommand(CreateClassTemplateApiRequest request) {
        return new CreateClassTemplateCommand(
                request.structureId(),
                request.classTypeId(),
                request.templateCode(),
                request.namingPattern(),
                request.description(),
                nullSafeList(request.requiredAnnotations()),
                nullSafeList(request.forbiddenAnnotations()),
                nullSafeList(request.requiredInterfaces()),
                nullSafeList(request.forbiddenInheritance()),
                nullSafeList(request.requiredMethods()));
    }

    /**
     * UpdateClassTemplateApiRequest + PathVariable ID -> UpdateClassTemplateCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param classTemplateId ClassTemplate ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateClassTemplateCommand toCommand(
            Long classTemplateId, UpdateClassTemplateApiRequest request) {
        return new UpdateClassTemplateCommand(
                classTemplateId,
                request.classTypeId(),
                request.templateCode(),
                request.namingPattern(),
                request.description(),
                nullSafeList(request.requiredAnnotations()),
                nullSafeList(request.forbiddenAnnotations()),
                nullSafeList(request.requiredInterfaces()),
                nullSafeList(request.forbiddenInheritance()),
                nullSafeList(request.requiredMethods()));
    }

    private List<String> nullSafeList(List<String> list) {
        return list != null ? list : Collections.emptyList();
    }
}
