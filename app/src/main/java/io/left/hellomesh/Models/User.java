package io.left.hellomesh.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sachin Raturi on 2018-01-15.
 */

public class User implements Parcelable {
   private String userName;
   private int userAvatar;

   public User(String userName, int userAvatar){
       this.userAvatar = userAvatar;
       this.userName = userName;
   }

    public int getUserAvatar() {
        return userAvatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserAvatar(int userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getUserName());
        dest.writeInt(this.userAvatar);
    }
}
