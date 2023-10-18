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

package core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Comm2String;

import core.SocketPool;

import net.ChannelServer;
import net.CommandServer;
import net.ServiceServer;


/**
 * The CommManager is the BIG link between the local facilities and the network support. 
 * It uses the Codec for the channel management (an this last has a bad name for now, it code 
 * and decode nothing, it's the only distortion compared to the architecture draft).
 * Typically, the communication to a distant host are requested and might not be used immediatly. So
 * we keep them in a collection and then, the socket can consume it.
 * For each communication, the sending can be for a group or for an alias. This is managed by DispatchCommand.
 * The channel are supported only by the Codec and the CommManager, as we use them directly by interface
 * in module.
 * 
 * The CommManager is started by the main manager and start the different servers.
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommManager {
    HashMap machines = new HashMap(); // The list of connected machines, both client (for local client and channel from here) and server
    ManageInstances theInstanceManager;
    DispatchCommand theDispatcher;
    InterpretOrders theInterpreter;
    ManageModulesList theListManager;
    MainManager theBoss;
    SocketPool theSocketPool;
    
    CommandServer theCommandServer;
    ServiceServer theServiceServer;
    ChannelServer theChannelServer;
    
    String localHostName;
    ArrayList endedServices = new ArrayList();
    ArrayList resultsFromChannel = new ArrayList(); // Only if one call of requestChannelUSe(String,String) was made. See below.
    int historyOfService = 100; // Allowed number of memorised result from service.
    
    net.Codec theCodec; // To be used through the interface CodecCommon
    
    boolean endRequested = false; // Request to end the comm.
    
    boolean debugMode = false; // Switch the verbose mode...
    
    /**
     * @param theInstanceManager
     * @param theDispatcher
     * @param theInterpreter
     * @param theListManager
     * @param theBoss the Main Manager.
     */
    public CommManager(ManageInstances theInstanceManager, DispatchCommand theDispatcher, InterpretOrders theInterpreter,ManageModulesList theListManager, MainManager theBoss)
    {
        this.theInstanceManager = theInstanceManager;
        this.theDispatcher = theDispatcher;
        this.theInterpreter = theInterpreter;
        this.theListManager = theListManager;
        this.theBoss = theBoss;
        
        this.theSocketPool = new SocketPool();
        
        try
        {
            java.net.InetAddress myAddress = InetAddress.getLocalHost(); // myServerSocket.getInetAddress();
        	localHostName = myAddress.getHostName();
        	
        	// Then start the different servers.
        	theCommandServer = new CommandServer();
        	theServiceServer = new ServiceServer();
        	theChannelServer = new ChannelServer();
        	theCommandServer.start();
        	theServiceServer.start();
        	theChannelServer.start();
        	theCommandServer.attachCommManager(this);
        	theServiceServer.attachCommManager(this);
        	theChannelServer.attachCommManager(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    // Local service
    
    /*public void addDistantMachine(String hostName, String mmroName, String type)
    {
        // Connection with DispatchCommand...
        DistantMachine oneNewDistantMachine = new DistantMachine(hostName,mmroName,type);
        machines.put(hostName, oneNewDistantMachine);
    }*/
    
    /**
     * @return Returns the theDispatcher.
     */
    public DispatchCommand getTheDispatcher()
    {
        return theDispatcher;
    }
    /**
     * @return Returns the theInstanceManager.
     */
    public ManageInstances getTheInstanceManager()
    {
        return theInstanceManager;
    }
    /**
     * @return Returns the theInterpreter.
     */
    public InterpretOrders getTheInterpreter()
    {
        return theInterpreter;
    }
    /**
     * @return Returns the theListManager.
     */
    public ManageModulesList getTheListManager()
    {
        return theListManager;
    }
    
    /**
     * @return Returns the theBoss.
     */
    public MainManager getTheBoss()
    {
        return theBoss;
    } 
    
    /**
     * @return Returns the localHostName.
     */
    public String getLocalHostName()
    {
        return localHostName;
    }
    
    /**
     * @return Returns the theCodec.
     */
    public net.Codec getTheCodec()
    {
        return theCodec;
    }
    
    /**
     * @return Returns the resultsFromChannel.
     */
    public synchronized ArrayList getResultsFromChannel()
    {
        return resultsFromChannel;
    }
    
    // Distant service
    public boolean isLinked(String distantChannel)
    {
        
        return false;
    }
    
    public synchronized String askDistantService(String ownerName,String request)
    {
        ServiceSet newServiceSet = new ServiceSet();
        if (debugMode)
        {
            System.out.println("ADS 1 - " + ownerName);
        }
        newServiceSet.request = request;
        newServiceSet.params = new StringBuffer("nothing");
        if (machines.containsKey(ownerName))
        {
            if (debugMode)
            {
                System.out.println("ADS OK 1");
            }
            // First, we check if there is already an existing socket.
            if (((DistantMachine )machines.get(ownerName)).serviceOn)
            {
                // So we add the command set to the waiting queue...
                ((DistantMachine )machines.get(ownerName)).services.add(newServiceSet);
                if (debugMode)
                {
                    System.out.println("ADS OK 2");
                }
                return "OK";
            }
            else
            {
                if (debugMode)
                {
                    System.out.println("ADS OK ALT 2");
                }
                // First start a command socket to the distant host...
                ((DistantMachine )machines.get(ownerName)).myServiceSocket = new net.ServiceSocket(ownerName,this);
                ((DistantMachine )machines.get(ownerName)).myServiceSocket.start();
                // Ok, we can add a command... (it should be consumed immediatly)
                ((DistantMachine )machines.get(ownerName)).services.add(newServiceSet);
                ((DistantMachine )machines.get(ownerName)).serviceOn = true;
                if (debugMode)
                {
                    System.out.println("Socket creation");
                    System.out.println("ADS OK ALT 3");
                }
                return "OK";
            }
        }
        else
        {
            if (debugMode)
            {
                System.out.println("ADS NOT OK");
            }
            return "ERROR"; // Or an exception ???
        }
    }
    
    public synchronized String askDistantServiceWParams(String ownerName, String request, StringBuffer params)
    {
        ServiceSet newServiceSet = new ServiceSet();
        newServiceSet.request = request;
        newServiceSet.params = params;
        if (debugMode)
        {
            System.out.println("Distant service with params requested");
            System.out.println("Params= "+params);
        }
        if (machines.containsKey(ownerName))
        {
            // First, we check if there is already an existing socket.
            if (((DistantMachine )machines.get(ownerName)).serviceOn)
            {
                // So we add the command set to the waiting queue...
                ((DistantMachine )machines.get(ownerName)).services.add(newServiceSet);
                if (debugMode)
                {
                    System.out.println("Service registered");
                }
                return "OK";
            }
            else
            {
                // First start a command socket to the distant host...
                ((DistantMachine )machines.get(ownerName)).myServiceSocket = new net.ServiceSocket(ownerName,this);
                ((DistantMachine )machines.get(ownerName)).myServiceSocket.start();
                // Ok, we can add a command... (it should be consumed immediatly)
                ((DistantMachine )machines.get(ownerName)).services.add(newServiceSet);
                ((DistantMachine )machines.get(ownerName)).serviceOn = true;
                if (debugMode)
                {
                    System.out.println("Service registered with socket creation");
                }
                return "OK";
            }
        }
        else
        {
            if (debugMode)
            {
                System.out.println("Owner non existant");
            }
            return "ERROR"; // Or an exception ???
        }
    }
    public boolean isEndRequested()
    {
        // We want to end the server activity.
        return endRequested;
    }
    
    public synchronized int sendCommand(String distantHost, CommandSet commandSet)
    {
    	if (machines.containsKey(distantHost))
        {
            // First, we check if there is already an existing socket.
            if (((DistantMachine )machines.get(distantHost)).commandOn)
            {
                // So we add the command set to the waiting queue...
                ((DistantMachine )machines.get(distantHost)).commands.add(commandSet);
                
                return 0;
            }
            else
            {
                // First start a command socket to the distant host...
                ((DistantMachine )machines.get(distantHost)).myCommandSocket = new net.CommandSocket(distantHost,this);
                ((DistantMachine )machines.get(distantHost)).myCommandSocket.start();
                // Ok, we can add a command... (it should be consumed immediatly)
                ((DistantMachine )machines.get(distantHost)).commands.add(commandSet);
                ((DistantMachine )machines.get(distantHost)).commandOn = true;
                return 0;
            }
        }
        else
        {
            return 1; // Or an exception ???
        }
    }
    
    public synchronized String readCommand(String distantHost)
    {
        // Give to a command socket a possible command for the distant host.
    	Comm2String myComm2String = new Comm2String();
        if (machines.containsKey(distantHost))
        {
            if (((DistantMachine )machines.get(distantHost)).commands.isEmpty())
            {
                // We finished the sockets (difficult to do it elsewhere)
                ((DistantMachine )machines.get(distantHost)).commandOn = false;
                return (new String("end."));
            }
            else
            {
                CommandSet oneCommand = (CommandSet )((DistantMachine )machines.get(distantHost)).commands.get(0);
                return (myComm2String.giveTranscript(oneCommand.moduleName, oneCommand.instance, oneCommand.methodName, oneCommand.parameters));
            }
        }
        else
        {
            return (new String("No DistantHost defined")); // Or an exception ???
        }
    }
    
    public synchronized void iAmOut(String distantHost)
    {
        // The socket has ended in an unusual way
        ((DistantMachine )machines.get(distantHost)).commandOn = false;
    }
    
    public synchronized String findCommand4Lock(String distantHost, String lockCode)
    {
        // The command was refused because there was a lock on it (it justifies also the list of command, useless otherwise, as the command are consumed then they arrived if there is no lock)
        // So we look for a compatible command set.
        // It might seem stupid to do so, but in fact, it works well. It allows to count how much tries we do and to request an unlock if there is much.
        // The idea is that we always try to consume the first command.
        // For lock/unlock a module, use the service channel. Or lock it by your own way (so you give a restricted access).
        Comm2String myComm2String = new Comm2String();
        if (machines.containsKey(distantHost))
        {
            // Seems not possible, anyway...
            if (((DistantMachine )machines.get(distantHost)).commands.isEmpty())
            {
                return (new String("wait"));
            }
            else
            {
                ArrayList commandsList = (((DistantMachine )machines.get(distantHost)).commands);
                CommandSet oneCommand;
                for (java.util.Iterator i= commandsList.iterator(); i.hasNext() ; )
                {
                    oneCommand = ((CommandSet )(i.next()));
                    if (oneCommand.equals(lockCode))
                    {
                        // Ok to execute this command
                        return (myComm2String.giveTranscript(oneCommand.moduleName, oneCommand.instance, oneCommand.methodName, oneCommand.parameters));
                    }
                }
                // Nothing found, nothing to do
                return (new String("No match now"));
            }
        }
        else
        {
            return (new String("No DistantHost defined")); // Or an exception ???
        }
    }
    
    public synchronized int consumeCommand(String distantHost, String result)
    {
        // A command has been passed with success, we have to cut it from the list of command
        if (machines.containsKey(distantHost))
        {
            if (((DistantMachine )machines.get(distantHost)).commands.isEmpty())
            {
                return 1;
            }
            else
            {
                CommandSet currentCommandSet = (CommandSet )((((DistantMachine )machines.get(distantHost)).commands).get(0));
                theDispatcher.pushResult(currentCommandSet,result);
                (((DistantMachine )machines.get(distantHost)).commands).remove(0);
                if ( (((DistantMachine) machines.get(distantHost)).commands).isEmpty())
                {
                    CommandSet newCommandSet = new CommandSet();
                    // We have to ask for the end of the command...
                    //(((DistantMachine) machines.get(distantHost)).commands).add("end.");
                }
                return 0;
            }
        }
        else
        {
            return (2); // Or an exception ???
        }
    }
    
    public synchronized int consumeCommand4Lock(String distantHost, String result, String lockCode)
    {
        // A command has been passed with success, we have to cut it from the list of command
        if (machines.containsKey(distantHost))
        {
            if (((DistantMachine )machines.get(distantHost)).commands.isEmpty())
            {
                return 1;
            }
            else
            {
                ArrayList commandsList = (((DistantMachine )machines.get(distantHost)).commands);
                CommandSet oneCommand;
                for (int iComm=0; iComm < commandsList.size() ; iComm++)
                {
                    oneCommand = ((CommandSet )(commandsList.get(iComm)));
                    if (oneCommand.equals(lockCode))
                    {
                        // Ok to execute this command
                        theDispatcher.pushResult(oneCommand,result);
                        (((DistantMachine )machines.get(distantHost)).commands).remove(iComm);
                        if ( (((DistantMachine) machines.get(distantHost)).commands).isEmpty())
                        {
                            (((DistantMachine) machines.get(distantHost)).commands).add("end.");
                        }
                        return 0;
                    }
                }
                // Nothing found, nothing to do
                return (1);
                
                
            }
        }
        else
        {
            return (2); // Or an exception ???
        }
    }
    
    public synchronized int disconnectDistantMachine(String distantHost)
    {
        if (machines.containsKey(distantHost))
        {
            ((DistantMachine )machines.get(distantHost)).endOfConnection = true;
            return 0;
        }
        else
        {
            return 1;
        }
    }
    
    public ServiceSet readService(String distantHost, StringBuffer params)
    {
        //      Give to a service socket a possible service request for the distant host.
        if (debugMode)
        {
            System.out.println("Send to : "+ distantHost);
        }
        if (machines.containsKey(distantHost))
        {
            if (((DistantMachine )machines.get(distantHost)).services.isEmpty())
            {
                return (null);
            }
            else
            {
                ServiceSet oneService = (ServiceSet )((DistantMachine )machines.get(distantHost)).services.get(0);
               
                return (oneService);
            }
        }
        else
        {
            return (null); // Or an exception ???
        }
    }
    
    public synchronized int consumeService(String distantHost)
    {
            // A command has been passed with success, we have to cut it from
            // the list of command
        if (machines.containsKey(distantHost))
        {
            if (((DistantMachine) machines.get(distantHost)).services.isEmpty())
            {
                return 1;
            } else
            {
                ServiceSet currentServiceSet = (ServiceSet) (((DistantMachine) machines.get(distantHost)).services).get(0);
                currentServiceSet.result = new StringBuffer("");
                endedServices.add(currentServiceSet);
                if (endedServices.size() > historyOfService)
                {
                    endedServices.remove(0);
                }
                (((DistantMachine) machines.get(distantHost)).services).remove(0);
                if (debugMode)
                {
                    System.out.println("Removed !!!!");
                }
                // If the system has not added something for the same distant machine, we end the socket
                if ( (((DistantMachine) machines.get(distantHost)).services).isEmpty())
                {
                    ServiceSet newServiceSet = new ServiceSet();
                    newServiceSet.request = new String("end.");
                    (((DistantMachine) machines.get(distantHost)).services).add(newServiceSet);
                    ((DistantMachine )machines.get(distantHost)).serviceOn = false;
                    if (debugMode)
                    {
                        System.out.println("Socket removed.");
                    }
                }
                return 0;
            }
        } else
        {
            return (2); // Or an exception ???
        }
    }

    public void iAmOutServ(String distantHost)
    {
        // The socket (service) has ended in an unusual way
        ((DistantMachine )machines.get(distantHost)).serviceOn = false;
    }
    
    public synchronized int consumeService(String distantHost, StringBuffer result)
    {
        if (machines.containsKey(distantHost))
        {
            if (((DistantMachine) machines.get(distantHost)).services.isEmpty())
            {
                return 1;
            } else
            {
                ServiceSet currentServiceSet = (ServiceSet) (((DistantMachine) machines.get(distantHost)).services).get(0);
                currentServiceSet.result = result;
                endedServices.add(currentServiceSet);
                if (endedServices.size() > historyOfService)
                {
                    endedServices.remove(0);
                }
                (((DistantMachine) machines.get(distantHost)).services).remove(0);
                if (debugMode)
                {
                    System.out.println("Removed !!!!");
                }
                if ( (((DistantMachine) machines.get(distantHost)).services).isEmpty())
                {
                    ServiceSet newServiceSet = new ServiceSet();
                    newServiceSet.request = new String("end.");
                    (((DistantMachine) machines.get(distantHost)).services).add(newServiceSet);
                    ((DistantMachine )machines.get(distantHost)).serviceOn = false;
                    if (debugMode)
                    {
                        System.out.println("Socket removed.");
                    }
                }
                return 0;
            }
        } else
        {
            return (2); // Or an exception ???
        }
    }
    
    public synchronized int requestChannelUse(String channelName, String ownerName, Object reference)
    {
        return (theCodec.attachToChannel(channelName, ownerName, reference));
    }
    
    public synchronized  int requestChannelUse(String channelName, String ownerName)
    {
        // One module want us to keep trace of a channel
        return (theCodec.attachToChannel(channelName, ownerName, this));
    }
    
    public synchronized void receiveDataFromChannel(net.ChannelResult result)
    {
        // If one module want to use one channel result without implementing the FacilityUser interface,
        // We store the result here after one call was made for requestChannelUse(String channelName, String ownerName).
        resultsFromChannel.add(result);
    }
    
    public synchronized void addMachine(DistantMachine oneMachine)
    {
        String name = oneMachine.getHostName();
        String nameToTest = name.toString();
        int iHost = 1;
        while (machines.containsKey(nameToTest))
        {
                nameToTest = name + '['+ iHost +']'; // We have an other time the same host
                iHost++;
        }
        machines.put(nameToTest, oneMachine);
    }
    /**
     * @param endRequested The endRequested to set.
     */
    public void endRequested()
    {
        this.endRequested = true;
    }

	public SocketPool getSocketPool() {
		return theSocketPool;
	}
}