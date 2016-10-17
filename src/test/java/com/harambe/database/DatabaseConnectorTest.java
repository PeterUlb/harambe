package com.harambe.database;

import junit.framework.TestCase;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DatabaseConnectorTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testConnection() {
        DatabaseConnector db = null;
        try {
            db = new DatabaseConnector();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(db.conn);
    }

    public void testShutdown() throws Exception {
        DatabaseConnector db = new DatabaseConnector();
        db.shutdown();
        assertTrue(db.conn.isClosed());
    }

    public void testQueryAndUpdate() throws Exception {
        DatabaseConnector db = new DatabaseConnector();

        // IDENTITY = auto-generated id
        db.update(
                "CREATE TABLE IF NOT EXISTS sample_table ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)");

        // fill the table
        db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('ABC', 100)");
        db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('DEF', 200)");
        db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('GHI', 300)");
        db.update(
                "INSERT INTO sample_table(str_col,num_col) VALUES('JKL', 400)");

        // do a query
        ResultSet rs = db.query("SELECT * FROM sample_table WHERE num_col < 500");
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        assertEquals(3, columnsNumber); // id, str_col and num_col
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                String colName = rsmd.getColumnName(i);
                String colValue = rs.getString(i);
                if (i == 1) {
                    continue; // skip the id column
                } else if (i == 2) {
                    assertEquals("str_col", colName.toLowerCase());
                    assertEquals("ABC", colValue);
                } else if (i == 3) {
                    assertEquals("num_col", colName.toLowerCase());
                    assertEquals("100", colValue);
                }
            }
            break;
        }
        db.update(
                "DROP TABLE sample_table");
        db.shutdown();
    }


    // This one is to print stuff out, make sure to comment the drop table statement out
//    public void testAThing() throws Exception {
//        DatabaseConnector db = new DatabaseConnector();
//        ResultSet rs = db.query("SELECT * FROM sample_table WHERE num_col < 500");
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int columnsNumber = rsmd.getColumnCount();
//        assertEquals(3, columnsNumber); // id, str_col and num_col
//        while (rs.next()) {
//            for (int i = 1; i <= columnsNumber; i++) {
//                String colName = rsmd.getColumnName(i);
//                String colValue = rs.getString(i);
//
//                System.out.println(colName + ": " + colValue);
//            }
//            System.out.println();
//        }
//    }
}