package io.left.meshenger.Services;

/**
 * Created by sachin on 24/01/18.
 */

public class Constant {
    public interface ACTION {
        public static String MAIN_ACTION ="io.left.alertdialog.action.main";

        public static String STARTFOREGROUND_ACTION = "io.left.alertdialog.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "io.left.alertdialog.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
