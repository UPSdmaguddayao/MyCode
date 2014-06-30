import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 *Creates four different threads that cooperate in reading data, processing it, and finally
 *printing it to the screen.
 * 
 * Input should be a .txt file, but any file that can be read by the File class should work
 * 
 * 
 * Four different threads will each perform one
 *of the following tasks:
 *
 *1. Read a line of text from the file.
 *2. Count the number of characters in a line, and append that to the string inside of parentheses along with a
 *  space.
 *3. Determine which line it is, and prepend that to the line with colon and a space.
 *4. Write the line to the screen.
 * @author DJ Maguddayao 
 * @version 02.21.14
 */
class Muncher
{
    public static void main (String[] args){
        try
        {
            BufferedReader in = new BufferedReader
               (new InputStreamReader(System.in));  //makes it so that anything typed to the system will be read
            String commandLine;
            int numberOfLines;
            
            while (true){ //keeps running until told to stop
                
                System.out.print("Input text file to be read: ");
                commandLine = in.readLine();//reads any input to the screen
                
                if (commandLine.equals("exit")) //closes the program if exit is typed
                {
                    System.out.println("...Terminating the Virtual Machine");
                    System.out.println("...Done");
                    System.out.println("Please Close manually with Options > Close");
                    System.exit(0); 
                }
                else 
                {
                    File openFile = new File(commandLine);
                    
                    if (openFile.exists()) //if this file actually exists, attempt to parse.  Else it will tell the user it's an incorrect file
                    {
                    Scanner file = new Scanner(openFile); //sets the scanner to the file so it can be parsed
                    Scanner file2 = new Scanner(openFile);
                    
                    //File is read.  Now time to create threads to actually take these inputs and the databuffer
                    DataBuffer db = new DataBuffer();
                    
                    numberOfLines = 0;
                    while (file2.hasNext()) //file 2 is only used to count the maximum number of lines needed for the threads to complete
                    {
                        numberOfLines++;
                        file2.nextLine();
                    }
                    
                    //create the threads
                    StringReadThread read = new StringReadThread(file, db); //inputs file to be read
                    StringCounterThread counter = new StringCounterThread(numberOfLines, db); //adds the number of characters there is to the end of a line
                    StringLocationThread location = new StringLocationThread(numberOfLines,db); //adds the line number to the beginning of a line
                    StringDisplayThread display = new StringDisplayThread(numberOfLines,db); //displays to the screen
                    
                    //start the threads
                    read.start();
                    counter.start();
                    location.start();
                    display.start();
                    
                    //join the threads when finished
                    read.join();
                    counter.join();
                    location.join();
                    display.join();
                    }
                    else
                    {
                        System.out.println ("Error, incorrect file name");
                    }
                }
            }
        }
        catch(Exception e)
        {
        }
    }

   
    /**
     * Class to hold the data.  Only holds up to 8 strings
     */
    static class DataBuffer {
        String[] data;
        int[] position; //holds the position that each of the threads are at 
        public DataBuffer() {
            data = new String[8];
            position = new int[4];
            
            //all of the threads start at location 0
            position[0] = 0;
            position[1] = 0;
            position[2] = 0;
            position[3] = 0;
        }

        /**
         * Grabs the string at a given position.  Good for everything but the first thread (the one that sets lines)
         */
        public String getString(int which) {
            return data[which];
        }

        /**
         * Sets the string at a given line.  Good for everything but the last thread (display)
         */
        public void setString(int which, String newString) {
           data[which] = newString;
        }
        
        /**
         * Stores the position of a thread
         */
        public void setPosition (int thread, int positionPlace) 
        {
            position[thread] = positionPlace;
        }
        
        public int getPosition(int thread)
        {
            return position[thread];
        }
    }
    
    
    //Prefered order of threads:  StringRead > StringCounter > StringLocation > StringDisplay.  Use "synchronize" to
    //enforce this
    
    /**
     * The thread designed to read the file and append it to the DataBuffer
     */
    static class StringReadThread extends Thread 
    {
        private DataBuffer db;
        private Scanner scan;
        private int currentLine; //tracks the location of the thread.  Only useful for the other threads though
        
        public StringReadThread (Scanner scanned, DataBuffer dataB)
        {
            db = dataB; //databuffer that is inputted
            scan = scanned; //grabs the scanner of the file as well
            currentLine = 0;
        }
        
        /**
         * when "StringReadThread.start() is called, this is ran
         */
        public void run()
        {
              try
              {
                while (scan.hasNextLine())//as long as there's a line
                {
                    synchronized(db){
                        //makes it so that it isn't too far ahead of the other threads
                        //doesn't need to keep track of the second thread because it the other two threads enforce it's position
                    while (db.getPosition(0) > (db.getPosition(3)+7)|| db.getPosition(0) > (db.getPosition(1) + 7))
                    {
                       try
                        {
                            //waits for the other threads if this goes to far ahead
                            db.wait();
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    //hardcoded 8 since that's the array size.  Set's the string to what the line in the scanner is
                    db.setString(currentLine%8, scan.nextLine()); 
                    
                    //increments the position and sets it to the databuffer
                    currentLine++;
                    db.setPosition(0, currentLine);
                    
                    //notify the other threads that this is done
                    db.notifyAll();
                   }
                }
              }
              catch (NullPointerException e)
                {
            
                }
        }
    }
    
   /**
    * The thread that counts the number of characters in a line
    */
       static class StringCounterThread extends Thread
       {
        private DataBuffer db;
        private int currentLine;
        
        //initialises the length variable here
        private int length;
        
        //initialises the string here too
        private String newString;
        
        //holds the maximum number of lines so that it knows when to stop looping
        private int numberOfLines;
        public StringCounterThread (int noOfLines, DataBuffer dataB) //save the runtime and the item to execute (as a string)
        {
            db = dataB;
            currentLine = 0;
            length = 0;
            newString = "";
            
            numberOfLines = noOfLines;
        }
        public void run()
        {
              try
              {
                  synchronized (db){
                  while(currentLine < numberOfLines)
                  {
                     //if this gets ahead of the first thread, halts
                     while(db.getPosition(1) > (db.getPosition(0)-1))
                      {
                          try
                          {
                              db.wait();
                            }
                            catch(InterruptedException e)
                            {
                            }
                        }
                      //grabs the string at a currentPosition the thread is at
                      length =db.getString(currentLine%8).length();
                      
                      //appends the length of a string at the end and sets it as the new string
                      newString = db.getString(currentLine%8) + "        (" + length + ")";
                      db.setString(currentLine%8, newString);
                      
                      //same as seen in the first thread
                      currentLine++;
                      db.setPosition(1, currentLine);
                      db.notifyAll();
                    }
                }
              }
              catch (Exception e)
                {
            
                }
        }
    }
    
    /**
     * Tells what the line the string was on in the file
     */
       static class StringLocationThread extends Thread //Counts which line it's currently on.
     {
        private DataBuffer db;
        private int currentLine;
        private String newString;
        private int numberOfLines;
        public StringLocationThread (int noOfLines, DataBuffer dataB) //save the runtime and the item to execute (as a string)
        {
            db = dataB;
            currentLine = 0;
            newString = "";
            numberOfLines = noOfLines;
        }
        public void run()
        {
              try
              {
                synchronized (db){
                  while(currentLine < numberOfLines)
                  {
                      //Makes sure it doesn't get ahead of the thread that counts characters so there isn't an incorrect count
                     while(db.getPosition(2) > (db.getPosition(1)-1))
                     {
                          try
                          {
                              db.wait();
                            }
                            catch(Exception e)
                            {
                            }
                       }
                    
                    //Similar to the thread that counts character, appends the line number to the beginning of each line
                    //+1 is there because the array starts at position 0
                    newString = (currentLine + 1) + ": " + db.getString(currentLine%8);
                    db.setString(currentLine%8, newString);
                    currentLine++;
                    
                    db.setPosition(2, currentLine);
                    db.notifyAll();
                }
             }
              }
              catch (Exception e)
                {
            
                }
        }
    }
    
    /**
     * Thread that displays the final product
     */
       static class StringDisplayThread extends Thread 
    {
        private DataBuffer db;
        private int currentLine;
        private int numberOfLines;
        public StringDisplayThread (int noOfLines, DataBuffer dataB)
        {
            db = dataB;
            currentLine = 0;
            numberOfLines = noOfLines; 
        }
        public void run()
        {
              try
              {
                      while(currentLine < numberOfLines)
                      {
                          synchronized (db)
                          {
                            //Makes sure it doesn't read what it isn't supposed to read (the final product)
                            while(db.getPosition(3) > (db.getPosition(2)-1))
                            {
                                try
                                {
                                    db.wait();
                                }
                                catch(InterruptedException e)
                                {
                                }
                            }
                            //Grabs the string at it's position.
                            System.out.println(db.getString(currentLine%8));
                            currentLine++;
                       
                            db.setPosition(3, currentLine);
                            db.notifyAll();
                        }
                      }
              }
              catch (NullPointerException e)
                {
            
                }
        }
    }
}