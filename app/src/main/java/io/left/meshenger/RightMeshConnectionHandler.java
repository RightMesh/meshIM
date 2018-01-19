package io.left.meshenger;

import android.content.Context;
import android.os.RemoteException;

import java.util.HashMap;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Models.User;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshID;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshManager.RightMeshEvent;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;

public class RightMeshConnectionHandler implements MeshStateListener {
    // Port to bind app to.
    private static final int HELLO_PORT = 9876;

    // MeshManager instance - interface to the mesh network.
    private AndroidMeshManager mm = null;

    // Set to keep track of peers connected to the mesh.
    private HashMap<MeshID, User> users = new HashMap<>();
    private User user = null;

    // Link to current activity.
    private IActivity callback = null;

    public RightMeshConnectionHandler(User user) {
        this.user = user;
    }

    /**
     * Setter for {@link RightMeshConnectionHandler#callback}. Notifies the connected activity that
     * the connection is successful.
     *
     * @param callback new value
     */
    public void setCallback(IActivity callback) {
        this.callback = callback;
        try {
            callback.echo("IPC Connection Established.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * Launch the RightMesh settings activity.
     */
    public void configure() {
        try {
            mm.showSettingsActivity();
        } catch (RightMeshException ex) {
            echo(ex.getMessage());
        }
    }

    /**
     * Get a {@link AndroidMeshManager} instance, starting RightMesh if it isn't already running.
     *
     * @param context service context to bind to
     */
    public void connect(Context context) {
        mm = AndroidMeshManager.getInstance(context, RightMeshConnectionHandler.this);
    }

    /**
     * Close the RightMesh connection, stopping the service if no other apps are running.
     */
    public void disconnect() {
        try {
            mm.stop();
        } catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * HelloMesh function, not much longer for this world.
     */
    public void sendHello() {
        for (MeshID receiver : users.keySet()) {
            String msg = "Hello to: " + receiver + " from" + mm.getUuid();
            byte[] testData = msg.getBytes();
            try {
                mm.sendDataReliable(receiver, HELLO_PORT, testData);
            } catch (RightMeshException e) {
                echo(e.getMessage());
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
                mm.setPattern("FRAZER");

                // Binds this app to MESH_PORT.
                // This app will now receive all events generated on that port.
                mm.bind(HELLO_PORT);

                // Subscribes handlers to receive events from the mesh.
                mm.on(DATA_RECEIVED, this::handleDataReceived);
                mm.on(PEER_CHANGED, this::handlePeerChanged);

                // If connected to the main activity, tell it to enable its UI elements.
                try {
                    if (callback != null) {
                        callback.updateInterface();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } catch (RightMeshException e) {
                echo("Error initializing the library" + e.toString());
            }
        }
    }

    /**
     * A helper method that handles the null checking and exception handling around the AIDL
     * `echo` method.
     *
     * @param message Message to be forwarded to the activity.
     */
    private void echo(String message) {
        if (callback != null) {
            try {
                callback.echo(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles incoming data events from the mesh - toasts the contents of the data.
     *
     * @param e event object from mesh
     */
    private void handleDataReceived(RightMeshEvent e) {
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
    private void handlePeerChanged(RightMeshEvent e) {
        // Update peer list.
        MeshManager.PeerChangedEvent event = (MeshManager.PeerChangedEvent) e;
        if (event.state != REMOVED && !users.keySet().contains(event.peerUuid)) {
            users.put(event.peerUuid, null);
            echo(event.peerUuid.toString() + " added.");
        } else if (event.state == REMOVED) {
            users.remove(event.peerUuid);
            echo(event.peerUuid.toString() + " removed.");
        }
    }
}