package io.left.meshenger.saveData;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.left.meshenger.Models.User;

import static android.content.Context.MODE_PRIVATE;


public class SharedPrefence {

    /**
     * used to store the User class in Share preference
     * @param contex the context of the activity calling it
     * @param user the User class need to be saved
     */
    public static void storeUserData(Context contex, User user) {
        SharedPreferences pref = contex.getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gsonModel = new Gson();
        String savemodel = gsonModel.toJson(user);
        editor.putString("model", savemodel);
        editor.commit();
    }

    /**
     * used to access the User classed saved in Sharepreference
     * @param context Context of the activity calling it
     * @return returns User class
     */
    public static User getUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("app", MODE_PRIVATE);
        Gson gson = new Gson();
        String user = preferences.getString("model", null);
        Type type = new TypeToken<User>() {
        }.getType();
        return gson.fromJson(user, type);
    }
}



