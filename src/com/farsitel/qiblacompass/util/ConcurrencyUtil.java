package com.farsitel.qiblacompass.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrencyUtil {
    public static final ReentrantReadWriteLock directionChangedLock = new ReentrantReadWriteLock(
            true);
    private static final AtomicInteger numAnimationOnRun = new AtomicInteger(0);

    public static final int incrementAnimation() {
        return numAnimationOnRun.incrementAndGet();
    }

    public static void setToZero() {
        numAnimationOnRun.set(0);
    }

    public static final int decrementAnimation() {
        return numAnimationOnRun.decrementAndGet();
    }

    public static final boolean isAnyAnimationOnRun() {
        return (numAnimationOnRun.get() > 0);
    }

    public static final int getNumAimationsOnRun() {
        return numAnimationOnRun.get();
    }

}
