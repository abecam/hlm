/*
 * Created on Sep 30, 2005
 *
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

import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  With the same interface as the LogManager, this class does NOTHING !:)
 *  So you can replace your use of the LogManager by this one, so you don't use and generate any file and output!
 * @author Alain Becam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class LogManagerVoid extends LogManager
{
    boolean debugMode=true;

    public int initLog()
    {
        return 0;
    }
    
    public int initLog(String Name)
    {
        if (debugMode)
            System.out.println("Log instance: "+Name);
        return 0;
    }

    public int add2Log(String line)
    {
        if (debugMode)
            System.out.println("log: "+line);
        return 0;
    }
   
    int levelGen=6; // All
    int levelExc=6; // All too
    
    /**
     * @return Returns the levelExc.
     */
    public int getLevelExc()
    {
        return levelExc;
    }

    /**
     * @param levelExc The levelExc to set.
     */
    public void setLevelExc(int levelExc)
    {
        this.levelExc = levelExc;
    }

    /**
     * @return Returns the levelGen.
     */
    public int getLevelGen()
    {
        return levelGen;
    }

    /**
     * @param levelGen The levelGen to set.
     */
    public void setLevelGen(int levelGen)
    {
        this.levelGen = levelGen;
    }

    public int add2Log(int level,String line)
    {
        if (level<levelGen)
            return add2Log(line);
        else
            return 0; // Ok but nothing
    }
    
    public int add2Log(int level,Exception eToPr)
    {
        if (level<levelExc)
            return add2Log(eToPr);
        else
            return 0; // Ok but nothing
    }
    public int add2Log(Exception eToPr)
    {
    	eToPr.printStackTrace();
        return 0;
    }
    
    public int closeLog()
    {
        return 0;
    }
    /**
     *  
     */
    public LogManagerVoid()
    {
    	;
    }
    
    public void finalize()
    {
       
    }

	public void setSystemEcho(boolean systemEcho) {
		
	}
}