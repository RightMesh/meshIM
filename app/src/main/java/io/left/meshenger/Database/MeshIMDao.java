package io.left.meshenger.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import io.left.meshenger.Models.MeshIDTuple;
import io.left.meshenger.Models.User;
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
    public MeshIDTuple fetchMeshIdTupleByMeshId(MeshID meshId);
}