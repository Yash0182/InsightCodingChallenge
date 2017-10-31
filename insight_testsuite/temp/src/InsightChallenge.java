import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;



public class InsightChallenge {
    
    static HashMap<String,ArrayList> hashZIP = new HashMap();
    static SortedMap<String,ArrayList> hashDate = new TreeMap();
    
    static boolean flagMedianDate=false;
    static SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");           //for String to Date conversion
    static String outputFileDate = "";
    
    //File Read Function Line by Line
    static void readFromFile(File inputFile, String outputFilePath)
    {
        try 
            {
            
            File fileName = inputFile;
            
            FileInputStream fis = new FileInputStream(fileName);                
            
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            
            while ((line = br.readLine()) != null) 
                {
                        
                        medianZIP(line,outputFilePath);
                        medianDate(line,outputFilePath);
                }
            br.close();
            
                if(flagMedianDate)                                              //this is for writing to the output date file, as we wanted to write all the records only once.
                {
                    ArrayList temp_List= new ArrayList();
                    for(String hashKey:hashDate.keySet())                       //get all the keys
                    {
                        temp_List.add(hashDate.get(hashKey));                   //temporary list which is adding all the arraylist from the hash map in order to iterate over so to sort based on the C-ID and the Date
                        
                    }
                                Collections.sort(temp_List,dateCIDSort);

                            
                       for(Object copyTempListObject:temp_List)
                       {
                        ArrayList copyTempList = (ArrayList)copyTempListObject;
                        String outputLine = copyTempList.get(0)+"|"+copyTempList.get(1)+"|"+copyTempList.get(2)+"|"+copyTempList.get(3)+"|"+copyTempList.get(4);    //preparing to write to the line
                        writeToFile(outputLine,outputFileDate);                               //function call to append to the file
                       }
                    flagMedianDate=false;
                  
                }
           } 
        catch(Exception e)
            {
                
                e.printStackTrace();
                
            }
    }
    
    
    
    
            static void writeToFile(String outputLine, String givenFileName)
            {
               
                try 
                {
                //String filename= "c:/Users/vaio1/Desktop/yashtest.txt";
                String filename= givenFileName;
                FileWriter fwOutput = new FileWriter(filename,true);    //the true will append the new data
                     
                fwOutput.write(outputLine);                             //appends the string to the file
                fwOutput.write("\r\n");
                fwOutput.close();
                } 
                catch(Exception e)
                {
                    
                    System.out.println(e.getStackTrace());
                }
                
            }
    
           //logic to sort the date output first in the c_id order and than the datewise 
           static Comparator<ArrayList> dateCIDSort = new Comparator<ArrayList>()
            {
                
                    @Override
                            public int compare(ArrayList s1, ArrayList s2) {
                                
                                    String x1 = (String)s1.get(0);              
                                    String x2 = (String)s2.get(0);
                                    int sComp = x1.compareTo(x2);               //comparing if the c_id's are unequal, then directly return'
                                    try
                                    {
                                    if (sComp != 0) {
                                        
                                       return sComp;
                                    } else {                                    //if the c_id's are equal then compare the dates which is the second index of the array list
                                        
                                        Date d1 = sdf.parse((String)s1.get(1));
                                        Date d2 = sdf.parse((String)s2.get(1));
                                        
                                       return d1.compareTo(d2);
                                    }
                                    }catch(Exception e)
                                    {
                                        
                                        System.out.println(e.getStackTrace());}
                                    return 0;
                            }
 
            };
            
         
            static void medianZIP(String line,String outputFilePath)
            {
                String outputFile = outputFilePath+"medianvals_by_zip.txt";
                String[] differentColumns = line.split("\\|");
                
                if(differentColumns.length<16)
                {return ;}
                if(!differentColumns[15].isEmpty() || differentColumns[0].isEmpty() || differentColumns[14].isEmpty())                  //if othetID is not empty or if amount is empty or if c_id is empty
                    {return ;}
                if(differentColumns[10].length()<5)            //if zipcode is fewer than 5 digit
                    {return ;}
                differentColumns[10]=differentColumns[10].substring(0,5);      //only have to consider first 5 digits
                String hashKey = differentColumns[0].concat(differentColumns[10]);
                if(!hashZIP.containsKey(hashKey))               //Cheking for C-ID and ZIP COde combination
                    {
                    
                     MedianFinder medianFinder = new MedianFinder();  
                     
                     
                     medianFinder.addNum(Float.parseFloat(differentColumns[14]));
                     
                     ArrayList insertIntoHash = new ArrayList();
                     insertIntoHash.add(differentColumns[0]);                  //adding CID to the arraylist
                     insertIntoHash.add(differentColumns[10]);                  //adding ZIP CODE to the arraylist
                     insertIntoHash.add(medianFinder.findMedian());          //adding Median
                     
                     insertIntoHash.add(1);                                 //Adding transaction
                     insertIntoHash.add(differentColumns[14]);              //Adding sum
                     insertIntoHash.add(medianFinder);                      //adding Median Finder Object of this particular hash_ZIP combination to use later
                     
                     hashZIP.put(hashKey,insertIntoHash);
                     //preparing a string to append to the file
                     String outputLine = differentColumns[0]+"|"+differentColumns[10]+"|"+insertIntoHash.get(2)+"|"+insertIntoHash.get(3)+"|"+insertIntoHash.get(4);    
                     writeToFile(outputLine,outputFile);                               //function call to append to the file
                    }
                else if(hashZIP.containsKey(hashKey))
                    {
                     
                     ArrayList temp_List = hashZIP.get(hashKey);
                     
                     MedianFinder medianFinderFromList = (MedianFinder)temp_List.get(5);
                     
                     medianFinderFromList.addNum(Float.parseFloat(differentColumns[14]));
                     
                     int new_Sum = Math.round(Float.parseFloat(differentColumns[14])+Float.parseFloat(String.valueOf(temp_List.get(4))));
                     
                     temp_List.set(2,medianFinderFromList.findMedian());        //Caling medianFinder and storing new median in hash map
                     temp_List.set(3,(int)(temp_List.get(3))+1);                //Add 1 to the transaction
                     temp_List.set(4,new_Sum);                                  //Add Sum
                     String outputLine = temp_List.get(0)+"|"+temp_List.get(1)+"|"+temp_List.get(2)+"|"+temp_List.get(3)+"|"+temp_List.get(4);    
                     writeToFile(outputLine,outputFile);                               //function call to append to the file
                    }
                
                    
                    
            }
     //----------------------------------------------------------------------------------------------------       
            
            static void medianDate(String line, String outputFilePath)
            {
                outputFileDate="";
                outputFileDate = outputFilePath + "medianvals_by_date.txt" ;
                String[] differentColumns = line.split("\\|");
                
                if(!differentColumns[15].isEmpty()|| differentColumns[0].isEmpty() || differentColumns[14].isEmpty())    //checking edge cases if Amount is null              //if othetID is not empty of if amount or c_id is empty
                    {
                   return ;}
                
                if(differentColumns[13].length()==0 || differentColumns[13].length()!=8 )            //if date field is null
                    {
                    
                    return ;}
                
                String hashKey = differentColumns[0].concat(differentColumns[13]);
                if(!hashDate.containsKey(hashKey))               //Cheking for C-ID and ZIP COde combination
                    {
                     
                     MedianFinder medianFinder = new MedianFinder();  
                     
                     medianFinder.addNum(Float.parseFloat(differentColumns[14]));
                     
                     ArrayList insertIntoHash = new ArrayList();
                     insertIntoHash.add(differentColumns[0]);                  //adding C_ID to the arraylist
                     insertIntoHash.add(differentColumns[13]);                  //adding DATE to the arraylist
                     
                     insertIntoHash.add(medianFinder.findMedian());          //adding Median
                     
                     insertIntoHash.add(1);                                 //Adding transaction
                     insertIntoHash.add(differentColumns[14]);              //Adding sum
                     insertIntoHash.add(medianFinder);                      //adding Median Finder Object of this particular hash_ZIP combination to use later
                     
                     hashDate.put(hashKey,insertIntoHash);
                     
                    }
                else if(hashDate.containsKey(hashKey))
                    {
                     
                     ArrayList temp_List = hashDate.get(hashKey);
                     
                     MedianFinder medianFinderFromList = (MedianFinder)temp_List.get(5);
                     
                     medianFinderFromList.addNum(Float.parseFloat(differentColumns[14]));
                     
                     
                     int new_Sum = Math.round(Float.parseFloat(differentColumns[14])+Float.parseFloat(String.valueOf(temp_List.get(4))));
                     temp_List.set(2,medianFinderFromList.findMedian());        //Caling medianFinder and storing new median in hash map
                     temp_List.set(3,(int)(temp_List.get(3))+1);                //Add 1 to the transaction
                     temp_List.set(4,new_Sum);                                  //Add Sum
                    
                    }
                flagMedianDate = true;
               
                
            }
 
    
    public static void main(String args[])                                      //Program will start from here    
    {
        if(args.length<2)
        {System.out.println("Too few arguements");}
        else
        {
        File inputFile = new File(args[0]);                                     //input file path with the filename
        String outputFilePath = args[1];                                        //output file path
        readFromFile(inputFile,outputFilePath);                                 //this is the function which will call the two function medianZIP and the medianDATE
        }
        
    }
}


    class MedianFinder                                                          //class defined to find running median              
    {
        
        private Queue<Float> small = new PriorityQueue(),                       //declaring two queues
                            large = new PriorityQueue();

         public void addNum(float num) {                                        //function to add numbers to the Queue
            
             large.add((float) num);
          
            small.add(-large.poll());
            if (large.size() < small.size())
                large.add(-small.poll());
        }

        
         public long findMedian() {                                             // Function that calculates the median
        
            return large.size() > small.size()
        
                    ? Math.round(large.peek())
                   : Math.round(((large.peek() - small.peek()) / 2.0));
        }
    }

    

