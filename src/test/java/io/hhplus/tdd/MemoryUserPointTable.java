package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserPointTable extends UserPointTable {

	private final Map<Long, UserPoint> table = new HashMap<>();

	@Override
	public UserPoint selectById(Long id) {
		return table.getOrDefault(id, UserPoint.empty(id));
	}

	@Override
	public UserPoint insertOrUpdate(long id, long amount) {
		UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());
		table.put(id, userPoint);
		return userPoint;
	}
}