package io.left.meshim.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;

import io.left.meshim.services.IMeshIMService;
import io.left.meshim.services.MeshIMService;

/**
 * Abstract parent class to all Acitivies that need to connect to {@link MeshIMService}.
 */
public abstract class ServiceConnectedActivity extends Activity {
    // Reference to AIDL interface of app service.
    IMeshIMService mService = null;

    // Handles connecting to service. Registers `mCallback` with the service when the
    // mConnection is successful.
    ServiceConnection mConnection = new ServiceConnection() {
        // Called when the mConnection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IMeshIMService.Stub.asInterface(service);
            try {
                mService.registerActivityCallback(mCallback);
                mService.setForeground(false);
            } catch (RemoteException e) {
                // If the connection has died, attempt to reconnect, otherwise ignore it.
                if (e instanceof DeadObjectException) {
                    reconnectToService();
                }
            }
        }

        // Called when the mConnection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    // Implementation of AIDL interface.
    IActivity.Stub mCallback = new IActivity.Stub() {
        @Override
        public void updateInterface() throws RemoteException {
            ServiceConnectedActivity.this.updateInterface();
        }
    };

    /**
     * Connects to service when activity starts.
     * @param savedInstanceState passed by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToService();
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
     * To be called when the service connection has broken (e.g. an AIDL call has failed with a
     * {@link DeadObjectException}. Disconnects and reconnects to the service.
     */
    public void reconnectToService() {
        mService = null;
        disconnectFromService();
        connectToService();
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
            } catch (RemoteException ignored) {
                // As we are disconnecting, this isn't our problem.
                // When we need the service again we will restart it if it doesn't exist.
            }
            mService = null;
            unbindService(mConnection);
        }
    }

    /**
     * Called by the {@link MeshIMService} when the state of the mesh has changed, and the UI needs
     * to re-draw.
     */
    abstract void updateInterface();
}