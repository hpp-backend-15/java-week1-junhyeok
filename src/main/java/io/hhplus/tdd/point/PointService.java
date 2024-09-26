package io.hhplus.tdd.point;

import io.hhplus.tdd.LockManager;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
public class PointService {
	private final UserPointTable userPointTable;
	private final LockManager lockManager;

	/**
	 * 포인트 충전
	 * */
	public UserPoint charge(long id, long amount) {
		Lock lock = lockManager.getLock(id);
		lock.lock();
		try {
			if (amount < 1)
				throw new IllegalArgumentException("포인트 충전 금액은 1 이상이어야 합니다");

			UserPoint userPoint   = userPointTable.selectById(id);
			long currentPoint     = userPoint.point();
			long updatedPoint     = currentPoint + amount;

			return userPointTable.insertOrUpdate(id, updatedPoint);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 포인트 사용
	 * */
	public UserPoint use(long id, long amount) {
		Lock lock = lockManager.getLock(id);
		lock.lock();
		try {
			if (amount < 1)
				throw new IllegalArgumentException("포인트 사용 금액은 1 이상이어야 합니다");

			UserPoint userPoint = userPointTable.selectById(id);
			long currentPoint = userPoint.point();

			if (currentPoint < amount)
				throw new IllegalStateException("사용 가능한 포인트가 부족합니다");

			long updatedPoint = currentPoint - amount;

			return userPointTable.insertOrUpdate(id, updatedPoint);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 유저 포인트 조회
	 * */
	public UserPoint selectPoint(long id) { return userPointTable.selectById(id); }
}
