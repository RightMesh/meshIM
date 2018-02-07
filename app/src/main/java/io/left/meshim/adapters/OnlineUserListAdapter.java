package io.left.meshim.adapters;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.left.meshim.R;
import io.left.meshim.activities.MainActivity;
import io.left.meshim.models.User;
import io.left.meshim.services.IMeshIMService;

import java.util.ArrayList;

/**
 * Adapter that fetches online users from the app service to populate the list of online users in
 * {@link MainActivity}.
 */
public class OnlineUserListAdapter extends ArrayAdapter<User> {
    // Used to inflate views for the list.
    private Context mContext;

    // Reference to user list. Used for updating from service.
    private ArrayList<User> mUserList;

    /**
     * Stores context so we can inflate views.
     * @param context context of activity
     * @param userList list to manage
     */
    public OnlineUserListAdapter(Context context, ArrayList<User> userList) {
        super(context, R.layout.online_user_list_item, userList);
        this.mContext = context;
        this.mUserList = userList;
    }

    /**
     * Updates the list of users.
     * @param service service connection to fetch users from
     */
    public void updateList(IMeshIMService service) {
        if (service == null) {
            return;
        }
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
        View v = View.inflate(mContext, R.layout.online_user_list_item, null);

        // Null-check the user at this position.
        User user = this.getItem(position);
        if (user != null) {
            ImageView userAvatar = v.findViewById(R.id.userMessageAvatar);
            userAvatar.setImageResource(user.getAvatar());
            TextView userName = v.findViewById(R.id.userNameMessageText);
            userName.setText(user.getUsername());
        }

        return v;
    }
}