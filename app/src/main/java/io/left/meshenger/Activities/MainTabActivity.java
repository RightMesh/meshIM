package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import io.left.meshenger.Adapters.UserListAdapter;
import io.left.meshenger.Adapters.UserMessageListAdapter;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.Services.IMeshIMService;
import io.left.meshenger.Services.MeshIMService;

import java.util.ArrayList;

/**
 * Main interface for MeshIM. Displays tabs for viewing online users, conversations, and the
 * user's account.
 */
public class MainTabActivity extends Activity {
    // Reference to AIDL interface of app service.
    private IMeshIMService mService = null;

    // Handles connecting to service. Registers `mCallback` with the service when the
    // mConnection is successful.
    ServiceConnection mConnection = new ServiceConnection() {
        // Called when the mConnection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IMeshIMService.Stub.asInterface(service);
            try {
                mService.registerMainActivityCallback(mCallback);
                mService.setForeground(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        // Called when the mConnection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    // Implementation of AIDL interface.
    private IActivity.Stub mCallback = new IActivity.Stub() {
        @Override
        public void updateInterface() throws RemoteException {
            runOnUiThread(() -> {
                if (mService != null) {
                    mUserListAdapter.updateList(mService);
                    mUserListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    // Adapter that populates the online user list with user information from the app service.
    UserListAdapter mUserListAdapter;
    ArrayList<User> mUsers = new ArrayList<>();
    UserMessageListAdapter mUserMessageListAdapter;

    /**
     * Binds to service and initializes UI elements.
     * @param savedInstanceState passed by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        configureTabs();
        configureUserList();
        connectToService();
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
            startActivity(intent);
        });
    }
    private void configureMessageList(){
        ArrayList<User> u = new ArrayList<>();
        u.add(new User("user1",R.mipmap.avatar1));
        u.add(new User("user2",R.mipmap.avatar2));
        u.add(new User("user3",R.mipmap.avatar3));

        ListView listView = findViewById(R.id.multiUserMessageListView);
        mUserMessageListAdapter = new UserMessageListAdapter(this,u);
        listView.setAdapter(mUserMessageListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainTabActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Disconnect from service when activity stops.
     */
    @Override
    protected void onStop() {
        super.onStop();
        disconnectFromService();
    }

    /**
     * Reconnect to service when activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        connectToService();
    }

    /**
     * Disconnect from service when activity isn't active on screen.
     */
    @Override
    protected void onPause() {
        super.onPause();
        disconnectFromService();
    }

    /**
     * Handle creating the service intent and binding to it in a reusable function.
     */
    private void connectToService() {
        Intent serviceIntent = new Intent(this, MeshIMService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    /**
     * Unbinds from app service and sets {@link this#mService} to null.
     */
    private void disconnectFromService() {
        if (mService != null) {
            try {
                mService.setForeground(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(mConnection);
            mService = null;
        }
    }
}