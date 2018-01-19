package io.left.meshenger.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.RightMeshConnectionHandler;

/**
 * Handles service and lifecycle management. Defers RightMesh operations to
 * {@link RightMeshConnectionHandler}.
 */
public class MeshConnectionManagerService extends Service {
    private RightMeshConnectionHandler meshConnection;

    /**
     * Connects to RightMesh when service is started.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        meshConnection = new RightMeshConnectionHandler();
        meshConnection.connect(this);
    }

    /**
     * Disconnects from RightMesh when service is stopped.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        meshConnection.disconnect();
    }

    /**
     * Implementation of AIDL interface. As most of these calls are for mesh operations, most of
     * them just call a method in {@link MeshConnectionManagerService#meshConnection}.
     */
    private final IMeshConnectionManagerService.Stub mBinder
            = new IMeshConnectionManagerService.Stub() {
        @Override
        public void send(String message) {
            // Nothing for now.
        }

        @Override
        public void registerMainActivityCallback(IActivity callback) {
            meshConnection.setCallback(callback);
        }

        @Override
        public void sendHello() {
            meshConnection.sendHello();
        }

        @Override
        public void configure() {
            meshConnection.configure();
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