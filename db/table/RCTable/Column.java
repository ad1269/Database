package db.table.RCTable;

import db.literals.Literal;
import db.operators.Operator;
import db.operators.arithmetic.ArithmeticOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by admohanraj on 2/16/17.
 * A column class represents one column in the table.
 */
public class Column {
    private String name;
    private String type;
    private List<Literal> items;

    /** Create an empty column object called name. */
    public Column(String name, String type) {
        this.name = name;
        this.type = type;
        items = new ArrayList<>();
    }

    /** Create a column object called name with items as data points. */
    public Column(String name, String type, List<Literal> items) {
        this.name = name;
        this.type = type;
        this.items = new ArrayList<>(items);
    }

    /** Create a column object called name with items as data points. */
    public Column(String name, String type, Literal[] items) {
        this.name = name;
        this.type = type;
        this.items = Arrays.asList(items);
    }

    /** Returns the name of the column. */
    public String name() {
        return name;
    }

    /** Returns the type of the column. */
    public String type() {
        return type;
    }

    /** Returns the item at position index in this column. */
    public Literal getItem(int index) {
        return items.get(index);
    }

    /** Appends item at last position in this column. */
    public void addItem(Literal item) {
        items.add(item);
    }

    /** Returns the data in the column, probably will delete later. */
    public List<Literal> items() {
        return items;
    }

    /** Returns how many items are stored in this column. */
    public int length() {
        return items.size();
    }

    /** Deletes all data stored in this column. */
    public void clearData() {
        items = new ArrayList<>();
    }

    /** Returns whether two columns are equal. */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Column)) {
            return false;
        }

        Column c = (Column) obj;
        if (!c.name.equals(this.name) || c.items.size() != this.items.size()) {
            return false;
        }

        for (int i = 0; i < items.size(); i++) {
            if (!this.items.get(i).equals(c.items.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Column clone() {
        return new Column(this.name, this.type, this.items);
    }

    /** Returns a new column that is the result of applying the operator to the two operands. */
    public static Column evaluate(Column o1, Column o2, String opString, String alias) throws RuntimeException {

        if (o1.length() != o2.length()) {
            throw new RuntimeException("ERROR: Column size mismatch.");
        }

        ArithmeticOperator operator = (ArithmeticOperator) Operator.getOperator(opString);
        if (operator == null) {
            throw new RuntimeException("ERROR: Invalid operator.");
        }

        List<Literal> newColList = new ArrayList<>();
        String type = "fail";
        for (int i = 0; i < o1.length(); i++) {
            Literal result = operator.operate(o1.getItem(i), o2.getItem(i));
            if (result == null) {
                throw new RuntimeException("ERROR: Operator failed!");
            }

            type = result.getType();
            newColList.add(result);
        }

        return new Column(alias, type, newColList);
    }

    /** Returns a new column that is the result of applying the operator to the two operands. */
    public static Column evaluate(Column o1, Literal o2, String opString, String alias) throws RuntimeException {
        /*if (!o1.type().equals(o2.getType())) {
            throw new RuntimeException("ERROR: Type mismatch between operators.");
        }*/

        ArithmeticOperator operator = (ArithmeticOperator) Operator.getOperator(opString);
        if (operator == null) {
            throw new RuntimeException("ERROR: Invalid operator.");
        }

        List<Literal> newColList = new ArrayList<>();
        String type = "fail";
        for (int i = 0; i < o1.length(); i++) {
            Literal result = operator.operate(o1.getItem(i), o2);
            if (result == null) {
                throw new RuntimeException("ERROR: Operator failed!");
            }

            type = result.getType();
            newColList.add(result);
        }

        return new Column(alias, type, newColList);
    }
}
