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

/**
 * This interface defines the needed method for the coder/decoder class. This last is the link between this Java implementation
 * and the network protocole used. It also (and mainly... ) abstract the network reality to offer a virtually infinite number
 * of channel over the net...
 * 
 * @author Alain Becam
 * 
 * TODO Review and redo everything... But first secure the connection and adjust the robustess of the whole.
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CodecCommon
{
    // Client:
    /**
     * @param distantChannel
     * @param ownerName
     * @return a channel composed name, typically "ownerName+distantChannel", if not changed by the distant Codec
     */
    public int attachToChannel(String distantChannel, String ownerName, Object reference); // Try to attach to a distant private channel
    
    public boolean isChannelExisting(String distantChannel, String ownerName);
    
    public String channelState(String distantChannel, String ownerName);
    
    public int addDistantChannel(String distantChannel, String ownerName); // From the service server.
    // Server:
    
    public String setChannel(String privateChannel, String lockName); // Set a private channel, return the name if different (ie. the channel already exists)
    // The lockName is used to avoid another actor to destruct this channel
    
    public int removeChannel(String privateChannel, String lockName); // Destruct a private channel.
    
    // Common:
    
    public void receive(ChannelResult theResult); // Receive and "decode" the binary stream...
    
    public void send(String privateChannel, char[] objectOut); // Give a receive object on a private channel...
}
