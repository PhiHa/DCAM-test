# App database migrations

This package owns native Room database versioning for the app.

The table/entity manifest lives one level up in `AppDatabase.java`.

`AppDatabaseMigrations.java` is the single place for:

- named version constants,
- `LATEST_VERSION`,
- all `Migration(from, to)` objects,
- the ordered migration registry passed to Room.

Native Room behavior:

- Fresh install: Room creates the latest schema from `@Entity` definitions.
- Existing install: Room runs the registered migrations needed to reach latest.

## Adding the next app DB version

1. Add a named version constant, for example `V3_ADD_UPLOAD_JOBS = 3`.
2. Set `LATEST_VERSION` to the new constant.
3. Add the adjacent native Room migration, for example `V2_TO_V3`.
4. Append the migration to `ALL` in order.
5. Update Room entities/DAOs.
6. Run `./gradlew assembleDebug` so Room exports the new JSON schema snapshot
   in `app/schemas`.
7. Add or update migration tests.

Never renumber, edit, or remove a released migration. Add the next version
instead.
