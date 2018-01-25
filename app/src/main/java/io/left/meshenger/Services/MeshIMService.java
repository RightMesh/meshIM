package io.left.meshenger.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Activities.MainTabActivity;
import io.left.meshenger.Database.MeshIMDatabase;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import io.left.meshenger.RightMeshConnectionHandler;

/**
 * Handles service and lifecycle management. Defers RightMesh operations to
 * {@link RightMeshConnectionHandler}.
 */
public class MeshIMService extends Service {
    private MeshIMDatabase mDatabase;
    private RightMeshConnectionHandler mMeshConnection;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(Constant.ACTION.STARTFOREGROUND_ACTION)){
            Toast.makeText(this,"Start Service",Toast.LENGTH_SHORT).show();

            Intent notificationIntent = new Intent(this, MainTabActivity.class);
            notificationIntent.setAction(Constant.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.notification);


            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.rm_launcher);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("nkDroid Music Player")
                    .setTicker("nkDroid Music Player")
                    .setContentText("nkDroid Music")
                    .setSmallIcon(R.mipmap.rm_launcher)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContent(notificationView)
                    .setOngoing(true).build();
            startForeground(Constant.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);

        }



        return super.onStartCommand(intent, flags, startId);
    }
}
