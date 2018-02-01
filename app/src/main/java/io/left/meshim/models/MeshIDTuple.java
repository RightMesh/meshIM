package io.left.meshim.models;

import android.arch.persistence.room.ColumnInfo;

import io.left.rightmesh.id.MeshID;

/**
 * Tuple for querying if a MeshID has been seen before.
 */
public class MeshIDTuple {
    @ColumnInfo(name = "UserID")
    public int id;

    @ColumnInfo(name = "MeshID")
    public MeshID meshId;
}