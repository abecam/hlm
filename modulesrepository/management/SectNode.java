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

/**
 * Representation of player in sect tree, containing linkbacks (if available) to 
 * PlayerEntity object. 
 * 
 * @author Joakim Olsson and Johan Simonsson, Daydream AB Sweden
 *
 */
public class SectNode {
	
	public static final int		LEVEL_NIHILIST = -1;			// Nihilist = A prophet without children

	public static final int		LEVEL_PROPHET = 0;
	public static final int		LEVEL_PRIEST_1ST = 1;
	public static final int		LEVEL_PRIEST_2ND = 2;
	public static final int		LEVEL_PRIEST_3RD = 3;
	public static final int		LEVEL_SECTMEMBER = 4;
	
	private String				playerName;
	private int					level;
	private SectNode			parent;
	private ArrayList			children;
	
	private ArrayList	 		superiors;
	
	private PlayerEntity playerRecord;	// Pointing back to owner of node (nodes with null here could perhaps be deleted)
	
	public SectNode(String name, SectNode parent) {

		children = new ArrayList();
		superiors = new ArrayList();

		playerRecord = null;
		this.parent = null;

		setLevel(LEVEL_NIHILIST);
		
		this.playerName = name;
		
		if (parent != null)
			attach(parent);
		
		
			
	}

	public ArrayList getChildren() {
		return children;
	}

	public void addChild(SectNode child) {
		if (!children.contains(child))
			children.add(child);
		
		// Nihilist got his first follower? Then he's a prophet now!
		if (level==LEVEL_NIHILIST) {
			level = LEVEL_PROPHET;
		}
	}
	public void removeChild(SectNode child) {
		if (children.contains(child))
			children.remove(child);
		
		if (level==LEVEL_PROPHET && children.size() == 0) {
			level = LEVEL_NIHILIST;
		}
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		
		if (level <= LEVEL_SECTMEMBER) 
			this.level = level;
		else
			this.level = LEVEL_SECTMEMBER;
	
		// Automatically update level of children nodes
		
		Iterator i = children.iterator();
		
		while (i.hasNext()) {
			
			SectNode sn = (SectNode)i.next();
			
			sn.setLevel(this.level+1);

		}

	}

	public SectNode getParent() {
		return parent;
	}

	public void setParent(SectNode parent) {
		this.parent = parent;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void detach() {
		
		if (parent != null) {
			
			// Remove this object from parent's children list before removing reference to parent
			parent.removeChild(this);
		}
		
		setParent(null);
		
		if (children.size() > 0) {
			// Player has followers - set as prophet (congratulations!)
			setLevel(LEVEL_PROPHET);		
		} else { 
			// Player has no followers
			setLevel(LEVEL_NIHILIST);
		}
		
		// Iterate through children to update their lists of superiors
		updateSuperiors();

		SectManager.getInstance().reportDetachment(this);
	}
	
	public void attach(SectNode toWhat) {
		
		if (toWhat == parent)				
			return;							// No need for any readjustments
		
		if (parent != null) {
			detach();						// Process disconnection first to maintain integrity 
		}
		
		setParent(toWhat);					// Point to new parent
		
		if (toWhat != null) {
			
			setLevel(toWhat.getLevel() + 1);
			toWhat.addChild(this);
			updateSuperiors();

			SectManager.getInstance().reportAttachment(this);
		
		} else {
			
			// Attaching to null? Kind of strange action, but handle it correctly anyway.
			if (children.size() > 0)
				setLevel(LEVEL_PROPHET);
			else
				setLevel(LEVEL_NIHILIST);
		}
		
	}

	public void updateSuperiors() {
		
		superiors.clear();

		if (parent != null)
			SectManager.getInstance().recursiveSearchForSuperior(this, superiors);
		
		// Update superiors for children as well
		Iterator i = children.iterator();
		
		while (i.hasNext()) {
			SectNode sn = (SectNode)i.next();
			sn.updateSuperiors();
		}
	}

	
	public SectNode getProphet() {
		if (getParent() == null)
			return this;
		else
			return getParent().getProphet();
	}

	public PlayerEntity getPlayerRecord() {
		return playerRecord;
	}

	public void setPlayerRecord(PlayerEntity playerRecord) {
		this.playerRecord = playerRecord;
		
	}

	public ArrayList getSuperiors() {
		return superiors;
	}

	public void setSuperiors(ArrayList superiors) {
		this.superiors = superiors;
	}
	
	
	
}


