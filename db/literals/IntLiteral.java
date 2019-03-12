package db.literals;

/**
 * Created by admohanraj on 2/22/17.
 */
public class IntLiteral implements Literal {
    public static final String type = "int";
    private int object;

    public IntLiteral(Integer x) {
        object = x;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Integer getObject() {
        return object;
    }

    @Override
    public String toString() {
        return object + "";
    }

    /** Returns whether this equals object. */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntLiteral) {
            return ((IntLiteral) obj).getObject().equals(this.object);
        } else if (obj instanceof FloatLiteral) {
            return ((FloatLiteral) obj).getObject().equals((double) this.object);
        }
        return false;
    }

    @Override
    public boolean isZero() {
        if (object == 0) {
            return true;
        }
        return false;
    }
}
