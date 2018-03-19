
package com.bear.core.util;


public final class SystemClock implements Clock {


    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
