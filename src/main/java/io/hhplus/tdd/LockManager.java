package io.hhplus.tdd;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockManager {
    private final ConcurrentHashMap<Long, Lock> lockMap = new ConcurrentHashMap<>();

    public Lock getLock(Long userId) {
        return lockMap.computeIfAbsent(userId, id -> new ReentrantLock(true));
    }
}
