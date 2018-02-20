package io.left.meshim.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

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
           // database.execSQL("ALTER TABLE Messages"+ "CREATE INDEX `index_Messages_isRead` (`isRead`) ");
            database.execSQL("ALTER TABLE Messages "
                    + " ADD COLUMN isRead INTEGER");
        }
    };

        public static final Migration[] ALL_MIGRATIONS = {MIGRATION_1_2, MIGRATION_2_3,MIGRATION_3_4 };
}
//CHECKSTYLE END IGNORE LineLengthCheck