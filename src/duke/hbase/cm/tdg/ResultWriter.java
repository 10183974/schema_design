package duke.hbase.cm.tdg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ResultWriter {
	private String fileName = null;
    public ResultWriter(String fileName){
		this.fileName = fileName;
		File td = new File(fileName);
	    try{
	          if (td.exists()){
	        	   throw new IOException("training data file " + fileName + " already exists");
	           }
	          else{
	        	//create a new file
	        	  System.out.println("----------------------------------------------------------------------");
	        	  System.out.println("Creating new training data file = " + fileName);
	              new FileWriter(fileName);	        	  
	          }

	     } catch (IOException e){
	           e.printStackTrace();
	     }
    }
	public void writeResult(int sampleSize, ArrayList<Double> relativeErrors){
		  try {
				System.out.println("=========================================================================");
				System.out.println("Writing result to " + fileName);
				
				FileWriter writer = new FileWriter(fileName,true);//true for "append"
				writer.append(Integer.toString(sampleSize));
				for(double e:relativeErrors){
					writer.append("," + Double.toString(e));       
					
				}
				writer.append("\n");
	            writer.flush();
		        writer.close();
			} catch (IOException e) {		
				e.printStackTrace();
			}	
				
	}

}
