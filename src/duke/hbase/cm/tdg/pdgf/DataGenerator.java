
import pdgf.Controller;


import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import pdgf.core.exceptions.ConfigurationException;
import pdgf.core.exceptions.InvalidArgumentException;
import pdgf.core.exceptions.InvalidElementException;
import pdgf.core.exceptions.InvalidStateException;
import pdgf.core.exceptions.XmlException;

public class DataGenerator{
	private String xmlFile = null;
	private String sqlFile = null;
	private String csvFile = null;
			
	public void generateXml(ArrayList<Table> tableList){
		
	    XmlBuilder builder = new XmlBuilder();
	 	builder.setOutFilePath(xmlFile);	 
	    builder.addTableToXML(tableList);
	}
	public void generateSql(ArrayList<Table> tableList){

		//build sql file
		SqlBuilder sqlBuilder = new SqlBuilder();
		sqlBuilder.setOutFile(sqlFile);
		sqlBuilder.createTableSql(tableList);
	}
	public void generateCSV(){
		try {
		       Controller controller = new Controller();
		       controller.executeCommand(new String[] {"load",xmlFile});
		       controller.executeCommand(new String[] {"start"});
		       //close when done set true
		   } catch (IOException e) {
		       e.printStackTrace();
		   } catch (InvalidArgumentException e) {
		       e.printStackTrace();
		   } catch (InstantiationException e) {
		       e.printStackTrace();
		   } catch (IllegalAccessException e) {
		       e.printStackTrace();
		   } catch (XmlException e) {
		       e.printStackTrace(); 
		   } catch (ConfigurationException e) {
		       e.printStackTrace();
		   } catch (ParserConfigurationException e) {
		       e.printStackTrace();       
		   } catch (SAXException e) {
		      e.printStackTrace();
		   } catch (ClassNotFoundException e) {
		      e.printStackTrace();
		   } catch (InvalidElementException e) {
		      e.printStackTrace();
		   } catch (InvalidStateException e) {
		      e.printStackTrace();
		   }
	}
	public void setXmlFile(String fileName){
		this.xmlFile = fileName;
	}
    public void setSqlFile(String fileName){
    	this.sqlFile = fileName;
    }
    public void setCsvFile(String fileName){
    	this.csvFile = fileName;
    }
    public static void main(String[] agrs){
        ArrayList<Table> tableList = new ArrayList<Table>();
		
		//generate table 1
		Column id = new Column("ID", " ", "INTEGER", 10, true, true);
		Column userName = new Column("UserName"," ","VARCHAR",10,false,true);		
		Column address = new Column("Address", "_0","VARCHAR", 10,false,false);
		Column accBal = new Column("AccBal","_0","DECIMAL",10,false,false);
		Column comment = new Column("Comment", "_0","VARCHAR", 10,false,false);
		
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
		Column message = new Column("Message", "_0","VARCHAR", 10, false,false);
		
        ArrayList<Column> rowkey2 = new ArrayList<Column>();
        ArrayList<Column> columns2 = new ArrayList<Column>();
        
        rowkey2.add(ip);  
        columns2.add(message);     
		Table table2 = new Table("X",20,rowkey2,columns2) ;
		
		//tableList
		tableList.add(table1);
		tableList.add(table2);
	
    	DataGenerator dg = new DataGenerator();
    	dg.setSqlFile("createTable.sql");
    	dg.setXmlFile("z.xml");
    	dg.generateXml(tableList);
    	dg.generateSql(tableList);
    	dg.generateCSV();
  
   }
} 

