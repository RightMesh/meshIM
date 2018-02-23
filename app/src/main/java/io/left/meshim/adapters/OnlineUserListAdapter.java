package io.left.meshim.adapters;

import android.content.Context;
import android.os.DeadObjectException;
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
import java.util.List;

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
        super(context, R.layout.list_item_online_user, userList);
        this.mContext = context;
        this.mUserList = userList;
    }

    /**
     * Updates the list of users.
     * @param service service connection to fetch users from
     * @throws DeadObjectException if service connection dies unexpectedly
     */
    public void updateList(IMeshIMService service) throws DeadObjectException {
        if (service == null) {
            return;
        }

        try {
            List<User> query = service.getOnlineUsers();
            this.clear();
            this.addAll(query);
        } catch (RemoteException e) {
            // If the connection has died, propagate error, otherwise ignore it.
            if (e instanceof DeadObjectException) {
                throw (DeadObjectException) e;
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)  {
        View v = View.inflate(mContext, R.layout.list_item_online_user, null);

        // Null-check the user at this position.
        User user = this.getItem(position);
        if (user != null) {
            ImageView userAvatar = v.findViewById(R.id.conversation_avatar);
            userAvatar.setImageResource(user.getAvatar());
            TextView userName = v.findViewById(R.id.conversation_username);
            userName.setText(user.getUsername());
        }

        return v;
    }
}