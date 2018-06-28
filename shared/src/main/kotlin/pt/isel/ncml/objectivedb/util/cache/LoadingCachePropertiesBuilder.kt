package pt.isel.ncml.objectivedb.util.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Utility object for building [LoadingCache].
 * Uses [Properties] for main configurations.
 */
object LoadingCachePropertiesBuilder {

    private val DF_INIT_CAP = "10"
    private val DF_CONCURR_LEVEL = "3"

    val PROP_CONCUR_LEVEL = "concurrency-level"
    val PROP_EXP_AFTER_ACCESS = "expires-after-access"
    val PROP_EXP_AFTER_WRITE = "expires-after-write"
    val PROP_INIT_CAP = "initial-capacity"

    fun <K, V> buildCache(prefix: String, properties: Properties, loader: CacheLoader<K, V>): LoadingCache<K, V> {
        return buildCache(prefix, properties, loader, CacheBuilder.newBuilder())
    }

    fun <K, V> buildCache(prefix: String, properties: Properties, loader: CacheLoader<K, V>, builder: CacheBuilder<Any, Any>): LoadingCache<K, V> {
        val sanitized = sanitizePrefix(prefix)

        return mapExpiresAfter(builder, sanitized, properties)
                .initialCapacity(initialCapacity(sanitized, properties))
                .concurrencyLevel(concurrencyLevel(sanitized, properties))
                .build(loader)
    }

    private fun concurrencyLevel(sanitized: String, properties: Properties): Int {
        return Integer.parseInt(properties.getProperty(sanitized + PROP_CONCUR_LEVEL, DF_CONCURR_LEVEL))
    }

    private fun initialCapacity(sanitized: String, properties: Properties): Int {
        return Integer.parseInt(properties.getProperty(sanitized + PROP_INIT_CAP, DF_INIT_CAP))
    }


    private fun mapExpiresAfter(builder: CacheBuilder<Any, Any>, sanitized: String, properties: Properties): CacheBuilder<Any, Any> {
        val accessExpires = properties.getProperty(sanitized + PROP_EXP_AFTER_ACCESS)
        val writeExpires = properties.getProperty(sanitized + PROP_EXP_AFTER_WRITE)
        if (null != accessExpires)
            builder.expireAfterAccess(java.lang.Long.parseLong(accessExpires), TimeUnit.MILLISECONDS)
        if (null != writeExpires)
            builder.expireAfterWrite(java.lang.Long.parseLong(writeExpires), TimeUnit.MILLISECONDS)
        return builder
    }

    private fun sanitizePrefix(prefix: String): String {
        if (prefix.endsWith(".")) {
            return prefix
        } else {
            return prefix + "."
        }
    }
}//Utils class
