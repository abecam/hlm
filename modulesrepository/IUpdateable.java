/*
 * Created on Oct 13, 2005
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

/**
 * Implementing this interface means that your module can be updated by the system.
 * So you can use SModule.updateModule("OldModule","NewModule","INewModule") to replace one old module,
 * and the method releaseMe (object newModule) is called by the instance manager 
 * prior to remove the old module and install the new one. Java doesn't 
 * allow two classes with the same name, so we swap between the package 
 * modulerepository and modulerepository2. If the current module is in modulerepository, the
 * system will considere that the new one is in modulerepository2, and similarly will
 * considere that the new one is in modulerepository if the old one is in modulerepository2.
 * Don't forget that your module might be concurrently used, so it's better to "lock" it before
 * you copy the data in the new module, so no changes will be lost.
 * 
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IUpdateable
{
    // Method to be released, return 0 if success, the update is refused if the result is different!
    public int releaseMe (Object newModule);
    
    // Method to give the version. If two same modules are present (on in modulerepository,
    // one in modulerepository2), the instance manager ask the version to know which one is new
    public int getVersion ();
}

