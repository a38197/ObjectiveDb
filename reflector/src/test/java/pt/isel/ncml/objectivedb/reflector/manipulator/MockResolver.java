package pt.isel.ncml.objectivedb.reflector.manipulator;

public class MockResolver implements IObjectResolver {
    @Override
    public byte[] toByteArray(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getReferenceSize() {
        throw new UnsupportedOperationException();
    }
}
