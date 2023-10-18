/*
 * Created on Mar 22, 2005
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


package net;

/**
 * A result from a channel.
 * 
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChannelResult
{
    String channelName;
    String ownerName;
    String dataSetName;
    char[] result;
    
    
    /**
     * @return Returns the channelName.
     */
    public String getChannelName()
    {
        return channelName;
    }
    /**
     * @param channelName The channelName to set.
     */
    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }
    /**
     * @return Returns the dataSetName.
     */
    public String getDataSetName()
    {
        return dataSetName;
    }
    /**
     * @param dataSetName The dataSetName to set.
     */
    public void setDataSetName(String dataSetName)
    {
        this.dataSetName = dataSetName;
    }
    /**
     * @return Returns the ownerName.
     */
    public String getOwnerName()
    {
        return ownerName;
    }
    /**
     * @param ownerName The ownerName to set.
     */
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }
    /**
     * @return Returns the result.
     */
    public char[] getResult()
    {
        return result;
    }
    /**
     * @param result The result to set.
     */
    public void setResult(char[] result)
    {
        this.result = result;
    }
}
