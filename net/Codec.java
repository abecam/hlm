/*
 * Created on Mar 13, 2005
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

import java.util.HashMap;
import java.util.Set;

import core.CommManager;


/**
 * Finaly, this class manage the channel... A channel is a really virtual thing, one machine create a channel,
 * inform the others ones, then a module can attach to a channel (and must implement FacilityUser for that), the
 * sender must be informed then (new client), and send the info to all module wanting it. Typically, only one
 * module on a local computer use one channel. We don't manage 
 * 
 * Obsolete comment to put into the "real" codec class;
 * 			This class is the "low" level coder-decoder for the communication.
 *          It is supposed to be changed soon... So we define an interface,
 *          which describes the needed functions. Anyway, Java offers us already
 *          high-levels sockets, so this class is more a support for futur
 *          protocol enhancement...
 * @author Alain Becam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

// Distant connection: In this first version, we will limite the number of
// socket (threads), so we have to find a way to realocate them if needed
// Servers : the distant computers have first to identify themself, to attach to
// a channel and then we accept everything supported by protocole (we classes
// using the protocole have to take care of what they do)
// The communication can be 1-N, one channel owned can be used by several other
// clients.
public class Codec implements CodecCommon
{
    CommManager theCommManager;
    
    // Owned channels collections and classes
    HashMap ownedChannels = new HashMap();
    
    public class Channel
    {
        String channelName;
        HashMap channelClient = new HashMap(); // key=clientname+machinename
    }
    public class ChannelClient
    {
        String clientName;
        String machinename;
        String state;
    }
    // Used channel collections and classes, key = ownername+channelname
    HashMap usedChannels = new HashMap();
    
    public class UsedChannel
    {
        String channelName;
        String ownerName;
        HashMap channelUsers = new HashMap();
        /**
         * @param channelName
         * @param ownerName
         */
        public UsedChannel(String channelName, String ownerName)
        {
            this.channelName = channelName;
            this.ownerName = ownerName;
        }
    }
    
    public class ChannelUser
    {
        Object reference; // One reference on a module implementing receiveDataFromChannel(), interface FacilityUser
    }
   
    /* (non-Javadoc)
     * @see mmrocom.CodecCommon#channelState(java.lang.String, java.lang.String)
     */
    public String channelState(String distantChannel, String ownerName)
    {
        // TODO Auto-generated method stub
        if (theCommManager.isLinked(distantChannel))
        {
            String answer = theCommManager.askDistantService(ownerName,"channelstateof\nstart\n"+distantChannel+"\nend.");
            return (answer);
        }
        else
            return null;
    }
    /*
     * (non-Javadoc)
     * 
     * @see mmrocom.CodecCommon#ask(java.lang.String, java.lang.Object)
     */
    public void send(String privateChannel, char[] objectOut)
    {
        if (ownedChannels.containsKey(privateChannel))
        {
            HashMap clientList = (HashMap )ownedChannels.get(privateChannel);
        
            Set clientSet = clientList.entrySet();
            for (java.util.Iterator i = clientSet.iterator(); i.hasNext() ; )
            {
                ChannelClient oneClient = ((ChannelClient )(i.next()));
                ChannelSocket newSocketChannel = new ChannelSocket(oneClient.machinename,oneClient.clientName, privateChannel, objectOut, this, theCommManager);
            }
        }

    }
    /*
     * (non-Javadoc)
     * 
     * @see mmrocom.CodecCommon#removeChannel(java.lang.String,
     *      java.lang.String)
     */
    public int removeChannel(String privateChannel, String lockName)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    /*
     * (non-Javadoc)
     * 
     * @see mmrocom.CodecCommon#setChannel(java.lang.String, java.lang.String)
     */
    public String setChannel(String privateChannel, String lockName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see mmrocom.CodecCommon#attachToChannel(java.lang.String,
     *      java.lang.String)
     */
    public int attachToChannel(String distantChannel, String ownerName, Object reference)
    {
        // TODO Auto-generated method stub
        if (usedChannels.containsKey(ownerName+distantChannel))
        {
            UsedChannel requestedChannel = (UsedChannel )(usedChannels.get(ownerName+distantChannel));
            ChannelUser oneNewUser = new ChannelUser();
            oneNewUser.reference = reference;
            requestedChannel.channelUsers.put(oneNewUser, oneNewUser);
            return 0;
        }
        else
        {
            return 1;
        }
    }
    /*
     * (non-Javadoc)
     * 
     * @see mmrocom.CodecCommon#isChannelExisting(java.lang.String,
     *      java.lang.String)
     */
    public boolean isChannelExisting(String distantChannel, String ownerName)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /*
     * (non-Javadoc)
     * 
     * @see mmrocom.CodecCommon#receive(java.lang.String, char[])
     */
    public void receive(ChannelResult oneResult)
    {
        // Need to dispatch the result to all local channel clients

    }

    /* (non-Javadoc)
     * @see mmrocom.CodecCommon#addDistantChannel(java.lang.String, java.lang.String)
     */
    public int addDistantChannel(String distantChannel, String ownerName)
    {
        // TODO Auto-generated method stub
        if (usedChannels.containsKey(ownerName + distantChannel))
        {
            return 1; // Already done
        }
        else
        {
            UsedChannel oneNewDistantChannel = new UsedChannel(ownerName, distantChannel);
            usedChannels.put(ownerName+distantChannel, oneNewDistantChannel);
            return 0;
        }
    }
}
