# App database

This platform package is the app-level Room database boundary.

## Where to look

- `AppDatabase.java`: database manifest. It lists every Room entity/table and
  every DAO exposed by the app database.
- `entities/`: Room entity classes owned by the database layer.
- `dao/`: Room DAO interfaces owned by the database layer.
- `migrations/AppDatabaseMigrations.java`: version constants, native Room
  migrations, and the ordered migration registry.
- `app/schemas`: exported Room schema snapshots generated during builds.

## Native Room flow

- Fresh install: Room creates the latest schema from the entity list in
  `AppDatabase.java`.
- Existing install: Room runs the native migrations from
  `AppDatabaseMigrations.java`.

## Adding a table

1. Create the `@Entity` in `entities/` and DAO in `dao/`.
2. Add the entity class to `AppDatabase.java`.
3. Add the DAO accessor to `AppDatabase.java`.
4. Add the next version and migration to `AppDatabaseMigrations.java`.
5. Run `./gradlew assembleDebug` and commit the new schema JSON.

`AppDatabaseManifestTest` scans source files and fails if any `@Entity` class is
missing from `AppDatabase.java`, so the entity manifest is enforced by tests
rather than maintained by memory.
