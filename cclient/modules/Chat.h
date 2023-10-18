#ifndef MODULES_CHAT_H
#define MODULES_CHAT_H 1

#include "../net/MMROClient.h"
#include "../net/Base64.h"

namespace MODULES
{
    #define MMROCHAT_SUCCESS              0
    #define MMROCHAT_ERROR               -100

    /*** Structures to help to handle the data exchanged with the remote server ***/

    /**
    * A CHATEVENTS structure holds a collection of events that have been received
    * from the remote server. The maximum number of EVENTS is dynamically
    * calculated based on the size of the buffer
    */
    struct CHATEVENTS
    {
        struct CHATEVENT
        {
            int type;
            char * info;
        } event[(sizeof(char)*(BUFFERSIZE/4))/(sizeof(int)+sizeof(char*))];

        int length;
    };

    /**
    * Hold a collection of messages. The maximum size is calculated from the
    * size of the buffer.
    */
    struct CHATMESSAGES
    {
        struct CHATMESSAGE
        {
            int type;
            char * from;
            char * message;
        } message[(sizeof(char)*(BUFFERSIZE))/(sizeof(int)+sizeof(char*)+sizeof(char*))];

        int length;
    };

    /**
    * Hold a collection of players. The maximum size is calculated from the
    * size of the buffer.
    */
    struct CHATPLAYERS
    {
        struct CHATPLAYER
        {
            char * name;
            bool online;
        } player[(sizeof(char)*(BUFFERSIZE))/(sizeof(char*)+sizeof(bool))];

        int length;
    };


    /**
    * The MMRO WP10 Chat module (Client Side)
    */
    class CChat : public NET::CMMROClient
    {
    public:

		/**
		* Construct a Chat client assuming that the client is already registered
		*/
		CChat(void);

		/**
		* Construct a Chat client for the given name and nickname and password
		*/
		CChat(const char * name, const char * faction);

		/**
        * Destructor for the Chat client. By default it only clears
        * memory allocated objects, it doesn't closes the CInetSocket
        * connection.
        */
        ~CChat(void);

        /**
        * Login procedure
        */
        int Login(const char * nick, const char * password);

        /**
        * Logout procedure
        */
        int Logout();

		/** 
		 * Create a new account on the database
		 * @param name name of the player. There can be duplicates
		 * @param nick the unique id for a player. Only one nick is possible, no duplicates
		 * @param password the password for the player.
		 */
		int CreateAccount(char * name, char * nick, char * password);

        /**
        * Retrieve all private messages that have been unread
        */
        CHATMESSAGES * GetPrivateMessages();

        /**
        * Send a private message to a online user
        */
        int AddPrivateMessage(char * to, char * msg);

        /**
        * Add a friend to the current friend list
        */
        int AddFriend(char * nick);

        /**
        * Remove a friend from the friend list
        */
        int RemoveFriend(char * nick);

        /**
        * Retrieve the friend list
        */
        CHATPLAYERS * GetFriends();


        /**
        * Retrieve all events since last check
        */
        CHATEVENTS * GetEvents();


        /**
        * Join into a new Chat room
        */
        int JoinChatroom(char * room);

        /**
        * Leave from the chat room
        */
        int LeaveChatroom(char * room);

        /**
        * Get all unread messages for a specific chat room
        */
        CHATMESSAGES * GetMessages(char * room);

        /**
        * Send a message into a chat room
        */
        int AddMessage(char * room, char * msg);

		/**
		* Some debug method
		*/
		int Test();

        /**
        * Returns the latest runtime error code. 0 for success.
        * @return 0 for success
        */
        int GetLastErr();

    private:
        // internal member variables
        char * m_method_output;
        char * m_method_call;
        // event buffer can't be hold on the same as the output because
        // it must persist after invoking the event specific method.
        char * m_event_buffer;
        int m_lastErr;

		char * m_name;
		char * m_faction;
        char * m_nick;
        char * m_password;

        // internal object to encode/decode Base64 strings
        NET::CBase64 * m_base64;

		char * m_b64inout;

        CHATMESSAGES messages;
        CHATEVENTS events;
        CHATPLAYERS players;
    };
}

#endif
