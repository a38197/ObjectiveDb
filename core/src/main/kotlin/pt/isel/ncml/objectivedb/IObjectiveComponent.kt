package pt.isel.ncml.objectivedb

import dagger.Component
import pt.isel.ncml.objectivedb.configuration.ConfigModule
import pt.isel.ncml.objectivedb.identity.IdentityModule
import pt.isel.ncml.objectivedb.query.QueryModule
import pt.isel.ncml.objectivedb.reflector.ReflectorModule
import pt.isel.ncml.objectivedb.serialization.SerializationModule
import pt.isel.ncml.objectivedb.storage.StorageModule
import javax.inject.Singleton

/**
 * Created by mlourenc on 4/18/2017.
 */
@Singleton
@Component(
        modules = arrayOf(ConfigModule::class,
                ObjectiveDbModule::class,
                SerializationModule::class,
                IdentityModule::class,
                ReflectorModule::class,
                StorageModule::class,
                ObjectiveLockModule::class,
                QueryModule::class
        )
)
interface IObjectiveComponent {
    fun getDb() : ObjectiveDB
}
