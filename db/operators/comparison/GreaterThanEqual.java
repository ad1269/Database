package db.operators.comparison;

import db.literals.Literal;

/**
 * Created by admohanraj on 2/28/17.
 */
public class GreaterThanEqual implements ComparisonOperator {
    @Override
    public Boolean operate(Literal a, Literal b) {
        if (isNOVALUE(a) || isNOVALUE(b)) {
            return false;
        }
        
        return (new Equal()).operate(a, b) || (new GreaterThan()).operate(a, b);
    }
}
