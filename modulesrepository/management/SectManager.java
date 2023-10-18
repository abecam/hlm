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

import java.util.*;

import utils.LogManager;

/**
 * Manages sect structures and provides interface for the GameManagement class.
 * 
 * @author Joakim Olsson and Johan Simonsson, Daydream AB Sweden
 */
public class SectManager {

	private Hashtable	sects;
	private Hashtable	allNodes;
	private LogManager 	logger; 
	private SectCreator creator;
	
	private long 		lastSave;
	
	private static SectManager instance;
	
	private SectManager() {
		sects = new Hashtable(10);
		allNodes = new Hashtable(50);
		creator = new SectCreator(this);
		logger = new LogManager();
		lastSave = System.currentTimeMillis();
		
		logger.initLog();
		logger.setSystemEcho(true);
	}

	public SectNode makeNode(String name, SectNode parent) {
		
		SectNode result = null;
		
		if (allNodes.contains(name)) {
			// Just a precaution so that we dont get duplicates in the hash table
			result = (SectNode)allNodes.get(name);
			if (parent != null) 
				result.attach(parent);
		} else {
			result = new SectNode(name, parent);
			synchronized (allNodes) {
				allNodes.put(name, result);
			}
		}
		
		return result;
	}
	
	
	
	public void loadSects() {
		
		ArrayList topNodes = creator.getAllSects();
		
		// Transfer node array into hashtable 
		
		logger.add2Log("Loaded data on " + topNodes.size() + " sects.");
		
		Iterator i = topNodes.iterator();
		
		while (i.hasNext()) {
			
			// Add node to prophet table
			SectNode topNode = (SectNode)i.next();
			
			sects.put(topNode.getPlayerName(), topNode);
			
			// Add entire sect to allNode table
			
			Collection members = new ArrayList();
			getAllChildren(topNode, members);
			
			Iterator sectMembers = members.iterator();
			
			while (sectMembers.hasNext()) {
				SectNode sectMember = (SectNode)sectMembers.next();
				
				if (!allNodes.containsKey(sectMember.getPlayerName())) {
					allNodes.put(sectMember.getPlayerName(), sectMember);
				} else {
					logger.add2Log("Illegal references found in sect table: Player " + sectMember.getPlayerName() + " exists in multiple sects!");
				}
			}
			
		}
		
		printAll();
	}

	public static SectManager getInstance() {
		if (instance == null) {
			instance = new SectManager();
		}
		return instance;
	}
	
	public SectNode findNode(String playerName) {
		
		if (allNodes.containsKey(playerName)) {
			
			return (SectNode)allNodes.get(playerName);
			
		}
		
		return null;
	}

	public void reportDetachment(SectNode where) {
		if (!sects.containsKey(where.getPlayerName()) && (where.getLevel() == SectNode.LEVEL_PROPHET)) {
			
			// Player is not already listed in prophet table (phew!), and since he's a prophet, he should be there!
			sects.put(where.getPlayerName(), where);
			
		} else {
			
			// Player already listed in sect table or player is nihilist
			
			if (where.getLevel() != SectNode.LEVEL_NIHILIST)
				logger.add2Log("Detached player " + where.getPlayerName() + " (new prophet) already listed in prophet table?!");
			
		}
	}
	
	public void reportAttachment(SectNode where) {
		if (sects.containsKey(where.getPlayerName())) {
			sects.remove(where.getPlayerName());
		} else { 
			/* Former prophet wasnt listed in sect table? Or we could get here
			 * if a new node was created which was never listed as a prophet -
			 * then all is fully ok!
			 */			
		}
	}
	
	public Collection getSectSuperiorsForPlayer(String playerName, SectNode sect) {

		logger.add2Log("Composing list of sect superiors for player " + playerName);
		
		SectNode sn = recursiveSearchForName(sect, playerName);

		Collection al = new ArrayList();
		recursiveSearchForSuperior(sn, al);
		
		logger.add2Log("(" + al.size() + " superiors added to list)");

		return al;
	}

	public void recursiveSearchForSuperior(SectNode sn, Collection names) {

		if (sn.getParent() != null) {

			names.add(sn.getParent().getPlayerName());
			recursiveSearchForSuperior(sn.getParent(), names);
		}

	}

	private SectNode recursiveSearchForName(SectNode sn, String playerName) {

		if (sn.getPlayerName() == playerName)
			return sn;

		Iterator i = sn.getChildren().iterator();

		while (i.hasNext()) {

			SectNode temp = (SectNode) i.next();
			SectNode check = recursiveSearchForName(temp, playerName);

			if (check != null)
				return check;

		}

		return null;

	}
	

	
	// Match interface from GameManagement
	public void addToSect(PlayerEntity from, PlayerEntity to) {

		logger.add2Log("Player " + from.getNick() + " now following " + to.getNick());
		
		// Attach entry to new "target"
		from.getSect().attach(to.getSect());
		
	}
    
	public int disconnectSubsect(PlayerEntity player, PlayerEntity guru) {
		
		if (player == null || guru == null)
			return 1;
		
		logger.add2Log("Player " + player.getNick() + " was kicked out of " + guru.getName() + "'s sect");
		
		if (player.getSect().getSuperiors().contains(guru.getNick())) {
			
			// Specified guru is above player in rank and in same sect - kick OK!
		
			player.getSect().detach();
			
		} else {
			return 1;
		}
		
		return 0;
		
	}

    public void breakFaith(PlayerEntity who)  { 
    	
    	if (who.getSect() != null) {
    		
    		who.getSect().detach();
    		
    	}
    	
    }
    
    public void saveToDB() {
    	System.out.println("Saving sect data to database:");
    	printAll();
    	creator.dumpToDatabase(sects.values());
    }
    
    public void saveToDBAttempt(long interval) {
    	if (System.currentTimeMillis() > (lastSave + interval)) {
    		saveToDB();
    		lastSave = System.currentTimeMillis();    		
    	}
    }
    
    public void printAll() {
    	Iterator i = sects.values().iterator();
    	
		System.out.println("-Full sectlist---------------------");

		while (i.hasNext()) {
    		printSectTree((SectNode)i.next(), new Integer(2));
    		System.out.println("-----------------------------------");
    	}
		
		System.out.println("-Nihilists-------------------------");
		
		printNihilists(40);

		System.out.println("-----------------------------------");
    }

    public void printNihilists(int maxLineWidth) {
		int lineCounter = 0;
		
		Iterator i = allNodes.values().iterator();
		
		while (i.hasNext()) {
			SectNode sn = (SectNode)i.next();
			
			if (sn.getLevel() == SectNode.LEVEL_NIHILIST) {		// Only print nihilists
				
				System.out.print(sn.getPlayerName() + ", ");
				
				lineCounter += sn.getPlayerName().length();
				if (lineCounter >= maxLineWidth) {				// Line break after 40 chars
					System.out.println();
					lineCounter = 0;
				}
				
			}
		}
    }
    
	public void printSectTree(SectNode sn, Integer backStrokeOfTheWest)
	{
		for(int i = 0; i < backStrokeOfTheWest.intValue(); i++)
			System.out.print(" ");
		
		System.out.println(sn.getPlayerName() + " " + sn.getLevel());
		Iterator i = sn.getChildren().iterator();
		
		while(i.hasNext())
		{
			backStrokeOfTheWest = new Integer(backStrokeOfTheWest.intValue() + 3);
			SectNode temp = (SectNode)i.next();
			printSectTree(temp, backStrokeOfTheWest);
			backStrokeOfTheWest = new Integer(backStrokeOfTheWest.intValue() - 3);
		}
	}

	public Collection getEntireSect(SectNode fromWhere) {

		Collection al = new ArrayList();
		
		getAllChildren(fromWhere, al);
		
		return al;
		
	}
	
	public Collection getEntireSect(PlayerEntity fromWho) {
		
		SectNode prophet = fromWho.getSect().getProphet();
		
		return getEntireSect(prophet);
		
	}
	
	public void getAllChildren(SectNode source, Collection container) {
		
		container.add(source);
		
		Iterator i = source.getChildren().iterator();
		
		while (i.hasNext()) {
			getAllChildren((SectNode)i.next(), container);
		}
	}
	
}
