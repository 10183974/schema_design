package duke.hbase.cm.tdg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataWriter {
	private String fileName = null;
    public DataWriter(String fileName){
		this.fileName = fileName;
		File td = new File(fileName);
	    try{
	          if (td.exists()){
	        	   throw new IOException("Data file " + fileName + " already exists");
	           }
	          else{
	        	  //create a new file
	        	  System.out.println("----------------------------------------------------------------------");
	        	  System.out.println("Creating new data file = " + fileName);
	              new FileWriter(fileName);	        	  
	          }

	     } catch (IOException e){
	           e.printStackTrace();
	     }
    }
	public void writeResult(int sampleSize, ArrayList<Double> relativeErrors){
		  StringBuilder builder = new StringBuilder();
		  
		   builder.append(Integer.toString(sampleSize));
		   for(double e:relativeErrors){
				builder.append("," + Double.toString(e));       					
		   }
		   builder.append("\n");	   
		   this.write(builder.toString());
		  				
	}
    public void write(String line){
        try {

                 System.out.println("Writing data to " + fileName);
                 System.out.println(line);
                 System.out.println("----------------------------------------------------------------------");
                 FileWriter writer = new FileWriter(fileName,true);//true for "append"
                 writer.append(line);
                 writer.flush();
                 writer.close();
           } catch (IOException e) {
                 e.printStackTrace();
          }
    }
	

}
