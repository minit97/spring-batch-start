package com.example.SpringBatchTutorial.job.helloWorld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
 * run : --spring.batch.job.names=helloWorldJob
 */
@Configuration
public class HelloWorldJobConfig {

    @Bean
    public Job helloWorldJob(JobRepository jobRepository, @Qualifier("helloWorldStep") Step helloWorldStep) {
        return new JobBuilder("helloWorldJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(helloWorldStep)
                .build();
    }

    @Bean
    public Step helloWorldStep(JobRepository jobRepository, @Qualifier("helloWorldTasklet") Tasklet helloWorldTasklet, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("helloWorldStep",jobRepository)
                .tasklet(helloWorldTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    public Tasklet helloWorldTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Hello World Spring Batch");
            return RepeatStatus.FINISHED;
        };
    }

}
