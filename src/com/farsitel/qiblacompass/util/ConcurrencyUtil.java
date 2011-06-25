/*
 * Copyright (C) 2011 Iranian Supreme Council of ICT, The FarsiTel Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASICS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
