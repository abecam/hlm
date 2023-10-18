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

import utils.ThreadQueue;

import core.CommManager;


/**
 * This class wait the distant call for module.
 * It also access the CommManager to send the result.
 *
 * @author Alain Becam Jan 2005
 * @author Paulo Lopes Jun 2005
 */
public class CommandServer extends Thread {
    
	public static final int COMMAND_PORT = 6920;
    private CommManager commManager;
    private ThreadQueue workersQueue;
    
    public void attachCommManager(CommManager theComManager) {
        commManager = theComManager;
    }

    public void run() {
        boolean endRequested = false;
        workersQueue = new ThreadQueue(100);
        
        try {
            ServerSocket server = new ServerSocket(COMMAND_PORT); //6920 for commands
            while (!endRequested) {
                Socket sckt = server.accept();
            	workersQueue.execute(new CommandSocketSrv(sckt, commManager));
                endRequested = commManager.isEndRequested();
            }
            workersQueue.endWanted();
        } catch (Exception e) {
            e.printStackTrace(); // To be enhanced
        }
    }
    
}
