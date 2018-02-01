package io.left.meshenger.Database;

import android.arch.persistence.room.TypeConverter;

import io.left.rightmesh.id.MeshID;

import java.util.Date;

/**
 * A collection of {@link TypeConverter}s for use in {@link MeshIMDatabase}.
 */
public class Converters {
    /**
     * Initializes a {@link MeshID} from bytes stored in SQLite.
     * @param bytes representing a UUID
     * @return MeshID with UUID specified in parameters
     */
    @TypeConverter
    public MeshID meshIdFromBytes(byte[] bytes) {
        return new MeshID(bytes);
    }

    /**
     * Converts a {@link MeshID} to a byte array for storage in SQLite.
     * @param id MeshID to be converted
     * @return converted UUID of provided MeshID
     */
    @TypeConverter
    public byte[] bytesFromMeshId(MeshID id) {
        return id.getRawUuid();
    }

    /**
     * Initializes a {@link Date} from a long stored in SQLite.
     * @param l representing UNIX time in milliseconds
     * @return initialized Date
     */
    @TypeConverter
    public Date dateFromLong(long l) {
        return new Date(l);
    }

    /**
     * Returns the UNIX timestamp from a {@link Date} for storage in SQLite.
     * @param date date to get time from
     * @return UNIX timestamp
     */
    @TypeConverter
    public long longFromDate(Date date) {
        return date.getTime();
    }
}