/*
 * Created on Mar 23, 2005
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

import java.util.ArrayList;

/**
 * "Living" Description of the distant machines, built by DispatchCommand but used for the communication by the CommManager.
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class DistantMachine {
    // Definition of a distant machine
    String hostName;
    String mmroName; // Calculated ???
    String type; // Not supposed to be use, just for information (justify the String type)
    boolean endOfConnection = false;
    boolean serviceOn = false; // Are the service and the command sockets active for this machine ?
    boolean commandOn = false;
    
    net.CommandSocket myCommandSocket; // Just to keep a trace (the architecture now allows to use them separated, but it is maybe better to not do so now)
    net.ServiceSocket myServiceSocket;
    
    ArrayList commands = new ArrayList();
    ArrayList services = new ArrayList();
    
    /**
     * @param hostName
     * @param mmroName
     * @param type
     */
    public DistantMachine(String hostName, String mmroName, String type)
    {
        this.hostName = hostName;
        this.mmroName = mmroName;
        this.type = type;
    }
    /**
     * @return Returns the commandOn.
     */
    public boolean isCommandOn()
    {
        return commandOn;
    }
    /**
     * @param commandOn The commandOn to set.
     */
    public void setCommandOn(boolean commandOn)
    {
        this.commandOn = commandOn;
    }
    /**
     * @return Returns the commands.
     */
    public ArrayList getCommands()
    {
        return commands;
    }
    /**
     * @param commands The commands to set.
     */
    public void setCommands(ArrayList commands)
    {
        this.commands = commands;
    }
    /**
     * @return Returns the endOfConnection.
     */
    public boolean isEndOfConnection()
    {
        return endOfConnection;
    }
    /**
     * @param endOfConnection The endOfConnection to set.
     */
    public void setEndOfConnection(boolean endOfConnection)
    {
        this.endOfConnection = endOfConnection;
    }
    /**
     * @return Returns the myCommandSocket.
     */
    public net.CommandSocket getMyCommandSocket()
    {
        return myCommandSocket;
    }
    /**
     * @param myCommandSocket The myCommandSocket to set.
     */
    public void setMyCommandSocket(net.CommandSocket myCommandSocket)
    {
        this.myCommandSocket = myCommandSocket;
    }
    /**
     * @return Returns the myServiceSocket.
     */
    public net.ServiceSocket getMyServiceSocket()
    {
        return myServiceSocket;
    }
    /**
     * @param myServiceSocket The myServiceSocket to set.
     */
    public void setMyServiceSocket(net.ServiceSocket myServiceSocket)
    {
        this.myServiceSocket = myServiceSocket;
    }
    /**
     * @return Returns the serviceOn.
     */
    public boolean isServiceOn()
    {
        return serviceOn;
    }
    /**
     * @param serviceOn The serviceOn to set.
     */
    public void setServiceOn(boolean serviceOn)
    {
        this.serviceOn = serviceOn;
    }
    /**
     * @return Returns the services.
     */
    public ArrayList getServices()
    {
        return services;
    }
    /**
     * @param services The services to set.
     */
    public void setServices(ArrayList services)
    {
        this.services = services;
    }
    /**
     * @return Returns the hostName.
     */
    public String getHostName()
    {
        return hostName;
    }
    /**
     * @return Returns the mmroName.
     */
    public String getMmroName()
    {
        return mmroName;
    }
    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }
}
