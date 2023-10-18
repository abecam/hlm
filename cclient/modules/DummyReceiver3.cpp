#include "../../../necro_core/ncore.h"
#include "../net/MMROClient.h"
#include "DummyReceiver3.h"

namespace MODULES
{
	CDummyReceiver3::CDummyReceiver3(void) : CMMROClient()
	{
		m_method_output = new char[BUFFERSIZE];
		m_method_call = new char[BUFFERSIZE];
	}

	CDummyReceiver3::~CDummyReceiver3(void)
	{
		delete m_method_output;
		delete m_method_call;
	}

	int CDummyReceiver3::GetLastErr()
	{
		// by default last error is the number of bytes read, or a negative error code
		if(m_lastErr >= 0) return 0;
		// real error
		return m_lastErr;
	}

	char * CDummyReceiver3::Test(char * name)
	{
		m_lastErr = 0;
		sprintf(m_method_call, "DummyReceiver3.test(\"%s\")", name);
		m_lastErr = this->ExecuteMethod(m_method_call, m_method_output);
		return m_method_output;
	}
}
