package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Schema {
	 String name = null;
	 int numRows =  0;
	 int numColumns = 0;
	 int rowkeySize = 0;
	 int columnSize = 0;
	 int hbaseRegionSize = 0;
	 int phoenixThreadSize = 0;
     int hbaseThreadSize = 0;
     //store data in the dataDir
     String dataDir = null;
     
     public void initialSchema(String schemaName, String lhsFile,  int k) { 
    	 
 		    BufferedReader br = null;
 		    String line = "";
 		    String seperator = ",";  
 		    
    	    this.name = schemaName;
    	   	    	 
    		try {
    			
    			this.initialDataDir();
    			System.out.println("Parsing " + k + "th row of LHS data from " + lhsFile + " to schema " + this.name);
    			br = new BufferedReader(new FileReader(lhsFile));
    			int lineNumber = 1;
    			while ((line = br.readLine()) != null) {
    				if (k==lineNumber){
    					String[] s = line.split(seperator);
    					this.numRows = Integer.parseInt(s[0]);
    					this.numColumns = Integer.parseInt(s[1]);
    					this.rowkeySize = Integer.parseInt(s[2]);
    					this.columnSize = Integer.parseInt(s[3]);	
    					System.out.println("-------------------------------------------");
    					
    					System.out.println(this.name  + ":\n" + 
    					                    "\tNumber of rows = " + this.numRows + "\n" + 
    		                                "\tNumber of columns = " + this.numColumns + "\n" + 
    							            "\tRowkey size = " + this.rowkeySize + "\n" + 
    		                                "\tColumnSize = " + this.columnSize);	
    					System.out.println("-------------------------------------------");
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
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		
    	  }
     private void initialDataDir() throws IOException{
         String s = System.getenv("PROJECT_HOME") +
        		          "/" +  "workdir" +
        		           "/" + this.name;       
         // make new directory
         File dataDir = new File(s);
         if (!dataDir.exists())
         {
        	  dataDir.mkdir();
              System.out.println("Making data directory for " + this.name + " = " + dataDir);
	          new File(dataDir + "/csvDir").mkdir(); 
              System.out.println("Making csv directory for "  + this.name + " = " + dataDir);
          }
         else {
        	 throw new IOException(dataDir + " already exists.");
         }	 
     }
   
	 public static void main(String[] args){
		   String lhsFile = "/Users/Weizheng/git/schema_design/src/duke/hbase/cm/tdg/LHS.csv";
		
		   Schema schema = new Schema();
		   schema.initialSchema("z",lhsFile, 2);
		   System.out.println(schema.numRows);
	 }
}
