/*
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/*
 * This software is distributed under the MIT License
 *
 * Copyright (c) 2005 Alain Becam, Paulo Lopes, Joakim Olsson, and Johan Simonsson - 2005
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package utils;

/**
 * A simple facility to translate a module call from fields to a string
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Comm2String
{

    public static void main(String[] args)
    {
        Comm2String myComm2Str = new Comm2String();
        
        Object[] parametrs = new Object[3];
        parametrs[0] = "Ah Ah";
        parametrs[1] = Integer.valueOf(78);
        parametrs[2] = Character.valueOf('e');
        //oneModule.invokeMethod(0,"maMethodAMoi2",parametrs);
        System.out.println(myComm2Str.giveTranscript("maClasseDeTest", 0, "maMethodAMoi2", parametrs));
    }
    
    /**
     * This method create a command line, on the form "modulename.instancenumber.methodname(parameters...)"
     * It allows the direct use of InterpretOrders via a String. It is aimed at giving a simple way to send
     * method on the net. Might be better to adopt a binary representation (and secure ??? )
     * @param modulename
     * @param instance
     * @param methodName
     * @param parameters
     * @return  String, the string representation of the command.
     */
    public String giveTranscript(String modulename,int instance, String methodName, Object[] parameters)
    {
        StringBuffer lineGiven = new StringBuffer();
        
        String lineStart = modulename+"."+instance+"."+methodName+"(";
        lineGiven.append(lineStart);
        for (int iParam = 0; iParam < parameters.length ; iParam++)
        {
            if (iParam > 0)
            {
                lineGiven.append(",");
            }
            if (parameters[iParam].getClass() == java.lang.String.class)
            {
                // The string are included into " "
                lineGiven.append("\"");
                lineGiven.append(parameters[iParam].toString());
                lineGiven.append("\"");
            }
            else
            {
                if ( (parameters[iParam].getClass() == java.lang.Character.class) || (parameters[iParam].getClass() == char.class))
                {
                    // The characters are included into ' '
                    lineGiven.append("'");
                    lineGiven.append(parameters[iParam].toString());
                    lineGiven.append("'");
                }
                else
                {
                    // The numbers (other possibles types) are "nudes"
                    lineGiven.append(parameters[iParam].toString());
                }
            }
        }
        lineGiven.append(")");
        return lineGiven.toString();
    }
}
