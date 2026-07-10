package com.dvid.dcam.platform.database.migrations;

import androidx.room.migration.Migration;

/**
 * Single app-level registry for Room database versions and migrations.
 *
 * <p>Native Room flow:
 * <ul>
 *     <li>Fresh install: Room creates {@link #LATEST_VERSION} from entities.</li>
 *     <li>Existing install: Room runs the registered migrations in order.</li>
 * </ul>
 *
 * <p>The current app database has not shipped yet, so unreleased schema work is
 * folded into version 1. Once a version ships, add future version constants,
 * migration objects, and the central {@link #ALL} registry entry together here.
 */
public final class AppDatabaseMigrations {
    public static final int V1_INITIAL_SCHEMA = 1;

    public static final int LATEST_VERSION = V1_INITIAL_SCHEMA;

    private static final Migration[] ALL = {};

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
     *      public static final int V2_MEANINGFUL_NAME = 2;
     *
     * 2. Point LATEST_VERSION at V2_MEANINGFUL_NAME.
     *
     * 3. Add:
     *      private static final Migration V1_TO_V2 = new Migration(
     *              V1_INITIAL_SCHEMA,
     *              V2_MEANINGFUL_NAME
     *      ) {
     *          @Override
     *          public void migrate(@NonNull SupportSQLiteDatabase database) {
     *              database.execSQL("ALTER TABLE pending_logs ADD COLUMN example TEXT");
     *          }
     *      };
     *
     * 4. Append V1_TO_V2 to ALL in order.
     *
     * 5. Build so Room exports app/schemas/.../2.json, then add a migration test.
     */
}
