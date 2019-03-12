package db.literals;

/**
 * Created by admohanraj on 3/4/17.
 */
public class NaN implements Literal {
    public static final String default_type = "NaN";
    private static final String object = "NaN";
    private String type;

    public NaN() {
        this.type = default_type;
    }

    public NaN(String type) {
        this.type = type;
    }

    public void setType(String newType) {
        type = newType;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return object;
    }

    /** Returns whether this equals object. */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NaN) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isZero() {
        return false;
    }
}
