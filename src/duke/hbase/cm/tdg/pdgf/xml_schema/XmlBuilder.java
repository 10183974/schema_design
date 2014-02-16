import java.io.IOException;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XmlBuilder {
	 private String templateFile = "template.xml"; 
   	 private String outputFile = "z.xml";
   	 private Document document = null;
   		
	 private Element getTablesRoot(){	            
	        try {
			    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				document = documentBuilder.parse(templateFile);
			} catch (SAXException e){
				e.printStackTrace();
			} catch(IOException e){
			} catch(ParserConfigurationException e){
			   e.printStackTrace();
			}
			
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
	 private Element createTextGeneratorElement(String name, int min){	    		           
	        Element e = document.createElement("generator");
	        e.setAttribute("name", "tpc.h.generators.TextString");        
	        
	        Element size = createSizeElement(min); 
	        e.appendChild(size);
	        return e;	       
	 }
	 
	 private Element createIntegerGeneratorElement(String name){	          
	        Element e = document.createElement("generator");
	        e.setAttribute("name", "IdGenerator");
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
	        Element primaryElement = createPrimaryElement(true);
		    Element typeElement = createTypeElement("INTEGER");
		    Element generatorElement = createIntegerGeneratorElement("IdGenerator");
		    	    
		    field.appendChild(typeElement);
		    field.appendChild(primaryElement);
		    field.appendChild(generatorElement);
		    return field;	  	    
	 }
	 
	 private Element createTextField(String fieldName, int min, int max){
		    Element field = document.createElement("field");
		    field.setAttribute("name", fieldName);
		    Element sizeElement = createSizeElement(max);
		    Element typeElement = createTypeElement("VARCHAR");
		    Element generatorElement = createTextGeneratorElement("tpc.h.generators.TextString",min);
		    field.appendChild(sizeElement);
		    field.appendChild(typeElement);
		    field.appendChild(generatorElement);
		    return field;	  	    
	 }
	 private Element createTable(String tableName, int NoRows){
		 
		 Element table = document.createElement("table");
		 table.setAttribute("name", tableName);
		 Element sizeElement = createSizeElement(NoRows);
                 table.appendChild(sizeElement);
		    
		 Element fields = document.createElement("fields");
		 
		 Element IdField = createIntegerField("ID");
		 fields.appendChild(IdField);
	     	     
		 for(int i=0;i<10;i++){
			 Element field = createTextField("Text"+(i+1),5,5);
			 fields.appendChild(field);
		 }
		 	 	 
		 table.appendChild(fields);
		 return table;
	 }

	 private void writeToXML(){	
			try {
		        DOMSource source = new DOMSource(document);
		        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		        Transformer transformer;
				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			    StreamResult result = new StreamResult(outputFile);
			    transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
	 }
	 public void addTableToXML(String tableName, int NoRows){	  
	    	Element tablesRoot = getTablesRoot();
	    	Element table = createTable(tableName, NoRows);
	    	tablesRoot.appendChild(table);
	    	writeToXML();	 
	 }

     
	 public static void main(String[] args){
		 XmlBuilder builder = new XmlBuilder();
		 builder.addTableToXML("Z",300);

     }
}
