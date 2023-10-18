/*
 * Created on 2005-nov-23
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

import java.io.File;

/**
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MFile implements IMFile, IDemo
{
    File ourFile;
    /* (non-Javadoc)
     * @see java.io.File#getAbsolutePath()
     */
    /* (non-Javadoc)
     * @see modulesrepository.IMFile#getAbsolutePath()
     */
    public String getAbsolutePath()
    {
        // TODO Auto-generated method stub
        return ourFile.getAbsolutePath();
    }
    
    public String eraseFile(String nameOfFile)
    {
    	File newFile=new File(ourFile,nameOfFile);
    	if (newFile.exists())
    	{
    		newFile.delete();
    		return new String(nameOfFile+" deleted");
    	}
    	return new String("No file with this name");
    }
    
    public String renameFile(String nameOfFile, String newName)
    {
    	File newFile=new File(ourFile,nameOfFile);
    	if (newFile.exists())
    	{
    		newFile.renameTo(newFile=new File(ourFile,newName));
    		return new String(nameOfFile+" renamed in "+newName);
    	}
    	return new String("No file with this name");
    }

    /* (non-Javadoc)
     * @see java.io.File#getName()
     */
    /* (non-Javadoc)
     * @see modulesrepository.IMFile#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return ourFile.getName();
    }

    /* (non-Javadoc)
     * @see java.io.File#isDirectory()
     */
    /* (non-Javadoc)
     * @see modulesrepository.IMFile#isDirectory()
     */
    public int isDirectory()
    {
        // TODO Auto-generated method stub
        if (ourFile.isDirectory())
            return 1;
        else
            return 0;
    }

    /* (non-Javadoc)
     * @see java.io.File#listFiles()
     */
    /* (non-Javadoc)
     * @see modulesrepository.IMFile#listFiles()
     */
    /* (non-Javadoc)
     * @see modulesrepository.IDemo#listFiles()
     */
    public String listFiles()
    {
        // TODO Auto-generated method stub
        StringBuffer listF=new StringBuffer();
        File[] listFil=ourFile.listFiles();
        for (int i=0;i<listFil.length;i++)
        {
            listF.append(listFil[i].getName()+"\n");
        }
        return listF.toString();
    }

    /**
     * @param pathname
     */
    public MFile()
    {
        ourFile = new File(".");
        // TODO Auto-generated constructor stub
    }

}
