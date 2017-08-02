package com.bhash.cabtask;

import java.math.BigDecimal;

/**
 * Created by saurabh on 8/1/2017.
 */

public class Compare {
    public static final String TAG = Compare.class.getSimpleName();

    private Compare() {
        throw new UnsupportedOperationException("Cannot construct an utility class");
    }

    public static boolean isEqual(Object lhs, Object rhs) {
        boolean result;
        try {
            result = lhs == rhs || lhs != null && rhs != null && lhs.equals(rhs);
        } catch (ClassCastException var4) {
            result = false;
        }

        return result;
    }


}

