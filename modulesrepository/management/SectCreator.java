/*
 * Created on Oct 19, 2005
 *
 * This software is distributed under the MIT License
 *
 * Copyright (c) 2005 Alain Becam, Paulo Lopes, Joakim Olsson, and Johan Simonsson
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

import modulesrepository.db.*;
import java.sql.*;
import java.util.*;

/**
 * Database interface for SectManager in order to load and store sect trees to
 * HSQL database using the PersistenceManager.
 * 
 * @author Johan Simonsson and Joakim Olsson, Daydream AB Sweden
 */
public class SectCreator {
	
	// table idParent String, idChild String
	private SectManager owner;
	private ArrayList validPlayers;
	
	final public String LOAD_ALL_SECTINFO = "Select * from SectRelationTable";
	final public String DELETE_ALL_SECTINFO = "Delete from SectRelationTable";
	final public String ADD_ROW = "Insert into SectRelationTable (idParent, idChild) values(?,?)";
	
	final public String LOAD_EXISTING_PLAYERS = "SELECT player_nick FROM player_info";
	
	public SectCreator(SectManager mgr)
	{
		owner = mgr;
		validPlayers = null;
	}

	private void populatePlayerList(Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		try {
			
			pstmt = conn.prepareStatement(LOAD_EXISTING_PLAYERS);
			
			rs = pstmt.executeQuery();
			
			if (validPlayers == null) 
				validPlayers = new ArrayList(20);
			else
				validPlayers.clear();
			
			while (rs.next()) {
				
				validPlayers.add(rs.getString("player_nick"));
				
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.close(pstmt, rs);
		}
		
	}
	
	private void validateSect(SectNode topNode) {

		// Prefix iteration of tree 
		
		Iterator c = topNode.getChildren().iterator();
		
		while (c.hasNext()) {
			SectNode sn = (SectNode)c.next();
			validateSect(sn);
		}
		
		// Now we're at the bottom
		
		if (!validPlayers.contains(topNode.getPlayerName())) {
			/* Player for current node doesnt exist anymore. To avoid messy
			 * sect trees remove current node and make followers (if any)
			 * prophets or nihilists.
			 */

			// Iterate children and detach them, then detach this
			
			Iterator i = topNode.getChildren().iterator();
			
			while (i.hasNext()) {
				SectNode child = (SectNode)i.next();
				child.detach();
			}
			
			topNode.detach();
			
		}
		
	}
	
	
	public ArrayList getAllSects()
	{
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
        Hashtable ht = new Hashtable();
        
        try {
       	
            conn = PersistenceManager.getInstance().getConnection();
            if (conn == null) {
            	throw new Exception("Failed to acquire connection from PersistenceManager!");
            }
            synchronized(conn) {
                
            	populatePlayerList(conn);
            	
                // prepare the query
                pstmt = conn.prepareStatement(LOAD_ALL_SECTINFO);
               
                if (pstmt == null)
                	throw new Exception("Failed to prepare statement!");
                
                // execute
                rs = pstmt.executeQuery();
                
                System.out.println("Reading sect database...");
                
                while(rs.next()) {
                	
                	String p1 = rs.getString("idParent");
                	String p2 = rs.getString("idChild");
                	
                	if(ht.get(p1) == null) ht.put(p1, new ArrayList());
                	ArrayList al = (ArrayList)ht.get(p1);
                	al.add(p2);
                }
            } 
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(Exception exc) {
            exc.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
            PersistenceManager.close(pstmt, rs);
        }
        
        System.out.println("Postprocessing sect data...");
        
        //second pass
        ArrayList top = (ArrayList)ht.get("__TOP_SECT_MEMBER");
        //int count = ((ArrayList)ht.get("__TOP_SECT_MEMBER")).size();

        ArrayList returnList = new ArrayList();
        
        if (top != null) {
        
	        for(int i = 0; i < top.size(); i++)
	        {
	        	SectNode temp  = owner.makeNode((String)top.get(i), null); //new SectNode((String)top.get(i), null);
	        	temp = fillMeUp(ht, temp);
	        	returnList.add(temp);
	        }
	        
        }
        
		return returnList;
		
	}
	
	public SectNode fillMeUp(Hashtable ht, SectNode parent)
	{
		
		//SectNode returnNode = new SectNode();
		ArrayList al = (ArrayList)ht.get(parent.getPlayerName());
		if(al != null)
		for(int i = 0; i < al.size(); i++)
		{
			SectNode sn = owner.makeNode((String)al.get(i), parent); //new SectNode((String)al.get(i), parent);
			sn = fillMeUp(ht, sn);
			
		}
		
		return parent;
	}

	public void dumpToDatabase(Collection sectNodes)
	{
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = PersistenceManager.getInstance().getConnection();
            synchronized(conn) 
            {
            	pstmt = conn.prepareStatement(DELETE_ALL_SECTINFO);
            	pstmt.executeUpdate();
            	PersistenceManager.close(pstmt, null);
            	
            	Iterator i = sectNodes.iterator();
            	
            	while(i.hasNext())
            	{
            		SectNode temp = (SectNode)i.next();
            		pstmt = conn.prepareStatement(ADD_ROW);
            		pstmt.setString(1, "__TOP_SECT_MEMBER");
            		pstmt.setString(2, temp.getPlayerName());
            		rs = pstmt.executeQuery();
            		PersistenceManager.close(pstmt, rs);
            		saveSectNode(conn, temp);
            		
            	}
            	
            }
        } catch(SQLException e) 
        {
            e.printStackTrace();
        } finally {
            // as a good coding practice close connections and statements
                PersistenceManager.close(pstmt, rs);
        }
	}
	
	public void saveSectNode(Connection conn, SectNode parent)
	{
		PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
        	synchronized(conn) 
        	{
        		Iterator i = parent.getChildren().iterator();
        		while(i.hasNext())
        		{
        			SectNode temp = (SectNode)i.next();
        			pstmt = conn.prepareStatement(ADD_ROW);
        			pstmt.setString(1, parent.getPlayerName());
        			pstmt.setString(2, temp.getPlayerName());
        			rs = pstmt.executeQuery();
        			PersistenceManager.close(pstmt, rs);
        			saveSectNode(conn, temp);
        		}
			}
        
        }
		catch(SQLException e) 
	    {
	         e.printStackTrace();
	    } finally {
	         // as a good coding practice close connections and statements
	    	PersistenceManager.close(pstmt, rs);
	    }
	}
	
	
}
