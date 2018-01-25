package io.left.meshenger.Database;

import android.arch.persistence.room.TypeConverter;

import io.left.rightmesh.id.MeshID;

/**
 * A collection of {@link TypeConverter}s for use in {@link MeshIMDatabase}.
 */
class Converters {
    /**
     * Initializes a {@link MeshID} from bytes stored in SQLite.
     * @param bytes representing a UUID
     * @return MeshID with UUID specified in parameters
     */
    @TypeConverter
    MeshID meshIdFromBytes(byte[] bytes) {
        return new MeshID(bytes);
    }

    /**
     * Converts a {@link MeshID} to a byte array for storage in SQLite.
     * @param id MeshID to be converted
     * @return converted UUID of provided MeshID
     */
    @TypeConverter
    byte[] bytesFromMeshId(MeshID id) {
        return id.getRawUuid();
    }
}