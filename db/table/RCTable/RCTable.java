package db.table.RCTable;

import db.literals.Literal;
import db.operators.Operator;
import db.operators.comparison.ComparisonOperator;
import db.table.Table;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by admohanraj on 2/16/17.
 * A RCTable object has rows and columns that store miscellaneous data.
 * The columns each store a specific type.
 */
public class RCTable implements Table {
    private String name;
    private List<Column> columns;
    private List<Row> rows;

    /** Initializes empty table. */
    public RCTable() {
        name = "";
        columns = new ArrayList<>();
        rows = new ArrayList<>();
    }

    /** Initializes table with columns. */
    public RCTable(List<Column> columns) {
        name = "";
        this.columns = new ArrayList<>(columns);
        updateRowsToMatchColumns();
    }

    /** Creates a new empty table with same columns as the given table. */
    public RCTable(RCTable table) {
        List<Column> cols = new ArrayList<>();
        for (Column c : table.columns()) {
            Column cop = new Column(c.name(), c.type());
            cols.add(cop);
        }
        name = "";
        columns = cols;
        rows = new ArrayList<>();
    }

    /** Returns a list of columns with the given names and types. */
    private static List<Column> getColumnsFromNameTypes(List<String> names, List<String> types) {
        // Create columns from names and type in order to set up the table
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String type = types.get(i);

            List<Literal> columnList = new ArrayList<>();

            Column column = new Column(name, type, columnList);
            columns.add(column);
        }
        return columns;
    }

    /** Initializes a table with the given column names and types. */
    public RCTable(List<String> names, List<String> types) {
        this(getColumnsFromNameTypes(names, types));
    }

    private static List<Column> getColumnsFromExpressions(Table table, List<String> columnExpressions) throws RuntimeException {
        // Go through the column expressions and evaluate each one.
        List<Column> columns = new ArrayList<>();
        for(String colExpression : columnExpressions) {
            Column evaluated = ((RCTable) table).evaluateColumnExpression(colExpression);

            // Return a null table, the evaluation failed.
            if (evaluated == null) {
                throw new RuntimeException("ERROR: Column expression failed to evaluate.");
            }

            columns.add(evaluated);
        }

        return columns;
    }

    /** Initializes a table with the given column expressions.*/
    public RCTable(Table table, List<String> columnExpressions) throws RuntimeException {
        this(getColumnsFromExpressions(table, columnExpressions));
    }

    /** Updates the rows to match the data stored in the columns. */
    private void updateRowsToMatchColumns() {
        this.rows = new ArrayList<>();
        if (columns.size() == 0) {
            return;
        }

        // Update the rows to match the columns.
        int columnLength = columns.get(0).length();
        for (int i = 0; i < columnLength; i++) {
            // Create an empty list of columns.
            List<Column> rowCols = new ArrayList<>();

            // Create length 1 columns and store it.
            for (Column col : columns) {
                Column newC = new Column(col.name(), col.type());
                newC.addItem(col.getItem(i));
                rowCols.add(newC);
            }

            // Construct row from list of columns and add it to rows.
            Row row = new Row(rowCols);
            rows.add(row);
        }
    }

    /** Rebuilds the columns to match the data stored in the rows. */
    private void updateColumnsToMatchRows() {
        if (rows.size() == 0) {
            for (Column c: columns) {
                c.clearData();
            }
            return;
        }

        this.columns = new ArrayList<>();

        // Update the columns to match the rows.
        int rowWidth = rows.get(0).width();
        for (int i = 0; i < rowWidth; i++) {
            // Creates an empty column
            Column current = new Column(rows.get(0).getColumnName(i), rows.get(0).getColumnType(i));

            for (int j = 0; j < rows.size(); j++) {
                current.addItem(rows.get(j).getItem(i));
            }

            columns.add(current);
        }
    }

    /** Updates the columns to match the data stored in the rows. */
    private void fastUpdateColumnsToMatchRow(Row row) {
        // Update the columns to match the rows.
        int rowWidth = rows.get(0).width();
        for (int i = 0; i < rowWidth; i++) {
            columns.get(i).addItem(row.getItem(i));
        }
    }

    /** Takes in a row and adds it to this table. */
    private void addRow(Row row) throws IOException {
        if (row.width() != columns.size() && columns.size() != 0) {
            // Throw error message here.
            throw new IOException("Error! Cannot add row! Size mismatch!");
        }
        rows.add(row);

        if (columns().isEmpty()) {
            updateColumnsToMatchRows();
        } else {
            fastUpdateColumnsToMatchRow(row);
        }
    }

    /** Adds a row with the given values to this table. */
    public void addRow(List<String> values) throws IOException {
        List<String> names = this.columnNames();
        List<String> types = this.columnTypes();

        // Makes a row with the given information
        Row row = new Row(names, types, values);

        if (row.isNullRow()) {
            throw new IOException("Error! Cannot add row! Size mismatch!");
        }

        if (row.width() != columns.size() && columns.size() != 0) {
            // Throw error message here.
            throw new IOException("Error! Cannot add row! Size mismatch!");
        }
        rows.add(row);
        fastUpdateColumnsToMatchRow(row);
    }

    /** Returns a list of names of the columns stored in the table. */
    private List<String> columnNames() {
        List<String> names = new ArrayList<>();
        for (Column c: columns) {
            names.add(c.name());
        }
        return names;
    }

    /** Returns a list of types of the columns stored in the table. */
    private List<String> columnTypes() {
        List<String> types = new ArrayList<>();
        for (Column c: columns) {
            types.add(c.type());
        }
        return types;
    }

    /** Gets the column with the given name in this table. */
    private Column getColumn(String name) throws RuntimeException {
        for (Column c : columns) {
            if (c.name().equals(name)) {
                return c;
            }
        }
        throw new RuntimeException(String.format("ERROR: %s is not a column!", name));
    }

    /** Returns whether or not this table has the given column. */
    private boolean hasColumn(String name) {
        try {
            getColumn(name);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** Parses and evaluates a column expression in the context of this table. */
    private Column evaluateColumnExpression(String expr) throws RuntimeException {
        String[] subexpressions = expr.split("\\s+");

        // Single operand means it must be a column name.
        if (subexpressions.length == 1) {
            // Retrieves the column from the table.
            return this.getColumn(subexpressions[0]);
        } else if (subexpressions.length == 3 && subexpressions[1].equals("as")) {
            // This expression is formatted correctly.
            String alias = subexpressions[2];

            // Split compound on arithmetic operator.
            String compound = subexpressions[0];
            String[] compoundExpressions = compound.split("(\\+|-|\\*|/)");
            if (compoundExpressions.length != 2) {
                throw new RuntimeException("ERROR: Arithmetic Expression not formatted correctly!");
            }

            Column operand0 = this.getColumn(compoundExpressions[0]);
            String operator = compound.substring(compoundExpressions[0].length(), compound.length() - compoundExpressions[1].length());

            Column operand1;
            try {
                operand1 = this.getColumn(compoundExpressions[1]);
            } catch (RuntimeException e) {
                // Treat compoundExpressions[2] as a literal.
                Literal operandLiteral1 = Literal.make(compoundExpressions[1]);
                if (operandLiteral1 == null) {
                    throw new RuntimeException("ERROR: Operand 2 is neither a column nor a literal.");
                }

                return Column.evaluate(operand0, operandLiteral1, operator, alias);
            }

            return Column.evaluate(operand0, operand1, operator, alias);

        } else if(subexpressions.length == 5 && subexpressions[3].equals("as")) {
            // This expression is formatted correctly.
            Column operand0 = this.getColumn(subexpressions[0]);
            String alias = subexpressions[4];
            String operator = subexpressions[1];
            Column operand1;
            try {
                operand1 = this.getColumn(subexpressions[2]);
            } catch (RuntimeException e) {
                // Treat subexpressions[2] as a literal.
                Literal operandLiteral1 = Literal.make(subexpressions[2]);
                if (operandLiteral1 == null) {
                    throw new RuntimeException("ERROR: Operand 2 is neither a column nor a literal.");
                }
                return Column.evaluate(operand0, operandLiteral1, operator, alias);
            }

            return Column.evaluate(operand0, operand1, operator, alias);
        }
        // If not properly formatted, return null.
        throw new RuntimeException("ERROR: Column expression is not valid.");
    }

    /** Returns a new filtered table of all rows in this table that don't match the condition. */
    public RCTable filterTableByCondition(String condition) throws RuntimeException {

        //**** STAGE 1: PARSING THE CONDITIONAL EXPRESSION ****//

        String[] subexpressions = condition.split(">=|<=|==|!=|>|<");

        // Malformed conditional expression
        if (subexpressions.length != 2) {
            throw new RuntimeException("ERROR: Malformed conditional expression!");
        }
        String op = condition.substring(subexpressions[0].length(), condition.length() - subexpressions[1].length());
        String o2 = subexpressions[1].trim();


        String col0Name = subexpressions[0].trim();
        // If the first column doesn't exist in the table
        if (!hasColumn(col0Name)) {
            throw new RuntimeException("ERROR: First column doesn't exist in the table!");
        }

        ComparisonOperator condOperator;
        try {
            condOperator = (ComparisonOperator) Operator.getOperator(op);
        } catch (Exception e) {
            throw new RuntimeException("ERROR: Invalid operator!");
        }

        // Invalid operator symbol.
        if (condOperator == null) {
            return null;
        }

        boolean isUnaryExpression = true;
        String col1Name = null;
        Literal literal;

        // It's not a literal, check if it's a column name.
        if (!Literal.isLiteral(o2)) {
            // Second column doesn't exist.
            if (!hasColumn(o2)) {
                throw new RuntimeException("ERROR: Second operator is not a column/literal!");
            }

            isUnaryExpression = false;
            col1Name = o2;
        }

        literal = Literal.make(o2);

        //**** STAGE 2: FILTER THE TABLE WITH THE PARSED EXPRESSION. ****//

        RCTable filtered = new RCTable(this);

        // Iterate through the rows and add to the table if they meet the conditional expression.
        for (Row row : this.rows()) {
            if (isUnaryExpression) {
                if (row.satisfiesConditional(col0Name, condOperator, literal)) {
                    try {
                        filtered.addRow(row);
                    } catch (IOException e) {
                        System.out.println("This should never be printed. We're adding rows from the same table.");
                    }
                }
            } else {
                if (row.satisfiesConditional(col0Name, condOperator, col1Name)) {
                    try {
                        filtered.addRow(row);
                    } catch (IOException e) {
                        System.out.println("This should never be printed. We're adding rows from the same table.");
                    }
                }
            }
        }

        return filtered;
    }

    /** Sets the name of this table to the given string. */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns a list of columns stored in the table. */
    private List<Column> columns() {
        return columns;
    }

    /** Returns the number of columns in this table. */
    public int numColumns() {
        return columns.size();
    }

    /** Returns a list of rows stored in the table. */
    public List<Row> rows() {
        return rows;
    }

    /** Returns the number of rows in this table. */
    public int numRows() {
        return rows.size();
    }

    /** Returns the name of this table. */
    public String name() {
        return this.name;
    }

    /** Returns a string representation of the table. */
    @Override
    public String toString() {
        StringBuilder tbl = new StringBuilder();

        // Print the column names
        for (int i = 0; i < columns.size(); i++) {
            Column cc = columns.get(i);
            tbl.append(cc.name());
            tbl.append(" ");
            tbl.append(cc.type());

            if (i != columns.size() - 1) {
                tbl.append(",");
            }
        }
        tbl.append("\n");

        // Print all the data points
        for (Row row : rows) {
            tbl.append(row.toString());
            tbl.append("\n");
        }
        return tbl.deleteCharAt(tbl.length() - 1).toString();
    }

    /** Returns whether two Tables are equal. */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RCTable)) {
            return false;
        }

        RCTable tbl = (RCTable) obj;

        if (tbl.numColumns() != this.numColumns() || tbl.numRows() != this.numRows()) {
            return false;
        }

        for (int i = 0; i < tbl.columns().size(); i++) {
            Column tc = tbl.columns().get(i);
            Column mc = this.columns.get(i);
            if (!tc.equals(mc) || !tc.name().equals(mc.name())) {
                return false;
            }
        }

        return true;
    }

    /** Returns a clone of this object. */
    @Override
    public RCTable clone() {
        RCTable cloned = new RCTable();
        cloned.name = this.name();

        List<Column> copiedCols = new ArrayList<>();
        for (Column c : this.columns) {
            copiedCols.add(c.clone());
        }

        List<Row> copiedRows = new ArrayList<>();
        for (Row r : this.rows) {
            copiedRows.add(r.clone());
        }

        cloned.columns = copiedCols;
        cloned.rows = copiedRows;

        return cloned;
    }

    /** Joins table1 with table2 using natural inner join
     * and returns the resulting table. */
    private static RCTable join(RCTable table1, RCTable table2) {
        RCTable joined = new RCTable();
        for (Row row1 : table1.rows()) {
            for (Row row2 : table2.rows()) {
                if (Row.mergeable(row1, row2)) {
                    Row merged = Row.merge(row1, row2);
                    try {
                        joined.addRow(merged);
                    } catch (IOException e) {
                        System.out.println("This shouldn't ever happen if the mergeable and merge functions are correct.");
                    }
                }
            }
        }

        return joined;
    }

    /** Joins all tables given in the list with this table using natural inner join
     * and returns the resulting table. */
    public Table join(List<Table> tables) {
        RCTable leftTable = this.clone();

        for (Table nextTable : tables) {
            leftTable = RCTable.join(leftTable, (RCTable) nextTable);
        }

        leftTable.setName("");
        return leftTable;
    }
}
