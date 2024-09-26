package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
	private final UserPointTable    userPointTable;
	private final PointHistoryTable pointHistoryTable;

	/**
	 * 포인트 충전
	 * */
	public UserPoint charge(long id, long amount) {
		if (amount < 0)
			throw new IllegalArgumentException("포인트 충전 금액이 0보다 작습니다");

		UserPoint userPoint   = userPointTable.selectById(id);
		long currentPoint     = userPoint.point();
		long updatedPoint     = currentPoint + amount;
		long updateTimeMillis = System.currentTimeMillis();

		pointHistoryTable.insert(id, updatedPoint, TransactionType.CHARGE, updateTimeMillis);

		return new UserPoint(id, updatedPoint, updateTimeMillis);
	}

	/**
	 * 포인트 사용
	 * */
	public UserPoint use(long id, long amount) {
		if (amount < 0)
			throw new IllegalArgumentException("포인트 사용 금액이 0보다 작습니다");

		UserPoint userPoint   = userPointTable.selectById(id);
		long currentPoint     = userPoint.point();

		if (currentPoint < amount)
			throw new IllegalStateException("포인트가 부족합니다");

		long updatedPoint     = currentPoint - amount;
		long updateTimeMillis = System.currentTimeMillis();

		pointHistoryTable.insert(id, updatedPoint, TransactionType.USE, updateTimeMillis);
		return new UserPoint(id, updatedPoint, updateTimeMillis);
	}

	/**
	 * 유저 포인트 조회
	 * */
	public UserPoint getPoint(long id) {
		return userPointTable.selectById(id);
	}

	/**
	 * 유저 포인트 히스토리 조회
	 * */
	public List<PointHistory> getPointHistory(long id) {
		return pointHistoryTable.selectAllByUserId(id);
	}
}
