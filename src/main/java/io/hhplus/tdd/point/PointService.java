package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
	private final UserPointTable    userPointTable;

	/**
	 * 포인트 충전
	 * */
	public UserPoint chargePoint(long id, long amount) {
		if (amount < 0)
			throw new IllegalArgumentException("포인트 충전 금액이 0보다 작습니다");

		UserPoint userPoint   = userPointTable.selectById(id);
		long currentPoint     = userPoint.point();
		long updatedPoint     = currentPoint + amount;

		return new UserPoint(id, updatedPoint, System.currentTimeMillis());
	}

	/**
	 * 포인트 사용
	 * */
	public UserPoint usePoint(long id, long amount) {
		if (amount < 0)
			throw new IllegalArgumentException("포인트 사용 금액이 0보다 작습니다");

		UserPoint userPoint   = userPointTable.selectById(id);
		long currentPoint     = userPoint.point();

		if (currentPoint < amount)
			throw new IllegalStateException("포인트가 부족합니다");

		long updatedPoint     = currentPoint - amount;

		return new UserPoint(id, updatedPoint, System.currentTimeMillis());
	}

	/**
	 * 유저 포인트 조회
	 * */
	public UserPoint selectUserPoint(long id) { return userPointTable.selectById(id); }
}
