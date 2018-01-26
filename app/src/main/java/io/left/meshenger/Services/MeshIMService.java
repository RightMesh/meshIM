package io.left.meshenger.Services;

import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Database.MeshIMDatabase;
import io.left.meshenger.Models.User;
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
}