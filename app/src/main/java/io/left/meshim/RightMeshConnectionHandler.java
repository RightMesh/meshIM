package io.left.meshim;

import static io.left.rightmesh.mesh.MeshManager.ADDED;
import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static io.left.rightmesh.mesh.MeshManager.REMOVED;
import static protobuf.MeshIMMessages.MessageType.MESSAGE;
import static protobuf.MeshIMMessages.MessageType.PEER_UPDATE;

import android.content.Context;
import android.os.RemoteException;
import android.util.SparseArray;

import com.google.protobuf.InvalidProtocolBufferException;

import io.left.meshim.activities.IActivity;
import io.left.meshim.database.MeshIMDao;
import io.left.meshim.database.MeshIMDatabase;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.MeshIDTuple;
import io.left.meshim.models.Message;
import io.left.meshim.models.User;
import io.left.meshim.services.MeshIMService;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.android.MeshService;
import io.left.rightmesh.id.MeshID;
import io.left.rightmesh.mesh.MeshManager.DataReceivedEvent;
import io.left.rightmesh.mesh.MeshManager.PeerChangedEvent;
import io.left.rightmesh.mesh.MeshManager.RightMeshEvent;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import java.util.ArrayList;
import java.util.Arrays;
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

    // Database interface.
    private MeshIMDao dao;

    // Link to current activity.
    private IActivity callback = null;
    //reference to service
    private MeshIMService meshIMService;

    /**
     * Constructor.
     * @param user user info for this device
     * @param database open connection to database
     * @param meshIMService link to service instance
     */
    public RightMeshConnectionHandler(User user, MeshIMDatabase database,
                                      MeshIMService meshIMService) {
        this.user = user;
        this.dao = database.meshIMDao();
        this.meshIMService  = meshIMService;

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
     * Retrieve the list of messages exchanged between this device and the supplied user.
     * @param user user to get messages exchanged with
     * @return list of messages exchanged with the supplied user
     */
    public List<Message> getMessagesForUser(User user) {
        // Retrieve messages from database.
        Message[] messages = dao.getMessagesBetweenUsers(this.user.id, user.id);

        // Make User classes easier to find by their id.
        SparseArray<User> idUserMap = new SparseArray<>();
        idUserMap.put(this.user.id, this.user);
        idUserMap.put(user.id, user);

        // Populate messages with actual User classes.
        for (Message m : messages) {
            m.setSender(idUserMap.get(m.senderId));
            m.setRecipient(idUserMap.get(m.recipientId));
        }

        return Arrays.asList(messages);
    }

    /**
     * Returns a list of online users.
     * @return online users
     */
    public List<User> getUserList() {
        return new ArrayList<>(users.values());
    }

    /**
     * Returns a list of conversation summaries stored in the database.
     * @return summaries of all conversations stored in the database
     */
    public List<ConversationSummary> getConversationSummaries() {
        return Arrays.asList(dao.getConversationSummaries());
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

            dao.insertMessages(messageObject);
            updateInterface();
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
            user.setMeshId(uuid);
            user.save();
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
                e.printStackTrace();
            }
        }
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
            MeshIMMessage messageWrapper = MeshIMMessage.parseFrom(event.data);
            MeshID peerId = event.peerUuid;

            MessageType messageType = messageWrapper.getMessageType();
            if (messageType == PEER_UPDATE) {
                PeerUpdate peerUpdate = messageWrapper.getPeerUpdate();

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
                updateInterface();
            } else if (messageType == MESSAGE) {
                MeshIMMessages.Message protoMessage = messageWrapper.getMessage();
                User sender = users.get(peerId);
                Message message = new Message(sender, user, protoMessage.getMessage(), false);
                dao.insertMessages(message);
                meshIMService.sendNotification(sender,message);
                updateInterface();
            }
        } catch (InvalidProtocolBufferException ignored) { /* Ignore malformed messages. */ }
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
        if (event.peerUuid == user.getMeshId()) {
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
                .setUserName(user.getUsername())
                .setAvatarId(user.getAvatar())
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

    /**
     * Displays Rightmesh setting page.
     */
    public void showRightMeshSettings() {
        try {
            meshManager.showSettingsActivity();
        } catch (RightMeshException e) {
            e.printStackTrace();
        }
    }
}