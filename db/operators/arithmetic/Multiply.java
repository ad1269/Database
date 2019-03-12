package db.operators.arithmetic;

import db.literals.*;
import db.operators.Operator;

/**
 * Created by admohanraj on 2/23/17.
 */
public class Multiply implements ArithmeticOperator {
    @Override
    public Literal operate(Literal a, Literal b) {
        if (!validInputs(a, b)) {
            return null;
        }

        if (bothAreNOVALUE(a, b)) {
            return new NOVALUE(a.getType());
        }

        // If one of the operands is NaN, then the result is NaN
        if (oneIsNaN(a, b)) {
            if (a.getType().equals(b.getType())) {
                return new NaN(a.getType());
            }
            return new NaN(FloatLiteral.type);
        }

        // Performs the correct operation based upon type.
        switch (a.getType()) {
            case IntLiteral.type:
                if (b.getType().equals("float")) {
                    return new FloatLiteral((Integer) a.getObject() * (Double) b.getObject());
                }
                return new IntLiteral((Integer) a.getObject() * (Integer) b.getObject());
            case FloatLiteral.type:
                if (b.getType().equals("int")) {
                    return new FloatLiteral((Double) a.getObject() * (Integer) b.getObject());
                }
                return new FloatLiteral((Double) a.getObject() * (Double) b.getObject());
            case StringLiteral.type:
                return null;
            default:
                return null;
        }
    }
}
