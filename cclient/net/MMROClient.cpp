#include "../../../necro_core/ncore.h"
#include "MMROClient.h"

namespace NET
{
	CMMROClient::CMMROClient(void) : CInetSocket()
	{
		// by default keepalive is disabled
		m_keepalive = false;
		m_timeout = 500;
		m_buffer = new char[BUFFERSIZE];
		m_host = NULL;
	}

	CMMROClient::~CMMROClient(void)
	{
		if(this->IsConnected()) this->DisconnectFromServer();
		if(m_host != NULL) delete m_host;
		delete m_buffer;
	}

	void CMMROClient::Init(char * host, short port)
	{
		// store address and port for further connections...
		m_host = new char[strlen(host)+1];
		strcpy((char *)m_host, host);
		m_port = port;
	}

	void CMMROClient::KeepAlive(bool keepalive)
	{
		this->m_keepalive = keepalive;
	}

	bool CMMROClient::GetKeepAlive()
	{
		return this->m_keepalive;
	}

	int CMMROClient::ExecuteMethod(char * method, char * result)
	{
		if(this->m_keepalive)
		{
			// connect (if already connected just ignore it
			this->ConnectToServer();
			// execute the method
			return this->BaseExecuteMethod(method, result);
		}
		else
		{
			int connectStatus;
			int remoteStatus;

			// already connected
			if(this->IsConnected()) return MMROCLIENT_ALREADY_CONNECTED;
			// connect to the server
			connectStatus = this->ConnectToServer();
			// successfully connected to the server
			if(connectStatus == INETSOCKET_SUCCESS)
			{
				// execute the method
				remoteStatus = this->BaseExecuteMethod(method, result);
				// disconnect from the server
				connectStatus = this->DisconnectFromServer();
				// non successfully disconnect from the server
				if(connectStatus != INETSOCKET_SUCCESS)
				{
					return connectStatus;
				}
				// after closing the socket return the result
				return remoteStatus;
			}
			else
			{
				return connectStatus;
			}
		}
	}

	int CMMROClient::BaseExecuteMethod(char * method, char * result)
	{
		int iresult;
		int length;

		length = (int) strlen(method);
		// add the end control chars "\r\n"
		sprintf(m_buffer, "%s\r\n", method);
		length += 2;
		// send the request
		iresult = this->Send(m_buffer, &length);
		// handle the result
		if(iresult == INETSOCKET_SUCCESS)
		{
			// Try to recover a message to come back from the server.
			iresult = this->Receive(result, BUFFERSIZE, m_timeout);
			// check if there was an error...
			if(iresult == INETSOCKET_TIMEOUT || iresult == INETSOCKET_RECEIVE_ERROR)
			{
				perror("MMROClient->BaseExecuteMethod()");
				this->DisconnectFromServer();
				return iresult;
			}

			// check if we received any data (we must receive as part of the
			// protocol
			if(iresult == 0)
			{
				perror("MMROClient->BaseExecuteMethod()");
				this->DisconnectFromServer();
				return MMROCLIENT_NODATA;
			}

			// remove trainling end of line chars
			RemoveLineTerminator(result, &iresult);

			// format java string into C++ string
			result[iresult] = '\0';
			if(strcmp(result, "OK") == 0)
			{
				// send ACK
				length = 5;
				iresult = this->Send("ACK\r\n", &length);
				// receive the actual result
				if(iresult == INETSOCKET_SUCCESS)
				{
					// Try to recover a message to come back from the server.
					iresult = this->Receive(result, BUFFERSIZE, m_timeout);
					// there was an error...
					if(iresult == INETSOCKET_TIMEOUT || iresult == INETSOCKET_RECEIVE_ERROR)
					{
						perror("MMROClient->BaseExecuteMethod()");
						this->DisconnectFromServer();
						return iresult;
					}
					// remove trainling end of line chars
					RemoveLineTerminator(result, &iresult);
					// format java string into C++ string
					result[iresult] = '\0';
					return iresult;
				}
				else
				{
					perror("MMROClient->BaseExecuteMethod()");
					this->DisconnectFromServer();
					return MMROCLIENT_EXECUTE_IOERROR;
				}
			}
			else
			{
				this->DisconnectFromServer();
				return MMROCLIENT_UNKNOWN_METHOD;
			}
		}
		else
		{
			perror("MMROClient->BaseExecuteMethod()");
			this->DisconnectFromServer();
			return MMROCLIENT_EXECUTE_IOERROR;
		}
	}

	int CMMROClient::ConnectToServer()
	{
		int isvalid;

		if(this->IsConnected()) return MMROCLIENT_ALREADY_CONNECTED;
		// create the inetsocket object
		isvalid = this->Create(m_host, m_port);
		if(isvalid == INETSOCKET_SUCCESS)
		{
			return this->Connect();
		}
		else
		{
			return isvalid;
		}
	}

	int CMMROClient::DisconnectFromServer()
	{
		if(!this->IsConnected()) return MMROCLIENT_NOT_CONNECTED;

		// end this session
		int length = 6;
		this->Send("end.\r\n", &length);

		return this->Close();
	}

	void CMMROClient::RemoveLineTerminator(char * line, int * len)
	{
		// remote trailing end of line and format as a C-String
		// remove end for *NIX and MacOS and last \n for Win*
		if(*len-1 >= 0)
		{
			if(line[*len-1] == '\n')
			{
				// this can be either Win* or *NIX
				*len = *len - 1;
			}
		}
		if(*len-1 >= 0)
		{
			if(line[*len-1] == '\r')
			{
				// this can be MacOS if failed the if above
				// or Win* if entered the if above
				*len = *len - 1;
			}
		}
	}
}
