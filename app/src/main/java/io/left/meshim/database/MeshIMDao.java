package io.left.meshim.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.util.SparseArray;

import io.left.meshim.activities.MainActivity;
import io.left.meshim.adapters.ConversationListAdapter;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.MeshIdTuple;
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

    @Query("SELECT UserId, MeshId FROM Users WHERE MeshId = :meshId")
    public abstract MeshIdTuple fetchMeshIdTupleByMeshId(MeshID meshId);

    @Query("SELECT * FROM Users WHERE UserId = :id")
    public abstract User fetchUserById(int id);

    @Query("SELECT * FROM Users WHERE MeshId = :meshId")
    public abstract User fetchUserByMeshId(MeshID meshId);

    @Query("UPDATE  Messages SET IsRead = 1 WHERE MessageId=:messageId ")
    public abstract void updateMessageIsRead(int messageId);

    /**
     * inserts the messages into the database.
     * @param messages messages needed to be inserted into the database.
     * @return  the row id that was inserted.
     */
    @Insert()
    public abstract long[]insertMessages(Message... messages);

    @Query("SELECT * FROM Messages WHERE SenderId IN (:userIds) AND RecipientId IN (:userIds)"
            + " ORDER BY Timestamp ASC")
    public abstract Message[] fetchMessagesBetweenUsers(int... userIds);

    @Query("UPDATE  Messages SET IsDelivered = 1 WHERE MessageId=:messageId ")
    public abstract void updateMessageIsDelivered(int messageId);

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
     *     exchanged with. This is achieved by creating  `PeerId`, which maps to a `User.UserId`
     *     of a user a message has been exchanged with. `PeerId` is created by shrinking each
     *     `RecipientId`/`SenderId` pairdown to just the UserId that isn't this device's user. Since
     *     the latter is always 1, the other user's Id will always be greater in value, so we use
     *     `max(RecipientId, SenderId)` to acquire the foreign party in the pair. Finally, we use
     *     `MAX(Timestamp) AS Timestamp` and `GROUP BY PeerId` to filter the table down to the most
     *     recent message for each peer.
     * </p>
     *
     * <p>
     *     With the messages sorted, we do an inner join on the Users table where PeerId = UserId in
     *     order to get the information for the user we've had the conversation with. We go in this
     *     direction because we will not have exchanged a message with every user in the
     *     Users table.
     * </p>
     * @return a summary of every conversation the device's user has started
     */
    @Query("SELECT Username, Avatar, Contents, Timestamp, PeerId, IsRead,IsDelivered, UnreadMessages "
            + "FROM ("
            + "SELECT max(RecipientId, SenderId) AS PeerId, Contents,IsRead,IsDelivered, "
            + "SUM(CASE WHEN IsRead =0 THEN 1 ELSE 0 END) AS UnreadMessages  , "
            + "MAX(Timestamp) AS Timestamp FROM Messages GROUP BY PeerId"
            + ") INNER JOIN Users ON PeerId = UserId "
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
            //marks all the messages loading into chat activity as read.
            this.updateMessageIsRead(m.id);
            m.setSender(idUserMap.get(m.senderId));
            m.setRecipient(idUserMap.get(m.recipientId));
        }

        return Arrays.asList(messages);
    }
}