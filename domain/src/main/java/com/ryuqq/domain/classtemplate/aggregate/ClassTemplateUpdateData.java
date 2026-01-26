package com.ryuqq.domain.classtemplate.aggregate;

import com.ryuqq.domain.classtemplate.vo.NamingPattern;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.classtemplate.vo.TemplateDescription;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import java.util.List;

/**
 * ClassTemplateUpdateData - 클래스 템플릿 업데이트 데이터
 *
 * @author ryu-qqq
 */
public record ClassTemplateUpdateData(
        ClassTypeId classTypeId,
        TemplateCode templateCode,
        NamingPattern namingPattern,
        TemplateDescription description,
        List<String> requiredAnnotations,
        List<String> forbiddenAnnotations,
        List<String> requiredInterfaces,
        List<String> forbiddenInheritance,
        List<String> requiredMethods) {}
