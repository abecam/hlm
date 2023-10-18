#ifndef NET_BASE64_H
#define NET_BASE64_H 1

namespace NET
{
    /**
    * Base64 Encoder/Decoder. Encodes a string for a given len and returns a null
    * terminated string and the length. If working as a decoder, decodes a Base64
    * string into a char array and returns it's length.
    */
    class CBase64
    {
    public:
        /**
        * Constructs the base64 encoder/decoder
        */
        CBase64(void);

        /**
        * Destructor
        */
        ~CBase64(void);

        /**
        * Encode a string.
        *
        * @param src source string to encode
        * @param out output encoded string
        * @param len length of the in/out strings
        */
        void Encode(const char * src, char * out, int * len);

        /**
        * Decode a string.
        *
        * @param src source string to decode
        * @param out output encoded string
        * @param len length of the in/out strings
        */
        void Decode(const char * src, char * out, int * len);

        /**
        * Helper method to find out the length of a decoded string. Usefull for
        * allocating the output buffer into memory.
        *
        * @param src encoded string
        * @param len length of the encoded string
        * @return the size that will be necessary to store the decoded string
        */
        int DecodeLength(const char * src, int * len);

    private:
        // alphabets
        static char * bstr;
        static char rstr[128];
    };
}

#endif
