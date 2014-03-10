package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TrainDataSpliter {
	public void splitTrainData(String trainData, String testData, String testReal){
		String seperator = "\t";  
		BufferedReader br  = null;
		FileWriter td = null;
	    FileWriter tr = null;	    
		try {		   			
	        br = new BufferedReader(new FileReader(trainData));   
            td = new FileWriter(testData);
            tr = new FileWriter(testReal); 
		    String line = "";	  
			while ((line = br.readLine()) != null) {							
			        String[] s = line.split(seperator);
			        //latency goes to testReal
					tr.append(s[0] + "\n");					
					//others goes to testData
					StringBuilder builder = new StringBuilder();
					for(int i=1; i<s.length;i++){
					   builder.append(s[i]);
					   if(i<s.length-1)
						   builder.append(seperator);				
					}
					builder.append("\n");
					td.append(builder.toString());
					td.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				    td.close();
					tr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}		
}
