package io.left.meshim.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Insert()
    void insertMessages(Message... messages);

    @Query("SELECT * FROM Messages WHERE SenderID IN (:userIds) OR RecipientID IN (:userIds)"
            + " ORDER BY Timestamp ASC")
    Message[] getMessagesBetweenUsers(int... userIds);
}