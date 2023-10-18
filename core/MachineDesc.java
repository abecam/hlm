/*
 * Created on Mar 23, 2005
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

import java.util.ArrayList;

/**
 * The flat machine list... coming from the document.
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class MachineDesc
{
    // A description for a machine, coming from the description file.
    // The DistantMachine class in the comm manager is used to the actually used distant machines
    // This one is just used to know the machines on the net, and their type.
    // And to construct the DistantMachine list in the comm manager.
    String hostName;
    String type;	// Might be PcClient, MobileClient, PCServer, MobileServer
    String alias;
    ArrayList groupe; // List of groupe
    String language; // For information only 
    /**
     * @return Returns the alias.
     */
    public String getAlias()
    {
        return alias;
    }
    /**
     * @param alias The alias to set.
     */
    public void setAlias(String alias)
    {
        this.alias = alias;
    }
    /**
     * @return Returns the groupe.
     */
    public ArrayList getGroupe()
    {
        return groupe;
    }
    /**
     * @param groupe The groupe to set.
     */
    public void setGroupe(ArrayList groupe)
    {
        this.groupe = groupe;
    }
    /**
     * @return Returns the hostName.
     */
    public String getHostName()
    {
        return hostName;
    }
    /**
     * @param hostName The hostName to set.
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
    /**
     * @return Returns the language.
     */
    public String getLanguage()
    {
        return language;
    }
    /**
     * @param language The language to set.
     */
    public void setLanguage(String language)
    {
        this.language = language;
    }
    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type)
    {
        this.type = type;
    }
}
