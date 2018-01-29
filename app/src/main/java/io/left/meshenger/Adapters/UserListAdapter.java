package io.left.meshenger.Adapters;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.Services.IMeshIMService;

import java.util.ArrayList;

/**
 * Adapter that fetches online users from the app service to populat the list of online users in
 * {@link io.left.meshenger.Activities.MainTabActivity}.
 */
public class UserListAdapter extends ArrayAdapter<User> {
    // Used to inflate views for the list.
    private Context mContext;

    // Reference to user list. Used for updating from service.
    private ArrayList<User> mUserList;

    /**
     * Stores context so we can inflate views.
     * @param context context of activity
     * @param userList list to manage
     */
    public UserListAdapter(Context context, ArrayList<User> userList) {
        super(context, R.layout.user_list, userList);
        this.mContext = context;
        this.mUserList = userList;
    }

    /**
     * Updates the list of users.
     * @param service service connection to fetch users from
     */
    public void updateList(IMeshIMService service) {
        try {
            this.clear();
            this.addAll(service.getOnlineUsers());
        } catch (RemoteException ignored) { /* Leave the list untouched on failure. */ }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)  {
        View v = View.inflate(mContext, R.layout.user_list, null);

        // Null-check the user at this position.
        User user = this.getItem(position);
        if (user != null) {
            ImageView userAvatar = v.findViewById(R.id.user_Avatar);
            userAvatar.setImageResource(user.getUserAvatar());
            TextView userName = v.findViewById(R.id.userNameText);
            userName.setText(user.getUserName());
        }

        return v;
    }
}