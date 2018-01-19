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
    private IMeshIMService mIMeshIMService = null;

    // Implementation of AIDL interface.
    private IActivity.Stub callback = new IActivity.Stub() {
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

        //protobuf sample
        /*    MessageType.createMessage sample =  MessageType.createMessage.newBuilder().setMessage("hello protobuf works").build();
            byte [] protobyte = sample.toByteArray();
            MessageType.createMessage sample2 =null;
            try {
                sample2 = MessageType.createMessage.parseFrom(protobyte);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            Toast.makeText(this,sample2.getMessage().toString(),Toast.LENGTH_SHORT).show();
            */

        // Handles connecting to service. Registers `callback` with the service when the connection
        // is successful.
        ServiceConnection connection = new ServiceConnection() {
            // Called when the connection with the service is established
            public void onServiceConnected(ComponentName className, IBinder service) {
                mIMeshIMService
                        = IMeshIMService.Stub.asInterface(service);
                try {
                    mIMeshIMService.registerMainActivityCallback(callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                appendToLog("Service has unexpectedly disconnected");
                mIMeshIMService = null;
            }
        };

        Intent serviceIntent = new Intent(this, MeshIMService.class);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        Settings settings = Settings.fromDisk(this);
        if (settings == null) {
            // Initialize settings without UI.
            settings = new Settings();
            settings.save(this);
        }

        //shared pref demo
        User user = User.fromDisk(this);
        if (user == null) {
            //load onboarding fragment.
            //this is dummy data
            user = new User();
            Toast.makeText(this,"Making new user",Toast.LENGTH_SHORT).show();
            user.save(this);
        }

        appendToLog("userID: "+ user.getUserAvatar()+"\n show notif: "+settings.isShowNotification() );

        // dummy button to test sharedpref
        Button bt=findViewById(R.id.testbttn);

        User finalUser = new User(user.getUserName(),user.getUserAvatar());
        Settings finalSettings = settings;

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changing data and saving it
                finalUser.setUserName("username");
                finalUser.setUserAvatar(finalUser.getUserAvatar()+1);
                finalUser.save(MainActivity.this);

                finalSettings.setShowNotification(!finalSettings.isShowNotification());
                finalSettings.save(MainActivity.this);

                appendToLog("userID changed to: "+ finalUser.getUserAvatar()+"\n show notif changed to: "+finalSettings.isShowNotification() );
            }

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
        if (mIMeshIMService != null) {
            mIMeshIMService.sendHello();
        }
    }

    /**
     * Calls the `sendHello` method of the app service.
     *
     * @param v Reference to button that called method.
     * @throws RemoteException If service disappears unexpectedly.
     */
    public void configure(View v) throws RemoteException {
        if (mIMeshIMService != null) {
            mIMeshIMService.configure();
        }
    }
}