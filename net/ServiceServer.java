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

import core.CommManager;


/**
 * The server for the services, start a socket when it receives a request.
 * @author Alain Becam Jan 2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServiceServer extends Thread{
    CommManager usedCommManager;
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        // TODO Auto-generated method stub
        createServiceServer();
    }
	private String instanceName ;
	//private Socket genericSocket= new Socket();
	
	public ServiceServer()
	{
		instanceName = new String("Service server");
	}
	
    public void attachCommManager(CommManager theComManager)
    {
        usedCommManager = theComManager;
    }
    
	public void createServiceServer()
    {
        boolean endRequested = false;
        try
        {
            ServerSocket server = new ServerSocket(6910); //6910 for
            // service
            while (!endRequested)
            {

                Socket sckt = server.accept();
                // So if we are here, we have accepted a connexion
                ServiceSocketSrv oneServSocket = new ServiceSocketSrv(sckt, usedCommManager);
                oneServSocket.start();

                endRequested = usedCommManager.isEndRequested();
            }

        } catch (Exception e)
        {
            e.printStackTrace(); // To be enhanced
        }
    }
	
}
