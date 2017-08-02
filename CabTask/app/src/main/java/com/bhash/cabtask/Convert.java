package com.bhash.cabtask;


import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

import org.json.JSONArray;

@SuppressLint({"DefaultLocale", "SimpleDateFormat", "UseValueOf"})
public final class Convert {
    private static final String TAG = Convert.class.getSimpleName();
    public static float ROUND_THRESHOLD = 0.5F;
    public static float CEIL_THRESHOLD = 0.99F;
    @SuppressLint({"SimpleDateFormat"})
    private static final SimpleDateFormat m_asOfDateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    private Convert() {
        throw new UnsupportedOperationException("Cannot construct an utility class");
    }

    public static String toString(Object source, String defaultValue) {
        String result;
        if(source == null) {
            result = defaultValue;
        } else if(source instanceof String) {
            result = (String)source;
        } else {
            result = "" + source;
        }

        return result;
    }

    public static String toString(Object source) {
        return toString(source, "--");
    }

    public static int toInt(Object source, int defaultValue) {
        int value = defaultValue;
        if(source != null) {
            if(source instanceof Number) {
                value = ((Number)source).intValue();
            } else {
                String valueString = toString(source);

                try {
                    value = Integer.parseInt(valueString.trim());
                } catch (NumberFormatException var5) {
                    ;
                }
            }
        }

        return value;
    }

    public static int toInt(Object source) {
        return toInt(source, 0);
    }

    public static double toDouble(Object source, double defaultValue) {
        double value = defaultValue;
        if(source != null) {
            if(source instanceof Number) {
                value = ((Number)source).doubleValue();
            } else {
                String valueString = toString(source);

                try {
                    value = Double.parseDouble(valueString.trim());
                } catch (NumberFormatException var7) {
                    ;
                }
            }
        }

        return value;
    }

    public static double toDouble(Object source) {
        return toDouble(source, 0.0D);
    }

    public static JSONArray toJsonArray(int[] source, JSONArray defaultValue) {
        JSONArray result;
        if(source == null) {
            result = defaultValue;
        } else {
            result = new JSONArray();

            for(int i = 0; i < source.length; ++i) {
                result.put(source[i]);
            }
        }

        return result;
    }




    public static boolean toBoolean(Object source, boolean defaultValue) {
        boolean result;
        if(source instanceof Boolean) {
            result = ((Boolean)source).booleanValue();
        } else {
            String valueString = toString(source).trim().toLowerCase();
            result = valueString.equals("true") || valueString.equals("on") || valueString.equals("yes") || toInt(valueString) != 0;
        }

        return result;
    }

    public static boolean toBoolean(Object source) {
        return toBoolean(source, false);
    }


}
