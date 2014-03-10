package duke.hbase.cm.tdg;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TrainDataWriter {
	private String fileName = null;
	public TrainDataWriter(String fileName) {
		  //create a new file, if fileName not exist
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
    public void write(String line){   	
        try {
			System.out.println("----------------------------------------------------------------------");
			System.out.println("Writing training data to " + fileName);
			System.out.println(line);
			FileWriter writer = new FileWriter(fileName,true);//true for "append"
			writer.append(line);         
            writer.flush();
	        writer.close();
		} catch (IOException e) {		
			e.printStackTrace();
		}	  
    }
	
}

