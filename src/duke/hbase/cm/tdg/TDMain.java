package duke.hbase.cm.tdg;


import java.util.ArrayList;

public class TDMain {
	
	private ScanQTDGenerator scanTrainGen= null;
	private ScanQTDGenerator scanTestGen= null;
	private JoinQTDGenerator joinTrainGen = null;
	private JoinQTDGenerator joinTestGen = null;
	private ReadQTDGenerator readTrainGen = null;
	private ReadQTDGenerator readTestGen = null;
	private UpsertQTDGenerator upsertTrainGen = null;
	private UpsertQTDGenerator upsertTestGen = null;
	
    public void setTestGenerators(String testLHS){
          this.scanTestGen  = new  ScanQTDGenerator(testLHS, "test_", "scan"); 	
	      this.joinTestGen  = new  JoinQTDGenerator(testLHS, "test_", "join"); 	
	      this.readTestGen  = new  ReadQTDGenerator(testLHS, "test_", "read");
	      this.upsertTestGen = new UpsertQTDGenerator(testLHS, "test_", "upsert");
    }
	public void setTrainGenerators(String trainLHS,  int n){
	      this.scanTrainGen = new  ScanQTDGenerator(trainLHS,  "train" + n + "_",   "scan_"  + n);      
	      this.joinTrainGen = new  JoinQTDGenerator(trainLHS,  "train" + n + "_",   "join_"  + n);  
	      this.readTrainGen  = new  ReadQTDGenerator(trainLHS, "train" + n + "_",   "read_"  + n);
	      this.upsertTrainGen = new UpsertQTDGenerator(trainLHS, "train" + n + "_", "upsert_" +n );
	}
	
	public void prepareTrainTables(){ 
	      this.joinTrainGen.prepareTables();
	}
	
	public void prepareTestTable(){
		 this.joinTestGen.prepareTables();
	}

	public void genTrainData(){
		this.scanTrainGen.generateTrainData();
//		this.joinTrainGen.generateTrainData();
		this.readTrainGen.generateTrainData();
//		this.upsertTrainGen.generateTrainData();      		
	}
	public void genTestData(){
		this.scanTestGen.generateTestData();
//		this.joinTestGen.generateTestData();
		this.readTestGen.generateTestData();
//		this.upsertTestGen.generateTestData();
	}

	public ArrayList<Double> genResult(){
		
	      double scanError = this.scanTrainGen.getStat();
//	      double joinError = this.joinTrainGen.getStat();
	      double readError = this.readTrainGen.getStat();
//	      double upsertError = this.upsertTrainGen.getStat();
	      
	      ArrayList<Double> relativeErrors = new ArrayList<Double>(); 
	      relativeErrors.add(scanError);
//	      relativeErrors.add(joinError);
	      relativeErrors.add(readError);
//	      relativeErrors.add(upsertError);
	      
	      return relativeErrors;
	}
	

	public static void main(String[] args){		
		  long startTime = System.currentTimeMillis();
			
	      int[] sampleSize = {20,40,60,80,100};
		      
	      String testLHS = System.getenv("PROJECT_HOME") + "/workdir/lhs_test.csv";
		  String resultFileName = System.getenv("PROJECT_HOME") + "/workdir/result.txt";
	      ResultWriter rsw = new ResultWriter(resultFileName);
	      
	      TDMain td = new TDMain();
	      td.setTestGenerators(testLHS);
	      
//	      td.prepareTestTable();
	      
	      td.genTestData();
		
		  for(int i=0;i<sampleSize.length;i++){		      
			  String trainLHS = System.getenv("PROJECT_HOME") + "/workdir/lhs_train_" + sampleSize[i]+ ".csv"; 		 		        
		      td.setTrainGenerators(trainLHS, sampleSize[i]);
		      		      		     
//		      td.prepareTrainTables();  //create all tables  
		      
		      td.genTrainData();
		      
		      //execute queries
	          ArrayList<Double> relativeErrors = td.genResult();
	          rsw.writeResult(sampleSize[i], relativeErrors);		      
		  }

	      long endTime = System.currentTimeMillis();
	      System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");   
	      System.exit(0);
	}

}
