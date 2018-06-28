package pt.isel.ncml.objectivedb.exception

/**
 * Created by nuno on 4/23/17.
 */

/**
 * Base checked exception for project.
 * Open modules written in kotlin visible from Java must add @Throws annotation
 * Makes part of Util module because core should not be a dependency
 */
open class DbException : Exception {
    constructor(cause:Throwable) : super(cause)
    constructor(msg:String) : super(msg)
    constructor(msg:String, cause:Throwable) : super(msg, cause)
}

/**
 * A more serious, mainly unrecoverable exception, that can be thrown.
 */
open class DbError : RuntimeException {
    constructor(cause:Throwable) : super(cause)
    constructor(msg:String) : super(msg)
    constructor(msg:String, cause:Throwable) : super(msg, cause)
}