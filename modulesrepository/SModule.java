/*
 * Created on Mar 11, 2005
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

package modulesrepository;

import core.ExtractInterface;
import core.ManageInstances;
import core.ManageModulesList;

/**
 * This class is mainly the module access to ManageModulesList, to add dynamically a module via the module system.
 * It is a special module, which must be recognised by the system to update the instance list...
 * 
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SModule implements SModuleInterface
{
    ManageModulesList inSysModuleMan = new ManageModulesList();
    StringBuffer lastModuleDescription;
    
    public int addModule (String className, String interfaceName)
    {
        ExtractInterface ourExtractor = new ExtractInterface();
        
        int extrResult;
        // Time to add the class description. Notice that we do NOT
        // verify something.
        // We also add it at the end, as it seems better to not organize
        // them...
        extrResult = ourExtractor
                .recoverClass(className, interfaceName);
        if (extrResult == 0)
        {
            extrResult = ourExtractor.extractComponents();
            if (extrResult == 0)
            {
                lastModuleDescription = ourExtractor.getTranscript();
            }
        }
        return (inSysModuleMan.addModule(className, interfaceName));
    }
    
    public int removeModule(String className)
    {
        // To be implemented (in ManageModulesList)
        return inSysModuleMan.removeModule(className);
    }
    
    public int invokeLastAddedModule(ManageInstances instManager)
    {
        // This method might be called only by the management system, to invoke the last added Module
        return (instManager.addModuleOnFly(lastModuleDescription));
    }
}
