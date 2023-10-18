/*
 * Created on Jun 4, 2005
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

import java.net.InetAddress;

import core.CommManager;
import core.ManageInstances;

import net.ChannelResult;

import modulesrepository.FacilityUser;

/**
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ToGetResults implements FacilityUser
{
    String resultFrom;
    boolean completed=false;
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#distantCallResultListener(java.lang.String)
     */
    public void distantCallResultListener(String result)
    {
        this.resultFrom = result;
        completed = true;
    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#distantMultiCallResultListener(java.lang.String[])
     */
    public void distantMultiCallResultListener(String[] result)
    {
        // TODO Auto-generated method stub
        StringBuffer globalResult = new StringBuffer();
        for (int i=0; i<result.length ; i++)
        {
            globalResult.append(result[i]);
        }
        this.resultFrom = new String(globalResult);
        completed = true;
    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#getCommManager(mmrolink.CommManager)
     */
    public void getCommManager(CommManager theCommManager)
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#getManageInstances(mmrolink.ManageInstances)
     */
    public void getManageInstances(ManageInstances instancesManager)
    {
        // TODO Auto-generated method stub

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
    
    public boolean isDataReady()
    {
        return completed;
    }
    
    public void dataConsumed()
    {
        completed = false;
    }
    
    public String getResult()
    {
        return resultFrom;
    }
    /* (non-Javadoc)
     * @see modulesrepository.FacilityUser#setOneCaller(java.net.InetAddress)
     */
    public void setOneCaller(InetAddress theAddress)
    {
        // TODO Auto-generated method stub
        
    }
}
