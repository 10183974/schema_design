package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ErrorEstimator {
	private double mean = 0.;
	private double mse = 0;
	private static final String CM_BIN_HOME = System.getenv("PROJECT_HOME") + "/cm-1.2/bin";
	private static final String CM = CM_BIN_HOME + "/cm";
	
	private void runCM(String trainData, String testPara, String testEstimate){
		//use costModel to compute the estimated latency of test data
		String[] cmd = new String[]{CM,
				       "--train_file="+trainData,
				       "--test_file="+testPara,
				       "--output="+testEstimate};
		
		ProcessExecutor pe = new ProcessExecutor("cm-1.2", cmd,CM_BIN_HOME);
        pe.read();
        pe.stop();
	}
	
	private void compute(String testReal, String testEstimate){
		BufferedReader brReal  = null;
		BufferedReader brEstimate = null;
		
		double mseSum = 0;
		double meanSum = 0;
		int count = 0 ;
		

		try {	
            brReal = new BufferedReader(new FileReader(testReal)); 
            brEstimate = new BufferedReader(new FileReader(testEstimate));  
            String lineReal = null;
			String lineEstimate = null;

 			while ((lineReal = brReal.readLine()) != null && 
 					(lineEstimate = brEstimate.readLine()) != null) {
 				double realLatency = Double.valueOf(lineReal);
 				double estimateLatency = Double.valueOf(lineEstimate);
 			    meanSum = meanSum + realLatency;
 				mseSum = mseSum + Math.pow((realLatency-estimateLatency), 2);
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
		
		this.mse = mseSum/count;
		this.mean = meanSum/count;
		
	}
	
	public void estimate(String trainData, String testPara, String testReal, String testEstimate){		
		this.runCM(trainData,testPara,testEstimate);
		this.compute(testReal, testEstimate);
	}
	public double getMean(){
		return this.mean;
	}
	public double getSigma(){
		return Math.sqrt(this.mse);
	}
	

}
