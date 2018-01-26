package io.left.meshenger.Services;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Activities.MainTabActivity;
import io.left.meshenger.Database.MeshIMDatabase;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.RightMeshConnectionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles service and lifecycle management. Defers RightMesh operations to
 * {@link RightMeshConnectionHandler}.
 */
public class MeshIMService extends Service {
    private MeshIMDatabase mDatabase;
    private RightMeshConnectionHandler mMeshConnection;
    private Notification mServiceNotification;
    /**
     * Connects to RightMesh when service is started.
     */

    @Override
    public void onCreate() {
        super.onCreate();
        User user = User.fromDisk(this);

        mDatabase = Room.databaseBuilder(getApplicationContext(), MeshIMDatabase.class, "MeshIM")
                .build();

        mMeshConnection = new RightMeshConnectionHandler(user, mDatabase);
        mMeshConnection.connect(this);
    }

    /**
     * Disconnects from RightMesh when service is stopped.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMeshConnection.disconnect();
    }

    /**
     * Implementation of AIDL interface. As most of these calls are for mesh operations, most of
     * them just call a method in {@link MeshIMService#mMeshConnection}.
     */
    private final IMeshIMService.Stub mBinder = new IMeshIMService.Stub() {
        @Override
        public void send(String message) {
            // Nothing for now.
        }

        @Override
        public void registerMainActivityCallback(IActivity callback) {
            mMeshConnection.setCallback(callback);
        }

        @Override
        public List<User> getOnlineUsers() {
            return new ArrayList<>(mMeshConnection.getUserList());
        }
    };

    /**
     * Return reference to AIDL stub.
     *
     * @param intent Intent that started service.
     * @return AIDL stub.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Helps turn the foreground service on and off.
     * @param intent service intent.
     * @param flags extra flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ServiceConstants.ACTION.STOPFOREGROUND_ACTION)) {
            Toast.makeText(this,"Service Stopped !",Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        } else  if (intent.getAction().equals(ServiceConstants.ACTION.STARTFOREGROUND_ACTION)) {
            startinForeground();
            Toast.makeText(this,"Service Started !",Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    /**
     * creates a notification bar for foreground service and starts the service.
     */
    private void startinForeground() {
        Intent myActivity = new Intent(this,MainTabActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,myActivity,0);
        Notification.Builder builder = new Notification.Builder(this);

        builder.setAutoCancel(false);
        builder.setTicker("");
        builder.setContentTitle("Mesh IM is Running");
        builder.setContentText("Tap to open the App");
        builder.setSmallIcon(R.mipmap.rm_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setNumber(100);
        builder.build();
        mServiceNotification = builder.getNotification();
        startForeground(ServiceConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, mServiceNotification);
    }


}

