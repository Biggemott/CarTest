package com.biggemott.cartest.utils;

import com.biggemott.cartest.BuildConfig;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("LogTagMismatch")
public class L {

    private static final String MAIN_TAG = "CarTest";

    private static boolean isLoggable(int level) {
        return Log.isLoggable(MAIN_TAG, level) || BuildConfig.DEBUG;
    }

    private static String format(String tag, String msg) {
        return String.format("%s: %s", tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isLoggable(Log.VERBOSE)) Log.v(MAIN_TAG, format(tag, msg));
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (isLoggable(Log.VERBOSE)) Log.v(MAIN_TAG, format(tag, msg), tr);
    }

    public static void d(String tag, String msg) {
        if (isLoggable(Log.DEBUG)) Log.d(MAIN_TAG, format(tag, msg));
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isLoggable(Log.DEBUG)) Log.d(MAIN_TAG, format(tag, msg), tr);
    }

    public static void i(String tag, String msg) {
        if (isLoggable(Log.INFO)) Log.i(MAIN_TAG, format(tag, msg));
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (isLoggable(Log.INFO)) Log.i(MAIN_TAG, format(tag, msg), tr);
    }

    public static void w(String tag, String msg) {
        if (isLoggable(Log.WARN)) Log.w(MAIN_TAG, format(tag, msg));
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (isLoggable(Log.WARN)) Log.w(MAIN_TAG,format(tag, msg), tr);
    }

    public static void w(String tag, Throwable tr) {
        if (isLoggable(Log.WARN)) Log.w(MAIN_TAG, tag, tr);
    }

    public static void e(String tag, String msg) {
        if (isLoggable(Log.ERROR)) Log.e(MAIN_TAG, format(tag, msg));
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (isLoggable(Log.ERROR)) Log.e(MAIN_TAG, format(tag, msg), tr);
    }
}
