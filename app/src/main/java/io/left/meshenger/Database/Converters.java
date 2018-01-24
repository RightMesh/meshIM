package io.left.meshenger.Database;

import android.arch.persistence.room.TypeConverter;

import io.left.rightmesh.id.MeshID;

public class Converters {
    @TypeConverter
    public MeshID meshIdFromBytes(byte[] bytes) {
        return new MeshID(bytes);
    }

    @TypeConverter
    public byte[] bytesFromMeshId(MeshID id) {
        return id.getRawUuid();
    }
}