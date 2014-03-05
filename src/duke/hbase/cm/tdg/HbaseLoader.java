package duke.hbase.cm.tdg;

import java.util.ArrayList;


public class HbaseLoader {
	private static final String PHOENIX_BIN_HOME = System.getenv("PHOENIX_HOME") +"/bin";
	private static final String psql = PHOENIX_BIN_HOME + "/psql.sh";
	private static final String csv_bulk_loader = PHOENIX_BIN_HOME + "/csv-bulk-loader.sh";
	
    private static final String zk =  "master:2181";
    private static final String hd =  "hdfs://master:54310";
    private static final String mr =  "master:54311"; 
	public void createTableInHbase(String sqlFile){
		 System.out.println("----------------------------------------------------------------------");
	     System.out.println("Creating tables in Hbase");
		 String[] cmd = new String[]{psql, "localhost",sqlFile};
		 ProcessExecutor pe = new ProcessExecutor("psql", cmd,PHOENIX_BIN_HOME);
         pe.read();
         pe.stop();
	}
	
	public void loadTableInHbase(ArrayList<Table> tableList, String csvDir){
		for (Table t:tableList){
			 System.out.println("----------------------------------------------------------------------");
		     System.out.println("Loading table " + t.getName() + " into Hbase");
			String[] cmd = new String[]{csv_bulk_loader,
					   "-i", csvDir + "/" + t.getName()+".csv",
					   "-t", t.getName(), 
					   "-zk", zk,
					   "-hd", hd,
					   "-mr", mr};
			 ProcessExecutor pe = new ProcessExecutor("csv-bulk-loader", cmd,PHOENIX_BIN_HOME);
	         pe.read();
	         pe.stop();	
		}     
	}
}
