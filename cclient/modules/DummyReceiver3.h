#ifndef MODULES_DUMMYRECEIVER3_H
#define MODULES_DUMMYRECEIVER3_H 1

#include "../net/MMROClient.h"

namespace MODULES
{
	/**
	* Sample class to execute a remote call on DummyReceiver3 module
	*/
	class CDummyReceiver3 : public NET::CMMROClient
	{
	public:
		/**
		* Creates a new object (initializes the private vars only)
		*/
		CDummyReceiver3(void);

		/**
		* Destroys the current object (deletes the private vars only)
		*/
		~CDummyReceiver3(void);

		/**
		* Method that stubs for the remote method named "public String test(String s)"
		*/
		char * Test(char * name);

		/**
		* Returns the latest runtime error code. 0 for success.
		* @return 0 for success
		*/
		int GetLastErr();

	private:
		// internal member variables
		char * m_method_output;
		char * m_method_call;
		int m_lastErr;
	};
}

#endif
