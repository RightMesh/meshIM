package io.left.meshenger.Adapters;

import android.content.Context;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.Services.IMeshIMService;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter that fetches online users from the app service to populat the list of online users in
 * {@link io.left.meshenger.Activities.MainTabActivity}.
 */
public class UserListAdapter extends BaseAdapter {
    // Used to inflate views for the list.
    private Context mContext;

    // List to be adapted.
    private List<User> mUserList = new ArrayList<>();

    /**
     * Stores context so we can inflate views.
     * @param context context of activity
     */
    public UserListAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * Updates the list of users.
     * @param service service connection to fetch users from
     */
    public void updateList(IMeshIMService service) {
        try {
            this.mUserList =  service.getOnlineUsers();
        } catch (RemoteException ignored) { /* Leave the list untouched on failure. */ }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getCount() {
        return mUserList.size();
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public Object getItem(int i) {
        return mUserList.get(i);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.user_list, null);
        ImageView userAvatar = v.findViewById(R.id.user_Avatar);
        userAvatar.setImageResource(R.mipmap.avatar_00);
        TextView userName = v.findViewById(R.id.userNameText);
        userName.setText(mUserList.get(i).getUserName());
        return v;
    }
}