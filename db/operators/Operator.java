package db.operators;
import db.literals.Literal;
import db.operators.arithmetic.Divide;
import db.operators.arithmetic.Multiply;
import db.operators.arithmetic.Plus;
import db.operators.arithmetic.Subtract;
import db.operators.comparison.*;

/**
 * Created by admohanraj on 2/23/17.
 */
public interface Operator {
    Object operate(Literal a, Literal b);

    default boolean validInputs(Literal a, Literal b) {
        boolean sameType = a.getType().equals(b.getType());
        boolean areNumbers =  (a.getType().equals("float") && b.getType().equals("int"));
        areNumbers = areNumbers || (a.getType().equals("int") && b.getType().equals("float"));
        return sameType || areNumbers;
    }

    static Operator getOperator(String op) {
        switch (op) {
            case "+":
                return new Plus();
            case "-":
                return new Subtract();
            case "*":
                return new Multiply();
            case "/":
                return new Divide();
            case "==":
                return new Equal();
            case "!=":
                return new NotEqual();
            case "<":
                return new LessThan();
            case ">":
                return new GreaterThan();
            case "<=":
                return new LessThanEqual();
            case ">=":
                return new GreaterThanEqual();
            default:
                return null;
        }
    }
}
