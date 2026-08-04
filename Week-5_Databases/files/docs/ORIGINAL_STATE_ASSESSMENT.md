# Original-State Assessment

## Artifact reviewed

The exact original CS 360 Weight Tracker is preserved under
`original/Project2`. No source file in that folder was edited.
The original application was created in Android Studio with Java and SQLite and includes
registration, login, weight-entry CRUD controls, an SMS settings screen, layouts, resources,
Gradle files, and the original placeholder tests.

## Original schema

The original database helper creates these tables:

```sql
users(username TEXT PRIMARY KEY, password TEXT)
Userdetails(name TEXT PRIMARY KEY, weight TEXT, date TEXT)
```

Source: `original/Project2/app/src/main/java/com/example/project/DBHelper.java`,
lines 16-17.

## Specific original limitations

1. **Passwords are stored as plaintext.** `insertData()` places the supplied password directly
   into the `password` column, and `checkusernamepassword()` compares the supplied password
   directly in SQL. This means a database copy would expose every account password.
2. **Weight rows do not belong to an account.** `Userdetails` has no `user_id` column or foreign
   key. `getdata()` selects every row, regardless of who signed in.
3. **The primary key models a person instead of a measurement.** `name` is the primary key, so
   the same name cannot have a realistic history of multiple records.
4. **Numeric and date values are stored as unrestricted text.** SQLite cannot enforce a numeric
   weight range, and the date strings cannot be ordered reliably unless every caller uses the
   same format.
5. **Update and delete operations are not ownership-scoped.** They use only `name = ?`.
6. **Authentication does not create an account session.** After login, no stable user ID is sent
   to the dashboard.
7. **Registration contains a route around authentication.** The original Sign In button in
   `MainActivity4` opens `MainActivity2` directly.
8. **The database upgrade is destructive.** `onUpgrade()` drops both tables and does not recreate
   or migrate the stored records.
9. **Several cursors depend on `@SuppressLint("Recycle")` instead of explicit closure.**
10. **The test folders contain template tests.** They do not verify schema, CRUD behavior,
    account isolation, authentication, migration, constraints, or query ordering.

These limitations are the basis for the Milestone Four changes. They are not theoretical
problems added after the fact; each one can be located in the untouched original files.
