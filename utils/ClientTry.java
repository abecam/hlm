/*
 * Created on Aug 5, 2005
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

package utils;


import java.awt.Graphics;

import javax.swing.JFrame;

import javax.swing.JPanel;

import core.CommandSet;
import core.InterpretOrders;
import core.MainManager;
import core.ManageInstances;

/**
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClientTry extends JFrame implements ITickable
{

	//static String usedServer="193.11.8.108";
	static String usedServer="localhost";
	//static String usedServer="localhost";
	int usedSpeed=3; //00;

	private javax.swing.JPanel jContentPane = null;
	private JPanel jPanel = null;
	private MainManager myMainManager = new MainManager(null); 
	// You might have only one main manager on a computer. If not (for test for example), you must start 
	// The server first, as you will have some bind error after that, then the others main managers will
	// automatically start them servers.
	ToGetResults myGetter , myDummyGetter; // You must have one Getter for one distant method. It will wait the result for you.
	InterpretOrders myInterpret ;
	ManageInstances myManager ;
	Tick myTicker;
	
	boolean playerRecFree = true; // We use this variables to call the modules only after we have consumed our data
	boolean dummyRecFree = true;
	float x ;
    float y ;
    int NbPlayerRead=0; // To avoid lock, after a while, we resend our infos anyway
    int NbDummyRead=0;
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() { 
				public void mouseDragged(java.awt.event.MouseEvent e) {    
					//System.out.println("mouseDragged()"); // TODO Auto-generated Event stub mouseDragged()
					//jPanel.getGraphics().drawLine(e.getX(),e.getY(), 30, 20);
				    
                    {
                        
                        // In my stupid PlayerReceiver, we expect float values for x,y and z.
                        // So let push something float...
                        x = ((float )(e.getX()))/2;
                        y = ((float )(e.getY()))/2;

					}

					
				}
			});
		}
		return jPanel;
	}
      public static void main(String[] args)
    {
    	  (new ClientTry()).setVisible(true);
    }
	/**
	 * This is the default constructor
	 */
	public ClientTry() {
		super();
		myGetter = new ToGetResults();
		myDummyGetter = new ToGetResults();
		myInterpret = myMainManager.getTheInterpreter();
		 System.out.println(myMainManager+" "+myInterpret);
		myManager = myMainManager.getTheInstanceManager();
		initialize();
		myTicker = new Tick(usedSpeed,this);
		myTicker.start();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300,200);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	
    /* (non-Javadoc)
     * @see java.awt.Component#update(java.awt.Graphics)
     */
    public void update(Graphics arg0)
    {
        // TODO Auto-generated method stub
        //super.update(arg0);
        // Player Receiver Call
        if (playerRecFree)
        {
            playerRecFree = false; // The module is called, we will wait for the data to be consumed before we call it again
            InterpretOrders.MethodDesc myMethod;
            Object result;
            String command;

            // For now : Id (integer), x,y,z (floats), direction (float, in radians), statut (see PlayerReceiver or the PC client)
            // Should change soon
            command = new String("PlayerReceiver.managePos(1010,"+Float.toString(x)+ ",10.0,"+Float.toString(y)+",0.0,2)");
            try
            {

                myMethod = myInterpret.extractMethod(command);


                myMethod.setInstanceManager(myManager);


                CommandSet myCommandSet = new CommandSet();
                myCommandSet.setCallerName("ClientTry");

                // We give our reference, we must implement
                // the FacilityUser interface to get a
                // result, and it is asynchrone... The
                // answer may come later...
                myCommandSet.setOwner(myGetter);
                myCommandSet.setMethod(myMethod);

                int manaRes = myMainManager.getTheDispatcher().ask4DistantCommand(usedServer, myCommandSet, myGetter);
                //System.out.println("Return "+manaRes);
                // And then we wait for the data...
            } catch (Exception ex)
            {
                // We do not push the Stack here !!!
                playerRecFree = true; // Retry
                ex.printStackTrace();
            }
        }
        // Dummy Receiver Call
        if (dummyRecFree)
        {
            dummyRecFree = false; // Wait for the result before we recall the module
            InterpretOrders.MethodDesc myMethod;
            Object result;

            String command;
            command = new String("DummyReceiver3.managePos(" + Float.toString(x) + "," + Float.toString(y) + ")");
            try
            {

                myMethod = myInterpret.extractMethod(command);

                myMethod.setInstanceManager(myManager);

                CommandSet myCommandSet = new CommandSet();
                //myCommandSet.setCallerName("ClientTry");

                // We give our reference, we must implement
                // the FacilityUser interface to get a
                // result, and it is asynchrone... The
                // answer may come later...
                myCommandSet.setOwner(myDummyGetter);
                myCommandSet.setMethod(myMethod);

                int manaRes = myMainManager.getTheDispatcher().ask4DistantCommand(usedServer, myCommandSet, myGetter);
                // And then we wait for the data...
            } catch (Exception ex)
            {
                // We do not push the Stack here !!!
                ex.printStackTrace();
                dummyRecFree = true;
            }
        }
	
        // Recover the data from the PlayerReceiver module
        if (myGetter.isDataReady())
        {
            arg0.clearRect(0,0,getWidth(), getHeight());
            // Recover the result (Ultra crappy
            // method)
            String resultText = myGetter.getResult();
            //System.out.println(resultText);
            {
                // Manage the result
                StringBuffer chunk = new StringBuffer(resultText);
                int posSlash = chunk.indexOf("/");
                while (posSlash > 0)
                {
                    String myCurrentChunk = chunk.substring(0,posSlash);
                    // Now, myCurrentChunk might contain Id : posX : posY : posZ : dir : statut/
                    // Let extract the x and y pos.
                    int posX = myCurrentChunk.indexOf(":");
                    int endPosX = myCurrentChunk.indexOf(":",posX+1);
                    int endPosY = myCurrentChunk.indexOf(":",endPosX+1);
                    int endPosZ = myCurrentChunk.indexOf(":",endPosY+1);
                    
                    Float currX = Float.valueOf(myCurrentChunk.substring(posX+1, endPosX));
                    Float currY = Float.valueOf(myCurrentChunk.substring(endPosY+1, endPosZ));
                   
                    
                    jPanel.getGraphics().drawOval(currX.intValue()*2,currY.intValue()*2,3,3);

                    chunk.delete(0, posSlash+1);

                    posSlash = chunk.indexOf("/");
                }
            }         
            // Then we release the getter...
            playerRecFree = true; // Ok to call the module another time !
            
            myGetter.dataConsumed();
        }
        //      Recover the data from the DummyReceiver module
        if (myDummyGetter.isDataReady())
        {
            //arg0.clearRect(0,0,getWidth(), getHeight());
            // Recover the result (Ultra crappy
            // method)
            String resultText = myDummyGetter.getResult();
            //System.out.println(resultText);
            {
                // Manage the result
                StringBuffer chunk = new StringBuffer(resultText);
                int posSlash = chunk.indexOf("/");
                while (posSlash > 0)
                {
                    String myCurrentChunk = chunk.substring(0,posSlash);
                    // Now, myCurrentChunk might contain Id : posX : posY : posZ : dir : statut/
                    // Let extract the x and y pos.
                    int posX = myCurrentChunk.indexOf(":");
                    int endPosX = myCurrentChunk.indexOf(":",posX+1);
                    int endPosY = myCurrentChunk.indexOf("/",endPosX+1);
                    Float currX = Float.valueOf(myCurrentChunk.substring(0, posX));
                    Float currY = Float.valueOf(myCurrentChunk.substring(posX+1));
                   
                    int currIX = currX.intValue()*2;
                    int currIY = currY.intValue()*2;
                    
                    
                    jPanel.getGraphics().drawRect(currIX, currIY, 2, 2);

                    chunk.delete(0, posSlash+1);

                    posSlash = chunk.indexOf("/");
                }
            }
            
            dummyRecFree = true; // Ok to recall the module
            // Then we release the getter...
            myGetter.dataConsumed();
        }

        if ( (!playerRecFree) && (NbPlayerRead > 10))
        {
            playerRecFree = true;
            NbPlayerRead = 0;
        }
        if ( (!dummyRecFree) && (NbDummyRead > 10))
        {
            dummyRecFree = true;
            NbDummyRead = 0;
        }
        NbPlayerRead++;
        NbDummyRead++;
    }
    /* (non-Javadoc)
     * @see mmroutils.ITickable#onTick()
     */
    public int onTick()
    {
        // TODO Auto-generated method stub
        this.update(this.getGraphics());
        
        return 0;
    }
    
    
    // For info only for now, not used in this example
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
}
