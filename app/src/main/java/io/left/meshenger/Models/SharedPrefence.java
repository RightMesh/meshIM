package io.left.meshenger.Models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;


public class SharedPrefence {

    public static void storeSharePreference(Context contex) {
        SharedPreferences pref = contex.getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gsonModel = new Gson();
        String dummy = gsonModel.toJson(DummyDataToSave.getCurrentInstance());
        editor.putString("model_1", dummy);
        editor.commit();
    }

    public static DummyDataToSave getSharePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("app", MODE_PRIVATE);
        Gson gson = new Gson();
        String carbonModel = preferences.getString("model_1", null);
        Type type = new TypeToken<User>() {
        }.getType();
        return gson.fromJson(carbonModel, type);
    }
}



