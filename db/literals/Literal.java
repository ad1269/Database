package db.literals;

import java.util.Arrays;
import java.util.List;

/**
 * Created by admohanraj on 2/22/17.
 */
public interface Literal {
    String type = "default";
    Object getObject();
    boolean isZero();

    default String getType() {
        return type;
    }

    static Literal make(String obj, String type) {

        // If it's NOVALUE
        if (obj.equals("NOVALUE")) {
            return new NOVALUE(type);
        }

        // If it's NaN
        if (obj.equals("NaN")) {
            return new NaN(type);
        }

        switch (type) {
            case StringLiteral.type:
                // String must be listed in single quotes
                if (!obj.startsWith("'") || !obj.endsWith("'")) {
                    return null;
                }
                return new StringLiteral(obj.substring(1, obj.length() - 1));
            case IntLiteral.type:
                // Tries to parse obj to an int, and fails if it can't
                try {
                    return new IntLiteral(Integer.parseInt(obj));
                } catch (Exception e) {
                    return null;
                }
            case FloatLiteral.type:
                // Tries to parse obj to a double, and fails if it can't
                try {
                    return new FloatLiteral(Double.parseDouble(obj));
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }

    static String getType(String obj) {

        if (obj.equals("NOVALUE")) {
            return "NOVALUE";
        }

        if (obj.equals("NaN")) {
            return "NaN";
        }

        if (obj.startsWith("'") && obj.endsWith("'")) {
            return StringLiteral.type;
        }

        boolean allInts = true;
        int dotCount = 0;
        for(char c : obj.toCharArray()) {
            if (!Character.isDigit(c) && c != '.' && c != '-') {
                allInts = false;
                break;
            } else if (c == '.') {
                dotCount++;
            }
        }

        if (allInts && dotCount == 0) {
            return IntLiteral.type;
        } else if (allInts && dotCount == 1) {
            return FloatLiteral.type;
        }
        return null;
    }

    static Literal make(String obj) {
        String type = getType(obj);

        // Failed to find type.
        if (type == null) {
            return null;
        }
        return make(obj, type);
    }

    static boolean isLiteral(String obj) {
        String type = getType(obj);
        return type != null;
    }

    /** Checks whether all the types given are valid types supported by the database. */
    static boolean validTypes(List<String> types) {
        String[] t = {FloatLiteral.type, IntLiteral.type, StringLiteral.type};
        List<String> validTypes = Arrays.asList(t);
        for (String type : types) {
            if (!validTypes.contains(type)) {
                return false;
            }
        }
        return true;
    }
}
