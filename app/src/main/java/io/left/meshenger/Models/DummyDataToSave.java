package io.left.meshenger.Models;

import android.content.Context;

/**
 * Created by Sachin on 2018-01-17.
 */

public class DummyDataToSave {
    private static DummyDataToSave  ourInstance=  new DummyDataToSave();
    private  static User user;

    public void setUser(User user) {
        this.user = user;
    }
    private DummyDataToSave(){
        user = new User("user",1);
    }
    public static void getSavedInstance(Context context){
        if (SharedPrefence.getSharePreference(context) != null) {
            ourInstance = SharedPrefence.getSharePreference(context);
        } else {
            ourInstance = new DummyDataToSave();
        }
    }
    public static DummyDataToSave getCurrentInstance(){
        return ourInstance;
    }
}
