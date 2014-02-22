package duke.hbase.cm.tdg;

import java.io.BufferedWriter;
import java.io.File;

import duke.hbase.sd.Util;

public class ScanQTDGenerator {
  
  private void generate(String string) {

  }

  public void generate() {
    System.out.println("generating training data from scan operation...");
    BufferedWriter bw = Util.getFileWriter("training_data/scan.csv");
    generate("customer");
    generate("orders");
    generate("supplier");
    generate("partsupp");
    generate("part");
    generate("lineitem");
    generate("nation");
    generate("region");
    Util.closeFileWriter(bw);
  }

  public static void main(String[] args) {
	  long startTime = System.currentTimeMillis(); 
	  
      String PROJECT_HOME = System.getenv("PROJECT_HOME"); 
      System.out.println("PROJECT_HOME = " + PROJECT_HOME);
      
      String workdir = PROJECT_HOME + "/workdir";
      System.out.println("Working directory = " + workdir);
      
      String lhsFile = PROJECT_HOME + "/src/duke/hbase/cm/tdg/LHS.csv"; 		
      System.out.println("LHS.csv directory = " + lhsFile);
   
     for(int i =1; i<2;i++){		 
          int numSchema = i ;
   
  // initialize a new schema 
          Schema schema = new Schema();
      schema.name = "schema_" + numSchema;
      schema.initializeFromLHS(lhsFile, numSchema);
          
          String dataDir = workdir + "/" + schema.name;
      HbaseTableGenerator.dataDir = dataDir; 
          // make new directory
          new File(dataDir).mkdirs();
          System.out.println("Making data directory for " + schema.name + " = " + dataDir);
  new File(dataDir + "/csvDir").mkdirs(); 
      System.out.println("Making csv directory for "  + schema.name + " = " + dataDir);
      
          //
          HbaseTableGenerator gen = new HbaseTableGenerator();
          
          gen.setSqlFile(schema.name + ".sql");
          gen.setXmlFile(schema.name + ".xml");
          gen.setcsvDir("csvDir");
          gen.setHdfsCsvDir(schema.name + "/csvDir" );
          gen.createTableInHbase(schema);
	      }
      long endTime = System.currentTimeMillis();
 System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");
  } 

}
