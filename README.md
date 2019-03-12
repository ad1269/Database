# Database
Implementation of a SQL Database system (with a focus on efficient object oriented design and queries)

## Intro
This database system can handle a subset of the SQL language (selects, inserts, table creation, etc). The database also supports arithmetic operations, comparison operations, joins, and filters.

## How to Run
Run `Main.java` to test the functionality. This class simply gets queries from stdin, runs them against our database, and prints the results to stdout. Alternatively, you can simply create a new instance of class `Database`. To run a query against this database, simply call `Database.transact(String query)`.

## Example Queries

`load teams` <br/>
`load records`<br/>
`load fans`<br/>
These commands load these three tables from file into the database.

`select * from teams where YearEstablished > 1960`<br/>
This returns all teams in our database that were founded later than the year 1960.

`select * from teams where YearEstablished > 1960 and Sport != 'eSports'`<br/>
This returns all teams in our database that were founded later than the year 1960 that are not eSports teams.

`select TeamName, Sport, Season, Wins, Losses, Ties from teams, records where Sport == 'NFL Football'`<br/>
This query performs a natural join between the teams and records tables on the TeamName column. It then gives us the season records of all NFL Football teams in our database.

`select TeamName, Sport, Season, Wins - Losses as WinDifference from teams, records where Sport == 'NFL Football' and WinDifference > 0`<br/>
This query calculates the WinDifference (defined as # Wins - # Losses) for every NFL Team in our database, and then only returns those records where the team won more games than it lost.

## Code Overview
The Database class is our main class that everything runs through. The query is passed into the database as a String. The database passes the query to the `Parser` class, which parses the query and then calls the appropriate function back in the database.

The database class owns a list of tables, and the SQL queries it processes operate on these tables. The implementation of the `Table` interface can be found in the table package. `RCTable` is a specific implementation of `Table` that stores its data in both Column and Row formats for speed.

Values stored in the database must all be of type `Literal`. The implementations of these types can be found in the literals package.

Operators take in two input literals and output a value. The `ArithmeticOperator` outputs a `Literal` while the `ComparisonOperator` outputs a `Boolean`. Operators are used to in order to filter selections or combine two columns together.
