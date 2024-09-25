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

	//유저 포인트 조회
	public UserPoint getPointById(long id) {
		return userPointTable.selectById(id);
	}

	//유저 포인트 히스토리 조회
	public List<PointHistory> getPointHistoryById(long id) {
		return pointHistoryTable.selectAllByUserId(id);
	}
}
