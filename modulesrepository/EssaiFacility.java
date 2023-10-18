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

package modulesrepository;

import java.net.InetAddress;

import core.CommManager;
import core.ManageInstances;

import net.ChannelResult;


/**
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EssaiFacility implements FacilityUser, TestInterface
{
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#distantCallResultListener(java.lang.String)
     */
    public void distantCallResultListener(String result)
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#distantMultiCallResultListener(java.lang.String[])
     */
    public void distantMultiCallResultListener(String[] result)
    {
        // TODO Auto-generated method stub

    }
    CommManager theCommManager;
    ManageInstances instancesManager;
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#getCommManager(mmrolink.CommManager)
     */
    public void getCommManager(CommManager theCommManager)
    {
        // TODO Auto-generated method stub
        this.theCommManager = theCommManager;

    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#getManageInstances(mmrolink.ManageInstances)
     */
    public void getManageInstances(ManageInstances instancesManager)
    {
        // TODO Auto-generated method stub
        this.instancesManager = instancesManager;
    }
    /* (non-Javadoc)
     * @see modulesrepository.TestInterface#maDerniereMethode(java.lang.String)
     */
    public void maDerniereMethode(String nomDuFilm)
    {
        // TODO Auto-generated method stub
        System.out.println(instancesManager.giveInstanceNb("EssaiFacility"));
        System.out.println(theCommManager);
    }
    /* (non-Javadoc)
     * @see modulesrepository.TestInterface#maMethodAMoi(java.lang.String, java.lang.Integer, java.lang.Character)
     */
    public int maMethodAMoi(String bidule, Integer truc, Character trucmuch)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    /* (non-Javadoc)
     * @see modulesrepository.TestInterface#maMethodAMoi2(java.lang.String, int, char)
     */
    public int maMethodAMoi2(String bidule, int truc, char trucmuch)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    /* (non-Javadoc)
     * @see modulesrepository.TestInterface#monAutreMethodAMoi(int, java.lang.String, int, java.lang.String)
     */
    public String monAutreMethodAMoi(int EhOh, String JeSuisBo, int IAmlast, String JsuisPasBo)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#receiveDataFromChannel(mmrocom.ChannelResult)
     */
    public void receiveDataFromChannel(ChannelResult result)
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#receiveExtRefs(java.lang.Object[])
     */
    public void receiveExtRefs(Object[] references)
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#setOneCaller(java.net.InetAddress)
     */
    public void setOneCaller(InetAddress theAddress)
    {
        // TODO Auto-generated method stub
        
    }
}
