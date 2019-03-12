package db;

import db.table.Table;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admohanraj on 2/19/17.
 * Heavily based on the Parse class provided.
 */
public class Parser {

    private static final String BAD_CREATE = "ERROR: Malformed create: %s\n";
    private static final String BAD_QUERY = "ERROR: Malformed query: %s\n";
    private static final String BAD_INSERT = "ERROR: Malformed insert: %s\n";
    private static final String BAD_SELECT = "ERROR: Malformed select: %s\n";


    // Various common constructs, simplifies parsing.
    private static final String REST  = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND   = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD   = Pattern.compile("load " + REST),
            STORE_CMD  = Pattern.compile("store " + REST),
            DROP_CMD   = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD  = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    public static String eval(Database db, String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(db, m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(db, m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(db, m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(db, m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(db, m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(db, m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(db, m.group(1));
        }
        return String.format(BAD_QUERY, query);
    }

    private static String createTable(Database db, String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(db, m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(db, m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            return String.format(BAD_CREATE, expr);
        }
    }

    private static String createNewTable(Database db, String name, String[] cols) {
        // Debug code start
        /*StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length-1; i++) {
            joiner.add(cols[i]);
        }
        String colSentence = joiner.toString() + " and " + cols[cols.length-1];

        System.out.printf("You are trying to create a table named %s with the columns %s\n", name, colSentence);*/
        // Debug code end

        return db.createTable(name, cols);
    }

    private static String createSelectedTable(Database db, String name, String exprs, String tables, String conds) {

        // Debug code start
        // System.out.printf("You are trying to create a table named %s by selecting these expressions:" +
        //         " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", name, exprs, tables, conds);
        // Debug code end

        String[] exprArr = exprs.split("\\s*,\\s*");
        String[] tableNameArr = tables.split("\\s*,\\s*");
        Table selected;

        if (conds == null) {
            selected = db.select(Arrays.asList(exprArr), Arrays.asList(tableNameArr));
        } else {
            selected = db.select(Arrays.asList(exprArr), Arrays.asList(tableNameArr), Arrays.asList(conds.split("\\s*and\\s*")));
        }

        // Selection failed
        if (selected == null) {
            return "ERROR: Selection failed, and table cannot be created!";
        }

        return db.createTable(name, selected);
    }

    private static String loadTable(Database db, String name) {
        // Debug code start
        // System.out.printf("You are trying to load the table \"%s\"\n", name);
        // Debug code end

        return db.load(name);
    }

    private static String storeTable(Database db, String name) {
        return db.store(name);
    }

    private static String dropTable(Database db, String name) {
        // Debug code start
        // System.out.printf("You are trying to drop the table \"%s\"\n", name);
        // Debug code end

        return db.drop(name);
    }

    private static String insertRow(Database db, String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            return String.format(BAD_INSERT, expr);
        }

        // Debug code start
        // System.out.printf("You are trying to insert the row \"%s\" into the table %s\n", m.group(2), m.group(1));
        // Debug code end

        String name = m.group(1);
        String row = m.group(2);
        String[] rowValues = row.trim().split("\\s*,\\s*");
        return db.insert(name, rowValues);
    }

    private static String printTable(Database db, String name) {
        return db.print(name);
    }

    private static String select(Database db, String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            return String.format(BAD_SELECT, expr);
        }

        return select(db, m.group(1), m.group(2), m.group(3));
    }

    private static String select(Database db, String exprs, String tables, String conds) {
        // Debug code start
        // System.out.printf("You are trying to select these expressions:" +
        //         " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", exprs, tables, conds);
        // Debug code end

        String[] exprArr = exprs.split("\\s*,\\s*");
        String[] tableNameArr = tables.split("\\s*,\\s*");
        String[] condArr;

        if (conds == null) {
            condArr = new String[0];
        } else {
            condArr = conds.split("\\s*and\\s*");
        }

        return db.select(exprArr, tableNameArr, condArr);
    }
}
