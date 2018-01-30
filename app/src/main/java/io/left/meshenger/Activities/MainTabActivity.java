package io.left.meshenger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TabHost;

import io.left.meshenger.Adapters.UserListAdapter;
import io.left.meshenger.Adapters.UserMessageListAdapter;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;

import java.util.ArrayList;

/**
 * Main interface for MeshIM. Displays tabs for viewing online users, conversations, and the
 * user's account.
 */
public class MainTabActivity extends ServiceConnectedActivity {
    // Adapter that populates the online user list with user information from the app service.
    UserListAdapter mUserListAdapter;
    ArrayList<User> mUsers = new ArrayList<>();
    UserMessageListAdapter mUserMessageListAdapter;

    /**
     * Initializes UI elements.
     * @param savedInstanceState passed by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        configureTabs();
        configureUserList();
        configureMessageList();
    }

    /**
     * Configure the content and UI of the tabs.
     */
    private void configureTabs() {
        TabHost host = findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("In Range");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Messages");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Account");
        host.addTab(spec);
    }

    /**
     * Configure the user list adapter and click event.
     */
    private void configureUserList() {
        ListView listView = findViewById(R.id.userListView);
        mUserListAdapter = new UserListAdapter(this, mUsers);
        listView.setAdapter(mUserListAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainTabActivity.this, ChatActivity.class);
            intent.putExtra("user", mUserListAdapter.getItem(position));
            startActivity(intent);
        });
    }

    private void configureMessageList() {
        ArrayList<User> u = new ArrayList<>();
        u.add(new User("user1",R.mipmap.avatar1));
        u.add(new User("user2",R.mipmap.avatar2));
        u.add(new User("user3",R.mipmap.avatar3));

        ListView listView = findViewById(R.id.multiUserMessageListView);
        mUserMessageListAdapter = new UserMessageListAdapter(this,u);
        listView.setAdapter(mUserMessageListAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainTabActivity.this,ChatActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void updateInterface() {
        runOnUiThread(() -> {
            if (mService != null) {
                mUserListAdapter.updateList(mService);
                mUserListAdapter.notifyDataSetChanged();
            }
        });
    }
}