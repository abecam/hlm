#include "../../../necro_core/ncore.h"
#include "Chat.h"

namespace MODULES
{
	CChat::CChat(void) : CMMROClient()
	{
		m_method_output = new char[BUFFERSIZE];
		m_method_call = new char[BUFFERSIZE];
		// event buffer doesn't need to be so big
		m_event_buffer = new char[BUFFERSIZE/4];

		m_name = NULL;
		m_faction = NULL;
		m_nick = NULL;
		m_password = NULL;

		// decoding buffer
		m_b64inout = new char[500];

		// create the encoder/decoder
		m_base64 = new NET::CBase64();
	}

	CChat::CChat(const char * name, const char * faction) : CMMROClient()
    {
        m_method_output = new char[BUFFERSIZE];
        m_method_call = new char[BUFFERSIZE];
        // event buffer doesn't need to be so big
        m_event_buffer = new char[BUFFERSIZE/4];

        // store name and faction...
        m_name = new char[strlen(name)+1];
        strcpy((char *)m_name, name);

        m_faction = new char[strlen(faction)+1];
        strcpy((char *)m_faction, faction);

		m_nick = NULL;
		m_password = NULL;

		// decoding buffer
		m_b64inout = new char[500];

        // create the encoder/decoder
        m_base64 = new NET::CBase64();
    }

	CChat::~CChat(void)
    {
        // buffers
        delete m_method_output;
        delete m_method_call;
        delete m_event_buffer;

        // configs
        if(m_nick == NULL) delete m_nick;
        if(m_password == NULL) delete m_password;
		if(m_name == NULL) delete m_name;
		if(m_faction == NULL) delete m_faction;

        // delete the encoder/decoder
		delete m_b64inout;
        delete m_base64;
    }

    int CChat::GetLastErr()
    {
        // by default last error is the number of read bytes, or a negative error code
        if(m_lastErr >= 0) return 0;
        // real error
        return m_lastErr;
    }

    /******************************************************************************/
    /**       Connection management code                                        ***/
    /******************************************************************************/

    int CChat::Login(const char * nick, const char * password)
    {
		if(m_nick != NULL) delete m_nick;
		if(m_password == NULL) delete m_password;

		// store nick and password...
		m_nick = new char[strlen(nick)+1];
		strcpy((char *)m_nick, nick);

		m_password = new char[strlen(password)+1];
		strcpy((char *)m_password, password);

		m_lastErr = 0;

		if(m_name != NULL && m_faction != NULL)
		{
			sprintf(m_method_call, "Chat.login(\"%s\", \"%s\", \"%s\", \"%s\")", m_name, m_faction, m_nick, m_password);
		}
		else
		{
			sprintf(m_method_call, "Chat.login(\"\", \"\", \"%s\", \"%s\")", m_nick, m_password);
		}
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);
        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

    int CChat::Logout()
    {
        m_lastErr = 0;

        sprintf(m_method_call, "Chat.logout(\"%s\")", m_nick);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

	int CChat::CreateAccount(char * name, char * nick, char * password)
	{
		m_lastErr = 0;

		sprintf(m_method_call, "Chat.createAccount(\"%s\", \"%s\", \"%s\")", name, nick, password);
		m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

		// verify the return data
		if(m_lastErr > 0)
		{
			return ((int)m_method_output[0]) - '0';
		}
		else if(m_lastErr == 0)
		{
			// the remote method is not void it can be empty
			return MMROCLIENT_NODATA;
		}
		else
		{
			// return the internal error code
			return m_lastErr;
		}
	}

	/******************************************************************************/
    /**       Event Handler                                                     ***/
    /******************************************************************************/

    CHATEVENTS * CChat::GetEvents()
    {
        m_lastErr = 0;
        this->events.length = 0;

        sprintf(m_method_call, "Chat.getEvents(\"%s\")", m_nick);
        m_lastErr = this->ExecuteMethod(m_method_call, m_event_buffer);

        // verify the return data
        if(m_lastErr >= 0)
        {
            char * p;

            // format is: <number (1 char)>:<string>;
            p = strtok(m_event_buffer, ":");
            while(p != NULL)
            {
                this->events.event[this->events.length].type = ((int) p[0]) - '0';
                // jump over #:
                p = strtok(NULL, ";");
                if(p != NULL)
                {
                    this->events.event[this->events.length].info = p;
                }
                // increment return struct length
                this->events.length++;
                // update the tokenizer for the next message
                p = strtok(NULL, ":");
            }
            return &this->events;
        }
        else
        {
            // return the internal error code
            return NULL;
        }
    }


    /******************************************************************************/
    /**       Private Messages                                                  ***/
    /******************************************************************************/

    CHATMESSAGES * CChat::GetPrivateMessages()
    {
        m_lastErr = 0;
        this->messages.length = 0;

        sprintf(m_method_call, "Chat.getPrivateMessages(\"%s\")", m_nick);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr >= 0)
        {
            char * p;

            // format is: <number (1 char)>:<string>:<string>;
            p = strtok(m_method_output, ":");
            while(p != NULL)
            {
                this->messages.message[this->messages.length].type = ((int) p[0]) - '0';
                // jump over #:
                p = strtok(NULL, ":");
                if(p != NULL)
                {
                    this->messages.message[this->messages.length].from = p;
                }
                // jump over the <player>:
                p = strtok(NULL, ";");
                if(p != NULL)
                {
					// need to decode from Base64
					int len = (int) strlen(p);
					m_base64->Decode(p, m_b64inout, &len);
					// the encoded string will come on "m_method_output"
					strcpy(p, m_b64inout);
					this->messages.message[this->messages.length].message = p;
                }
                // increment return struct length
                this->messages.length++;
                // update the tokenizer
                p = strtok(NULL, ":");
            }
            return &this->messages;
        }
        else
        {
            // return the internal error code
            return NULL;
        }
    }

    int CChat::AddPrivateMessage(char * to, char * msg)
    {
        m_lastErr = 0;

        // convert MSG into Base64 encoding
        int len = (int) strlen(msg);
        if(len > 0)
        {
            return MMROCLIENT_NODATA;
        }

		m_base64->Encode(msg, m_b64inout, &len);
		// the encoded string will come on "m_b64inout"
		sprintf(m_method_call, "Chat.addPrivateMessage(\"%s\", \"%s\", \"%s\")", m_nick, to, m_b64inout);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

    int CChat::AddFriend(char * nick)
    {
        m_lastErr = 0;

        sprintf(m_method_call, "Chat.addFriend(\"%s\", \"%s\")", m_nick, nick);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

    int CChat::RemoveFriend(char * nick)
    {
        m_lastErr = 0;

        sprintf(m_method_call, "Chat.removeFriend(\"%s\", \"%s\")", m_nick, nick);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

    CHATPLAYERS * CChat::GetFriends()
    {
        m_lastErr = 0;
        this->players.length = 0;

        sprintf(m_method_call, "Chat.getFriends(\"%s\")", m_nick);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr >= 0)
        {
            char * p;

            // format is: <number (1 char)>:<string>;
            p = strtok(m_method_output, ":");
            while(p != NULL)
            {
                this->players.player[this->players.length].online = (((int) p[0]) - '0')==1?true:false;
                // jump over #:
                p = strtok(NULL, ";");
                if(p != NULL)
                {
                    this->players.player[this->players.length].name = p;
                }
                // increment return struct length
                this->players.length++;
                // update the tokenizer
                p = strtok(NULL, ":");
            }
            return &this->players;
        }
        else
        {
            // return the internal error code
            return NULL;
        }
    }

    /******************************************************************************/
    /**       Group Messages                                                    ***/
    /******************************************************************************/

    int CChat::JoinChatroom(char * room)
    {
        m_lastErr = 0;

        sprintf(m_method_call, "Chat.joinChatroom(\"%s\", \"%s\")", m_nick, room);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

    int CChat::LeaveChatroom(char * room)
    {
        m_lastErr = 0;

        sprintf(m_method_call, "Chat.leaveChatroom(\"%s\", \"%s\")", m_nick, room);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

    CHATMESSAGES * CChat::GetMessages(char * room)
    {
        m_lastErr = 0;
        this->messages.length = 0;

        sprintf(m_method_call, "Chat.getMessages(\"%s\", \"%s\")", m_nick, room);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr >= 0)
        {
            char * p;

            // format is: <number (1 char)>:<string>:<string>;
            p = strtok(m_method_output, ":");
            while(p != NULL)
            {
                this->messages.message[this->messages.length].type = ((int) p[0]) - '0';
                // jump over #:
                p = strtok(NULL, ":");
                if(p != NULL)
                {
                    this->messages.message[this->messages.length].from = p;
                }
                // jump over the <player>:
                p = strtok(NULL, ";");
                if(p != NULL)
                {
					// need to decode from Base64
					int len = (int) strlen(p);
					m_base64->Decode(p, m_b64inout, &len);
					// the encoded string will come on "m_method_output"
					strcpy(p, m_b64inout);
                    this->messages.message[this->messages.length].message = p;
                }
                // increment return struct length
                this->messages.length++;
                // update the tokenizer
                p = strtok(NULL, ":");
            }
            return &this->messages;
        }
        else
        {
            // return the internal error code
            return NULL;
        }
    }

    int CChat::AddMessage(char * room, char * msg)
    {
        m_lastErr = 0;

        // convert MSG into Base64 encoding
        int len = (int) strlen(msg);
        if(len == 0)
        {
            return MMROCLIENT_NODATA;
        }

        m_base64->Encode(msg, m_b64inout, &len);
        // the encoded string will come on "m_method_output"

        sprintf(m_method_call, "Chat.addMessage(\"%s\", \"%s\", \"%s\")", m_nick, room, m_b64inout);
        m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);

        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }

    int CChat::Test()
    {
        m_lastErr = 0;

        m_lastErr = this->ExecuteMethod("Chat.test()", m_method_output);
        // verify the return data
        if(m_lastErr > 0)
        {
            return ((int)m_method_output[0]) - '0';
        }
        else if(m_lastErr == 0)
        {
            // the remote method is not void it can be empty
            return MMROCLIENT_NODATA;
        }
        else
        {
            // return the internal error code
            return m_lastErr;
        }
    }
}
