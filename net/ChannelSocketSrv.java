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
 * USe the created socket from CommandServer to accept command request, and answer...
 * @author Alain Becam Jan 2005
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChannelSocketSrv extends Thread{
    Socket sckt;
    CommManager theCommManager;
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        // TODO Auto-generated method stub
        createServiceListener();
    }
    
	private String instanceName;
	
	public ChannelSocketSrv(Socket usedSocket, CommManager theCommManager)
	{
		instanceName = new String("Channel server socket support");
		sckt = usedSocket;
		
		this.theCommManager = theCommManager;
	}
	
	public void createServiceListener()
    {
        boolean endOfSocket = false;

        while (!endOfSocket)
        {
            try
            {
                // So if we are here, we have accepted a connexion
                BufferedReader in = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
                PrintWriter out = new PrintWriter(sckt.getOutputStream(), true);
                String oneRequest= in.readLine();
                
                System.out.println("Entering the server channel !");
                if (oneRequest.equals("end."))
                {
                    endOfSocket = true;
                } else
                {
                    if (oneRequest.equals("Channel"))
                    {
                        String channelName = in.readLine();
                        if ( (in.readLine().equals("Data from")) )
                        {
                            // We continue
                            String hostName = in.readLine();
                            String senderName = in.readLine();
                            int length = Integer.parseInt(in.readLine());
                            // Start to receive data
                            if ( (in.readLine().equals("start")) )
                            {
                                char[] buffer = new char[length];
                                in.read(buffer);
                                if ( (in.readLine().equals("end.")) )
                                {
                                    // All ok...
                                    out.println("OK");
                                    // No OK+ for now !
                                    ChannelResult theResult = new ChannelResult();
                                    theResult.setChannelName(channelName);
                                    theResult.setOwnerName(senderName);
                                    theResult.setResult(buffer);
                                    theResult.setDataSetName(hostName + channelName); 
                                    
                                    theCommManager.getTheCodec().receive(theResult);
                                }
                                else
                                {
                                    out.println("CUT");
                                }
                            }
                            else
                            {
                                out.println("CUT");
                            }
                        }
                        else
                        {
                            out.println("CUT");
                        }
                        
                    }
                    else
                    {
                        out.println("FAILED");
                        // For now the distinction is thin...
                    }
                    if (!endOfSocket)
                    {
                        endOfSocket = theCommManager.isEndRequested();
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace(); //...
            }
        }
        try
        {
            sckt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace(); //...
        }
    }
	
}
