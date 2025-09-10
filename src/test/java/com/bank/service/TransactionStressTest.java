package com.bank.service;

import com.bank.model.Transaction;
import com.bank.model.TransactionType;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
@Slf4j
public class TransactionStressTest {

    @Autowired
    private TransactionService transactionService;

    private final Random random = new Random();

    private final TransactionType[] types = TransactionType.values();

    private static final int CONCURRENT_USERS = 50;

    private static final int TRANSACTIONS_PER_USER = 20;

    private static final int TOTAL_OPERATIONS = CONCURRENT_USERS * TRANSACTIONS_PER_USER;

    /**
     * 测试创建交易的并发性能
     */
    @Test
    void testConcurrentTransactionCreation() throws InterruptedException {
        log.info("Starting stress test - {} concurrent users, {} transactions each",
            CONCURRENT_USERS, TRANSACTIONS_PER_USER);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Future<Long>> futures = new ArrayList<>();

        // 提交并发任务
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            futures.add(executor.submit(() -> performUserTransactions(userId)));
        }

        // 等待所有任务完成
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        // 计算总耗时和平均响应时间
        long totalTime = futures.stream()
            .mapToLong(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    log.error("Error in transaction", e);
                    return 0;
                }
            })
            .sum();

        stopWatch.stop();

        // 输出测试结果
        printTestResults(stopWatch.getTotalTimeMillis(), totalTime);
    }

    private Long performUserTransactions(int userId) {
        StopWatch userWatch = new StopWatch();
        userWatch.start();

        for (int i = 0; i < TRANSACTIONS_PER_USER; i++) {
            String reference = "USER-" + userId + "-TX-" + i + "-" + System.currentTimeMillis();
            createTestTransaction(reference);
        }

        userWatch.stop();
        return userWatch.getTotalTimeMillis();
    }

    private Long createTestTransaction(String reference) {
        Transaction transaction = new Transaction();
        transaction.setReference(reference);
        transaction.setAmount(new BigDecimal(random.nextInt(1000) + 10));
        transaction.setType(types[random.nextInt(types.length)]);
        transaction.setAccountNumber("LYQ" + System.currentTimeMillis());
        transaction.setDescription("Stress test transaction - " + System.currentTimeMillis());

        Transaction response = transactionService.createTransaction(transaction);
        return response.getId();
    }

    private void printTestResults(long totalTimeMs, long totalOperationTimeMs) {
        double throughput = (double) TOTAL_OPERATIONS / (totalTimeMs / 1000.0);
        double avgResponseTime = (double) totalOperationTimeMs / TOTAL_OPERATIONS;

        log.info("=== Stress Test Results ===");
        log.info("Total operations: {}", TOTAL_OPERATIONS);
        log.info("Total time: {} ms", totalTimeMs);
        log.info("Average response time per operation: {} ms", avgResponseTime);
        log.info("Throughput: {} operations/second", throughput);
        log.info("==========================================");
    }
}
