/*
 * PersistentPlayer.java
 *
 * Created on 6 de Agosto de 2005, 14:06
 *
 */

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
public class PersistentPlayer {
    
    /**
     * Error code for success.
     */
    public static final int SUCCESS = 0;
    /**
     * Error code for duplicated nick name.
     */
    public static final int ERROR_NEW_NICKNAME = 1;
    /**
     * Error code for unknown nick name.
     */
    public static final int ERROR_INVALID_LOGIN = 2;
    /**
     * Error code for duplicated nick name.
     */
    public static final int ERROR_DUPLICATE_NICKNAME = 3;
    /**
     * Error code for internal error.
     */
    public static final int ERROR_INTERNAL = 4;
    
    private static final String IS_VALID_PLAYER = "SELECT player_nick, player_password FROM player WHERE player_nick = ? AND player_password = ? AND player_active = true";
    private static final String ADD_PLAYER = "INSERT INTO player (player_name, player_faction, player_nick, player_password, player_active) VALUES (?, ?, ?, ?, true)";
    private static final String GET_PLAYER = "SELECT player_nick FROM player WHERE player_nick = ?";
    

	/**
     * Validated a login against a persistent profile.
     *
     * @param nick nick name
     * @param password password for the plauer
     * @return SUCCESS if login is correct
     */
    public static int login(String nick, String password) {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // go to the database and load friend list
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // chech if the player exists
                pstmt = conn.prepareStatement(GET_PLAYER);
                pstmt.setString(1, nick);
                // execute
                rs = pstmt.executeQuery();
                if(!rs.next()) {
                    return ERROR_NEW_NICKNAME;
                }
                
                // prepare the query
                pstmt = conn.prepareStatement(IS_VALID_PLAYER);
                pstmt.setString(1, nick);
                pstmt.setString(2, password);
                // execute
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    return SUCCESS;
                }
                return ERROR_INVALID_LOGIN;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
        return ERROR_INTERNAL;
    }
    
    /**
     * Create a new player in the persistence engine.
     *
     * @param nick nick name
     * @param password password for the player
     * @return error code
     */
    public static int createPersistentPlayer(String name, String faction, String nick, String password) {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // go to the database and load friend list
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(GET_PLAYER);
                pstmt.setString(1, nick);
                // execute
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    return ERROR_DUPLICATE_NICKNAME;
                }
                // close the statement and re-use it
                PersistenceManager.close(pstmt, null);
                // run the other query
                pstmt = conn.prepareStatement(ADD_PLAYER);
                pstmt.setString(1, name);
                pstmt.setString(2, faction);
                pstmt.setString(3, nick);
                pstmt.setString(4, password);
                // execute
                pstmt.execute();
                return SUCCESS;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
        return ERROR_INTERNAL;
    }
    
    /**
     * Check if the player exists on the database.
     *
     * @param nick nick name
     * @return true if exists
     */
    public static boolean existPlayer(String nick) {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // go to the database and load friend list
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(GET_PLAYER);
                pstmt.setString(1, nick);
                // execute
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    return true;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
        return false;
    }
}
