package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrentTest {
    @Autowired
    PointService pointService;

    @Autowired
    UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable.insertOrUpdate(1L, 0);
        userPointTable.insertOrUpdate(2L, 0);
        userPointTable.insertOrUpdate(3L, 0);
    }

    @Test
    @DisplayName("단일 사용자 포인트 사용 동시성 테스트")
    void shouldHandleConcurrentPointCharge() throws InterruptedException {
        long id = 1L;

        // given
        final int threadCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        pointService.charge(id, 9999L);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.use(id, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        UserPoint resultUser = pointService.selectPoint(id);

        // then
        assertEquals(9899, resultUser.point());
    }

    @Test
    @DisplayName("다중 사용자 포인트 사용 동시성 테스트")
    void shouldHandleConcurrentPointChargeForMultipleUsers() throws InterruptedException {
        long[] userIds = {1L, 2L, 3L};
        int threadCountPerUser = 100;
        int totalThreads = threadCountPerUser * userIds.length;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(totalThreads);

        // Given
        for (long userId : userIds) {
            pointService.charge(userId, 9999L);
        }

        // When
        for (long userId : userIds) {
            for (int i = 0; i < threadCountPerUser; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.use(userId, 1L);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
        }

        countDownLatch.await();

        // Then
        for (long userId : userIds) {
            UserPoint resultUser = pointService.selectPoint(userId);
            assertEquals(9999 - threadCountPerUser, resultUser.point());
        }

        executorService.shutdown();
    }

    @Test
    @DisplayName("단일 사용자 포인트 충전 동시성 테스트")
    void shouldHandleConcurrentPointUse() throws InterruptedException {
        long id = 1L;

        // given
        final int threadCount = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        pointService.charge(id, 1L);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.charge(id, 1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        UserPoint resultUser = pointService.selectPoint(id);

        // then
        assertEquals(1 + threadCount, resultUser.point());
    }

    @Test
    @DisplayName("다중 사용자 포인트 충전 동시성 테스트")
    void shouldHandleConcurrentPointUseForMultipleUsers() throws InterruptedException {
        long[] userIds = {1L, 2L, 3L};
        int threadCountPerUser = 100;
        int totalThreads = threadCountPerUser * userIds.length;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(totalThreads);

        // Given
        for (long userId : userIds) {
            pointService.charge(userId, 1L);
        }

        // When
        for (long userId : userIds) {
            for (int i = 0; i < threadCountPerUser; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.charge(userId, 1L);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
        }

        countDownLatch.await();

        // Then
        for (long userId : userIds) {
            UserPoint resultUser = pointService.selectPoint(userId);
            assertEquals(1 + threadCountPerUser, resultUser.point());
        }

        executorService.shutdown();
    }
}
