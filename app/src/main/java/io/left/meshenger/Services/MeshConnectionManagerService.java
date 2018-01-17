package io.left.meshenger.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

class MeshConnectionManagerService extends Service {
    private final IMeshConnectionManagerService.Stub mBinder
            = new IMeshConnectionManagerService.Stub() {
        @Override
        public void send(String message) throws RemoteException {
            // Nothing for now.
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}