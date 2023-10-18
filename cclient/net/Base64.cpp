#include "../../../necro_core/ncore.h"
#include "Base64.h"

namespace NET
{
    char *CBase64::bstr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    char CBase64::rstr[] =
    {
        0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
        0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,
        0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,  62,   0,   0,   0,  63,
        52,  53,  54,  55,  56,  57,  58,  59,  60,  61,   0,   0,   0,   0,   0,   0,
        0,   0,   1,   2,   3,   4,   5,   6,   7,   8,   9,  10,  11,  12,  13,  14,
        15,  16,  17,  18,  19,  20,  21,  22,  23,  24,  25,   0,   0,   0,   0,   0,
        0,  26,  27,  28,  29,  30,  31,  32,  33,  34,  35,  36,  37,  38,  39,  40,
        41,  42,  43,  44,  45,  46,  47,  48,  49,  50,  51,   0,   0,   0,   0,   0
    };


    CBase64::CBase64(void)
    {
    }

    CBase64::~CBase64(void)
    {
    }

    void CBase64::Encode(const char * src, char * out, int * len)
    {
        int i = 0;
        int j = 0;
        int o = 0;

        // it's all about shifting...
        while (i < *len)
        {
            int remain = *len - i;

            switch (remain)
            {
            case 1:
                out[j++] = bstr[ ((src[i] >> 2) & 0x3f) ];
                out[j++] = bstr[ ((src[i] << 4) & 0x30) ];
                out[j++] = '=';
                out[j++] = '=';
                break;
            case 2:
                out[j++] = bstr[ ((src[i] >> 2) & 0x3f) ];
                out[j++] = bstr[ ((src[i] << 4) & 0x30) + ((src[i + 1] >> 4) & 0x0f) ];
                out[j++] = bstr[ ((src[i + 1] << 2) & 0x3c) ];
                out[j++] = '=';
                break;
            default:
                out[j++] = bstr[ ((src[i] >> 2) & 0x3f) ];
                out[j++] = bstr[ ((src[i] << 4) & 0x30) + ((src[i + 1] >> 4) & 0x0f) ];
                out[j++] = bstr[ ((src[i + 1] << 2) & 0x3c) + ((src[i + 2] >> 6) & 0x03) ];
                out[j++] = bstr[ (src[i + 2] & 0x3f) ];
            }
            o += 4;
            i += 3;
        }
        // set the len out param
        *len = j;
        // add the \0 char to the out string
        out[*len] = '\0';
    }

    void CBase64::Decode(const char * src, char * out, int * len)
    {
        int i = 0;
        int j = 0;
        int dl = this->DecodeLength(src, len);

        // decode length
        if (dl == 0)
        {
            // nothing to decode or can't decode
            *len = 0;
            out[0] = '\0';
            return;
        }

        // it's all about shifting...
        while (i < *len)
        {
            char b1 = (char)((rstr[(int)src[i]] << 2 & 0xfc) + (rstr[(int)src[i + 1]] >> 4 & 0x03));
            out[j++] = b1;
            if(src[i + 2] != '=')
            {
                char b2 = (char)((rstr[(int)src[i + 1]] << 4 & 0xf0) + (rstr[(int)src[i + 2]] >> 2 & 0x0f));
                out[j++] = b2;
            }
            if(src[i + 3] != '=')
            {
                char b3 = (char)((rstr[(int)src[i + 2]] << 6 & 0xc0) + rstr[(int)src[i + 3]]);
                out[j++] = b3;
            }
            i += 4;
        }
        // set the len out parameter with the total decoded chars
        *len = dl;
        // \0 char to the end of the string
        out[*len] = '\0';
    }

    int CBase64::DecodeLength(const char * src, int * len)
    {
        int l = *len;

        // decode length
        if (!l || l % 4)
        {
            // nothing to decode or can't decode
            return 0;
        }
        int dl = 3 * (l / 4 - 1) + 1;
        if(src[l - 2] != '=') dl++;
        if(src[l - 1] != '=') dl++;

        return dl;
    }
}
