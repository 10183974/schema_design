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
	private static final String CM_BIN_HOME = System.getenv("PROJECT_HOME") + "/cm-1.2/bin";
	private static final String CM = CM_BIN_HOME + "/cm";
	
	private void runCM(String trainData, String testData, String testEstimate){
		//use costModel to compute the estimated latency of test data
		String[] cmd = new String[]{CM,
				       "--train_file="+trainData,
				       "--test_file="+testData,
				       "--output="+testEstimate};
		
		ProcessExecutor pe = new ProcessExecutor("cm-1.2", cmd,CM_BIN_HOME);
        pe.read();
        pe.stop();
	}
	
	private double computeMSE(String testReal, String testEstimate){
		BufferedReader brReal  = null;
		BufferedReader brEstimate = null;
		
		double sum = 0;
		int count = 0 ;
		double mse =0.;

		try {	
            brReal = new BufferedReader(new FileReader(testReal)); 
            brEstimate = new BufferedReader(new FileReader(testEstimate));  
            String lineReal = null;
			String lineEstimate = null;

 			while ((lineReal = brReal.readLine()) != null && 
 					(lineEstimate = brEstimate.readLine()) != null) {
 				double realLatency = Double.valueOf(lineReal);
 				double estimateLatency = Double.valueOf(lineEstimate);
 				sum = sum + Math.pow((realLatency-estimateLatency), 2);
 				count = count + 1;
 			}
 			
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			if (brReal != null || brEstimate != null) {
 				try {
 					brReal.close();
 					brEstimate.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			}
 		}	
		
		mse = sum/count;
		return mse;
	}
	
	public double estimate(String trainData, String testData, String testReal, String testEstimate){		
		this.runCM(trainData,testData,testEstimate);
		return computeMSE(testReal,testEstimate);
	}
	
	
	public static void main(String[] args){


    }
}
