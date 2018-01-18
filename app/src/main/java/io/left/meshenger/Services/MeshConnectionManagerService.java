package io.left.meshenger.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.HashSet;

import io.left.meshenger.Activities.IMainActivity;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshID;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.MeshUtility;
import io.left.rightmesh.util.RightMeshException;

import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;

/**
 * Service responsible for handling the RightMesh communication and lifecycle for Meshenger.
 */
public class MeshConnectionManagerService extends Service implements MeshStateListener {
    // Port to bind app to.
    private static final int HELLO_PORT = 9876;

    // MeshManager instance - interface to the mesh network.
    AndroidMeshManager mm = null;

    // Set to keep track of peers connected to the mesh.
    HashSet<MeshID> users = new HashSet<>();

    // Callback to communicate with the activity.
    IMainActivity mainActivityCallback = null;

    /**
     * Connects to RightMesh when service is started.
     */
    @Override
    public void onCreate() {
        mm = AndroidMeshManager.getInstance(MeshConnectionManagerService.this,
                MeshConnectionManagerService.this);
    }

    /**
     * Disconnects from RightMesh when service is stopped.
     */
    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            mm.stop();
        } catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
        }
    }

    // Implementation of AIDL interface.
    private final IMeshConnectionManagerService.Stub mBinder
            = new IMeshConnectionManagerService.Stub() {
        @Override
        public void send(String message) throws RemoteException {
            // Nothing for now.
        }

        /**
         * Allows the activity that bound to the service to add a callback for two-way IPC.
         *
         * @param callback Callback to keep a reference to.
         */
        @Override
        public void registerMainActivityCallback(IMainActivity callback) {
            mainActivityCallback = callback;
            try {
                mainActivityCallback.echo("IPC Connection Established.");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * Sends "hello" to all known peers.
         */
        @Override
        public void sendHello() throws RemoteException {
            for (MeshID receiver : users) {
                String msg = "Hello to: " + receiver + " from" + mm.getUuid();
                MeshUtility.Log(this.getClass().getCanonicalName(), "MSG: " + msg);
                byte[] testData = msg.getBytes();
                try {
                    mm.sendDataReliable(receiver, HELLO_PORT, testData);
                } catch (RightMeshException e) {
                    echo(e.getMessage());
                }
            }
        }

        /**
         * Open mesh settings screen.
         */
        @Override
        public void configure() throws RemoteException {
            try {
                mm.showSettingsActivity();
            } catch (RightMeshException ex) {
                echo(ex.getMessage());
            }
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
     * A helper method that handles the null checking and exception handling around the AIDL
     * `echo` method.
     *
     * @param message Message to be forwarded to the activity.
     */
    private void echo(String message) {
        if (mainActivityCallback != null) {
            try {
                mainActivityCallback.echo(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called by the {@link MeshService} when the mesh state changes. Initializes mesh connection
     * on first call.
     *
     * @param uuid  our own user id on first detecting
     * @param state state which indicates SUCCESS or an error code
     */
    @Override
    public void meshStateChanged(MeshID uuid, int state) {
        if (state == MeshStateListener.SUCCESS) {
            try {
                mm.setPattern("Meshenger-Frazer");

                // Binds this app to MESH_PORT.
                // This app will now receive all events generated on that port.
                mm.bind(HELLO_PORT);

                // Subscribes handlers to receive events from the mesh.
                mm.on(DATA_RECEIVED, this::handleDataReceived);
                mm.on(PEER_CHANGED, this::handlePeerChanged);

                // If connected to the main activity, tell it to enable its UI elements.
                try {
                    if (mainActivityCallback != null) {
                        mainActivityCallback.enableInterface();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } catch (RightMeshException e) {
                String status = "Error initializing the library" + e.toString();
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles incoming data events from the mesh - toasts the contents of the data.
     *
     * @param e event object from mesh
     */
    private void handleDataReceived(MeshManager.RightMeshEvent e) {
        final MeshManager.DataReceivedEvent event = (MeshManager.DataReceivedEvent) e;

        // Echo.
        String message = new String(event.data);
        echo(message);
    }

    /**
     * Handles peer update events from the mesh - maintains a list of peers and updates the display.
     *
     * @param e event object from mesh
     */
    private void handlePeerChanged(MeshManager.RightMeshEvent e) {
        // Update peer list.
        MeshManager.PeerChangedEvent event = (MeshManager.PeerChangedEvent) e;
        if (event.state != REMOVED && !users.contains(event.peerUuid)) {
            users.add(event.peerUuid);
            echo(event.peerUuid.toString() + " added.");
        } else if (event.state == REMOVED) {
            users.remove(event.peerUuid);
            echo(event.peerUuid.toString() + " removed.");
        }
    }
}