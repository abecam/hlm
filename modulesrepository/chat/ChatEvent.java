/*
 * ChatEvent.java
 *
 * Created on 6 de Agosto de 2005, 14:05
 *
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

package modulesrepository.chat;

/**
 * ChatEvent represents a event that can occur during the usage of the chat system.
 *
 * @author Paulo <pmlopes@gmail.com>
 * @version 1.0
 */
public interface ChatEvent {
    
    /**
     * A message has been added to a chatroom
     */
    public static final int EVENT_NEW_CHATROOM_MESSAGE = 1;
    /**
     * A player requested to be other player's friend.
     */
    public static final int EVENT_NEW_FRIEND_REQUEST   = 2;
    /**
     * A friend of the player is online.
     */
    public static final int EVENT_FRIEND_ONLINE        = 3;
    /**
     * A friend of the player went offline.
     */
    public static final int EVENT_FRIEND_OFFLINE       = 4;
    /**
     * A new private message awaits.
     */
    public static final int EVENT_NEW_PRIVATE_MESSAGE  = 5;
    
    /**
     * Returns the name for the object instance that implements this interface.
     * For example if a player implements this interface this should return the
     * player name.
     *
     * @return name
     */
    String getName();
    
    /**
     * Listener to manage the events generated from outside objects.
     * For example, a player adds a new message to the chatroom, then the chatroom
     * uses this listener to notify all other players about a new message
     * available.
     *
     * @param event to be notified
     * @param name a optional parameter for the event
     */
    void notifyEvent(int event, String name);
    
    /**
     * Boolean test for check if a listener is alive.
     *
     * @return true if alive
     */
    boolean isAlive();
}
