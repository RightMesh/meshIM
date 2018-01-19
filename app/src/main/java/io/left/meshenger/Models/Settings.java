package io.left.meshenger.Models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by sachin on 18/01/18.
 */

public class Settings {
    private boolean showNotification;

    // Used in shared preference to store / load data
    private final String saveVersion = "SettingSaveVersion_v1";


    public void setShowNotification(boolean showNotification) {
        this.showNotification = showNotification;
    }

    public boolean isShowNotification() {
        return showNotification;
    }
    public Settings(boolean showNotification){
        this.showNotification = showNotification;
    }

    /**
     * This functionn loads setting data if it exist
     * @param context context of the activity
     * @return true if function was able to load else false
     */
    public boolean load(Context context){
        SharedPreferences preferences = context.getSharedPreferences("app", MODE_PRIVATE);
        Gson gson = new Gson();
        String userSetting = preferences.getString(saveVersion, null);
        Type type = new TypeToken<Settings>() {}.getType();
        Settings temp = gson.fromJson(userSetting, type);
        if(temp == null){
            return false;
        }
        else {
            this.setShowNotification(temp.isShowNotification());
        }
        return true;
    }

    /**
     * saves the setting data
     * @param context context of the activity
     */
    public void save(Context context){
        Settings temp = new Settings(this.isShowNotification());
        SharedPreferences pref = context.getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gsonModel = new Gson();
        String savemodel = gsonModel.toJson(temp);
        editor.putString(saveVersion, savemodel);
        editor.commit();
    }
}
