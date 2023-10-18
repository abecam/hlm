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

package modulesrepository.management;

import java.sql.*;
import java.util.*;
import modulesrepository.db.*;


/**
 * Helper class to store and load player persistent data.
 *
 * @author Paulo <pmlopes@gmail.com>
 * @version 1.0
 */
public class PlayerEntity extends PersistentPlayer {
    
	public static final int STATUS_ONLINE = 1;
	public static final int STATUS_AURABALL = 2;
	
    private static final String LOAD_ALL_PLAYER_INFO = "SELECT player_name, player_nick, player_x, player_y, player_z, player_direction, player_status, player_mindenergy, player_inventory1, player_inventory2, player_inventory3, player_inventory4, player_sectInfo FROM player_info, player WHERE player.player_nick = player_info.player_nick AND player.player_active = true";
    private static final String LOAD_PLAYER_INFO = "SELECT player_name, player_nick, player_x, player_y, player_z, player_direction, player_status, player_mindenergy, player_inventory1, player_inventory2, player_inventory3, player_inventory4, player_sectInfo FROM player_info, player WHERE player.player_nick = player_info.player_nick AND player.player_active = true AND player.player_nick = ?";
    private static final String INSERT_PLAYER_INFO = "INSERT INTO player_info(player_nick, player_x, player_y, player_z, player_direction, player_status, player_mindenergy, player_inventory1, player_inventory2, player_inventory3, player_inventory4, player_sectInfo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_PLAYER_INFO = "UPDATE player_info SET player_x = ?, player_y = ?, player_z = ?, player_direction = ?, player_status = ?, player_mindenergy = ?, player_inventory1 = ?, player_inventory2 = ?, player_inventory3 = ?, player_inventory4 = ?, player_sectInfo = ? WHERE player_nick = ?";
    private static final String DELETE_PLAYER_INFO = "DELETE FROM player_info WHERE player_nick = ?";
    
    private static final long	playerLoginTimeout = 30 * 1000;
    
    private float x, y, z, direction;
    private int status, inventory1, inventory2, inventory3, inventory4, sectInfo;
    private String nick, name;
    private int stillAlive;
    private float mindenergy, energymax; 	// The maximum energy for this level
    private int Xp; 						// Number of eXperience points.
    private int level;

    long	lastManaged;

	SectNode		sect;
    
    long lastSave;
    private static final int MAX_WAIT = 120 * 1000; // 30 secs.
    
    private int sampleTic;
    private static final int TIC_RATE = 20; // Each 20 steps, we add a line in the log.

    public void wasManaged() {
    	lastManaged = System.currentTimeMillis();
    }
    public boolean isOnline() {
    	return (System.currentTimeMillis() < (lastManaged + playerLoginTimeout));
    }
    
    private PlayerEntity() {
        // default values
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
        direction = 0.0f;
        status = 0;
        mindenergy = 200;
        inventory1 = 0;
        inventory2 = 0;
        inventory3 = 0;
        inventory4 = 0;
        sectInfo = 0;
        lastSave = System.currentTimeMillis();
        sampleTic = 0;
        energymax = 400;
        Xp = 0;
        level = 1;
        sect = null;
        lastManaged = 0;
    }
    
    private void connectToSect() {
    	
        sect = SectManager.getInstance().findNode(nick);
           
        if (sect == null) {
        	// Okay, no sect membership yet. Make own sectnode for nihilist!
        	sect = SectManager.getInstance().makeNode(nick, null);
        }
        
        sectInfo = sect.getLevel();
    }
    
    public static PlayerEntity load(String nick) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                PlayerEntity p = new PlayerEntity();
                // prepare the query
                pstmt = conn.prepareStatement(LOAD_PLAYER_INFO);
                pstmt.setString(1, nick);
                // execute
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    p.name = rs.getString("player_name");
                    p.nick = rs.getString("player_nick");
                    p.x = rs.getFloat("player_x");
                    p.y = rs.getFloat("player_y");
                    p.z = rs.getFloat("player_z");
                    p.direction = rs.getFloat("player_direction");
                    p.status = rs.getInt("player_status");
                    p.mindenergy = rs.getInt("player_mindenergy");
                    p.inventory1 = rs.getInt("player_inventory1");
                    p.inventory2 = rs.getInt("player_inventory2");
                    p.inventory3 = rs.getInt("player_inventory3");
                    p.inventory4 = rs.getInt("player_inventory4");
                    p.sectInfo = rs.getInt("player_sectInfo");

                    p.connectToSect();
                
                	p.sect.setPlayerRecord(p);
                    
                    p.level = p.inventory3;
                    
                    if (p.level < 1)
                        p.level=1;
                    p.Xp = p.inventory4;
                    
                    
                    return p;
                } else {
                    // it means that the query didn't return any values
                    // this can happen when there is no records on the database, or
                    // when the player table has data but no data exists on the
                    // player_info table.
                    
                    if(existPlayer(nick)) {
                        p.nick = nick;
                        // this means that the player table has data but no data exists for
                        // for the player_info table, just store the defaults.
                        PersistenceManager.close(pstmt, rs);
                        
                        pstmt = conn.prepareStatement(INSERT_PLAYER_INFO);
                        pstmt.setString(1, p.nick);
                        pstmt.setFloat(2, p.x);
                        pstmt.setFloat(3, p.y);
                        pstmt.setFloat(4, p.z);
                        pstmt.setFloat(5, p.direction);
                        pstmt.setInt(6, p.status);
                        pstmt.setInt(7, (int)(p.mindenergy));
                        pstmt.setInt(8, p.inventory1);
                        pstmt.setInt(9, p.inventory2);
                        pstmt.setInt(10, p.inventory3);
                        pstmt.setInt(11, p.inventory4);
                        pstmt.setInt(12, p.sectInfo);
                        pstmt.execute();
                    } else {
                        // this shoudln't happen, the chat login should have created the
                        // the account
                        return null;
                    }
                    return p;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
        return null;
    }
    
    public static List loadAll() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List all = new ArrayList();
        
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(LOAD_ALL_PLAYER_INFO);
                // execute
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    PlayerEntity p = new PlayerEntity();
                    p.name = rs.getString("player_name");
                    p.nick = rs.getString("player_nick");
                    p.x = rs.getFloat("player_x");
                    p.y = rs.getFloat("player_y");
                    p.z = rs.getFloat("player_z");
                    p.direction = rs.getFloat("player_direction");
                    p.status = rs.getInt("player_status");
                    p.mindenergy = rs.getInt("player_mindenergy");
                    p.inventory1 = rs.getInt("player_inventory1");
                    p.inventory2 = rs.getInt("player_inventory2");
                    p.inventory3 = rs.getInt("player_inventory3");
                    p.inventory4 = rs.getInt("player_inventory4");
                    p.sectInfo = rs.getInt("player_sectInfo");
                    
                    p.connectToSect();
                 
                    p.level = p.inventory3;
                    if (p.level < 1)
                        p.level=1;
                    p.Xp = p.inventory4;
                    // add to the list
                    all.add(p);
                }
                return all;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
        return null;
    }

    public void store() {
        System.out.println("Saving...");
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(UPDATE_PLAYER_INFO);
                pstmt.setFloat(1, x);
                pstmt.setFloat(2, y);
                pstmt.setFloat(3, z);
                pstmt.setFloat(4, direction);
                pstmt.setInt(5, status);
                pstmt.setInt(6, (int)mindenergy);
                pstmt.setInt(7, inventory1);
                pstmt.setInt(8, inventory2);
                pstmt.setInt(9, inventory3);
                pstmt.setInt(10, inventory4);
                pstmt.setInt(11, sectInfo);
                pstmt.setString(12, nick);
                pstmt.execute();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, null);
            lastSave = System.currentTimeMillis();
        }
    }

    public void delete() {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) {
                // prepare the query
                pstmt = conn.prepareStatement(DELETE_PLAYER_INFO);
                pstmt.setString(1, nick);
                // execute
                pstmt.execute();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, null);
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float player_x) {
        this.x = player_x;
    }

    public float getY() {
        return y;
    }

    public void setY(float player_y) {
        this.y = player_y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float player_z) {
        this.z = player_z;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float player_direction) {
        this.direction = player_direction;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int player_status) {
        this.status = player_status;
    }

    public int getSectInfo() {
        return sectInfo;
    }

    public void setSectInfo(int player_sectInfo) {
        this.sectInfo = player_sectInfo;
    }

    public float getMindEnergy() {
        return mindenergy;
    }

    public void setMindEnergy(float player_mindenergy) {
        this.mindenergy = player_mindenergy;
    }

    public int getInventory1() {
        return inventory1;
    }

    public void setInventory1(int player_inventory1) {
        this.inventory1 = player_inventory1;
    }

    public int getInventory2() {
        return inventory2;
    }

    public void setInventory2(int player_inventory2) {
        this.inventory2 = player_inventory2;
    }

    public int getInventory3() {
        return inventory3;
    }

    public void setInventory3(int player_inventory3) {
        this.inventory3 = player_inventory3;
    }

    public int getInventory4() {
        return inventory4;
    }

    public void setInventory4(int player_inventory4) {
        this.inventory4 = player_inventory4;
    }

    public String getNick() {
        return nick;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String rName) {
        name = rName;
    }
    
    public void setAll(float xPos, float yPos, float zPos, float dir, int status, int invent1, int invent2, int invent3, int invent4, int mindEnergy) {
        this.x = xPos;
        this.y = yPos;
        this.z = zPos;
        this.direction = dir;
        this.status = status;
        //this.inventory1 = invent1;
        //this.inventory2 = invent2;
        //this.inventory3 = invent3;
        //this.inventory4 = invent4;
        //this.mindenergy = mindEnergy;
        // The sect info is determined HERE !
    }
    
    public String getAll() {
        // test the persistence engine...
        //if(System.currentTimeMillis() > lastSave + MAX_WAIT) store();
        return new String(nick + " : " + name + " : " + x + " : " + y + " : " + z + " : " + direction + " : " + status + " : " + (int)mindenergy + " : " + sectInfo + " : " + inventory1 + " : " + inventory2 + " : " + inventory3 + " : " + inventory4 + "/");
    }
        
    public String getAll2D() {
        return new String(nick + " : " + name + " : " + x + " : " + z + " : "  + status + " : " + (int)mindenergy + " : " + sectInfo + " : " + inventory1 + " : " + inventory2 + " : " + inventory3 + " : " + inventory4 + "/");
    }
    
    public void isUpdated()
    {
        stillAlive = 0;
    }
    
    public void saveAttempt()
    {
        if(System.currentTimeMillis() > lastSave + MAX_WAIT) store();
    }
    
   
    public void nonResponding()
    {
        if ((this.status & STATUS_ONLINE) != 0)
        {
            // If the player is both-nonresponding and in normal mode, we have to change his status
            // to push it in aura-ball mode (to respect the game rules and protect the player)
            stillAlive++;
            if (stillAlive > 100)
            {
                // Ok, so the player seems to be off-line - clear online-bit and set auraball bit
                this.status = (this.status ^ STATUS_ONLINE) | STATUS_AURABALL; 
            }
        }
    }

    public boolean ok4TheLog()
    {
        sampleTic++;
        if (sampleTic == TIC_RATE)
        {
            sampleTic = 0;
            return true;
        }
        return false;
    }
    
    public void increaseMindEnergy(float value)
    {
        this.mindenergy += value;
        checkEnergy();
    }
    
    public void decreaseMindEnergy(float value)
    {
        this.mindenergy -= value;
        checkEnergy();
    }
    
    public void checkEnergy()
    {
        if (this.mindenergy > energymax)
            mindenergy = energymax;
        if (this.mindenergy < 0)
            this.mindenergy = 0;
    }
    
    public int getXp(int value)
    {
        this.Xp+=value;
        this.inventory4= Xp;
        
        int limit = ((int)(Math.exp(level)*energymax));

        if (Xp > limit)
        {
            level++;
            this.energymax=400+level*200; // Absolute to avoid cheat...
            this.inventory3 = level;
            return (limit);
        }
        return 0;
    }
    
    /**
     * @return Returns the level.
     */
    public int getLevel()
    {
        return level;
    }
    /**
     * @param level The level to set.
     */
    public void setLevel(int level)
    {
        this.level = level;
    }
    /**
     * @return Returns the xp.
     */
    public int getXp()
    {
        return Xp;
    }
    /**
     * @param xp The xp to set.
     */
    public void setXp(int xp)
    {
        this.Xp = xp;
    }

	/**
     * @return Returns the energymax.
     */
    public float getEnergymax()
    {
        return energymax;
    }
    public SectNode getSect() {
		return sect;
	}

	public void setSect(SectNode sect) {
		this.sect = sect;
	}

}
