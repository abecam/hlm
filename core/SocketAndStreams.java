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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import utils.LogManager;


public class SocketAndStreams {
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
    LogManager myLog;
	
    public SocketAndStreams(Socket s) {
        if (s.isClosed()) {
            System.out.println("Socked closed upon SAS init start");
        }
        try {
            socket = s;
                reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new PrintWriter(s.getOutputStream(), true);
        } catch (Exception e) {
            // Yadayada... error handling is for the weak!
           e.printStackTrace();
        }
        if (s.isClosed()) {
            System.out.println("Socked closed upon SAS init end");
        }
    }
    
	public SocketAndStreams(Socket s,LogManager theLog) {
        myLog=theLog;
		if (s.isClosed()) {
			System.out.println("Socked closed upon SAS init start");
		}
		try {
			socket = s;
				reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new PrintWriter(s.getOutputStream(), true);
		} catch (Exception e) {
			// Yadayada... error handling is for the weak!
            theLog.add2Log(e.getMessage());
		}
		if (s.isClosed()) {
            theLog.add2Log("Socked closed upon SAS init end");
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}
	
}
