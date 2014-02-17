package duke.hbase.cm.tdg;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class HbaseLoader {
	public void createTableInHbase(){
		try {
			String[] command = {"/home/hadoop/git/phoenix/bin/psql.sh", "localhost",
                    "/home/hadoop/git/schema_design/src/duke/hbase/cm/tdg/pdgf/createTable.sql"};
			ProcessBuilder pb = new ProcessBuilder(command);
             pb.directory(new File("/home/hadoop/git/phoenix/bin"));   
             Process p;
			 p = pb.start();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			 String line = null;
			 while ((line = reader.readLine()) != null)
			 {
			    System.out.println(line);
			 }

		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
	     
	}
	
	public void loadTableInHbase(ArrayList<Table> tableList){
		for (Table t:tableList){
			try {
				String[] command = {"/home/hadoop/git/phoenix/bin/csv-bulk-loader.sh",
						   "-i", "/tdg/output/"+t.getTableName().toUpperCase()+".csv",
						   "-t", t.getTableName().toUpperCase(), 
						   "-zk", "master:2181",
						   "-hd", "hdfs://master:54310",
						   "-mr", "master:54311"};
				
				ProcessBuilder pb = new ProcessBuilder(command);
	            pb.directory(new File("/home/hadoop/git/phoenix/bin"));   
	            Process p;
				p = pb.start();
				System.out.println("Loading table " + t.getTableName() +  " into Hbase ...");
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null)
				 {
				    System.out.println(line);
				 }
				System.out.println("Table " + t.getTableName() + " was loaded into Hbase.");

			  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			
		}     
	}
	public static void main(String[] args){
		
		
        ArrayList<Table> tableList = new ArrayList<Table>();
		
		//generate table 1
		Column id = new Column("ID", " ", "INTEGER", 10, true, true);
		Column userName = new Column("UserName"," ","VARCHAR",10,false,true);		
		Column address = new Column("Address", "f","VARCHAR", 10,false,false);
		Column accBal = new Column("AccBal","f","DECIMAL",10,false,false);
		Column comment = new Column("Comment", "f","VARCHAR", 10,false,false);
		
        ArrayList<Column> rowkey = new ArrayList<Column>();
        ArrayList<Column> columns = new ArrayList<Column>();
        rowkey.add(id);
        rowkey.add(userName);
        
        columns.add(address);
        columns.add(accBal);
        columns.add(comment);
       
		Table table1 = new Table("Z",20,rowkey,columns) ;
		
		//generate table 2
		Column ip = new Column("IP", " ", "INTEGER", 10, true, true);
		Column message = new Column("Message", "f","VARCHAR", 10, false,false);
		
        ArrayList<Column> rowkey2 = new ArrayList<Column>();
        ArrayList<Column> columns2 = new ArrayList<Column>();
        
        rowkey2.add(ip);  
        columns2.add(message);     
		Table table2 = new Table("X",20,rowkey2,columns2) ;
		
		//tableList
		tableList.add(table1);
		tableList.add(table2);
	
        //
		HbaseLoader hLoader = new HbaseLoader();
		hLoader.createTableInHbase();
		hLoader.loadTableInHbase(tableList);
		
	
	}

}
