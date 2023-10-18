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
import core.SocketAndStreams;
import core.SocketPool;


/**
 * Send one or more commands via one socket.
 * 
 * @author Alain Becam Jan 2005
 */
public class CommandSocket extends Thread {

	private String distantHost;
	private boolean endOfSocket = false;
	private CommManager theCommManager;
	boolean debugMode = false;


	public CommandSocket(String distantHost, CommManager theCommManager) {
		super("CommandSocket");
		//instanceName = "command socket support to " + distantHost;
		this.theCommManager = theCommManager;
		this.distantHost = distantHost;
	}

	class MMROInfoInputStream extends InputStream 
	{
		InputStream parent;
		public MMROInfoInputStream(InputStream parent)
		{
			this.parent=parent;
		}
		
		public int available() throws IOException
		{
			int av=parent.available();
			System.out.println("avail:"+av);
			return av;
		}
		public void close() throws IOException
		{
			System.out.println("closed");
			parent.close();
		}
		public void mark(int limit)
		{
			parent.mark(limit);
		}
		public boolean markSupported()
		{
			return parent.markSupported();
		}
		public int read() throws IOException
		{
			int value=parent.read();
			if (value<0)
				System.out.println("CONNECTION CLOSED!!!");
			return value;
		}
		public int read(byte[] data) throws IOException
		{
			int count=parent.read(data);
			if (count<0)
				System.out.println("CONNECTION CLOSED!!!");
			return count;
		}
		public int read(byte[] data,int ofs,int len) throws IOException
		{
			int count=parent.read(data,ofs,len);
			if (count<0)
				System.out.println("CONNECTION CLOSED!!!");
			return count;
		}		
		public void reset() throws IOException
		{
			System.out.println("reset");
			parent.reset();
		}
		public long skip(long count) throws IOException
		{
			long skipped=parent.skip(count);
			if (skipped!=count)
				System.out.println("skipped to end");
			return skipped;
		}
	}
	
	public void xassert(boolean cond,String msg) throws Exception
	{
		if (!cond)
			throw new Exception(msg);
	}
	
	public void run() {
		
		
		while ((!theCommManager.isEndRequested()) && !endOfSocket) {
			try {
				
				//SocketAndStreams sas = theCommManager.getSocketPool().getSAS(distantHost, 6920);
				Socket s = new Socket(distantHost, 6920);
				
				SocketAndStreams sas = new SocketAndStreams(s);
			    
				if (sas == null) {
			    	endOfSocket = true;
			    	System.out.println("SocketAndStreams became null");		
					continue;
			    }
				// normal
			    
				BufferedReader in = sas.getReader(); 
				// info-stream to detect closed files, useful for debugging!
				// BufferedReader in = new BufferedReader(new InputStreamReader(new MMROInfoInputStream(sckt.getInputStream())));
				
				PrintWriter out = sas.getWriter();
				// So if we are here, we have accepted a connexion
				
				String returnStr; 			// The return result.
				boolean outError=false;

				while (!endOfSocket) {
					// So if we are here, we have accepted a connexion

					String oneCommand = theCommManager.readCommand(distantHost);
					if (oneCommand.equals("end.") || outError) {
						endOfSocket = true;
					} else {
						out.println(oneCommand);
						xassert(out.checkError()==false,"Error condition on writer!");
						String result = in.readLine();

						// So, that is the result
						if (result.equals("OK")) {
						    out.println("ACK");
							xassert(out.checkError()==false,"Error condition on writer!");
							returnStr = in.readLine();
							if (debugMode)
							{
							    System.out.println("Result given: " + returnStr);
							}
							theCommManager.consumeCommand(distantHost,
									returnStr);
						} else {
							if (result.equals("wait")) {
								// Nothing to do, no command consumed
								// We have to wait.
							} else {
								if (result.equals("locked")) {
									String lock = in.readLine();
									oneCommand = theCommManager
											.findCommand4Lock(distantHost, lock);
									out.println("lock:" + lock);
									out.println(oneCommand);
									xassert(out.checkError()==false,"Error condition on writer!");
								}
							}
						}
						endOfSocket = theCommManager.isEndRequested();
					}
				}
				out.println("end.");

			} catch (ConnectException ce) {
				System.out.println("Connection to main server failed. Is it up?");
				theCommManager.consumeCommand(distantHost, "!#");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}