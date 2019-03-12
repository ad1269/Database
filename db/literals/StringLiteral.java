package db.literals;

/**
 * Created by admohanraj on 2/22/17.
 */
public class StringLiteral implements Literal {
    public static final String type = "string";
    private String object;

    public StringLiteral(String x) {
        object = x;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "'" + object + "'";
    }

    /** Returns whether two TableStrings are equal. */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StringLiteral)) {
            return false;
        }

        return ((StringLiteral) obj).getObject().equals(this.object);
    }

    @Override
    public boolean isZero() {
        return false;
    }
}
