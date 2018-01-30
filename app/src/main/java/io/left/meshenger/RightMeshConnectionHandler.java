package io.left.meshenger;

import static io.left.rightmesh.mesh.MeshManager.ADDED;
import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;
import static protobuf.MeshIMMessages.MessageType.MESSAGE;
import static protobuf.MeshIMMessages.MessageType.PEER_UPDATE;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.RemoteException;

import com.google.protobuf.InvalidProtocolBufferException;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Database.MeshIMDao;
import io.left.meshenger.Database.MeshIMDatabase;
import io.left.meshenger.Models.MeshIDTuple;
import io.left.meshenger.Models.Message;
import io.left.meshenger.Models.User;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshID;
import io.left.rightmesh.mesh.MeshManager.DataReceivedEvent;
import io.left.rightmesh.mesh.MeshManager.PeerChangedEvent;
import io.left.rightmesh.mesh.MeshManager.RightMeshEvent;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.MeshUtility;
import io.left.rightmesh.util.RightMeshException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import protobuf.MeshIMMessages;
import protobuf.MeshIMMessages.MeshIMMessage;
import protobuf.MeshIMMessages.MessageType;
import protobuf.MeshIMMessages.PeerUpdate;

/**
 * All RightMesh logic abstracted into one class to keep it separate from Android logic.
 */
public class RightMeshConnectionHandler implements MeshStateListener {
    // Port to bind app to.
    private static final int HELLO_PORT = 9876;

    // MeshManager instance - interface to the mesh network.
    private AndroidMeshManager meshManager = null;

    // Set to keep track of peers connected to the mesh.
    private HashMap<MeshID, User> users = new HashMap<>();
    private User user = null;

    // Temporary messages array before we store them to the database.
    private HashMap<User, List<Message>> messages = new HashMap<>();

    // Database reference.
    private MeshIMDatabase database;
    private MeshIMDao dao;

    // Link to current activity.
    private IActivity callback = null;

    /**
     * Constructor.
     * @param user user info for this device
     * @param database open connection to database
     */
    public RightMeshConnectionHandler(User user, MeshIMDatabase database) {
        this.user = user;
        this.database = database;
        this.dao = database.meshIMDao();

        new Thread(() -> {
            if (dao.fetchAllUsers().length == 0) {
                // Insert this device's user as the first user on first run.
                this.dao.insertUsers(user);
            } else {
                // Otherwise make sure the database is up to date with SharedPreferences.
                this.dao.updateUsers(user);
            }
        }).start();
    }

    public void setCallback(IActivity callback) {
        this.callback = callback;
        updateInterface();
    }

    /**
     * Returns a list of online users.
     * @return online users
     */
    public List<User> getUserList() {
        return new ArrayList<>(users.values());
    }

    /**
     * Sends a simple text message to another user.
     * @param recipient recipient of the message
     * @param message contents of the message
     */
    public void sendTextMessage(User recipient, String message) {
        Message messageObject = new Message(user, recipient, message, true);
        try {
            meshManager.sendDataReliable(recipient.getMeshId(), HELLO_PORT,
                    createMessagePayloadFromMessage(messageObject));
            messages.get(recipient).add(messageObject);
        } catch (RightMeshException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a {@link AndroidMeshManager} instance, starting RightMesh if it isn't already running.
     *
     * @param context service context to bind to
     */
    public void connect(Context context) {
        meshManager = AndroidMeshManager.getInstance(context, RightMeshConnectionHandler.this);
    }

    /**
     * Close the RightMesh connection, stopping the service if no other apps are running.
     */
    public void disconnect() {
        try {
            meshManager.stop();
        } catch (MeshService.ServiceDisconnectedException e) {
            e.printStackTrace();
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
                // Binds this app to MESH_PORT.
                // This app will now receive all events generated on that port.
                meshManager.bind(HELLO_PORT);
                // Subscribes handlers to receive events from the mesh.
                meshManager.on(DATA_RECEIVED, this::handleDataReceived);
                meshManager.on(PEER_CHANGED, this::handlePeerChanged);

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
        MeshUtility.Log("MeshIM", message);
    }

    /**
     * Exception boilerplate around {@link IActivity#updateInterface()}.
     */
    private void updateInterface() {
        try {
            if (callback != null) {
                callback.updateInterface();
            }
        } catch (RemoteException | NullPointerException ignored) {
            // Just keep swimming.
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
                MeshID peerId = e.peerUuid;

                // Initialize peer with info from update packet.
                User peer = new User(peerUpdate.getUserName(), peerUpdate.getAvatarId(), peerId);

                // Create or update user in database.
                MeshIDTuple dietPeer = dao.fetchMeshIdTupleByMeshId(peerId);
                if (dietPeer == null) {
                    dao.insertUsers(peer);

                    // Fetch the user's id after it is initialized.
                    dietPeer = dao.fetchMeshIdTupleByMeshId(peerId);
                    peer.id = dietPeer.id;
                } else {
                    peer.id = dietPeer.id;
                    dao.updateUsers(peer);
                }

                // Store user in list of online users.
                users.put(peerId, peer);
                messages.put(peer, new ArrayList<>());
                updateInterface();
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
        if (event.peerUuid == meshManager.getUuid()) {
            return;
        }

        if (event.state == ADDED) {
            // Send our information to a new or rejoining peer.
            byte[] message = createPeerUpdatePayloadFromUser(user);
            try {
                meshManager.sendDataReliable(event.peerUuid, HELLO_PORT, message);
            } catch (RightMeshException rme) {
                rme.printStackTrace();
            }
        } else if (event.state == REMOVED) {
            users.remove(event.peerUuid);
            updateInterface();
        }
    }

    /**
     * Creates a byte array representing a {@link User}, to be broadcast over the mesh.
     * @param user user to be represented in bytes
     * @return payload to be broadcast
     */
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

    private byte[] createMessagePayloadFromMessage(Message message) {
        MeshIMMessages.Message protoMsg = MeshIMMessages.Message.newBuilder()
                .setMessage(message.getMessage())
                .setTime(message.getDateAsTimestamp())
                .build();

        MeshIMMessage payload = MeshIMMessage.newBuilder()
                .setMessageType(MESSAGE)
                .setMessage(protoMsg)
                .build();

        return payload.toByteArray();
    }
}