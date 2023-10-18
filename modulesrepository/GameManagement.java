/*
 * Created on Jun 24, 2005
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

package modulesrepository;

import java.util.*;
import java.lang.Math;

import utils.ITickable;
import utils.Lock;
import utils.LogManager;
import utils.Tick;
import modulesrepository.management.*;

/**
 * The main module to handle the gameplay, manage the players, the monsters (motion, attack and help), the spells.
 * 
 * @author Alain Becam - Joakim Olsson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class GameManagement implements ITickable, IGameManagement
{
	private static final double azazelBaseX = -747.6;
	private static final double azazelBaseY = 34.7398;
	
	private static final double penemueBaseX = 667.5;
	private static final double penemueBaseY = -697.467;
	
	private static final double comfortZoneSize = 534.0;
	
	private static final float energySectDistribution = 0.1f;
	
    Tick myTicker;

    int idMonst = 0; 		// To give unique Id to the monsters

    int iSave = 0;

    Lock lockMonsts; 		// locks for monsters, entities and spells.

    Lock lockEnts;

    Lock lockSpells;

    double TouchRate = 0.2; // Chance for a monster to take/give energy
    
    int iHighSt; 			// Variables to simple express that we are still alive. From time to time, we print that we are still here!
    int iLowSt;
    int waitingTime;

    HashMap ourEntities; 	// HashMap of ourEntities[];
    SectManager sectMgr;

    public class Monster
    {
        // The monster has the goal to attack/help an entity (enemy/friend)
        // It uses his perception to target an entity (player or monster), then try to reach it
        String name;

        float posX, posY, dir;

        float xSpeed, ySpeed;

        int energy, faction; // The factions -> Azarel or Penemue, a monster can attack the rival faction or help (boost) one friend

        int type;

        Integer monsterId; // Unique id (we hope)

        // For some better behaviour
        //float targetX,targetY;
        //String targetNick;
        String creatorId;

        int timeSinceCreation;

        float minTarget; // Where is the "patrol" target on the circle

        float dirX, dirY, dist;

        float fearLevel;

        float adrenaline;

        float courage;

        int step4Nrj;

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
        public Monster(String name, float posX, float posY, int energy, int faction, int type, String creatorId)
        {
            this.name = name;
            this.posX = posX;
            this.posY = posY;

            this.dir = 0;
            this.energy = energy;
            this.faction = faction;
            if (faction == 16)
            {
                courage = 10;
            } else
            {
                courage = 15;
            }
            if (type == 1)
            {
                courage -= 5;
                // More difficult to attack to kill !
            }
            this.type = type;
            this.timeSinceCreation = 0; // Used to not follow the creator
            this.fearLevel = 0;
            this.adrenaline = 0;
            this.dirX = 2 * (float) Math.random() - 1;
            this.dirY = 2 * (float) Math.random() - 1;
            this.creatorId = creatorId;
        }

        public String giveAll()
        {
            // Return a string of all informations for the monsters
            return new String(monsterId + " : " + name + " : " + posX + " : " + posY + " : " + energy + " : " + faction + " : " + type + "/");
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

        /**
         * @return Returns the xSpeed.
         */
        public float getXSpeed()
        {
            return xSpeed;
        }

        /**
         * @param speed The xSpeed to set.
         */
        public void setXSpeed(float speed)
        {
            xSpeed = speed;
        }

        /**
         * @return Returns the ySpeed.
         */
        public float getYSpeed()
        {
            return ySpeed;
        }

        /**
         * @param speed The ySpeed to set.
         */
        public void setYSpeed(float speed)
        {
            ySpeed = speed;
        }

        /**
         * @return Returns the creatorId.
         */
        public String getCreatorId()
        {
            return creatorId;
        }

        /**
         * @param creatorId The creatorId to set.
         */
        public void setCreatorId(String creatorId)
        {
            this.creatorId = creatorId;
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
         * @return Returns the faction.
         */
        public int getFaction()
        {
            return faction;
        }

        /**
         * @param faction The faction to set.
         */
        public void setFaction(int faction)
        {
            this.faction = faction;
        }

        public boolean creatorAlert(String PlayerToCompare)
        {
            // To NOT follow the creator at the beginning
            if (creatorId != null)
            {
                if (PlayerToCompare.equals(creatorId))
                {
                    if (timeSinceCreation > 20)
                    {
                        return false;
                    } else
                    {
                        timeSinceCreation++;
                        return true;
                    }
                }
                return false;
            } else
                return false;
        }

        public boolean creatorAlertNoChg(String PlayerToCompare)
        {
            // To NOT follow the creator at the beginning
            if (creatorId != null)
            {
                if (PlayerToCompare.equals(creatorId))
                {
                    if (timeSinceCreation > 50)
                    {
                        return false;
                    } else
                    {
                        return true;
                    }
                }
                return false;
            } else
                return false;
        }

        public void patrol()
        {
            // If no target, the monster patrols
            float minX, minY;

            minX = 2 * (float) (Math.random()) - 1;
            minY = 2 * (float) (Math.random()) - 1;

            dirX = 2 * dirX + minX;
            dirY = 2 * dirY + minY;
            dist = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            dirX = 0.5f*dirX / dist;
            dirY = 0.5f*dirY / dist;

            this.setPosX(this.getPosX() + dirX);
            this.setPosY(this.getPosY() + dirY);

            step4Nrj++;

            if (step4Nrj > 200)
            {
                this.setEnergy(this.getEnergy() - 1); // After a while, die !
                if (this.getEnergy() < 0)
                    this.setEnergy(0);
                step4Nrj = 0;
            }
        }

        public void targetting(float x, float y, float realDist)
        {
            //if (fearLevel < courage)
            {
                // When targeting, try to reach the target
                float stepX = (x - this.getPosX()) / realDist;
                float stepY = (y - this.getPosY()) / realDist;
                //System.out.println("Moving " + stepX + stepY);
                this.dirX = stepX;
                this.dirY = stepY;

                if (realDist > 3)
                {
                    this.setPosX(this.getPosX() + stepX / ((float) (3 + 2 * Math.random())));
                    this.setPosY(this.getPosY() + stepY / ((float) (3 + 2 * Math.random())));
                } else
                {
                    this.setPosX(this.getPosX() - stepX / ((float) (3 + 2 * Math.random())));
                    this.setPosY(this.getPosY() - stepY / ((float) (3 + 2 * Math.random())));
                }

                //fearLevel += realDist / 2;
            }
            /*else
             {
             if (realDist != 0)
             {
             adrenaline += 4/realDist ;
             }
             if (adrenaline > 4)
             {
             fearLevel =  0;
             adrenaline = 0;
             }
             }*/
        }
    }

    HashMap ourMonsters;

    public class Spell
    {
        String idCreator;

        int type, status;

        float posX, posY, posZ, dir;

        float posCreatX, posCreatY, posCreatZ;

        int nbPlayersInvolved;

        int nbCurrentContributors;

        int placesAvailable; // Show which place is still free (just a shift)

        int energy;

        int energyAsked;

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
        public Spell(String idCreator, int type, float posX, float posY, float posZ, float posCreatX, float posCreatY,
                float posCreatZ, float dir, int nbPlayersInvolved, int energy, String victimNick)
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
            return new String(idCreator + " : " + type + " : " + posX + " : " + posY + " : " + posZ + " : " + dir + " : " + posCreatX + " : " + posCreatY + " : " + posCreatZ + " : " + status + " : " + energy + " : " + nbPlayersInvolved + " : " + placesAvailable + "/");
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

        /**
         * @return Returns the energyAsked.
         */
        public int getEnergyAsked()
        {
            return energyAsked;
        }

        /**
         * @param energyAsked The energyAsked to set.
         */
        public void setEnergyAsked(int energyAsked)
        {
            this.energyAsked = energyAsked;
        }
    }

    HashMap ourSpells; // Current spells.
    
    // Log facility
    LogManager ourLog;

    
    public PlayerEntity findByRealName(String realName)
    {
        PlayerEntity ourEntity = null;
        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext();)
        {
            PlayerEntity currentEntity = ((PlayerEntity) (iter.next()));
            //System.out.println( "Real name :" + currentEntity.getName() + " compared to " + realName);
            if (currentEntity.getName().equals(realName))
            {
                ourEntity = currentEntity;
                break;
            }
        }
        return ourEntity;
    }

    // Player distributed method
    synchronized public void createPlayerEntity(String Nick, float x, float y, float z, float dir, int status)
    {
        PlayerEntity theLittleNewOne = PlayerEntity.load(Nick);
        theLittleNewOne.setX(x);
        theLittleNewOne.setY(y);
        theLittleNewOne.setZ(z);
        theLittleNewOne.setDirection(dir);
        theLittleNewOne.setStatus(status);
        theLittleNewOne.setMindEnergy(200); // Cheating, has to change !!
        theLittleNewOne.isUpdated();
        ourEntities.put(Nick, theLittleNewOne);
    }

    synchronized public String managePos(String Nick, String RealName, float x, float y, float z, float dir,
            int status, int mindEnergy)
    {
        StringBuffer myOutput = new StringBuffer();
        PlayerEntity receivedPlayer;

        String IdObj = new String(Nick);
        
        boolean okToLog ; // Must indicate if we can log the current move or not.

        if (ourEntities.containsKey(IdObj))
        {
            receivedPlayer = ((PlayerEntity) (ourEntities.get(IdObj)));
            // If the statut is 4096, then the client initialise itself on the PC, so I want to receive informations,
            // not to send.
            
            receivedPlayer.wasManaged();
            
            if (status != 4096)
            {
                // 65536 is for Mobile initialisation
                if (status != 65536)
                {
                    // To not trust the client, if the distance is too big, we ignore the received one !
                    if (((Math.abs(receivedPlayer.getX() - x) + Math.abs(receivedPlayer.getZ() - z)) < 100) || ((status & 2) >  0) )
                    {
                        receivedPlayer.setAll(x, y, z, dir, status, 0, 0, 0, 0, mindEnergy);       
                    }
                    else
                    {
                        // To not stuck the client, we have to take care of the distant related output !
                        x = receivedPlayer.getX();
                        y = receivedPlayer.getY();
                        z = receivedPlayer.getZ();
                    }
                    receivedPlayer.setMindEnergy(((PlayerEntity) (ourEntities.get(IdObj))).getMindEnergy()+mindEnergy);
                    receivedPlayer.isUpdated(); // Still responding
                }
                else
                    ourLog.add2Log(Nick+" initialised from a mobile.");
            } else
            {
                // If the client connect itself on a PC, we both have to send him his information and to switch
                // his statut to "Player", which means he is not anymore only the aura ball
                ((PlayerEntity) (ourEntities.get(IdObj))).setStatus(1);
                x = receivedPlayer.getX();
                y = receivedPlayer.getY();
                z = receivedPlayer.getZ();
                ourLog.add2Log(Nick+" initialised from a PC.");
            }
            okToLog =  ((PlayerEntity) (ourEntities.get(IdObj))).ok4TheLog();
        } else
        {
            // Here is a new one !!!!
        	
            // From PC ?
            if (status == 4096)
            {
                status = 1; // Then his status is "normal"
                ourLog.add2Log("New player " +Nick+" initialised from a Pc.");
            } else
            {
                // Might be an aura ball...
                if (status == 65536)
                {
                    status = 2;
                    ourLog.add2Log("New player " +Nick+" initialised from a Mobile.");
                }
            }

            PlayerEntity theLittleNewOne = PlayerEntity.load(Nick);
            theLittleNewOne.setX(x);
            theLittleNewOne.setY(y);
            theLittleNewOne.setZ(z);
            theLittleNewOne.setDirection(dir);
            theLittleNewOne.setName(RealName);
            theLittleNewOne.setStatus(status);
            theLittleNewOne.isUpdated();
            theLittleNewOne.wasManaged();

            //theLittleNewOne.setMindEnergy(200);
            ourEntities.put(IdObj, theLittleNewOne);


            okToLog = true;
        }
        // Anyway, we send back the result.
        // It might be good to filter the result considering the distance. Here it is also... The bad but fast
        // square distance...
        if (okToLog)
        {
            if ((status & 1) == 0)
            {
                if ((status & 16) == 0)
                {
                    ourLog.add2Log("From mobile: " + Nick + " - Penemue - has moved to " + x + ", " + z + ".");
                } else
                {
                    ourLog.add2Log("From mobile: " + Nick + " - Azazel - has moved to " + x + ", " + z + ".");
                }
            } else
            {
                if ((status & 16) == 0)
                {
                    ourLog.add2Log("From PC: " + Nick + " - Penemue - has moved to " + x + ", " + z + ".");
                } else
                {
                    ourLog.add2Log("From PC: " + Nick + " - Azazel - has moved to " + x + ", " + z + ".");
                }
            }
        }

        // Return scan of nearby players 
        
        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext();)
        {
            PlayerEntity currentEntity = ((PlayerEntity) (iter.next()));
            if ((Math.abs(currentEntity.getX() - x) + Math.abs(currentEntity.getY() - y) + Math.abs(currentEntity.getZ() - z)) < 1000)
            {
                myOutput.append(currentEntity.getAll());
            }
        }

        myOutput.append("#");
        return (new String(myOutput));
    }

    synchronized public String giveEntities2D(float x, float y, float z, float distance)
    {
        StringBuffer myOutput = new StringBuffer();

        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext();)
        {
            PlayerEntity currentEntity = ((PlayerEntity) (iter.next()));
            if ((Math.abs(currentEntity.getX() - x) + Math.abs(currentEntity.getZ() - z)) < distance)
            {
                myOutput.append(currentEntity.getAll2D());
            }
        }
        myOutput.append("#");

        return (new String(myOutput));
    }
    
    synchronized private void givePlayerMindenergy(PlayerEntity who, float amount) {
    	
    	who.increaseMindEnergy(amount);
    	
    	if ((amount > 0) && (who.getSect() != null)) {
    		
    		// Player is given energy - distribute some of it to the superiors in
    		// player sect (if any)
    		
    		float energyShare = amount * energySectDistribution;
    		
    		Iterator i = who.getSect().getSuperiors().iterator();
    		
    		while (i.hasNext()) {
    			
    			String receiverName = (String)i.next();
    			
    			if (ourEntities.containsKey(receiverName)) {
    				
    				// Sect superior is online and will get its share of the energy
    				
    				PlayerEntity receiver = (PlayerEntity)ourEntities.get(receiverName);
    				
    				receiver.setMindEnergy(receiver.getMindEnergy() + energyShare);
    			}
    			
    		}
    		
    	}
    }

    synchronized public String givePlayer2D(String nick)
    {
        StringBuffer myOutput = new StringBuffer();

        PlayerEntity ent = (PlayerEntity) ourEntities.get(nick);
        if (ent != null)
        {
            myOutput.append(ent.getAll2D());
        }

        myOutput.append("#");
        return (new String(myOutput));
    }
       
    // Monsters distributed methods
    synchronized public void spawnMonsters(int type, float posX, float posY, float posZ, int faction, int number,
            int energy)
    {
        // One type of monster (rabbit, snake, ...) can do only ONE thing (take energy, give energy).
        // Type 0-> good, 1-> bad. Faction -> 16 Azarel, 32 Penemue

        //System.out.println(number);
        String name;
        String creatorId = null;

        if (faction == 16)
        {
            if (type == 0)
            {
                name = new String("Flute");
            } else
            {
                name = new String("Diamond");
            }
        } else
        {
            if (type == 0)
            {
                name = new String("LivingTree");
            } else
            {
                name = new String("Owl");
            }
        }
        // Find the creator
        float minDist = 4000;
        Iterator iterPl = ourEntities.values().iterator();
        for (; iterPl.hasNext();)
        {
            PlayerEntity currentPlayEntity = ((PlayerEntity) (iterPl.next()));
            float dist = ((currentPlayEntity.getX() - posX) * (currentPlayEntity.getX() - posX) + (currentPlayEntity.getZ() - posY) * (currentPlayEntity.getZ() - posY));

            if ((faction & currentPlayEntity.getStatus()) > 0)
            {
                if (dist < minDist)
                {
                    minDist = dist;
                    creatorId = currentPlayEntity.getNick();
                    //System.out.println("Moving " + dist);
                }
            }
        }

        if (minDist == 4000)
        {
            //System.out.println("No creator !\n");
            creatorId = null;
        }

        if (faction == 16)
            ourLog.add2Log(creatorId+" has spawned "+number+" "+name+" in the faction Azazel, Id "+idMonst+" to "+(idMonst+number));
        else
            ourLog.add2Log(creatorId+" has spawned "+number+" "+name+" in the faction Penemue, Id "+idMonst+" to "+(idMonst+number));
        for (int i = 0; i < number; i++)
        {
            //System.out.println("un monstre");
            Monster newMonster = new Monster(name, posX + 4 * ((float) Math.random()) - 2, posY + 4 * ((float) Math.random()) - 2, energy, faction, type, creatorId); // Create number monsters of type type at the start position given owned by faction (so attacking or helping following that)
            Integer idMonster = new Integer(idMonst++);
            if (idMonst < 0)
                idMonst = 0; // We do not allow negative Id. (might be use for other goals, for example signifying there is no monsters.
            newMonster.setMonsterId(idMonster);
            ourMonsters.put(idMonster, newMonster);
        }
    }

    synchronized public String updateMonsters(float posX, float posY)
    {
        // return every informations about monsters "around" the given position, then the client can print
        // the monsters (this method is called to render monsters)
        StringBuffer myOutput = new StringBuffer("");

        // Give at least
        Iterator iter = ourMonsters.values().iterator();
        for (; iter.hasNext();)
        {
            Monster currentEntity = ((Monster) (iter.next()));
            if ((Math.abs(currentEntity.getPosX() - posX) + Math.abs(currentEntity.getPosY() - posY)) < 500)
            {
                myOutput.append(currentEntity.giveAll());
            }
        }
        
        myOutput.append("#");
        //System.out.println("Update Monster sends : "+myOutput);
        return (new String(myOutput));
    }

    // End monsters distributed methods

    // Sect distributed methods

    synchronized public void giveFaith(String IdPlayer, String IdGuru)
    {
    	PlayerEntity from = (PlayerEntity)ourEntities.get(IdPlayer);
    	PlayerEntity to = (PlayerEntity)ourEntities.get(IdGuru);
    	
    	if (from != null && to != null)
    		sectMgr.addToSect(from, to);
        // If the IdGuru is not already guru, raise it at guru (level 0), and add the player as a member (level 1)
        // Or add a member to the guru list, and take the level of the guru+1 for the player
    }

    synchronized public int kickOutMemberOut(String IdPlayer, String IdGuru)
    {
        // As the name said, verify first if the guru can (player member of guru followers)
    	
    	/* NOTE: IdGuru is the name of anyone above Player in rank.
    	 * If the guru has to be the PROPHET - just check against player.getSectProphet()
    	 */
    	
    	PlayerEntity player = null;
    	PlayerEntity guru = null;
    	if (ourEntities.containsKey(IdPlayer))
    		player = (PlayerEntity)ourEntities.get(IdPlayer);
    	if (ourEntities.containsKey(IdGuru))
    		guru = (PlayerEntity)ourEntities.get(IdGuru);
    	
    	sectMgr.disconnectSubsect(player, guru);
    	
        return 0;
    }

    synchronized public void breakFaith(String IdPlayer)
    {
        // If the player is in a sect, push it away. If he has follower, he keeps them, a new sect is done !
    	PlayerEntity player = (PlayerEntity)ourEntities.get(IdPlayer);
    	
    	if (player != null)
    		sectMgr.breakFaith(player);
    }
    
    
    
    synchronized public String getOnlineSectmembers2D(String IdPlayer) {
    	
    	StringBuffer result = new StringBuffer();
    	PlayerEntity plr = null;
    	
    	if (ourEntities.containsKey(IdPlayer))
    		plr = (PlayerEntity)ourEntities.get(IdPlayer);
    	
    	if (plr != null) {

    		// Get player sect members
    		Collection sect = sectMgr.getEntireSect(plr);
    		
    		// Iterate through sect nodes and scan for online players
    		
    		Iterator i = sect.iterator();
    		
    		while (i.hasNext()) {
    			SectNode sectMember = (SectNode)i.next();
    			PlayerEntity memberInfo = sectMember.getPlayerRecord();
    			
    			if (memberInfo != null) {
    				
    				if (memberInfo.isOnline()) {
    					result.append(memberInfo.getAll2D());
    				}
    				
    			}
    		}
    		
    	}
    	
    	result.append("#");
    	return new String(result);
    	
    }

    // End sect distributed methods

    // Magic distributed methods

    synchronized public void ask4Spell(String creatorId, int targetType, String victimRealName, String targetNick,
            int spellType, float posX, float posY, float posZ, float posCreatX, float posCreatY, float posCreatZ,
            int nbParticipants, int energyGiven)
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
                    System.out.println("Victim : " + victimNick);
                } else if (targetType == 16) // By real name
                {
                    if (victimRealName != null)
                    {
                        PlayerEntity victim = findByRealName(victimRealName);
                        if (victim == null)
                            return; // Nothing to do...
                        victimNick = victim.getNick();
                        //System.out.println("Real Name given - Victim : "+victimNick);
                    } else
                        return; // Nothing to do
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
                    //System.out.println("Creating a spell");
                    // If the creator has already done a spell, it will cancel it !
                    if (ourSpells.containsKey(creatorId))
                        this.cancelSpell(creatorId, creatorId);
                    
                    if (nbParticipants > 1)
                        ourLog.add2Log(creatorId + " has created a spell with "+ energyGiven + " energy and "+nbParticipants+" contributors asked.");
                    else
                        ourLog.add2Log(creatorId+ " has created a mono-player spell.");

                    Spell theNewSpell = new Spell(creatorId, spellType, posX, posY, posZ, posCreatX, posCreatY, posCreatZ, 0, nbParticipants, energyGiven, victimNick); // The
                    theNewSpell.setEnergyAsked(energyGiven);

                    theNewSpell.setPlacesAvailable(1); // First place occuped by the creator

                    if (nbParticipants == 1)
                    {
                        // So the spell is confirmed !
                        System.out.println("Confirmed !");
                        theNewSpell.setStatus(1); // On the way !
                    }

                    //System.out.println("Will remove"+ energyGiven +" from "+ creatorEntity.getMindEnergy());
                    // Then remove the energy from the creator
                    creatorEntity.decreaseMindEnergy(energyGiven);
                    //System.out.println("Removed from "+ creatorEntity.getMindEnergy());
                    if (creatorEntity.getMindEnergy() == 0)
                    {
                        creatorEntity.setStatus(2); // Aura ball only...
                    }
                   
                    // And add the spell
                    ourSpells.put(creatorId, theNewSpell);
     
                }
                else
                {
                    ourLog.add2Log(creatorId + " has tried to create a spell but the target was not found.");
                }
            }
        }
    }

    synchronized public String updateMagic(float posX, float posY, float posZ)
    {
        // Return the informations concerning the spells in the zone around the given position
        // Used to draw spell before (for the creation of it) and during execution
        // The first phase (selection of the spell) is only on the client
        StringBuffer myOutput = new StringBuffer();

        Iterator iter = ourSpells.values().iterator();
        for (; iter.hasNext();)
        {
            Spell currentSpell = ((Spell) (iter.next()));
            if ((Math.abs(currentSpell.getPosX() - posX) + Math.abs(currentSpell.getPosY() - posY) + Math.abs(currentSpell.getPosZ() - posZ)) < 1000)
            {
                myOutput.append(currentSpell.getAll());
            }
        }

        myOutput.append("#");
        //System.out.println("To send: "+myOutput);

        return new String(myOutput);
    }

    synchronized public void contributeToSpell(String creatorId, String playerId, int placeWanted)
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
            if (contributorEntity.getMindEnergy() >= ourSpell.getEnergyAsked())
            {
                // Is the triangle still available
                if ((ourSpell.getPlacesAvailable() & placeWanted) == 0)
                {
                    // Then remove the energy from the creator
                    contributorEntity.decreaseMindEnergy(ourSpell.getEnergyAsked());

                    ourSpell.setEnergy(ourSpell.getEnergy() + ourSpell.getEnergyAsked());
                    ourSpell.setNbCurrentContributors(ourSpell.getNbCurrentContributors() + 1);
                    ourSpell.addContributor(playerId);
                    ourSpell.setPlacesAvailable(ourSpell.getPlacesAvailable() | placeWanted);
                    ourLog.add2Log(playerId+ " has contributed to the spell created by "+creatorId+".");
                    if (ourSpell.getNbCurrentContributors() == ourSpell.getNbPlayersInvolved())
                    {
                        // So the spell is confirmed !
                        ourSpell.setStatus(1); // On the way !
                        ourLog.add2Log("The spell created by "+creatorId+" is confirmed.");
                    }
                }
                else
                {
                    ourLog.add2Log(contributorEntity.getNick()+" has tried to contribute to a non-available triangle.");
                }
            }
            else
            {
                ourLog.add2Log(contributorEntity.getNick()+" has tried to contribute to spell without the needed energy.");
            }
        }
    }

    synchronized public void cancelSpell(String creatorId, String playerId)
    {
        // One player break the spell. The mindEnergy given is lost, the spell also
        if (ourSpells.containsKey(creatorId))
        {
            Spell ourSpell = (Spell) ourSpells.get(creatorId);
            if ((ourSpell.getContributors().containsKey(playerId)) || (playerId.equals(creatorId)))
            {
                // Then the asker is truly a caster (then it's still a little bit unsure, it's possible to cheat),
                // We cancel the spell.
                ourSpells.remove(creatorId);
                ourLog.add2Log(playerId+" has canceled the spell created by "+creatorId+".");
            }
        }
        else
        {
            ourLog.add2Log(playerId+" has tried to cancele a spell created by "+creatorId+", but no spell exist with this creator.");
        }
    }

    synchronized public void confirmSpell(String creatorId)
    {
        // The creator confirm the spell, it becomes active.
       
        if (ourSpells.containsKey(creatorId))
        {
            Spell ourSpell = (Spell) ourSpells.get(creatorId);
            ourSpell.setStatus(16); // Running !
            PlayerEntity theCreator = (PlayerEntity )ourEntities.get(creatorId);
            
            PlayerEntity victimEntity = (PlayerEntity) ourEntities.get(ourSpell.getVictimNick());
            
            // The creator is very happy, he will receive a lot of Xp point !
            int newLimit = theCreator.getXp(ourSpell.getEnergy()/3);
            if (newLimit != 0)
            {
                // New level !
                ourLog.add2Log(creatorId + " has raised one level ("+theCreator.getLevel()+"), next level at "+newLimit+" ("+theCreator.getXp()+").");
            }

            System.out.println("1- Will remove " + ourSpell.getEnergy() + " from " + victimEntity.getNick());
            System.out.println("1- Nb asked" + ourSpell.getNbPlayersInvolved() + " and nb involved " + ourSpell.getNbCurrentContributors());

            // Is everyone here ?
            //if ((victimEntity.getStatus() & 1) > 0)
            {
                // If NOT as an auraball

                if ((ourSpell.getNbPlayersInvolved() - ourSpell.getNbCurrentContributors()) == 0)
                {
                    if (ourSpell.getType() == 0)
                    {
                        // Take energy !
                        //System.out.println("2 - Will remove " + ourSpell.getEnergy() + " from " + victimEntity.getNick());
                        ourLog.add2Log("The vampiric spell created by "+ creatorId + " removes " + ourSpell.getEnergy() + " from " + victimEntity.getNick()); 
                        victimEntity.decreaseMindEnergy(ourSpell.getEnergy());                      
                    } else
                    {
                        //System.out.println("2 + Will remove " + ourSpell.getEnergy() + " from " + victimEntity.getNick());
                        ourLog.add2Log("The help spell created by "+ creatorId + " adds " + ourSpell.getEnergy() + " from " + victimEntity.getNick());
                        // Give energy
                        givePlayerMindenergy(victimEntity, ourSpell.getEnergy());
                    }
                    // Remove spell.
                    ourSpells.remove(creatorId);
                }
            }         
        }
    }

    // Drain/Boost action (mobile play only)
    synchronized public void mobileSpell(int type, int energy, String creatorId, String targetNick)
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
                    PlayerEntity theCreator = (PlayerEntity )ourEntities.get(creatorId);
                    PlayerEntity victimEntity = (PlayerEntity) ourEntities.get(targetNick);
                    creatorEntity.decreaseMindEnergy(energy);
                    
//                  The creator is very happy, he will receive a lot of Xp point !
                    int newLimit = theCreator.getXp(energy/3);
                    if (newLimit != 0)
                    {
                        // New level !
                        ourLog.add2Log(creatorId + " has raised one level ("+theCreator.getLevel()+"), next level at "+newLimit+" ("+theCreator.getXp()+").");
                    }

                    if (type == 0)
                    {
                        // Take energy !
                        ourLog.add2Log("From mobile: The vampiric spell created by "+ creatorId + " removes " + energy + " from " + targetNick);
                       
                        victimEntity.decreaseMindEnergy(energy);
                    } else
                    {
                        ourLog.add2Log("From mobile: The help spell created by "+ creatorId + " adds " + energy + " from " + targetNick);
                        // Give energy
                        givePlayerMindenergy(victimEntity, energy);
                    }
                }
            }
        }
    }

    public GameManagement()
    {
    	/* Load sect structures before players so that player instances initialize
    	 * themselves properly to the sect nodes! (see PlayerEntity)
    	 */
        sectMgr = SectManager.getInstance();
        sectMgr.loadSects();

        ourEntities = new HashMap();
        Iterator i = PlayerEntity.loadAll().iterator();
        PlayerEntity ent;
        while (i.hasNext())
        {
            ent = (PlayerEntity) i.next();
            ent.setStatus((ent.getStatus() & (~1)) | 2);
            ourEntities.put(ent.getNick(), ent);
        }
        ourMonsters = new HashMap();
        ourSpells = new HashMap();
        lockMonsts = new Lock(); // locks for monsters, entities and spells.
        lockEnts = new Lock();
        lockSpells = new Lock();
        
        iHighSt = 0; // Variables to simple express that we are still alive. From time to time, we print that we are still here!
        iLowSt = 0;
        waitingTime = 0;
        
        // Some monsters
        //this.spawnMonsters(1, 10, 10, 10, 16, 100,20);
        //this.spawnMonsters(0, 10, 10, 10, 32, 100,20);
        
        kickOutMemberOut("Cesar","Adam");
        sectMgr.printAll();
        
        myTicker = new Tick(60, this);
        myTicker.start();
        ourLog = new LogManager();
        
        ourLog.initLog();
    }

    
    private double hypot(double side1, double side2) {
    	return Math.sqrt(side1 * side1 + side2 * side2);
    }
    
    
	public synchronized int onTick()
    {
        // The tick object help us to update our entity each X seconds.
        // For that we implement one and we inherit of ITickable
        // The tick will ask for this method.

        // Here come the monster management then...
        String victimNick = null; // The target the most close
        int monsterVictim = -1;
        ArrayList monsterToRemove = new ArrayList();
        float minDist;
        float realDist=100;
        float dist;

        float targetX = 0;
        float targetY = 0; // position of the target

        
        
        //if (lockMonsts.isAvailable(false) && lockEnts.isAvailable(false))
        //if (lockMonsts.isAvailable(false))
        {
            //System.out.println("Monsters try to move");
            Iterator iter = ourMonsters.values().iterator();
            // First we move the monsters
            for (; iter.hasNext();)
            {
                Monster currentEntity = ((Monster) (iter.next()));

                if (currentEntity.getEnergy() <= 0)
                {
                    if (!monsterToRemove.contains(currentEntity.getMonsterId()))
                    {
                        monsterToRemove.add(currentEntity.getMonsterId());
                        ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ " created by "+ currentEntity.getCreatorId()+" is dead.");
                    }
                } else
                {
                    monsterVictim = -1; // No monster as victim now

                    minDist = 600;

                    Iterator iterPl = ourEntities.values().iterator();
                    for (; iterPl.hasNext();)
                    {
                        PlayerEntity currentPlayEntity = ((PlayerEntity) (iterPl.next()));
                        dist = ((currentPlayEntity.getX() - currentEntity.getPosX()) * (currentPlayEntity.getX() - currentEntity.getPosX()) + (currentPlayEntity.getZ() - currentEntity.getPosY()) * (currentPlayEntity.getZ() - currentEntity.getPosY()));

                        if ((((currentEntity.faction & currentPlayEntity.getStatus()) > 0) && (currentEntity.getType() == 0)) || (((currentEntity.faction & currentPlayEntity.getStatus()) == 0) && (currentEntity.getType() != 0) && (currentPlayEntity.getMindEnergy() > 0) ))
                        {
                            if (dist < minDist)
                            {
                                if (!currentEntity.creatorAlert(currentPlayEntity.getNick()))
                                {
                                    minDist = dist;
                                    victimNick = currentPlayEntity.getNick();
                                    targetX = currentPlayEntity.getX();
                                    targetY = currentPlayEntity.getZ();
                                }
                            }
                        }
                    }

                    Iterator iterMons = ourMonsters.values().iterator();
                    for (; iterMons.hasNext();)
                    {
                        Monster currentMonster = ((Monster) (iterMons.next()));
                        dist = ((currentMonster.getPosX() - currentEntity.getPosX()) * (currentMonster.getPosX() - currentEntity.getPosX()) + (currentMonster.getPosY() - currentEntity.getPosY()) * (currentMonster.getPosY() - currentEntity.getPosY()));
                        
                        if ((((currentEntity.faction & currentMonster.getFaction()) > 0) && (currentEntity.getType() == 0) && (currentMonster.getType() != 0)) || (((currentEntity.faction & currentMonster.getFaction()) == 0) && (currentEntity.getType() != 0)) && (currentEntity.getMonsterId() != currentMonster.getMonsterId()))
                        {
                            if (dist < minDist)
                            {

                                minDist = dist;
                                monsterVictim = currentMonster.getMonsterId().intValue();
                                targetX = currentMonster.getPosX();
                                targetY = currentMonster.getPosY();
                            }
                        }
                    }

                    /*
                     * PlayerEntity targetedPlayEntity; if (victimNick != null)
                     * targetedPlayEntity = (PlayerEntity)
                     * (ourEntities.get(victimNick));
                     */

                    if (minDist < 600)
                    {
                        realDist = (float) Math.sqrt(minDist);

                        currentEntity.targetting(targetX, targetY, realDist);
                    } else
                        currentEntity.patrol(); // No target, we patrol

                    PlayerEntity currentPlayEntity;

                    if (minDist < 600)
                    {
                        if (monsterVictim == -1)
                        {
                            // A player is targeted
                            currentPlayEntity = (PlayerEntity) (ourEntities.get(victimNick));

                            if (realDist <= 3)
                            {
                                if ((((currentEntity.faction & currentPlayEntity.getStatus()) == 0) && (currentEntity.getType() != 0)))
                                {
                                    //victimNick = currentPlayEntity.getNick();

                                    {
                                        // Attacking ! Will lost energy, then
                                        // die
                                        // when
                                        // no energy left
                                        if (Math.random() > TouchRate)
                                        {
                                            if (currentEntity.getEnergy() >= 2)
                                            {
                                                PlayerEntity theCreator = (PlayerEntity) ourEntities.get(currentEntity.getCreatorId());
                                                
                                                // The creator is very happy, he will receive a lot of Xp point !
                                                int newLimit = theCreator.getXp(2);
                                                if (newLimit != 0)
                                                {
                                                    // New level !
                                                    ourLog.add2Log(theCreator.getNick() + " has raised one level ("+theCreator.getLevel()+"), next level at "+newLimit+" ("+theCreator.getXp()+").");
                                                }
                                                if (currentPlayEntity.getMindEnergy() < 2)
                                                {
                                                    // The attacking monster
                                                    // lost
                                                    // the energy of the
                                                    // attacked
                                                    // one
                                                    ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has killed " + currentPlayEntity.getNick()+"("+currentPlayEntity.getMindEnergy()+").");
                                                    
                                                    currentEntity.setEnergy(currentEntity.getEnergy() - (int)(currentPlayEntity.getMindEnergy()));
                                                    currentPlayEntity.setMindEnergy(0);

                                                } else
                                                {
                                                    ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has attacked " + currentPlayEntity.getNick()+"("+currentPlayEntity.getMindEnergy()+"). Hit -2.");
                                                    currentPlayEntity.decreaseMindEnergy(2);
                                                    currentEntity.setEnergy(currentEntity.getEnergy() - 2);
                                                }
                                            } else
                                            {
                                                ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has attacked " + currentPlayEntity.getNick()+"("+currentPlayEntity.getMindEnergy()+"). Hit - "+currentEntity.getEnergy()+".");
                                                if (currentPlayEntity.getMindEnergy() >= 2)
                                                {
                                                    if (!monsterToRemove.contains(currentEntity.getMonsterId()))
                                                    {
                                                        ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId() + "("+currentEntity.getEnergy()+") created by " + currentEntity.getCreatorId() + " is dead after attack.");
                                                        monsterToRemove.add(currentEntity.getMonsterId());
                                                    }
                                                }
                                                currentPlayEntity.decreaseMindEnergy(currentEntity.getEnergy());
                                            }
                                            currentEntity.setEnergy(currentEntity.getEnergy() - 2);
                                        } else
                                        {
                                            ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has attacked and missed " + currentPlayEntity.getNick()+"("+currentPlayEntity.getMindEnergy()+"). 4 ME Lost.");
                                            currentEntity.setEnergy(currentEntity.getEnergy() - 4); // Double
                                            // damage
                                            // if
                                            // missed

                                        }
                                        if (currentEntity.getEnergy() <= 0)
                                        {
                                            if (!monsterToRemove.contains(currentEntity.getMonsterId()))
                                            {
                                                monsterToRemove.add(currentEntity.getMonsterId());
                                                ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" is dead after attack.");
                                            }
                                        }
                                    }
                                } else
                                {
                                    PlayerEntity theCreator = (PlayerEntity) ourEntities.get(currentEntity.getCreatorId());
                                    
                                    // The creator is very happy, he will receive a lot of Xp point !
                                    int newLimit = theCreator.getXp(2);
                                    if (newLimit != 0)
                                    {
                                        // New level !
                                        ourLog.add2Log(theCreator.getNick() + " has raised one level ("+theCreator.getLevel()+"), next level at "+newLimit+" ("+theCreator.getXp()+").");
                                    }
                                    // Give energy
                                    ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has given its energy ("+currentEntity.getEnergy()+") to " + currentPlayEntity.getNick()+"("+currentPlayEntity.getMindEnergy()+").");
                                    System.out.println(currentPlayEntity.getNick() + "+ " + currentEntity.getEnergy());
                                    
                                    givePlayerMindenergy(currentPlayEntity, currentEntity.getEnergy());
                                    monsterToRemove.add(currentEntity.getMonsterId());
                                }

                                //ourMonsters.remove(currentEntity.getMonsterId());
                            }

                        }

                        else
                        {
                            // A monster is targeted
                            Monster targetedMonster = (Monster) (ourMonsters.get(new Integer(monsterVictim)));

                            if (realDist <= 3)
                            {
                                if ((((currentEntity.faction & targetedMonster.getFaction()) == 0)))
                                {

                                    {
                                        // Attacking ! Will lost energy, then
                                        // die
                                        // when
                                        // no energy left
                                        if (Math.random() > TouchRate)
                                        {
                                            PlayerEntity theCreator = (PlayerEntity) ourEntities.get(currentEntity.getCreatorId());
                                            
                                            // The creator is very happy, he will receive a lot of Xp point !
                                            int newLimit = theCreator.getXp(2);
                                            if (newLimit != 0)
                                            {
                                                // New level !
                                                ourLog.add2Log(theCreator.getNick() + " has raised one level ("+theCreator.getLevel()+"), next level at "+newLimit+" ("+theCreator.getXp()+").");
                                            }
                                            
                                            if (currentEntity.getEnergy() > 2)
                                            {
                                                if (targetedMonster.getEnergy() <= 2)
                                                {
                                                    // The attacking monster
                                                    // lost
                                                    // the energy of the
                                                    // attacked
                                                    // one
                                                    ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has killed the weak monster " + targetedMonster.getName()+" "+targetedMonster.getMonsterId()+"("+targetedMonster.getEnergy()+").");
                                                    currentEntity.setEnergy(currentEntity.getEnergy() - targetedMonster.getEnergy());
                                                    targetedMonster.setEnergy(0);
                                                    // The attacked monster will
                                                    // disappear
                                                    if (!monsterToRemove.contains(targetedMonster.getMonsterId()))
                                                        monsterToRemove.add(targetedMonster.getMonsterId());
                                                } else
                                                {
                                                    ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has attacked the monster " + targetedMonster.getName()+" "+targetedMonster.getMonsterId()+"("+targetedMonster.getEnergy()+"). Hit - 2.");
                                                    targetedMonster.setEnergy(targetedMonster.getEnergy() - 2);
                                                    currentEntity.setEnergy(currentEntity.getEnergy() - 2);
                                                }
                                            } else
                                            {
                                                ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has attacked the monster " + targetedMonster.getName()+" "+targetedMonster.getMonsterId()+"("+targetedMonster.getEnergy()+"). Hit - "+currentEntity.getEnergy()+".");
                                                if (targetedMonster.getEnergy() >= 2)
                                                {
                                                    if (!monsterToRemove.contains(currentEntity.getMonsterId()))
                                                    {
                                                        ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" is dead after attack.");
                                                        monsterToRemove.add(currentEntity.getMonsterId());
                                                    }
                                                }
                                                targetedMonster.setEnergy(targetedMonster.getEnergy() - currentEntity.getEnergy());
                                            }
                                            currentEntity.setEnergy(currentEntity.getEnergy() - 2);
                                        } else
                                        {
                                            currentEntity.setEnergy(currentEntity.getEnergy() - 4); // Double
                                            ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ "("+currentEntity.getEnergy()+") created by "+ currentEntity.getCreatorId()+" has attacked and missed the monster " + targetedMonster.getName()+" "+targetedMonster.getMonsterId()+"("+targetedMonster.getEnergy()+"). 4 ME Lost.");
                                            // damage
                                            // if
                                            // missed

                                        }
                                        if (currentEntity.getEnergy() <= 0)
                                        {
                                            if (!monsterToRemove.contains(currentEntity.getMonsterId()))
                                            {
                                                monsterToRemove.add(currentEntity.getMonsterId());
                                                ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ " created by "+ currentEntity.getCreatorId()+" is dead after attack.");
                                            }
                                        }
                                    }
                                } else
                                {
                                    PlayerEntity theCreator = (PlayerEntity) ourEntities.get(currentEntity.getCreatorId());
                                    
                                    // The creator is very happy, he will receive a lot of Xp point !
                                    int newLimit = theCreator.getXp(2);
                                    if (newLimit != 0)
                                    {
                                        // New level !
                                        ourLog.add2Log(theCreator.getNick() + " has raised one level ("+theCreator.getLevel()+"), next level at "+newLimit+" ("+theCreator.getXp()+").");
                                    }
                                    // Give energy
                                    ourLog.add2Log(currentEntity.getName() + " " + currentEntity.getMonsterId()+ " created by "+ currentEntity.getCreatorId()+" has given its energy ("+currentEntity.getEnergy()+") to the monster " + targetedMonster.getName()+" "+targetedMonster.getMonsterId()+"("+targetedMonster.getEnergy()+").");
                                    //System.out.println(targetedMonster.getMonsterId() + "+ " + currentEntity.getEnergy());
                                    targetedMonster.setEnergy(targetedMonster.getEnergy() + currentEntity.getEnergy());
                                    monsterToRemove.add(currentEntity.getMonsterId());
                                }
                                //ourMonsters.remove(currentEntity.getMonsterId());
                            }

                        }
                    }
                }
            }

            //lockEnts.isAvailable(true);
            // Then we remove the dead monsters
            Iterator iterMonst2Rm = monsterToRemove.iterator();
            for (; iterMonst2Rm.hasNext();)
            {
                Integer IdMonsterToRemove = ((Integer) (iterMonst2Rm.next()));
                ourMonsters.remove(IdMonsterToRemove);
            }
            //lockMonsts.isAvailable(true);
        }
        
        // And we must count the time to save our data on the database sometimes...
        /*iSave++;
         if (iSave > 36000)
         {
         if (lockEnts.isAvailable(false))
         {
         Iterator iterPl2 = ourEntities.values().iterator();

         for (; iterPl2.hasNext();)
         {
         PlayerEntity currentPlayEntity = ((PlayerEntity) (iterPl2.next()));
         currentPlayEntity.store();
         }     
         lockEnts.isAvailable(true);
         iSave = 0;
         }
         }*/

        // Clean the current entities to check if the status is ok.
        {
            Iterator iterPl2 = ourEntities.values().iterator();

            for (; iterPl2.hasNext();)
            {
                PlayerEntity currentPlayEntity = ((PlayerEntity) (iterPl2.next()));
                if ( currentPlayEntity.getMindEnergy() == 0)
                    currentPlayEntity.setStatus((currentPlayEntity.getStatus() & ~1) | 2);
                currentPlayEntity.saveAttempt();
                currentPlayEntity.nonResponding(); // The method check IF the player is not responding (combined with isUpadted)
                currentPlayEntity.checkEnergy(); // Avoid to cheat !
               
                // Confort zone
                if (waitingTime > 100)
                {
                	double penemueDist = 
                		hypot(penemueBaseX - currentPlayEntity.getX(),
                			   penemueBaseY - currentPlayEntity.getZ());
                	double azazelDist = 
                		hypot(azazelBaseX - currentPlayEntity.getX(),
                			   azazelBaseY - currentPlayEntity.getZ());

                	if ((currentPlayEntity.getStatus() & 16) == 0)
                    {
                        // Penemue
                    	
                        if (azazelDist < comfortZoneSize)
                        {
                            if (currentPlayEntity.getMindEnergy() > 0)
                            {
                                currentPlayEntity.decreaseMindEnergy(1);
                                ourLog.add2Log(currentPlayEntity.getNick() + " is in Azazel territory, loosing energy (" + currentPlayEntity.getMindEnergy() + ").");
                            }
                        }
                        if (penemueDist < comfortZoneSize)
                        {
                            if (currentPlayEntity.getMindEnergy() < currentPlayEntity.getEnergymax())
                            {
                                givePlayerMindenergy(currentPlayEntity, 1);
                                ourLog.add2Log(currentPlayEntity.getNick()+" is in Penemue territory, gaining energy ("+currentPlayEntity.getMindEnergy()+").");
                            }
                        }
                        
                    } else
                    {
                    	// Azazel
                    	
                        if (penemueDist < comfortZoneSize)
                        {
                            if (currentPlayEntity.getMindEnergy() > 0)
                            {
                                currentPlayEntity.decreaseMindEnergy(1);
                                ourLog.add2Log(currentPlayEntity.getNick()+" is in Penemue territory, loosing energy ("+currentPlayEntity.getMindEnergy()+").");
                            }
                        }
                        if (azazelDist < comfortZoneSize)
                        {
                            if (currentPlayEntity.getMindEnergy() < currentPlayEntity.getEnergymax())
                            {
                                givePlayerMindenergy(currentPlayEntity, 1);
                                ourLog.add2Log(currentPlayEntity.getNick() + " is in Azazel territory, gaining energy (" + currentPlayEntity.getMindEnergy() + ").");
                            }
                        }
                        
                    }
                }
            }
        }
        
        // Save sect info every now and then
        
        /* Note: This is done in one big swoop at the moment. In order to distribute
         * database calls over time better, every playerEntity could save its specific
         * row to the SectRelationTable independently instead.
         */
        sectMgr.saveToDBAttempt(60000);
        
        
        
        if (waitingTime > 100)
        {
            waitingTime = 0;
            System.out.println("Game management still running - "+iHighSt+" - "+iLowSt+".\n");
            iLowSt++;
            if (iLowSt < 0) // Max int limit reached (Wow !)
                iHighSt++; //Very sure we don't need to go further...
        }
        waitingTime++;
        return 0;
    }

    public int dummyWait()
    {
        while (!lockEnts.isAvailable(false))
        {
            try
            {
                Thread.sleep(10);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            System.out.println("Here...");
        }
        try
        {
            Thread.sleep(10000);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        lockEnts.isAvailable(true);

        return 10;
    }
    
    

}