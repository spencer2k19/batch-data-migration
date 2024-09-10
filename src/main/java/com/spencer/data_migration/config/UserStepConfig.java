package com.spencer.data_migration.config;

import com.spencer.data_migration.models.User;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import com.spencer.data_migration.mappers.UserRowMapper;


import javax.sql.DataSource;
import java.sql.Timestamp;

@Configuration
public class UserStepConfig {

    @Bean
    public JdbcCursorItemReader<User> userReader(@Qualifier("mySqlDataSource") DataSource mySqlDataSource) {
        return new JdbcCursorItemReaderBuilder<User>()
                .dataSource(mySqlDataSource)
                .name("userReader")
                .sql("select * from socialite_users")
                .rowMapper(new UserRowMapper())
                //.rowMapper(new BeanPropertyRowMapper<>(User.class))
                .build();
    }

    @Bean
    public ItemProcessor<User, User> processor() {
        return user -> {
            // Optional: make transformations
            return user;
        };
    }

    @Bean
    public JdbcBatchItemWriter<User> userWriter(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO socialite_users (id, email, password, identity, phone_number, created_on, updated_on) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")
                .itemPreparedStatementSetter((user, ps) -> {
                    ps.setLong(1, user.getId());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPassword());
                    ps.setString(4, user.getIdentity());
                    ps.setString(5, user.getPhoneNumber());
                    ps.setTimestamp(6, Timestamp.from(user.getCreatedOn().toInstant()));
                    ps.setTimestamp(7, Timestamp.from(user.getUpdatedOn().toInstant()));
                })
                .dataSource(postgresDataSource)
                .build();
    }

    @Bean(name = "userStep")
    public Step userStep(
            JdbcCursorItemReader<User> reader,
            JdbcBatchItemWriter<User> writer,
            ItemProcessor<User, User> processor,
            PlatformTransactionManager mySqlTransactionManager,
            JobRepository jobRepository
    ) throws Exception {

        return new StepBuilder("userStep", jobRepository)
                .<User, User>chunk(100, mySqlTransactionManager)
                .reader(reader)
                .writer(writer)
                .startLimit(1)
                .build();

    }
}
