package io.left.meshenger;

import static io.left.rightmesh.mesh.MeshManager.ADDED;
import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;
import static protobuf.MeshIMMessages.MessageType.PEER_UPDATE;

import android.content.Context;
import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Models.User;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshID;
import io.left.rightmesh.mesh.MeshManager.DataReceivedEvent;
import io.left.rightmesh.mesh.MeshManager.PeerChangedEvent;
import io.left.rightmesh.mesh.MeshManager.RightMeshEvent;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import java.util.HashMap;
import protobuf.MeshIMMessages.MeshIMMessage;
import protobuf.MeshIMMessages.PeerUpdate;

public class RightMeshConnectionHandler implements MeshStateListener {
    // Port to bind app to.
    private static final int HELLO_PORT = 9876;

    // MeshManager instance - interface to the mesh network.
    private AndroidMeshManager mMeshManager = null;

    // Set to keep track of peers connected to the mesh.
    private HashMap<MeshID, User> mUsers = new HashMap<>();
    private User mUser = null;

    // Link to current activity.
    private IActivity mCallback = null;

    public RightMeshConnectionHandler(User user) {
        this.mUser = user;
    }

    /**
     * Setter for {@link RightMeshConnectionHandler#mCallback}. Notifies the connected activity that
     * the connection is successful.
     *
     * @param callback new value
     */
    public void setCallback(IActivity callback) {
        this.mCallback = callback;
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
            mMeshManager.showSettingsActivity();
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
        mMeshManager = AndroidMeshManager.getInstance(context, RightMeshConnectionHandler.this);
    }

    /**
     * Close the RightMesh connection, stopping the service if no other apps are running.
     */
    public void disconnect() {
        try {
            mMeshManager.stop();
        } catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
        }
    }

    /**
     * HelloMesh function, not much longer for this world.
     */
    public void sendHello() {
        for (MeshID receiver : mUsers.keySet()) {
            String msg = "Hello to: " + receiver + " from" + mMeshManager.getUuid();
            byte[] testData = msg.getBytes();
            try {
                mMeshManager.sendDataReliable(receiver, HELLO_PORT, testData);
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
                mMeshManager.bind(HELLO_PORT);

                // Subscribes handlers to receive events from the mesh.
                mMeshManager.on(DATA_RECEIVED, this::handleDataReceived);
                mMeshManager.on(PEER_CHANGED, this::handlePeerChanged);

                // If connected to the main activity, tell it to enable its UI elements.
                try {
                    if (mCallback != null) {
                        mCallback.updateInterface();
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
        if (mCallback != null) {
            try {
                mCallback.echo(message);
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
        DataReceivedEvent event = (DataReceivedEvent) e;

        try {
            MeshIMMessage message = MeshIMMessage.parseFrom(event.data);

            if (message.getMessageType() == PEER_UPDATE) {
                PeerUpdate peerUpdate = message.getPeerUpdate();
                User peer = new User(peerUpdate.getUserName(), peerUpdate.getAvatarId());
                if (!mUsers.keySet().contains(e.peerUuid)) {
                    echo("Found a new peer!");
                }
                mUsers.put(e.peerUuid, peer);
            }
        } catch (InvalidProtocolBufferException ignored) {
            /* Ignore malformed messages. */
            // but for now...
            echo(new String(event.data));
        }
    }

    /**
     * Handles peer update events from the mesh - maintains a list of peers and updates the display.
     *
     * @param e event object from mesh
     */
    private void handlePeerChanged(RightMeshEvent e) {
        // Update peer list.
        PeerChangedEvent event = (PeerChangedEvent) e;

        // Ignore ourselves.
        if (event.peerUuid == mMeshManager.getUuid()) {
            return;
        }

        if (event.state == ADDED) {
            // Send our information to a new or rejoining peer.
            byte[] message = createPeerUpdatePayloadFromUser(mUser);
            try {
                mMeshManager.sendDataReliable(event.peerUuid, HELLO_PORT, message);
            } catch (RightMeshException rme) {
                rme.printStackTrace();
            }
        } else if (event.state == REMOVED) {
            mUsers.remove(event.peerUuid);
        }
    }

    private byte[] createPeerUpdatePayloadFromUser(User user) {
        PeerUpdate peerUpdate = PeerUpdate.newBuilder()
                .setUserName(user.getUserName())
                .setAvatarId(user.getUserAvatar())
                .build();

        MeshIMMessage message = MeshIMMessage.newBuilder()
                .setMessageType(PEER_UPDATE)
                .setPeerUpdate(peerUpdate)
                .build();

        return message.toByteArray();
    }
}