// Thread global vars
HANDLE hThread;
HANDLE hRunMutex;
HANDLE hNextTime;

/**
 * Create a Thread that will anwser the network call to the remote server
 */
bool createNetThread();

/**
 * Close the thread from memory
 */
void closeNetThread();

/**
 * Run again the network thread
 */
void runNetThread();

/**
 * The handler (the run method) of the thread
 */
DWORD WINAPI netHandler(LPVOID lpParam);


// Thread main method (This can be seen as the run method from Java)
DWORD WINAPI netHandler(LPVOID lpParam)
{
    while ( WaitForSingleObject( hRunMutex, 75L ) == WAIT_TIMEOUT )
    {
        // code goes here
        MODULES::CChat * o_chat;
        o_chat = (MODULES::CChat *)lpParam;

		// helper pointers
		MODULES::CHATEVENTS * c_events;
		MODULES::CHATMESSAGES * c_messages;

        // get the current available events
        c_events = o_chat->GetEvents();

        // for each event parse it and handle it
        for(int i=0; (c_events != NULL) && (i < c_events->length); i++)
        {
            switch(c_events->event[i].type)
            {
            case 1:
                // EVENT_NEW_CHATROOM_MESSAGE
                // arg1 is the room name, invoke getMessages
				NCORE::cLogMgr::getSingleton().getLog("log").debug("We have a new message from a chatroom (%s)", c_events->event[i].info);
				// get the messages from the room
				c_messages = o_chat->GetMessages(c_events->event[i].info);
				// for each message print it
				for(int j=0; j<c_messages->length; j++)
				{
					NCORE::cLogMgr::getSingleton().getLog("log").debug(" -> Message {Type(%d) From(%s) Message(%s)}", c_messages->message[j].type, c_messages->message[j].from, c_messages->message[j].message);
					char line[500];
					sprintf(line, "%s: %s\0", c_messages->message[j].from, c_messages->message[j].message);
					PrintTextMessage(line);
				}
                break;
            case 2:
                // EVENT_NEW_FRIEND_REQUEST
                // arg1 is the friend name, do nothing
				NCORE::cLogMgr::getSingleton().getLog("log").debug("Someone want to be your friend (%s)", c_events->event[i].info);
                break;
            case 3:
                // EVENT_FRIEND_ONLINE
                // arg1 is the friend name, do nothing
				NCORE::cLogMgr::getSingleton().getLog("log").debug("A friend of yours is online (%s)", c_events->event[i].info);
                break;
            case 4:
                // EVENT_FRIEND_OFFLINE
                // arg1 is the friend name, do nothing
				NCORE::cLogMgr::getSingleton().getLog("log").debug("A friend of yours went offline (%s)", c_events->event[i].info);
                break;
            case 5:
                // EVENT_NEW_PRIVATE_MESSAGE
                // arg1 is the sender name, invoke getPrivateMessages
				NCORE::cLogMgr::getSingleton().getLog("log").debug("We have a new private message (%s)", c_events->event[i].info);
				// get the messages from the room
				c_messages = o_chat->GetPrivateMessages();
				// for each message print it
				for(int j=0; j<c_messages->length; j++)
				{
					NCORE::cLogMgr::getSingleton().getLog("log").debug(" -> Message {Type(%d) From(%s) Message(%s)}", c_messages->message[j].type, c_messages->message[j].from, c_messages->message[j].message);
				}
                break;
            default:
                // shouldn't happen
                break;
            }
        }

        // Wait for the autorisation to continue
        WaitForSingleObject( hNextTime, INFINITE );
    }

    return 0;
}

bool createNetThread()
{
    DWORD  dwThreadId;

    hNextTime = CreateEvent(
        NULL,
        false,
        true,
        NULL);

    hRunMutex = CreateMutex(
        NULL,
        TRUE,
        NULL);

    // Create the thread
    hThread = CreateThread(
        NULL,               // Security attribute
        0,                  // File size
        netHandler,			// Handler function
        o_chat,               // arguments
        0,                  // Creation flag
        &dwThreadId);       // Give thread ID

    return (hThread == NULL);
}

void closeNetThread()
{
    ReleaseMutex(hRunMutex);
    ReleaseMutex(hNextTime);
    CloseHandle(hThread);
}

void runNetThread()
{
    SetEvent(hNextTime);
}
