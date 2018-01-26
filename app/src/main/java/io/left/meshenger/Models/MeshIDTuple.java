package io.left.meshenger.Models;

import android.arch.persistence.room.ColumnInfo;

import io.left.rightmesh.id.MeshID;

/**
 * Tuple for querying if a MeshID has been seen before.
 */
public class MeshIDTuple {
    @ColumnInfo(name = "UserID")
    public int id;

    @ColumnInfo(name = "UserMeshID")
    public MeshID meshId;
}