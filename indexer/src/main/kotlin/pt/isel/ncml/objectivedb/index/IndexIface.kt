package pt.isel.ncml.objectivedb.index


interface IIndexer {
    fun useIndex(cls: Class<*>, fields: Sequence<String>): Sequence<(Any) -> Any>?
    fun setIndex(cls: Class<*>, field: String)
    fun setIndex(cls: Class<*>, fields: Sequence<String>)
    fun getIndexData(obj: Any): Sequence<Any>?
}

interface IndexerFactory : () -> IIndexer
