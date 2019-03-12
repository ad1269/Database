package db.operators.comparison;

import db.literals.FloatLiteral;
import db.literals.IntLiteral;
import db.literals.Literal;
import db.literals.StringLiteral;

/**
 * Created by admohanraj on 2/28/17.
 */
public class LessThan implements ComparisonOperator {
    @Override
    public Boolean operate(Literal a, Literal b) {
        if (!validInputs(a, b)) {
            return null;
        }

        if (isNOVALUE(a) || isNOVALUE(b)) {
            return false;
        }

        if (isNaN(b) && !isNaN(a)) {
            return true;
        } else if (isNaN(a) && isNaN(b) || !isNaN(b) && isNaN(a)) {
            return false;
        }

        // Performs the correct operation based upon type.
        switch (a.getType()) {
            case IntLiteral.type:
                if (b.getType().equals("float")) {
                    return ((IntLiteral) a).getObject() < ((FloatLiteral) b).getObject();
                }
                return ((IntLiteral) a).getObject() < ((IntLiteral) b).getObject();
            case FloatLiteral.type:
                if (b.getType().equals("int")) {
                    return ((FloatLiteral) a).getObject() < ((IntLiteral) b).getObject();
                }
                return ((FloatLiteral) a).getObject() < ((FloatLiteral) b).getObject();
            case StringLiteral.type:
                return ((StringLiteral) a).getObject().compareTo((String) b.getObject()) < 0;
            default:
                return null;
        }
    }
}
