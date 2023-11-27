package com.example.SpringBatchTutorial.job.dbDataReadWrite;

import com.example.SpringBatchTutorial.core.domain.Accounts;
import com.example.SpringBatchTutorial.core.domain.Orders;
import com.example.SpringBatchTutorial.core.repository.AccountsRepository;
import com.example.SpringBatchTutorial.core.repository.OrdersRepository;
import com.example.SpringBatchTutorial.job.jobListener.JobLoggerListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Collections;

/**
 * desc: 주문 테이블 -> 정산 테이블 데이터 이관
 * run : --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(JobRepository jobRepository, @Qualifier("trMigrationStep") Step step) {
        return new JobBuilder("trMigrationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(step)
                .build();
    }

    @Bean
    @JobScope
    public Step trMigrationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                @Qualifier("trOrdersReader") ItemReader itemReader,
                                @Qualifier("trOrderProcessor") ItemProcessor itemProcessor,
                                @Qualifier("trOrdersWriter") ItemWriter itemWriter) {
        return new StepBuilder("trMigrationStep",jobRepository)
//                .<Orders, Orders>chunk(5)       // deprecated
                .chunk(5, transactionManager)   // 몇개의 단위로 데이터를 처리할 것인가?, 5개의 트랜잭션 갯수
                .reader(itemReader)
//                .writer(new ItemWriter<Object>() {
//                    @Override
//                    public void write(Chunk<?> chunk) throws Exception {
//                        chunk.forEach(System.out::println);
//                    }
//                })
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

//    @StepScope
//    @Bean
//    public RepositoryItemWriter<Accounts> trOrdersWriter() {
//        return new RepositoryItemWriterBuilder<Accounts>()
//                .repository(accountsRepository)
//                .methodName("save")
//                .build();
//    }

    @StepScope
    @Bean
    public ItemWriter<Accounts> trOrdersWriter() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(Chunk<? extends Accounts> chunk) throws Exception {
                chunk.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor() {
        return item -> new Accounts(item);
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

}
