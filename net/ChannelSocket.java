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
 * This class create a socket to send a data set to a channel on a distant machine
 * We send only one set of data here, then we end, to avoid a huge amount of thread in the same time
 * Would be fine to manage a poll of thread instead...
 * @author Alain Becam Jan 2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChannelSocket extends Thread
{
    private String instanceName;

    private String distantHost;

    private String clientName;

    private String channelName;

    private char[] objectOut;

    boolean endOfSocket = false;

    Codec theCodec;

    CommManager theCommManager;

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        // TODO Auto-generated method stub
        while ((!theCommManager.isEndRequested()) && !endOfSocket)
        {
            createClient();
        }
    }

    public ChannelSocket(String distantHost, String clientName, String channelName, char[] objectOut, Codec theCodec,
            core.CommManager theCommManager)
    {
        instanceName = new String("generic socket support to " + distantHost);
        this.theCommManager = theCommManager;
        this.theCodec = theCodec;
        this.distantHost = distantHost;
        this.clientName = clientName; // Name of the requester
        this.objectOut = objectOut;
    }

    public void createClient()
    {
        try
        {
            Socket sckt = new Socket(distantHost, 6901); // 6901 for channels.

            String result;
            StringBuffer params = new StringBuffer("nothing");
            BufferedReader in = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
            PrintWriter out = new PrintWriter(sckt.getOutputStream(), true);

            while (!endOfSocket)
            {
                // So if we are here, we have accepted a connexion

                out.println("Channel");
                out.println(channelName);
                out.println("Data from");
                out.println(theCommManager.getLocalHostName());
                out.println(clientName);
                out.println(objectOut.length);
                out.println("start");
                out.write(objectOut);
                out.println("end.");

                result = in.readLine();
                // Answer to manage...
                if (result.equals("OK"))
                {
                    // Nothing, all ok.
                    endOfSocket = true;
                    // theCodec.
                    // Notification ???
                } else
                {
                    if (result.equals("OK+"))
                    {
                        String answer;
                        StringBuffer bufferedResult = new StringBuffer();
                        answer = in.readLine();
                        if (answer.equals("start"))
                        {
                            while (!answer.equals("end."))
                            {
                                bufferedResult.append(answer);
                                answer = in.readLine();
                            }
                        }
                        //theCommManager.consumeService(distantHost, bufferedResult);
                    } else
                    {
                        if (result.equals("FAILED") || result.equals("CUT"))
                        {
                            // Nothing to do for now
                        }
                    }
                }
                endOfSocket = theCommManager.isEndRequested();

            }

            in.close();
            out.close();
            sckt.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}