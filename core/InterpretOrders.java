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

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * This class is intended to interpret a string command to
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
 * @author Alain Becam 
 * TODO    Manage the exception, enhance the support of string, allow more type (if needed)
 *         Code Templates
 */

public class InterpretOrders
{
    CommManager theCommManager;
    boolean debugMode = false;
    
    public class MethodDesc
    {
        String className;
        String methodName;
        Object returnValue;
        Object[] parameters;
        int instance;
        ManageInstances instanceManager;
        InetAddress callerAddress;
        
        /**
         * @param className
         * @param methodName
         * @param parameters
         */
        public MethodDesc(String className, String methodName,
                Object[] parameters)
        {
            super();
            this.className = className;
            this.methodName = methodName;
            this.parameters = parameters;
        }
        
        public void setInstanceManager(ManageInstances instanceManager)
        {
            // Needed to run
            this.instanceManager = instanceManager;
        }
        
        public Object execMethod()
        {
            returnValue = instanceManager.callModuleMethod(className, instance, methodName, parameters,callerAddress);
            return returnValue;
        }
        
        public Object execMethod(int instance)
        {
            returnValue = instanceManager.callModuleMethod(className, instance, methodName, parameters,callerAddress);
            return returnValue;
        }
        
        public Object returnLastValue()
        {
            return returnValue;
        }
        
        /*
         * In case it is of some use, recover the address of the caller
         */
        public void setIPCaller(InetAddress theAddress)
        {
            callerAddress=theAddress;
        }
        
        /**
         * @return Returns the className.
         */
        public String getClassName()
        {
            return className;
        }
        /**
         * @return Returns the instance.
         */
        public int getInstance()
        {
            return instance;
        }
        /**
         * @return Returns the methodName.
         */
        public String getMethodName()
        {
            return methodName;
        }
        /**
         * @return Returns the parameters.
         */
        public Object[] getParameters()
        {
            return parameters;
        }
        /**
         * @return Returns the returnValue.
         */
        public Object getReturnValue()
        {
            return returnValue;
        }

        /**
         * @return Returns the callerAddress.
         */
        public InetAddress getCallerAddress()
        {
            return callerAddress;
        }
    }

    public static void main(String[] args)
    {
        // Just for test
        InterpretOrders myInterpret = new InterpretOrders();
        ManageInstances myManager = new ManageInstances(null);
        MethodDesc myMethod;

        try
        {
        myMethod = myInterpret
                .extractMethod("trucmuch.bidule('i','i','j'   ,   'o', 89, 76.56,\"hello\",6,\"truc\",\"truc2\",14,\"jui\")");
        myMethod.setInstanceManager(myManager);
        myMethod.execMethod(0);
        myMethod = myInterpret.extractMethod("maClasseDeTest.maMethodAMoi2(\"Ca marche ?\",450000000,'a')");
        myMethod.setInstanceManager(myManager);
        myMethod.execMethod(0);
        myMethod = myInterpret.extractMethod("trucmuch.2.bidule()");
        myMethod.setInstanceManager(myManager);
        myMethod.execMethod(0);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void setCommManager(CommManager theCommManager)
    {
        this.theCommManager = theCommManager;
    }

    public MethodDesc extractMethod(String line) throws Exception 
    {
        int startPar;
        int endPar;
        String lineLight; // Consists in the line without the spaces
        String className; // As it seems
        int instance = 0; // If wanted
        String methodName;
        int nbParameters;
        
        ArrayList paramList = new ArrayList();
        Object[] parameters;
        MethodDesc extractedMethod;

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
            /*System.out.println("I have missed the '(', is there any problem?");
            return 1;*/
            throw new Exception("I have missed the '(', is there any problem?");
        }
        endPar = lineLight.indexOf(')');
        if (endPar == -1)
        {
            //System.out.println("Where is the ')' ?");
            //return 2;
            throw new Exception("')' Missing");
        }
        if (endPar < startPar)
        {
            /*System.out.println("I expect that '(' come before ')' !");
            return 3;*/
            throw new Exception("I expect that '(' come before ')' !");
        }
        // What is the name of the asked class
        {
            int firstPointPos = lineLight.indexOf('.');
            int secondPointPos;
            if (firstPointPos == -1)
            {
                //System.out.println("I wait for class.method(...)");
                //return 4;
                throw new Exception("I wait for class.method(...)");
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
                    /*System.out
                            .println("I am waiting for a proper number, not for "
                                    + instanceStr);
                    return 5; // Of course, to be enhanced*/
                    throw new Exception("I am waiting for a proper number, not for "
                            + instanceStr);
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
                                //System.out.println("Parameter badly formed");
                                //return 6;
                                throw new Exception("Parameter badly formed");
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
                        
                        if (debugMode)
                        {
                            System.out.println("Paramètre String : " + currentLine);
                        }
                        paramList.add(currentLine);
                    } else
                    {
                        if (parametersString.charAt(pos) == '\'')
                        {
                            // It's a character
                            pos++;
                            char currentChar = parametersString.charAt(pos);
                            pos += 2;
                            nbParameters++;
                            if (debugMode)
                            {
                                System.out.println("Character : " + currentChar);
                            }
                            paramList.add(new Character(currentChar));
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
                                    if (debugMode)
                                    {
                                        System.out.println("Near the end.");
                                    }
                                    // not so beautiful but efficient
                                }
                                //Let look for a point... If it is a float...
                                if ((parametersString.indexOf('.', pos) < commaPos)
                                        && (parametersString.indexOf('.', pos) != -1))
                                {
                                    doubleValue = Double.valueOf(
                                            parametersString.substring(pos,
                                                    commaPos)).doubleValue();
                                    if (debugMode)
                                    {
                                        System.out.println("Chiffre flottant : "
                                            + doubleValue);
                                    }
                                    paramList.add(new Double(doubleValue));
                                } else
                                {
                                    // It is an integer ! (we hope)
                                    longValue = Long.valueOf(
                                            parametersString.substring(pos,
                                                    commaPos)).longValue();
                                    if (debugMode)
                                    {
                                        System.out.println("Chiffre entier : "
                                            + longValue);
                                    }
                                    paramList.add(new Long(longValue));
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
        parameters = paramList.toArray();
        
        if (debugMode)
        {
            System.out.println("It seems we want the instance " + instance
                + " of the method " + methodName + " in the class " + className
                + ".");
        }
        extractedMethod = new MethodDesc(className,methodName,parameters);
        return extractedMethod;
    }
}