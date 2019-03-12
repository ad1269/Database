package db.table;

import java.io.IOException;
import java.util.List;

/**
 * Created by admohanraj on 3/4/17.
 */
public interface Table {
    /** Sets the name of this table to the given string. */
    void setName(String name);

    /** Returns the number of columns in this table. */
    int numColumns();

    /** Returns a list of rows stored in the table. */
    List rows();

    /** Returns the number of rows in this table. */
    int numRows();

    /** Returns the name of this table. */
    String name();

    /** Adds a row with the given values to this table. */
    void addRow(List<String> values) throws IOException;

    /** Returns a new filtered table of all rows in this table that don't match the condition. */
    Table filterTableByCondition(String condition) throws RuntimeException;

    /** Returns a string representation of the table. */
    @Override
    String toString();

    /** Returns whether two Tables are equal. */
    @Override
    boolean equals(Object obj);

    /** Returns a clone of this object. */
    Table clone();

    /** Joins all tables given in the list with this table using natural inner join
     * and returns the resulting table. */
    Table join(List<Table> tables);
}
