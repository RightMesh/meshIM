package io.left.meshim.models;

import android.arch.persistence.room.ColumnInfo;

import io.left.rightmesh.id.MeshId;

/**
 * Tuple for querying if a MeshId has been seen before.
 */
public class MeshIdTuple {
    @ColumnInfo(name = "UserId")
    public int id;

    @ColumnInfo(name = "MeshId")
    public MeshId meshId;
}