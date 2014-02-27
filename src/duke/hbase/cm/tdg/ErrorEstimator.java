package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;



public class ErrorEstimator {
	
	public ArrayList<Double> computeEstimateLatency(String trainData, String testData, String output){
		//use costModel to compute the estimated latency of test data
		String cmPath = System.getenv("PROJECT_HOME") + "/cm-1.2/bin";
		try {

            String[] command = {System.getenv("PROJECT_HOME") +"/cm-1.2/bin/cm",
            		   "--train_file="+trainData,"--test_file="+testData,"--output="+output};
   
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
            } catch (IOException e) {
                 e.printStackTrace();
           } 
		return this.getEstimateLatency(output);
	}
	private ArrayList<Double> getEstimateLatency(String fileName){
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
			System.out.println("The measured data length Not equal to the estimated data length");
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

	public static void main(String[] args){

		String workDir = System.getenv("PROJECT_HOME")+"/workdir";
		String rowData = workDir + "/scanTD.txt";
		String trainData = workDir + "/scanTrain.txt";
		String testData = workDir + "/scanTest.txt";
		String estimateData = workDir + "/scanEstimate.txt";
		
		rowData = workDir + "/scanData.txt";
		
		TrainTestSpliter  spliter = new TrainTestSpliter(); 
		ArrayList<Double> measureLatency = spliter.split(rowData,0.8,trainData,testData);	
		
		ErrorEstimator es = new ErrorEstimator();
		ArrayList<Double>  estimateLatency =es.computeEstimateLatency(trainData,testData,estimateData);
		

//		double mse = es.MeanSquaureError(measureLatency, estimateLatency);
//		System.out.println("The mean square error = " + mse);	
    }
}
