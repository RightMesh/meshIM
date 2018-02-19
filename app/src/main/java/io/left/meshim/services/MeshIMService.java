package io.left.meshim.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import io.left.meshim.R;
import io.left.meshim.activities.ChatActivity;
import io.left.meshim.activities.IActivity;
import io.left.meshim.controllers.RightMeshController;
import io.left.meshim.database.MeshIMDatabase;
import io.left.meshim.database.Migrations;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.Message;
import io.left.meshim.models.Settings;
import io.left.meshim.models.User;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Handles service and lifecycle management. Defers RightMesh operations to
 * {@link RightMeshController}.
 */
public class MeshIMService extends Service {
    public static final String START_FOREGROUND_ACTION = "io.left.meshim.action.startforeground";
    public static final String STOP_FOREGROUND_ACTION = "io.left.meshim.action.stopforeground";
    public static final int FOREGROUND_SERVICE_ID = 101;

    public static final String CHANNEL_NAME = "meshim";
    public static final String CHANNEL_ID = "notification_channel";

    private MeshIMDatabase mDatabase;
    private RightMeshController mMeshConnection;
    private Notification mServiceNotification;
    private boolean mIsBound = false;
    private boolean mIsForeground = false;
    private int mVisibleActivities = 0;

    /**
     * Connects to RightMesh when service is started.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Intent stopForegroundIntent = new Intent(this, MeshIMService.class);
        stopForegroundIntent.setAction(STOP_FOREGROUND_ACTION);
        PendingIntent pendingIntent
                = PendingIntent.getService(this,0,stopForegroundIntent,0);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            //noinspection deprecation
            builder = new NotificationCompat.Builder(this);
        }
        Resources resources = getResources();
        mServiceNotification = builder.setAutoCancel(false)
                .setTicker(resources.getString(R.string.app_name))
                .setContentTitle(resources.getString(R.string.notification_title))
                .setContentText(resources.getString(R.string.notification_text))
                .setSmallIcon(R.mipmap.available_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setNumber(100)
                .build();

        mDatabase = Room.databaseBuilder(getApplicationContext(), MeshIMDatabase.class, "meshIM")
                .addMigrations(Migrations.ALL_MIGRATIONS)
                .build();

        User user = User.fromDisk(this);
        mMeshConnection = new RightMeshController(user, mDatabase.meshIMDao(), this);
        mMeshConnection.connect(this);
    }

    /**
     * Disconnects from RightMesh when service is stopped.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMeshConnection.disconnect();
        mMeshConnection = null;

        mDatabase.close();
        mDatabase = null;

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
        public void setForeground(boolean value) {
            // As calls to show/hide the notification are asynchronous, keep count of calls to
            // show vs hide.
            if (value && mVisibleActivities > 0) {
                mVisibleActivities--; // Activity is closing and signalling to show notification.
            } else {
                mVisibleActivities++; // Activity is opening and signalling to close notification.
            }

            if (mVisibleActivities == 0 && !mIsForeground) {
                // If the calls to open are equal to the calls to close and we aren't already
                // running in the foreground, start in foreground.
                startInForeground();
                mIsForeground = true;
            } else if (mIsForeground) {
                // Otherwise, if we're running in the foreground, stop running in foreground.
                stopForeground(true);
                mIsForeground = false;
            }
        }

        @Override
        public void broadcastUpdatedProfile() {
            mMeshConnection.broadcastProfile();
        }

        @Override
        public List<User> getOnlineUsers() {
            return mMeshConnection.getUserList();
        }

        @Override
        public void registerActivityCallback(IActivity callback) {
            mMeshConnection.setCallback(callback);
        }

        @Override
        public void sendTextMessage(User recipient, String message) {
            mMeshConnection.sendTextMessage(recipient, message);
        }

        @Override
        public void showRightMeshSettings()  {
            mMeshConnection.showRightMeshSettings();
        }

        @Override
        public List<ConversationSummary> fetchConversationSummaries() {
            return Arrays.asList(mDatabase.meshIMDao().fetchConversationSummaries());
        }

        @Override
        public List<Message> fetchMessagesForUser(User user) {
            return mDatabase.meshIMDao().fetchMessagesForUser(user);
        }

        @Override
        public User fetchUserById(int id) {
            return mDatabase.meshIMDao().fetchUserById(id);
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
        if (action != null && action.equals(STOP_FOREGROUND_ACTION)) {
            if (mIsBound) {
                stopForeground(true);
            } else {
                stopSelf();
            }
        } else if (action != null && action.equals(START_FOREGROUND_ACTION)) {
            startInForeground();
        }
        return START_STICKY;
    }

    /**
     * creates a notification bar for foreground service and starts the service.
     */
    private void startInForeground() {
        startForeground(FOREGROUND_SERVICE_ID, mServiceNotification);
    }

    /**
     *Sends a notification to user when a text message is received.
     * @param user sender
     * @param message message received
     */
    public void sendNotification(User user, Message message) {
        Settings settings = Settings.fromDisk(this);
        if (mIsForeground && (settings == null || settings.isShowNotifications())) {
            long time = Calendar.getInstance().getTimeInMillis();
            String notifContent =  message.getMessage();
            String notifTitle = user.getUsername();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), user.getAvatar());
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("recipient", user);
            intent.setData(Uri.parse("content://" + time));
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(this,
                        NotificationChannel.DEFAULT_CHANNEL_ID);
            } else {
                //noinspection deprecation
                builder = new NotificationCompat.Builder(this);
            }
            builder.setWhen(time)
                   .setContentText(notifContent)
                   .setContentTitle(notifTitle)
                   .setAutoCancel(true)
                   .setTicker(notifTitle)
                   .setLargeIcon(bitmap)
                   .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND
                           | Notification.DEFAULT_VIBRATE)
                   .setSmallIcon(R.mipmap.ic_launcher)
                   .setContentIntent(pendingIntent);

            Notification notification = builder.build();
            NotificationManager notificationManager
                    = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify((int) time, notification);
            }
        }
    }
}