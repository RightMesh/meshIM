package io.left.meshenger.Models;

import static android.content.Context.MODE_PRIVATE;
import static io.left.meshenger.BuildConfig.APPLICATION_ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


public class User implements Parcelable {
    //used in share preference to save or load data
    private final String SAVE_VERSION = "UserDataSaveVersion_v1";

    private String mUserName;
    private int mUserAvatar;

    // SharedPreferences is a singleton - the same reference is always returned. It also updates
    // itself in a threadsafe way, so might as well keep one version of it open.
    // The transient qualifier makes Gson ignore it for serialization.
    private transient SharedPreferences mPreferences;

    public User() {
        this("Anonymous", -1);
    }

    /**
     * Returns a User object that can be used to store users nearby.
     * @param userName is the user name of the User
     *                 does not need to be unique
     * @param userAvatar is the Avatar chosen by the user
     */
    public User(String userName, int userAvatar) {
        this.mUserAvatar = userAvatar;
        this.mUserName = userName;
    }

    public User(Context context) {
        this();
        mPreferences = context.getSharedPreferences(APPLICATION_ID, MODE_PRIVATE);
    }

    /**
     * Attempts to load the stored {@link User} from {@link SharedPreferences}.
     *
     * @param context to load {@link SharedPreferences} from.
     * @return instance loaded from disk, or null
     */
    public static User fromDisk(Context context) {
        User temp = new User(context);
        if (!temp.load()) {
            return null;
        }
        return temp;
    }

    public int getUserAvatar() {

        return mUserAvatar;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserAvatar(int userAvatar) {
        this.mUserAvatar = userAvatar;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    /**
     * A constructor for the parcel data type.
     *
     * <p>
     *     Extracts username and uyserAvatar from parcel data type
     * </p>
     * @param in parel to parse
     */
    private User(Parcel in) {
        this.mUserName = in.readString();
        this.mUserAvatar = in.readInt();
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
     * flatten the object in parcel.
     * @param dest needed by Parcelable
     * @param flags needed by Parcelable
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getUserName());
        dest.writeInt(this.mUserAvatar);
    }

    /**
     * This function loads setting data if it exist.
     * @return true if function was able to load else false
     */
    public boolean load() {
        try {
            Gson gson = new Gson();
            String user = mPreferences.getString(SAVE_VERSION, null);
            Type type = new TypeToken<User>() {
            }.getType();
            User temp = gson.fromJson(user, type);
            if (temp == null) {
                return false;
            } else {
                this.setUserAvatar(temp.getUserAvatar());
                this.setUserName(temp.getUserName());
            }
            return true;
        } catch (NullPointerException npe) {
            // If mPreferences is null, we can't load anything.
            return false;
        }
    }

    /**
     * This function loads setting data if it exist.
     */
    public void save() {
        try {
            SharedPreferences.Editor editor = mPreferences.edit();
            Gson gsonModel = new Gson();
            String savemodel = gsonModel.toJson(this);
            editor.putString(SAVE_VERSION, savemodel);
            editor.commit();
        } catch (NullPointerException ignored) {
            // In case mPreferences is null.
        }
    }
}
