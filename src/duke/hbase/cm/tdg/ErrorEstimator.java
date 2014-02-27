package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//read training data from scanTD.txt, 
//split it into train_data.txt and test_data.txt for cost model
//call cost model, to compute the estimetated_latency
//compute the mean square error 
public class ErrorEstimator {
	
	public int numOfLines(String inputFile){
		//compute the total number of lines in the inputFile
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
	
    public List<Integer> kOutofN(int k, int N){
    	//return k distinct random number from 1 to N
		List<Integer> index = new ArrayList<Integer>();
		for(int i=0;i<N;i++){
			index.add(i+1);
		}
		Collections.shuffle(index);			 
		List<Integer> resList = index.subList(0, k);
//		for(int a:index)
//		  System.out.print(a + ", ");
//	    System.out.println("");
//	 	for(int a:resList)
//			 System.out.print(a + ", ");
//	 	System.out.println("");
		return resList;	
	}
	
	public ArrayList<Double> splitFile(String inputFile, double percent){
		    //randomly split input file into percent : (1-percent) 
		
		    //store the measured latency of test data
			ArrayList<Double> testMeasuredLatency = new ArrayList<Double>();
			//file reader and writer 
			BufferedReader br  = null;
			FileWriter tdWriter = null;
 			FileWriter testWriter = null;	
			String line = "";
		    String seperator = "\t";  
 		try {		   			
            br = new BufferedReader(new FileReader(inputFile));          
            //the total number of lines in inputFile
            int numOfLines = this.numOfLines(inputFile);
            int trainLines = (int) Math.floor(percent * numOfLines);
 			//generate a list containing percent * numOfLines distinct random number            
 			List<Integer> trainDataIndex = kOutofN(trainLines,numOfLines);
 			
 			System.out.println(trainLines + " out of " +
 			        numOfLines + " are selected as the training data.");         
            tdWriter = new FileWriter("scan_train_data.txt");
            testWriter = new FileWriter("scan_test_data.txt"); 	
            int lineNumber = 1;
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
	public ArrayList<Double> getEstimateLatency(String fileName){
		ArrayList<Double> testEstimateLatency = new ArrayList<Double>();
		BufferedReader br  = null;
		String line = null;
		try {		   			
            br = new BufferedReader(new FileReader(fileName));           
 			while ((line = br.readLine()) != null) {
 				testEstimateLatency.add(Double.valueOf(line));		
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
		return testEstimateLatency;
	}
			
	public double MeanSquaureError(ArrayList<Double> measure, ArrayList<Double> estimate){
		//compute mean square error of two list
		double mse = 0.;
		if(measure.size() != estimate.size()){
			System.out.println("Measured data length Not equal to estimate data length");
			return 0;
		}
		Iterator<Double> it_measure = measure.iterator();
		Iterator<Double> it_estimate = estimate.iterator();
		while(it_measure.hasNext()){
			double measureLatency = it_measure.next();
			double estimateLatency = it_estimate.next();
			mse = mse + Math.pow((measureLatency - estimateLatency),2);
		}
		return mse;		
	}
	
	public void computeEstimateLatency(String trainData, String testData, String output){
		//use costModel to compute the estimated latency of test data
		String cmPath = System.getenv("PROJECT_HOME") + "/cm-1.2/bin";
                System.out.println(cmPath);		
		try {
            System.out.println("-------------------------------------------");                      
            System.out.println();
            System.out.println("-------------------------------------------");
            String[] command = {System.getenv("PROJECT_HOME") +"/cm-1.2/bin/cm",
            		            "--train_file="+trainData,
            		            "--test_file="+testData,
            		            "--output="+output};
   
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(cmPath));   
            pb.redirectErrorStream(true);
            Process p;
            p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null){
                 System.out.println(line);
            }
            System.out.println("Cost Model executed.");
            System.out.println("-------------------------------------------");
            } catch (IOException e) {
                 e.printStackTrace();
           } 
	}

	public static void main(String[] args){
		ErrorEstimator es = new ErrorEstimator();
		String workDir = System.getenv("PROJECT_HOME");
		String trainingData = workDir + "/scanTD.txt";
		String testData = workDir + "/scanTest.txt";
		String estimateData = workDir + "/scanEstimate.txt";
		
		ArrayList<Double> measureLatency = es.splitFile(trainingData,0.7);	
		es.computeEstimateLatency(trainingData,testData,estimateData);
		ArrayList<Double>  estimateLatency = es.getEstimateLatency(estimateData);
		double mse = es.MeanSquaureError(measureLatency, estimateLatency);
		System.out.println("The mean square error = " + mse);
		
    }
		

}
