package com.example.SpringBatchTutorial.job.validatedParam;

import com.example.SpringBatchTutorial.job.validatedParam.validator.FileParamValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.names=validatedParamJob -fileName=test.csv
 */
@Configuration
public class ValidatedParamConfig {

    @Bean
    public Job validatedParamJob(JobRepository jobRepository, @Qualifier("validatedParamStep1") Step validatedParamStep) {
        return new JobBuilder("validatedParamJob", jobRepository)
                .incrementer(new RunIdIncrementer())
//                .validator(new FileParamValidator())
                .validator(multipleValidator())
                .start(validatedParamStep)
                .build();
    }

    private CompositeJobParametersValidator multipleValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));

        return validator;
    }

    @JobScope
    @Bean
    public Step validatedParamStep1(JobRepository jobRepository, @Qualifier("validatedParamTasklet") Tasklet validatedParamTasklet, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("validatedParamStep1",jobRepository)
                .tasklet(validatedParamTasklet, platformTransactionManager)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['-fileName']}") String fileName) {
        return (contribution, chunkContext) -> {
            System.out.println("validated Param Tasklet");
            return RepeatStatus.FINISHED;
        };
    }
}
