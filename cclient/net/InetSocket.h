#ifndef NET_INETSOCKET_H
#define NET_INETSOCKET_H 1

// OS specific include
#pragma comment(lib, "ws2_32.lib")

namespace NET
{
	#define BUFFERSIZE 8192

	#define INETSOCKET_SUCCESS          0
	#define INETSOCKET_CREATE_ERROR     -1
	#define INETSOCKET_SETOPTS_ERROR    -2
	#define INETSOCKET_CONNECT_ERROR    -3
	#define INETSOCKET_TIMEOUT          -4
	#define INETSOCKET_RECEIVE_ERROR    -5
	#define INETSOCKET_SEND_ERROR       -6

	/**
	* A CInetSocket is a wrapper object for the operating system internet socket libraries.
	* The goal of this object is to wrap common simple functions such as creation of sockets
	* and read and write operations in a unique interface.
	*/
	class CInetSocket
	{
	public:

		/**
		* Creates a CInetSocket object
		*/
		CInetSocket(void);

		/**
		* Destroys the object avoiding memory leaks (at least it should be).
		*/
		~CInetSocket(void);

		/**
		* Creates a CInetSocket to a specific host and port.
		* @param host hostname
		* @param port port number
		*
		* @return status code for the operation
		*/
		int Create(char * host, short port);

		/**
		* Physically connect to the other party defined in the create method.
		*
		* @return status code for the operation
		*/
		int Connect();

		/**
		* Boolean test for an currently open connection.
		*
		* @return true if connected
		*/
		bool IsConnected();

		/**
		* Closes an open connection.
		*
		* @return status code for the operation
		*/
		int Close();

		/**
		* Receive data from the socket.
		* @param buf a char array buffer to hold the incoming data
		* @param len the size of the buffer
		* @param timeout a timeout value to wait for data
		*
		* @return a status code for the operation, or the length of the received data
		*/
		int Receive(char * buf, int len, int timeout = 0);

		/**
		* Sends a char array buffer through out the socket
		* @param buf the char array buffer to send
		* @param len a int pointer that holds the remaining bytes to be sent
		*
		* @return status code for the operation
		*/
		int Send(char * buf, int * len);

	private:
		// internal data structures to help the implementation

		/**
		* The Socket Structure
		*/
		SOCKET m_socket;

		/**
		* The INET address structure
		*/
		sockaddr_in m_sockaddr_in;

		/**
		* Boolean test for connected sockets
		*/
		bool m_connected;
	};
}

#endif