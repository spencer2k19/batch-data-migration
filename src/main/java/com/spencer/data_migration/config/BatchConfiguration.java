package com.spencer.data_migration.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {
    @Bean(name = "mySqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mySqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "postgresDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.postgres")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "mySqlTransactionManager")
    @Primary
    public PlatformTransactionManager mySqlTransactionManager(@Qualifier("mySqlDataSource") DataSource mySqlDataSource) {
        return new DataSourceTransactionManager(mySqlDataSource);
    }

    @Bean(name = "postgresTransactionManager")
    public PlatformTransactionManager postgresTransactionManager(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new DataSourceTransactionManager(postgresDataSource);
    }



    @Bean
    public Job migrateDataJob(
            Step userStep,
            Step publicationStep,
            Step commentStep,
            Step resetSequenceStep,
           JobRepository jobRepository
    ) throws Exception {
        return new JobBuilder("dataJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(userStep)
                .next(publicationStep)
                .next(commentStep)
                .next(resetSequenceStep)
                .build();
    }
}
