package duke.hbase.cm.tdg;

public class TDMain {
	private String trainLHS =  System.getenv("PROJECT_HOME") + "/workdir/lhs_train.csv";
	private String testLHS = System.getenv("PROJECT_HOME") + "/workdir/lhs_test.csv";
	public void prepareTables(){
		 //prepper tables using join schema
	      JoinQTDGenerator trainGen = new  JoinQTDGenerator("train","join");   
	      JoinQTDGenerator testGen = new  JoinQTDGenerator("test","join"); 
	      
	      trainGen.parseLHSFile(trainLHS);
	      trainGen.prepareTables();

	      testGen.parseLHSFile(testLHS);
	      testGen.prepareTables(); 	
	}
	public void genJoinQTD(){
		//generate training data
	      JoinQTDGenerator joinTrainGen = new  JoinQTDGenerator("train","join");   
	      JoinQTDGenerator joinTestGen = new  JoinQTDGenerator("test","join"); 	      
	      genQTD("join",joinTrainGen,joinTestGen);
	}
	public void genScanQTD(){

	      ScanQTDGenerator scanTrainGen = new  ScanQTDGenerator("train","scan");   
	      ScanQTDGenerator scanTestGen = new  ScanQTDGenerator("test","scan"); 
	      genQTD("scan",scanTrainGen,scanTestGen);

	}
	private void genQTD(String queryName, TDGenerator trainGen, TDGenerator testGen){           
	      trainGen.parseLHSFile(trainLHS);
	      trainGen.generateTrainData();
	      testGen.parseLHSFile(testLHS);
	      testGen.generateTestData();
	      testGen.getStat();  
		
	}

	public static void main(String[] args){
		
		  long startTime = System.currentTimeMillis();  	      
//	      String trainLHS = System.getenv("PROJECT_HOME") + "/workdir/lhs_train.csv"; 		 
//	      String testLHS = System.getenv("PROJECT_HOME") + "/workdir/lhs_test.csv";
	      
	      TDMain td = new TDMain();
	      
	      td.prepareTables();
	      td.genScanQTD();
	      td.genJoinQTD();
	                    
	      long endTime = System.currentTimeMillis();
	      System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");   
	      System.exit(0);
	}

}
