package io.left.meshenger.Models;

import static android.content.Context.MODE_PRIVATE;
import static io.left.meshenger.BuildConfig.APPLICATION_ID;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Settings {
    // Used in shared preference to store / load data
    private final String SAVE_VERSION = "SettingSaveVersion_v1";

    private boolean mShowNotification;

    public void setmShowNotification(boolean showNotification) {
        this.mShowNotification = showNotification;
    }

    public boolean ismShowNotification() {
        return mShowNotification;
    }

    public Settings() {
        this(true);
    }

    public Settings(boolean showNotification) {
        this.mShowNotification = showNotification;
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
     * This function loads setting data if it exist.
     *
     * @param context context of the activity
     * @return true if function was able to load else false
     */
    public boolean load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APPLICATION_ID, MODE_PRIVATE);
        Gson gson = new Gson();
        String userSetting = preferences.getString(SAVE_VERSION, null);
        Type type = new TypeToken<Settings>() {
        }.getType();
        Settings temp = gson.fromJson(userSetting, type);
        if (temp == null) {
            return false;
        } else {
            this.setmShowNotification(temp.ismShowNotification());
        }
        return true;
    }

    /**
     * saves the setting data.
     *
     * @param context context of the activity
     */
    public void save(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gsonModel = new Gson();
        String savemodel = gsonModel.toJson(this);
        editor.putString(SAVE_VERSION, savemodel);
        editor.commit();
    }
}