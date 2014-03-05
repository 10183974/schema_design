package duke.hbase.cm.tdg;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlBuilder {
	 private static final String PROJECT_HOME = System.getenv("PROJECT_HOME");
	 private static final String templateFile =  PROJECT_HOME + "/src/duke/hbase/cm/tdg/template.xml";
	 private String xmlFile = null;
	 private String csvDir = null;
	  
     private Document document = null;
     public XmlBuilder(){
    	 parseXMLDocument();
     }
     private void parseXMLDocument(){
	        try {
			    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				document = documentBuilder.parse(templateFile);
				System.out.println("Parsing pdgf template file = " + templateFile);
			} catch (SAXException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			} catch(ParserConfigurationException e){
			   e.printStackTrace();
			}	        
   	 }
     private void modifyOutputDir(){
	        //get element <output/>
	        Element output = (Element) document.getElementsByTagName("output").item(0);
	        //get outputDir element
	        Element outputDir = (Element) output.getElementsByTagName("outputDir").item(0);       
	        outputDir.setTextContent(this.csvDir);
	        output.appendChild(outputDir);	       
	        System.out.println("Setting output csv directory  = " + this.csvDir);      
     }
 	 private Element getTablesNode(){	            	
            //list of tables
	        NodeList tablesList = document.getElementsByTagName("tables");
	        Element tables = (Element) tablesList.item(0);               
	        return tables;
	 }
	 private Element createSizeElement(int size){
	        Element e = document.createElement("size");
	        e.appendChild(document.createTextNode(String.valueOf(size)));
	        return e;        
	 }
	 private Element createTypeElement(String type){
	        Element e = document.createElement("type");
	        e.appendChild(document.createTextNode("java.sql.Types." + type));
	        return e;
	 }
	 private Element createTextGeneratorElement(String name, int gsize){	    		           
	        Element e = document.createElement("generator");
	        e.setAttribute("name", "tpc.h.generators.TextString");        
	        
	        Element sizeElement = createSizeElement(gsize); 
	        e.appendChild(sizeElement);
	        return e;	       
	 }
	 private Element createIntegerGeneratorElement(String name){	          
	        Element e = document.createElement("generator");
	        e.setAttribute("name", "IdGenerator");
	        return e;	       
	 }
	 private Element createDecimalGeneratorElement(){	          
	        Element e = document.createElement("generator");
	        e.setAttribute("name", "tpc.h.generators.RandomValueXY");
	        Element x = document.createElement("x");
	        Element y = document.createElement("y");
	        x.appendChild(document.createTextNode(String.valueOf(-999.99)));
	        y.appendChild(document.createTextNode(String.valueOf(9999.99)));
	        e.appendChild(x);
	        e.appendChild(y);
	        return e;	       
	 }
	 private Element createPrimaryElement(boolean isPrimary){
		   Element e = document.createElement("primary");
		   e.appendChild(document.createTextNode(String.valueOf(isPrimary)));
	       return e;	   
	 }
	 private Element createIntegerField(String fieldName){
		    // Assuming this is the primary key
		    Element field = document.createElement("field");
		    field.setAttribute("name", fieldName);
		    Element typeElement = createTypeElement("INTEGER");
		    Element generatorElement = createIntegerGeneratorElement("IdGenerator");	    	    
		    field.appendChild(typeElement);
		    field.appendChild(generatorElement);		    	        
		    return field;	  	    
	 }
	 private Element setPrimary(Element field){
		    Element primaryElement = createPrimaryElement(true);
		    field.appendChild(primaryElement);
		    return field;
	 }
	 private Element createDecimalField(String fieldName){
		    Element field = document.createElement("field");
		    field.setAttribute("name", fieldName);
		    Element typeElement = createTypeElement("DECIMAL");
		    Element generatorElement = createDecimalGeneratorElement();
		    	    
		    field.appendChild(typeElement);
		    field.appendChild(generatorElement);
		    return field;		 
	 }
	 private Element createTextField(String fieldName, int size){
		    Element field = document.createElement("field");
		    field.setAttribute("name", fieldName);
		    Element sizeElement = createSizeElement(size);
		    Element typeElement = createTypeElement("VARCHAR");
		    Element generatorElement = createTextGeneratorElement("tpc.h.generators.TextString",size);
		    field.appendChild(sizeElement);
		    field.appendChild(typeElement);
		    field.appendChild(generatorElement);
		    return field;	  	    
	 }
	 private Element createTableElement(Table t){
		 
		 Element tableElement = document.createElement("table");
		 tableElement.setAttribute("name", t.getName());
		 Element sizeElement = createSizeElement(t.getNumRows());
         tableElement.appendChild(sizeElement);
         
         //new fields
		 Element fields = document.createElement("fields");
		      
  
         ArrayList<Column> rowkey = t.getRowkeyList();
         ArrayList<Column> columns = t.getColumnList();
         for(Column r:rowkey){
        	 if (r.getType().equalsIgnoreCase("INTEGER")){
        		 Element field = createIntegerField(r.getName());
        		 if (r.isPrimary())
        		     field = setPrimary(field);
        		 
        		 fields.appendChild(field);
        	 }
        	 else if (r.getType().equalsIgnoreCase("DECIMAL")){
        		 Element field = createDecimalField(r.getName());
        		 fields.appendChild(field);
        		 
        	 }
        	 else if (r.getType().equalsIgnoreCase("VARCHAR")){
    			 Element field = createTextField(r.getName(),r.getSize());
    			 fields.appendChild(field);
        	 }      	 
         }	

         for(Column c:columns){
        	 if (c.getType().equalsIgnoreCase("INTEGER")){
        		 Element field = createIntegerField(c.getName());
        		 if (c.isPrimary())
        		     field = setPrimary(field);
        		 
        		 fields.appendChild(field);
        	 }
        	 else if (c.getType().equalsIgnoreCase("DECIMAL")){
        		 Element field = createDecimalField(c.getName());
        		 fields.appendChild(field);
        		 
        	 }
        	 else if (c.getType().equalsIgnoreCase("VARCHAR")){
    			 Element field = createTextField(c.getName(),c.getSize());
    			 fields.appendChild(field);
        	 }      	 
         }	
         
         
         tableElement.appendChild(fields);
		 return tableElement;
	 }
	 private void writeToXML(){	
			try {
				DOMSource source = new DOMSource(document);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			        StreamResult result = new StreamResult(xmlFile);
			        transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
	 }
	 public void createXmlFile(ArrayList<Table> tables){	 
	
		 //get the node  <tables/>
	     Element tablesNodeParent = getTablesNode();
	     //create table in the table list, and append them to the tablenode
		 for(Table t:tables){
			 Element tableElement = createTableElement(t);
		     tablesNodeParent.appendChild(tableElement);
		 }
		 //write tableList into xml file
		 writeToXML();	    
		 System.out.println("-------------------------------------------");
		 System.out.println("Writing pdgf xml file to " + xmlFile);
		 System.out.println("-------------------------------------------");
	 }

     public void setXmlFile(String name){
		 this.xmlFile = name;
	 }
     public void setCsvDir(String aCsvDir){	 
	     this.csvDir = aCsvDir;
	    modifyOutputDir();
	 }
 
	 public static void main(String[] args){
		   
		 
         } 
}
