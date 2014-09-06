/*
 * This test program read the workload xml files
 * building the prepared statement according to the statement and bindings
 * and executes the queries
 * */

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.yahoo.ycsb.StringByteIterator;


public class WorkloadParse {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//PreparedStatement createSt;
		PreparedStatement selectSt;
		String select;
		Properties prop = new Properties();
		double readproportion;
		double scanproportion;
		double updateproportion;
		double insertproportion;
		double deleteproportion;
		Hashtable<Integer, String> readbinds=new Hashtable<Integer, String>();
		Hashtable<Integer, String> scanbinds=new Hashtable<Integer, String>();
		Hashtable<Integer, String> updatebinds=new Hashtable<Integer, String>();
		Hashtable<Integer, String> insertbinds=new Hashtable<Integer, String>();
		Hashtable<Integer, String> deletebinds=new Hashtable<Integer, String>();
		Hashtable<Integer, String> binds=new Hashtable<Integer, String>();
		try{
			Class.forName("com.salesforce.phoenix.jdbc.PhoenixDriver");
			Connection conn = DriverManager.getConnection("jdbc:phoenix:yahoo029.nicl.cs.duke.edu,yahoo030.nicl.cs.duke.edu,yahoo031.nicl.cs.duke.edu:2181",prop);
			
			// read in workload files
			File fXmlFile = new File("/home/meizhen/workspace/phoenixTest/src/promoteKeyWorkload2.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList nList = doc.getElementsByTagName("query");
			System.out.println(nList.getLength());

	 
			//for (int temp = 0; temp < nList.getLength(); temp++) {
	 
				//Node nNode = nList.item(temp);
				Node nNode = nList.item(0);
				System.out.println("\nCurrent Element :" + ((Element)nNode).getAttribute("name"));
	 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
					Element eElement = (Element) nNode;
					
	 
					System.out.println("Staff id : " + eElement.getAttribute("name"));
					String name=eElement.getAttribute("name");
					select= eElement.getElementsByTagName("statement").item(0).getTextContent();
					selectSt=conn.prepareStatement(select);
					selectSt.setString(1, "user5228908089637460490");
					ResultSet resultSet = selectSt.executeQuery();
					resultSet.next();
					System.out.println(resultSet.getString("c"));
					System.out.println(" ");
				      if (!resultSet.next()) {
				          resultSet.close();
				         //return 1;
				        }
				        
				        resultSet.close();
				        //return 0;
				//	  
					System.out.println("proportion : " + eElement.getElementsByTagName("proportion").item(0).getTextContent());
					
					String proportion=eElement.getElementsByTagName("proportion").item(0).getTextContent();
					if (name.equals("read")) {
						readproportion=Double.parseDouble(proportion);
						binds=readbinds;
					}
					if (name.equals("scan")) {
						scanproportion=Double.parseDouble(proportion);
						binds=scanbinds;
					}
					if (name.equals("update")) {
						updateproportion=Double.parseDouble(proportion);
						binds=updatebinds;
					}
					if (name.equals("insert")) {
						insertproportion=Double.parseDouble(proportion);
						binds=insertbinds;
					}
					if (name.equals("delete")) {
						deleteproportion=Double.parseDouble(proportion);
						binds=deletebinds;
					}
					NodeList binding=eElement.getElementsByTagName("binding");
					if (binding!=null){
					//NodeList children=binding.getChildNodes();
					int len= binding.getLength();
					System.out.println(len);
					String[] bindings=new String[len];
					for (int i = 0; i < len; i++) {
						Node textChild = binding.item(i);
						Element e=(Element)textChild;
						//String type=e.getAttribute("type");						
						bindings[i]=textChild.getTextContent();	
						binds.put(i,bindings[i]);
						System.out.println(bindings[i]);

					}		
								
				//System.out.println(bind.getTextContent());										
				//System.out.println("bindiHashtable<Integer, String> binds;ng : " + eElement.getElementsByTagName("binds").item(1).getTextContent());
				/*for (String s:bindings){
					System.out.println(s);
					String keyDistri=binds.get(0);
    	IntegerGenerator keyGenerator;
    	int keynum;
    	if (keyDistri.equals("uniform"))keyGenerator=uniformKey;
    	if (keyDistri.equals("zipfian"))keyGenerator=zipfianKey;
    	if (keyDistri.equals("latest"))keyGenerator=latestKey;
				}*/
					}
			//createSt.executeUpdate();
			}
		//}
		}
		catch (ClassNotFoundException e){
			  System.err.println("Error in initializing the JDBS driver: " + e);
		 }
		catch (ParserConfigurationException e){
			  System.err.println("Error in Parser: " + e);
		 }
		catch (SAXException e){
			  System.err.println("Error in SAX: " + e);
		 }
		catch (IOException e){
			  System.err.println("Error in IO: " + e);
		 }
		catch(SQLException e){
			System.err.println("error in building prepareStatement"+e);
		}
	
	}
	}


