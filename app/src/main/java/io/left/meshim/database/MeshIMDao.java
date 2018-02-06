package io.left.meshim.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.MeshIDTuple;
import io.left.meshim.models.Message;
import io.left.meshim.models.User;
import io.left.rightmesh.id.MeshID;

/**
 * A collection of queries for accessing data types for MeshIM.
 */
@Dao
public interface MeshIMDao {
    @Insert()
    void insertUsers(User... users);

    @Update
    void updateUsers(User... users);

    @Query("SELECT * FROM Users")
    User[] fetchAllUsers();

    @Query("SELECT UserID, MeshID FROM Users WHERE MeshID = :meshId")
    MeshIDTuple fetchMeshIdTupleByMeshId(MeshID meshId);

    @Query("SELECT * FROM Users WHERE UserID = :id")
    User fetchUserById(int id);

    @Insert()
    void insertMessages(Message... messages);

    @Query("SELECT * FROM Messages WHERE SenderID IN (:userIds) AND RecipientID IN (:userIds)"
            + " ORDER BY Timestamp ASC")
    Message[] getMessagesBetweenUsers(int... userIds);

    /**
     * This query is used by {@link io.left.meshim.adapters.UserMessageListAdapter}. That adapter
     * populates a tab in {@link io.left.meshim.activities.MainTabActivity} which shows a list of
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
    @Query("SELECT Username, Avatar, Contents, Timestamp, PeerID "
            + "FROM ("
            + "SELECT max(RecipientID, SenderID) AS PeerID, Contents, MAX(Timestamp) AS Timestamp "
            + "FROM Messages GROUP BY PeerID"
            + ") INNER JOIN Users ON PeerID = UserID "
            + "ORDER BY Timestamp DESC"
    )
    ConversationSummary[] getConversationSummaries();
}