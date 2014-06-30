import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Date;
/**
 * This program uses the command line to do matrix multiplication.  The first operator tells us what type of threading
 * we'll be using.  The second is how many matrices we're multiplying and the third is the size of the marices
 * 
 * @author DJ Maguddayao
 * @version 3.6.2014
 */
class MatrixMultiplier
{
    private static String unthreaded = "U";
    private static String rowThread = "R";
    private static String everyElementThread = "E";
    private static String threadState;//keeps track of which thread method is used
    
    private static long startTime;
    private static long endTime;
    
    private static squareMatrix resultMatrix; //final result
    private static squareMatrix extraMatrix1; //helper matrix for multiplying
    private static squareMatrix extraMatrix2; //this one always stays the same.
    
    private static int multipliesDone; //keeps tracks of how many times something has been multiplied.  Useful for looping
     public static void main (String[] args){
        try
        {
            BufferedReader in = new BufferedReader
               (new InputStreamReader(System.in));  //makes it so that anything typed to the system will be read
            String commandLine;
            String [] parser = new String[3];
            
            //Initial text that appears on screen to help explain what this program does
            System.out.println("This is a program to help with matrix multiplication of n by n matrices");
            System.out.println("");
            System.out.println("For the first input, choose 'U' for unthreaded, 'R' for threaded by row, or 'E' for thread by each element");
            System.out.println("");
            System.out.println("The last two inputs will be the number of matrices multipled and the size of the matrices");
            System.out.println("");
            System.out.println("To finish the program, type 'exit'");
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println("");
            
            while (true)
            { //keeps running until told to stop
                
                System.out.print("Input: ");
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
                   Scanner parse = new Scanner (commandLine);
                   parse.useDelimiter(" "); //this delimter works to grab items that are spaced apart
                   try{
                    //only reads the first three inputs
                    parser[0] = parse.next();
                    parser[1] = parse.next();
                    parser[2] = parse.next();
                   }
                   catch(Exception n) //it's actually a NoSuchElementException...but Java didn't like it when I used it
                   {
                       System.out.println("Not enough inputs, please try again");
                   }
                   
                   if (parser[0] != null && parser[1] != null && parser[2] != null) //if the error wasn't caught
                       {
                        threadState = ""; //starts the thread off with nothing
                        try
                        {
                             startTime = System.currentTimeMillis();//begins timer
                            
                            threadState = parser[0];//threadstate is always a string.
                        
                            int numOfMatrices = Integer.parseInt(parser[1]);//exception at the end catch if these two aren't integers
                            int numOfRows = Integer.parseInt(parser[2]);
                            
                            extraMatrix1= new squareMatrix(numOfRows); //creates the matrices
                            extraMatrix1.autoFill();
                            extraMatrix2= new squareMatrix(numOfRows);
                            extraMatrix2.autoFill(); //autofill utilizes a method in the squareMatrix class to help fill it out depending on how many rows/columns there are
              
                            if(threadState.equals(unthreaded))
                            {
                                multipliesDone= 1; //assume we have on multiply done
                                if (numOfMatrices == 1)
                                {
                                    resultMatrix = extraMatrix1;
                                }
                                
                                //create matrix multiplication normally
                                
                                else
                                {
                                    while (multipliesDone < numOfMatrices) //check if we're still multiplying
                                    {
                                        resultMatrix = new squareMatrix(numOfRows); //For some reason, I can't just refill the result matrix with zeroes because it affects extraMatrix1
                                        for (int i = 0; i < numOfRows; i++)  //for each row
                                        {
                                            for (int j = 0; j < numOfRows; j++) //for each column
                                            {
                                                for (int k = 0; k < numOfRows; k++) //utilize theorem EMP
                                                {
                                                    resultMatrix.fillElement(i,j, (resultMatrix.returnElement(i,j) + extraMatrix1.returnElement(i,k) * extraMatrix2.returnElement(k,j)));
                                                }
                                            }
                                        }
                                        
                                        multipliesDone++; //increase the number of times we've multiplied
                                        extraMatrix1 = resultMatrix; //set extraMatrix to be the resulting matrix if we're not done multiplying
                                   }
                                }
                                
                                 endTime = System.currentTimeMillis(); //end the timer
                                System.out.println("Time take to complete: " + (endTime-startTime) + "ms"); //state the time taken in milliseconds
                                resultMatrix.printMatrix(); //prints the Matrix using the method in squareMatrix
                            }
                       
                            else if(threadState.equals(rowThread))
                            {                                
                                RowThread[] allRows = new RowThread[numOfRows]; //create an array to store the threads.  Needed for every thread there is (which is equal to how many rows there are)
                                if (numOfMatrices == 1)
                                {
                                    resultMatrix = extraMatrix1;
                                }
                                else
                                {
                                    try{ //needed a try catch block for .join()'s catch of InterruptedException
                                        multipliesDone = 1;
                                        while (multipliesDone < numOfMatrices) //same as before, as long as we're still multiplying
                                        {
                                            resultMatrix = new squareMatrix(numOfRows); //always initialize this so that extraMatrix1 doesn't get re-writting incorrectly
                                            
                                            for (int num = 0; num < numOfRows; num++) //initialize every thread needed
                                            {
                                                //Thread at allRows[0] handles row 0, at allRows[1] handles row 1, etc.
                                                allRows[num] = new RowThread(num, numOfRows, extraMatrix1, extraMatrix2, resultMatrix);
                                            }
                                        
                                            for (int startNum = 0; startNum < numOfRows; startNum++) //needed to start all of the threads
                                            {
                                                allRows[startNum].start();
                                            }
                                        
                                            for (int endNum = 0; endNum < (numOfRows); endNum++) //join all the threads at the end
                                            {
                                                allRows[endNum].join();
                                            }
                                            multipliesDone++;
                                            extraMatrix1= resultMatrix;
                                        }
                                    }
                                    catch(InterruptedException ie)
                                    {
                                    }
                                     endTime = System.currentTimeMillis(); //same in the unthreaded method
                                    System.out.println("Time take to complete: " + (endTime-startTime) + "ms");
                                    resultMatrix.printMatrix();
                                }
                            }
                       
                            else if(threadState.equals(everyElementThread)) //threading by element
                            {
                                ElementThread[] allElement = new ElementThread[(numOfRows*numOfRows)]; //create a thread for every element.  It will need an array row*row long
                                
                                if (numOfMatrices == 1)
                                {
                                    resultMatrix = extraMatrix1;
                                }
                                else
                                {
                                    try{
                                        multipliesDone = 1;
                                        while (multipliesDone < numOfMatrices)
                                            {
                                                resultMatrix = new squareMatrix(numOfRows);
                                                for (int num = 0; num < (numOfRows * numOfRows); num++)
                                                {
                                                //i = num/numofRows, j = num%numofRows.  So if #rows = 2.  allElement[0] is (0,0), allElements[1] is (0,1), allElements[2] is (1,0), and allElements[3] is (1,1)
                                                allElement[num] = new ElementThread(num/numOfRows, num%numOfRows, numOfRows, extraMatrix1, extraMatrix2,resultMatrix);
                                            }
                                    
                                            for (int startNum = 0; startNum < (numOfRows * numOfRows); startNum++) //starts threads
                                            {
                                                allElement[startNum].start();
                                            }
                                    
                                            for (int endNum = 0; endNum < (numOfRows * numOfRows); endNum++)//ends threads
                                            {
                                                allElement[endNum].join();
                                            }
                                            multipliesDone++;
                                            extraMatrix1 = resultMatrix;
                                        }
                                   }
                                   catch(InterruptedException ie)
                                   {
                                   }
                                    endTime = System.currentTimeMillis();
                                   System.out.println("Time take to complete: " + (endTime-startTime)+ "ms");
                                   resultMatrix.printMatrix();
                                }
                            }
                            else //first element wasn't valid
                            {
                                System.out.println("Input was not valid");
                                System.out.println("");
                            }
                            
                        }
                        catch(NumberFormatException n)//if a number wasn't caught
                        {
                            System.out.println("Incorrect statement, please try again");
                        }
                    }
                } 
                parser[0] = null;
                parser[1] = null;
                parser[2] = null;
            }
        }
        catch(IOException e)
        {
        }
    }
    
    /**
     * Creates a matrix that is squared (n by n).  Has an autofill method to do the dirty work
     */
    private static class squareMatrix
    {
        private float[][] twoDMatrix; //matrix
        private int filler; //used for autofill
        private int size;
        private int currentValue;
        
        public squareMatrix (int n)
        {
            size = n;
            twoDMatrix = new float[size][size];
        }
        
        /**
         * Fills the matrix with having everything diagonal heading down right
         */
        public void autoFill()
        {
                for(int row = 0; row < size; row++)
                {
                   filler = -1; //defaults to subtracting to filling
                   
                   currentValue = row+1; //So that 0,0 will have the number "1" instead of 0
                   for(int column = 0; column < size; column++)
                    {
                        twoDMatrix[row][column] = currentValue;
                        if(currentValue == 1) //when we hit the minimum amount (1), we resort to increasing
                        {
                            filler = 1;
                        }
                        currentValue = currentValue + filler; //increments the current Value
                    }
                }
        }
        
        /**
         * Used for testing to see the matrix
         */
        public void printMatrix() 
        {
            for (int row =0; row < size; row++)
            {
                for(int column = 0; column < size; column++)
                {
                    System.out.print("    " + twoDMatrix[row][column]);
                }
                System.out.println("");
            }
        }        
        
        /**
         * Helps with filling an element in
         */
        public void fillElement(int i, int j, float filler)
        {
            twoDMatrix[i][j]= filler;
        }
       
        /**
         * Grabs an element at i,j in the matrix
         */
        public float returnElement(int i, int j)
        {
            return twoDMatrix[i][j];
        }
    }
    
    /**
     * Create a thread for each row
     */
    private static class RowThread extends Thread
    {
        private int row; //the rowNumber this thread refers to
        private int noOfColumns; //literally how many columns there are
        
        private squareMatrix matrix1;//grabs matrix to multiply with
        private squareMatrix matrix2;
        private squareMatrix resultMatrix; //where the results end up in 
        
        private float[] rowResult; //holds the results when waiting for the synchronized object
        
        public RowThread(int rowNumber, int numOfColumns, squareMatrix m1, squareMatrix m2, squareMatrix resultingMatrix)
        {
            row = rowNumber;
            noOfColumns = numOfColumns;
            rowResult = new float[numOfColumns];
            matrix1 = m1;
            matrix2 = m2;
            resultMatrix = resultingMatrix;
        }
        
        //create a program that will evaluate rows
        public void run()
        {
            try
            {
              for (int j = 0; j < noOfColumns; j++) //the number of columns
                {
                  for (int k = 0; k < noOfColumns; k++) //theorem EMP again
                     {
                       rowResult[j] = rowResult[j] + matrix1.returnElement(row,k) *matrix2.returnElement(k,j);
                     }
                     
                  synchronized(resultMatrix) //we synchronize on the resultMatrix so that the grabs for resultMatrix aren't out of order...this actually slows down the program though
                  {
                    for(int column = 0; column< noOfColumns; column++) //j is already taken. so we'll use this
                    {
                        resultMatrix.fillElement(row, column, rowResult[column]);
                    }
                    resultMatrix.notifyAll(); //notify every other thread that is trying to access this
                  }
                }
            }
            catch (ArithmeticException e) 
            {
                
            }
        }
    }
    
    /**
     * Creates threads for each element to thread
     */
    private static class ElementThread extends Thread
    {
        private int row; //row the element is in
        private int column; //column the element is in
        
        private int numOfRows; //how many rows/columns there are
        
        private float number; //stores the float calcuated
        private squareMatrix matrix1;
        private squareMatrix matrix2;
        private squareMatrix resultMatrix;
        
        public ElementThread(int rowNumber, int columnNumber, int numberOfRows, squareMatrix m1, squareMatrix m2, squareMatrix resultingMatrix)
        {
            row = rowNumber;
            column = columnNumber;
            numOfRows = numberOfRows;
            number = 0; //initial value = 0;
            matrix1 = m1;
            matrix2 = m2;
            resultMatrix = resultingMatrix;
        }
        public void run()
        {
            try
            {
              for (int k = 0; k < numOfRows; k++) //Theorem EMP
              {
                number = number + matrix1.returnElement(row,k) * matrix2.returnElement(k,column);
              }
              
              synchronized(resultMatrix)
              {
                  resultMatrix.fillElement(row, column, number);
                  
                  resultMatrix.notifyAll();
              }
            }
            catch (ArithmeticException e)
            {
                
            }
        }
    }   
    
}
