package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.left.meshenger.Models.Settings;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.Services.IMeshIMService;
import io.left.meshenger.Services.MeshIMService;

public class MainActivity extends Activity {
    // Reference to AIDL interface of app service.
    private IMeshIMService mService = null;

    // Implementation of AIDL interface.
    private IActivity.Stub mCallback = new IActivity.Stub() {
        /**
         * A lazy helper method that dumps text to the log TextView on the screen.
         *
         * @param message Text to be dumped to the log.
         * @throws RemoteException If service disappears unexpectedly.
         */
        @Override
        public void echo(String message) throws RemoteException {
            appendToLog(message);
        }

        /**
         * Called when RightMesh has initialized, enables the buttons.
         *
         * @throws RemoteException If service disappears unexpectedly.
         */
        @Override
        public void updateInterface() throws RemoteException {
            runOnUiThread(() -> {
                findViewById(R.id.btnHello).setEnabled(true);
                findViewById(R.id.btnConfigure).setEnabled(true);
            });
        }
    };

    /**
     * Initializes UI and starts/binds to app service.
     *
     * @param savedInstanceState Passed by Android.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load settings, or initialize them before starting service.
        Settings settings = Settings.fromDisk(this);
        if (settings == null) {
            // Initialize settings without UI.
            settings = new Settings();
            settings.save(this);
        }

        // Load user, or launch onboarding activity to initialize before starting service.
        User user = User.fromDisk(this);
        if (user == null) {
            //load onboarding fragment.
            //this is dummy data
            user = new User(this);
            Toast.makeText(this,"Making new user",Toast.LENGTH_SHORT).show();
            user.save();
        }

        // Handles connecting to service. Registers `mCallback` with the service when the connection
        // is successful.
        ServiceConnection connection = new ServiceConnection() {
            // Called when the connection with the service is established
            public void onServiceConnected(ComponentName className, IBinder service) {
                mService
                        = IMeshIMService.Stub.asInterface(service);
                try {
                    mService.registerMainActivityCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                appendToLog("Service has unexpectedly disconnected");
                mService = null;
            }
        };

        Intent serviceIntent = new Intent(this, MeshIMService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);


        // dummy button to test sharedpref
        Button bt = findViewById(R.id.testbttn);
        User finalUser = new User(user.getUserName(),user.getUserAvatar());
        Settings finalSettings = settings;
        bt.setOnClickListener(v -> {
            //changing data and saving it
            finalUser.setUserName("username");
            finalUser.setUserAvatar(finalUser.getUserAvatar() + 1);
            finalUser.save();

            finalSettings.setShowNotifications(!finalSettings.isShowNotifications());
            finalSettings.save(MainActivity.this);

            appendToLog("userID changed to: " + finalUser.getUserAvatar()
                    + "\n show notif changed to: " + finalSettings.isShowNotifications());
        });
    }

    /**
     * Helper method that appends a string to the log TextView.
     *
     * @param message Message to append.
     */
    private void appendToLog(final String message) {
        runOnUiThread(() -> {
            TextView txtLog = findViewById(R.id.txtLog);
            txtLog.append(message + "\n");
        });
    }

    /**
     * Calls the `sendHello` method of the app service.
     *
     * @param v Reference to button that called method.
     * @throws RemoteException If service disappears unexpectedly.
     */
    public void sendHello(View v) throws RemoteException {
        if (mService != null) {
            mService.sendHello();
        }
    }

    /**
     * Calls the `sendHello` method of the app service.
     *
     * @param v Reference to button that called method.
     * @throws RemoteException If service disappears unexpectedly.
     */
    public void configure(View v) throws RemoteException {
        if (mService != null) {
            mService.configure();
        }
    }
}