package pt.isel.ncml.objectivedb.test.reflectorstubs

import pt.isel.ncml.objectivedb.reflector.IReflector
import pt.isel.ncml.objectivedb.reflector.manipulator.IFieldStateManipulator

/**
 * Created by Mario on 2017-05-19.
 */
class ReflectorStub : IReflector {
    override fun getFieldManipulator(objClass: Class<*>?): IFieldStateManipulator {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}