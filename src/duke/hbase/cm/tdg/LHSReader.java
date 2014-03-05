package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LHSReader {
	private static final String seperator = ",";  
	public String[] readKthLine(String lhsFile,  int k) {   
        BufferedReader br = null;
	    String line = "";    
	    try {		   			

		   br = new BufferedReader(new FileReader(lhsFile));
		   int lineNumber = 1;
		   while ((line = br.readLine()) != null) {
			  if (k == lineNumber){
				  break;
			  }
			  lineNumber++;
		   }
	    } catch (FileNotFoundException e) {
		     e.printStackTrace();
	    } catch (IOException e) {
		     e.printStackTrace();
	    } finally {
		     if (br != null) {
		     try {
				 br.close();
			  } catch (IOException e) {
				e.printStackTrace();
			  }
	     }
	   }
	   String[] s = line.split(seperator);
	   return s;
	}
	public int getTotalNumRows(String inputFile){
		//get the total number of lines in the inputFile
		BufferedReader br  = null;
		int totalNumOfLines = 0;
		try {		   			
            br = new BufferedReader(new FileReader(inputFile));
 			while ( br.readLine() != null) {
 				totalNumOfLines++;		
 			}
		} catch (IOException e){
			e.printStackTrace();
		} finally {
 			if (br != null) {
 				try {
 					br.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			}
 		}	
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Total number of rows in " + inputFile + " is " + totalNumOfLines);
		return totalNumOfLines;		
	}
}


