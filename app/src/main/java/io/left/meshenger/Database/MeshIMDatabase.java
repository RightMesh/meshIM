package io.left.meshenger.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import io.left.meshenger.Models.User;

@Database(entities = {User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class MeshIMDatabase extends RoomDatabase {
    public abstract MeshIMDao meshIMDao();
}
