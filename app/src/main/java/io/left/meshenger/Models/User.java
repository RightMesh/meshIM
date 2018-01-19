package io.left.meshenger.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;


public class User implements Parcelable {
   private String userName;
   private int userAvatar;

   //used in share preference to save or load data
   private final String saveVersion = "UserDataSaveVersion_v1";

   /**
     * Returns a User object that can be used to store users nearby
     * @param userName is the user name of the User
     *                 does not need to be unique
     * @param userAvatar is the Avatar chosen by the user
     */
   public User(String userName, int userAvatar){
       this.userAvatar = userAvatar;
       this.userName = userName;

   }

    /**
     * Default constructor
     */
   public User(){
       this.userName= "UserName";
       this.userAvatar = 1;
   }

    /**
     * get the id of the user's avatar
     * @return an int
     */
    public int getUserAvatar() {

        return userAvatar;
    }

    /**
     * get the username of the user
     * @return username in a string format
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Takes in the id for userAvatar in the int format
     * sets the user avatar to the new avatar
     * @param userAvatar
     */
    public void setUserAvatar(int userAvatar) {
        this.userAvatar = userAvatar;
    }

    /**
     * set the username of the User to the paramter
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * A constructor for the parcel data type
     * @param in
     * extracts username and uyserAvatar from parcel data type
     */
    protected User(Parcel in) {
        this.userName = in.readString();
        this.userAvatar = in.readInt();
    }



    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * flatten the object in parcel
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getUserName());
        dest.writeInt(this.userAvatar);
    }

    /**
     * This functionn loads setting data if it exist
     * @param context context of the activity
     * @return true if function was able to load else false
     */
    public boolean load(Context context){
        SharedPreferences preferences = context.getSharedPreferences("app", MODE_PRIVATE);
        Gson gson = new Gson();
        String user = preferences.getString(saveVersion, null);
        Type type = new TypeToken<User>() {}.getType();
        User temp = gson.fromJson(user, type);
        if(temp == null){
            return false;
        }
        else {
            this.setUserAvatar(temp.getUserAvatar());
            this.setUserName(temp.getUserName());
        }
        return true;
    }

    /**
     * This functionn loads setting data if it exist
     * @param context context of the activity
     * @return true if function was able to load else false

     */
    public void save(Context context){
        User temp = new User(this.getUserName(),this.getUserAvatar());

        SharedPreferences pref = context.getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gsonModel = new Gson();
        String savemodel = gsonModel.toJson(temp);
        editor.putString(saveVersion, savemodel);
        editor.commit();
    }
}
