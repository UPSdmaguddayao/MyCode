import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.*;
/**
 * Simulate process scheduling.  This wouldn't need to be multithreaded. 
 * Have several processes that have alternating CPU and I/O bursts--for 
 * example, one process might need 5 ms of CPU time, followed by 10 ms on 
 * Device A, followed by 12 ms of CPU time, etc.  Allow the system to vary in 
 * the number of CPUs it has, and the CPU-scheduling algorithm (e.g. FIFO, 
 * shortest-job-first, preemptive-shortest-job-first).  Show how choice of 
 * scheduling algorithm helps or hurts average wait time, throughput time, 
 * etc.

 * 
 * @author DJ Maguddayao
 * @version 5.14.14
 */
class CPUBurstScheduler
{    
    public static void main(String[] args)
    {
        File fileName = null; //initiate the fileName
        int CPUAvailable = 0; //we'll fill this in a bit.  The file we read should have it
        ArrayList<Integer> waitingOnCPU = new ArrayList<Integer>(); //keeps track if something is waiting on CPU
        ArrayList<Job> finishedList = new ArrayList<Job>(); //helps for the very end when we do our calculations
        ArrayList<CPU> CPUList = new ArrayList<CPU>(); //list of CPUs
        
        if (args.length == 0) //no input was added
        {
            //System.out.println("Error, no file is read");
            return;
        }
        else 
        {
            //System.out.println(args[0]);
            fileName = new File(args[0]); //attaches the "file".  The try catch block will catch if it's actually a legit file or not
        }
        
        try
        {
            Scanner parser = new Scanner(fileName); //will fault if the file doesn't exist.
            String currentLine = "";
            String readLine[] = null;
            
            //grab CPU
            if(parser.hasNext())
            {
                currentLine = parser.nextLine();
                readLine = currentLine.split(" "); //seperates from "CPUAvailable:" and the actual value
                //System.out.println("CPU Available: " + readLine[1]); //got the right value
                CPUAvailable = Integer.parseInt(readLine[1]); //if it follows the format given, it should be this line
            }
            else
            {
                System.out.println("Error, empty file"); //input file was empty
                return;
            }
            
            for (int i = 0; i < CPUAvailable; i++) //creates the CPUs needed
            {
                CPU temp = new CPU(i);
                CPUList.add(temp);
            }
            //System.out.println("Number of CPUs: " + CPUAvailable);
            //now get rest of lists ready
            
            int processNumber; //for labeling the processes.  Helps for parts later
            int arrivalTime; //self explanitory
            int noOfBursts;
            int burstType;  //0 is CPU, 1 is for IO.  Could use boolean, but this works as well
            int CPULocation = 0; //for storing which CPU the process is allocated to
            
            while(parser.hasNext())
            {
                processNumber = 0;
                arrivalTime = 0;
                noOfBursts = 0;
                burstType = 0; //keeps track of which type we're looking at when parsing so we can put it in the correct table
                currentLine = parser.nextLine(); //for reading the lines one at a time
                
                if (currentLine.trim().length() == 0)
                {
                    //empty line, do nothing
                }
                else
                {
                    //Should decide here which Jobs go to which CPUs to make calculations easier
                    readLine = currentLine.split("\\t"); //make sure everything is tabbed                    
                    try //catches if it didn't read an int.  If so
                    {
                        noOfBursts = ((readLine.length) -2)/2 ;
                        processNumber = Integer.parseInt(readLine[0]);
                        arrivalTime = Integer.parseInt(readLine[1]); 
                        //System.out.println("Arrival time of: " + arrivalTime + "ms");
                        
                        //creates four instances so that each type of Scheduler can run
                        Job jobber1 = new Job(processNumber, arrivalTime, noOfBursts);
                        Job jobber2 = new Job(processNumber, arrivalTime, noOfBursts);
                        Job jobber3 = new Job(processNumber, arrivalTime, noOfBursts);
                        Job jobber4 = new Job(processNumber, arrivalTime, noOfBursts);
                        
                        for (int i = 2; i < readLine.length ; i++) //starts at line 2
                        {
                            if(burstType == 0) //CPUBurst fill
                            {
                                jobber1.fillCPUBurst(Integer.parseInt(readLine[i]));
                                jobber2.fillCPUBurst(Integer.parseInt(readLine[i]));
                                jobber3.fillCPUBurst(Integer.parseInt(readLine[i]));
                                jobber4.fillCPUBurst(Integer.parseInt(readLine[i]));
                                //System.out.println("Filled out CPU Burst of: " + readLine[i] + " for Process " + processNumber);
                                
                                burstType = 1; //switches to IOBurst
                            }
                            else //IOBurst fill
                            {
                                jobber1.fillIOBurst(Integer.parseInt(readLine[i]));
                                jobber2.fillIOBurst(Integer.parseInt(readLine[i]));
                                jobber3.fillIOBurst(Integer.parseInt(readLine[i]));
                                jobber4.fillIOBurst(Integer.parseInt(readLine[i]));
                                //System.out.println("Filled out IO Burst of: " + readLine[i] + " for Process " + processNumber);
                                burstType = 0; //switches back
                            }
          
                        }
                        //jobber.printCPUBurst();// helper for seeing the entire arrays of a process
                        //jobber.printIOBurst(); //helper for seing the arrays
                        
                        CPUList.get(CPULocation%CPUAvailable).addJob(jobber1,jobber2,jobber3,jobber4); //splits the jobs evenly across the CPUs
                        
                        //System.out.println("Process Number: "+ processNumber + " sent to CPU " +CPULocation%CPUAvailable);
                        
                        CPULocation++; //increments each time.  Never hits error due to CPULocation%CPUAvailable so don't have to worry if it goes past max
                    }
                    catch (NumberFormatException e)
                    {
                        //This is the case of the second and third line occuring in my example.  This shouldn't take any String values at all.  Else we're not following the format
                    }
                }
            }
            
            
            //now that we finished initializing everything, have the CPUs do work.
            
            for (int cpu = 0; cpu < CPUList.size(); cpu++)
            {
                System.out.println("Running process on CPU: " + (cpu+1));
                CPUList.get(cpu).runScheduler();
            }
            
        }
        catch(FileNotFoundException fnfe) //exception if the directory to the file isn't correct
        {
            System.out.println("Error, file does not exist");
            return;
        }
    }

    /**
     * CPU Class for handling all the calculations.  Goes slowly (1 ms each iteration) for each
     * schedule run calculations easier
     */
    static class CPU
    {
        private int CPUNumber; //labels the CPU
        
        private ArrayList<Job> BurstList; //should only contain one thing at a time.  It's checked below in code
        private ArrayList<Job> WaitList; //list of Jobs waiting for another process to be finished with CPU
        private ArrayList<Job> IOList; //list for jobs in the IOBurst phase (i.e. not waiting anymore)
        private ArrayList<Job> finishedList;//helps for the very end when we do our calculations.  Is cleared at the end of each scheduler's run
        
        //needed to make a JobList and CheckList for every scheduler.  This is because the initial list (JobList) makes
        //it so that we know what Jobs there are.  We remove Jobs to the CheckList so that we know what hasn't be added to
        //any of the other lists (because of the initial check at each of the Schedulers).  We remove objects from the 
        //CheckList to make it so that we know which jobs are finished.
        private ArrayList<Job> FCFSJobList; 
        private ArrayList<Job> FCFSJobCheckList;
        
        private ArrayList<Job> SJFJobList;
        private ArrayList<Job> SJFJobCheckList;
        
        private ArrayList<Job> STRJobList;
        private ArrayList<Job> STRJobCheckList;
        
        private ArrayList<Job> RRJobList;
        private ArrayList<Job> RRJobCheckList;
        
        
        private static int ROUND_ROBIN = 10; //change this to however you want.  This is the default for how many ms each process has in Round Robin
        /**
         * Labels the CPU with a number.  Helps for tracking
         */
        public CPU(int label)
        {
            CPUNumber = label; 
            
            //initiates all the ArrayList
            FCFSJobList = new ArrayList<Job>();
            FCFSJobCheckList = new ArrayList<Job>();
            BurstList = new ArrayList<Job>();
            WaitList = new ArrayList<Job>();
            IOList = new ArrayList<Job>();
            finishedList = new ArrayList<Job>();
            
            SJFJobList = new ArrayList<Job>();
            SJFJobCheckList = new ArrayList<Job>();
            
            STRJobList = new ArrayList<Job>();
            STRJobCheckList = new ArrayList<Job>();
            
            RRJobList = new ArrayList<Job>();
            RRJobCheckList = new ArrayList<Job>();
        }
        
        /**
         * Adds a job to the CPU
         */
        public void addJob(Job jobber1, Job jobber2, Job jobber3, Job jobber4)
        {
            FCFSJobList.add(jobber1);
            SJFJobList.add(jobber2);
            STRJobList.add(jobber3);
            RRJobList.add(jobber4);
        }
        
        /**
         * Method to run the different type of schedule algorithms.  Need to find a way to make copies of an instance
         * rather than pointers.  Could just make multiples though.  Need to implement two methods before trying this out.
         */
        public void runScheduler()
        {
            
                firstComeFirstServeScheduler();
                shortestJobFirstScheduler();
                shortestTimeRemainingScheduler(); 
                roundRobinScheduler();
            
        }
        
        /**
         * Use this to clear the lists after each scheduler is done
         */
        public void clearEverything()
        {
            BurstList.clear();
            WaitList.clear();
            IOList.clear();
            finishedList.clear();
        }
        
        /**
         * Scheduler where the first thing is is the first one out.  Should run a queue of some sort (ArrayList has a queue)
         */
        public void firstComeFirstServeScheduler()
        {
            int time = 0; //tracks the time
            Job tempJob = null; //initiate this so we can replace some objects
            int name = 0; //this is used for checking the name of a process
            
            while(!FCFSJobList.isEmpty() || !FCFSJobCheckList.isEmpty()) //JobCheckList is used to seeing if anything is still running
            {
                tempJob = null;// just in case, we set it to null each time
                
                //run code.  Remove jobs from job list and the other lists when they're finished
                
                //first thing: check if anything arrived at the time.  
                for (int i = 0; i < FCFSJobList.size(); i++) //uses JobCheckList so it doesn't iterate over and over
                {
                    if (time == FCFSJobList.get(i).getArrivalTime())
                    {
                        WaitList.add(FCFSJobList.get(i)); //adds to a waiting list.  Important for later
                        FCFSJobCheckList.add(FCFSJobList.get(i)); //adds to CheckList
                        
                        ////System.out.println("Process number " + JobList.get(i).getProcessName() + "has been added at time" + time);
                       
                        FCFSJobList.remove(i); //removes it from this list so we don't have to loop everytime once the process has already been added
                        i--; //has to move back by one otherwise errors can occur
                    }
                }
                
                if(BurstList.size() > 1) //error check.  Shouldn't ever occur, but you never know
                {
                    System.out.println ("Error, too many things in the burst list");
                }
                
                if(!IOList.isEmpty()) //there are items in IO Burst
                {
                    for(int j = 0; j < IOList.size(); j++)
                    {
                        //decrement the wait on IO (assuming this is how the IOBurst works)
                        
                        tempJob = IOList.get(j);
                        tempJob.lowerIOBurstTime();
                        
                        //System.out.println("Process " + tempJob.getProcessName() + " has " + tempJob.getCurrentIOBurst() + " left for waiting IO");
                        if(tempJob.getCurrentIOBurst() == 0) //if it's done being in IOBurst, potentially put it back in waitList
                        {
                            tempJob.removeIOBurst(); //removes the IOBurst from it's own array
                            IOList.remove(j); //remove it from the IOBurstList
                            j--; //set j back so we don't end up with errors
                            
                            if(tempJob.isFinished()) //has to check if finished.  If so, remove the reference in JobCheckList
                            {
                                tempJob.setEndTime(time);
                                name = tempJob.getProcessName(); //check the name so we can remove it from CheckList
                                
                                for (int c = 0; c < FCFSJobCheckList.size(); c++) //check the checklist and remove a process that has been finished
                                {
                                    if(FCFSJobCheckList.get(c).getProcessName() == name)
                                    {
                                        finishedList.add(tempJob); //add this to the finishedList
                                        FCFSJobCheckList.remove(c);
                                        break; //gets out of the loop.  We found what we wanted
                                    }
                                }
                                
                                //System.out.println("Process " + name + "has finished");
                            }
                            else
                            {
                                WaitList.add(tempJob); //adds it to the waitlist instead if not finished
                                ////System.out.println("Process" + tempJob.getProcessName() + " has been added to waitlist");
                            }
                        }
                        else
                        {
                            IOList.set(j, tempJob); //if it wasn't removed, then replace the values
                        }
                    }
                }
                
                if(!BurstList.isEmpty()) //if the burstlist isn't empty, then run do "calculations"
                {
                    tempJob = BurstList.get(0); //should only be one item in the list.  If not...welp
                    tempJob.lowerCPUBurstTime();
                    
                    //System.out.println("Process " + tempJob.getProcessName() + "has a burst time of " + tempJob.getCurrentCPUBurst() + " ms remaining");
                    
                    BurstList.remove(0); //remove it from the BurstList
                    if (tempJob.getCurrentCPUBurst() != 0)
                    {
                        BurstList.add(tempJob);
                    }
                    else
                    {
                        tempJob.removeCPUBurst();
                        if(tempJob.isFinished())
                        {
                            //has to check if finished.  If so, remove the reference in JobCheckList
                            tempJob.setEndTime(time);
                            name = tempJob.getProcessName(); //grab the name to run comparisons
                            for (int c = 0; c < FCFSJobCheckList.size(); c++)
                            {
                                if(FCFSJobCheckList.get(c).getProcessName() == name)
                                {
                                    finishedList.add(tempJob);
                                    FCFSJobCheckList.remove(c);
                                    ////System.out.println("Process " + name + " has finished");
                                    break;
                                }
                            }
                            //System.out.println("Process " + name + "has finished");
                        }
                        else
                        {
                            IOList.add(tempJob); //now in IOBurst
                            //System.out.println("Process " + tempJob.getProcessName() + "has been added to IOList");
                        }
                    }
                    //everything in the waitlist needs to increment as well
                    for(int w = 0; w < WaitList.size(); w++)
                    {
                        tempJob = WaitList.get(w);
                        tempJob.incrementWaitTime();
                        ////System.out.println("Process " + tempJob.getProcessName() + "has been waiting for " + tempJob.getWaitTime());
                        WaitList.set(w, tempJob);
                    }
                    
                }
                
                if (BurstList.isEmpty() && !WaitList.isEmpty()) //nothing in the burst list and waitlist isn't empty
                {
                    //grab the first thing that arrived in the WaitList.
                    
                    //System.out.println("Process " + WaitList.get(0).getProcessName() + " has been added to the BurstList");
                    
                    BurstList.add(WaitList.get(0));
                    
                    WaitList.remove(0); //removes it from the waiting list
                    
                }
                
                time++;
            }
            
            System.out.println("First Come First Serve Results"); //at the end of everything
            for (int f = 0; f < finishedList.size(); f++)
            {
                System.out.println("Process " + finishedList.get(f).getProcessName() + "has a total wait time of " + finishedList.get(f).getWaitTime() + "ms and a total turnaround time of " + finishedList.get(f).getTurnaroundTime() + "ms");
            }
            System.out.println("");
            
            clearEverything(); //clears it out so another scheduler can run
        }
        
        /**
         * Scheduler where the shortest CPU burst time at the moment will be ran
         */
        public void shortestJobFirstScheduler()
        {
            int time = 0;
            Job tempJob = null;
            int name = 0;
            int shortestBurst = 0; //used for comparisons
            while(!SJFJobList.isEmpty() || !SJFJobCheckList.isEmpty()) //JobCheckList is used to seeing if anything is still running
            {
                tempJob = null;// just in case
                
                //first thing: check if anything arrived at the time.  
                for (int i = 0; i < SJFJobList.size(); i++) //uses JobCheckList so it doesn't iterate over and over
                {
                    if (time == SJFJobList.get(i).getArrivalTime())
                    {
                        WaitList.add(SJFJobList.get(i)); //adds to a waiting list.  Important for later
                        SJFJobCheckList.add(SJFJobList.get(i)); //
                        
                        //System.out.println("Process number " + JobList.get(i).getProcessName() + "has been added at time" + time);
                        
                        SJFJobList.remove(i); //removes it from this list
                        i--; //has to move back by one otherwise errors can occur
                       
                    }
                }
                
                if(BurstList.size() > 1) //error check
                {
                    //System.out.println ("Error, too many things in the burst list");
                }
                
                if(!IOList.isEmpty()) //items in IO Burst
                {
                    for(int j = 0; j < IOList.size(); j++)
                    {
                        //decrement the wait on IO (assuming this is how the IOBurst works)
                        tempJob = IOList.get(j);
                        tempJob.lowerIOBurstTime();
                        ////System.out.println("Process " + tempJob.getProcessName() + " has " + tempJob.getCurrentIOBurst() + " left for waiting IO");
                        if(tempJob.getCurrentIOBurst() == 0)
                        {
                            tempJob.removeIOBurst();
                            IOList.remove(j);
                            j--;
                            if(tempJob.isFinished())
                            {
                                //has to check if finished.  If so, remove the reference in JobCheckList
                                tempJob.setEndTime(time);
                                name = tempJob.getProcessName();
                                for (int c = 0; c < SJFJobCheckList.size(); c++)
                                {
                                    if(SJFJobCheckList.get(c).getProcessName() == name)
                                    {
                                        finishedList.add(tempJob);
                                        SJFJobCheckList.remove(c);
                                        break; //gets out of the loop
                                    }
                                }
                                //System.out.println("Process " + name + "has finished");
                            
                            }
                            else
                            {
                                WaitList.add(tempJob);
                                //System.out.println("Process" + tempJob.getProcessName() + " has been added to waitlist");
                            }
                        }
                        else
                        {
                            IOList.set(j, tempJob);
                        }
                    }
                }
                
                if(!BurstList.isEmpty())
                {
                    tempJob = BurstList.get(0); //should only be one item in the list
                    tempJob.lowerCPUBurstTime();
                    //System.out.println("Process " + tempJob.getProcessName() + "has a burst time of " + tempJob.getCurrentCPUBurst() + "ms remaining");
                    BurstList.remove(0);
                    if (tempJob.getCurrentCPUBurst() != 0)
                    {
                        BurstList.add(tempJob);
                    }
                    else
                    {
                        tempJob.removeCPUBurst();
                        if(tempJob.isFinished())
                        {
                            //has to check if finished.  If so, remove the reference in JobCheckList
                            tempJob.setEndTime(time);
                            name = tempJob.getProcessName();
                            for (int c = 0; c < SJFJobCheckList.size(); c++)
                            {
                                if(SJFJobCheckList.get(c).getProcessName() == name)
                                {
                                    finishedList.add(tempJob);
                                    SJFJobCheckList.remove(c);
                                    //System.out.println("Process " + name + " has finished");
                                    break;
                                }
                            }
                            //System.out.println("Process " + name + "has finished");
                        }
                        else
                        {
                            IOList.add(tempJob); //now in IOBurst
                            //System.out.println("Process " + tempJob.getProcessName() + "has been added to IOList");
                        }
                    }
                    //everything in the waitlist needs to increment as well
                    for(int w = 0; w < WaitList.size(); w++)
                    {
                        tempJob = WaitList.get(w);
                        tempJob.incrementWaitTime();
                        ////System.out.println("Process " + tempJob.getProcessName() + "has been waiting for " + tempJob.getWaitTime());
                        WaitList.set(w, tempJob);
                    }
                    
                }
                
                if (BurstList.isEmpty() && !WaitList.isEmpty()) //nothing in the burst list and waitlist isn't empty
                {
                    //grab the first thing that arrived in the WaitList.
                    shortestBurst = 0; //keeps track of which item in the list has the shortest burst time.
                    
                    for(int s = 0; s < WaitList.size(); s++)
                    {
                        if(WaitList.get(shortestBurst).getCurrentCPUBurst() > WaitList.get(s).getCurrentCPUBurst())
                        {
                            shortestBurst = s;
                        }
                    }
                    
                    //System.out.println("Process " + WaitList.get(shortestBurst).getProcessName() + " was added to the BurstList with a burst of " + WaitList.get(shortestBurst).getCurrentCPUBurst());
                    BurstList.add(WaitList.get(shortestBurst));
                    WaitList.remove(shortestBurst);
                    
                }
                
                time++;
            }
            
            System.out.println("Shortest Job First Results");
            for (int f = 0; f < finishedList.size(); f++)
            {
                System.out.println("Process " + finishedList.get(f).getProcessName() + "has a total wait time of " + finishedList.get(f).getWaitTime() + "ms and a total turnaround time of " + finishedList.get(f).getTurnaroundTime() + "ms");
            }
            
            System.out.println("");
            clearEverything();
        }
        
        /**
         * Scheduler where the Job with the shortest remaining CPUBurst is ran first
         * This occurs when something is added to the list
         */
        public void shortestTimeRemainingScheduler()
        {
            int time = 0;
            Job tempJob = null;
            int name = 0;
            int shortestBurst = 0;
            boolean somethingAdded = false;
            while(!STRJobList.isEmpty() || !STRJobCheckList.isEmpty()) //JobCheckList is used to seeing if anything is still running
            {
                tempJob = null;// just in case
                //run code.  Remove jobs from job list and the other lists when they're finished
                
                //first thing: check if anything arrived at the time.  
                for (int i = 0; i < STRJobList.size(); i++) //uses JobCheckList so it doesn't iterate over and over
                {
                    if (time == STRJobList.get(i).getArrivalTime())
                    {
                        WaitList.add(STRJobList.get(i)); //adds to a waiting list.  Important for later
                        STRJobCheckList.add(STRJobList.get(i)); //
                        //System.out.println("Process number " + JobList.get(i).getProcessName() + "has been added at time" + time);
                        STRJobList.remove(i); //removes it from this list
                        i--; //has to move back by one otherwise errors can occur
                        
                        somethingAdded = true; //something was added.  So it can run a check if it has a shorter burst
                     
                    }
                }
                
                if(BurstList.size() > 1) //error check
                {
                    //System.out.println ("Error, too many things in the burst list");
                }
                
                if(!IOList.isEmpty()) //items in IO Burst
                {
                    for(int j = 0; j < IOList.size(); j++)
                    {
                        //decrement the wait on IO (assuming this is how the IOBurst works)
                        tempJob = IOList.get(j);
                        tempJob.lowerIOBurstTime();
                       // //System.out.println("Process " + tempJob.getProcessName() + " has " + tempJob.getCurrentIOBurst() + " left for waiting IO");
                        if(tempJob.getCurrentIOBurst() == 0)
                        {
                            tempJob.removeIOBurst();
                            IOList.remove(j);
                            j--;
                            if(tempJob.isFinished())
                            {
                                //has to check if finished.  If so, remove the reference in JobCheckList
                                tempJob.setEndTime(time);
                                name = tempJob.getProcessName();
                                for (int c = 0; c < STRJobCheckList.size(); c++)
                                {
                                    if(STRJobCheckList.get(c).getProcessName() == name)
                                    {
                                        finishedList.add(tempJob);
                                        STRJobCheckList.remove(c);
                                        break; //gets out of the loop
                                    }
                                }
                                //System.out.println("Process " + name + "has finished");
                            
                            }
                            else
                            {
                                WaitList.add(tempJob);
                                //System.out.println("Process" + tempJob.getProcessName() + " has been added to waitlist");
                                somethingAdded = true;
                            }
                        }
                        else
                        {
                            IOList.set(j, tempJob);
                        }
                    }
                }
                
                if(!BurstList.isEmpty())
                {
                    tempJob = BurstList.get(0); //should only be one item in the list
                    tempJob.lowerCPUBurstTime();
                   // //System.out.println("Process " + tempJob.getProcessName() + "has a burst time of " + tempJob.getCurrentCPUBurst() + "ms remaining");
                    BurstList.remove(0);
                    if (tempJob.getCurrentCPUBurst() != 0)
                    {
                        BurstList.add(tempJob);
                    }
                    else
                    {
                        tempJob.removeCPUBurst();
                        if(tempJob.isFinished())
                        {
                            //has to check if finished.  If so, remove the reference in JobCheckList
                            tempJob.setEndTime(time);
                            name = tempJob.getProcessName();
                            for (int c = 0; c < STRJobCheckList.size(); c++)
                            {
                                if(STRJobCheckList.get(c).getProcessName() == name)
                                {
                                    finishedList.add(tempJob);
                                    STRJobCheckList.remove(c);
                                    //System.out.println("Process " + name + " has finished");
                                    break;
                                }
                            }
                            //System.out.println("Process " + name + "has finished");
                        }
                        else
                        {
                            IOList.add(tempJob); //now in IOBurst
                           // //System.out.println("Process " + tempJob.getProcessName() + "has been added to IOList");
                        }
                    }
                    //everything in the waitlist needs to increment as well
                    for(int w = 0; w < WaitList.size(); w++)
                    {
                        tempJob = WaitList.get(w);
                        tempJob.incrementWaitTime();
                        ////System.out.println("Process " + tempJob.getProcessName() + "has been waiting for " + tempJob.getWaitTime());
                        WaitList.set(w, tempJob);
                    }
                    
                }
                
                if(!BurstList.isEmpty() && somethingAdded == true) //if something was added recently, so we check something has a smaller burst than what's in the BurstList
                {
                    shortestBurst = 0;
                    for (int a = 0; a < WaitList.size(); a++)
                    {
                        if (BurstList.get(0).getCurrentCPUBurst() > WaitList.get(a).getCurrentCPUBurst())
                        {
                            shortestBurst = a;
                        }
                    }
                    
                    //run a check if it's actually larger (and not the first item in the list
                    
                    if (BurstList.get(0).getCurrentCPUBurst() > WaitList.get(shortestBurst).getCurrentCPUBurst())
                    {
                        tempJob = BurstList.get(0);
                        //System.out.println("Process " + tempJob.getProcessName() + " has been evicted with a burst of " + tempJob.getCurrentCPUBurst());
                        WaitList.add(tempJob);
                        BurstList.remove(0);
                        BurstList.add(WaitList.get(shortestBurst));
                        //System.out.println("Process " + BurstList.get(0).getProcessName() + " has been added with a burst of " + BurstList.get(0).getCurrentCPUBurst());
                        WaitList.remove(shortestBurst);
                    }
                    somethingAdded = false; //switches off
                }
                
                if (BurstList.isEmpty() && !WaitList.isEmpty()) //nothing in the burst list and waitlist isn't empty
                {
                    //grab the first thing that arrived in the WaitList.
                    shortestBurst = 0;
                    for(int s = 0; s < WaitList.size(); s++)
                    {
                        if(WaitList.get(shortestBurst).getCurrentCPUBurst() > WaitList.get(s).getCurrentCPUBurst())
                        {
                            shortestBurst = s;
                        }
                    }
                    //System.out.println("Process " + WaitList.get(shortestBurst).getProcessName() + " was added to the BurstList with a burst of " + WaitList.get(shortestBurst).getCurrentCPUBurst());
                    BurstList.add(WaitList.get(shortestBurst));
                    WaitList.remove(shortestBurst);
                    somethingAdded = false;
                    //Something to note, it's added to the list when something is finished, it won't start 'computing' until the next ms
                }
                
                time++;
            }
            
            System.out.println("Shortest Time Remaining Results");
            for (int f = 0; f < finishedList.size(); f++)
            {
                System.out.println("Process " + finishedList.get(f).getProcessName() + "has a total wait time of " + finishedList.get(f).getWaitTime() + "ms and a total turnaround time of " + finishedList.get(f).getTurnaroundTime() + "ms");
            }
            
            System.out.println("");
            clearEverything();
        }
        
        /**
         * Runs a round robin giving jobs a certain amount of time to do CPUBurst.  If it isn't finished
         * it puts it back in waiting
         */
        public void roundRobinScheduler()
        {
            int time = 0;
            Job tempJob = null;
            int name = 0;
            int counter = 0; //tracks how long something has been in the BurstList
            while(!RRJobList.isEmpty() || !RRJobCheckList.isEmpty()) //JobCheckList is used to seeing if anything is still running
            {
                tempJob = null;// just in case
                
                //first thing: check if anything arrived at the time.  
                for (int i = 0; i < RRJobList.size(); i++) //uses JobCheckList so it doesn't iterate over and over
                {
                    if (time == RRJobList.get(i).getArrivalTime())
                    {
                        WaitList.add(RRJobList.get(i)); //adds to a waiting list.  Important for later
                        RRJobCheckList.add(RRJobList.get(i)); //
                        //System.out.println("Process number " + JobList.get(i).getProcessName() + "has been added at time" + time);
                        RRJobList.remove(i); //removes it from this list
                        i--; //has to move back by one otherwise errors can occur
                    }
                }
                
                if(BurstList.size() > 1) //error check
                {
                    //System.out.println ("Error, too many things in the burst list");
                }
                
                if(!IOList.isEmpty()) //items in IO Burst
                {
                    for(int j = 0; j < IOList.size(); j++)
                    {
                        //decrement the wait on IO (assuming this is how the IOBurst works)
                        tempJob = IOList.get(j);
                        tempJob.lowerIOBurstTime();
                        ////System.out.println("Process " + tempJob.getProcessName() + " has " + tempJob.getCurrentIOBurst() + " left for waiting IO");
                        if(tempJob.getCurrentIOBurst() == 0)
                        {
                            tempJob.removeIOBurst();
                            IOList.remove(j);
                            j--;
                            if(tempJob.isFinished())
                            {
                                //has to check if finished.  If so, remove the reference in JobCheckList
                                tempJob.setEndTime(time);
                                name = tempJob.getProcessName();
                                for (int c = 0; c < RRJobCheckList.size(); c++)
                                {
                                    if(RRJobCheckList.get(c).getProcessName() == name)
                                    {
                                        finishedList.add(tempJob);
                                        RRJobCheckList.remove(c);
                                        break; //gets out of the loop
                                    }
                                }
                                //System.out.println("Process " + name + "has finished");
                            
                            }
                            else
                            {
                                WaitList.add(tempJob);
                                //System.out.println("Process" + tempJob.getProcessName() + " has been added to waitlist");
                            }
                        }
                        else
                        {
                            IOList.set(j, tempJob);
                        }
                    }
                }
                
                if(!BurstList.isEmpty())
                {
                    tempJob = BurstList.get(0); //should only be one item in the list
                    tempJob.lowerCPUBurstTime();
                    ////System.out.println("Process " + tempJob.getProcessName() + "has a burst time of " + tempJob.getCurrentCPUBurst() + "ms remaining");
                    BurstList.remove(0);
                    if (tempJob.getCurrentCPUBurst() != 0)
                    {
                        BurstList.add(tempJob);
                    }
                    else
                    {
                        tempJob.removeCPUBurst();
                        if(tempJob.isFinished())
                        {
                            //has to check if finished.  If so, remove the reference in JobCheckList
                            tempJob.setEndTime(time);
                            name = tempJob.getProcessName();
                            for (int c = 0; c < RRJobCheckList.size(); c++)
                            {
                                if(RRJobCheckList.get(c).getProcessName() == name)
                                {
                                    finishedList.add(tempJob);
                                    RRJobCheckList.remove(c);
                                    //System.out.println("Process " + name + " has finished");
                                    break;
                                }
                            }
                            //System.out.println("Process " + name + "has finished");
                        }
                        else
                        {
                            IOList.add(tempJob); //now in IOBurst
                            //System.out.println("Process " + tempJob.getProcessName() + "has been added to IOList");
                        }
                        counter = -1; //ticks so that counter = 0 at the end of this;
                    }
                    //everything in the waitlist needs to increment as well
                    for(int w = 0; w < WaitList.size(); w++)
                    {
                        tempJob = WaitList.get(w);
                        tempJob.incrementWaitTime();
                        ////System.out.println("Process " + tempJob.getProcessName() + "has been waiting for " + tempJob.getWaitTime());
                        WaitList.set(w, tempJob);
                    }
                    counter++;
                    
                    if(counter == ROUND_ROBIN) //times up.  Evict this process for now.
                    {
                        tempJob = BurstList.remove(0);
                        //System.out.println("Process " + tempJob.getProcessName() + "has been evicted with burst " + tempJob.getCurrentCPUBurst());
                        WaitList.add(tempJob);
                    }
                }
                
                if (BurstList.isEmpty() && !WaitList.isEmpty()) //nothing in the burst list and waitlist isn't empty
                {
                    //grab the first thing that arrived in the WaitList.
                    //System.out.println("Process " + WaitList.get(0).getProcessName() + " has been added to the BurstList");
                    BurstList.add(WaitList.get(0));
                    WaitList.remove(0); //removes it from the waiting list
                    counter = 0;
                    //Something to note, it's added to the list when something is finished, it won't start 'computing' until the next ms
                }
                
                time++;
            }
            
            System.out.println("Round Robin Results");
            for (int f = 0; f < finishedList.size(); f++)
            {
                System.out.println("Process " + finishedList.get(f).getProcessName() + "has a total wait time of " + finishedList.get(f).getWaitTime() + "ms and a total turnaround time of " + finishedList.get(f).getTurnaroundTime() + "ms");
            }
            System.out.println("");
            clearEverything();
        }
    }
    
    /**
     * Class Representing different Jobs.  This hold the meat and bones of the Job's CPUBursts and IOBursts
     */
    static class Job
    {
        private int processorAffinity; //might not need this if my algorithm is forces hard affinity
        private int arrivalTime;
        private int endTime;
        private int processName;
        private ArrayList<Integer> CPUBurst;
        private ArrayList<Integer> IOBurst;
        
        private boolean inCPUBurst;
        private int totalWaitTime;
        
        public Job(int pName, int aTime, int NoOfBursts) //find out the NoOfBursts later.  This will be a place holder for now
        {
            processName = pName;
            arrivalTime = aTime;
            CPUBurst = new ArrayList<Integer>();
            IOBurst = new ArrayList<Integer>();
            inCPUBurst = true;
            totalWaitTime = 0;
            endTime = 0;
        }
        
        /**
         * Calculates the time spent until it finishes
         */
        public int getTurnaroundTime()
        {
            return endTime - arrivalTime;
        }
        
        public void fillCPUBurst(int BurstValue)
        {
            CPUBurst.add(BurstValue);
        }
        
        public void fillIOBurst(int BurstValue)
        {
            IOBurst.add(BurstValue);
        }
        
        public void incrementWaitTime()
        {
            totalWaitTime = totalWaitTime + 1;
        }
        
        public int getWaitTime()
        {
            return totalWaitTime;
        }
        
        public int getArrivalTime()
        {
            return arrivalTime;
        }
        
        public void setEndTime(int end)
        {
            endTime = end;
        }
        public int getCurrentIOBurst()
        {
            return IOBurst.get(0);
        }
        
        public int getCurrentCPUBurst()
        {
            return CPUBurst.get(0);
        }
        
        /**
         * This one benefits shortest time remaining and round robin scheduler the most, but it's needed
         * for the other ones if I'm going each ms at a time.
         */
        public void lowerCPUBurstTime()
        {
            int x = CPUBurst.get(0);
            CPUBurst.set(0, x-1);
        }
        
        /**
         * Use this to decrease the IOBurst time each time.  When an IOBurst hits zero, remove it and add it back to
         * the CPUBurst waiting line
         */
        public void lowerIOBurstTime()
        {
            int x = IOBurst.get(0);
            IOBurst.set(0, x-1);
        }
        
        /**
         * Removes the burst.  Used when it is finished being in that cycle
         */
        public void removeCPUBurst()
        {
            CPUBurst.remove(0);
        }
        
        /**
         * Same as remoeCPUBurst()
         */
        public void removeIOBurst()
        {
            IOBurst.remove(0);
        }
        
        /**
         * Helper method to test things out
         */
        public void printCPUBurst()
        {
            String s = "";
            for (int i = 0; i < CPUBurst.size(); i++)
            {
                s = s+ CPUBurst.get(i) + " ";
            }
            //System.out.println(s);
        }
        
        /**
         * Helper method to test things out
         */
        public void printIOBurst()
        {
            String s = "";
            for (int i = 0; i < IOBurst.size(); i++)
            {
                s = s+ IOBurst.get(i) + " ";
            }
            //System.out.println(s);
        }
        
        public int getProcessName()
        {
            return processName;
        }
        
        /**
         * Helps for determining if a Job is finished (no more CPUBursts and IOBursts)
         */
        public boolean isFinished()
        {
            if(CPUBurst.isEmpty() && IOBurst.isEmpty())
            {
                return true;
            }
            return false;
        }
    }
}
