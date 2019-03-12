package db.literals;

/**
 * Created by admohanraj on 3/3/17.
 */
public class NOVALUE implements Literal {
    public static final String default_type = "NOVALUE";
    private String type;
    private static final String display = "NOVALUE";
    private Object object;

    public NOVALUE() {
        this.type = default_type;
        this.object = display;
    }

    public NOVALUE(String type) {
        this.type = type;
        setType(type);
    }

    public void setType(String newType) {
        type = newType;

        switch (type) {
            case StringLiteral.type:
                object = "";
                break;
            case IntLiteral.type:
                object = 0;
                break;
            case FloatLiteral.type:
                object = 0.0;
                break;
            default:
                object = display;
        }
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
        return display;
    }

    @Override
    public boolean isZero() {
        if (type.equals(StringLiteral.type)) {
            return false;
        }
        return true;
    }
}
