package pt.isel.ncml.objectivedb.util

/**
 * Same as guava memoize but with platform types
 */
fun <T> memorize(original: () -> T) : () -> T {
    var temp : () -> T
    //have to define variable first to be referenced in lambda
    temp = {
        val value = original()
        temp = { value }
        value
    }
    return temp
}

/**
 * Same as guava memoize but with platform types
 */
fun <T> memorizeSync(original: () -> T) : () -> T {
    return SyncSupp(original)::invoke
}

private class SyncSupp<T>(private val original: () -> T) {
    @Volatile
    private var value : T? = null
    private val lock = Any()

    fun invoke() : T {
        if(null != value){
            return value!!
        }

        synchronized(lock){
            if(null != value){
                return value!!
            }

            value = original()
            return value!!
        }
    }
}
