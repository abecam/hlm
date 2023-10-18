#include "../../../necro_core/ncore.h"
#include "InetSocket.h"

namespace NET
{
	CInetSocket::CInetSocket(void)
	{
		m_connected = false;
		// Init for sockets.
		WSADATA WSAData;
		WSAStartup(MAKEWORD(2,0), &WSAData);
	}

	CInetSocket::~CInetSocket(void)
	{
		if(IsConnected()) Close();
		// Socket cleanup
		WSACleanup();
	}

	int CInetSocket::Create(char * host, short port)
	{
		int windowSize = BUFFERSIZE;
		int socket_opts = 1;

		/* fill in the socket structure with host information */
		memset(&m_sockaddr_in, 0, sizeof(m_sockaddr_in));
		m_sockaddr_in.sin_family = AF_INET;
		m_sockaddr_in.sin_addr.s_addr	= inet_addr(host);
		m_sockaddr_in.sin_port = htons(port);

		/* grab an Internet domain socket */
		if ((m_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) 
		{
			return INETSOCKET_CREATE_ERROR;
		}
		// we do not wait for the result...
		if ( setsockopt( m_socket, IPPROTO_TCP, TCP_NODELAY, (char*)&socket_opts, sizeof(socket_opts) ) != 0 )
		{
			return INETSOCKET_SETOPTS_ERROR; 
		}

		// Set the windows's size (of the socket)
		if ( setsockopt( m_socket, SOL_SOCKET, SO_SNDBUF, (char*)&windowSize, sizeof(windowSize) ) != 0 || setsockopt( m_socket, SOL_SOCKET, SO_RCVBUF, (char*)&windowSize, sizeof(windowSize) ) != 0 )
		{
			return INETSOCKET_SETOPTS_ERROR;
		}

		return INETSOCKET_SUCCESS;
	}

	int CInetSocket::Connect()
	{
		/* connect to PORT on HOST */
		if (connect(m_socket,(struct sockaddr *)  &m_sockaddr_in, sizeof(m_sockaddr_in)) == -1) 
		{
			return INETSOCKET_CONNECT_ERROR;
		}

		m_connected = true;
		return INETSOCKET_SUCCESS;
	}

	bool CInetSocket::IsConnected()
	{
		return m_connected;
	}

	int CInetSocket::Close()
	{
		int result = closesocket(m_socket);
		m_connected = false;
		return result;
	}

	int CInetSocket::Receive(char * buf, int len, int timeout)
	{
		fd_set fds;
		int n;
		struct timeval tv;

		// set up the file descriptor set
		FD_ZERO(&fds);
		FD_SET(m_socket, &fds);

		// set up the struct timeval for the timeout
		tv.tv_sec = timeout;
		tv.tv_usec = 0;

		// wait until timeout or data received
		n = select (0, &fds, NULL, NULL, &tv );
		if (n == 0) return INETSOCKET_TIMEOUT; // timeout !
		if (n == SOCKET_ERROR) return INETSOCKET_RECEIVE_ERROR; // error

		// data must be here, so do a normal recv()
		return recv(m_socket, buf, len, 0);
	}

	int CInetSocket::Send(char * buf, int * len)
	{
		int total = 0;
		int bytesleft = *len;
		int n=0;

		while (total < *len)
		{
			n = send(m_socket, buf+total, bytesleft, 0);
			
			if (n == -1)
			{
				break;
			}
			total += n;
			bytesleft -= n;
		}

		*len = total; // number actually sent

		return n==-1?INETSOCKET_SEND_ERROR:INETSOCKET_SUCCESS; // -1 on failure, 0 on success
	}
}