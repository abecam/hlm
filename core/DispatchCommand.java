/*
 * Created on Feb 21, 2005
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
import java.util.HashMap;
import java.util.Iterator;

import modulesrepository.FacilityUser;

/**
 * This class is aimed to dispatch the command from this station.
 * It allows the use of group, defined in meta-data ("group nameofgroup: nameofhost, nameofhost, ...")
 * And the use of alias, one alias for one group (to be discussed if an alias has to be defined, as it is a sort of group)
 * 	--- Migth be good to use XML for that (as it is still very readable) ---
 * There exist also predefined groups: allPcClients, allPcServers, allMobileClients, allMobileServers
 * 
 * @author Alain Becam
 * 
 * TODO clean everything... Use a better format (XML ?), but it is the job of ManageMachineList.
 */

public class DispatchCommand
{
    CommManager theCommManager;
    HashMap groupList = new HashMap(); // Group -> machines (arraylist of string)
    HashMap aliasList = new HashMap(); // Alias -> machine
    ArrayList machinesList = new ArrayList(); // Just the list of machine, see the class below
    HashMap resultSets = new HashMap();
    
    boolean debugMode = false;
    
    public static void main(String[] args)
    {
        // For test.
    }
    
    /**
     * 
     */
    public DispatchCommand()
    {
        // As we are correctly initialised AFTER our construction, please do not ask for methods here (wait at least for the setCommManager external call)
    }
    public void setCommManager(CommManager theCommManager)
    {
        this.theCommManager = theCommManager;
    }
    
    /**
     * Receive a result from the commManager, give it back directly or not to the caller
     * This is a not-so-good approach to send the command, as we decode the commandSet before we call the socket
     * There is no economy of CPU (because we store the commandSet for each machine, there might be an economy to
     * Store the String representation then (and so to do the transcript only one time), but it is a bad idea anyway.
     * In fact, we want to be able to NOT change too much the CommManager and to change if needed the sockets.
     * And the transcript from the commandSet to the string IS sort of low-level mechanisms and might be enhanced soon
     * Furthermore (but it is not only because of that), we do not inform the distant module about the caller...
     * And it is a problem of security (trusted code), even if we are far from this goal anyway
     * @param commandSet from the CommManager, which allows us to keep a trace of the execution (managed locally)
     * @param result from the distant call.
     */
    public void pushResult(CommandSet commandSet, String result)
    {
        if (debugMode)
        {
            System.out.println("Ready to get one result");
        }
        if (commandSet.owner != this)
        {
            if (debugMode)
            {
                System.out.println("This is not! It is: "+commandSet.owner);
            }
            ((FacilityUser )(commandSet.owner)).distantCallResultListener(result);
            if (debugMode)
            {
                System.out.println("Finished to get one result");
            }
        }
        else
        {
            if (debugMode)
            {
                System.out.println("Is this a group ?");
            }
            // We have a group, we store the result
            if (resultSets.containsKey(commandSet.callerName))
            {
                if (debugMode)
                {
                    System.out.println("It is a group ?");
                }
                ArrayList theCurrentSet = ( ArrayList) ( resultSets.get(commandSet.callerName));
                theCurrentSet.add(result);
            }
        }
        if (debugMode)
        {
            System.out.println("Finished");
        }
    }
    
    
    /**
     * Return a result for a caller, this last has to give its id-name.
     * @param forName
     * @return The arraylist of results
     */
    public synchronized ArrayList getResult(String forName)
    {
        if (resultSets.containsKey(forName))
        {
            return ((ArrayList )resultSets.get(forName));
        }
        else
        {
            return null;
        }
    }
    
    public synchronized int ask4DistantCommand(String distantMachine, CommandSet commandSet, Object reference)
    {
        if (groupList.containsKey(distantMachine))
        {
            // It is a group, we send the command to all the group, and WE receive the result
            ArrayList newResultSet = new ArrayList();
            resultSets.put(commandSet.callerName, newResultSet);
            commandSet.owner = this;
            ArrayList machinesGroup = (ArrayList )(groupList.get(distantMachine));
            int result = 0;
            for (Iterator i = machinesGroup.iterator() ; i.hasNext() ; )
            {
                int stepResult = theCommManager.sendCommand((String )i.next(), commandSet);
                result += stepResult;
            }
            return result;
        }
        else
        {
            // Predefined groups
            // allPcClients, allPcServers, allMobileClients, allMobileServers
            if (distantMachine.equals("allPcClients"))
            {
                // It is a group, we send the command to all the group, and WE receive the result
                ArrayList newResultSet = new ArrayList();
                resultSets.put(commandSet.callerName, newResultSet);
                commandSet.owner = this;
                
                int result = 0;
                for (Iterator i = machinesList.iterator() ; i.hasNext() ; )
                {
                    MachineDesc currentDesc = ((MachineDesc )(i.next()));
                    if (currentDesc.type.equals("PcClient"))
                    {
                        int stepResult = theCommManager.sendCommand(currentDesc.hostName, commandSet);
                        result += stepResult;
                    }
                }
                return result;
            }
            if (distantMachine.equals("allMobileClients"))
            {
                // It is a group, we send the command to all the group, and WE receive the result
                ArrayList newResultSet = new ArrayList();
                resultSets.put(commandSet.callerName, newResultSet);
                commandSet.owner = this;
                
                int result = 0;
                for (Iterator i = machinesList.iterator() ; i.hasNext() ; )
                {
                    MachineDesc currentDesc = ((MachineDesc )(i.next()));
                    if (currentDesc.type.equals("MobileClient"))
                    {
                        int stepResult = theCommManager.sendCommand(currentDesc.hostName, commandSet);
                        result += stepResult;
                    }
                }
                return result;
            }
            if (distantMachine.equals("allPcServers"))
            {
                // It is a group, we send the command to all the group, and WE receive the result
                ArrayList newResultSet = new ArrayList();
                resultSets.put(commandSet.callerName, newResultSet);
                commandSet.owner = this;
                
                int result = 0;
                for (Iterator i = machinesList.iterator() ; i.hasNext() ; )
                {
                    MachineDesc currentDesc = ((MachineDesc )(i.next()));
                    if (currentDesc.type.equals("PcServer"))
                    {
                        int stepResult = theCommManager.sendCommand(currentDesc.hostName, commandSet);
                        result += stepResult;
                    }
                }
                return result;
            }
            if (distantMachine.equals("allMobileServers"))
            {
                // It is a group, we send the command to all the group, and WE receive the result
                ArrayList newResultSet = new ArrayList();
                resultSets.put(commandSet.callerName, newResultSet);
                commandSet.owner = this;
                
                int result = 0;
                for (Iterator i = machinesList.iterator() ; i.hasNext() ; )
                {
                    MachineDesc currentDesc = ((MachineDesc )(i.next()));
                    if (currentDesc.type.equals("MobileServer"))
                    {
                        int stepResult = theCommManager.sendCommand(currentDesc.hostName, commandSet);
                        result += stepResult;
                    }
                }
                return result;
            }
            if (aliasList.containsKey(distantMachine))
            {
                // It is an alias, just replace it
                String wantedMachine = (String )aliasList.get(distantMachine);
                return (theCommManager.sendCommand(wantedMachine, commandSet));
            }
            else
            {
                return(theCommManager.sendCommand(distantMachine, commandSet));
            }
        }
    }
    
    public int buildMachineList()
    {
        boolean notEnd = true;

        utils.ManageMachineList newManager = new utils.ManageMachineList();
        StringBuffer doc = newManager.readDocument();
        while (notEnd)
        {
            String section = newManager.extractSection(doc);
            MachineDesc oneDesc = newManager.extractMachineDesc(section);
            machinesList.add(oneDesc);
            // Alias list creation
            if ( ( ( (String )oneDesc.getAlias()).length() > 0 ) && (oneDesc.getAlias() != null))
            {
                if (aliasList.containsKey(oneDesc.getAlias()))
                {
                    // Problem !!!
                    // But better to do nothing... for now !!!
                }
                else
                {
                    aliasList.put(oneDesc.getAlias(), oneDesc.getHostName());
                }
            }
            ArrayList oneGroupe = oneDesc.getGroupe();
            for (int i = 0; i < oneGroupe.size(); i++)
            {
                // Groupe list creation
                if ( (oneGroupe.get(i) != null) && (((String ) oneGroupe.get(i)).length() > 0))
                {
                   if (groupList.containsKey(oneGroupe.get(i)))
                   {
                       ArrayList machineList = (ArrayList )groupList.get(oneGroupe.get(i));
                       machineList.add(oneDesc.getHostName());
                   }
                   else
                   {
                       ArrayList machineList = new ArrayList();
                       machineList.add(oneDesc.getHostName());
                       groupList.put(oneGroupe.get(i), machineList);
                   }
                }
            }
            System.out.println("One machine on alias: "+oneDesc.getAlias()+", groupe(s): "+oneDesc.getGroupe()+", address:"+oneDesc.getHostName());
            // Machines list for CommManager
            DistantMachine oneDistantMachine = new DistantMachine(oneDesc.getHostName(), oneDesc.getHostName() + oneDesc.getType(), oneDesc.getLanguage());
            theCommManager.addMachine(oneDistantMachine);
            if (doc.indexOf("Machine:") == -1)
            {
                notEnd = false;
            }
        }
        return 0;
    }
}
