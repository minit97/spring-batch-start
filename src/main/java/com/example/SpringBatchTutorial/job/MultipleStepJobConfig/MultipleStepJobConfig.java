package com.example.SpringBatchTutorial.job.MultipleStepJobConfig;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * desc : 다중 step을 사용하기 및 step to step 데이터 전달
 * run : --spring.batch.job.name=multipleStepJob
 */
@Configuration
public class MultipleStepJobConfig {

    @Bean
    public Job multipleStepJob(JobRepository jobRepository,
                               @Qualifier("multipleStep1") Step step1,
                               @Qualifier("multipleStep2") Step step2,
                               @Qualifier("multipleStep3") Step step3) {
        return new JobBuilder("multipleStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }

    @JobScope
    @Bean
    public Step multipleStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("multipleStep1",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step1");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    @JobScope
    @Bean
    public Step multipleStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("multipleStep2",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    executionContext.put("someKey", "hello!");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    @JobScope
    @Bean
    public Step multipleStep3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("multipleStep3",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step3");

                    ExecutionContext executionContext = chunkContext
                            .getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext();

                    System.out.println(executionContext.get("someKey"));

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
