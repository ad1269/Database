package db.table.RCTable;

import db.literals.Literal;
import db.operators.comparison.ComparisonOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admohanraj on 2/16/17.
 * A row class represents one row in the table.
 * It consists of a list of RowItems.
 */
public class Row {
    private List<RowItem> items;

    /** Class that keeps track of the column name and the item that's stored
     *  at a position in the row. */
    private static class RowItem {
        private String name;
        private String type;
        private Literal item;

        /** Creates new RowItem with name and item. */
        public RowItem(String name, String type, Literal item) {
            this.name = name;
            this.type = type;
            this.item = item;
        }

        /** Creates new RowItem with the name and contents of column. */
        public RowItem(Column column) {
            name = column.name();
            type = column.type();
            item = column.getItem(0);
        }

        /** Returns the item contained in RowItem. */
        public Literal getItem() {
            return item;
        }

        /** Returns the column name of this RowItem. */
        public String getName() {
            return name;
        }

        /** Returns the column type of this RowItem. */
        public String getType() {
            return type;
        }

        /** Returns a string representation of RowItem. */
        @Override
        public String toString() {
            return name + ": " + item;
        }

        /** Returns whether the names of two RowItems are equal. */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof RowItem)) {
                return false;
            }

            RowItem ri = (RowItem) obj;
            return ri.name.equals(this.name);
        }

        /** Clones this row item. */
        @Override
        public RowItem clone() {
            return new RowItem(this.name, this.type, this.item);
        }
    }

    /** Constructs an empty row. */
    public Row() {
        items = new ArrayList<>();
    }

    /** Constructs a row with a list of RowItems. */
    private Row(List<RowItem> ri, String nullor) {
        items = ri;
    }

    /** Constructs a row from a list of columns. All columns must be length 1. */
    public Row(List<Column> columns) {
        if (!checkValidRow(columns)) {
            // Throw error message here. Change to raise exception if needed later.
            return;
        }

        // Convert all the columns to RowItems and store them.
        items = new ArrayList<>();
        for (Column c : columns) {
            RowItem ri = new RowItem(c);
            items.add(ri);
        }
    }

    private static List<Column> columnsFromNameTypeObjects(List<String> names, List<String> types, List<String> objects) {
        if (names.size() != types.size() || types.size() != objects.size()) {
            return null;
        }

        // Create columns from names and type
        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String type = types.get(i);
            String obj = objects.get(i);

            // Sets up column with the appropriate type
            List<Literal> columnList = new ArrayList<>();
            columnList.add(Literal.make(obj, type));

            // If this fails, this means we got an unexpected type
            if (columnList.get(0) == null) {
                return null;
            }

            // Creates new column and adds to the list of columns
            Column column = new Column(name, type, columnList);
            columns.add(column);
        }

        return columns;
    }

    /** Initializes a Row object with the specified column names, types, and objects. */
    public Row(List<String> names, List<String> types, List<String> objects) {
        this(columnsFromNameTypeObjects(names, types, objects));
    }

    /** Checks if every column in this row has length 1. */
    private boolean checkValidRow(List<Column> columns) {
        // Protects against a malformed load or insert into
        if (columns == null) {
            return false;
        }

        for (Column c : columns) {
            if (c.length() != 1) {
                return false;
            }
        }
        return true;
    }

    /** Returns the literal stored under column name in this row. */
    private Literal getItemByName(String name) {
        RowItem rowItem = null;
        for (RowItem ri : items) {
            if (ri.getName().equals(name)) {
                rowItem = ri;
                break;
            }
        }
        return rowItem.getItem();
    }

    /** Returns whether this row is improperly initialized.. */
    public boolean isNullRow() {
        return items == null;
    }

    public boolean satisfiesConditional(String col0Name, ComparisonOperator operator, String col1Name) {
        Literal col0Value = getItemByName(col0Name);
        Literal col1Value = getItemByName(col1Name);

        return operator.operate(col0Value, col1Value);
    }

    public boolean satisfiesConditional(String col0Name, ComparisonOperator operator, Literal literal) {
        Literal col0Value = getItemByName(col0Name);
        return operator.operate(col0Value, literal);
    }

    /** Returns the item at position index in this row. */
    public Literal getItem(int index) {
        return items.get(index).getItem();
    }

    /** Returns the column name for item at position index in this row. */
    public String getColumnName(int index) {
        return items.get(index).getName();
    }

    /** Returns the column type for item at position index in this row. */
    public String getColumnType(int index) {
        return items.get(index).getType();
    }

    /** Appends item to the end of the row. */
    private void addItem(RowItem ri) {
        items.add(ri);
    }

    /** Returns the width of this row. */
    public int width() {
        return items.size();
    }

    /** Returns a string representation of the row. */
    @Override
    public String toString() {
        StringBuilder rString = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            rString.append(items.get(i).getItem());
            if (i != items.size() - 1) {
                rString.append(",");
            }
        }
        return rString.toString();
    }

    /** Returns whether the given object is equal to the row. */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Row)) {
            return false;
        }

        Row r = (Row) obj;

        for (int i = 0; i < items.size(); i++) {
            // Checks that each row entry has the same column name.
            if (!this.items.get(i).equals(r.items.get(i))) {
                return false;
            }

            // Checks that each row entry has the same value.
            if (!this.getItem(i).equals(r.getItem(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Row clone() {
        List<RowItem> copiedRI = new ArrayList<>();
        for (RowItem ri : this.items) {
            copiedRI.add(ri.clone());
        }

        return new Row(copiedRI, "");
    }

    /** Returns a list of commonly named row items between the two lists.
     *  Returns the row item from row1 not row2. */
    public static List<RowItem> getCommonRows(Row row1, Row row2) {
        List<RowItem> common = new ArrayList<>();
        for (RowItem ri1 : row1.items) {
            for (RowItem ri2 : row2.items) {
                if (ri1.name.equals(ri2.name)) {
                    common.add(ri1);
                }
            }
        }
        return common;
    }

    /** Returns true if the two rows have no common rows or have
     *  common rows with matching values. */
    public static boolean mergeable(Row row1, Row row2) {
        List<RowItem> common1 = getCommonRows(row1, row2);
        List<RowItem> common2 = getCommonRows(row2, row1);

        for (RowItem ri1 : common1) {
            for (RowItem ri2 : common2) {
                if (ri1.name.equals(ri2.name)) {
                    if (!ri1.item.equals(ri2.item)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /** Takes in two rows that have been found to be mergeable
     *  and merges them. */
    public static Row merge(Row row1, Row row2) {
        List<RowItem> common1 = getCommonRows(row1, row2);
        List<RowItem> common2 = getCommonRows(row2, row1);

        Row merged = new Row();

        // Add all the shared rows to the merged row.
        for (RowItem ri1 : common1) {
            for (RowItem ri2 : common2) {
                if (ri1.name.equals(ri2.name)) {
                    // Add all common columns to merged.
                    merged.addItem(ri1);
                }
            }
        }

        // Add left unshared rows
        for (RowItem ri1 : row1.items) {
            if (!common1.contains(ri1)) {
                merged.addItem(ri1);
            }
        }

        // Add right unshared rows
        for (RowItem ri2 : row2.items) {
            if (!common1.contains(ri2)) {
                merged.addItem(ri2);
            }
        }

        return merged;
    }
}
