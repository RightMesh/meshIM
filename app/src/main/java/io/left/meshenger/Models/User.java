package io.left.meshenger.Models;

import static android.content.Context.MODE_PRIVATE;
import static io.left.meshenger.BuildConfig.APPLICATION_ID;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.left.rightmesh.id.MeshID;

import java.lang.reflect.Type;


@Entity(tableName = "Users",
        indices = {@Index(value = {"UserID", "UserMeshID"}, unique = true)})
public class User implements Parcelable {
    //used in share preference to save or load data
    @Ignore
    private static final String SAVE_VERSION = "UserDataSaveVersion_v1";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UserID")
    public int id;

    @ColumnInfo(name = "UserMeshID")
    private MeshID meshId = null;

    @ColumnInfo(name = "UserName")
    private String userName;

    @ColumnInfo(name = "UserAvatar")
    private int userAvatar;

    // SharedPreferences is a singleton - the same reference is always returned. It also updates
    // itself in a threadsafe way, so might as well keep one version of it open.
    // The transient qualifier makes Gson ignore it for serialization.
    @Ignore
    private transient SharedPreferences preferences;

    public User() {
        this("Anonymous", -1);
    }

    /**
     * Returns a User object that can be used to store users nearby.
     * @param userName is the user name of the User
     *                 does not need to be unique
     * @param userAvatar is the Avatar chosen by the user
     */
    @Ignore
    public User(String userName, int userAvatar) {
        this.userAvatar = userAvatar;
        this.userName = userName;
    }

    @Ignore
    public User(Context context) {
        this();
        preferences = context.getSharedPreferences(APPLICATION_ID, MODE_PRIVATE);
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

    public MeshID getMeshId() {
        return meshId;
    }

    public void setMeshId(MeshID meshId) {
        this.meshId = meshId;
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

    /**
     * A constructor for the parcel data type.
     *
     * <p>
     *     Extracts username and uyserAvatar from parcel data type
     * </p>
     * @param in parel to parse
     */
    private User(Parcel in) {
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
     * flatten the object in parcel.
     * @param dest needed by Parcelable
     * @param flags needed by Parcelable
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getUserName());
        dest.writeInt(this.userAvatar);
    }

    /**
     * This function loads setting data if it exist.
     * @return true if function was able to load else false
     */
    public boolean load() {
        try {
            Gson gson = new Gson();
            String user = preferences.getString(SAVE_VERSION, null);
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
            // If preferences is null, we can't load anything.
            return false;
        }
    }

    /**
     * This function loads setting data if it exist.
     */
    public void save() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            Gson gsonModel = new Gson();
            String savemodel = gsonModel.toJson(this);
            editor.putString(SAVE_VERSION, savemodel);
            editor.commit();
        } catch (NullPointerException ignored) {
            // In case preferences is null.
        }
    }
}
