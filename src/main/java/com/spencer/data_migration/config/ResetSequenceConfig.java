package com.spencer.data_migration.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ResetSequenceConfig {

    @Bean(name = "resetSequenceStep")
    public Step resetSequenceStep(
            PlatformTransactionManager postgresTransactionManager,
            JobRepository jobRepository,
            ResetSequenceIdTasklet  resetSequenceIdTasklet
    ) throws Exception {
        return new StepBuilder("resetSequenceStep",jobRepository)
                .tasklet(resetSequenceIdTasklet,postgresTransactionManager)
                .build();

    }
}
