#ifndef NET_MMROCLIENT_H
#define NET_MMROCLIENT_H 1

#include "InetSocket.h"

namespace NET
{
	#define MMROCLIENT_SUCCESS              0
	#define MMROCLIENT_NODATA               -10
	#define MMROCLIENT_ALREADY_CONNECTED    -11
	#define MMROCLIENT_NOT_CONNECTED        -12
	#define MMROCLIENT_EXECUTE_IOERROR      -13
	#define MMROCLIENT_UNKNOWN_METHOD       -14

	/**
	* A CMMROClient is an object that handles the IO messages through the game engine and
	* the network server. It extends the base CInetSocket class in a way that the network
	* core is completly isolated from the application core. This could be seen as a
	* protocol layer that stays above the connection layer which is the CInetSocket object.
	*/
	class CMMROClient : public CInetSocket
	{
	public:
		/**
		* Creates a CMMROClient object
		*/
		CMMROClient(void);

		/**
		* Destroys a CMMROClient Object
		*/
		~CMMROClient(void);

		/**
		* This should be the first method to be called. It initializes some data structures
		* and creates the connection level socket. It isn't a constructor because we may
		* (and will) want to call this method from upper level objects.
		* @param host a internet host name
		* @param port a port number
		*/
		void Init(char * host, short port);

		/**
		* If we want to reuse the socket connection (in other words, do not create a socket
		* and close it for each remote method call) we can set a keepalive CMMROClient
		* object.
		* @param keepalive boolean value
		*/
		void KeepAlive(bool keepalive);

		/**
		* We may need to know what is the current value for keep alive.
		*
		* @return true for keep alive on
		*/
		bool GetKeepAlive();

		/**
		* The main method from this class is the execute method, it receives two char arrays
		* one with the command to execute (no need to add extra \r\n control chars) and the
		* other one for the output.
		* @param method remote method to invoke
		* @param result buffer to hold the result
		*
		* @result the status code for the operation (negative for errors)
		*/
		int ExecuteMethod(char * method, char * result);

	private:
		/**
		* Internal method to handle the connection of the socket
		*/
		int ConnectToServer();

		/**
		* Internal method to handle gracefully the disconnection of the socket
		*/
		int DisconnectFromServer();

		/**
		* Internal method that will handle all the MMRO protocol. This method is
		* agnostic to the keepalive which is handled by the public method above.
		*/
		int BaseExecuteMethod(char * method, char * result);

		/**
		* Remove the end of line for any OS. Given a String, returns the length
		* of the string without the line terminator.
		*/
		void RemoveLineTerminator(char * line, int * len);

		// Internal instance variables

		bool m_keepalive;

		int m_timeout;
		char * m_buffer;

		char * m_host;
		short m_port;
	};
}

#endif