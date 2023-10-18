/*
 * Created on Jun 4, 2005
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

/**
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommandSet
{
    String callerName;
    String moduleName;
    int instance;
    String methodName;
    Object[] parameters;
    String lockCode; // If used. We have to add later a management of badly used lock (so we unlock the module after a while)
    // Also, we have to add the management of the joker instances : any-free, anyfree-or-new, forcethisone
    Object owner; // The owner of the command, will be used to give the result to dispatchOrder.
    
    
    /**
     * @return Returns the callerName.
     */
    public String getCallerName()
    {
        return callerName;
    }
    /**
     * @param callerName The callerName to set.
     */
    public void setCallerName(String callerName)
    {
        this.callerName = callerName;
    }
    /**
     * @return Returns the instance.
     */
    public int getInstance()
    {
        return instance;
    }
    /**
     * @param instance The instance to set.
     */
    public void setInstance(int instance)
    {
        this.instance = instance;
    }
    /**
     * @return Returns the lockCode.
     */
    public String getLockCode()
    {
        return lockCode;
    }
    /**
     * @param lockCode The lockCode to set.
     */
    public void setLockCode(String lockCode)
    {
        this.lockCode = lockCode;
    }
    /**
     * @return Returns the methodName.
     */
    public String getMethodName()
    {
        return methodName;
    }
    /**
     * @param methodName The methodName to set.
     */
    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }
    /**
     * @return Returns the moduleName.
     */
    public String getModuleName()
    {
        return moduleName;
    }
    /**
     * @param moduleName The moduleName to set.
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }
    /**
     * @return Returns the owner.
     */
    public Object getOwner()
    {
        return owner;
    }
    /**
     * @param owner The owner to set.
     */
    public void setOwner(Object owner)
    {
        this.owner = owner;
    }
    
    /**
     * Set one set of command through InterpretOrders.MethodDesc
     * Use the default instance (0)
     * @param owner The description of the method.
     */
    public void setMethod(InterpretOrders.MethodDesc descOfMethod)
    {
    	this.setModuleName(descOfMethod.getClassName());
    	this.setMethodName(descOfMethod.getMethodName());
    	this.setInstance(0);
    	this.setParameters(descOfMethod.getParameters());
    }
    
    /**
     * Set one set of command through InterpretOrders.MethodDesc
     * Use the given instance number
     * @param owner The description of the method.
     * @param nbInstance The instance number wanted.
     */
    public void setMethod(InterpretOrders.MethodDesc descOfMethod, int nbInstance)
    {
    	this.setModuleName(descOfMethod.getClassName());
    	this.setMethodName(descOfMethod.getMethodName());
    	this.setInstance(nbInstance);
    	this.setParameters(descOfMethod.getParameters());
    }
    
    /**
     * @return Returns the parameters.
     */
    public Object[] getParameters()
    {
        return parameters;
    }
    /**
     * @param parameters The parameters to set.
     */
    public void setParameters(Object[] parameters)
    {
        this.parameters = parameters;
    }
}