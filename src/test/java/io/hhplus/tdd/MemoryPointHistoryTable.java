package io.hhplus.tdd;


import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MemoryPointHistoryTable extends PointHistoryTable {
    private final List<PointHistory> table = new ArrayList<>();
    private long cursor = 1;

    @Override
    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis) {
        PointHistory pointHistory = new PointHistory(cursor++, userId, amount, type, updateMillis);
        table.add(pointHistory);
        return pointHistory;
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return table.stream().filter(pointHistory -> pointHistory.userId() == userId).toList();
    }
}
