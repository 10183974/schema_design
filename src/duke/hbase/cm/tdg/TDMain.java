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
	
    public void setTestGenerators(){
    	 System.out.println("Initialize test data generators");
	      String testLHS = System.getenv("PROJECT_HOME") + "/workdir/lhs_test.csv";
    	 this.scanTestGen   = new   ScanQTDGenerator(testLHS,    "test_",  null, "scan_"); 	
	     this.joinTestGen   = new   JoinQTDGenerator(testLHS,    "test_",  null, "join_"); 	
	     this.readTestGen   = new   ReadQTDGenerator(testLHS,    "test_",  null, "read_");
	     this.upsertTestGen = new   UpsertQTDGenerator(testLHS,  "test_",  null, "upsert_");
    }	
    public void setTrainGenerators(int n){	
     	  System.out.println("Initialize train data generators for sample size = " + n);  	  
    	  String trainLHS = System.getenv("PROJECT_HOME") + "/workdir/lhs_train_" + n+ ".csv"; 	
	      this.scanTrainGen   = new  ScanQTDGenerator(trainLHS,   "train" + n + "_",   "scan_"   + n + "_",  "scan_");      
		  this.joinTrainGen   = new  JoinQTDGenerator(trainLHS,   "train" + n + "_",   "join_"   + n + "_",  "join_");  
		  this.readTrainGen   = new  ReadQTDGenerator(trainLHS,   "train" + n + "_",   "read_"   + n + "_",  "read_");
		  this.upsertTrainGen = new  UpsertQTDGenerator(trainLHS, "train" + n + "_",   "upsert_" + n + "_",  "upsert_");	            
    }

	public void prepareTrainTables(){ 
		  System.out.println("Preparing train tables");
	      this.joinTrainGen.prepareTables();
	}	
	public void prepareTestTables(){
		 System.out.println("Preparing test tables");
		 this.joinTestGen.prepareTables();
	}
	public void dropTestTables(){	
		System.out.println("Droping test tables");

		this.joinTestGen.dropTables("test");		
	}
	public void dropTrainTables(int n){
		System.out.println("Droping train tables");
		this.joinTrainGen.dropTables("train"+n);
	}
	public void genData(String tName, String qName){
		if(tName.equals("train")){
			if(qName.equals("scan") || qName.equals("all")){
				this.scanTrainGen.generateTrainData();	
			}
			if(qName.equals("join") || qName.equals("all")){
				this.joinTrainGen.generateTrainData();
			}
			if(qName.equals("read") || qName.equals("all")){
				this.readTrainGen.generateTrainData();
			}
			if(qName.equals("upsert") || qName.equals("all")){
				this.upsertTrainGen.generateTrainData();
			}		
		} else if(tName.equals("test")) {
			if(qName.equals("scan") || qName.equals("all")){
				this.scanTestGen.generateTestData();	
			}
			if(qName.equals("join") || qName.equals("all")){
				this.joinTestGen.generateTestData();
			}
			if(qName.equals("read") || qName.equals("all")){
				this.readTestGen.generateTestData();
			}
			if(qName.equals("upsert") || qName.equals("all")){
				this.upsertTestGen.generateTestData();
			}	
		}		
	}

	public ArrayList<Double> getResult(String qName){	   
		   ArrayList<Double> relativeErrors = new ArrayList<Double>(); 
		   if(qName.equals("scan") || qName.equals("all")){
			   double scanError = this.scanTrainGen.getStat();
			   relativeErrors.add(scanError);
		    }
		   if(qName.equals("join") || qName.equals("all")){
			   double joinError = this.joinTrainGen.getStat();
			   relativeErrors.add(joinError);
		    }
		   if(qName.equals("read") || qName.equals("all")){
			   double readError = this.readTrainGen.getStat();
			   relativeErrors.add(readError);
		    }
		   if(qName.equals("upsert") || qName.equals("all")){
			  double upsertError = this.upsertTrainGen.getStat();
			  relativeErrors.add(upsertError);
		    }		  
		  return relativeErrors;
	}
	
	public static void main(String[] args){		
		  long startTime = System.currentTimeMillis();
			
		  String resultFileName = System.getenv("PROJECT_HOME") + "/workdir/result.txt";	
	      DataWriter dw = new DataWriter(resultFileName);
	      
	      String qName = "upsert";	      
	      TDMain td = new TDMain();
	      td.setTestGenerators();	      
	      td.prepareTestTables();
//	      td.genData("test",qName);
//	      td.dropTestTables();
	      	
	      int[] sampleSize = {30,60,90,120,150};
		  for(int i=0;i<sampleSize.length;i++){		       		        
		      td.setTrainGenerators( sampleSize[i]);
		      td.prepareTrainTables();  		      
//		      td.genData("train",qName);
//		      td.dropTrainTables(sampleSize[i]);
		      
//	          ArrayList<Double> relativeErrors = td.getResult(qName);
//	          dw.writeResult(sampleSize[i], relativeErrors);		      
		  }

	      long endTime = System.currentTimeMillis();
	      System.out.println("Total time used: " + (endTime - startTime)/1e3 + " seconds");   
	      System.exit(0);
	}

}
