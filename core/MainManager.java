/*
 * Created on Feb 1, 2005
 *
 * TODO A lot !
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

import java.util.HashMap;


/**
 * Start the other managers, ensure the right concurrency management (VERY simple monitor, might work well anyway)
 * Also keep a set of reference which are passed to the module, so you can link the module with your architecture easily.
 * This set of reference must be fixed before your start the main manager
 * @author Alain Becam
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MainManager {
	private CommManager theCommManager; // Needed to keep one set of servers/sockets
	private ManageInstances theInstanceManager; // Needed to keep one unique list of module
	private DispatchCommand theDispatcher;  // Not mandatory
	private InterpretOrders theInterpreter; // Not mandatory
	private ManageModulesList moduleListManager; // Not mandatory too
	HashMap usedModules = new HashMap();
	
	public Object externalRef[]; // A very useful set of references, which is passed to the module via the facility interface
	
	public class moduleInUse
	{
	    public String moduleName;
	    public String lockCode;
	    public boolean inUse;
	}
	
	public MainManager(Object [] references)
	{
	    this.externalRef = references;
		theInstanceManager = new ManageInstances(references);
		theDispatcher = new DispatchCommand();
		theInterpreter = new InterpretOrders();
		moduleListManager = new ManageModulesList();
		// The communication manager need the other managers to work.
		theCommManager = new CommManager(theInstanceManager,theDispatcher,theInterpreter,moduleListManager,this);
		// And we inform everybody...
		// The dispatcher need to know the CommManager
		theDispatcher.setCommManager(theCommManager);
		theDispatcher.buildMachineList();
		// TheInstanceManager need to know the communication manager (it gives the transparent use of channels/services/commands to the modules)
		theInstanceManager.setCommManager(theCommManager);
		// the interpreter need to know the dispatcher, as it has to dispatch the distant commands.
		theInterpreter.setCommManager(theCommManager);
	}
	
	public static void main(String[] args) {
		MainManager myManager = new MainManager(null);
	}
	
	public synchronized boolean requestModuleUse( String moduleName , int instance, String lockCode) throws Exception
	{
	    // Return true if the module is free, if the lockCode is the same or if their is no lockCode ("")
	    // Return false otherwise
	    if (theInstanceManager.giveInstanceNb(moduleName) >= instance)
	    {
	        String key = new String(moduleName+instance);
	        if (usedModules.containsKey(key))
	        {
	           moduleInUse oneModuleIU = (moduleInUse )usedModules.get(key);
	           if (!oneModuleIU.inUse)
	           {
	               oneModuleIU.lockCode = lockCode;
	               oneModuleIU.inUse = true;
	               return true;
	           }
	           else
	           {
	               if ( ((oneModuleIU.lockCode).equals(lockCode)) || ((oneModuleIU.lockCode).equals("")) )
	               {
	                   return true;
	               }
	               else
	               {
	                   return false;
	               }
	           }
	        }
	        else
	        {
	            // No request for now, will do one now.
	            moduleInUse oneModuleIU = new moduleInUse();
	            oneModuleIU.moduleName = moduleName;
                oneModuleIU.lockCode = lockCode;
                oneModuleIU.inUse = true;
                usedModules.put(key, oneModuleIU);
                
                return true; 
	        }
	    }
	    else
	    {
	        throw new Exception("No module for this name and/or this instance exists");
	    }
	}
	
	public synchronized boolean releaseModule(String moduleName, int instance, String lockCode) throws Exception
    {
        String key = new String(moduleName + instance);
        if (usedModules.containsKey(key))
        {
            moduleInUse oneModuleIU = (moduleInUse) usedModules.get(key);
            if (oneModuleIU.lockCode.equals(lockCode))
            {
                oneModuleIU.inUse = false;
                return true;
            } else
            {
                return false;
            }
        } 
        else
        {
            throw new Exception("no module for this name");
        }
    }
	
	public synchronized String requestLockCode( String moduleName , int instance) throws Exception
	{
	    // Return the lock code if the module is not free
	    // Or return null
	    if (theInstanceManager.giveInstanceNb(moduleName) >= instance)
	    {
	        String key = new String(moduleName+instance);
	        if (usedModules.containsKey(key))
	        {
	           moduleInUse oneModuleIU = (moduleInUse )usedModules.get(key);
	           return (oneModuleIU.lockCode);
	        }
	        else
	        {
                return null; 
	        }
	    }
	    else
	    {
	        throw new Exception("No module for this name and/or this instance exists");
	    }
	}
	
    /**
     * @return Returns the externalRef.
     */
    public synchronized Object[] getExternalRef()
    {
        return externalRef;
    }
   
    /**
     * @return Returns the moduleListManager.
     */
    public ManageModulesList getModuleListManager()
    {
        return moduleListManager;
    }
    /**
     * @param moduleListManager The moduleListManager to set.
     */
    public void setModuleListManager(ManageModulesList moduleListManager)
    {
        this.moduleListManager = moduleListManager;
    }
    /**
     * @return Returns the theCommManager.
     */
    public CommManager getTheCommManager()
    {
        return theCommManager;
    }
    /**
     * @param theCommManager The theCommManager to set.
     */
    public void setTheCommManager(CommManager theCommManager)
    {
        this.theCommManager = theCommManager;
    }
    /**
     * @return Returns the theDispatcher.
     */
    public DispatchCommand getTheDispatcher()
    {
        return theDispatcher;
    }
    /**
     * @param theDispatcher The theDispatcher to set.
     */
    public void setTheDispatcher(DispatchCommand theDispatcher)
    {
        this.theDispatcher = theDispatcher;
    }
    /**
     * @return Returns the theInstanceManager.
     */
    public ManageInstances getTheInstanceManager()
    {
        return theInstanceManager;
    }
    /**
     * @param theInstanceManager The theInstanceManager to set.
     */
    public void setTheInstanceManager(ManageInstances theInstanceManager)
    {
        this.theInstanceManager = theInstanceManager;
    }
    /**
     * @return Returns the theInterpreter.
     */
    public InterpretOrders getTheInterpreter()
    {
        return theInterpreter;
    }
    /**
     * @param theInterpreter The theInterpreter to set.
     */
    public void setTheInterpreter(InterpretOrders theInterpreter)
    {
        this.theInterpreter = theInterpreter;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
        // TODO Auto-generated method stub
        theCommManager.endRequested();
        super.finalize();
    }
}