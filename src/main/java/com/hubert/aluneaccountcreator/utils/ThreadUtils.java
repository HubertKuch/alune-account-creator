package com.hubert.aluneaccountcreator.utils;

public class ThreadUtils {
    public static void delay(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
