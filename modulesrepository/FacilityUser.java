/*
 * Created on Mar 20, 2005
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

package modulesrepository;

import java.net.InetAddress;

import core.ManageInstances;


/**
 * This interface allows the instance manager to know that the module want some informations
 * Just implement it and you can benefit of all information asked by the methods here.
 * @author Alain Becam
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface FacilityUser
{
    public void getCommManager(core.CommManager theCommManager);
    
    public void getManageInstances(ManageInstances instancesManager);
    
    public void distantCallResultListener(String result);
    // Take the result of a distant call. If not used, take the value in the dispatch command.
    public void distantMultiCallResultListener(String[] result);
    // Result from a group
    
    /*
     * This method allows to know the IP of one caller. Due to the concurrency, the caller of the following
     * method in the module can be different ! But you can lock the module using the Lock class if you
     * really need to be sure the caller is the same, until you unlock the module. Though it is quite unsafe
     * to lock a module like that (it can stay locked if something unexpected happened, for instance if the client
     * didn't call the right methods (lock-method-method-...-method-unlock), and it might happen with a network problem.
     * So you better have to use this method has a statistic help.
     */
    public void setOneCaller(InetAddress theAddress);
    /**
     * This method allows to receive data from one or more channel.
     * The module using it MUST first register itself at the CommManager : requestChannelUse(String channelName, String ownerName, Object reference (this) )
     * One module can register itself to more than one channel.
     * The result given is composed of the name of the channel, the name of the owner, the name of the data, and the binary set.
     */
    public void receiveDataFromChannel(net.ChannelResult result);
    
    /**
     * This method is used by the instance manager to give the external reference. Use it to link your architecture to yours
     * modules.
     * @param references
     */
    public void receiveExtRefs(Object[] references);
}
