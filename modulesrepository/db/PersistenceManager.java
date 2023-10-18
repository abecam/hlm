/*
 * This software is distributed under the MIT License
 *
 * Copyright (c) 2005 Alain Becam, Paulo Lopes, Joakim Olsson, and Johan Simonsson - 2005
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package modulesrepository.db;

import java.sql.*;

/**
 * Helper class to store and load player persistent data.
 *
 * @author Paulo <pmlopes@gmail.com>
 * @version 1.0
 */
public class PersistenceManager {

    // single instance
    private static PersistenceManager instance;

    //  change the url to reflect your preferred db location and name
    //private String url = "jdbc:hsqldb:file:data/mmro-database";
    private String url = "jdbc:hsqldb:hsql://localhost/mmro";
    private String user = "sa";
    private String password = "";

    private Connection conn;

    static {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PersistenceManager getInstance() {
        if(instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    /**
     * Creates a new persistence manager
     */
    private PersistenceManager() {
        // extra init code could go here
        // for instance we could load the config from a file
        // instead from being hardcoded
    }

    /**
     * Internal helper to close resources
     */
    public static void close(PreparedStatement pstmt, ResultSet rs) {
        try {
            if(rs != null) rs.close();
        } catch(SQLException e) {
            // ignore
        }
        try {
            if(pstmt != null) pstmt.close();
        } catch(SQLException e) {
            // ignore
        }
    }

    /**
     * Return a new connection if never initialized or closed connection is
     * available. Otherwise a currently open connection is returned.
     * @return a new connection
     */
    public synchronized Connection getConnection() throws SQLException {
        if(conn == null) {
            conn = DriverManager.getConnection(url, user, password);
        } else {
            if(conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, password);
            }
        }
        return conn;
    }

    /**
     * Overload the base finalize method to assure that the DB connection is closed.
     */
    protected void finalize() throws Throwable {
        if(conn != null) {
            try {
                if(!conn.isClosed()) conn.close();
            } catch(SQLException e) {
                // ignore
            }
        }
        super.finalize();
    }
}
