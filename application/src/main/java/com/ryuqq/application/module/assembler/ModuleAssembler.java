package com.ryuqq.application.module.assembler;

import com.ryuqq.application.module.dto.response.ModuleResult;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.dto.response.ModuleTreeResult;
import com.ryuqq.domain.module.aggregate.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ModuleAssembler - Module 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain → Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleAssembler {

    /**
     * Module 도메인 객체를 ModuleResult로 변환
     *
     * @param module 모듈 도메인 객체
     * @return ModuleResult
     */
    public ModuleResult toResult(Module module) {
        return new ModuleResult(
                module.id().value(),
                module.layerIdValue(),
                module.parentModuleId() != null ? module.parentModuleId().value() : null,
                module.name().value(),
                module.description().value(),
                module.modulePath().value(),
                module.buildIdentifier() != null ? module.buildIdentifier().value() : null,
                module.createdAt(),
                module.updatedAt());
    }

    /**
     * Module 목록을 ModuleResult 목록으로 변환
     *
     * @param modules 모듈 도메인 객체 목록
     * @return ModuleResult 목록
     */
    public List<ModuleResult> toResults(List<Module> modules) {
        return modules.stream().map(this::toResult).toList();
    }

    /**
     * Module 목록을 트리 구조로 변환
     *
     * <p>평면 리스트를 받아서 parentModuleId를 기반으로 트리 구조를 생성합니다.
     *
     * <p>알고리즘:
     *
     * <ol>
     *   <li>모든 모듈을 Map에 저장 (children는 빈 리스트)
     *   <li>각 모듈의 children 리스트를 구성 (parentModuleId -> List<children>)
     *   <li>모든 모듈을 순회하면서 children를 포함한 새 인스턴스 생성
     *   <li>루트 노드들만 반환
     * </ol>
     *
     * @param modules 모듈 도메인 객체 목록
     * @return ModuleTreeResult 목록 (루트 노드들, children 포함)
     */
    public List<ModuleTreeResult> toTreeResults(List<Module> modules) {
        if (modules.isEmpty()) {
            return List.of();
        }

        // 1. 모든 모듈을 ModuleTreeResult로 변환하고 Map에 저장 (children는 빈 리스트)
        Map<Long, ModuleTreeResult> moduleMap =
                modules.stream()
                        .map(this::toTreeResult)
                        .collect(Collectors.toMap(ModuleTreeResult::moduleId, result -> result));

        // 2. 각 모듈의 children 리스트를 구성 (parentModuleId -> List<children>)
        Map<Long, List<ModuleTreeResult>> childrenMap = new HashMap<>();
        for (Module module : modules) {
            Long parentModuleId =
                    module.parentModuleId() != null ? module.parentModuleId().value() : null;

            if (parentModuleId != null) {
                childrenMap
                        .computeIfAbsent(parentModuleId, k -> new ArrayList<>())
                        .add(moduleMap.get(module.id().value()));
            }
        }

        // 3. children를 포함한 ModuleTreeResult로 재구성
        Map<Long, ModuleTreeResult> resultMap = new HashMap<>();
        for (Module module : modules) {
            Long moduleId = module.id().value();
            ModuleTreeResult original = moduleMap.get(moduleId);
            List<ModuleTreeResult> children = childrenMap.getOrDefault(moduleId, List.of());

            ModuleTreeResult updated =
                    ModuleTreeResult.withChildren(
                            original.moduleId(),
                            original.layerId(),
                            original.parentModuleId(),
                            original.name(),
                            original.description(),
                            original.modulePath(),
                            original.buildIdentifier(),
                            original.createdAt(),
                            original.updatedAt(),
                            children);
            resultMap.put(moduleId, updated);
        }

        // 4. 루트 노드들만 반환 (children 포함)
        return modules.stream()
                .filter(module -> module.parentModuleId() == null)
                .map(module -> resultMap.get(module.id().value()))
                .toList();
    }

    /**
     * Module 도메인 객체를 ModuleTreeResult로 변환 (children는 빈 리스트)
     *
     * @param module 모듈 도메인 객체
     * @return ModuleTreeResult (children는 빈 리스트)
     */
    private ModuleTreeResult toTreeResult(Module module) {
        return ModuleTreeResult.of(
                module.id().value(),
                module.layerIdValue(),
                module.parentModuleId() != null ? module.parentModuleId().value() : null,
                module.name().value(),
                module.description().value(),
                module.modulePath().value(),
                module.buildIdentifier() != null ? module.buildIdentifier().value() : null,
                module.createdAt(),
                module.updatedAt());
    }

    /**
     * Module 목록을 ModuleSliceResult로 변환
     *
     * @param modules 모듈 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return ModuleSliceResult
     */
    public ModuleSliceResult toSliceResult(List<Module> modules, int requestedSize) {
        boolean hasNext = modules.size() > requestedSize;
        List<Module> resultModules = hasNext ? modules.subList(0, requestedSize) : modules;
        List<ModuleResult> results = toResults(resultModules);
        return ModuleSliceResult.of(results, hasNext);
    }
}
