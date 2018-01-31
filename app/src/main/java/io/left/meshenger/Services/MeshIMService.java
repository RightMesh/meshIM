package io.left.meshenger.Services;

import static io.left.meshenger.Services.ServiceConstants.ACTION.STARTFOREGROUND_ACTION;
import static io.left.meshenger.Services.ServiceConstants.ACTION.STOPFOREGROUND_ACTION;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Database.MeshIMDatabase;
import io.left.meshenger.Models.Message;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.RightMeshConnectionHandler;
import io.left.rightmesh.util.RightMeshException;

import java.util.List;

/**
 * Handles service and lifecycle management. Defers RightMesh operations to
 * {@link RightMeshConnectionHandler}.
 */
public class MeshIMService extends Service {
    private MeshIMDatabase mDatabase;
    private RightMeshConnectionHandler mMeshConnection;
    private Notification mServiceNotification;
    private boolean mIsBound = false;

    /**
     * Connects to RightMesh when service is started.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Intent stopForegroundIntent = new Intent(this, MeshIMService.class);
        stopForegroundIntent.setAction(STOPFOREGROUND_ACTION);
        PendingIntent pendingIntent
                = PendingIntent.getService(this,0,stopForegroundIntent,0);

        mServiceNotification = new NotificationCompat.Builder(this)
                .setAutoCancel(false)
                .setTicker("Mesh IM")
                .setContentTitle("Mesh IM is Running")
                .setContentText("Tap to go offline.")
                .setSmallIcon(R.mipmap.available_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setNumber(100)
                .build();

        mDatabase = Room.databaseBuilder(getApplicationContext(), MeshIMDatabase.class, "MeshIM")
                .build();

        User user = User.fromDisk(this);
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
        mDatabase.close();

        // We need to ensure the service is recreated every time we think it is dead.
        // If this doesn't happen, RightMesh doesn't always start up again.
        // We aren't _thrilled_ with this solution, and are investigating it further.
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * Implementation of AIDL interface. As most of these calls are for mesh operations, most of
     * them just call a method in {@link MeshIMService#mMeshConnection}.
     */
    private final IMeshIMService.Stub mBinder = new IMeshIMService.Stub() {
        @Override
        public void sendTextMessage(User recipient, String message) {
            mMeshConnection.sendTextMessage(recipient, message);
        }

        @Override
        public void setForeground(boolean value) {
            if (value) {
                startinForeground();
            } else {
                stopForeground(true);
            }
        }

        @Override
        public void registerMainActivityCallback(IActivity callback) {
            mMeshConnection.setCallback(callback);
        }

        @Override
        public List<User> getOnlineUsers() {
            return mMeshConnection.getUserList();
        }

        @Override
        public List<Message> getMessagesForUser(User user) throws RemoteException {
            return mMeshConnection.getMessagesForUser(user);
        }
        @Override
        public void showRightMeshSettings()  {
            mMeshConnection.showRightMeshSetting();
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
        mIsBound = true;
        return mBinder;
    }

    /**
     * Keeps track of if this is actively bound.
     *
     * @param intent Intent that bound to the service
     * @return false (don't call onRebind())
     */
    @Override
    public boolean onUnbind(Intent intent) {
        mIsBound = false;
        mMeshConnection.setCallback(null);
        return false;
    }

    /**
     * Responds to events sent by the notification. Used to toggle foreground mode on/off.
     *
     * @param intent service intent.
     * @param flags extra flags
     * @param startId id to start with
     * @return {@link Service#START_STICKY}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String action = null;
        if (intent != null) {
            action = intent.getAction();
        }
        if (action != null && action.equals(STOPFOREGROUND_ACTION)) {
            if (mIsBound) {
                stopForeground(true);
            } else {
                stopSelf();
            }
        } else if (action != null && action.equals(STARTFOREGROUND_ACTION)) {
            startinForeground();
        }
        return START_STICKY;
    }

    /**
     * creates a notification bar for foreground service and starts the service.
     */
    private void startinForeground() {
        startForeground(ServiceConstants.NOTIFICATION_ID.FOREGROUND_SERVICE, mServiceNotification);
    }
}