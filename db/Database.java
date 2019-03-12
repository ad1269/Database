package db;

import db.literals.Literal;
import db.table.RCTable.RCTable;
import db.table.Table;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;

public class Database {

    private List<Table> tables;

    // Error messages
    private static final String ROW_MISMATCH_ERROR = "ERROR: Row doesn't match the table!";
    private static final String TYPE_ERROR = "ERROR: Unsupported type error!";
    private static final String EMPTY_TABLE_ERROR = "ERROR: Table must have at least one column!";
    private static final String COLUMN_FORMAT_ERROR = "ERROR: Malformed column definition!";
    private static final String NONEXISTENT_TABLE_ERROR = "ERROR: Nonexistent Table!";
    private static final String DUPLICATE_TABLE_ERROR = "ERROR: Table already exists!";
    private static final String FILE_DNE_ERROR = "ERROR: Given file does not exist!";
    private static final String FILE_WRITE_ERROR = "ERROR: There was an error writing the file!";
    private static final String SELECTION_ERROR = "ERROR: Selection Error!";


    public Database() {
        tables = new ArrayList<>();
    }

    public String transact(String query) {

        // Parses query
        // Parser.eval needs to be modified to call the function it finds and
        // to return the String output that function returns
        return Parser.eval(this, query);
    }

    /** Creates an empty table with the given column names and types. */
    private Table makeTable(String name, String[] nameTypes) throws RuntimeException {
        // Get column size
        int columnSize = nameTypes.length;

        // Table must have at least one column
        if (columnSize == 0) {
            throw new RuntimeException(EMPTY_TABLE_ERROR);
        }

        // Parses the column name and type into Lists
        List<String> names = new ArrayList<>();
        List<String> types = new ArrayList<>();
        for (String nameType : nameTypes) {
            // Splits on whitespace
            String[] ntArray = nameType.trim().split("\\s+");

            // Errors out if the nameType isn't formatted correctly.
            if (ntArray.length != 2) {
                throw new RuntimeException(COLUMN_FORMAT_ERROR);
            }

            // Parse the name and type into separate arrays.
            names.add(ntArray[0]);
            types.add(ntArray[1]);
        }

        // Check that all types are valid
        if (!Literal.validTypes(types)) {
            throw new RuntimeException(TYPE_ERROR);
        }

        // Create table with the given column names and add it to database
        Table loadedTable = new RCTable(names, types);
        loadedTable.setName(name);
        return loadedTable;
    }

    /** Creates an empty table with the given column names and types. */
    public String createTable(String name, String[] nameTypes) {
        Table loadedTable;
        try {
            loadedTable = makeTable(name, nameTypes);
        } catch (RuntimeException e) {
            return e.getMessage();
        }

        return addTable(loadedTable);
    }

    /** Adds a table with the given name to this database. */
    public String createTable(String name, Table table) {
        table.setName(name);
        return addTable(table);
    }

    /** Loads the file <tableName>.tbl and stores it in the database. */
    public String load(String tableName) {
        // Loads file into an array of lines and fails if it cannot find the file
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(tableName + ".tbl"), Charset.defaultCharset());
        } catch (IOException e) {
            return FILE_DNE_ERROR;
        }

        // Attempts to create a table from the data, and returns error message if malformed
        String[] nameTypes = lines.remove(0).split("\\s*,\\s*");
        try {
            Table loadedTable = makeTable(tableName, nameTypes);
            forceAddTable(loadedTable);
        } catch (RuntimeException e) {
            return e.getMessage();
        }

        // Load all remaining rows to the table
        for (int i = 0; i < lines.size(); i++) {
            String[] rowStrings = lines.get(i).trim().split("\\s*,\\s*");
            String insertStatus = insert(tableName, rowStrings);

            // Row insertion failed
            if (!insertStatus.equals("")) {
                drop(tableName);
                return insertStatus;
            }
        }

        return "";
    }

    /** Looks up the table in the database and writes it to disk. */
    public String store(String tableName) {
        // Retrieves the table from the database
        Table table = getTable(tableName);
        if (table == null) {
            return NONEXISTENT_TABLE_ERROR;
        }

        // Formats the table as a string and writes it to disk
        List<String> lines = Arrays.asList(table.toString().split("\\n"));
        try {
            Files.write(Paths.get(tableName + ".tbl"), lines, Charset.defaultCharset());
        } catch (IOException e) {
            return FILE_WRITE_ERROR;
        }

        return "";
    }

    /** Looks up the table in the database and deletes it. */
    public String drop(String tableName) {
        // Retrieves the table from the database
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).name().equals(tableName)) {
                tables.remove(i);
                return "";
            }
        }

        return NONEXISTENT_TABLE_ERROR;
    }

    /** Looks up the table in the database and adds rows to it. */
    public String insert(String tableName, String[] values) {

        List<String> rowStrings = Arrays.asList(values);

        // Get necessary information about the table
        Table tbl = getTable(tableName);
        if (tbl == null) {
            return NONEXISTENT_TABLE_ERROR;
        }

        // Tries to add the row to the table
        try {
            tbl.addRow(rowStrings);
        } catch (IOException e) {
            return ROW_MISMATCH_ERROR;
        }

        return "";
    }

    /** Returns a string representation of the table with the given name. */
    public String print(String tableName) {
        // Retrieves the table from the database
        Table table = getTable(tableName);
        if (table == null) {
            return NONEXISTENT_TABLE_ERROR;
        }

        return table.toString();
    }

    /** Returns a list of tables from this database from a list of names. */
    private List<Table> tablesForNames(List<String> names) {
        List<Table> tbls = new ArrayList<>();
        for (String n : names) {
            Table x = getTable(n);

            // If table not found return null
            if (x == null) {
                return null;
            }

            tbls.add(x);
        }
        return tbls;
    }

    /** Evaluates a select query and returns a table. */
    public Table select(List<String> colExpr, List<String> tblNames) throws RuntimeException {

        // Returns a null table if there are no columns to select. (We can't have an empty table)
        if (colExpr.isEmpty()) {
            return null;
        }

        // Return a null table if not all table names are valid.
        List<Table> tbls = tablesForNames(tblNames);
        if (tbls == null || tblNames.isEmpty()) {
            return null;
        }

        // Join the tables and use that to select.
        Table joinedTable = tbls.remove(0).join(tbls);

        // If *, we want to return all columns
        if (colExpr.get(0).equals("*")) {
            return joinedTable;
        }

        // Return a new table.
        return new RCTable(joinedTable, colExpr);
    }

    /** Evaluates a select query, filters it, and returns a table. */
    public Table select(List<String> colExpr,
                            List<String> tableNames,
                            List<String> conditions) throws RuntimeException {

        // Select from the table.
        Table filtered = select(colExpr, tableNames);
        if (filtered == null) {
            return null;
        }

        for (String cond : conditions) {
            filtered = filtered.filterTableByCondition(cond);
        }

        return filtered;
    }

    /** Evaluates a select query, filters it, and returns a string representation of the table. */
    public String select(String[] columnExpressions, String[] tableNames, String[] conditions) {
        Table selected;
        List<String> listCols = Arrays.asList(columnExpressions);
        List<String> listTables = Arrays.asList(tableNames);

        try {
            if (conditions.length == 0) {
                selected = select(listCols, listTables);
            } else {
                selected = select(listCols, listTables, Arrays.asList(conditions));
            }
        } catch (RuntimeException e) {
            return e.getMessage();
        }

        if (selected == null) {
            return SELECTION_ERROR;
        }

        return selected.toString();
    }

    /** Returns the table from this database or null if it doesn't exist. */
    private Table getTable(String tableName) {
        for (Table t : tables) {
            if (t.name().equals(tableName)) {
                return t;
            }
        }
        return null;
    }

    /** Adds the table to the database, returning an error if it already exists. */
    private String addTable(Table tbl) {
        if (getTable(tbl.name()) == null) {
            tables.add(tbl);
            return "";
        }
        return DUPLICATE_TABLE_ERROR;
    }

    /** Adds the table to the database forcefully, overwriting if necessary. */
    private String forceAddTable(Table tbl) {
        if (getTable(tbl.name()) != null) {
            drop(tbl.name());
        }
        tables.add(tbl);
        return "";
    }
}
