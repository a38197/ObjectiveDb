package pt.isel.ncml.objectivedb.reflector.manipulator;

/**
 * Created by ncaro on 3/30/2017.
 */
public class PublicDataClass {
    public int i1;
    public int i2;
    public boolean b1;
    public long l1;

    public PublicDataClass(int i1, int i2, boolean b1, long l1) {
        this.i1 = i1;
        this.i2 = i2;
        this.b1 = b1;
        this.l1 = l1;
    }

    public int getI1() {
        return i1;
    }

    public void setI1(int i1) {
        this.i1 = i1;
    }

    public int getI2() {
        return i2;
    }

    public void setI2(int i2) {
        this.i2 = i2;
    }

    public boolean isB1() {
        return b1;
    }

    public void setB1(boolean b1) {
        this.b1 = b1;
    }

    public long getL1() {
        return l1;
    }

    public void setL1(long l1) {
        this.l1 = l1;
    }
}
