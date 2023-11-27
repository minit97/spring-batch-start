package com.example.SpringBatchTutorial.core.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SampleScheduler {
    private final Job helloWorldJob;
    private final JobLauncher jobLauncher;

    public SampleScheduler(@Qualifier("helloWorldJob") Job helloWorldJob, JobLauncher jobLauncher) {
        this.helloWorldJob = helloWorldJob;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void helloworldJobRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParameters(
                Collections.singletonMap("requestTime", new JobParameter(System.currentTimeMillis(), Long.class))
        );

        jobLauncher.run(helloWorldJob, jobParameters);
        // jobParameters 값 없이 job을 실행시키면 동일한 파라미터로 실행되어 spring-batch에서는 똑같은 job을 실행하다고 생각하여
        // job이 실행이 안된다.
    }


}
