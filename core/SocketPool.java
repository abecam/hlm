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

package core;


import java.net.*;
import java.io.*;
import java.util.*;

import core.SocketAndStreams;

public class SocketPool {

	Map		hostToSocket;
	
	
	public SocketPool() {
		hostToSocket = new HashMap(15);
	}
	
	public SocketAndStreams getSAS(String host, int port) {
		
		SocketAndStreams result;
		
		if (hostToSocket.containsKey(host)) {
			
			// Requested connection exists in pool - return socket object immediately
			result = (SocketAndStreams)hostToSocket.get(host);
			
			if (result.getSocket().getPort() != port) {
				
				/* A connection to the right host was found, but connected to another port
				 * Thus, the fetched socket is regarded as invalid
				 * 
				 * TODO: Handle the situation
				 */

			}
			
		} else {
			
			// Connection not yet established - create and add it!
			result = createConnection(host, port);
			
		}
		
		return result;
	}
	
	private SocketAndStreams createConnection(String host, int port) {
		
		SocketAndStreams newSAS = null;
		Socket newSocket;
		
		System.out.println("SocketPool.createConnection - Adding connection for " + host + ":" + port);
		
		try {
			
			synchronized (hostToSocket) {
				
				newSocket = new Socket(host, port);
				
				if (newSocket != null) {
					
					newSAS = new SocketAndStreams(newSocket);
					
					// Creation successful - add to map
					
					hostToSocket.put(host, newSAS);
					
				} else {
					
					System.out.println("SocketPool.createConnection - Failed to create new socket!");					
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			newSocket = null;		// Just ensure null return
		}
		
		if (newSocket.isClosed()) {
			System.out.println("Socked is closed even when it's fresh!");
		}

		return newSAS;
	}
	
	public void closeSocket(String host) {

		if (hostToSocket.containsKey(host)) {
		
			try {
		
				SocketAndStreams mySAS = (SocketAndStreams)hostToSocket.get(host);
				mySAS.getReader().close();
				mySAS.getWriter().close();
				mySAS.getSocket().close();
				
			} catch (Exception e) {
				// IOException? then screw it!
			}
		
			hostToSocket.remove(host);
			
		}
		
	}
	
	
}
