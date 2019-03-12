package db.literals;

/**
 * Created by admohanraj on 2/22/17.
 */
public class FloatLiteral implements Literal {
    public static final String type = "float";
    private static final double epsilon = 0.001;
    private double object = 0.0;

    public FloatLiteral(Double x) {
        object = x;
    }

    public String getType() {
        return type;
    }

    @Override
    public Double getObject() {
        return object;
    }

    @Override
    public String toString() {
        return String.format("%.3f", object);
    }

    /** Returns whether this equals object. */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FloatLiteral) {
            return ((FloatLiteral) obj).getObject().equals(this.object);
        } else if (obj instanceof IntLiteral) {
            return ((Double) ((IntLiteral) obj).getObject().doubleValue()).equals(this.object);
        }
        return false;
    }

    @Override
    public boolean isZero() {
        return Math.abs(object - 0.0) < epsilon;
    }
}
