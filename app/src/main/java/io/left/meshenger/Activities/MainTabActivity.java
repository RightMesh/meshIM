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

import java.util.ArrayList;

import io.left.meshenger.Adapters.UserListAdapter;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.Services.IMeshIMService;
import io.left.meshenger.Services.MeshIMService;
import io.left.meshenger.Services.ServiceConstants;

/**
 * Main interface for MeshIM. Displays tabs for viewing online users, conversations, and the
 * user's account.
 */
public class MainTabActivity extends Activity {
    // Reference to AIDL interface of app service.
    private IMeshIMService mService = null;
    ServiceConnection connection;
    // Implementation of AIDL interface.
    private IActivity.Stub mCallback = new IActivity.Stub() {
        @Override
        public void updateInterface() throws RemoteException {
            runOnUiThread(() -> {
                mUserListAdapter.updateList(mService);
                mUserListAdapter.notifyDataSetChanged();
            });
        }
    };

    // Handles connecting to service. Registers `mCallback` with the service when the connection
    // is successful.
    private ServiceConnection mServiceConnection = new ServiceConnection() {
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

    // Adapter that populates the online user list with user information from the app service.
    UserListAdapter mUserListAdapter;
    ArrayList<User> mUsers = new ArrayList<User>();

    /**
     * Binds to service and initializes UI elements.
     * @param savedInstanceState passed by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        // Handles connecting to service. Registers `mCallback` with the service when the connection
        // is successful.
        connection = new ServiceConnection() {
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
        serviceIntent.setAction(ServiceConstants.ACTION.STARTFOREGROUND_ACTION);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);
        startService(serviceIntent);
        //function to manually toggle foreground service
        toggleForegroundServices();

        configureTabs();
        configureUserList();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    /**
     * A switch to toggle Foreground service notification on/off.
     */
    private void toggleForegroundServices(){
        Switch serviceSwitch = findViewById(R.id.serviceSwitch);
        serviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serviceSwitch.isChecked()){
                    Intent startIntent = new Intent(MainTabActivity.this,MeshIMService.class);
                    startIntent.setAction(ServiceConstants.ACTION.STARTFOREGROUND_ACTION);
                    startService(startIntent);
                }
                else {
                    Intent stopIntent = new Intent(MainTabActivity.this, MeshIMService.class);
                    stopIntent.setAction(ServiceConstants.ACTION.STOPFOREGROUND_ACTION);
                    startService(stopIntent);
                }
                }
        });
    }
}