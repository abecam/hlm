/*
 * Created on Feb 3, 2005
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

import java.lang.reflect.Method;

/**
 * The goal of this class is to use introspection to express the public
 * interface of one Java class. It might be good to be able to add automatically
 * some documentation...
 * @author Alain Becam
 * 
 *  
 */
public class ExtractInterface
{

    Class exploredClass;

    Class exploredInterface;

    Method[] setOfMethods;

    String nameOfClass;

    Method methodArray;

    StringBuffer classTranscript;

    public static void main(String[] args)
    {
        int result;
        // Just for test
        ExtractInterface monExtractInterface = new ExtractInterface();
        result = monExtractInterface.recoverClass("JustForTest",
                "InterfaceForJustForTest");
        if (result != 0)
        {
            System.out.println("Oups " + result);
        }
        result = monExtractInterface.extractComponents();
        if (result != 0)
        {
            System.out
                    .println("Oups, problème dans la récupération des méthodes "
                            + result);
        }
    }

    public int recoverClass(String className, String InterfaceName)
    {
        Class[] setOfInterfaces;
        boolean isImplemented = false; // Indicate if the given interface is
        // implemented by the given class
        try
        {
            // Here we just verify the class exist in the module repository (we are working locally...)
            exploredClass = Class.forName("modulesrepository."+className);
            nameOfClass = className; // So, the caller must create the object !
            // And not destructs it just after...
            try
            {
                exploredInterface = Class.forName("modulesrepository."+InterfaceName);
                // Exception to manage, if no class for this name !
                if (!exploredInterface.isInterface())
                {
                    // Exception to raise, we expect one interface
                }
                setOfInterfaces = exploredClass.getInterfaces();
                for (int i = 0; i < setOfInterfaces.length; i++)
                {
                    if (setOfInterfaces[i] == exploredInterface)
                    {
                        isImplemented = true;
                    }
                }
                if (!isImplemented)
                {
                    return 3; // To be enhanced.
                }
                classTranscript = new StringBuffer("Module:" + nameOfClass
                        + ": Java\n"); //////////////
                // It's all here, now it's time to do something !
                return 0;
            } catch (Exception e)
            {
                return 1; // To be enhanced!!!
            }
        } catch (Exception e)
        {
            return 2; // To be enhanced!!!
        }
    }
    
    public int recoverClass4Distant(String className, String InterfaceName, String hostName)
    {
        Class[] setOfInterfaces;
        boolean isImplemented = false; // Indicate if the given interface is
        // implemented by the given class
        try
        {
            // Here we just verify the class exist in the module repository (we are working locally...)
            exploredClass = Class.forName("modulesrepository."+className);
            nameOfClass = className; // So, the caller must create the object !
            // And not destructs it just after...
            try
            {
                exploredInterface = Class.forName("modulesrepository."+InterfaceName);
                // Exception to manage, if no class for this name !
                if (!exploredInterface.isInterface())
                {
                    // Exception to raise, we expect one interface
                }
                setOfInterfaces = exploredClass.getInterfaces();
                for (int i = 0; i < setOfInterfaces.length; i++)
                {
                    if (setOfInterfaces[i] == exploredInterface)
                    {
                        isImplemented = true;
                    }
                }
                if (!isImplemented)
                {
                    return 3; // To be enhanced.
                }
                classTranscript = new StringBuffer("Module:" + hostName + ":" + nameOfClass
                        + ": Java\n"); //////////////
                // It's all here, now it's time to do something !
                {
                    int result = this.extractComponents();
                    if (result != 0)
                    {
                        System.out.println("Oups, problème dans la récupération des méthodes " + result);
                    }
                }
                return 0;
            } catch (Exception e)
            {
                return 1; // To be enhanced!!!
            }
        } catch (Exception e)
        {
            return 2; // To be enhanced!!!
        }
    }
    
    public int extractComponents()
    {
        Class[] setOfParameters;
        Class returnValue;
        String returnName;
        StringBuffer methodsList = new StringBuffer("Methods:\n");
        // Here we will recover all the useful stuffs we needs, and check if it
        // is usable !
        try
        {
            setOfMethods = exploredInterface.getMethods();
            // If it has worked, we want to describe the interface (it is the
            // goal !) and to verify its integrity
            // It means that we allow only string, numeric and binaries fields
            // (and NOT generic objects).
            // The purpose is to communicate from one language to another, not
            // to serialize objects, so we try to define a common subset of the
            // languages.
            for (int i = 0; i < setOfMethods.length; i++)
            {
                StringBuffer globalLine;
                //System.out.println(setOfMethods[i].getName());
                globalLine = new StringBuffer("Method:"
                        + setOfMethods[i].getName() + ":");
                setOfParameters = setOfMethods[i].getParameterTypes();
                if (setOfParameters.length == 0)
                {
                    globalLine.append(":noparameters");
                }
                for (int iPar = 0; iPar < setOfParameters.length; iPar++)
                {
                    String nameOfParam = setOfParameters[iPar].getName();
                    //System.out.println(setOfParameters[iPar].getName());

                    if (nameOfParam.equals("java.lang.Long"))
                    {
                        globalLine.append("long");
                    } else
                    {
                        if (nameOfParam.equals("java.lang.Integer"))
                        {
                            globalLine.append(":int");
                        } else
                        {
                            if (nameOfParam.equals("java.lang.Character"))
                            {
                                globalLine.append(":char");
                            } else
                            {
                                if (nameOfParam.equals("java.lang.String"))
                                {
                                    globalLine.append(":string");
                                } else
                                {
                                    if (nameOfParam
                                            .equals("java.lang.StringBuffer"))
                                    {
                                        globalLine.append(":string");
                                    } else
                                    {
                                        if (nameOfParam.equals("int")
                                                || nameOfParam.equals("long")
                                                || nameOfParam.equals("double")
                                                || nameOfParam.equals("float")
                                                || nameOfParam.equals("char"))
                                        {
                                            globalLine
                                                    .append(":" + nameOfParam);
                                        } else
                                        {
                                            System.out
                                                    .println("This type is not allowed");
                                            // And raise an exception
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
                globalLine.append(". return:");
                returnValue = setOfMethods[i].getReturnType();
                returnName = returnValue.getName();
                if (returnName.equals("int") || returnName.equals("long")
                        || returnName.equals("double")
                        || returnName.equals("float")
                        || returnName.equals("char")
                        || returnName.equals("void"))
                {
                    globalLine
                            .append(returnValue.getName() + ". endofmethod. ");
                } else
                    if (returnName.equals("java.lang.Integer"))
                    {
                        globalLine.append("int. endofmethod. ");
                    }
                    else
                    {
                        if (returnName.equals("java.lang.Long"))
                        {
                            globalLine.append("long. endofmethod. ");
                        }
                        else
                        {
                            if (returnName.equals("java.lang.Character"))
                            {
                                globalLine.append("char. endofmethod. ");
                            }
                            else
                            {
                                if (returnName.equals("java.lang.String"))
                                {
                                    globalLine.append("string. endofmethod. ");
                                }
                                else
                                {
                                    if (returnName.equals("java.lang.StringBuffer"))
                                    {
                                        globalLine.append("StringBuffer. endofmethod. ");
                                    }
                                    else
                                    {
                                        System.out.println("This return type is not allowed");
                                        return 3;
                                        // Or raise an exception ?
                                    }
                                        
                                }
                            }
                        }
                    }
            
                //System.out.println(returnValue.getName());
                System.out.println(globalLine);
                methodsList.append(globalLine + "\n");

            }
            methodsList.append("No more methods.\n"); // We need a way to add
            // some comments...
            classTranscript.append(methodsList);
            System.out.println(classTranscript);
        } catch (Exception e)
        {
            return 1; //...
        }
        return 0;
    }

    public StringBuffer getTranscript()
    {
        return classTranscript;
    }
}