package io.left.meshim.models;

import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_PRIVATE;
import static io.left.meshim.BuildConfig.APPLICATION_ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Object that stores application settings.
 */
public class Settings {
    // Used in shared preference to store / load data
    private static final String SAVE_VERSION = "SettingSaveVersion_v1";

    // Needed for older phones in order for SharedPreferences to cross the IPC barrier.
    private static final int SAVE_MODE
            = (Build.VERSION.SDK_INT > 23) ? MODE_PRIVATE : MODE_MULTI_PROCESS;

    private boolean showNotifications;

    public void setShowNotifications(boolean showNotifications) {
        this.showNotifications = showNotifications;
    }

    public boolean isShowNotifications() {
        return showNotifications;
    }

    public Settings() {
        this(true);
    }

    public Settings(boolean showNotification) {
        this.showNotifications = showNotification;
    }

    /**
     * Attempts to load the stored {@link Settings} from {@link SharedPreferences}.
     *
     * @param context to load {@link SharedPreferences} from.
     * @return instance loaded from disk, or null
     */
    public static Settings fromDisk(Context context) {
        Settings temp = new Settings();
        if (!temp.load(context)) {
            return null;
        }
        return temp;
    }

    /**
     * Loads settings data from shared preferences if it exist.
     *
     * @param context context to load shared preferences with
     * @return true if function was able to load else false
     */
    public boolean load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APPLICATION_ID, SAVE_MODE);
        Gson gson = new Gson();
        String userSetting = preferences.getString(SAVE_VERSION, null);
        Type type = new TypeToken<Settings>(){}.getType();
        Settings temp = gson.fromJson(userSetting, type);
        if (temp == null) {
            return false;
        } else {
            this.setShowNotifications(temp.isShowNotifications());
        }
        return true;
    }

    /**
     * Saves the setting data to shared preferences.
     *
     * @param context context to load shared preferences with
     */
    public void save(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APPLICATION_ID, SAVE_MODE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gsonModel = new Gson();
        String savemodel = gsonModel.toJson(this);
        editor.putString(SAVE_VERSION, savemodel);
        editor.commit();
    }
}