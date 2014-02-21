package duke.hbase.cm.tdg;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class HbaseLoader {
	    private static final String PHOENIX_HOME = System.getenv("PHOENIX_HOME");
        private static final String psql = PHOENIX_HOME+"/bin/psql.sh";
        private static final String psqlPath = PHOENIX_HOME+"/bin/";
        private static final String csvBulkLoader = PHOENIX_HOME + "/bin/csv-bulk-loader.sh";
        private static final String csvBulkLoaderPath = PHOENIX_HOME + "/bin/";
        private static final String zk =  "master:2181";
        private static final String hd =  "hdfs://master:54310";
        private static final String mr =  "master:54311"; 
	public void createTableInHbase(String sqlFile){
		try {
                         System.out.println("-------------------------------------------");                      
                         System.out.println("Executing " +  sqlFile + " to create tables in Hbase");
                         System.out.println("-------------------------------------------");
                         String[] command = {HbaseLoader.psql, "localhost",sqlFile};
		        
                         ProcessBuilder pb = new ProcessBuilder(command);
                         pb.directory(new File(HbaseLoader.psqlPath));   
                         pb.redirectErrorStream(true);
                         Process p;
			 p = pb.start();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			 String line = null;
			 while ((line = reader.readLine()) != null)
			 {
			    System.out.println(line);
			 }
                        System.out.println("Tables were created in Hbase");
                        System.out.println("-------------------------------------------");
		  } catch (IOException e) {
			e.printStackTrace();
		 } 
	}
	
	public void loadTableInHbase(ArrayList<Table> tableList, String csvDir){
		for (Table t:tableList){
			try {
				String[] command = {HbaseLoader.csvBulkLoader,
						   "-i", csvDir+t.getTableName().toUpperCase()+".csv",
						   "-t", t.getTableName().toUpperCase(), 
						   "-zk", zk,
						   "-hd", hd,
						   "-mr", mr};
				
				ProcessBuilder pb = new ProcessBuilder(command);
			        pb.directory(new File(HbaseLoader.csvBulkLoaderPath));   
			        pb.redirectErrorStream(true); 
                                Process p;
				p = pb.start();
                                System.out.println("---------------------------------------------");
				System.out.println("Loading table " + t.getTableName() + 
                                     " from " + csvDir+t.getTableName().toUpperCase()+".csv" +" into Hbase ...");
                                System.out.println("-------------------------------------------");
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null)
				 {
				    System.out.println(line);
				 }
				System.out.println("Table " + t.getTableName() + " was loaded into Hbase.");

			  } catch (IOException e) {
				e.printStackTrace();
			 }
			
		}     
	}
	public static void main(String[] args){
ArrayList<Table> tableList = new ArrayList<Table>();
		
		//table 1
 		Column id       = new Column("ID",      " ", "INTEGER",  10,               true, true, true);
 		Column userName = new Column("UserName"," ", "VARCHAR",  10,false,true,true);	
 		Column address  = new Column("Address", "f", "VARCHAR",  10,false,false,false);
 		Column accBal   = new Column("AccBal",  "f", "DECIMAL",  10,false,false,false);
 		Column comment  = new Column("Comment", "f", "VARCHAR",  10,false,false,false);
 		
		ArrayList<Column> rowkey = new ArrayList<Column>();
		rowkey.add(id);
		rowkey.add(userName);
		ArrayList<Column> columns = new ArrayList<Column>();
		columns.add(address);
		columns.add(accBal);
		columns.add(comment);
       
		Table table1 = new Table("Z",20,rowkey,columns) ;
	
		tableList.add(table1);
		
		HbaseLoader hLoader = new HbaseLoader();
		hLoader.createTableInHbase("workdir/createTable.sql");
		hLoader.loadTableInHbase(tableList,"/tdg/csvdir/");
	
	}

}
