package com.dvid.dcam.data.db.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Single app-level registry for Room database versions and migrations.
 *
 * <p>Native Room flow:
 * <ul>
 *     <li>Fresh install: Room creates {@link #LATEST_VERSION} from entities.</li>
 *     <li>Existing install: Room runs the registered migrations in order.</li>
 * </ul>
 *
 * <p>When adding a version, keep all version constants, migration objects, and
 * the central {@link #ALL} registry together in this file.
 */
public final class AppDatabaseMigrations {
    public static final int V1_INITIAL_SCHEMA = 1;
    public static final int V2_LOG_OUTBOX_RETRY_STATE = 2;

    public static final int LATEST_VERSION = V2_LOG_OUTBOX_RETRY_STATE;

    private static final Migration V1_TO_V2 = new Migration(
            V1_INITIAL_SCHEMA,
            V2_LOG_OUTBOX_RETRY_STATE
    ) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE pending_logs ADD COLUMN status TEXT NOT NULL DEFAULT 'PENDING'");
            database.execSQL("ALTER TABLE pending_logs ADD COLUMN attemptCount INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE pending_logs ADD COLUMN nextAttemptAt INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE pending_logs ADD COLUMN firstFailedAt INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE pending_logs ADD COLUMN lastError TEXT");
        }
    };

    private static final Migration[] ALL = {
            V1_TO_V2
    };

    private AppDatabaseMigrations() {
    }

    /** Returns a copy so callers cannot mutate the central migration registry. */
    public static Migration[] all() {
        return ALL.clone();
    }

    /*
     * Template for the next version:
     *
     * 1. Add:
     *      public static final int V3_MEANINGFUL_NAME = 3;
     *
     * 2. Point LATEST_VERSION at V3_MEANINGFUL_NAME.
     *
     * 3. Add:
     *      private static final Migration V2_TO_V3 = new Migration(
     *              V2_LOG_OUTBOX_RETRY_STATE,
     *              V3_MEANINGFUL_NAME
     *      ) {
     *          @Override
     *          public void migrate(@NonNull SupportSQLiteDatabase database) {
     *              database.execSQL("ALTER TABLE pending_logs ADD COLUMN example TEXT");
     *          }
     *      };
     *
     * 4. Append V2_TO_V3 to ALL in order.
     *
     * 5. Build so Room exports app/schemas/.../3.json, then add a migration test.
     */
}
