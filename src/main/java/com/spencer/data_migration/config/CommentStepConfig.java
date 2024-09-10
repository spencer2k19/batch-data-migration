package com.spencer.data_migration.config;

import com.spencer.data_migration.mappers.CommentRowMapper;
import com.spencer.data_migration.mappers.UserRowMapper;
import com.spencer.data_migration.models.Comment;
import com.spencer.data_migration.models.User;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Types;

@Configuration
public class CommentStepConfig {
    @Bean
    public JdbcCursorItemReader<Comment> commentReader(@Qualifier("mySqlDataSource") DataSource mySqlDataSource) {
        return new JdbcCursorItemReaderBuilder<Comment>()
                .dataSource(mySqlDataSource)
                .name("commentReader")
                .sql("select * from comments")
                .rowMapper(new CommentRowMapper())
                //.rowMapper(new BeanPropertyRowMapper<>(User.class))
                .build();
    }

    @Bean public JdbcBatchItemWriter<Comment> commentWriter(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new JdbcBatchItemWriterBuilder<Comment>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO comments VALUES (?, ?, ?, ?, ?, ?)")
                .itemPreparedStatementSetter((comment, ps) -> {
                    ps.setLong(1,comment.getId());
                    ps.setString(2,comment.getContent());
                    ps.setObject(3, comment.getPublication() != null ? comment.getPublication().getId(): null, Types.BIGINT);
                    ps.setObject(4, comment.getUser() != null ? comment.getUser().getId(): null, Types.BIGINT);
                    ps.setTimestamp(5, Timestamp.from(comment.getCreatedOn().toInstant()));
                    ps.setTimestamp(6, Timestamp.from(comment.getUpdatedOn().toInstant()));
                })
                .dataSource(postgresDataSource)
                .build();
    }


    @Bean(name = "commentStep")
    public Step commentStep(
            JdbcCursorItemReader<Comment> reader,
            JdbcBatchItemWriter<Comment> writer,
            PlatformTransactionManager mySqlTransactionManager,
            JobRepository jobRepository
    ) throws Exception {

        return new StepBuilder("commentStep",jobRepository)
                .<Comment,Comment>chunk(100,mySqlTransactionManager)
                .reader(reader)
                .writer(writer)
                .startLimit(1)
                .build();

    }
}
