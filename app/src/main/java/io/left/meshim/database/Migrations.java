package io.left.meshim.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

//CHECKSTYLE IGNORE LineLengthCheck
public class Migrations {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Users RENAME TO OldUsers");
            database.execSQL("CREATE TABLE `Users` (`UserID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `MeshID` BLOB, `Username` TEXT, `Avatar` INTEGER NOT NULL)");
            database.execSQL("CREATE UNIQUE INDEX `index_Users_UserID_MeshID` ON `Users` (`UserID`, `MeshID`)");
            database.execSQL("INSERT INTO Users(UserID, MeshID, Username, Avatar) SELECT UserID, UserMeshID, UserName, UserAvatar FROM OldUsers");
            database.execSQL("DROP TABLE OldUsers");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Messages` (`MessageID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Contents` TEXT, `Timestamp` INTEGER, `SenderID` INTEGER NOT NULL, `RecipientID` INTEGER NOT NULL, `SentFromDevice` INTEGER NOT NULL, FOREIGN KEY(`SenderID`) REFERENCES `Users`(`UserID`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`RecipientID`) REFERENCES `Users`(`UserID`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
            database.execSQL("CREATE  INDEX `index_Messages_RecipientID` ON `Messages` (`RecipientID`)");
            database.execSQL("CREATE  INDEX `index_Messages_SenderID` ON `Messages` (`SenderID`)");
        }
    };
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Messages "
                    + " ADD COLUMN IsRead INTEGER  NOT NULL DEFAULT 1");
        }

    };
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Messages "
                    + " ADD COLUMN IsDelivered INTEGER  NOT NULL DEFAULT 1");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Users RENAME TO OldUsers");
            database.execSQL("CREATE TABLE `Users` (`UserId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `MeshId` BLOB, `Username` TEXT, `Avatar` INTEGER NOT NULL)");
            database.execSQL("DROP INDEX `index_Users_UserID_MeshID`");
            database.execSQL("CREATE UNIQUE INDEX `index_Users_UserId_MeshId` ON `Users` (`UserId`, `MeshId`)");
            database.execSQL("INSERT INTO Users(UserId, MeshId, Username, Avatar) SELECT UserID, MeshID, Username, Avatar FROM OldUsers");
            database.execSQL("DROP TABLE OldUsers");

            database.execSQL("ALTER TABLE Messages RENAME TO OldMessages");
            database.execSQL("CREATE TABLE IF NOT EXISTS Messages (`MessageId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Contents` TEXT, `Timestamp` INTEGER, `SenderId` INTEGER NOT NULL, `RecipientId` INTEGER NOT NULL, `SentFromDevice` INTEGER NOT NULL, `IsRead` INTEGER NOT NULL, `IsDelivered` INTEGER NOT NULL, FOREIGN KEY(`SenderId`) REFERENCES `Users`(`UserId`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`RecipientId`) REFERENCES `Users`(`UserId`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
            database.execSQL("DROP INDEX `index_Messages_RecipientId`");
            database.execSQL("DROP INDEX `index_Messages_SenderId`");
            database.execSQL("CREATE INDEX `index_Messages_RecipientId` ON `Messages` (`RecipientId`)");
            database.execSQL("CREATE INDEX `index_Messages_SenderId` ON `Messages` (`SenderId`)");
            database.execSQL("INSERT INTO Messages(MessageId, Contents, Timestamp, SenderId, RecipientId, SentFromDevice, IsRead, IsDelivered) SELECT MessageID, Contents, Timestamp, SenderID, RecipientID, SentFromDevice, IsRead, IsDelivered FROM OldMessages");
            database.execSQL("DROP TABLE OldMessages");
        }
    };
    public static final Migration[] ALL_MIGRATIONS = {MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
            MIGRATION_4_5, MIGRATION_5_6 };
}
//CHECKSTYLE END IGNORE LineLengthCheck