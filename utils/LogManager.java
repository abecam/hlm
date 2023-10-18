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
 * Small class to manage a log file. Try to create small logs instead of one big one.
 * 
 * @author Alain Becam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class LogManager
{
    DateFormat myDateFormat;
    DateFormat hoursFormat;
    DateFormat logEntryFormat;
    
    String optName=null;

    RandomAccessFile logFile;
    
    boolean	systemEcho;

    String nameOfFile;
    String lastHour; 	// Each hour, we will create a new log, this string store the last one, so we can see if it change.
    int nbOfLines; 		// Number of line in a file, we create a new file after 10.000 lines.
    int nbForThisHour; 	// Number of file for an hour.

    public int initLog()
    {
        nameOfFile = "logs/"+myDateFormat.format(new Date()) + "h-gamesessionlog.txt";
        lastHour = hoursFormat.format(new Date());

        try
        {
            logFile = new RandomAccessFile(nameOfFile, "rw");

            logFile.seek(logFile.length());
        } catch (Exception e)
        {
            System.out.println("Log init problem");
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
    
    public int initLog(String Name)
    {
        optName=Name;
        
        nameOfFile = "logs/"+Name+"-"+myDateFormat.format(new Date()) + "h-gamesessionlog.txt";
        lastHour = hoursFormat.format(new Date());

        try
        {
            logFile = new RandomAccessFile(nameOfFile, "rw");

            logFile.seek(logFile.length());
        } catch (Exception e)
        {
            System.out.println("Log init problem");
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int add2Log(String line)
    {
        String actualHour = hoursFormat.format(new Date());
        //System.out.println(">"+actualHour+"\n");
        nbOfLines++;
        if (!actualHour.equals(lastHour))
        {
            try{
            // We close the old log file and we create a new one.
            logFile.close();
            if (optName == null)
                nameOfFile = "logs/"+myDateFormat.format(new Date()) + "h-gamesessionlog.txt";
            else
                nameOfFile = "logs/"+optName+"-"+myDateFormat.format(new Date()) + "h-gamesessionlog.txt";
            lastHour = actualHour;
            
            logFile = new RandomAccessFile(nameOfFile, "rw");
            
            // Just in case
            logFile.seek(logFile.length());
            nbOfLines = 0;
            nbForThisHour = 0;
            }
            catch (Exception e)
            {
                System.out.println("New log creation problem");
                e.printStackTrace();
                return -1;
            }
        }
        if (nbOfLines > 10000)
        {
            nbOfLines = 0;
            nbForThisHour++;
            try{
                // We close the old log file and we create a new one.
                logFile.close();
                if (optName == null)
                    nameOfFile = "logs/"+optName+"-"+myDateFormat.format(new Date()) + "h-gamesessionlog-"+nbForThisHour+".txt";
                else
                    nameOfFile = "logs/"+optName+"-"+myDateFormat.format(new Date()) + "h-gamesessionlog-"+nbForThisHour+".txt";
                
                logFile = new RandomAccessFile(nameOfFile, "rw");
                
                // Just in case
                logFile.seek(logFile.length());
                nbOfLines = 0;
                
                }
                catch (Exception e)
                {
                    System.out.println("New log creation problem");
                    e.printStackTrace();
                    return -1;
                }
        }
        // Anyway (if no exception before), add the line to the log
        try{
            String dateToAppend = logEntryFormat.format(new Date());
    
            //System.out.println("TO file:"+logFile+" : "+line+"\n");
            if (line != null) {
                logFile.writeBytes(dateToAppend+":  "+line+"\n");
                if (systemEcho) {
                	System.out.println(dateToAppend + ":  " + line);
                }
            } else
                logFile.writeBytes(dateToAppend+":  Void\n");
        }
        catch (Exception e)
        {
            System.out.println("Log append problem");
            e.printStackTrace();
            return -1;
        }
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
        String actualHour = hoursFormat.format(new Date());
        //System.out.println(">"+actualHour+"\n");
        nbOfLines++;
        if (!actualHour.equals(lastHour))
        {
            try{
            // We close the old log file and we create a new one.
            logFile.close();
            if (optName == null)
                nameOfFile = "logs/"+myDateFormat.format(new Date()) + "h-gamesessionlog.txt";
            else
                nameOfFile = "logs/"+optName+"-"+myDateFormat.format(new Date()) + "h-gamesessionlog.txt";
            lastHour = actualHour;
            
            logFile = new RandomAccessFile(nameOfFile, "rw");
            
            // Just in case
            logFile.seek(logFile.length());
            nbOfLines = 0;
            nbForThisHour = 0;
            }
            catch (Exception e)
            {
                System.out.println("New log creation problem");
                e.printStackTrace();
                return -1;
            }
        }
        if (nbOfLines > 10000)
        {
            nbOfLines = 0;
            nbForThisHour++;
            try{
                // We close the old log file and we create a new one.
                logFile.close();
                if (optName == null)
                    nameOfFile = "logs/"+optName+"-"+myDateFormat.format(new Date()) + "h-gamesessionlog-"+nbForThisHour+".txt";
                else
                    nameOfFile = "logs/"+optName+"-"+myDateFormat.format(new Date()) + "h-gamesessionlog-"+nbForThisHour+".txt";
                
                logFile = new RandomAccessFile(nameOfFile, "rw");
                
                // Just in case
                logFile.seek(logFile.length());
                nbOfLines = 0;
                
                }
                catch (Exception e)
                {
                    System.out.println("New log creation problem");
                    e.printStackTrace();
                    return -1;
                }
        }
        // Anyway (if no exception before), add the line to the log
        try{
            String dateToAppend = logEntryFormat.format(new Date());
    
            //System.out.println("TO file:"+logFile+" : "+line+"\n");
            if (eToPr != null) {
                logFile.writeBytes(dateToAppend+": Exception!!! ");
                logFile.writeBytes(dateToAppend+":  "+eToPr.getMessage()+"\n");
                StackTraceElement[] theStack=eToPr.getStackTrace();
                for (int iStack=0;iStack<theStack.length;iStack++)
                {
                    logFile.writeBytes(dateToAppend+":  "+theStack[iStack]+"\n");
                    if (systemEcho) {
                        System.out.println(dateToAppend+":  "+theStack[iStack]+"\n");
                    }
                }
                
            } else
                logFile.writeBytes(dateToAppend+":  Void\n");
        }
        catch (Exception e)
        {
            System.out.println("Log append problem");
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
    
    public int closeLog()
    {
        try
        {
            logFile.close();
        }
        catch (Exception e)
        {
            System.out.println("Log close problem");
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
    /**
     *  
     */
    public LogManager()
    {
        myDateFormat = new SimpleDateFormat("dd-MM-yy-H");
        logEntryFormat = new SimpleDateFormat("dd-MM-yy-H:m:s");
        hoursFormat = new SimpleDateFormat("H");
        nbOfLines = 0;
        nbForThisHour = 0;
        systemEcho = false;
    }
    
    public void finalize()
    {
        try
        {
            logFile.close();
        }
        catch (Exception e)
        {
            System.out.println("Log close problem");
            e.printStackTrace();
        }
    }

	public void setSystemEcho(boolean systemEcho) {
		this.systemEcho = systemEcho;
	}
}