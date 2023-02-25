package com.hubert.glevia2accountcreator.utils;

public class ThreadUtils {
    public static void delay(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
