package com.bhash.cabtask;

import android.annotation.SuppressLint;



import java.util.Collection;
import java.util.List;


@SuppressLint({"SimpleDateFormat", "UseValueOf"})
public final class StringUtil {
    private static String TAG = "StringUtil";
    private static List value;


    private StringUtil() {
        throw new UnsupportedOperationException("Cannot construct an utility class");
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isListNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    /** @deprecated */
    public static boolean isEqual(String lhs, String rhs) {
        return Compare.isEqual(lhs, rhs);
    }

    public static boolean isEqualIgnoreCase(String lhs, String rhs) {
        return lhs == rhs || lhs != null && rhs != null && lhs.equalsIgnoreCase(rhs);
    }

    public static String formatUSD(double amount) {
        return formatUSD((new Double(amount)).doubleValue());
    }

    }




