/*
 * Player.java
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
 * A player representation on the chat environment.
 *
 * @author Paulo
 */
public class Player implements ChatEvent {

    /**
     * The ammount of time a player can stay without checking for new events.
     */
    public static final int SESSION_TIMEOUT = 5 * 60 * 1000;

    private static final Map allplayers = new HashMap();
    
    // debug
    private static final boolean DEBUG = false;

    private final String name;
    private int lastMessageId;
    private final List friends;
    private final LinkedList messages;
    private final List eventQueue;
    private long lastAccess;

    /**
     * Initialize a player object
     *
     * @param nick player to be initialized
     */
    public static void startSession(String nick) {
        if(DEBUG) System.out.println("Player.startSession(\"" + nick + "\")");
        synchronized(allplayers) {
            Player player = (Player) allplayers.get(nick);
            if(player == null) {
                // new player
                player = new Player(nick);
                // load player data
                PersistentPlayer.loadPlayerData(player);
                // notify player friends about us being online...
                Player friend;
                Iterator i = allplayers.keySet().iterator();
                while(i.hasNext()) {
                    friend = (Player) allplayers.get(i.next());
                    if(friend.friends.contains(nick)) {
                        // we are friends
                        friend.notifyEvent(ChatEvent.EVENT_FRIEND_ONLINE, nick);
                    }
                }
                allplayers.put(nick, player);
            }
        }
    }

    /**
     * Finalize a player object
     *
     * @param nick player to be finalized
     */
    public static void endSession(String nick) {
        if(DEBUG) System.out.println("Player.endSession(\"" + nick + "\")");
        synchronized(allplayers) {
            Player player = (Player) allplayers.get(nick);
            if(player != null) {
                allplayers.remove(nick);
                // notify player friends about us being online...
                Player friend;
                Iterator i = allplayers.keySet().iterator();
                while(i.hasNext()) {
                    friend = (Player) allplayers.get(i.next());
                    if(friend.friends.contains(nick)) {
                        // we are friends
                        friend.notifyEvent(ChatEvent.EVENT_FRIEND_OFFLINE, nick);
                    }
                }
                // clean objects
                player.friends.clear();
                player.messages.clear();
                player.eventQueue.clear();
                player.lastMessageId = 0;
            }
        }

    }

    /**
     * Clean up method for cleaner thread.
     */
    public static void cleanUp() {
        if(DEBUG) System.out.println("Player.cleanUp()");
        synchronized(allplayers) {
            Iterator i = allplayers.keySet().iterator();
            while(i.hasNext()) {
                endSession((String)i.next());
            }
        }
    }

    /**
     * Gets an online player.
     *
     * @param nick player to be found
     * @return a loaded player
     */
    public static Player getPlayer(String nick) {
        //if(DEBUG) System.out.println("Player.getPlayer(\"" + nick + "\")");
        synchronized(allplayers) {
            return (Player) allplayers.get(nick);
        }
    }

    /**
     * Creates a new instance of Player
     */
    private Player(String nick) {
        this.name = nick;
        this.lastMessageId = 0;
        this.friends = new LinkedList();
        this.messages = new LinkedList();
        this.eventQueue = new LinkedList();
        this.lastAccess = System.currentTimeMillis();
    }

    /**
     * Return a collection of formated message objects available to be read by the player.
     *
     * @return collection of messages
     */
    public String getPrivateMessages() {
        if(DEBUG) System.out.println("Player[" + getName() + "].getPrivateMessages()");
        StringBuffer output = new StringBuffer();
        synchronized(messages) {
            if(DEBUG) System.out.println(messages.size());
            for(int i=0; i<messages.size(); i++) {
                Message message = (Message) messages.get(i);
                output.append(message.format() + ";");
                if(DEBUG) System.out.println(output.toString());
            }
            messages.clear();
        }
        return output.toString();
    }

    /**
     * Add a private message to this player.
     *
     * @param message a message to be added
     */
    public void addMessage(Message message) {
        if(DEBUG) System.out.println("Player[" + getName() + "].addMessage(\"" + message + "\")");
        // set the new message id
        message.setId(++lastMessageId);
        synchronized(messages) {
            if(DEBUG) System.out.println(messages.size());
            if(DEBUG) System.out.println(message.format());
            messages.add(message);
            if(DEBUG) System.out.println(messages.size());
        }
        // notify about a new message...
        notifyEvent(ChatEvent.EVENT_NEW_PRIVATE_MESSAGE, message.getFrom());
    }

    /**
     * Add a friend to the player's friend list
     *
     * @param nick the friends nick name
     */
    public void addFriend(String nick) {
        if(DEBUG) System.out.println("Player[" + getName() + "].addFriend(\"" + nick + "\")");
        synchronized(friends) {
            friends.add(nick);
            // add it to the database
            PersistentPlayer.insertPlayerFriend(getName(), nick);
        }
        // if the friend is online, notify him/her
        Player friend = getPlayer(nick);
        if(friend != null) {
            friend.notifyEvent(ChatEvent.EVENT_NEW_FRIEND_REQUEST, name);
        }
    }

    /**
     * Add a friend to the player's friend list from the DB
     *
     * @param nick the friends nick name
     */
    void loadFriend(String nick) {
        if(DEBUG) System.out.println("Player[" + getName() + "].loadFriend(\"" + nick + "\")");
        synchronized(friends) {
            friends.add(nick);
        }
    }

    /**
     * Remove the friend from the friend list.
     *
     * @param nick the friend nick name
     */
    public void removeFriend(String nick) {
        if(DEBUG) System.out.println("Player[" + getName() + "].removeFriend(\"" + nick + "\")");
        synchronized(friends) {
            friends.remove(nick);
            // remove from database too
            PersistentPlayer.deletePlayerFriend(getName(), nick);
        }
    }

    /**
     * Returns all unread events since last time.
     *
     * @return collection of events
     */
    public String getEvents() {
        if(DEBUG) System.out.println("Player[" + getName() + "].getEvents()");
        this.lastAccess = System.currentTimeMillis();
        StringBuffer output = new StringBuffer();
        synchronized(eventQueue) {
            for(int i=0; i<eventQueue.size(); i++) {
                output.append(((String)eventQueue.get(i)) + ";");
            }
            eventQueue.clear();
        }
        return output.toString();
    }

    /**
     * Gets a collection of friends.
     *
     * @return collection of friends
     */
    public String getFriends() {
        if(DEBUG) System.out.println("Player[" + getName() + "].getFriends()");
        StringBuffer output = new StringBuffer();
        synchronized(friends) {
            for(int i=0; i<friends.size(); i++) {
                String nick = (String) friends.get(i);
                synchronized(allplayers) {
                    output.append((allplayers.get(nick)==null?"0":"1") + ":" + nick + ";");
                }
            }
        }
        return output.toString();
    }

    /**
     * Test if the player has read the event queue in a centain ammount of time.
     *
     * @return true if player is using the object
     */
    public boolean isAlive() {
        if(DEBUG) System.out.println("Player[" + getName() + "].isAlive()");
        return (System.currentTimeMillis() - lastAccess) > SESSION_TIMEOUT;
    }

    /**
     * Handle new notifications.
     *
     * @param event event to be notified
     * @param name event parameter
     */
    public void notifyEvent(int event, String name) {
        if(DEBUG) System.out.println("Player[" + getName() + "].notifyEvent(" + event + ", \"" + name + "\")");
        synchronized(eventQueue) {
            String _event = event + ":" + (name==null?"":name);
            // assure that we do not have duplicated events
            if(!eventQueue.contains(_event)) {
                eventQueue.add(_event);
            }
        }
    }

    /**
     * Return the name for this player
     *
     * @return name of the player
     */
    public String getName() {
        return this.name;
    }
}
