package io.left.meshim.database;

import static io.left.meshim.database.Migrations.MIGRATION_1_2;
import static io.left.meshim.database.Migrations.MIGRATION_2_3;
import static io.left.meshim.database.Migrations.MIGRATION_3_4;
import static io.left.meshim.database.Migrations.MIGRATION_4_5;
import static io.left.meshim.database.Migrations.MIGRATION_5_6;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import io.left.rightmesh.id.MeshId;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies that Room schema migrations work.
 */
@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    /**
     * Initializes {@link MigrationTest#helper}.
     */
    public MigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                MeshIMDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    /**
     * Renamed several columns in Users table. Verifies that values survive the migration.
     *
     * @throws IOException if the database can't open
     */
    @Test
    public void migrate1To2() throws IOException {
        // Dummy values.
        int id = 42;
        MeshId meshId = new MeshId();
        String meshIdUuid = meshId.toString().substring(2); // String representation for insertion.
        String userName = "John";
        int avatar = 2;

        // Insert dummy values into database manually.
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);
        db.execSQL("INSERT INTO Users (UserID, UserMeshID, UserName, UserAvatar)"
                + " VALUES ( " + id + ", X'" + meshIdUuid + "', \"" + userName + "\", "
                + avatar + ")");
        db.close();

        // Update and validate database.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2);
        Cursor cursor
                = db.query("SELECT UserID, MeshID, Username, Avatar FROM Users WHERE UserID = 42");

        // Assert the row was found.
        Assert.assertEquals("Row is found", cursor.getCount(), 1);
        cursor.moveToNext();

        // Check values were preserved.
        Assert.assertArrayEquals("MeshID is preserved", cursor.getBlob(1), meshId.getRawMeshId());
        Assert.assertEquals("Username is preserved", cursor.getString(2), userName);
        Assert.assertEquals("Avatar is preserved", cursor.getInt(3), avatar);
    }

    /**
     * Addition of Messages table - as long as schema verifies it should be fine.
     *
     * @throws IOException if the database can't open
     */
    @Test
    public void migrate2To3() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3);
    }

    /**
     * Addition of IsRead column in the Messages table. sets the default values for IsRead to true
     * for messages before the migration.
     * @throws IOException if the database cant open.
     */
    @Test
    public void migrate3To4() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 3);
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_3_4);
    }

    /**
     * Addition of IsDelivered column in the Messages table. sets the default values for IsDelivered
     * to true for messages before the migration.
     * @throws IOException if the database cant open.
     */
    @Test
    public void migrate4To5() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 4);
        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_5);
    }

    /**
     * @throws IOException if the database cant open.
     */
    @Test
    public void migrate5to6() throws IOException {

        // Dummy values.
        int id = 42;
        MeshId meshId = new MeshId();
        String meshIdUuid = meshId.toString().substring(2); // String representation for insertion.
        String userName = "John";
        int avatar = 2;
        int senderId = 10;
        int recipientId = 11;

        // Insert dummy values into database manually.
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 5);
        db.execSQL("INSERT INTO Users (UserID, MeshID, Username, Avatar)"
                + " VALUES ( " + id + ", X'" + meshIdUuid + "', \"" + userName + "\", "
                + avatar + ")");
        db.execSQL("INSERT INTO Messages (MessageId, Contents, Timestamp, SenderId, RecipientId," +
                "SentFromDevice, IsRead, IsDelivered)" +
                "VALUES ( " + id + ", \"Hello World\", " + 1 + ", " + senderId + ", " + recipientId
                + ", " + 1 + ", " + 1 + ", " + 1 + ")");

        db.close();
        db = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6);

        // Check User values
        Cursor cursor
                = db.query("SELECT UserId, MeshId, Username, Avatar FROM Users WHERE UserId = 42");
        Assert.assertEquals("Row is found", cursor.getCount(), 1);
        cursor.moveToNext();
        Assert.assertArrayEquals("MeshId is preserved", cursor.getBlob(1), meshId.getRawMeshId());

        // Check Message values.
        cursor = db.query("SELECT MessageId, SenderId, RecipientId FROM Messages "
                + "WHERE MessageId = 42");
        Assert.assertEquals("Row is found", cursor.getCount(), 1);
        cursor.moveToNext();
        Assert.assertEquals("MessageId is preserved", id, cursor.getInt(0));
        Assert.assertEquals("SenderId is preserved", senderId, cursor.getInt(1));
        Assert.assertEquals("RecipientId is preserved", recipientId, cursor.getInt(2));
    }
}