/*
 * 
 * This test program is used to read the schema information from xml files like ColumnFamilySchema.xml
 * The xml can also have bindings like
 * <binds>
        <binding pos="1" type="Integer">1</binding>
        <binding pos="2" type="String">user2</binding>
        <binding pos="3" type="Bytes">3</binding>
        <binding pos="4" type="Double">8.0</binding>
   </binds>
   */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class WorkloadTest {
	public static void main(String argv[]) {
		PreparedStatement createSt;
		String create;
		Properties prop = new Properties();
		try{
			//build connection between YCSB phoenix client and HBase server
			Class.forName("com.salesforce.phoenix.jdbc.PhoenixDriver");
			Connection conn = DriverManager.getConnection("jdbc:phoenix:yahoo029.nicl.cs.duke.edu,yahoo030.nicl.cs.duke.edu,yahoo031.nicl.cs.duke.edu:2181",prop);
			
			//read schema files
			File fXmlFile = new File("/home/meizhen/workspace/phoenixTest/src/ColumnFamilySchema.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList nList = doc.getElementsByTagName("table");
			System.out.println(nList.getLength());
	 
			for (int temp = 0; temp < nList.getLength(); temp++) {
	 
				Node nNode = nList.item(temp);
	 
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
	 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
					Element eElement = (Element) nNode;
	 
					System.out.println("table name : " + eElement.getAttribute("name"));
					create= eElement.getElementsByTagName("statement").item(0).getTextContent();
					createSt=conn.prepareStatement(create);
					//parse bindings
					
					//System.out.println("proportion : " + eElement.getElementsByTagName("proportion").item(0).getTextContent());
					NodeList binding=eElement.getElementsByTagName("binding");
					if (binding!=null){
					//NodeList children=binding.getChildNodes();
					int len= binding.getLength();
					System.out.println(len);
					String[] bindings=new String[len];
					for (int i = 0; i < len; i++) {
						Node textChild = binding.item(i);
						Element e=(Element)textChild;
						String type=e.getAttribute("type");						
						bindings[i]=textChild.getTextContent();						
						System.out.print(bindings[i]);
						if (type.equals("Bytes")){
							createSt.setBytes(i+1,bindings[i].getBytes());
						}
						if (type.equals("Integer")){
							createSt.setInt(i+1,Integer.parseInt(bindings[i]));							
						}
						if (type.equals("String")){
							createSt.setString(i+1,bindings[i]);							
						}
						if (type.equals("Double")){
							createSt.setDouble(i+1,Double.parseDouble(bindings[i]));							
						}
					}					
				//System.out.println(bind.getTextContent());										
				//System.out.println("binding : " + eElement.getElementsByTagName("binds").item(1).getTextContent());
				/*for (String s:bindings){
					System.out.println(s);
				}*/
					}
					//execute schema creation
			createSt.executeUpdate();
			}
		}
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
