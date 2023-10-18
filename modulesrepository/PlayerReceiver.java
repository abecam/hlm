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

/**
 * This module manages a simple set of entities.
 * 
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlayerReceiver implements IPlayerReceiver
{
    
    public class PlayerEntity
    {
        float xPos,yPos, zPos, dir;
        int statut, Id;
        
        // Here we just store (later we will also check)
        
        /**
         * @param pos
         * @param pos2
         * @param dir
         */
        public PlayerEntity(int Id,float pos, float pos2, float zpos, float dir, int statut)
        {
            this.Id=Id;
            xPos = pos;
            yPos = pos2;
            zPos = zpos;
            this.dir = dir;
            this.statut = statut;
        }
        
        public void setAll(float xPos, float yPos, float zPos, float dir, int statut)
        {
            this.xPos = xPos;
            this.yPos = yPos;
            this.zPos = zPos;
            this.dir = dir;
            this.statut = statut;
        }
        
        public String getAll()
        {
            return new String(Id + " : " +xPos + " : " +yPos + " : " + zPos + " : " + dir + " : " + statut + "/");
        }
        /**
         * @return Returns the id.
         */
        public int getId()
        {
            return Id;
        }
        /**
         * @param id The id to set.
         */
        public void setId(int id)
        {
            Id = id;
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
    }
    
    HashMap ourEntities; // HashMap of ourEntities[];
    
    synchronized public String managePos(int Id, float x, float y, float z, float dir, int statut)
    {
        StringBuffer myOutput = new StringBuffer();
        
        Integer IdObj = new Integer(Id);
      
        if (ourEntities.containsKey(IdObj))
        {
            // If the statut is 4096, then the client initialise itself on the PC, so I want to receive informations,
            // not to send.
            if ( statut != 4096)
            {
                // 65536 is for Mobile initialisation
                if ( statut != 65536)
                {
                    ((PlayerEntity )(ourEntities.get(IdObj))).setAll(x, y, z, dir, statut);
                }
            }
            else
            {
                // If the client connect itself on a PC, we both have to send him his information and to switch
                // his statut to "Player", which means he is not anymore only the aura ball
                ((PlayerEntity )(ourEntities.get(IdObj))).setStatut( 1 );
            }
        }
        else
        {
            // Here is a new one !!!!
            // From PC ?
            if ( statut == 4096)
            {
                statut = 1; // Then his statut is "normal"
            }
            else
            {
                // Might be an aura ball...
                if ( statut == 65536)
                {
                    statut = 2;
                }
            }
                
            PlayerEntity theLittleNewOne = new PlayerEntity(Id,x,y,z,dir,statut);
            ourEntities.put(IdObj, theLittleNewOne);
        }
        // Anyway, we send back the result.
        // It might be good to filter the result considering the distance. Here it is also... The bad but fast
        // square distance...
        Iterator iter = ourEntities.values().iterator();
        for (; iter.hasNext() ;)
        {
            PlayerEntity currentEntity = ((PlayerEntity )(iter.next()));
            if ( (Math.abs(currentEntity.xPos - x) + Math.abs(currentEntity.yPos - y) + Math.abs(currentEntity.zPos -z)) < 1000)
            {
                myOutput.append(currentEntity.getAll());
            }
        }
        myOutput.append("#");
        return (new String(myOutput));
    }
    /**
     * 
     */
    public PlayerReceiver()
    {
        ourEntities  = new HashMap();
    }
}
