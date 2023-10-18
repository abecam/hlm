/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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

package modulesrepository;

import java.util.HashMap;
import java.util.Iterator;

import utils.ITickable;
import utils.Tick;


import modulesrepository.management.PlayerEntity;

/**
 * This module manages a simple set of entities.
 * 
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GameTemp implements ITickable /*, IGameManagement */
{
    Tick myTicker;
    int idMonst = 0; // To give unique Id to the monsters
        
    HashMap ourEntities; // HashMap of ourEntities[];
    
    public class Monster
    {
        // The monster has the goal to attack/help an entity (enemy/friend)
        // It uses his perception to target an entity (player or monster), then try to reach it
        String name;
        float posX, posY, dir;
        int energy,faction; // The factions -> Azarel or Penemue, a monster can attack the rival faction or help (boost) one friend
        int type;
        Integer monsterId; // Unique id (we hope)
        /**
         * @param name
         * @param posX
         * @param posY
         * @param posZ
         * @param dir
         * @param energy
         * @param faction
         * @param type
         */
        public Monster(String name, float posX, float posY, int energy, int faction, int type)
        {
            this.name = name;
            this.posX = posX;
            this.posY = posY;
            
            this.dir = 0;
            this.energy = energy;
            this.faction = faction;
            this.type = type;
        }
        
        public String giveAll()
        {
            // Return a string of all informations for the monsters
            return new String(name + " : " + posX+ " : " +posY + " : " +energy + " : " + faction + " : " + type + "/");
        }
        /**
         * @return Returns the name.
         */
        public String getName()
        {
            return name;
        }
        /**
         * @param name The name to set.
         */
        public void setName(String name)
        {
            this.name = name;
        }
        /**
         * @return Returns the posX.
         */
        public float getPosX()
        {
            return posX;
        }
        /**
         * @param posX The posX to set.
         */
        public void setPosX(float posX)
        {
            this.posX = posX;
        }
        /**
         * @return Returns the posY.
         */
        public float getPosY()
        {
            return posY;
        }
        /**
         * @param posY The posY to set.
         */
        public void setPosY(float posY)
        {
            this.posY = posY;
        }
       
        /**
         * @return Returns the type.
         */
        public int getType()
        {
            return type;
        }
        /**
         * @param type The type to set.
         */
        public void setType(int type)
        {
            this.type = type;
        }
        /**
         * @return Returns the energy.
         */
        public int getEnergy()
        {
            return energy;
        }
        /**
         * @param energy The energy to set.
         */
        public void setEnergy(int energy)
        {
            this.energy = energy;
        }
        /**
         * @return Returns the monsterId.
         */
        public Integer getMonsterId()
        {
            return monsterId;
        }
        /**
         * @param monsterId The monsterId to set.
         */
        public void setMonsterId(Integer monsterId)
        {
            this.monsterId = monsterId;
        }
    }
    
    HashMap ourMonsters;
    
    public class Sect
    {
        HashMap Members; // List of members.
        
        public void addMember(int level, String IdMember, String IdGuru, int NbFollowers)
        {
            // Level : 0 is the top-most (Guru of all, the prophet), then 1, 2, 3...
            // Then a member give faith to someone, if this last is not already guru, then he becomes one, level 0, and the member get a level 1
            // (The sect has no other existence, it is a structure)
            // If he is already a guru, then he get another follower and the member get the level of the guru + 1
            // The member give then some mindenergy to the guru (and 10% each day, without loosing)
            // If a member earn some energy, a part is given to the "parent", then a part of the part to the next parent, ...
            
            // A member can break is faith, but also a guru can kick off one member (just check the level is superior)
            
            // Finally, the nihilist are also considered (technically) as a sect, they just are all at level 0.
            ; 
        }
        
        public void removeMember()
        {
            ;
        }
        
        public void alterMember()
        {
            // Mainly to change level
            ;
        }
    }
    
    public class Spell
    {
        String idCreator;
        int type,  status;
        float posX, posY, posZ, dir;
        float posCreatX, posCreatY, posCreatZ;
        int nbPlayersInvolved;
        int nbCurrentContributors;
        int placesAvailable; // Show which place is still free (just a shift)
        int energy;
        String victimNick;
        HashMap contributors;
        
        /**
         * @param idCreator
         * @param type
         * @param status
         * @param posX
         * @param posY
         * @param posZ
         * @param dir
         * @param nbPlayersInvolved
         */
        public Spell(String idCreator, int type, float posX, float posY, float posZ, float posCreatX, float posCreatY, float posCreatZ, float dir,
                int nbPlayersInvolved, int energy, String victimNick)
        {
            this.idCreator = idCreator;
            this.type = type;
            this.status = 0; // "Forming"
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.posCreatX = posCreatX;
            this.posCreatY = posCreatY;
            this.posCreatZ = posCreatZ;
            this.dir = dir;
            this.nbPlayersInvolved = nbPlayersInvolved;
            this.energy = energy;
            this.victimNick = victimNick;
            this.nbCurrentContributors = 1; // The creator...
            this.contributors = new HashMap();
            this.placesAvailable = 0; // Binary information about available position (from lowest bit to highest)
        }
        
        public String getAll()
        {
            return new String(type + " : " +posX+ " : " +posY + " : " + posZ + " : " + dir + " : " +posCreatX+ " : " +posCreatY + " : " + posCreatZ + " : " + status + " : "+ energy+" : "+ nbPlayersInvolved+" : "+ placesAvailable+"/");
        }
        
        /**
         * @return Returns the posX.
         */
        public float getPosX()
        {
            return posX;
        }
        /**
         * @param posX The posX to set.
         */
        public void setPosX(float posX)
        {
            this.posX = posX;
        }
        /**
         * @return Returns the posY.
         */
        public float getPosY()
        {
            return posY;
        }
        /**
         * @param posY The posY to set.
         */
        public void setPosY(float posY)
        {
            this.posY = posY;
        }
        /**
         * @return Returns the posZ.
         */
        public float getPosZ()
        {
            return posZ;
        }
        /**
         * @param posZ The posZ to set.
         */
        public void setPosZ(float posZ)
        {
            this.posZ = posZ;
        }
        /**
         * @return Returns the status.
         */
        public int getStatus()
        {
            return status;
        }
        /**
         * @param status The status to set.
         */
        public void setStatus(int status)
        {
            this.status = status;
        }
        /**
         * @return Returns the type.
         */
        public int getType()
        {
            return type;
        }
        /**
         * @param type The type to set.
         */
        public void setType(int type)
        {
            this.type = type;
        }
        /**
         * @return Returns the dir.
         */
        public float getDir()
        {
            return dir;
        }
        /**
         * @param dir The dir to set.
         */
        public void setDir(float dir)
        {
            this.dir = dir;
        }
        /**
         * @return Returns the energy.
         */
        public int getEnergy()
        {
            return energy;
        }
        /**
         * @param energy The energy to set.
         */
        public void setEnergy(int energy)
        {
            this.energy = energy;
        }
        
        /**
         * @return Returns the posCreatX.
         */
        public float getPosCreatX()
        {
            return posCreatX;
        }
        /**
         * @param posCreatX The posCreatX to set.
         */
        public void setPosCreatX(float posCreatX)
        {
            this.posCreatX = posCreatX;
        }
        /**
         * @return Returns the posCreatY.
         */
        public float getPosCreatY()
        {
            return posCreatY;
        }
        
        /**
         * @param posCreatY The posCreatY to set.
         */
        public void setPosCreatY(float posCreatY)
        {
            this.posCreatY = posCreatY;
        }
        /**
         * @return Returns the posCreatZ.
         */
        public float getPosCreatZ()
        {
            return posCreatZ;
        }
        /**
         * @param posCreatZ The posCreatZ to set.
         */
        public void setPosCreatZ(float posCreatZ)
        {
            this.posCreatZ = posCreatZ;
        }
        /**
         * @return Returns the nbCurrentContributors.
         */
        public int getNbCurrentContributors()
        {
            return nbCurrentContributors;
        }
        /**
         * @param nbCurrentContributors The nbCurrentContributors to set.
         */
        public void setNbCurrentContributors(int nbCurrentContributors)
        {
            this.nbCurrentContributors = nbCurrentContributors;
        }
        /**
         * @return Returns the nbPlayersInvolved.
         */
        public int getNbPlayersInvolved()
        {
            return nbPlayersInvolved;
        }
        /**
         * @param nbPlayersInvolved The nbPlayersInvolved to set.
         */
        public void setNbPlayersInvolved(int nbPlayersInvolved)
        {
            this.nbPlayersInvolved = nbPlayersInvolved;
        }
        /**
         * @return Returns the contributors.
         */
        public HashMap getContributors()
        {
            return contributors;
        }
        /**
         * @param contributors The contributors to set.
         */
        public void addContributor(String contributorId)
        {
            this.contributors.put(contributorId, contributorId); // For now, just to keep trace of the contributor.
        }
        /**
         * @return Returns the victimNick.
         */
        public String getVictimNick()
        {
            return victimNick;
        }
        /**
         * @return Returns the placesAvailable.
         */
        public int getPlacesAvailable()
        {
            return placesAvailable;
        }
        /**
         * @param placesAvailable The placesAvailable to set.
         */
        public void setPlacesAvailable(int placesAvailable)
        {
            this.placesAvailable = placesAvailable;
        }
    }
    
    HashMap ourSpells; // Current spells.
    
    public PlayerEntity findByRealName(String realName)
    {
        PlayerEntity ourEntity = null;
        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext() ;)
        {
            PlayerEntity currentEntity = ((PlayerEntity )(iter.next()));
            if ( currentEntity.getName().equals(realName))
            {
                ourEntity = currentEntity;
                break;
            }
        }   
        return ourEntity;
    }
    // Player distributed method
    
    synchronized public String managePos(String Nick, String RealName, float x, float y, float z, float dir, int status, int mindEnergy)
    {
        StringBuffer myOutput = new StringBuffer();
        
        String IdObj = new String(Nick);
      
        if (ourEntities.containsKey(IdObj))
        {
            // If the statut is 4096, then the client initialise itself on the PC, so I want to receive informations,
            // not to send.
            if ( status != 4096)
            {
                // 65536 is for Mobile initialisation
                if ( status != 65536)
                {
                    ((PlayerEntity )(ourEntities.get(IdObj))).setAll(x, y, z, dir, status,0,0,0,0,mindEnergy);
                }
            }
            else
            {
                // If the client connect itself on a PC, we both have to send him his information and to switch
                // his statut to "Player", which means he is not anymore only the aura ball
                ((PlayerEntity )(ourEntities.get(IdObj))).setStatus( 1 );
            }
        }
        else
        {
            // Here is a new one !!!!
            // From PC ?
            if ( status == 4096)
            {
                status = 1; // Then his statut is "normal"
            }
            else
            {
                // Might be an aura ball...
                if ( status == 65536)
                {
                    status = 2;
                }
            }
                
            PlayerEntity theLittleNewOne = PlayerEntity.load(Nick);
            theLittleNewOne.setX(x);
            theLittleNewOne.setY(y);
            theLittleNewOne.setZ(z);
            theLittleNewOne.setDirection(dir);
            theLittleNewOne.setStatus(status);
            ourEntities.put(IdObj, theLittleNewOne);
        }
        // Anyway, we send back the result.
        // It might be good to filter the result considering the distance. Here it is also... The bad but fast
        // square distance...
        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext() ;)
        {
            PlayerEntity currentEntity = ((PlayerEntity )(iter.next()));
            if ( (Math.abs(currentEntity.getX() - x) + Math.abs(currentEntity.getY() - y) + Math.abs(currentEntity.getZ() -z)) < 1000)
            {
                myOutput.append(currentEntity.getAll());
            }
        }
        myOutput.append("#");
        
        return (new String(myOutput));
    }
    
    // Monsters distributed methods
    synchronized public void spawnMonsters(int type, float posX,float posY,float posZ, int faction, int number, int energy)
    {
        // One type of monster (rabbit, snake, ...) can do only ONE thing (take energy, give energy).
        // Type 0-> good, 1-> bad. Faction -> 16 Azarel, 32 Penemue

        String name;
    
        if (faction == 16)
        {
            if (type == 0)
            {
        	name = new String("Flute");
            }
        	else
        	{
        	name = new String("Diamond gangsaw machine (with feet)");
        	}
        }
        else
        {
            if (type == 0)
            {
                name = new String("Living Tree");
            }
        	else
        	{ 
        	    name = new String("Owl");
        	}
        }
        for (int i = 0 ; i < number ; i++)
        {
            Monster newMonster= new Monster(name,posX, posY , energy, faction, type); // Create number monsters of type type at the start position given owned by faction (so attacking or helping following that)
            Integer idMonster = new Integer(idMonst++);
            newMonster.setMonsterId(idMonster);
            ourMonsters.put(idMonster, newMonster);
        }
    }
    
    synchronized public String updateMonsters(float posX, float posY)
    {
        // return every informations about monsters "around" the given position, then the client can print
        // the monsters (this method is called to render monsters)
        StringBuffer myOutput = new StringBuffer("");
        
        // Give at leas
        Iterator iter = ourMonsters.values().iterator();
        for (; iter.hasNext() ;)
        {
            Monster currentEntity = ((Monster )(iter.next()));
            if ( (Math.abs(currentEntity.getPosX() - posX) + Math.abs(currentEntity.getPosY() - posY)) < 50)
            {
                myOutput.append(currentEntity.giveAll());
            }
        }
        myOutput.append("#");
        
        return (new String(myOutput));
    }
    
    // End monsters distributed methods
    
    // Sect distributed methods
    
    synchronized public void giveFaith(String IdPlayer, String IdGuru)
    {
        // If the IdGuru is not already guru, raise it at guru (level 0), and add the player as a member (level 1)
        // Or add a member to the guru list, and take the level of the guru+1 for the player
    }
    
    synchronized public int kickOutMemberOut(String IdPlayer, String IdGuru)
    {
        // As the name said, verify fisrt if the guru can (player member of guru followers)
        return 0;
    }
    
    synchronized public void breakFaith(String IdPlayer)
    {
        // If the player is in a sect, push it away. If he has follower, he keeps them, a new sect is done !
    }
    
    // End sect distributed methods
    
    // Magic distributed methods
    
    synchronized public void ask4Spell(String creatorId, int targetType, String victimRealName, String targetNick, int spellType, float posX, float posY, float posZ, float posCreatX, float posCreatY, float posCreatZ,int nbParticipants, int energyGiven)
    {
        // Ask for the creation of a spell, will then give the projected glyph on the ground (the client take that from upatMagic)
        // The TargetType can be position (the target is in front of the player) and name (the real name of a player)
        // The position is the center of the circle, the Creat(or) position give the position of the asker, so the center of the first triangle in the complete implementation
        // The energy given is just the energy given by the initial player
        // Verify the creator has enough magie
        PlayerEntity creatorEntity;
        if (!ourEntities.containsKey(creatorId))
        {
            ; // There is something wrong, but what to do?
        } else
        {
            creatorEntity = (PlayerEntity) ourEntities.get(creatorId);
            if (creatorEntity.getMindEnergy() >= energyGiven)
            {
                String victimNick = null;
                if (targetType == 0)
                {
                    // Target type already given, nothing to do
                    victimNick = targetNick;
                } else if (targetType == 16) // By real name
                {
                    PlayerEntity victim = findByRealName(victimRealName);
                    if (victim == null)
                        return; // Nothing to do...
                    victimNick = victim.getNick();
                } else
                {
                    // Or we aim at the victim...
                    Iterator iter = ourEntities.values().iterator();
                    for (; iter.hasNext();)
                    {
                        PlayerEntity currentEntity = ((PlayerEntity) (iter.next()));
                        if ((Math.abs(currentEntity.getX() - posX) + Math.abs(currentEntity.getY() - posY) + Math.abs(currentEntity.getZ() - posZ)) < 10)
                        {
                            victimNick = currentEntity.getNick();
                        }
                    }
                }
                if (victimNick != null)
                {
                    Spell theNewSpell = new Spell(creatorId, spellType, posX, posY, posZ, posCreatX, posCreatY, posCreatZ, 0, nbParticipants, energyGiven, victimNick); // The

                    if (nbParticipants == 1)
                    {
                        // So the spell is confirmed !
                        theNewSpell.setStatus(1); // On the way !
                    }

                    // Then remove the energy from the creator
                    creatorEntity.setMindEnergy(creatorEntity.getMindEnergy() - energyGiven);
                    if (creatorEntity.getMindEnergy() == 0)
                    {
                        creatorEntity.setStatus(2); // Aura ball only...
                    }
                    // And add the spell
                    ourSpells.put(creatorId, theNewSpell);
                }
            }
        }
    }
    
    synchronized public String updateMagic(float posX,float posY,float posZ)
    {
        // Return the informations concerning the spells in the zone around the given position
        // Used to draw spell before (for the creation of it) and during execution
        // The first phase (selection of the spell) is only on the client
        StringBuffer myOutput = new StringBuffer();
        
        Iterator iter = ourSpells.values().iterator();
        for (; iter.hasNext() ;)
        {
            Spell currentSpell = ((Spell)(iter.next()));
            if ( (Math.abs(currentSpell.getPosX() - posX) + Math.abs(currentSpell.getPosY() - posY) + Math.abs(currentSpell.getPosZ() - posZ)) < 1000)
            {
                myOutput.append(currentSpell.getAll());
            }
        }
        myOutput.append("#");
        return null;
    }
    
    synchronized public void contributeToSpell(String creatorId, String playerId, int energyGiven, int placeWanted)
    {
        // Another player is in a triangle. If all needed players are here, the spell is ok to be used
        PlayerEntity contributorEntity;
        
        if (!ourSpells.containsKey(creatorId))
        {
            ; // There is something wrong, but what to do?
        } else
        {
            Spell ourSpell = (Spell) ourSpells.get(creatorId);
            contributorEntity = (PlayerEntity) ourEntities.get(playerId);
            if (contributorEntity.getMindEnergy() >= energyGiven)
            {
                // Is the triangle still available
                if ((ourSpell.getPlacesAvailable() & placeWanted) == 0)
                {
                    // Then remove the energy from the creator
                    contributorEntity.setMindEnergy(contributorEntity.getMindEnergy() - energyGiven);

                    ourSpell.setEnergy(ourSpell.getEnergy() + energyGiven);
                    ourSpell.setNbCurrentContributors(ourSpell.getNbCurrentContributors() + 1);
                    ourSpell.addContributor(playerId);
                    ourSpell.setPlacesAvailable(ourSpell.getPlacesAvailable() | placeWanted);
                    if (ourSpell.getNbCurrentContributors() == ourSpell.getNbPlayersInvolved())
                    {
                        // So the spell is confirmed !
                        ourSpell.setStatus(1); // On the way !
                    }
                }
            }
        }
    }
    
    synchronized public void cancelSpell(String creatorId, String playerId)
    {
        // One player break the spell. The mindEnergy given is lost, the spell also
        if (ourSpells.containsKey(creatorId))
        {
            Spell ourSpell = (Spell) ourSpells.get(creatorId);
            if (ourSpell.getContributors().containsKey(playerId))
            {
                // Then the asker is truly a caster (then it's still a little bit unsure, it's possible to cheat),
                // We cancel the spell.
                ourSpells.remove(creatorId);
            }
        }
    }
    
    synchronized public void confirmSpell(String creatorId)
    {
        // The creator confirm the spell, it becomes active.
        if (ourSpells.containsKey(creatorId))
        {
            Spell ourSpell = (Spell) ourSpells.get(creatorId);
            ourSpell.setStatus(16); // Running !
            PlayerEntity victimEntity = (PlayerEntity) ourEntities.get(ourSpell.getVictimNick());
            // Is everyone here ?
            if ((ourSpell.getNbPlayersInvolved() - ourSpell.getNbCurrentContributors()) == 0)
            {
                if (ourSpell.getType() == 0)
                {
                    // Take energy !
                    if (victimEntity.getMindEnergy() < ourSpell.getEnergy())
                    {
                        victimEntity.setMindEnergy(0);
                    } else
                    {
                        victimEntity.setMindEnergy(victimEntity.getMindEnergy() - ourSpell.getEnergy());
                    }
                } else
                {
                    // Give energy
                    victimEntity.setMindEnergy(victimEntity.getMindEnergy() + ourSpell.getEnergy());
                }
                // Remove spell.
                ourSpells.remove(creatorId);
            }
        }
    }
    
    // Drain/Boost action (mobile play only)
    synchronized public void mobileSpell(int type, int energy , String creatorId, String targetNick)
    {
        if (!ourEntities.containsKey(creatorId))
        {
            ; // There is something wrong, but what to do?
        } else
        {
            PlayerEntity creatorEntity = (PlayerEntity) ourEntities.get(creatorId);
            if (creatorEntity.getMindEnergy() >= energy)
            {
                if (ourEntities.containsKey(targetNick))
                {
                    PlayerEntity victimEntity = (PlayerEntity) ourEntities.get(targetNick);
                    creatorEntity.setMindEnergy(creatorEntity.getMindEnergy() - energy);
                
                    if (type == 0)
                    {
                        // Take energy !
                        if (victimEntity.getMindEnergy() < energy)
                        {
                            victimEntity.setMindEnergy(0);
                        } else
                        {
                            victimEntity.setMindEnergy(victimEntity.getMindEnergy() - energy);
                        }
                    } else
                    {
                        // Give energy
                        victimEntity.setMindEnergy(victimEntity.getMindEnergy() + energy);
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    public GameTemp()
    {
        ourEntities  = new HashMap();
        ourMonsters = new HashMap();
        ourSpells = new HashMap();
        
        myTicker = new Tick(60,this);
		myTicker.start();
    }
    /* (non-Javadoc)
     * @see mmroutils.ITickable#onTick()
     */
    public int onTick()
    {
        // The tick object help us to update our entity each X seconds.
        // For that we implement one and we inherit of ITickable
        // The tick will ask for this method.
        
        // Here come the monster management then...
        String victimNick = null; // The target the most close
        float minDist;
        float dist;
        
        Iterator iter = ourMonsters.values().iterator();
        // First we move the monsters
        for (; iter.hasNext() ;)
        {
            Monster currentEntity = ((Monster )(iter.next()));
            
            minDist = 100;
            
            Iterator iterPl = ourEntities.values().iterator();
            for (; iterPl.hasNext() ;)
            {
                PlayerEntity currentPlayEntity = ((PlayerEntity) (iterPl.next()));
                dist =((currentPlayEntity.getX() - currentEntity.getPosX())*(currentPlayEntity.getX() - currentEntity.getPosX()) + (currentPlayEntity.getZ() - currentEntity.getPosY())*(currentPlayEntity.getZ() - currentEntity.getPosY()));
                if (dist < minDist)
                {
                    minDist = dist;
                    victimNick = currentPlayEntity.getNick();
                } 
            }
            
            PlayerEntity targetedPlayEntity = (PlayerEntity )(ourEntities.get(victimNick));
            if (minDist < 100)
            {
                float realDist = (float )Math.sqrt(minDist);
                float stepX = (targetedPlayEntity.getX() - currentEntity.getPosX())/realDist;
                float stepY = (targetedPlayEntity.getZ() - currentEntity.getPosY())/realDist;
                
                currentEntity.setPosX(currentEntity.getPosX() + stepX);
                currentEntity.setPosY(currentEntity.getPosY() + stepY);
            }
           
            Iterator iterPl2 = ourEntities.values().iterator();
            
            for (; iterPl2.hasNext() ;)
            {
                PlayerEntity currentPlayEntity = ((PlayerEntity) (iterPl2.next()));
                if ((Math.abs(currentPlayEntity.getX() - currentEntity.getPosX()) + Math.abs(currentPlayEntity.getZ() - currentEntity.getPosY())) < 2)
                {
                    victimNick = currentPlayEntity.getNick();
                    if (currentEntity.getType() != 0)
                    {
                        if (currentPlayEntity.getMindEnergy() < currentEntity.getEnergy())
                        {
                            currentPlayEntity.setMindEnergy(0);
                        } else
                        {
                            currentPlayEntity.setMindEnergy(currentPlayEntity.getMindEnergy() - currentEntity.getEnergy());
                        }
                    } else
                    {
                        // Give energy
                        currentPlayEntity.setMindEnergy(currentPlayEntity.getMindEnergy() + currentEntity.getEnergy());
                    }
                    ourMonsters.remove(currentEntity.getMonsterId());
                }
            }
            
        }
        // And we must count the time to save our data on the database sometimes...
        return 0;
    }
}
