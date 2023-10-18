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
import utils.LogManager;
import utils.Tick;

/**
 * The main module to handle the gameplay, manage the players, the monsters (motion, attack and help), the spells.
 * 
 * @author Alain Becam - Joakim Olsson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class SeeThem2 implements ITickable, ISeeThem2
{
    Tick myTicker;
    int endOfTick = 0; // Do we want to stop the ticker?

    int idMonst = 0; 		// To give unique Id to the monsters

    int iHighSt; 			// Variables to simple express that we are still alive. From time to time, we print that we are still here!
    int iLowSt;
    int waitingTime;
    int nbSubject[]; // Number of subject one player have. Planned to 40 players max (see in the constructor).
    String leaders[]; // The leaders of the subjects (which player)
    int nbPlayers; // Simply the number of player, to be able to send the scores.
    String defaultString; // One string containing "default"

    public class PlayerEntity
    {
        float xPos,yPos, zPos, dir;
        int statut;
        String nick;
        int iIdle=0; // Used to count the IDLE time of a player. After a while he is considered to be offline.
        int faction;// My faction (will correspond to the shape of the subjects)
        float avision=(float )Math.PI/4; // Angle of vision
        float avisionExt=0.0f; // Angle of vision asked by other players. Overwrite the player's angle!
        
        // Here we just store (later we will also check)
        
        /**
         * @param pos
         * @param pos2
         * @param dir
         */
        public PlayerEntity(String nick,float pos, float pos2, float zpos, float dir, int statut,int faction)
        {
            this.nick=nick;
            xPos = pos;
            yPos = pos2;
            zPos = zpos;
            this.dir = dir;
            this.statut = statut;
            this.faction=faction;
        }
        
        public void setAll(float xPos, float yPos, float zPos, float dir, int statut,float avision)
        {
            this.xPos = xPos;
            this.yPos = yPos;
            this.zPos = zPos;
            this.dir = dir;
            this.statut = statut;
            this.avision=avision;
        }
        
        public String getAll()
        {
            // We send the angle of vision the server wants (0.0 if not)
            return new String(nick + " : " +xPos + " : " +yPos + " : " + zPos + " : " + dir + " : " + statut + " : " + avisionExt + "/");
        }
        
        // The same one, but sending the actual angle of vision
        public String getAll2()
        {
            return new String(nick + " : " +xPos + " : " +yPos + " : " + zPos + " : " + dir + " : " + statut + " : " + avision + "/");
        }
        
        public String getAll2D()
        {
            return new String(nick + " : " +xPos + " : " +yPos + " : " + dir + " : " + statut + " : " + avision + "/");
        }
        
        /**
         * @return Returns the nick.
         */
        public String getNick()
        {
            return nick;
        }

        /**
         * @param nick The nick to set.
         */
        public void setNick(String nick)
        {
            this.nick = nick;
        }

        /**
         * @return Returns the statut.
         */
        public int getStatut()
        {
            return statut;
        }
        /**
         * @param statut The statut to set.
         */
        public void setStatut(int statut)
        {
            this.statut = statut;
        }
        /**
         * @return Returns the xPos.
         */
        public float getXPos()
        {
            return xPos;
        }
        /**
         * @param pos The xPos to set.
         */
        public void setXPos(float pos)
        {
            xPos = pos;
        }
        /**
         * @return Returns the yPos.
         */
        public float getYPos()
        {
            return yPos;
        }
        /**
         * @param pos The yPos to set.
         */
        public void setYPos(float pos)
        {
            yPos = pos;
        }
        /**
         * @return Returns the zPos.
         */
        public float getZPos()
        {
            return zPos;
        }
        /**
         * @param pos The zPos to set.
         */
        public void setZPos(float pos)
        {
            zPos = pos;
        }
        
        /**
         * @return Returns the dir.
         */
        public float getDir()
        {
            return dir;
        }

        // An new distant entity is initialised
        public void wasManaged()
        {
            iIdle = 0;
        }
        
        // Used to see if a player is online.
        public void isUpdated()
        {
            iIdle = 0;
        }
        
        public boolean nonResponding()
        {

            iIdle++;
            if (iIdle > 100000)
            {
                return true;
            }
            else
                return false;
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

        /**
         * @return Returns the avisionExt.
         */
        public float getAvisionExt()
        {
            return avisionExt;
        }

        /**
         * @param avisionExt The avisionExt to set.
         */
        public void setAvisionExt(float avisionExt)
        {
            this.avisionExt = avisionExt;
        }
    }
    
    java.util.HashMap ourEntities;
    
    
    public class Subject
    {
        // The subject are changed by your view on them. In fact we keep a type (which is not supposed to change like that) and a faction (which change like that).
        
        String name;

        float posX, posY, dir;

        float xSpeed, ySpeed; // Reserv

        int energy, faction; // The faction -> which player it belows to. Energy : reserv.

        int type;

        Integer subjectId; // Unique id (we hope)
        
        String leader; // The nick of my current leader

        
        float dirX, dirY, dist;

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
        public Subject(String name, float posX, float posY, int energy, int faction, int type)
        {
            this.name = name;
            this.posX = posX;
            this.posY = posY;

            this.dir = 0;
            this.energy = energy;
            this.faction = faction;
           
            this.type = type;
            this.leader=new String("default");
        }

        public String giveAll()
        {
            // Return a string of all informations for the monsters
            return new String(subjectId + " : " + name + " : " + posX + " : " + posY + " : " + energy + " : " + faction + " : " + type + "/");
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
        public Integer getSubjectId()
        {
            return subjectId;
        }

        /**
         * @param monsterId The monsterId to set.
         */
        public void setSubjectId(Integer subjectId)
        {
            this.subjectId = subjectId;
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

        /**
         * @return Returns the leader.
         */
        public String getLeader()
        {
            return leader;
        }

        /**
         * @param leader The leader to set.
         */
        public void setLeader(String leader)
        {
            this.leader = leader;
        }
    }

    HashMap ourSubjects;

        
    // Log facility
    LogManager ourLog;
    
    // function to give the angle for the angle of one vector. Can be then compared to the direction
    double posToAngle(float x, float y, float xT, float yT)
    {
        float xE, yE, norm;
        double alpha;

        xE = xT - x;
        yE = yT - y;
        norm = (float) Math.sqrt(xE * xE + yE * yE);

        alpha = Math.acos(xE / norm);
        if (yE < 0)
        {
            // Then we are on the other side
            alpha = 2*Math.PI- alpha;
        }
        return alpha;
    }
    
    boolean checkIfChanged(float alpha, float dir)
    {
       return true;
    }

    // Player distributed method
    synchronized public void createPlayerEntity(String Nick, float x, float y, float z, float dir, int status,int faction)
    {
        PlayerEntity theLittleNewOne = new PlayerEntity( Nick,  x,  y,  z,  dir,  status,faction);
        /*theLittleNewOne.setX(x);
        theLittleNewOne.setY(y);
        theLittleNewOne.setZ(z);
        theLittleNewOne.setDirection(dir);
        theLittleNewOne.setStatus(status);
        theLittleNewOne.setMindEnergy(200); // Cheating, has to change !!
        theLittleNewOne.isUpdated();*/
        ourEntities.put(Nick, theLittleNewOne);
    }

    /* (non-Javadoc)
     * @see modulesrepository.ISeeThem#managePos(java.lang.String, java.lang.String, float, float, float, float, int, int)
     */
    synchronized public String managePos(String Nick, String RealName, float x, float y, float z, float dir,
            int status, float avision)
    {
        // We update the player position and we check if he rotates, then he maybe change some subjects.
        // Then we send (anyway) the other player data.
        // The subjects are sent by updateSubjects
        StringBuffer myOutput = new StringBuffer();
        PlayerEntity receivedPlayer;

        String IdObj = new String(Nick);
        
        boolean okToLog ; // Must indicate if we can log the current move or not.

        if (ourEntities.containsKey(IdObj))
        {
            receivedPlayer = ((PlayerEntity) (ourEntities.get(IdObj)));
            // If the statut is 4096, then the client initialise itself on the PC, so I want to receive informations,
            // not to send.
            
            receivedPlayer.wasManaged(); ////////////////////////////////////////////
            
            if (status != 4096)
            {
                // 65536 is for Mobile initialisation
                if (status != 65536)
                {
                    // To not trust the client, if the distance is too big, we ignore the received one !
                    if (((Math.abs(receivedPlayer.getXPos() - x) + Math.abs(receivedPlayer.getZPos() - z)) < 100) || ((status & 2) >  0) )
                    {
                        // Do we have to check the subjects ?
                        //if (receivedPlayer.getDir() != dir)
                        {
                            // We change the entities in our view in our favor
                            // Note that it is now a digital change
                            // it might soon changed to be analogic... Then the concurrency might be better
                            Iterator iter = ourSubjects.values().iterator();
                            for (; iter.hasNext();)
                            {
                                Subject currentSubject = ((Subject) (iter.next()));
                                double angleToSub = posToAngle(x,z,currentSubject.getPosX(),currentSubject.getPosY());
                                float xT = currentSubject.getPosX() - x;
                                float yT = currentSubject.getPosY() - z;
                                float dist= (float) Math.sqrt(xT * xT + yT * yT);
                                
                                //double dif = Math.abs((2*Math.PI-dir)-angleToSub);
                                //double dif = Math.abs(2*Math.PI-dir-Math.PI/2-angleToSub);
                                float dirC = (float )Math.PI-dir;
                                if (dirC < 0)
                                    dirC+=2*Math.PI;
                                if (dirC > 2*Math.PI)
                                    dirC-=2*Math.PI;
                                double dif;
                                //if (dir>Math.PI)
                                {
                                    dif = Math.abs(dirC-angleToSub);
                                }
                                /*else
                                {
                                    dif = Math.abs(dirC-angleToSub);
                                }*/
                                if (dif > Math.PI)
                                {
                                    if (dirC > Math.PI)
                                        dif=(angleToSub+2*Math.PI)-dirC; 
                                    else
                                        dif=(dirC+2*Math.PI)-angleToSub; 
                                   //dif=(dif-2*Math.PI); 
                                }
                                
                                //if (dif < Math.PI/8)
                                //System.out.println("New angle "+dif+" from "+dir+" and "+angleToSub);
                                //System.out.println("Distance " + dist + " and our limit: " + (100.0f/avision) + " - " + avision);
                                //ourLog.add2Log(Nick+" moves or turns, aiming at "+dir+" for a difference with the current object of "+dif);
                                if ( (dif < avision) && (dist < (100.0f/avision) ) )
                                {
                                    currentSubject.setFaction(receivedPlayer.getFaction());
                                    //currentSubject.setFaction(3);
                                    String oldLeader = currentSubject.getLeader();
                                    if (!oldLeader.equals(Nick))
                                    {
                                        if (!oldLeader.equals("default"))
                                        {
                                            PlayerEntity oldPlayer = ((PlayerEntity) (ourEntities.get(oldLeader)));
                                            nbSubject[oldPlayer.getFaction()]--; // This player just lost one follower
                                        }
                                        else
                                        {
                                            nbSubject[0]--;
                                        }
                                        currentSubject.setLeader(Nick);
                                        nbSubject[currentSubject.getFaction()]++; // We have a new subject!
                                    }
                                    currentSubject.setType(1); // Visible
                                }
                                else
                                {
                                    String oldLeader = currentSubject.getLeader();
                                    /*if (!oldLeader.equals("default"))
                                    {
                                    
                                        PlayerEntity oldPlayer = ((PlayerEntity) (ourEntities.get(oldLeader)));
                                        nbSubject[oldPlayer.getFaction()]--; // This player just lost one follower
                                   
                                        currentSubject.setLeader(defaultString);
                                        nbSubject[0]++; // We have a new subject!
                                        currentSubject.setFaction(0); // For test ONLY !!!
                                    }*/
                                    if (oldLeader.equals(Nick))
                                    {
                                    
                                        //PlayerEntity oldPlayer = ((PlayerEntity) (ourEntities.get(oldLeader)));
                                        //nbSubject[oldPlayer.getFaction()]--; // This player just lost one follower
                                   
                                        //currentSubject.setLeader(defaultString);
                                        //nbSubject[0]++; // We have a new subject!
                                        currentSubject.setType(0); // Mean invisible
                                    }
                                }
                            }
                            
                        }
                        receivedPlayer.setAll(x, y, z, dir, status,avision);  
                    }
                    else
                    {
                        // To not stuck the client, we have to take care of the distant related output !
                        x = receivedPlayer.getXPos();
                        y = receivedPlayer.getYPos();
                        z = receivedPlayer.getZPos();
                    }
                    //receivedPlayer.setMindEnergy(((PlayerEntity) (ourEntities.get(IdObj))).getMindEnergy()+mindEnergy);
                    receivedPlayer.isUpdated(); // Still responding
                }
                else
                    ourLog.add2Log(Nick+" initialised from a mobile.");
            } else
            {
                // If the client connect itself on a PC, we both have to send him his information and to switch
                // his statut to "Player", which means he is not anymore only the aura ball
                ((PlayerEntity) (ourEntities.get(IdObj))).setStatut(1);
                x = receivedPlayer.getXPos();
                y = receivedPlayer.getYPos();
                z = receivedPlayer.getZPos();
                ourLog.add2Log(Nick+" initialised from a PC.");
            }
            // okToLog =  ((PlayerEntity) (ourEntities.get(IdObj))).ok4TheLog(); ////////////////////////
        } else
        {
            // Here is a new one !!!!
        	
            // From PC ?
            if (status == 4096)
            {
                status = 1; // Then his status is "normal"
                ourLog.add2Log("New player " +Nick+" initialised from a Pc.");
                nbPlayers++;
                nbSubject[nbPlayers]=0;
                leaders[nbPlayers-1]=Nick;
            } else
            {
                // Might be an aura ball...
                if (status == 65536)
                {
                    status = 2;
                    ourLog.add2Log("New player " +Nick+" initialised from a Mobile.");
                    nbPlayers++;
                    nbSubject[nbPlayers]=0;
                    leaders[nbPlayers-1]=Nick;
                }
            }
//////////////////////////////////////////////////////////////////////////////////////////////////
            /*PlayerEntity theLittleNewOne = PlayerEntity.load(Nick);
            theLittleNewOne.setX(x);
            theLittleNewOne.setY(y);
            theLittleNewOne.setZ(z);
            theLittleNewOne.setDirection(dir);
            theLittleNewOne.setName(RealName);
            theLittleNewOne.setStatus(status);
            theLittleNewOne.isUpdated();
            theLittleNewOne.wasManaged();

            //theLittleNewOne.setMindEnergy(200);*/
            //ourEntities.put(IdObj, theLittleNewOne);
            createPlayerEntity(Nick,x,y,z,dir,status,nbPlayers-1);


            okToLog = true;
        }
        // Anyway, we send back the result.
        // It might be good to filter the result considering the distance. Here it is also... The bad but fast
        // square distance...
        //if (okToLog)
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
                ourLog.add2Log("From PC: " + Nick + "  has moved to " + x + ", " + z + ".");
            }
        }

        // Return scan of nearby players 
        
        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext();)
        {
            PlayerEntity currentEntity = ((PlayerEntity) (iter.next()));
            if ((Math.abs(currentEntity.getXPos() - x) + Math.abs(currentEntity.getYPos() - y) + Math.abs(currentEntity.getZPos() - z)) < 1000)
            {
                myOutput.append(currentEntity.getAll());
            }
        }

        myOutput.append("#");
        return (new String(myOutput));
    }

    /* (non-Javadoc)
     * @see modulesrepository.ISeeThem#giveEntities2D(float, float, float, float)
     */
    synchronized public String giveEntities2D(float x, float y, float z, float distance)
    {
        StringBuffer myOutput = new StringBuffer();

        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext();)
        {
            PlayerEntity currentEntity = ((PlayerEntity) (iter.next()));
            if ((Math.abs(currentEntity.getXPos() - x) + Math.abs(currentEntity.getZPos() - z)) < distance)
            {
                myOutput.append(currentEntity.getAll2D());
            }
        }
        myOutput.append("#");

        return (new String(myOutput));
    }
    

    /* (non-Javadoc)
     * @see modulesrepository.ISeeThem#givePlayer2D(java.lang.String)
     */
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
       
    synchronized public String giveAll()
    {
        StringBuffer myOutput = new StringBuffer();
        
        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext();)
        {
            PlayerEntity currentEntity = ((PlayerEntity) (iter.next()));
            
            myOutput.append(currentEntity.getAll2());
        }

        myOutput.append("#");
        return (new String(myOutput));
    }
   
    /* (non-Javadoc)
     * @see modulesrepository.ISeeThem#updateSubjects(float, float)
     */
    synchronized public String updateSubjects(float posX, float posY, float dist)
    {
        // return every informations about monsters "around" the given position, then the client can print
        // the monsters (this method is called to render monsters)
        StringBuffer myOutput = new StringBuffer("");

        // Give at least
        Iterator iter = ourSubjects.values().iterator();
        for (; iter.hasNext();)
        {
            Subject currentEntity = ((Subject) (iter.next()));
            if ((Math.abs(currentEntity.getPosX() - posX) + Math.abs(currentEntity.getPosY() - posY)) < dist)
            {
                myOutput.append(currentEntity.giveAll());
            }
        }
        
        myOutput.append("#");
        //System.out.println("Update Monster sends : "+myOutput);
        return (new String(myOutput));
    }

    synchronized public String updateScore()
    {
        StringBuffer myOutput = new StringBuffer("");
        
        for (int iPlayer=0; iPlayer < nbPlayers-1;iPlayer++)
        {
            myOutput.append(leaders[iPlayer]+" : "+nbSubject[iPlayer]+"/");
        }
        myOutput.append(leaders[nbPlayers-1]+" : "+nbSubject[nbPlayers-1]+"/#");
        
        return (new String(myOutput));
    }

    public SeeThem2()
    {
        ourEntities = new HashMap();
        
        nbSubject = new int[40]; // 40 players max.
        leaders = new String[40];
        defaultString = new String("default");
        /*Iterator i = PlayerEntity.loadAll().iterator();
        PlayerEntity ent;
        while (i.hasNext())
        {
            ent = (PlayerEntity) i.next();
            ent.setStatus((ent.getStatut() & (~1)) | 2);
            ourEntities.put(ent.getNick(), ent);
        }*/
        ourSubjects = new HashMap();
        
        // Creation of the subjects.
        for (int iSubject=0;iSubject < 400;iSubject++)
        {
            idMonst=iSubject;
            Subject oneNewSubject = new Subject("Subject-"+idMonst, (float )Math.random() *600 - 200, (float )Math.random() *600 - 200, (int )Math.random() *200, 0, 1);
            oneNewSubject.setSubjectId(idMonst);
            ourSubjects.put(idMonst,oneNewSubject);
        }
        nbSubject[0]=idMonst+1; // the default player has everything now!
        leaders[0]=new String("default");
        nbPlayers=1;
        /*nbSubject[0]=30;
        leaders[0]=new String("Bobo");
        nbSubject[1]=40;
        leaders[1]=new String("Staline");
        nbSubject[2]=30;
        leaders[2]=new String("Julius");
        nbPlayers=3;*/
        iHighSt = 0; // Variables to simple express that we are still alive. From time to time, we print that we are still here!
        iLowSt = 0;
        waitingTime = 0;
        
        myTicker = new Tick(60, this);
        myTicker.start();
        ourLog = new LogManager();
        
        ourLog.initLog("SeeThem");
    }

    
    private double hypot(double side1, double side2) {
    	return Math.sqrt(side1 * side1 + side2 * side2);
    }
    
    
	public synchronized int onTick()
    {
        // The tick object help us to update our entity each X seconds.
        // For that we implement one and we inherit of ITickable
        // The tick will ask for this method.

        // Clean the current entities to check if the status is ok.
        {
            Iterator iterPl2 = ourEntities.values().iterator();

            for (; iterPl2.hasNext();)
            {
                PlayerEntity currentPlayEntity = ((PlayerEntity) (iterPl2.next()));
               
                //currentPlayEntity.saveAttempt();
                currentPlayEntity.nonResponding(); // The method check IF the player is not responding (combined with isUpdated)
            }
        }  
        
        if (waitingTime > 100)
        {
            waitingTime = 0;
            System.out.println("Game still running - "+iHighSt+" - "+iLowSt+".\n");
            iLowSt++;
            if (iLowSt < 0) // Max int limit reached (Wow !)
                iHighSt++; //Very sure we don't need to go further...
        }
        waitingTime++;
        return endOfTick;
    }
    
    public void Stop()
    {
        endOfTick = 1;
    }
}