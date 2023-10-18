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

/**
 * This module just create some "flying" objects using the given coordinates
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DummyReceiver3 implements IDummyReceiver
{
    
    public class AlEntity
    {
        float xPos,yPos;
        float xSpeed,ySpeed;
        
        float dir;
           
        /**
         * @param pos
         * @param pos2
         * @param dir
         */
        public AlEntity(float pos, float pos2, float dir)
        {
            xPos = pos;
            yPos = pos2;
            xSpeed = 0;
            ySpeed = 0;
            this.dir = dir;
        }
    }
    
    AlEntity ourEntities[];
    
    synchronized public String managePos(float x, float y)
    {
        // In this second idiotic test, we take the coord. received 
        // and the "monsters" try to follow the player...
        
        StringBuffer myOutput = new StringBuffer();
        
        // Let manage our army !!!!
        for (int i = 0 ; i < 1000 ; i++)
        {
            float xE, yE, norm;
            double alpha;
            
            xE = x- ourEntities[i].xPos;
            yE = y- ourEntities[i].yPos;
            norm = (float)Math.sqrt( xE*xE + yE*yE );
            
            ourEntities[i].xSpeed += 0.05 * xE / norm ;
            ourEntities[i].ySpeed += 0.05 * yE / norm ;
            
            //ourEntities[i].xSpeed *=  norm/10;
            //ourEntities[i].ySpeed *=  norm/10 ;
            
            ourEntities[i].xPos += ourEntities[i].xSpeed;
            ourEntities[i].yPos += ourEntities[i].ySpeed;
            xE = ourEntities[i].xPos;
            yE = ourEntities[i].yPos;
            
            myOutput = myOutput.append(xE + " : " + yE + "/");
        }
        myOutput.append("#");
        return (new String(myOutput));
    }
    
    synchronized public String returnPos()
    {
        // In this second idiotic test, we take the coord. received 
        // and the "monsters" try to follow the player...
        
        StringBuffer myOutput = new StringBuffer();
        
        for (int i = 0 ; i < 1000 ; i++)
        {
            float xE = ourEntities[i].xPos;
            float yE = ourEntities[i].yPos;
            
            myOutput = myOutput.append(xE + " : " + yE + "/");
        }
        myOutput.append("#");
        return (new String(myOutput));
    }
    /**
     * 
     */
    public DummyReceiver3()
    {
        ourEntities = new AlEntity[1000];
        // For now we manage 10 other entities
        for (int i = 0; i < 1000 ; i++)
        {
            ourEntities[i] = new AlEntity((float )(10*Math.random()), (float )(10*Math.random()) , (float )(2*Math.random() * Math.PI));
        }
    }
}
