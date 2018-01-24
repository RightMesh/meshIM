package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import io.left.meshenger.Adapters.UserListAdapter;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.Services.IMeshIMService;
import io.left.meshenger.Services.MeshIMService;
import io.left.rightmesh.util.MeshUtility;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends Activity {
    // Reference to AIDL interface of app service.
    private IMeshIMService mService = null;

    // Implementation of AIDL interface.
    private IActivity.Stub mCallback = new IActivity.Stub() {
        @Override
        public void updateInterface() throws RemoteException {
        }
    };

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

        // Handles connecting to service. Registers `mCallback` with the service when the connection
        // is successful.
        ServiceConnection connection = new ServiceConnection() {
            // Called when the connection with the service is established
            public void onServiceConnected(ComponentName className, IBinder service) {
                mService = IMeshIMService.Stub.asInterface(service);
                try {
                    mService.registerMainActivityCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                mService = null;
            }
        };

        Intent serviceIntent = new Intent(this, MeshIMService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);
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
