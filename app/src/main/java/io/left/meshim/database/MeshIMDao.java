package io.left.meshim.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.util.Log;
import android.util.SparseArray;

import io.left.meshim.activities.MainActivity;
import io.left.meshim.adapters.ConversationListAdapter;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.MeshIDTuple;
import io.left.meshim.models.Message;
import io.left.meshim.models.User;
import io.left.rightmesh.id.MeshID;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of queries for accessing data types for meshIM.
 */
@Dao
public abstract class MeshIMDao {
    @Insert()
    public abstract void insertUsers(User... users);

    @Update
    public abstract void updateUsers(User... users);

    @Query("SELECT * FROM Users")
    public abstract User[] fetchAllUsers();

    @Query("SELECT UserID, MeshID FROM Users WHERE MeshID = :meshId")
    public abstract MeshIDTuple fetchMeshIdTupleByMeshId(MeshID meshId);

    @Query("SELECT * FROM Users WHERE UserID = :id")
    public abstract User fetchUserById(int id);

    @Query("SELECT * FROM Users WHERE MeshID = :meshId")
    public abstract User fetchUserByMeshId(MeshID meshId);

    @Query("UPDATE  Messages SET isRead = :val WHERE MessageID=:messageID ")
    public abstract void updateMessageIsRead(int messageID, boolean val);

    @Insert()
    public abstract void insertMessages(Message... messages);

    @Query("SELECT * FROM Messages WHERE SenderID IN (:userIds) AND RecipientID IN (:userIds)"
            + " ORDER BY Timestamp ASC")
    public abstract Message[] fetchMessagesBetweenUsers(int... userIds);

    /**
     * This query is used by {@link ConversationListAdapter}. That adapter
     * populates a tab in {@link MainActivity} which shows a list of
     * conversations that have been had with other meshIM users in the past.
     *
     * <p>
     *     This query returns a list of {@link ConversationSummary}s, which contain the four fields
     *     needed to populate a list item in that list: the username and avatar for the user the
     *     conversation is with, and the message and timestamp of the most recent message exchanged
     *     with that user.
     * </p>
     *
     * <p>
     *     This first thing this query does is create a subquery, which shrinks the Messages table
     *     down to just the most recent message exchanged with each person a message has been
     *     exchanged with. This is achieved by creating  `PeerID`, which maps to a `User.UserID`
     *     of a user a message has been exchanged with. `PeerID` is created by shrinking each
     *     `RecipientID`/`SenderID` pairdown to just the UserID that isn't this device's user. Since
     *     the latter is always 1, the other user's ID will always be greater in value, so we use
     *     `max(RecipientID, SenderID)` to acquire the foreign party in the pair. Finally, we use
     *     `MAX(Timestamp) AS Timestamp` and `GROUP BY PeerID` to filter the table down to the most
     *     recent message for each peer.
     * </p>
     *
     * <p>
     *     With the messages sorted, we do an inner join on the Users table where PeerID = UserID in
     *     order to get the information for the user we've had the conversation with. We go in this
     *     direction because we will not have exchanged a message with every user in the
     *     Users table.
     * </p>
     * @return a summary of every conversation the device's user has started
     */
    @Query("SELECT Username, Avatar, Contents, Timestamp, PeerID, isRead "
            + "FROM ("
            + "SELECT max(RecipientID, SenderID) AS PeerID, Contents, isRead, MAX(Timestamp) AS Timestamp "
            + "FROM Messages GROUP BY PeerID"
            + ") INNER JOIN Users ON PeerID = UserID "
            + "ORDER BY Timestamp DESC"
    )
    public abstract ConversationSummary[] fetchConversationSummaries();

    /**
     * Retrieve the list of messages exchanged between this device and the supplied user.
     * @param user user to get messages exchanged with
     * @return list of messages exchanged with the supplied user
     */
    public List<Message> fetchMessagesForUser(User user) {
        // Fetch device's user
        User deviceUser = this.fetchUserById(User.DEVICE_USER_ID);

        // Retrieve messages from database.
        Message[] messages = this.fetchMessagesBetweenUsers(deviceUser.id, user.id);

        // Make User classes easier to find by their id.
        SparseArray<User> idUserMap = new SparseArray<>();
        idUserMap.put(deviceUser.id, deviceUser);
        idUserMap.put(user.id, user);

        // Populate messages with actual User classes.
        for (Message m : messages) {
            //marks all the messages loading as read.
            this.updateMessageIsRead(m.id,true);
            m.setSender(idUserMap.get(m.senderId));
            m.setRecipient(idUserMap.get(m.recipientId));
        }

        return Arrays.asList(messages);
    }
}