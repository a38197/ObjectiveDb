## Objective DB

Graduation Project for the Computer Science Engineer Course.

This project consisted in the analysis and development of an application that could theoretically persist
any object without any type on preparation or intrusion, while preserving its identity. It also differs from
other databases because it tracks references in the object graph and cleans objects that are no longer relevant
for the user.

The project spanned across 4 a month period in which all the planning, execution and reporting was done by a 2
person team. Some compromises were taken in order to accommodate time constraints, such as incomplete or non optimal 
feature implementations.
This repository contains only the code portion of the project.

### Instantiation and usage

A database instance can be produced by creating a database configuration. The only mandatory parameter is the
database name.
```kotlin
fun createDatabaseInstance() {
     val customConfig = Builder
             .configBuilder()
             .putImmutableIndex(TestDto::class.java.name, "numberField")
             .build("DbFileName.odb")
             
     val objectiveDB: ObjectiveDB = Builder.dbBuilder()
             .setStorageFilter(CustomStoreFilter())
             .setLockStrategy(::CustomLock)
             .build(customConfig)
}
```

With the database instance is possible to store any object with no additional modifications.
````kotlin
fun objectManage() {
        data class Node(val name:String, val index:Int, val next:Node?)
        val first = Node("first", 0, Node("second", 1, null))
        db.manage(first)
}
````

### Identity

The database knows the concept of object identity. While some instance of class `A` exists in memory, it is guaranteed
that all queries that point to that instance will return it. Once the object is no longer in memory, a new instance
its created and the state restored to the last `manage` operation preformed.

This concept requires the track of references between objects. The database manages all those references only releasing
an object from persistence once no one references it. Much like the reference counting used by some GC algorithms,
a similar method is used to track references from `roots`.

A `root` is any object that is explicitly stored using a `manage` operation, being considered relevant for the user. 

### Storage

Any object and its dependency graph is persisted to disk on each `manage` operation maintaining a snapshot of the root's
internal state. While in memory, the object's graph can suffer changes, and if no `manage` is issued, those changes
will be lost, much like any other database.

The current storage implementation uses Google's `LevelDb` with Java bindings but some other implementation can be
swapped as long as the interface is respected.  

### Querying

A layer of querying has been set up as a way of retrieving object from the database. This layer supports two methods
of retrieval.

- Metadata queries: designed to provide specific metadata information and take advantage of special features like 
field indexes
````kotlin
    fun metadataQuery(){
        val data: Collection<TestDto> = db
                .query()
                    .from(TestDto::class.java)
                        .where(ExprImpl.Gt(3, "numberField"))
                        .or()
                        .inner()
                            .where(ExprImpl.Lt(3, "numberField"))
                            .and()
                            .where(ExprImpl.Constant(true, "booleanField"))
                        .end()
                    .end()
                .query()
    }
````

- Predicate queries: fluent queries using lambda expressions for ease of use
````kotlin
    fun predicateQuery(){
        val data: Collection<TestDto> = db
                .query()
                    .from(TestDto::class.java)
                        .where { it.numberField > 3 }
                        .or()
                        .inner()
                            .where { it.numberField < 3 }
                            .and()
                            .where { it.booleanField == true }
                        .end()
                    .end()
                .query()
    }
````