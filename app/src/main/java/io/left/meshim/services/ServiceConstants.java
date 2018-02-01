package io.left.meshim.services;

/**
 * Holds all the constants for Foreground Service.
 */
class ServiceConstants {
    public interface ACTION {
        String STARTFOREGROUND_ACTION = "io.left.alertdialog.action.startforeground";
        String STOPFOREGROUND_ACTION = "io.left.alertdialog.action.stopforeground";
    }

    public interface NOTIFICATION_ID { //SUPPRESS CHECKSTYLE TypeNameCheck
        int FOREGROUND_SERVICE = 101;
    }
}
