/*
 * Chatroom.java
 *
 * Created on 6 de Agosto de 2005, 14:04
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

import java.util.*;

/**
 * Chatroom class represents a group where players can chat together. Once a player joins the room a single message sent to the room will be readed by all their listeners.
 *
 * @author Paulo <pmlopes@gmail.com>
 * @version 1.0
 */
public class Chatroom {
    
    // holds all rooms
    private static final Map allrooms = new HashMap();
    
    /**
     * Defines the maximum number of messages a room will hold until the old ones start being discarded.
     */
    public static final int MAX_AVAILABLE_MESSAGES = 10;
    
    private final LinkedList messages;
    private final HashMap lastRead;
    
    // room name
    private final String name;
    // internal message id for finding which listener haven't receive old messages
    private int lastMessageId;
    
    /**
     * Returns the chatroom for a given name.
     * If the room doesn't exist a empty one is created.
     *
     * @param name the room to be created
     * @return A already existing room reference or a new reference for a new
     *     room
     */
    public static Chatroom getChatroom(String name) {
        synchronized(allrooms) {
            Chatroom room = (Chatroom) allrooms.get(name);
            if(room == null) {
                room = new Chatroom(name);
                allrooms.put(name, room);
            }
            return room;
        }
    }
    
    /**
     * Create a new Chatroom with a given name.
     *
     * @param name of the new chatroom
     */
    private Chatroom(String name) {
        this.name = name;
        this.lastMessageId = 0;
        this.messages = new LinkedList();
        this.lastRead = new HashMap();
    }
    
    /**
     * Adds a listener to the room.
     * A listener will then receive notifications about new messages on the room.
     *
     * @param listener the listener to be registered in the notifications
     */
    public void addChatEventListener(ChatEvent listener) {
        synchronized(lastRead) {
            addMessage(
                    new Message(Message.MESSAGE_CONTROL, "#" + name, listener.getName() + " has join the room!", true)
                    );
            lastRead.put(listener, new Integer(lastMessageId));
        }
    }
    
    /**
     * A listener is removed from the notification list.
     * After this moment a leave message is added and the listener stops being
     * notified.
     *
     * @param listener the listener to unregister from the notifications
     */
    public void removeChatEventListener(ChatEvent listener) {
        synchronized(lastRead) {
            lastRead.remove(listener);
            addMessage(
                    new Message(Message.MESSAGE_CONTROL, "#" + name, listener.getName() + " has left the room!", true)
                    );
        }
        // clean data
        if(size() == 0) clean();
    }
    
    /**
     * Retreive all new messages (since last call) for this listener.
     *
     * @param listener the listener to retreive the unread messages
     * @return a formated message list.
     */
    public String getMessages(ChatEvent listener) {
        Integer last;
        int newLast = lastMessageId;
        StringBuffer output = new StringBuffer();
        
        synchronized(lastRead) {
            last = (Integer) lastRead.get(listener);
            if(last == null) {
                // means that this listener is not subscribed
                return output.toString();
            }
        }
        // search for new messages on the message list
        synchronized(messages) {
            for(int i=0; i<messages.size(); i++) {
                Message message = (Message) messages.get(i);
                // check if we already read this one
                if(message.getId() > last.intValue()) {
                    output.append(message.format() + ";");
                    newLast = message.getId();
                }
            }
        }
        // now just save the new last read value
        synchronized(lastRead) {
            lastRead.put(listener, new Integer(newLast));
        }
        // return the formated string
        return output.toString();
    }
    
    /**
     * Add a new message to this room.
     *
     * @param message a new message to be added
     */
    public void addMessage(Message message) {
        // set the new message id
        message.setId(++lastMessageId);
        synchronized(messages) {
            messages.addLast(message);
            if(messages.size() > MAX_AVAILABLE_MESSAGES) {
                messages.removeFirst();
            }
        }
        // notify listeners...
        notifyNewMessage();
    }
    
    /**
     * Check the number of registered listeners for this room.
     *
     * @return total of listeners
     */
    public int size() {
        return this.lastRead.size();
    }
    
    /**
     * Clean up this room for deletion.
     */
    public void clean() {
        this.messages.clear();
        this.lastRead.clear();
        this.lastMessageId = 0;
    }
    
    /**
     * Helper to notify all listeners.
     */
    private void notifyNewMessage() {
        // send a new event to each listener in the room
        synchronized(lastRead) {
            Iterator i = lastRead.keySet().iterator();
            while(i.hasNext()) {
                ChatEvent listener = (ChatEvent) i.next();
                listener.notifyEvent(ChatEvent.EVENT_NEW_CHATROOM_MESSAGE, name);
            }
        }
    }
    
    /**
     * Clean up method for the cleaner thread. This method will remove zombie listeners and remove 0 length rooms from memory.
     */
    public static void cleanUp() {
        synchronized(allrooms) {
            Iterator i = allrooms.keySet().iterator();
            while(i.hasNext()) {
                Chatroom chatroom = (Chatroom) allrooms.get(i.next());
                synchronized(chatroom.lastRead) {
                    Iterator i2 = chatroom.lastRead.keySet().iterator();
                    while(i2.hasNext()) {
                        ChatEvent listener = (ChatEvent) i2.next();
                        if(!listener.isAlive()) {
                            chatroom.removeChatEventListener(listener);
                        }
                    }
                }
            }
        }
    }
}
