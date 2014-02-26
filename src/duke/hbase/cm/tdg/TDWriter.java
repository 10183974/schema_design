package duke.hbase.cm.tdg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TDWriter {
	
	public TDWriter(String fileName) {
		  //create a new file, if fileName not exist
	      try{
	          if (new File(fileName).exists()){
	              System.out.println("Traning data ouput file " + fileName + " already exists." );
	              System.exit(0);
	           }
	          else {
	        	   //create a new file
	               new FileWriter(fileName);
	          }
	       } catch (IOException e){
	           e.printStackTrace();
	      }
	}
    public void write(String fileName, Schema schema, Double latency){
   	
        try {
        	
			FileWriter writer = new FileWriter(fileName,true);//true for "append"
            //(1). latency
			writer.append(Double.toString(latency) + "\t");         
            //(2). number of concurrent queries 
            writer.append(Integer.toString(1) + "\t");
			//(3).  number of server side threads	
			writer.append(Integer.toString(4) + "\t");
			//(4). number of rows 
			writer.append(Integer.toString(schema.numRows) + "\t");
			//(5). row key size
			writer.append(Integer.toString(schema.rowkeySize) + "\t");
			//(6). sum of rest of column size 
			writer.append(Integer.toString(schema.numColumns*schema.columnSize) + "\t");		
			
			writer.append("\n");
			
            writer.flush();
	        writer.close();
		} catch (IOException e) {		
			e.printStackTrace();
		}	  
  }

}
