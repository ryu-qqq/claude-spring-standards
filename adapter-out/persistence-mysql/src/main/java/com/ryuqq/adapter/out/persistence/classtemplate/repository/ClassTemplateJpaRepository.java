package com.ryuqq.adapter.out.persistence.classtemplate.repository;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ClassTemplateJpaRepository - 클래스 템플릿 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ClassTemplateJpaRepository extends JpaRepository<ClassTemplateJpaEntity, Long> {}
