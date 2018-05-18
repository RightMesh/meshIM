package io.left.meshim.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import io.left.meshim.models.Message;
import io.left.meshim.models.User;

/**
 * Manages versioning and exposed queries for the database for meshIM.
 */
@Database(entities = {User.class, Message.class}, version = 6)
@TypeConverters({Converters.class})
public abstract class MeshIMDatabase extends RoomDatabase {
    public abstract MeshIMDao meshIMDao();
}
