package com.example.SpringBatchTutorial.job.jobListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * desc: Hello World 출력
 * run : --spring.batch.job.names=jobListenerJob
 */
@Configuration
public class JobListenerConfig {
    @Bean
    public Job jobListenerJob(JobRepository jobRepository, @Qualifier("jobListenerStep") Step jobListenerStep) {
        return new JobBuilder("jobListenerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(jobListenerStep)
                .build();
    }

    @Bean
    @JobScope
    public Step jobListenerStep(JobRepository jobRepository, @Qualifier("jobListenerTasklet") Tasklet jobListenerTasklet, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("jobListenerStep",jobRepository)
                .tasklet(jobListenerTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet jobListenerTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Job Listener Tasklet");
//            return RepeatStatus.FINISHED;
            throw new Exception("Fail!!!!");
        };
    }

}
