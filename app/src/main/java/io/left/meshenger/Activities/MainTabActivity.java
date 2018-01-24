package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import io.left.meshenger.Adapters.UserListAdapter;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends Activity {

    UserListAdapter userListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        //setting up tabs
        TabHost host = (TabHost) findViewById(R.id.tabHost);
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

        sampleUserList();
        onListClick();
    }


    /**
     * Dummy function to fill the userList view with data
     */
    private void sampleUserList() {
        ListView listView = findViewById(R.id.userListView);
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            User user;
            if (i % 2 == 0) {
                user = new User("mUser " + i, R.mipmap.avatar_00);
            } else {
                user = new User("mUser " + i, R.mipmap.avatar_01);

            }
            userList.add(user);
        }
        userListAdapter = new UserListAdapter(this, userList);
        listView.setAdapter(userListAdapter);

    }

    /**
     * starts a new chat activity when user clicks on any other user in the userlist.
     */
    private void onListClick() {
        ListView listView = findViewById(R.id.userListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainTabActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }


}
