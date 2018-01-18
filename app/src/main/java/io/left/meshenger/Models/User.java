package io.left.meshenger.Models;

import android.os.Parcel;
import android.os.Parcelable;



public class User implements Parcelable {
   private String userName;
   private int userAvatar;
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
}
