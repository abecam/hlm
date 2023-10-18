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

package utils;

import java.io.*;
import java.util.ArrayList;

/**
 * This class manage the text file used for knowing the
 * different distant machines. -- This document is part of the meta-data --
 * @author Alain Becam 
 * TODO ...
 */
public class ManageMachineList
{
    java.lang.String nameOfDocument;
    
    boolean debugMode = false;

    public static void main(String[] args)
    {
        boolean notEnd = true;
        // Emergency addition of the Super Module, to manage all the others.
        ManageMachineList newManager = new ManageMachineList();
        StringBuffer doc = newManager.readDocument();
        while (notEnd)
        {
            //System.out.println("The document:\n" + doc);
            String section = newManager.extractSection(doc);
            core.MachineDesc oneDesc = newManager.extractMachineDesc(section);
            System.out.println("One machine:" + oneDesc.getHostName() + "--" + oneDesc.getType() + "--" + oneDesc.getAlias() + "--" + oneDesc.getLanguage());
            ArrayList oneGroupe = oneDesc.getGroupe();
            for (int i = 0; i < oneGroupe.size(); i++)
            {
                if (oneGroupe.get(i) != null)
                {
                    System.out.println("One group:" + oneGroupe.get(i) + "--");
                }
            }
            if (doc.indexOf("Machine:") == -1)
            {
                notEnd = false;
            }
        }
        
    }

    public ManageMachineList()
    {
        nameOfDocument = new String("machinesList.txt");
    }

    public int setFileName(String nameWanted)
    {
        // We just set the file name here (note that there exists a default file
        // name, as there is theorically only one file
        nameOfDocument = nameWanted;
        return 0;
    }


    public int addMachine(StringBuffer machineDesc)
    {
        try
        {

            File documentFile = new File(nameOfDocument);
            File backupFile = new File("bak-" + nameOfDocument);

            if (documentFile.exists())
            {
                // We create the backup file (rename)
                if (backupFile.exists())
                {
                    backupFile.delete();
                }
                this.copyFile(nameOfDocument, "bak-" + nameOfDocument);
            }
            {

                // Time to add the machine description. Notice that we do NOT
                // verify something.
                // We also add it at the end, as it seems better to not organize
                // them...
                RandomAccessFile rndFile = new RandomAccessFile(nameOfDocument, "rw");

                rndFile.seek(rndFile.length());
                rndFile.writeBytes(machineDesc.toString());

                rndFile.close();
            }
            return 0;
        } catch (Exception e)
        {
            e.printStackTrace();
            return 1;
        }
    }

    public int removeMachine(String className)
    {
        // Remove a module from the list
        try
        {
            File documentFile = new File(nameOfDocument);
            File backupFile = new File("bak-" + nameOfDocument);

            if (documentFile.exists())
            {
                // We create the backup file (rename)
                if (backupFile.exists())
                {
                    backupFile.delete();
                }
                this.copyFile(nameOfDocument, "bak-" + nameOfDocument);

                RandomAccessFile fromFileRnd = new RandomAccessFile(nameOfDocument, "r");
                File ToFile = new File("tmp-" + nameOfDocument);

                if (ToFile.exists())
                {
                    ToFile.delete();
                }
                
                    String oneLine;
                    int progress = 0; // Where are we in the file, 0: before the module desc, 1:in, 2: after
                    //long posInFile;
                    RandomAccessFile ToFileRnd = new RandomAccessFile("tmp-"+nameOfDocument, "rw");
                    oneLine = fromFileRnd.readLine();
                    while (oneLine != null)
                    {
                        //posInFile = ToFileRnd.getFilePointer();
                        if (oneLine.startsWith("Machine:") && (progress == 0))
                        {
                                // Start of the machine description
                                progress = 1;
                        }
                        if (oneLine.startsWith("end.") && (progress == 1))
                        {
                            oneLine = fromFileRnd.readLine();
                            progress = 2;
                        }
                        if ( (progress == 0) || (progress == 2))
                        {
                            ToFileRnd.writeBytes(oneLine+"\n");
                        }
                        oneLine = fromFileRnd.readLine();
                    }

                    ToFileRnd.close();
                    fromFileRnd.close();
                    {
                        File modifiedFile = new File("tmp-" + nameOfDocument);
                        File originalFile = new File(nameOfDocument);

                        if (modifiedFile.exists())
                        {
                            // We create the backup file (rename)
                            if (originalFile.exists())
                            {
                                originalFile.delete();
                            }
                            this.copyFile("tmp-" + nameOfDocument, nameOfDocument);
                        }
                    }
                    if (progress == 2)    
                    {
                        return 0;
                    }
                    else
                    {
                        return 4;
                    }

            } else
            {
                return 3;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return 2;
        }
    }    
        
    
    public StringBuffer readDocument()
    {
        try
        {
            //RandomAccessFile fromFileRnd = new RandomAccessFile(nameOfDocument, "r");
        	BufferedReader fromFileRnd=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+nameOfDocument)));
            StringBuffer document = new StringBuffer();

            String FileLine;
            int nbRead;
            //long posInFile;

            FileLine = fromFileRnd.readLine();
            while (FileLine != null)
            {
                //posInFile = ToFileRnd.getFilePointer();
                document.append(FileLine + "\n");
                FileLine = fromFileRnd.readLine();
            }

            fromFileRnd.close();
            return document;

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String extractSection(StringBuffer document)
    {
        // Extract one module description, return it and remove it from the document.
        String section;
        int startSect;
        int endSect;

        startSect = document.indexOf("Machine:");
        endSect = document.indexOf("end.") + 4; // 4 is the length of "end.".

        section = document.substring(startSect, endSect);

        document.delete(startSect, endSect);

        return section;
    }

    public core.MachineDesc extractMachineDesc(String section)
    {
        // This method just return the name of the language used by the module. Useful if WE are supposed to run this module. Or it's just information
        int start;
        int end;
        String HostName;
        String type;
        String alias;
        ArrayList<String> group = new ArrayList<String>();
        String language;
        core.MachineDesc oneDesc = new core.MachineDesc();

        start = section.indexOf("hostname: ") + ("hostname: ").length();
        end = section.indexOf("\n",start); // Relative position of the end of line for hostname (it's VERY crappy)
        if (debugMode)
        {
            System.out.println(start+"--"+end);
        }
        HostName = section.substring(start, end);
        
        start = section.indexOf("type: ") + ("type: ").length();
        end = section.indexOf("\n",start);
        if (debugMode)
        {
            System.out.println(start+"--"+end);
        }
        type = section.substring(start, end);
        
        start = section.indexOf("alias: ") + ("alias: ").length();
        end = section.indexOf("\n",start);
        if (debugMode)
        {
            System.out.println(start+"--"+end);
        }
        alias = section.substring(start, end);
        
        start = section.indexOf("group: ") + ("group: ").length();
        end = section.indexOf("\n",start);
        if (debugMode)
        {
            System.out.println(start+"--"+end);
        }
        {
            // There can be several groups... separated with a comma followed by a space
            String subsection = section.substring(start, end);
            
            start = 0;
            end = subsection.indexOf(",");
            while (end > -1)
            {
                String oneGroupe = subsection.substring(start, end);
                group.add(oneGroupe);
                start = end+1;
                end = subsection.indexOf(", ", start);
            }
            // last one
            end = subsection.indexOf("\0",start);
            String oneGroupe = subsection.substring(start, subsection.length());
            group.add(oneGroupe);
        }
        
        start = section.indexOf("language: ") + ("language: ").length();
        end = section.indexOf("\n",start);

        language = section.substring(start, end);

        oneDesc.setAlias(alias);
        oneDesc.setGroupe(group);
        oneDesc.setHostName(HostName);
        oneDesc.setLanguage(language);
        oneDesc.setType(type);
        
        return oneDesc;
    }

    public int copyFile(String fromName, String ToName)
    {
        try
        {
            RandomAccessFile fromFileRnd = new RandomAccessFile(fromName, "r");
            File ToFile = new File(ToName);
    
            if (ToFile.exists())
            {
                fromFileRnd.close();
                return 1;
            } else
            {
                byte[] FileBuffer = new byte[500];
                int nbRead;
                //long posInFile;
                RandomAccessFile ToFileRnd = new RandomAccessFile(ToName, "rw");
                nbRead = fromFileRnd.read(FileBuffer);
                while (nbRead != -1)
                {
                    //posInFile = ToFileRnd.getFilePointer();
                    ToFileRnd.write(FileBuffer, 0, nbRead);
                    nbRead = fromFileRnd.read(FileBuffer);
                }
    
                ToFileRnd.close();
                fromFileRnd.close();
                return 0;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return 2;
        }
    }
}