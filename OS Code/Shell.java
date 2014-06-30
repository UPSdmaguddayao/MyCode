import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
public class Shell
{  
    public static void main(String[] args) {  
        try {  
            Runtime rt = Runtime.getRuntime();  //get the current runtime
            BufferedReader in = new BufferedReader
               (new InputStreamReader(System.in));  //reads the input to the 'commandline'
            String commandLine;
            CommandThread[] threads = new CommandThread[5]; //I don't expect more than five...if so I can increase this number
            boolean legit = true; //this is a check for if a legal command in either exec or the created ones
           String[] parser = new String[10]; //initiate a parser for when there are & signs
           int length = 0; //initiate in order to see the true length of the parsers
            while (true) {    //loop until exit is called
                System.out.print("shell>");  //default on screen
                commandLine = in.readLine(); //reads the input
                commandLine = commandLine.trim(); //removes the spaces
                try
                   {
                    if (commandLine.contains ("&"))
                    {
                        Scanner parse = new Scanner (commandLine); //adds the input from commandLine to a scanner
                        parse.useDelimiter("&"); //and split it by the & signs
                        parser = new String[10]; //if for some reason the command is long by 9 & signs
                        int i = 0;
                        while(parse.hasNext())
                        {
                            parser[i] = parse.next(); //adds each seperate part to the array
                            i++;
                        }
                        length = i; //store the length of the array
                        commandLine = parser[0].trim(); //returns to the normal command line.  Acting as the main thread
                        for (int x = 1; x<length; x++)//adds everything not in the main thread to different threads
                        {
                            threads [x-1] = new CommandThread(rt,parser[x].trim()); 
                        }
                        for (int x = 1; x<length; x++)//starts the threads
                        {
                            threads [x-1].start();
                        }
                    }
                    
                    if (commandLine.contains(">")) //new command to writing to a file
                    {
                        Scanner parse = new Scanner (commandLine);
                        parse.useDelimiter(">");
                        parser = new String[2]; //you should only be writing to one file...
                        try
                        {
                            parser[0] = parse.next();
                            parser[1] = parse.next(); //never overshoots when parsing with >.  Only before and after count
                            
                            Process pro = rt.exec("/bin/"+parser[0]);
                            InputStream is = pro.getInputStream();
                            InputStream err = pro.getErrorStream();
                            try
                            {
                                pro.waitFor();
                            }
                            catch (InterruptedException intex) 
                            {
                                intex.printStackTrace();
                                legit = false;
                            }
                            
                            Scanner inScan = new Scanner (is);
                            Scanner errScan = new Scanner (err);
                            String filler = ""; //creates a string representation in order to add to a file
                            while(inScan.hasNext())
                            {
                                filler = filler + '\n' + inScan.nextLine();
                            }
                            while (errScan.hasNext())
                            {
                                filler = filler + '\n' + "E:  " +errScan.nextLine();
                            }
                            PrintStream print = new PrintStream(parser[1]+".txt"); //creates a text file
                            print.println(filler);//and adds it in
                            legit = true;
                        }
                        catch (NullPointerException n) //catches null Pointers
                        {
                            legit = false;
                            System.out.println("Incorrect statement, please try again");
                        }
                    }
                    else //normal printing
                    {    
                        Process pro = rt.exec("/bin/"+commandLine);
                        InputStream is = pro.getInputStream();
                        InputStream err = pro.getErrorStream();
                        try
                        {
                            pro.waitFor();
                        }
                        catch (InterruptedException intex) 
                        {
                            intex.printStackTrace();
                            legit = false;
                        }
                        Scanner inScan = new Scanner (is); //helps get string represention of output
                        Scanner errScan = new Scanner (err);
                        //Print output
                        while(inScan.hasNext())
                        {
                            System.out.println(inScan.nextLine());
                        }
                    
                        while (errScan.hasNext())
                        {
                            System.out.print("E: ");
                            System.out.println(errScan.nextLine());
                        }
                    
                        legit = true;
                        for(int e = 1; e < length; e++)
                        {
                            threads [e-1].join();
                        }
                        length= 0; //reset the value
                        parser = null; //resets the parser every time used
                        }
                    }
                catch (Exception e)
                {
                    legit = false; //not a correct command
                    //e.printStackTrace();
                }
                if(commandLine.equals(""))
                {
                    legit = true;
                    continue;
                }
                else if (commandLine.equals("help"))
                {
                    System.out.println();
                    System.out.println("------------------------------------------------------");
                    System.out.println("This is a help command");
                    System.out.println("Use normal command line commands here");
                    System.out.println("Type 'exit' to exit the program");
                    System.out.println("------------------------------------------------------");
                    System.out.println();
                    legit = true;
                }
                else if (commandLine.equals("exit"))
                {
                System.out.println("...Terminating the Virtual Machine");
                System.out.println("...Done");
                System.out.println("Please Close manually with Options > Close");
                System.exit(0);
                }
                else if (commandLine.equals("print"))
                {
                    PrintStream print = new PrintStream("print");
                }
                else if (!legit)
                {
                    System.out.println("Bad command: " + commandLine);
                }
            }  
        } catch (IOException e) {  
           //e.printStackTrace(); // Error checking
        }  
    }  
  

    public static class CommandThread extends Thread //creates Threads
    {
        private String execute;
        private Runtime run;
        public CommandThread (Runtime rt, String exec) //save the runtime and the item to execute (as a string)
        {
            execute = exec;
            run = rt;
        }
        public void run()
        {
              try //literally has to do the same as the normal thread.  Only difference is that the normal thread tracks good commands
              {
                  if (execute.contains(">"))
                    {
                        Scanner parse = new Scanner (execute);
                        parse.useDelimiter(">");
                        String[] parser = new String[2];
                        try
                        {
                            parser[0] = parse.next();
                            parser[1] = parse.next();
                            
                            Process pro = run.exec("/bin/"+parser[0]);
                            InputStream is = pro.getInputStream();
                            InputStream err = pro.getErrorStream();
                            try
                            {
                                pro.waitFor();
                            }
                            catch (InterruptedException intex) 
                            {
                                intex.printStackTrace();
                            }
                            
                            Scanner inScan = new Scanner (is);
                            Scanner errScan = new Scanner (err);
                            String filler = "";
                            while(inScan.hasNext())
                            {
                                filler = filler + '\n' + inScan.nextLine();
                            }
                            while (errScan.hasNext())
                            {
                                filler = filler + '\n' + "E:  " +errScan.nextLine();
                            }
                            PrintStream print = new PrintStream(parser[1]+".txt");
                            print.println(filler);
                        }
                        catch (NullPointerException n)
                        {
                            
                        }
                    }
                else{
                Process p = run.exec("/bin/"+execute); //never has to parse & signs, just >
                
                InputStream is = p.getInputStream();
                    InputStream err = p.getErrorStream();
                try
                    {
                        p.waitFor();
                    }
                catch (InterruptedException intex) 
                    {
                        intex.printStackTrace();
                    }
                Scanner inScan = new Scanner (is);
                Scanner errScan = new Scanner (err);
                while(inScan.hasNext())
                    {
                    System.out.println(inScan.nextLine());
                    }  
                while (errScan.hasNext())
                    {
                        System.out.print("E: ");
                        System.out.println(errScan.nextLine());
                    }
                }
              }
              catch(Exception e)
              {
                  
              }
        }
    }
 }
