package db.operators.comparison;

import db.literals.Literal;

/**
 * Created by admohanraj on 2/24/17.
 */
public class Equal implements ComparisonOperator {
    @Override
    public Boolean operate(Literal a, Literal b) {
        if (!validInputs(a, b)) {
            return null;
        }

        if (isNOVALUE(a) || isNOVALUE(b)) {
            return false;
        }

        // Performs the correct operation based upon type.
        return a.equals(b);
    }
}
