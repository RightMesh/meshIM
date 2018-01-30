package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import io.left.meshenger.Services.IMeshIMService;
import io.left.meshenger.Services.MeshIMService;

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
                mService.registerMainActivityCallback(mCallback);
                mService.setForeground(false);
            } catch (RemoteException e) {
                e.printStackTrace();
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
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(mConnection);
            mService = null;
        }
    }

    /**
     * Called by the {@link MeshIMService} when the state of the mesh has changed, and the UI needs
     * to re-draw.
     */
    abstract void updateInterface();
}