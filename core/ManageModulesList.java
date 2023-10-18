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

package core;

import java.io.*;

/**
 * This class manage the text file used for knowing the
 * module disponibility. -- This document is part of the meta-data --
 * @author Alain Becam 
 * TODO ...
 */
public class ManageModulesList
{
    java.lang.String nameOfDocument;

    java.lang.String nameOfDocumentDistant;

    ExtractInterface ourExtractor;

    public static void main(String[] args)
    {
        // Emergency addition of the Super Module, to manage all the others.
        ManageModulesList myManager = new ManageModulesList();

        //myManager.addModule("SModule", "SModuleInterface");
        StringBuffer distantDoc = myManager.readDistantDocument();
        boolean endOfDoc = false;
        /*while (!endOfDoc)
         {
         String section = myManager.extractSection(distantDoc);
         System.out.println("Host:"+myManager.extractDistantHostName(section)+"--");
         System.out.println("Name:"+myManager.extractDistantModuleName(section)+"--");
         boolean endOfMethod = false;
         StringBuffer bufferedSect = new StringBuffer(section);
         while ( bufferedSect.indexOf("Method:") > -1)
         {
         System.out.println("Method:"+myManager.extractMethod(bufferedSect));
         }
         if (distantDoc.indexOf("Module") == -1)
         {
         endOfDoc = true;
         }
         }*/
    }

    public ManageModulesList()
    {
        nameOfDocument = new String("modulesList.txt");
        nameOfDocumentDistant = new String("distantModulesList.txt");
        ourExtractor = new ExtractInterface();
    }

    public int setFileName(String nameWanted, String distantNameWanted)
    {
        // We just set the file name here (note that there exists a default file
        // name, as there is theorically only one file
        nameOfDocument = nameWanted;
        nameOfDocumentDistant = distantNameWanted;
        return 0;
    }

    public int addModule(String className, String interfaceName)
    {
        try
        {
            StringBuffer moduleDescription;

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
                int extrResult;
                // Time to add the class description. Notice that we do NOT
                // verify something.
                // We also add it at the end, as it seems better to not organize
                // them...
                RandomAccessFile rndFile = new RandomAccessFile(nameOfDocument, "rw");

                extrResult = ourExtractor.recoverClass(className, interfaceName);
                if (extrResult == 0)
                {
                    extrResult = ourExtractor.extractComponents();
                    if (extrResult == 0)
                    {
                        moduleDescription = ourExtractor.getTranscript();

                        rndFile.seek(rndFile.length());
                        rndFile.writeBytes(moduleDescription.toString());

                        rndFile.close();
                    }
                }
            }
            return 0;
        } catch (Exception e)
        {
            e.printStackTrace();
            return 1;
        }
    }

    public int addDistantModule(StringBuffer ModuleDesc)
    {
        try
        {
            StringBuffer moduleDescription;

            File documentFile = new File(nameOfDocumentDistant);
            File backupFile = new File("bak-" + nameOfDocumentDistant);

            if (documentFile.exists())
            {
                // We create the backup file (rename)
                if (backupFile.exists())
                {
                    backupFile.delete();
                }
                this.copyFile(nameOfDocumentDistant, "bak-" + nameOfDocumentDistant);
            }
            {

                // Time to add the class description. Notice that we do NOT
                // verify something.
                // We also add it at the end, as it seems better to not organize
                // them...
                RandomAccessFile rndFile = new RandomAccessFile(nameOfDocumentDistant, "rw");

                rndFile.seek(rndFile.length());
                rndFile.writeBytes(ModuleDesc.toString());

                rndFile.close();
            }
            return 0;
        } catch (Exception e)
        {
            e.printStackTrace();
            return 1;
        }
    }

    public int removeModule(String className)
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
                RandomAccessFile ToFileRnd = new RandomAccessFile("tmp-" + nameOfDocument, "rw");
                oneLine = fromFileRnd.readLine();
                while (oneLine != null)
                {
                    //posInFile = ToFileRnd.getFilePointer();
                    if (oneLine.startsWith("Module:") && (progress == 0))
                    {
                        // Crappy test...
                        if ((oneLine.indexOf(className) > -1) && oneLine.substring(7 + className.length(), 8 + className.length()).equals(":"))
                        {
                            // Start of the module description
                            progress = 1;
                        }
                    }
                    if (oneLine.startsWith("No more methods.") && (progress == 1))
                    {
                        oneLine = fromFileRnd.readLine();
                        progress = 2;
                    }
                    if ((progress == 0) || (progress == 2))
                    {
                        ToFileRnd.writeBytes(oneLine + "\n");
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
                } else
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

    public int removeDistantModule(String className, String host)
    {
        // Remove a distant module from the list
        // Copied from the local module remove method, must add the host info. (we can have the same name for different modules on differents machines, even if it is not likely to happen).
        try
        {
            File documentFile = new File(nameOfDocumentDistant);
            File backupFile = new File("bak-" + nameOfDocumentDistant);

            if (documentFile.exists())
            {
                // We create the backup file (rename)
                if (backupFile.exists())
                {
                    backupFile.delete();
                }
                this.copyFile(nameOfDocumentDistant, "bak-" + nameOfDocumentDistant);

                RandomAccessFile fromFileRnd = new RandomAccessFile(nameOfDocumentDistant, "r");
                File ToFile = new File("tmp-" + nameOfDocumentDistant);

                if (ToFile.exists())
                {
                    ToFile.delete();
                }

                String oneLine;
                int progress = 0; // Where are we in the file, 0: before the module desc, 1:in, 2: after
                //long posInFile;
                RandomAccessFile ToFileRnd = new RandomAccessFile("tmp-" + nameOfDocumentDistant, "rw");
                oneLine = fromFileRnd.readLine();
                while (oneLine != null)
                {
                    //posInFile = ToFileRnd.getFilePointer();
                    if (oneLine.startsWith("Module:") && (progress == 0))
                    {
                        // Crappy test...
                        if ((oneLine.indexOf(className) > -1) && oneLine.substring(7 + className.length(), 8 + className.length()).equals(":"))
                        {
                            // Start of the module description
                            progress = 1;
                        }
                    }
                    if (oneLine.startsWith("No more methods.") && (progress == 1))
                    {
                        oneLine = fromFileRnd.readLine();
                        progress = 2;
                    }
                    if ((progress == 0) || (progress == 2))
                    {
                        ToFileRnd.writeBytes(oneLine + "\n");
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
                } else
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

    public StringBuffer readDistantDocument()
    {
        try
        {
//            RandomAccessFile fromFileRnd = new RandomAccessFile(nameOfDocumentDistant, "r");
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

        startSect = document.indexOf("Module:");
        endSect = document.indexOf("No more methods.") + 16; // 16 is the length of "No more methods.".

        section = document.substring(startSect, endSect);

        document.delete(startSect, endSect);

        return section;
    }

    public String checkLanguage(String section)
    {
        // This method just return the name of the language used by the module. Useful if WE are supposed to run this module. Or it's just information
        int startLanguage;
        int endLanguage;
        String language;

        startLanguage = section.indexOf(": ") + 1;
        endLanguage = section.indexOf("\n");

        language = section.substring(startLanguage, endLanguage);

        return language;
    }

    public String extractClassName(String section)
    {
        // Extract the name of the class to instanciate
        int startName;
        int endName;
        String name;

        startName = section.indexOf(":") + 1;
        endName = section.indexOf(": ");

        name = section.substring(startName, endName);

        return name;
    }

    public String extractMethod(StringBuffer subsection)
    {
        int startMethod;
        int endMethod;
        String method;

        startMethod = subsection.indexOf("Method:");
        endMethod = subsection.indexOf("endofmethod.") + 12; // 12 is the length of "endofmethod.".

        method = subsection.substring(startMethod, endMethod);

        subsection.delete(startMethod, endMethod);

        return method;
    }

    public String extractMethodName(String method)
    {
        // Extract the name of the class to instanciate
        int startName;
        int endName;
        String name;

        startName = method.indexOf(":") + 1;
        endName = method.indexOf("::");

        name = method.substring(startName, endName);

        return name;
    }

    public String extractDistantHostName(String section)
    {
        // Extract the name of the class to instanciate
        int startName;
        int endName;
        String name;

        startName = section.indexOf(":") + 1;
        endName = section.indexOf(":", startName);

        name = section.substring(startName, endName);

        return name;
    }

    public String extractDistantModuleName(String section)
    {
        // Extract the name of the class to instanciate
        int startDesc;
        int startName;
        int endName;
        String name;

        startDesc = section.indexOf(":") + 1;
        startName = section.indexOf(":", startDesc);
        endName = section.indexOf(": ");

        name = section.substring(startName, endName);

        return name;
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