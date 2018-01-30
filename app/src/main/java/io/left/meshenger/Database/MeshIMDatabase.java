package io.left.meshenger.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import io.left.meshenger.Models.User;

/**
 * Manages versioning and exposed queries for the database for MeshIM.
 */
@Database(entities = {User.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class MeshIMDatabase extends RoomDatabase {
    public abstract MeshIMDao meshIMDao();
}
