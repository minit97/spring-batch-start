package com.example.SpringBatchTutorial.job.dbDataReadWrite;

import com.example.SpringBatchTutorial.SpringBatchTestConfig;
import com.example.SpringBatchTutorial.SpringBatchTutorialApplication;
import com.example.SpringBatchTutorial.core.domain.Orders;
import com.example.SpringBatchTutorial.core.repository.AccountsRepository;
import com.example.SpringBatchTutorial.core.repository.OrdersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationConfig.class})
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
class TrMigrationConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private AccountsRepository accountsRepository;

    @AfterEach
    void cleanUpEach() {
        ordersRepository.deleteAll();
        accountsRepository.deleteAll();
    }

    @Test
    void success_noDate() throws Exception {
        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(0, accountsRepository.count());
    }

    @Test
    void success_yesData() throws Exception {
        // given
        Orders orders1 = new Orders(null, "kakao gift", 15000, new Date());
        Orders orders2 = new Orders(null, "naver gift", 15000, new Date());
        ordersRepository.save(orders1);
        ordersRepository.save(orders2);

        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(2, accountsRepository.count());
    }
}