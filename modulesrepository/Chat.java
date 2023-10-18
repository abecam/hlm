/*
 * Chat.java
 *
 * Created on 6 de Agosto de 2005, 14:04
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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

import modulesrepository.chat.*;

/**
 * MMRO Chat module
 *
 * @author Paulo <pmlopes@gmail.com>
 * @version 1.0
 */
public class Chat {

    /**
     * Error code for success.
     */
    public static final int SUCCESS = 0;
    /**
     * Error code for error.
     */
    public static final int ERROR = 1;
    
    // debug
    private static final boolean DEBUG = false;

    // cleaner thread
    private ChatCleaner cleaner;

    /**
     * Creates a new instance of Chat
     */
    public Chat() {
        // create the cleaner thread
        //cleaner = new ChatCleaner();
        //cleaner.start();
    }

    /**
     * Create an account into the persistent system.
     *
     * @param nick nick name
     * @param password password for the nickname
     * @return error code
     */
    public int createAccount(String name, String faction, String nick, String password) {
        if(DEBUG) System.out.println("Chat.createAccount(\"" + name + "\", \"" + faction + "\", \"" + nick + "\", \"" + password + "\")");
        return PersistentPlayer.createPersistentPlayer(name, faction, nick, password);
    }

    /**
     * Login a player into the chat.
     *
     * @param name player's full name
     * @param faction player's faction
     * @param nick player nick name
     * @param password player password
     * @return error code
     */
    public int login(String name, String faction, String nick, String password) {
        if(DEBUG) System.out.println("Chat.login(\"" + name + "\", \"" + faction + "\", \"" + nick + "\", \"" + password + "\")");
        int output = PersistentPlayer.login(nick, password);
        if(output == PersistentPlayer.ERROR_NEW_NICKNAME) {
            // have a new player so add it to the database.
            output = createAccount(name, faction, nick, password);
        }
        if(output == PersistentPlayer.SUCCESS) {
            Player.startSession(nick);
        }
        return output;
    }

    /**
     * Logout a player in the chat.
     *
     * @param nick player nick name
     * @return error code.
     */
    public int logout(String nick) {
        if(DEBUG) System.out.println("Chat.logout(\"" + nick + "\")");
        Player.endSession(nick);
        // TODO: remove this nick from the chatroom listeners
        return SUCCESS;
    }

    // private message and player management

    /**
     * Get all private messages for a player.
     *
     * @param nick player nickname
     * @return formated collection of messages
     */
    public String getPrivateMessages(String nick) {
        if(DEBUG) System.out.println("Chat.getPrivateMessages(\"" + nick + "\")");
        Player player = Player.getPlayer(nick);
        if(player != null) {
            return player.getPrivateMessages();
        }
        return ";";
    }

    /**
     * Add a private message to a player
     *
     * @param fromNick who sent the message
     * @param toNick to whom the message was sent
     * @param txtmessage the message itself
     * @return error code
     */
    public int addPrivateMessage(String fromNick, String toNick, String txtmessage) {
        if(DEBUG) System.out.println("Chat.addPrivateMessage(\"" + fromNick + "\", \"" + toNick + "\", \"" + txtmessage + "\")");
        Player playerFrom = Player.getPlayer(fromNick);
        Player playerTo = Player.getPlayer(toNick);
        // both player must be online
        if(playerFrom != null && playerTo != null) {
            Message message = new Message(Message.MESSAGE_GENERAL, fromNick, txtmessage);
            playerTo.addMessage(message);
            return SUCCESS;
        }
        // can't deliver user is offline...
        return ERROR;
    }

    /**
     * Add a friend to the list
     *
     * @param fromNick who wants to add a friend
     * @param friendNick the player's new friend
     * @return error code
     */
    public int addFriend(String fromNick, String friendNick) {
        if(DEBUG) System.out.println("Chat.addFriend(\"" + fromNick + "\", \"" + friendNick + "\")");
        Player playerFrom = Player.getPlayer(fromNick);
        Player playerTo = Player.getPlayer(friendNick);
        // both player must be online or exist on database
        if(playerFrom != null && (playerTo != null || PersistentPlayer.existPlayer(friendNick))) {
            playerFrom.addFriend(friendNick);
            return SUCCESS;
        }
        // can't add user is offline...
        return ERROR;
    }

    /**
     * Remove a friend from the list
     *
     * @param fromNick who wants to remove
     * @param friendNick who to remove
     * @return error code
     */
    public int removeFriend(String fromNick, String friendNick) {
        if(DEBUG) System.out.println("Chat.removeFriend(\"" + fromNick + "\", \"" + friendNick + "\")");
        Player player = Player.getPlayer(fromNick);
        if(player != null) {
            player.removeFriend(friendNick);
            return SUCCESS;
        }
        // can't remove user is offline...
        return ERROR;
    }

    /**
     * Get all unread events
     *
     * @param nick who wants the events
     * @return collection of events
     */
    public String getEvents(String nick) {
        if(DEBUG) System.out.println("Chat.getEvents(\"" + nick + "\")");
        Player player = Player.getPlayer(nick);
        if(player != null) {
            return player.getEvents();
        }
        // can't add user is offline...
        return ";";
    }

    /**
     * Get the friends list for a player
     *
     * @param nick who wants the friends list
     * @return collection of friends
     */
    public String getFriends(String nick) {
        if(DEBUG) System.out.println("Chat.getFriends(\"" + nick + "\")");
        Player player = Player.getPlayer(nick);
        if(player != null) {
            return player.getFriends();
        }
        // can't add user is offline...
        return ";";
    }

    // chat room actions

    /**
     * Join a chat room.
     *
     * @param nick player nick name
     * @param room room to join
     * @return error code
     */
    public int joinChatroom(String nick, String room) {
        if(DEBUG) System.out.println("Chat.joinChatroom(\"" + nick + "\", \"" + room + "\")");
        Player player = Player.getPlayer(nick);
        if(player != null) {
            Chatroom chatroom = Chatroom.getChatroom(room);
            chatroom.addChatEventListener(player);
            return SUCCESS;
        }
        // can't join user is offline...
        return ERROR;
    }

    /**
     * Leave chat room.
     *
     * @param nick player nick name
     * @param room room to leave
     * @return error code
     */
    public int leaveChatroom(String nick, String room) {
        if(DEBUG) System.out.println("Chat.leaveRoom(\"" + nick + "\", \"" + room + "\")");
        Player player = Player.getPlayer(nick);
        if(player != null) {
            Chatroom chatroom = Chatroom.getChatroom(room);
            chatroom.removeChatEventListener(player);
            return SUCCESS;
        }
        // can't leave user is offline...
        return ERROR;
    }

    /**
     * Get all unread messages for a chat room.
     *
     * @param nick player nick name
     * @param room room name
     * @return formated collection of messages
     */
    public String getMessages(String nick, String room) {
        if(DEBUG) System.out.println("Chat.getMessages(\"" + nick + "\", \"" + room + "\")");
        Player player = Player.getPlayer(nick);
        if(player != null) {
            Chatroom chatroom = Chatroom.getChatroom(room);
            return chatroom.getMessages(player);
        }
        // can't get user messages, user is offline...
        return ";";
    }

    /**
     * Add a message to a chat room.
     *
     * @param nick who is senting the message
     * @param room to which room
     * @param message message
     * @return error code
     */
    public int addMessage(String nick, String room, String message) {
        if(DEBUG) System.out.println("Chat.addMessage(\"" + nick + "\", \"" + room + "\", \"" + message + "\")");
        Player player = Player.getPlayer(nick);
        if(player != null) {
            Chatroom chatroom = Chatroom.getChatroom(room);
            chatroom.addMessage(
                    new Message(Message.MESSAGE_GENERAL, nick, message)
                    );
            return SUCCESS;
        }
        // can't add user messages, user is offline...
        return ERROR;
    }

    /**
     * Overload the base finalize method to assure that the Thread is stopped.
     */
    protected void finalize() throws Throwable {
        if(cleaner != null) cleaner.exitCleaner();
        super.finalize();
    }

    public int test() {

        // login paulo
        login("Paulo", "Azerel", "pmml", "password");
        // login lopes
        login("Lopes", "Azerel", "pmlopes", "password");
        // make lopes, paulo's friend
        addFriend("pmml", "pmlopes");
        // send a private message to paulo
        addPrivateMessage("pmlopes", "pmml", "Message#1");
        // logout lopes
        logout("pmlopes");
        return 0;
    }
}
