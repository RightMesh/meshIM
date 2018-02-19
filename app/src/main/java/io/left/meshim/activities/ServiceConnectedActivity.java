package io.left.meshim.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import io.left.meshim.services.IMeshIMService;
import io.left.meshim.services.MeshIMService;

/**
 * Abstract parent class to all Acitivies that need to connect to {@link MeshIMService}.
 */
public abstract class ServiceConnectedActivity extends AppCompatActivity {
    // Reference to AIDL interface of app service.
    IMeshIMService mService = null;

    // Handles connecting to service. Registers `mCallback` with the service when the
    // mConnection is successful.
    ServiceConnection mConnection = new ServiceConnection() {
        // Called when the mConnection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IMeshIMService.Stub.asInterface(service);
            hideService();
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
     * Connects to service when activity is created.
     */
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        connectToService();
    }

    /**
     * Unbind from service when activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    /**
     * Hide service notification when activity starts.
     */
    @Override
    protected void onStart() {
        super.onStart();
        hideService();
    }

    /**
     * Show service notification when activity stops.
     */
    @Override
    protected void onStop() {
        super.onStop();
        showService();
    }

    /**
     * To be called when the service connection has broken (e.g. an AIDL call has failed with a
     * {@link DeadObjectException}. Disconnects and reconnects to the service.
     */
    public void reconnectToService() {
        showService();
        unbindService(mConnection);
        connectToService();
    }

    /**
     * Binds to and starts service.
     */
    private void connectToService() {
        Intent serviceIntent = new Intent(this, MeshIMService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    /**
     * Set service to run in foreground mode.
     */
    private void showService() {
        if (mService != null) {
            try {
                mService.setForeground(true);
            } catch (RemoteException ignored) {
                // As we are disconnecting, this isn't our problem.
                // When we need the service again we will restart it if it doesn't exist.
            }
        }
    }

    /**
     * Set service to run in background mode, and register this activity's callback to
     * receive updates from the service.
     */
    private void hideService() {
        if (mService != null) {
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
    }

    /**
     * Called by the {@link MeshIMService} when the state of the mesh has changed, and the UI needs
     * to re-draw.
     */
    abstract void updateInterface();

}