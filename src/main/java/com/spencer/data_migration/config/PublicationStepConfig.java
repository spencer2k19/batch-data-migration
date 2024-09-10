package com.spencer.data_migration.config;

import com.spencer.data_migration.mappers.PublicationRowMapper;
import com.spencer.data_migration.models.Publication;
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

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Types;

@Configuration
public class PublicationStepConfig {
    @Bean
    public JdbcCursorItemReader<Publication> publicationReader(@Qualifier("mySqlDataSource") DataSource mySqlDataSource) {
        return  new JdbcCursorItemReaderBuilder<Publication>()
                .dataSource(mySqlDataSource)
                .name("publicationReader")
                .sql("select * from publications")
                .rowMapper(new PublicationRowMapper())
                //.rowMapper(new BeanPropertyRowMapper<>(User.class))
                .build();
    }


    @Bean
    public ItemProcessor<Publication, Publication> publicationItemProcessor() {
        return publication -> {
            // Optional: make transformations
            return publication;
        };
    }


    @Bean public JdbcBatchItemWriter<Publication> publicationWriter(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new JdbcBatchItemWriterBuilder<Publication>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO publications VALUES (?, ?, ?, ?, ?, ?)")
                .itemPreparedStatementSetter((publication, ps) -> {
                    ps.setLong(1,publication.getId());
                    ps.setString(2,publication.getMedia());
                    ps.setString(3,publication.getContent());
                    ps.setObject(4, publication.getUser() != null ? publication.getUser().getId(): null, Types.BIGINT);

                    ps.setTimestamp(5, Timestamp.from(publication.getCreatedOn().toInstant()));
                    ps.setTimestamp(6, Timestamp.from(publication.getUpdatedOn().toInstant()));
                })
                .dataSource(postgresDataSource)
                .build();
    }


    @Bean(name = "publicationStep")
    public Step publicationStep(
            JdbcCursorItemReader<Publication> reader,
            JdbcBatchItemWriter<Publication> writer,
            ItemProcessor<Publication,Publication> publicationItemProcessor,
            PlatformTransactionManager mySqlTransactionManager,
            JobRepository jobRepository
    ) throws Exception {

        return new StepBuilder("publicationStep",jobRepository)
                .<Publication,Publication>chunk(100,mySqlTransactionManager)
                .reader(reader)
                .writer(writer)
                .startLimit(1)
                .allowStartIfComplete(false)

                .build();

    }
}
