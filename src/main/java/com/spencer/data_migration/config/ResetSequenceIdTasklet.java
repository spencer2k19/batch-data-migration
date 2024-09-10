package com.spencer.data_migration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ResetSequenceIdTasklet implements Tasklet, StepExecutionListener {


    private final DataSource dataSource;

    public ResetSequenceIdTasklet(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        this.dataSource = postgresDataSource;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('publications','id'),(select MAX(id) FROM publications) +1)", Long.class);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('socialite_users','id'),(select MAX(id) FROM socialite_users) +1)", Long.class);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('comments','id'),(select MAX(id) FROM comments) +1)", Long.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to reset sequence database", e);
        }

        return RepeatStatus.FINISHED;
    }
}
