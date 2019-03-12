package db.operators.arithmetic;

import db.literals.Literal;
import db.literals.NOVALUE;
import db.literals.NaN;
import db.literals.StringLiteral;
import db.operators.Operator;

/**
 * Created by admohanraj on 2/24/17.
 */
public interface ArithmeticOperator extends Operator {
    @Override
    Literal operate(Literal a, Literal b);

    default boolean bothAreNOVALUE(Literal a, Literal b) {
        return a.toString().equals(b.toString()) && a.toString().equals(NOVALUE.default_type);
    }

    default boolean oneIsNaN(Literal a, Literal b) {
        boolean notStrings = !(a.getType().equals(StringLiteral.type) || a.getType().equals(StringLiteral.type));
        boolean areNaN = (a.toString().equals(NaN.default_type) || b.toString().equals(NaN.default_type));
        return areNaN && notStrings;
    }
}
