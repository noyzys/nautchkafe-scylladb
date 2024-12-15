                                                # nautchkafe-scylladb 

**Lightweight, functional, modular, and flexible way to interact system built with ScyllaDB.**
[scylla.db](https://github.com/scylladb/java-driver)

### Clone the Repository

To get started, clone the repository and build the project:

```bash
git clone https://github.com/noyzys/nautchkafe-scylladb.git
cd nautchkafe-scylladb
```

### Build and Run
Navigate to the project directory:
```bash
./gradlew build
```

## Features

- **ScyllaDB Integration**: Provides seamless interaction with ScyllaDB via the Java Driver.
- **Functional Programming with Vavr**: Utilizes Vavr’s `Try`, `Option`, and other functional types to handle errors, nulls, and side effects more elegantly.
- **Modular & Extensible**: Components such as repositories, queries, and session management are decoupled and highly modular.
- **Asynchronous and Synchronous Operations**: Supports both async and sync operations, making it suitable for high-performance applications.
- **SQL Constants**: The SQL queries are abstracted and managed via constants for better maintainability and readability.

## Components by ScyllaDBRepository (CRUD):

- **findAll()** - Retrieves all rows from a table.
- **find()** - Retrieves a row based on a custom predicate.
- **save()** - Saves an entity to the database.
- **delete()** - Deletes an entity based on a predicate.
- **findAllAsync()** - Retrieves all rows asynchronously.
- **findAsync()** - Retrieves a row asynchronously based on a predicate.
- **saveAsync()** - Saves an entity asynchronously.
- **deleteAsync()** - Deletes an entity asynchronously.

## Example use case (Java)

```java
final class ScyllaApplication {

    public static void main(String[] args) {
        // Creating a session manager
        ScyllaSessionCoordinator sessionManager = new ScyllaSessionCoordinator();
        Try<ScyllaResource<Session>> session = sessionManager.openSession("localhost", "keyspace");

        session.onSuccess(resource -> {
            // Using the session
            resource.use(session -> {
                // Initializing query executor
                QueryExecutor executor = new ScyllaQueryExecutor(session);

                // Example of saving a user
                ScyllaUser newUser = new ScyllaUser("Frankie", 30);
                ScyllaRepository<ScyllaUser> userRepository = new ScyllaDBRepository(executor, "users", User::getId, User::toBindValues, User::mapRow);

                // Saving the user
                userRepository.save(newUser).onSuccess(res -> System.out.println("User saved"));

                // Example of fetching users
                userRepository.findAll().onSuccess(users -> users.forEach(user -> System.out.println("Found user: " + user.name())));

                // Example of deleting a user
                userRepository.delete(user -> user.name().equals("Frankie")).onSuccess(res -> System.out.println("User deleted"));


                /* ......................... */
                // Example of resultmapper
                ScyllaResultMapper<ScyllaUser> userMapper = row -> new ScyllaUser(
                    row.getColumn("name", String.class),
                    row.getColumn("age", Integer.class)
                );

                Function<User, List<Object>> userBinder = user -> List.of(user.name(), user.age());

                ScyllaTableCoordinator tableCoordinator = new ScyllaTableCoordinator(queryExecutor);
                Try<Void> createTable = tableCoordinator.createTable("users", "name TEXT PRIMARY KEY, age INT");

                if (createTable.isFailure()) {
                    System.err.println("Failed to create table");
                    return;
                }

                // Insert Example User
                ScyllaUser user = new ScyllaUser("Pookoty", 30);
                Try<Void> saveResult = userRepository.save(user);
                if (saveResult.isFailure()) {
                    System.err.println("Failed to save user");
                    return;
                }

                // Fetch All Users (Sync)
                Try<List<ScyllaUser>> users = userRepository.findAll();
                if (users.isFailure()) {
                    System.err.println("Failed to fetch users");
                    return;
                }

                users.get().forEach(u -> System.out.println(u.name() + " - " + u.age()));

                // Fetch User By Predicate (Async), u - user
                CompletableFuture<Option<User>> userOptionAsync = userRepository.findAsync(u -> u.age() > 25);
                userOptionAsync.thenAccept(userOpt -> userOpt
                    .peek(u -> System.out.println("Found user: " + u.name()))
                    .onEmpty(() -> System.out.println("No user found")));

                // Delete User Async
                CompletableFuture<Void> deleteAsyncResult = userRepository.deleteAsync(u -> u.name().equals("Pookoty"));
                deleteAsyncResult.thenRun(() -> System.out.println("User deleted"));

                // Drop Table
                Try<Void> dropTableTry = tableCoordinator.dropTable("users");
                if (dropTableTry.isFailure()) {
                    System.err.println("Failed to drop table");
                }

                // Close Session
                sessionCoordinator.closeSession(session);
            });
        }).onFailure(e -> System.err.println("Failed to connect to ScyllaDB: " + e.getMessage()));
    }
}
```

### Code Breakdown

**1. Session Coordinator: Establishing Connection**
```java
ScyllaSessionCoordinator sessionManager = new ScyllaSessionCoordinator();
Try<ScyllaResource<Session>> session = sessionManager.openSession("localhost", "keyspace");
```
- **Function: Establish a connection to the ScyllaDB using the ScyllaSessionCoordinator.**
- **Explanation: The openSession function tries to create a session with ScyllaDB, establishing a connection to the specified keyspace.**


**2. Using the Session**
```java
session.onSuccess(resource -> {
    resource.use(session -> {
        // The session is now ready to be used for queries and transactions.
    });
});
```
- **Function: The use method of ScyllaResource ensures the session is used for the scope of the block and safely closed after.**
- **Explanation: The session is passed to the repository or query executor inside the use block, and it will be closed automatically after the operations.**


**3. Initializing the Query Executor**
```java
QueryExecutor executor = new ScyllaQueryExecutor(session);
```


**4. Creating the Repository**
```java
ScyllaRepository<ScyllaUser> userRepository = new ScyllaDBRepository(
    executor, "users", ScyllaUser::id, ScyllaUser::toBindValues, ScyllaUser::mapRow
);
```
- **Function: Create a ScyllaRepository for the ScyllaUser entity.
- **The repository is responsible for CRUD operations, and is parameterized with the ScyllaUser entity.
It takes functions such as User::id, User::toBindValues, and User::mapRow to handle primary key extraction, data binding, and row mapping.**


**5. Saving a User**
```java
ScyllaUser newUser = new ScyllaUser("Nautchkafe", 30);
userRepository.save(newUser).onSuccess(res -> System.out.println("User saved"));
```
- **Function: Save a new user to the database.**
- **Explanation: The save method binds the newUser to the insert statement using toBindValues and executes the query**



**6. Fetching All Users**
```java
userRepository.findAll().onSuccess(users -> users.forEach(user -> System.out.println("Found user: " + user.name())));
```


**7. Deleting a User**
```java
userRepository.delete(user -> user.name().equals("Nautchkafe")).onSuccess(res -> System.out.println("User deleted");
```


**8. Mapping Result Rows**
```java
ScyllaResultMapper<ScyllaUser> userMapper = row -> new ScyllaUser(
    row.getColumn("name", String.class),
    row.getColumn("age", Integer.class)
);
```


**9. Binding User Data to Query**
```java
Function<User, List<Object>> userBinder = user -> List.of(user.name(), user.age());
```


**10. Creating a Table**
```java
ScyllaTableCoordinator tableCoordinator = new ScyllaTableCoordinator(queryExecutor);
Try<Void> createTable = tableCoordinator.createTable("users", "name TEXT PRIMARY KEY, age INT");
```


**11. Inserting Data**
```java
ScyllaUser user = new ScyllaUser("Nautchkafe", 30);
Try<Void> saveResult = userRepository.save(user);
```

### Asynchronously operations

**12. Fetching Users Asynchronously**
```java
CompletableFuture<Option<ScyllaUser>> userOptionAsync = userRepository.findAsync(user -> user.age() > 25);
userOptionAsync.thenAccept(userOpt -> userOpt
    .peek(user -> System.out.println("Found user: " + user.name()))
    .onEmpty(() -> System.out.println("No user found"))
);
```


**13. Deleting Data Asynchronously**
```java
CompletableFuture<Void> deleteAsyncResult = userRepository.deleteAsync(user -> user.name().equals("Nautchkafe"));
deleteAsyncResult.thenRun(() -> System.out.println("User deleted"));
```


**14. Dropping a Table**
```java
Try<Void> dropTableTry = tableCoordinator.dropTable("users");
if (dropTableTry.isFailure()) {
    System.err.println("Failed to drop table");
}

```

**Closing the Session**
```java
sessionCoordinator.closeSession(session);
```

### `ScyllaQueryExecutor`

- Handles the execution of SQL queries on ScyllaDB. It supports both synchronous and asynchronous query execution.

### `ScyllaSessionCoordinator`

- Manages the session lifecycle and ensures connection to ScyllaDB. It is responsible for opening and closing sessions.

### `ScyllaResource`

- A wrapper around `Try` that ensures the safe use of resources, automatically closing them once done. The `use` method ensures that resources are properly handled.

### `ScyllaTableCoordinator`

- Handles operations related to ScyllaDB tables such as creating, dropping, truncating, and checking if a table exists.

### `ScyllaResultMapper`

- A functional interface that maps a `Row` from ScyllaDB to a custom object. This can be used for mapping result sets from queries to domain objects.

**This example showcases how to integrate ScyllaDB with a functional approach.**

**If you are interested in exploring functional programming and its applications within this project visit the repository at [vavr-in-action](https://github.com/noyzys/bukkit-vavr-in-action), [fp-practice](https://github.com/noyzys/fp-practice).**