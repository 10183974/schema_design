package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrainTestSpliter {
	public int getNumOfLines(String inputFile){
		//get the total number of lines in the inputFile
		BufferedReader br  = null;
		int numOfLines = 0;
		try {		   			
            br = new BufferedReader(new FileReader(inputFile));
            //the total number of lines in inputFile
            
 			while ( br.readLine() != null) {
 				numOfLines++;		
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
		return numOfLines;		
	}
	public ArrayList<Double> split(String inputFile, double percent, String trainData, String testData){

		ArrayList<Double> testMeasuredLatency = new ArrayList<Double>();
		//file reader and writer 
		BufferedReader br  = null;
		FileWriter tdWriter = null;
	    FileWriter testWriter = null;	
	    String seperator = "\t"; 
 
		try {		   			
         
            int totalNumOfLines = this.getNumOfLines(inputFile);
            int trainNumofLines = (int) Math.floor(percent * totalNumOfLines);        
            RandomKofN rdg = new RandomKofN();
			List<Integer> trainDataIndex = rdg.generate(trainNumofLines,totalNumOfLines);
			System.out.println(trainNumofLines + " out of " + totalNumOfLines + " are selected as the training data.");  
			
	        br = new BufferedReader(new FileReader(inputFile));   
            tdWriter = new FileWriter(trainData);
            testWriter = new FileWriter(testData); 
        
            int lineNumber = 1;
		    String line = "";
	  
			while ((line = br.readLine()) != null) {			
				if (trainDataIndex.contains(lineNumber)){
					tdWriter.append(line);
					tdWriter.append("\n");
				    tdWriter.flush();
				}
				else{
					//get each field by sperator
					String[] s = line.split(seperator);
					//first field is measured latency
					testMeasuredLatency.add(Double.valueOf(s[0]));
					//write other fields to file
					StringBuilder builder = new StringBuilder();
					for(int i=1; i<s.length;i++){
					   builder.append(s[i]);
					   if(i<s.length-1)
						   builder.append(seperator);				
					}
					builder.append("\n");
					testWriter.append(builder.toString());
					testWriter.flush();
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
				 tdWriter.close();
					 testWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return testMeasuredLatency;
      }

}
