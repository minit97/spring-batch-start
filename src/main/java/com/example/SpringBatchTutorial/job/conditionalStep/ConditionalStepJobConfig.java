package com.example.SpringBatchTutorial.job.conditionalStep;

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
 * desc : step 결과의 따른 다음 step 분기 처리
 * run : --spring.batch.job.name=multipleStepJob
 */
@Configuration
public class ConditionalStepJobConfig {
    @Bean
    public Job conditionalStepJob(JobRepository jobRepository,
                                  @Qualifier("conditionalStartStep") Step startStep,
                                  @Qualifier("conditionalAllStep") Step allStep,
                                  @Qualifier("conditionalFailStep") Step failStep,
                                  @Qualifier("conditionalCompleteStep") Step completeStep) {
        return new JobBuilder("multipleStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(startStep)
                .on("FAILED").to(failStep)
                .from(startStep)
                .on("COMPLETED").to(completeStep)
                .from(startStep)
                .on("*").to(allStep)
                .end()
                .build();
    }

    @JobScope
    @Bean
    public Step conditionalStartStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("conditionalStartStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional Start Step");
                    return RepeatStatus.FINISHED;
//                    throw new Exception("exception!");
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step conditionalAllStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("conditionalAllStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional All Step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step conditionalFailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("conditionalFailStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional Fail Step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @JobScope
    @Bean
    public Step conditionalCompleteStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("conditionalCompleteStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("conditional Complete Step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
