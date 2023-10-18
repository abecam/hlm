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

package modulesrepository.chat;

import java.sql.*;
import modulesrepository.db.*;

/**
 * Helper class to store and load player persistent data.
 *
 * @author Paulo <pmlopes@gmail.com>
 * @version 1.0
 */
public class PersistentPlayer extends modulesrepository.db.PersistentPlayer {
    
    private static final String GET_FRIENDS = "SELECT player.player_nick FROM player, player_friend WHERE player.player_nick = player_friend.friend_nick AND player_friend.player_nick = ?";
    private static final String ADD_FRIEND = "INSERT INTO player_friend (player_nick, friend_nick) VALUES (?, ?)";
    private static final String REMOVE_FRIEND = "DELETE FROM player_friend WHERE player_nick = ? AND friend_nick = ?";
    
    /**
     * Loads the persistent data for a player
     *
     * @param player the plauer to be loaded
     */
    public static void loadPlayerData(Player player) {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // go to the database and load friend list
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(GET_FRIENDS);
                pstmt.setString(1, player.getName());
                // execute
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    player.loadFriend(rs.getString("player_nick"));
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
    }
    
    /**
     * Stores a new friend into the database so next time we will remember.
     *
     * @param player the player to be stored
     * @param nick the friends nick
     */
    public static void insertPlayerFriend(String player, String nick) {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // store friend list
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(ADD_FRIEND);
                pstmt.setString(1, player);
                pstmt.setString(2, nick);
                if(!pstmt.execute()) {
                    System.out.println("Store failed for: Player[" + player + "] = " + nick);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
    }
    
    /**
     * Removes a friend into the database so next time we will remember.
     *
     * @param player the player from which we will remove
     * @param nick the friends nick
     */
    public static void deletePlayerFriend(String player, String nick) {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // store friend list
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(REMOVE_FRIEND);
                pstmt.setString(1, player);
                pstmt.setString(2, nick);
                if(!pstmt.execute()) {
                    System.out.println("Remove failed for: Player[" + player + "] = " + nick);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close statements
            PersistenceManager.close(pstmt, rs);
        }
    }
}
