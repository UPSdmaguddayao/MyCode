import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.*;
/**
 * Utilizes the bankers algorithm to make the processes move along and avoid deadlocking
 * 
 * @author Dennis Maguddayao
 * @version 4.22.14
 */
class banker
{
    private static String TAB = "\\t";
    private static int[] resources; //stores the resources
    private static fakeProcesses[] processList; //stores the 'processes' that are running
    private static boolean[] finished; //keeps track if a process is finished or not
    
    private static LinkedList<String> commandQueue; //stores which process command is in the queue at the moment
    private static LinkedList<Integer> processesStalled; //if a process is stalled, add to Queue.  That way, we can figure out if we need to stall the command
    
    private static LinkedList<String> tempCommandQueue; //this is needed for when the commandQueue begins running
    private static LinkedList<Integer> tempProcessesStalled; //same as the other tempQueue
    public static void main(String[] args)
    {
        File fileName = null;
        resources = null;
        int processes = 0;
        
        //initializes the queues
        commandQueue = new LinkedList<String>(); 
        processesStalled = new LinkedList<Integer>();
        tempCommandQueue = new LinkedList<String>();
        tempProcessesStalled = new LinkedList<Integer>();
        
        
        if (args.length == 0) //no input was added
        {
            System.out.println("Error, no file is read");
            return;
            //System.exit(0); 
        }
        else 
        {
            System.out.println(args[0]);
            fileName = new File(args[0]);
        }
        
        try
        {
            Scanner parser = new Scanner(fileName); //sets up a scanner to read the document
            Scanner counter = new Scanner(fileName); //used to make sure we don't have wasted space
            
            String currentLine = null;
            
            while(counter.hasNext()) //utilizes this in order to get a count of how many processes there are.  Just for accurate amount for the array
            {
                currentLine = counter.nextLine();
                if (currentLine.trim().length() == 0) //an empty line is found. Any white space is removed for checking. Used as a break
                {
                    processes--;  //removes the first line which isn't a process
                   // System.out.println("Current number of processes: " + processes);
                    break;
                }
                processes++; //increments counters
            }
            currentLine = null;
            String[] readLine;
            
            //grab the first line as number of available resources.
            if(parser.hasNextLine())
            {
                readLine = parser.nextLine().split(TAB); //splits the line by the tabs.  Everything should be spaced by tabs only.  Nothing else
                resources = new int[readLine.length]; //keeps only the ammount of resources needed
                for(int i = 0; i < readLine.length; i++) 
                {
                    resources[i] = Integer.parseInt(readLine[i]);
                }
                
            }
            else
            {
                return; //if we read an empty file, just stop everything
            }
            
            
            //maximum resources type is resources.length
            int x = 0;
            
            processList = new fakeProcesses[processes]; //creates the array of processes
            finished = new boolean[processes]; //keeps track if a process is terminated or not
            
            //keep track of each of them when reading through the last part
            fakeProcesses newProcess = null;
            
            //try to store these values in different arrays.
            //0 = Resource A, 1= Resource B, 2 = Resource C.  Etc.  
            while (parser.hasNextLine())
            {
                currentLine = parser.nextLine();
                if (currentLine.trim().length() == 0) //an empty line is found. Any white space is removed for checking. Used as a break
                {
                    break;
                }
                readLine = currentLine.split(TAB);
                //create a new fake process
                newProcess = new fakeProcesses(resources.length,x); //starts with x is 0 and slowly increments for ID/place in array
                for(int i = 0; i < readLine.length; i++)
                {
                    //System.out.println("Resources needed for process" +x+ " " +readLine[i]);
                    newProcess.inputNeededResource(i,Integer.parseInt(readLine[i]));
                }
                processList[x] = newProcess; //inputs the process into the processlist
                finished[x] = false; //initializes the process as not being finished
                x++;
            }
            
            //after that, try to parse the lines afterwords
            //first part should be what we're doing with the process.  + requests, - releases, and ! releases ALL from a process
            //second part should be which process is doing this
            //third is which resource
            
            String methodType = null;
            String processNumber = null;
            String resourceNumber = null;
            
            while(parser.hasNextLine())

            {
                currentLine = parser.nextLine(); //continues iterating
                
                x=currentLine.length(); // check how many things are in this arguement.  Typically three, occasionally four
                
                readLine = currentLine.split(""); //note, the line starts at readLine[1]; when you do this
                
                methodType = readLine[1];//first item should always be the method type of +, -, or !
                
                processNumber = "";
                
                //read the processNumber
                    if(readLine[1].equals("!")) //if it terminates
                    {
                        for(int i = 2; i<(x+1); i++)
                        {
                            processNumber = processNumber + readLine[i]; //doesn't require knowing the resourceNumber
                        }
                    }
                    else if(x == 3)
                    {
                        processNumber = readLine[2];
                        resourceNumber = readLine[3];
                    }
                    else
                    {
                        for(int i = 2; i<x; i++) 
                        {
                            processNumber = processNumber + readLine[i];
                        }
                        resourceNumber = readLine[x];
                    }
               
                    //run a check if the process is in the processStalled list
                    if(processesStalled.contains(Integer.parseInt(processNumber)))
                    {
                        commandQueue.add(currentLine); //adds the line to a queue to parse
                    }
                    else //attempts to do the line.  If it can't accomplish it, adds it to the queue
                    {
                        action(methodType,Integer.parseInt(processNumber),resourceNumber,currentLine,commandQueue,processesStalled);
                    }
            }
            
            //create another method to decide if it's "safe" transaction
        }
        catch(FileNotFoundException fnfe)
        {
            System.out.println("Error, file does not exist");
            return;
        }
    }
    
    /**
     * Helper method for finding if something a string is an int.  Only used for if there are more than 9 processes.  Just in case
     */
    private static boolean isInt(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch(NumberFormatException nf)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Helper method for executing different actions.  Could've not been used, but avoids the code above from looking too clunky due to three different methods
     * that can occur
     */
    private static void action(String type, int processNumber, String resource, String enterQueue, LinkedList<String> cQueue, LinkedList<Integer> pQueue) 
    {
        int resourceNumber = parseResource(resource);
        if(type.equals("+"))//should be the only thing that needs a safety check. 
        //uses resource helper
        {
            System.out.print("Process " +processNumber + " has requested a resource " +resource);
            
            //run a check for if the resource is available
            if(resources[resourceNumber] == 0) //there is no available resources
            {
                if(!pQueue.contains(processNumber) ) //if the process number isn't in the Queue, add it
                {
                    pQueue.add(processNumber);
                }
                cQueue.add(enterQueue);//adds the command to the queue
                
                System.out.println("(DENIED)");
                return;
                //stop the process from doing anything until something else terminates
            }
            
            
            processList[processNumber].increaseResouce(resourceNumber);
            
            //run a check for if this was a safe transaction
            if (!isSafe())
            {
                processList[processNumber].decreaseResource(resourceNumber); //undoes the thing
                pQueue.add(processNumber); //adds to the processes that are stalled
                cQueue.add(enterQueue); //adds the command to the queue
                System.out.println("(DENIED)");
            }
            else
            {
                System.out.println("(GRANTED)");
            }
        }
        
        if(type.equals("-"))//uses resource helper
        {
            System.out.println("Process " +processNumber + " has relinquished a resource " +resource);
            processList[processNumber].decreaseResource(resourceNumber);
        }
        
        if(type.equals("!")) //shouldn't need to use helpers, just return everything back
        {
            System.out.println("Process " + processNumber + " has terminated");
            processList[processNumber].releaseResources();
            finished[processNumber] = true;
            //attempt to go through the queue
            queueExecution();
        }
    }
    
    /**
     * Helper method for parsing the text on which resource to use/change
     */
    private static int parseResource(String resource)
    {
        return resource.compareTo("A"); //assuming everything is capitalized, A should be the lowest value
    }
    
    /**
     * Helper method for finding if the process is safe
     * If it returns false, the method will end up stalled
     * Continually check if a) we have the resource to do it and
     * b) if giving the resource won't make a deadlock (meaning something is able terminate and able to return enough resources so other things can terminate too
     */
    private static boolean isSafe()//find a better "isSafe() algorithm
    {
        fakeProcesses check = null;
        int[] tempResources = new int[resources.length]; //puts a pointer to resources
        int[] neededResources = null;
        int[] resourcesTaken = null;
        boolean[] canTerminate = new boolean[finished.length];
        int ii = 0;
       
        for (int x = 0; x < finished.length; x++)
        {
            canTerminate[x] = false;
            if (x < resources.length)
            {
                tempResources[x] = resources[x];
            }
        }
        int i = 0;
        while (i < finished.length) //finds if there's a false
        {
            /* 
            String temp = "";
            String current = ""; //read the amount of resources there are and how much there will be when one terminates
            for (int zz = 0; zz < tempResources.length; zz++)
            {
                temp = temp + tempResources[zz] + " ";
                current = current + resources[zz] + " ";
            }
            System.out.println("Current Resources on hand: " + current);
            System.out.println("Temp Resources on hand: " + temp); */
            
            if (canTerminate[i] == false) //if not "terminated" yet
            {
                if (finished[i] == false) //if it's not finished, find if it CAN finish
                {
                    check = processList[i];
                    neededResources = check.resourcesNeeded();
                    resourcesTaken = check.currentResourceAmount();
                    //find an instance where a process can terminate
                    ii = 0;
                    while (ii < neededResources.length)
                    {
                        if (neededResources[ii] > tempResources[ii])
                        {
                            break;
                        }
                        ii++;
                    }
                    if (ii == neededResources.length)
                    {
                        canTerminate [i] = true;
                        ii=0;
                        while (ii < neededResources.length)
                        {
                            tempResources[ii] = tempResources[ii] + resourcesTaken[ii];
                            ii++;
                        }
                        i = -1; //reset
                    }
                }
                else
                {
                    canTerminate[i] = true;
                }
            }
            i++;
        }
        for (int z = 0; z < canTerminate.length; z++) //after all the loops are over, 
        {
            if(canTerminate[z] == false)
            {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * When we terminate, we go through the queue and execute
     * We use a temporary Queue so we can switch things up
     */
    private static void queueExecution()
    {
        String command = null;
        String readLine[] = null;
        int x = 0;
        tempCommandQueue = new LinkedList<String>();
        tempProcessesStalled = new LinkedList<Integer>();
        
        String methodType = null;
        String processNumber = null;
        String resourceNumber = null;
        while (commandQueue.size() != 0)
        {
                System.out.print("  ");
                command = commandQueue.poll();
          
                
                x=command.length(); // check how many things are in this arguement.  Typically three, occasionally four
                
                readLine = command.split(""); //note, the line starts at readLine[1]; when you do this
                
                methodType = readLine[1];
                
                processNumber = "";
                    if(readLine[1].equals("!"))
                    {
                        for(int i = 2; i<(x+1); i++)
                        {
                            processNumber = processNumber + readLine[i];
                        }
                    }
                    else if(x == 3)
                    {
                        processNumber = readLine[2];
                        resourceNumber = readLine[3];
                    }
                    else
                    {
                        for(int i = 2; i<x; i++)
                        {
                            processNumber = processNumber + readLine[i];
                        }
                        resourceNumber = readLine[x];
                    }
                    //run a check if the process is in the processStalled list
                    if(tempProcessesStalled.contains(Integer.parseInt(processNumber)))
                    {
                        tempCommandQueue.add(command); //adds the line to a queue to parse
                    }
                    else //attempts to do the line.  If it can't accomplish it, adds it to the queue
                    {
                        action(methodType,Integer.parseInt(processNumber),resourceNumber,command, tempCommandQueue, tempProcessesStalled);
                    }
                    //attempt to run this queue.
        }
        
        commandQueue = tempCommandQueue; //reassign the queue's pointers
        processesStalled = tempProcessesStalled;
    }
    
    
    /**
     * Class to act as a mediator for the processes
     */
    static class fakeProcesses
    {
        private int[] neededResources; //how many resources needed to complete the process
        private int[] currentResources; //how many resources it currently holds
        private int maxResourceTypes; 
        private int processNumber; //for knowing which process this is
        
        public fakeProcesses(int maximumResourceTypes, int pNumber)
        {
            neededResources = new int[maximumResourceTypes];
            currentResources = new int[maximumResourceTypes];
            maxResourceTypes = maximumResourceTypes;
            processNumber = pNumber;
        }
        
        /**
         * Inputs the amount of neededResources
         */
        public void inputNeededResource(int resourceType, int neededResource)
        {
            neededResources[resourceType] = neededResource;
        }
        
        /**
         * Increments the amount of resources it has and decreases the neededResources and decrease the resource pool
         */
        public void increaseResouce(int resourceType)
        {
            currentResources[resourceType] = currentResources[resourceType] + 1;
            neededResources[resourceType] = neededResources[resourceType] -1;
            resources[resourceType] = resources[resourceType] -1;
        }
        
        /**
         * Decreases the amount of resources it has and increases the neededResources and increase the resource pool
         */
        public void decreaseResource(int resourceType)
        {
            currentResources[resourceType] = currentResources[resourceType] - 1; //don't have to worry about releasing a process it doesn't own
            neededResources[resourceType] = neededResources[resourceType] +1;
            resources[resourceType] = resources[resourceType] +1;
        }
        
        
        /**
         * Releases the amount of resources this holds
         */
        public void releaseResources()
        {
            for (int i = 0; i < currentResources.length; i++)
            {   //taking extra precautions to make sure there aren't any resources there shouldn't be
                //resources[i] = resources[i] + getInt(currentResources, i);
                resources[i] = resources[i] + currentResources[i]; //current resources get back the ones this one is reliquishing
                currentResources[i] = 0;
            }
        }
        
        /**
         * Helper method
         */
        public int[] resourcesNeeded()
        {
            return neededResources;
        }
        
        /**
         * Helper method
         */
        public int[] currentResourceAmount()
        {
            return currentResources;
        }
        
        /**
         * Grabs an integers from a specified part of the array (whether it's from the needed or current resource array)
         */
        public int getInt(int[] array, int place)
        {
            return array[place];
        }
        
        /**
         * Check for if the resources have been fufilled.  If so, another method will call "releaseResources()"  This is just a method if it's needed
         */
        public boolean fulfilledResources()
        {
            for(int i = 0; i< maxResourceTypes; i++)
            {
                if (neededResources[i] > 0)
                {
                    return false;
                }
            }
            return true;
        }
    }
}
