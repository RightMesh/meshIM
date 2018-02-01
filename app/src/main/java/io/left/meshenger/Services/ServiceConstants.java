package io.left.meshenger.Services;

/**
 * Holds all the constants for Foreground Service
 */

public class ServiceConstants {
    public interface ACTION {
        public static String STARTFOREGROUND_ACTION = "io.left.alertdialog.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "io.left.alertdialog.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
