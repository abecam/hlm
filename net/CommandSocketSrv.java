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
import core.InterpretOrders;
import core.ManageInstances;


/**
 * Use the created socket from CommandServer to accept command request, and
 * answer...
 * 
 * @author Alain Becam Jan 2005
 * @author Paulo Lopes Jun 2005
 */
public class CommandSocketSrv implements Runnable
{
    boolean debugMode = false;
    
    private Socket sckt;

    private CommManager theCommManager;

    public CommandSocketSrv(Socket usedSocket, CommManager theCommManager)
    {
        sckt = usedSocket;
        this.theCommManager = theCommManager;
    }

    public void run()
    {
        InterpretOrders myInterpret = theCommManager.getTheInterpreter();
        ManageInstances instancesManager = theCommManager.getTheInstanceManager();
        int count = 0; // To avoid locks, we count the correct try (+1) and the
                       // mistakes (+4)... After some, we end.
        
        // Update : with the "acknolegdement" version, we suppose that we pass
        // on errors, to not loose time
        // So we must end the socket at the first error, for allowing the client
        // to redo quickly a new socket...
        boolean endOfSocket = false;
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(sckt.getInputStream()));
            PrintWriter out = new PrintWriter(sckt.getOutputStream(), true);

            while (!endOfSocket)
            {
                try
                {
                    // So if we are here, we have accepted a connexion

                    //System.out.println("Here -1");
                    String oneCommand = in.readLine();
                    if (debugMode)
                    {
                        System.out.println("We have received a command: "+oneCommand);
                    }
                    String lockCode;
                    if (oneCommand != null)
                    {
                        if (oneCommand.equals("end."))
                        {
                            endOfSocket = true;
                        } else
                        {
                            if (oneCommand.length() > 0)
                            {
                                // here is something

                                if (oneCommand.length() > 5)
                                {
                                    if (oneCommand.substring(0, 5).equals("lock:"))
                                    {
                                        lockCode = oneCommand.substring(5);
                                        oneCommand = in.readLine();
                                    } else
                                    {
                                        lockCode = new String("");
                                    }
                                } else
                                {
                                    lockCode = new String("");
                                }

                                InterpretOrders.MethodDesc myMethod;
                                myMethod = myInterpret.extractMethod(oneCommand);
                                myMethod.setIPCaller(sckt.getInetAddress());

                                if ((theCommManager.getTheBoss()).requestModuleUse(myMethod.getClassName(), myMethod.getInstance(), lockCode))
                                {
                                    myMethod.setInstanceManager(instancesManager);
                                    Object result = myMethod.execMethod(0);
                                    //System.out.println("We have executed a
                                    // command, result:" + result);
                                    out.println("OK");
                                    String ack = in.readLine();

                                    // If the client have well received, then it
                                    // send the acknowlegdement ACK.
                                    if (ack.equals("ACK"))
                                    {
                                        //out.flush();
                                        //System.out.println("Here 1");
                                        // We have achieved an execution, so
                                        // here is one
                                        // try
                                        count++;

                                        if (result != null)
                                        {
                                            out.println(result.toString());
                                            out.flush();
                                            //System.out.println("We send
                                            // :"+result.toString());
                                        } else
                                        {
                                            out.println("noresult");
                                            out.flush();
                                        }
                                    }
                                    // Or we do nothing (because we are not here to lost time, the client can try again)

                                    //System.out.println("Here 2");
                                } else
                                {
                                    out.println("locked : " + (theCommManager.getTheBoss()).requestLockCode(myMethod.getClassName(), myMethod.getInstance()));
                                    out.flush();
                                    // One other try (I am not sure it's good to count here... )
                                    count++;
                                }
                                endOfSocket = theCommManager.isEndRequested();
                            } else
                            {
                                //count+=4; // Nothing...
                                endOfSocket = true; // Problem, we quit !
                            }
                        }
                    }
                } catch (Exception e)
                {
                    //count+=20; // Problem
                    endOfSocket = true; // Problem, we quit !
                    //System.out.println("Here Pb");
                    e.printStackTrace(); //...
                }
                if (count > 100)
                {
                    // Too much loops, we end
                    endOfSocket = true;
                }
            }
            in.close();
            out.close();
        } catch (Exception e)
        {
            e.printStackTrace(); //...
        }

        try
        {
            //System.out.println("Socket closed (here)");
            sckt.close();
        } catch (Exception e)
        {
            e.printStackTrace(); //...
        }
    }
}