package db.operators.comparison;

import db.literals.Literal;
import db.literals.NOVALUE;
import db.literals.NaN;
import db.literals.StringLiteral;
import db.operators.Operator;

/**
 * Created by admohanraj on 2/24/17.
 */
public interface ComparisonOperator extends Operator {
    @Override
    Boolean operate(Literal a, Literal b);

    default boolean isNOVALUE(Literal a) {
        return a.toString().equals(NOVALUE.default_type);
    }

    default boolean isNaN(Literal a) {
        return a.toString().equals(NaN.default_type) && !a.getType().equals(StringLiteral.type);
    }
}
