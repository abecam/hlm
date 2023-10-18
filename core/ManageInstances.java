/*
 * Created on Feb 9, 2005
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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import modulesrepository.FacilityUser;
import modulesrepository.SModule;

/**
 * This class must read the local "Module" description and use it to instanciate
 * one first objects of each, if the method onDemand is not given on the module.
 * It also allows the creation of instance on demand.
 * 
 * Finally, this class take care of using these instances and of destructing
 * them.
 * 
 * We store the information about the usable classes (even if not instanciate)
 * into an map, which indicate the name of a class and the number of instance.
 * 
 * The instanciated classes are stored into a collection.
 * The differents callers MUST be well informed of the existing methods... We do
 * not check that, even if we have to supply a safe error-support.
 * @author Alain Becam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ManageInstances
{
    //Class[] classSet;
    //Method[][] MethodSet;
    CommManager theCommManager;
    HashMap managedModules; // Contain the list of ALL local modules (we are not
                            // managing external module here... )
    Object externalRef[]; // Array of external references (typically the "this"  of some important class before running the MainManager)

    // Even if there is no instance.
    
    boolean debugMode = false; // Switch the verbose mode...

    protected class ModuleJava
    {
        Class moduleClass;
        ArrayList instances;
        ManageInstances instancesManager;
        boolean hasFacility;

        int refCounter = 0;

        public ModuleJava(ManageInstances instancesManager)
        {
            instances = new ArrayList();
            hasFacility=false;
            this.instancesManager = instancesManager;
        }

        /**
         * @return Returns the instances.
         */
        public ArrayList getInstances()
        {
            return instances;
        }

        /**
         * @param instances
         *            The instances to set.
         */
        public void setInstances(ArrayList instances)
        {
            this.instances = instances;
        }

        /**
         * @return Returns the moduleClass.
         */
        public Class getModuleClass()
        {
            return moduleClass;
        }

        /**
         * @param moduleClass
         *            The moduleClass to set.
         */
        public void setModuleClass(Class moduleClass)
        {
            this.moduleClass = moduleClass;
        }

        /**
         * @return Increase the refCounter.
         */
        public void incRefCounter()
        {
            this.refCounter++;
        }

        /**
         * @param decrease
         *            the refCounter.
         */
        public void decRefCounter()
        {
            this.refCounter--;
            if (refCounter < 0)
            {
                // To be managed...
            }
        }

        /**
         * @return the refCounter.
         */
        public int getRefCounter()
        {
            return (this.refCounter);
        }

        public int addInstance() throws Exception
        {
            Object oneNewInstance;
            oneNewInstance = moduleClass.newInstance();
            // If the module implement the interface FacilityUser, we give it the reference to the instance manager and to the comm. manager
            Class[] interfaces = moduleClass.getInterfaces();
            //boolean wantFacility = false;
            for (int iInt=0; iInt < interfaces.length ; iInt++)
            {
                if (interfaces[iInt].getName() == "modulesrepository.FacilityUser")
                {
                    ((FacilityUser ) oneNewInstance).getCommManager(theCommManager);
                    ((FacilityUser ) oneNewInstance).getManageInstances(instancesManager);
                    ((FacilityUser ) oneNewInstance).receiveExtRefs(externalRef);
                    
                    hasFacility=true; // So we know that this module has extra-needs.
                }
            }
            
            instances.add(oneNewInstance);
            this.incRefCounter();

            return refCounter;
        }

        public int removeTopInstance() throws Exception
        {
            if (refCounter > 0)
            {
                instances.remove(instances.size());
                this.decRefCounter();
                return refCounter;
            } else
            {
                return -1;
            }
        }

        public int cleanModule()
        {
            // In case you are the responsible of a module and you want to erase
            // or deeply change one of them, this method safely clean the
            // module list.
            instances.clear();

            return 0;
        }

        public Object invokeMethod(int instance, String methodName, Object[] parameters)
        {
            if (instance < refCounter)
            {
                Method usedMethod = null;
                Method[] methodsArray = moduleClass.getDeclaredMethods();
                if (debugMode)
                {
                    for (int iParam = 0; iParam < parameters.length; iParam++)
                    {
                        System.out.println(parameters[iParam].getClass().getName());
                    }
                }
                {
                    for (int iMethod = 0; iMethod < methodsArray.length; iMethod++)
                    {
                        if (methodsArray[iMethod].getName().endsWith(methodName))
                        {
                            usedMethod = methodsArray[iMethod]; // No
                                                                // polymorphism
                                                                // !!!!!
                            break;
                        }
                    }
                }
                if (usedMethod != null)
                {
                    Object returnedObject;

                    try
                    {
                        if (debugMode)
                        {
                            System.out.println(instances.get(instance)+"  --  "+usedMethod);
                        }
                        returnedObject = usedMethod.invoke(instances.get(instance), parameters);
                        return returnedObject;
                    } catch (java.lang.IllegalArgumentException e)
                    {
                        // We might have a long instead of an int... Or a double instead of a float or something like that.
                        Object[] paramType = usedMethod.getParameterTypes();
                        Object[] newParameters = new Object[paramType.length];
                        if (parameters.length != paramType.length)
                        {
                            // Missed ! It cannot be valide
                            System.out.println("Invalide number of argument");
                            e.printStackTrace();
                        }
                        for (int iParamType = 0; iParamType < paramType.length; iParamType++)
                        {
                            if (!((Class )(paramType[iParamType])).equals(parameters[iParamType].getClass()))
                            {
                                if (debugMode)
                                {
                                    System.out.println("Type conversion "+((Class )(paramType[iParamType]))+":"+Long.class);
                                }
                                if (((Class )(paramType[iParamType])).equals(Long.class)
                                        || ((Class )(paramType[iParamType])).equals(long.class))
                                {
                                    if (debugMode)
                                    {
                                        System.out.println("Type conversion Long 1");
                                    }
                                    if ((parameters[iParamType].getClass().equals(Integer.class))
                                            || (parameters[iParamType].getClass().equals(Character.class))
                                            || (parameters[iParamType].getClass().equals(int.class))
                                            || (parameters[iParamType].getClass().equals(char.class)))
                                    {
                                        if (debugMode)
                                        {
                                            System.out.println("Type conversion Long 2");
                                        }
                                        // Ok to convert it. It seems that the
                                        // toString method is common to all
                                        // possible classes.
                                        newParameters[iParamType] = new Long(parameters[iParamType].toString());
                                        if (debugMode)
                                        {
                                            System.out.println("Long : "+newParameters[iParamType]);
                                        }
                                    }
                                   else
                                   {
                                       e.printStackTrace();
                                   }
                                }
                                else
                                {
                                    if (((Class )(paramType[iParamType])).equals(Integer.class)
                                            || ((Class )(paramType[iParamType])).equals(int.class))
                                    {
                                        if (debugMode)
                                        {
                                            System.out.println("Type conversion Integer 1");
                                        }
                                        if ((parameters[iParamType].getClass().equals(Long.class))
                                                || (parameters[iParamType].getClass().equals(Character.class))
                                                || (parameters[iParamType].getClass().equals(long.class))
                                                || (parameters[iParamType].getClass().equals(char.class)))
                                        {
                                            if (debugMode)
                                            {
                                                System.out.println("Type conversion Integer 2");
                                            }
                                            // Dangerous but we still try to
                                            // convert
                                            // it...
                                            newParameters[iParamType] = new Integer(parameters[iParamType].toString());
                                            if (debugMode)
                                            {
                                                System.out.println("Integer : "+newParameters[iParamType]);
                                            }
                                        }
                                        else
                                        {
                                            e.printStackTrace();
                                        }
                                    }else
                                    {
                                        if (((Class )(paramType[iParamType])).equals(Float.class)
                                                || ((Class )(paramType[iParamType])).equals(float.class))
                                        {
                                            if (debugMode)
                                            {
                                                System.out.println("Type conversion Float 1");
                                            }
                                            if ((parameters[iParamType].getClass().equals(Double.class))
                                                    || (parameters[iParamType].getClass().equals(double.class)))
                                            {
                                                if (debugMode)
                                                {
                                                    System.out.println("Type conversion Float 2");
                                                }
                                                // Dangerous but we still try to
                                                // convert
                                                // it...
                                                newParameters[iParamType] = new Float(((Double )parameters[iParamType]).floatValue());
                                                if (debugMode)
                                                {
                                                    System.out.println("Float : "+newParameters[iParamType]);
                                                }
                                            }
                                            else
                                            {
                                                e.printStackTrace();
                                            }
                                        }else
                                        {
                                    // Not managed type mismatch, we try anyway
                                    // (for example int work for Integer)
                                    newParameters[iParamType] = parameters[iParamType];
                                        }
                                    }
                                }
                            }
                            else
                            {
                                // No type mismatch
                                newParameters[iParamType] = parameters[iParamType];
                            }
                        }
                        try
                        {
                            returnedObject = usedMethod.invoke(instances.get(instance), newParameters);
                            return returnedObject;
                        } catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            return null;
        }
        
        public Object invokeMethodNoCast(int instance, String methodName, Object[] parameters)
        {
            if (instance < refCounter)
            {
                Method usedMethod = null;
                Method[] methodsArray = moduleClass.getDeclaredMethods();
                if (debugMode)
                {
                    for (int iParam = 0; iParam < parameters.length; iParam++)
                    {
                        System.out.println(parameters[iParam].getClass().getName());
                    }
                }
                {
                    for (int iMethod = 0; iMethod < methodsArray.length; iMethod++)
                    {
                        if (methodsArray[iMethod].getName().endsWith(methodName))
                        {
                            usedMethod = methodsArray[iMethod]; // No
                            // polymorphism
                            // !!!!!
                            break;
                        }
                    }
                }
                if (usedMethod != null)
                {
                    try
                    {
                        Object returnedObject = usedMethod.invoke(instances.get(instance), parameters);
                        return returnedObject;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            return null;
        }
        /**
         * @return Returns the hasFacility.
         */
        public boolean hasTheFacility()
        {
            return hasFacility;
        }
    }

    public static void main(String[] args)
    {
        ManageInstances myInstManager = new ManageInstances(null);

        //ModuleJava oneModule = (ModuleJava
        // )myInstManager.managedModules.get("mmrolink.maClasseDeTest");
        Object[] parametrs = new Object[3];
        parametrs[0] = "Ah Ah";
        parametrs[1] = new Integer(78);
        parametrs[2] = new Character('e');
        //oneModule.invokeMethod(0,"maMethodAMoi2",parametrs);
        myInstManager.callModuleMethod("maClasseDeTest", 0, "maMethodAMoi2", parametrs,null);
        /*
         * try { Method uneMethod; Class oneClass =
         * Class.forName("mmrolink.maClasseDeTest"); Class[] params = new
         * Class[3]; params[0] = Class.forName("java.lang.String"); params[1] =
         * int.class; params[2] = char.class; Object oneObject =
         * oneClass.newInstance(); Object[] parameters = new Object[3];
         * parameters[0] = "Ah Ah"; parameters[1] = new Integer(78);
         * parameters[2] = new Character('e'); uneMethod =
         * oneClass.getMethod("maMethodAMoi2", params);
         * uneMethod.invoke(oneObject, parameters); } catch (Exception e) {
         * e.printStackTrace(); }
         */
    }

    public ManageInstances(Object[] references)
    {
        this.externalRef = references;
        managedModules = new HashMap();
        listClass();
    }
    
    public void setCommManager(CommManager theCommManager)
    {
        this.theCommManager = theCommManager;
    }

    /**
     * @return int, 0 anyway for now, but might be used to give a result.
     */
    public int listClass()
    {
        // This method create the array of class, for now
        // We use a common document to store this list, see ManageModulesList
        ManageModulesList myModListManager = new ManageModulesList();
        StringBuffer myDocument;

        myDocument = myModListManager.readDocument();
        // For test
        if (debugMode)
        {
            System.out.println(myModListManager.readDocument());
        }
        // A little bit crappy, it would be better to read line after line, BUT
        // we also instanciate and store all the classes...
        // So it is far away from a clean repositery. But if needed, it would be
        // better to use something existent (EJB for exemple)

        // We now have to decode the file...

        while (myDocument.indexOf("Module:") != -1)
        {
            ModuleJava oneModule = new ModuleJava(this);
            boolean onDemand = false;

            String uneSection = myModListManager.extractSection(myDocument);
            StringBuffer subSection;
            if (debugMode)
            {
                System.out.println("Un module : " + uneSection + "\n Fin du module \n\n");
                System.out.println("Using language :" + myModListManager.checkLanguage(uneSection) + "---");
                System.out.println("The class used is : " + myModListManager.extractClassName(uneSection) + "---");
            }
            try
            {
                String className = myModListManager.extractClassName(uneSection);
                if (managedModules.containsKey("modulesrepository."+className))
                {
                    // Duplicated module, it is NOT managed and allowed, so we
                    // jump...
                    System.out.println("Duplicated module !!!!");
                } 
                else
                {

                    oneModule.setModuleClass(Class.forName("modulesrepository."+myModListManager.extractClassName(uneSection)));
                    System.out.println("Module to instantiate: "+(Class.forName("modulesrepository."+myModListManager.extractClassName(uneSection))).getName());
                    subSection = new StringBuffer(uneSection);
                    while (subSection.indexOf("Method:") != -1)
                    {
                        String uneMethod = myModListManager.extractMethod(subSection);
                        if (debugMode)
                        {
                            System.out.println("Method inside: " + uneMethod + "---");
                        }
                        onDemand = onDemand | (myModListManager.extractMethodName(uneMethod)).equals("onDemand");
                    }
                    if (!onDemand)
                    {
                        // If nothing specified, we instanciate a default
                        // module.
                        if (debugMode)
                        {
                            System.out.println("Class to instanciate: " + oneModule.getModuleClass().getName() + "---");
                        }
                        oneModule.addInstance();
                    }
                    managedModules.put(oneModule.getModuleClass().getName(), oneModule);
                    if (debugMode)
                    {
                        System.out.println(myDocument);
                    }
                }
            } catch (Exception e)
            { // A travailler !!!
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    /**
     * @return int, the result, 0 if OK, and anyway 0 for now...
     */
    public int addModuleOnFly(StringBuffer moduleDesc)
    {
        // To add "on the fly" a module, not only when we start... It a facility to NOT reuse listClass, if the module list become big
        // But for now, as we do not allow the duplicated modules, it is enough to restart listClass to add a module on the fly (but it is less elegant and performant).
        ManageModulesList myModListManager = new ManageModulesList(); // Still needed, as it contains the method to extract the module elements from the description
        
        while (moduleDesc.indexOf("Module:") != -1)
        {
            ModuleJava oneModule = new ModuleJava(this);
            boolean onDemand = false;

            String uneSection = myModListManager.extractSection(moduleDesc);
            StringBuffer subSection;
            if (debugMode)
            {
                System.out.println("Un module : " + uneSection + "\n Fin du module \n\n");
                System.out.println("Using language :" + myModListManager.checkLanguage(uneSection) + "---");
                System.out.println("The class used is : " + myModListManager.extractClassName(uneSection) + "---");
            }
            try
            {
                String className = myModListManager.extractClassName(uneSection);
                if (managedModules.containsKey("modulesrepository."+className))
                {
                    // Duplicated module, it is NOT managed and allowed, so we
                    // jump...
                    System.out.println("Duplicated module !!!!");
                } 
                else
                {

                    oneModule.setModuleClass(Class.forName("modulesrepository."+myModListManager.extractClassName(uneSection)));
                    
                    if (debugMode)
                    {
                        System.out.println((Class.forName("modulesrepository."+myModListManager.extractClassName(uneSection))).getName());
                    }
                    subSection = new StringBuffer(uneSection);
                    while (subSection.indexOf("Method:") != -1)
                    {
                        String uneMethod = myModListManager.extractMethod(subSection);
                        
                        if (debugMode)
                        {
                            System.out.println("Method inside: " + uneMethod + "---");
                        }
                        onDemand = onDemand | (myModListManager.extractMethodName(uneMethod)).equals("onDemand");
                    }
                    if (!onDemand)
                    {
                        // If nothing specified, we instanciate a default
                        // module.
                        if (debugMode)
                        {
                            System.out.println("Class to instanciate: " + oneModule.getModuleClass().getName() + "---");
                        }
                        oneModule.addInstance();
                    }
                    managedModules.put(oneModule.getModuleClass().getName(), oneModule);
                }
            } catch (Exception e)
            { // A travailler !!!
                e.printStackTrace();
            }
        }
        return 0;
    }


    public int addInstance(String className)
    {
        if (!managedModules.containsKey("modulesrepository."+className))
        {
            return -1; // To be supported better...
        }
        try
        {
            ((ModuleJava) managedModules.get("modulesrepository."+className)).addInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public int giveInstanceNb(String className)
    {
        if (managedModules.containsKey("modulesrepository."+className))
        {
            return ((ModuleJava) managedModules.get("modulesrepository."+className)).getRefCounter();
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * This method return the Object reference for an instance of one module. It has to be enhance to manages errors...
     * @param className
     * @param instance
     * @return The reference of the given instance
     */
    public Object giveInstanceRef(String className, int instance)
    {
        return ((Object )(((ModuleJava) managedModules.get("modulesrepository."+className)).getInstances()).get(instance));
    }

    public int cleanModule(String className)
    {
        // In case you are the responsible of a module and you want to erase or
        // deeply change one of them, this method safely clean the
        // module list.
        ((ModuleJava) managedModules.get("modulesrepository."+className)).cleanModule();

        return 0;
    }

    public Object callModuleMethod(String ModuleName, int instance, String MethodName, Object[] parameters,InetAddress callerAddress)
    {
        if (managedModules.containsKey("modulesrepository."+ModuleName))
        {
            ModuleJava usedModule = (ModuleJava) managedModules.get("modulesrepository."+ModuleName);
            if (!ModuleName.equals("SModule")) // The "Super" module is here to manage the other module, so we have different behaviours...
            {    
                if (usedModule.hasTheFacility())
                {
                    Object theParams[]=new Object[1];
                    theParams[0] = callerAddress;
                    usedModule.invokeMethodNoCast(instance, "setOneCaller", theParams);
                }
                return (usedModule.invokeMethod(instance, MethodName, parameters));
            }
            
            {
                if (MethodName.equals("addModule"))
                {
                    usedModule.invokeMethod(instance, MethodName, parameters);
                    int result = ((SModule )usedModule.getInstances().get(instance)).invokeLastAddedModule(this);
                        
                    return (new Integer(result)); // Nothing to return...
                }
                
                {
                    // For the moment nothing more...
                    return (usedModule.invokeMethod(instance, MethodName, parameters));
                }
            }
        } 
        return null;
    }
    
    // This method will replace one module with a new one without any consideration.
    // It just stop the old one and start the new one.
    public int replaceModule()
    {
        return 0;
    }
}