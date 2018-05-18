package io.left.meshim.database;

import android.arch.persistence.room.TypeConverter;

import io.left.rightmesh.id.MeshId;

import java.util.Date;

/**
 * A collection of {@link TypeConverter}s for use in {@link MeshIMDatabase}.
 */
public class Converters {
    /**
     * Initializes a {@link MeshId} from bytes stored in SQLite.
     * @param bytes representing a UUID
     * @return MeshId with UUID specified in parameters
     */
    @TypeConverter
    public MeshId meshIdFromBytes(byte[] bytes) {
        return new MeshId(bytes);
    }

    /**
     * Converts a {@link MeshId} to a byte array for storage in SQLite.
     * @param id MeshId to be converted
     * @return converted UUID of provided MeshId
     */
    @TypeConverter
    public byte[] bytesFromMeshId(MeshId id) {
        return id.getRawMeshId();
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