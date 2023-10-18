/*
 * Created on Feb 8, 2005
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

package core;

/**
 *		   This class is intended to interpret a string command to
 *         use a local command (to use a distant command, see DispatchCommand)
 *         As the command are already dispatched, they look like :
 *         class.method(param) We return a string which include the result if
 *         any, or "noreturn", all the other actions are outside our control
 *         This approach seems strongly far from the object-oriented way of
 *         working, as we are not considering the possibility of instance. As I
 *         am not sure it is of no use, I do not want to limit the control to
 *         static classes. So, we use a ManageInstances class, which has to
 *         create and maintain the instance of class. This last has to be
 *         DEFINED clearly further. For now, we can ask for a new instance by
 *         using new(class), which return the number of the instance Then, it is
 *         possible to use the default instance like above or other EXISTING by
 *         using class.X.method(param) Example: myclass.2.setParam("Où vont tous
 *         ces enfants","Victor Hugo",0) 
 * 
 * @author Alain Becam 
 * 
 * TODO    Manage the exception, enhance the support au string, allow more type (if needed)
 *         Code Templates
 */

public class InterpretOrdersSc
{

    public static void main(String[] args)
    {
        // Just for test
        InterpretOrdersSc myInterpret = new InterpretOrdersSc();

        myInterpret
                .extractMethod("trucmuch.bidule('i','i','j'   ,   'o', 89, 76.56,\"hello\",6,\"truc\",\"truc2\",14,\"jui\")");
        myInterpret.extractMethod("classname.3.methid(8,\"truc\",\"truc\",67)");
        myInterpret.extractMethod("trucmuch.2.bidule()");
    }

    public int extractMethod(String line)
    {
        // This version use java.util.Scanner
        int startPar;
        int endPar;
        String lineLight; // Consists in the line without the spaces
        String className; // As it seems
        int instance = 0; // If wanted
        String methodName;
        int nbParameters;
        //java.util.Scanner myScanner = new java.util.Scanner(line); Java 1.5

        // First, print the commentaries (if any) and remove the spaces
        {
            int startCom;
            startCom = line.indexOf("//");
            if (startCom != -1)
            {
                String comm = line.substring(startCom);
                System.out.println(comm);
            }
        }
        lineLight = line.replaceAll(" ", "");
        // Where are the parenthesis (if any)
        startPar = lineLight.indexOf('(');
        if (startPar == -1)
        {
            System.out.println("I have missed the '(', is there any problem?");
            return 1;
        }
        endPar = lineLight.indexOf(')');
        if (endPar == -1)
        {
            System.out.println("Where is the ')' ?");
            return 2;
        }
        if (endPar < startPar)
        {
            System.out.println("I expect that '(' come before ')' !");
            return 3;
        }
        // What is the name of the asked class
        {
            int firstPointPos = lineLight.indexOf('.');
            int secondPointPos;
            if (firstPointPos == -1)
            {
                System.out.println("I wait for class.method(...)");
                return 4;
            }
            className = lineLight.substring(0, firstPointPos);
            // Is there any instance ?
            secondPointPos = lineLight.indexOf('.', firstPointPos + 1);
            if ((secondPointPos != -1) && (secondPointPos < startPar))
            {
                // If there is, we try to capture it !
                String instanceStr = lineLight.substring(firstPointPos + 1,
                        secondPointPos);
                try
                {
                    Integer instanceInt = new Integer(instanceStr);
                    instance = instanceInt.intValue();
                    // Ok ! Let see the method name
                } catch (Exception e)
                {
                    System.out
                            .println("I am waiting for a proper number, not for "
                                    + instanceStr);
                    return 5; // Of course, to be enhanced
                }
                methodName = lineLight.substring(secondPointPos + 1, startPar);
            } else
            {
                // We just take the method name
                methodName = lineLight.substring(firstPointPos + 1, startPar);
            }
        }
        // Ok, let take the parameter(s).
        // Easy start : there is nothing.
        if ((endPar - startPar) == 1)
        {
            nbParameters = 0;
        } else
        {
            boolean end = false;
            nbParameters = 0;
            int startPar2, endPar2;
            int pos = 1;
            // We will rework on our complete string, as we have erased all the
            // space in the "light" string...
            startPar2 = line.indexOf('(');
            endPar2 = line.indexOf(')');

            String parametersString = line.substring(startPar2, endPar2);
            // Not so easy
            while (!end)
            {
                if (pos >= parametersString.length())
                {
                    // We must find a ')' for the end...
                    end = true;
                } else
                {
                    if (parametersString.charAt(pos) == '\"') // Start with the
                                                              // String (the
                                                              // most difficult)
                    {
                        //Here it is...
                        boolean endOfLine = false;
                        boolean voidChar = false; // I assume this is the '\'
                        int posInit = pos + 1;
                        pos++;
                        String currentLine;
                        // We look for the size and we extract
                        while (!endOfLine)
                        {
                            if (pos > parametersString.length())
                            {
                                System.out.println("Parameter badly formed");
                                return 6;
                            }
                            if ((parametersString.charAt(pos) == '\"')
                                    && !voidChar)
                            {
                                endOfLine = true; // We have found the last '"'
                            } else
                            {
                                // There must be something...
                                /*
                                 * if ( parametersString.charAt(pos)== '\\' ) {
                                 * voidChar = true; pos++; } else { voidChar =
                                 * false; pos++; }
                                 *///To be managed (but not LIKE that)
                                pos++;
                            }
                        }
                        nbParameters++;
                        currentLine = parametersString.substring(posInit, pos);
                        pos++;
                        System.out.println("Paramètre String : " + currentLine);

                    } else
                    {
                        if (parametersString.charAt(pos) == '\'')
                        {
                            // It's a character
                            pos++;
                            char currentChar = parametersString.charAt(pos);
                            pos += 2;
                            nbParameters++;
                            System.out.println("Character : " + currentChar);
                        } else
                        {
                            if (parametersString.charAt(pos) == ' ')
                            {
                                // Nothing to do
                                pos++;
                            } else
                            {
                                int commaPos;
                                long longValue;
                                double doubleValue; // There is something to do
                                                    // with int and float... (if
                                                    // int and long differs)
                                // It must be a number !!!
                                // First we have to see where is the next comma
                                commaPos = parametersString.indexOf(',', pos);
                                if (commaPos == -1)
                                {
                                    commaPos = parametersString.length();
                                    System.out.println("Near the end.");
                                    // not so beautiful but efficient
                                }
                                //Let look for a point... If it is a float...
                                if ((parametersString.indexOf('.', pos) < commaPos)
                                        && (parametersString.indexOf('.', pos) != -1))
                                {
                                    doubleValue = Double.valueOf(
                                            parametersString.substring(pos,
                                                    commaPos)).doubleValue();
                                    System.out.println("Chiffre flottant : "
                                            + doubleValue);
                                } else
                                {
                                    // It is an integer ! (we hope)
                                    longValue = Long.valueOf(
                                            parametersString.substring(pos,
                                                    commaPos)).longValue();
                                    System.out.println("Chiffre entier : "
                                            + longValue);
                                }
                                pos = commaPos;
                                nbParameters++;
                            }
                        }
                    }
                    // There must be a comma or a end parenthesis now !
                    {
                        if (pos < parametersString.length())
                        {
                            if (parametersString.charAt(pos) == ',')
                            {
                                // Next One !!!
                                pos++;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("It seems we want the instance " + instance
                + " of the method " + methodName + " in the class " + className
                + ".");
        return 0;
    }
}