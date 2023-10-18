/*
 * Created on Feb 1, 2005
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

package net;

import java.net.*;
import java.io.*;

import core.CommManager;


/**
 * Use the created socket from ServiceServer to accept command request, and answer...
 * @author Alain Becam Jan 2005
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServiceSocketSrv extends Thread{
    Socket sckt;
    CommManager theCommManager;
    private long idThread;
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        // TODO Auto-generated method stub
        createServiceListener();
    }
    
	private String instanceName;
	
	public ServiceSocketSrv(Socket usedSocket, CommManager theCommManager)
	{
		instanceName = new String("Command server socket support");
		sckt = usedSocket;
		this.theCommManager = theCommManager;
		idThread = Math.round((Math.random())*100000);
	}
	
	public void createServiceListener()
    {
        boolean endOfSocket = false;
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
            PrintWriter out = new PrintWriter(sckt.getOutputStream(), true);

            while (!endOfSocket)
            {
                // So if we are here, we have accepted a connexion

                String oneRequest= in.readLine();
                String lockCode;
                System.out.println("Received : "+oneRequest);
                if (oneRequest.equals("end."))
                {
                    endOfSocket = true;
                    System.out.println("End asked distantly");
                } else
                {
                    if (oneRequest.equals("addDistantModule"))
                    {
                        StringBuffer chunk = new StringBuffer("\n");
                        String oneLine = in.readLine();
                        if (oneLine.equals("start"))
                        {
                            oneLine = in.readLine();
                            while (!oneLine.equals("end."))
                            {
                                chunk.append(oneLine+"\n");
                                oneLine = in.readLine();
                            }
                        }
                        theCommManager.getTheListManager().addDistantModule(chunk);
                        out.println("OK");
                    }
                    if (oneRequest.equals("removeDistantModule"))
                    {
                        String oneLine = in.readLine();
                        if (oneLine.equals("start"))
                        {
                            System.out.println("RM1");
                            String moduleName = in.readLine();
                            System.out.println("RM2");
                            String hostName = in.readLine();
                            System.out.println("RM3");
                            theCommManager.getTheListManager().removeDistantModule(moduleName, hostName);
                        }
                        System.out.println("RM4");
                        String resultRM = in.readLine();
                        System.out.println("RM4: "+resultRM);
                        if (!(resultRM.equals("end.")))
                        {
                            out.println("CUT");
                        }
                        else
                        {
                            out.println("OK");
                        }
                        System.out.println("RM5");
                    }
                    
                    if (oneRequest.equals("getGlobalInformation"))
                    {
                        System.out.println("getGlobalInformation - Received !");
                        out.println("OK+");
                        out.println("start");
                        out.println("Help yourself for now... (lazy programmer)");
                        out.println("end.");
                    }
                    if (oneRequest.equals("addDistantChannel"))
                    {
                        String oneLine = in.readLine();
                        if (oneLine.equals("start"))
                        {
                            String channelName = in.readLine();
                            String ownerName = in.readLine();
                        
                            int state = (theCommManager.getTheCodec()).addDistantChannel(channelName, ownerName);
                            if (state == 0)
                            {
                                out.println("OK");
                            }
                            else
                            {
                                if (state == 1)
                                {
                                    out.println("ALREADYADDED");
                                }
                            }
                        }
                        if (!in.readLine().equals("end."))
                        {
                            out.println("CUT");
                        }
                    }
                    // We must check if the comm manager still want us
                    if (!endOfSocket)
                    {
                        endOfSocket = theCommManager.isEndRequested();
                    }
                }
            } 
            in.close();
            out.close();
            sckt.close();
            System.out.println("Socket really ended : "+idThread);
        }
        catch (Exception e)
        {
            e.printStackTrace(); //...
        }
    }
	
}
