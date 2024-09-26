package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointServiceTest {
    private PointService        pointService;
    private UserPointTable      userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable      = new MemoryUserPointTable();
        pointService        = new PointService(userPointTable, new LockManager());
    }

    @Test
    void 포인트_충전_성공() {
        // Given
        userPointTable.insertOrUpdate(1L, 500L);

        // When
        UserPoint updatedUserPoint = pointService.charge(1L, 100L);

        // Then
        assertEquals(600L, updatedUserPoint.point());
        assertEquals(600L, userPointTable.selectById(1L).point());
    }

    @Test
    void 포인트_충전금액이_1보다_작으면_예외() {
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(1L, 0);
        });
    }

    @Test
    void 포인트_사용_성공() {
        // Given
        userPointTable.insertOrUpdate(1L, 500L);

        // When
        UserPoint updatedUserPoint = pointService.use(1L, 100L);

        // Then
        assertEquals(400L, updatedUserPoint.point());
        assertEquals(400L, userPointTable.selectById(1L).point());
    }

    @Test
    void 포인트_사용금액이_1보다_작으면_예외() {
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.use(1L, 0);
        });
    }

    @Test
    void 포인트_사용금액이_잔여포인트_보다_크면_예외() {
        //Given
        userPointTable.insertOrUpdate(1L, 100L);

        // When, Then
        assertThrows(IllegalStateException.class, () -> {
            pointService.use(1L, 200L);
        });
    }
}
