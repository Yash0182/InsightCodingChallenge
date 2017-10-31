jar file "Insight.jar" is under the folder src. To call the jar file, run the shell script "run.sh" placed at the same directory level as of src.

There are two classes in the file. One class is the main execution class and the other one is the class for finding the Median ("MedianFinder").

There is one more class I have defined which is implementing the Comparator interface in order to Sort the results for the medianDate.

I have created two separate functions to calculate both "medianDate()" and "medianZIP()".

There is a function "readFromFile()" which is called from the main class, which is responsible for reading the file and calling te other two functions "medianDate()" and "medianZIP()".

To print the result to the file, "writeToFile()" function is defined.

Assumptions :

If any transaction is below $0.5, the transaction is counted and it will also display in the file, but as we need to round the integers to the nearest decimals, so their sum is displayed as $0.
