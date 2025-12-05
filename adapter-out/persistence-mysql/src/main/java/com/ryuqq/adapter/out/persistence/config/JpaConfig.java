package com.ryuqq.adapter.out.persistence.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 및 QueryDSL 설정
 *
 * @author Development Team
 * @since 1.0.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.ryuqq.adapter.out.persistence")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
