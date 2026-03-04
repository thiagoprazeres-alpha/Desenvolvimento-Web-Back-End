package com.example.aula03_3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuração para habilitar JPA Auditing.
 * 
 * O JPA Auditing permite o rastreamento automático de:
 * - @CreatedDate: Data de criação do registro
 * - @LastModifiedDate: Data da última atualização
 * - @CreatedBy: Usuário que criou (opcional)
 * - @LastModifiedBy: Usuário que atualizou (opcional)
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
