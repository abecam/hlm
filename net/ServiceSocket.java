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
import core.ServiceSet;


/**
 * The service socket, to ask something.
 * 
 * @author Alain Becam Jan 2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServiceSocket extends Thread
{
    private String instanceName;

    private String distantHost;

    private String command;

    private boolean endOfSocket;

    private long idThread;

    CommManager theCommManager;

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        // TODO Auto-generated method stub
        // The createClient shouldn't give the hand immediatly, but stop if there is a problem.
        // Then, if the end is not requested, we try again (might be good to enhance that... )
        // If the socket go to its end (endOfSocket), then it is ok to leave in the "normal" way
        while ((!theCommManager.isEndRequested()) && (!endOfSocket))
        {
            createClient();
            System.out.println("End of socket is " + endOfSocket);
        }
        System.out.println("End of socket is still " + endOfSocket);
    }

    public ServiceSocket(String distantHost, CommManager theCommManager)
    {
        instanceName = new String("generic socket support to " + distantHost);
        this.theCommManager = theCommManager;
        this.distantHost = distantHost;
        idThread = Math.round((Math.random()) * 100000);
        endOfSocket = false;
    }

    public void createClient()
    {
        try
        {
            Socket sckt = new Socket(distantHost, 6910);

            // So if we are here, we have accepted a connexion
            String result;
            StringBuffer params = new StringBuffer("nothing");
            BufferedReader in = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
            PrintWriter out = new PrintWriter(sckt.getOutputStream(), true);

            while (!endOfSocket)
            {
                // So if we are here, we have accepted a connexion

                ServiceSet ourCurrentService = theCommManager.readService(distantHost, params);
                String oneRequest = ourCurrentService.getRequest();
                if ((ourCurrentService.getParams()) != null )
                {
                    params = ourCurrentService.getParams();
                }
                System.out.println("We send "+oneRequest+" - Params :"+params);
                if (oneRequest.equals("end."))
                {
                    System.out.println("End requested locally");
                    out.println("end.");
                    endOfSocket = true;
                    theCommManager.iAmOutServ(distantHost);
                } else
                {
                    if (oneRequest.equals("wait"))
                    {
                        // We do nothing
                        System.out.println("Wait");
                    } else
                    {
                        out.println(oneRequest);
                        System.out.println(oneRequest);
                        System.out.print(params+" : ");
                        System.out.println((params.toString()).equals("nothing"));
                        if (!(params.toString()).equals("nothing")) // Command with
                        // parameters
                        {
                            System.out.println("Parameters sent");
                            out.println("start");
                            System.out.println("start");
                            out.println(params);
                            System.out.println(params);
                            out.println("end.");
                            System.out.println("end.");
                        }
                        result = in.readLine();
                        System.out.println("Answer: "+result);
                        // Answer to manage...
                        if (result.equals("OK"))
                        {
                            // Nothing, all ok.
                            theCommManager.consumeService(distantHost);
                            // We quit if needed...
                            /*System.out.println("No more socket needed...");
                            out.println("end.");
                            endOfSocket = true;*/
                        } else
                        {
                            if (result.equals("OK+"))
                            {
                                System.out.println("OK+ encountered");
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
                                } else
                                {
                                    // Problem...
                                    System.out.println("Unknow answer:" + answer);
                                }
                                System.out.println("OK+ ended");
                                theCommManager.consumeService(distantHost, bufferedResult);
                            } else
                            {
                                if (result.equals("FAILED") || result.equals("CUT"))
                                {
                                    // Nothing to do for now
                                }
                            }
                        }
                    }
                    if (!endOfSocket)
                    {
                        endOfSocket = theCommManager.isEndRequested();
                        if (endOfSocket)
                        {
                            System.out.println("Big END");
                        }
                    }
                }
            }

            in.close();
            out.close();
            sckt.close();
            System.out.println("Socket ended : " + idThread);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Socket really ended : " + idThread);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
        // TODO Auto-generated method stub
        System.out.println("Socket really really really ended : " + idThread);
        super.finalize();
    }
}