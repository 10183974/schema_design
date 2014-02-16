import java.io.IOException;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
/*
 * build xml file to communicate with pdgf
 * 
 */
public class XmlBuilder {
	 private String templateFile = "template.xml"; 
   	 private String outputFile = "z.xml";
   	 private Document document = null;
   		
   	 
   	 private void parseXMLDocument(){
	        try {
			    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
				document = documentBuilder.parse(templateFile);
			} catch (SAXException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			} catch(ParserConfigurationException e){
			   e.printStackTrace();
			}	        
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
		 tableElement.setAttribute("name", t.getTableName());
		 Element sizeElement = createSizeElement(t.getNRows());
         tableElement.appendChild(sizeElement);
         
         //new fields
		 Element fields = document.createElement("fields");
		      
         ArrayList<Column> columns = t.getRowkey();
         columns.addAll(t.getColumns()); 
         for(Column c:columns){
        	 if (c.getColumnType().equalsIgnoreCase("INTEGER")){
        		 Element field = createIntegerField(c.getColumnName());
        		 if (c.getIsPrimary())
        		     field = setPrimary(field);
        		 
        		 fields.appendChild(field);
        	 }
        	 else if (c.getColumnType().equalsIgnoreCase("DECIMAL")){
        		 Element field = createDecimalField(c.getColumnName());
        		 fields.appendChild(field);
        		 
        	 }
        	 else if (c.getColumnType().equalsIgnoreCase("VARCHAR")){
    			 Element field = createTextField(c.getColumnName(),c.getColumnSize());
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
			    StreamResult result = new StreamResult(outputFile);
			    transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
	 }
	 public void addTableToXML(ArrayList<Table> tables){	 
		 //must parse the document first
		 parseXMLDocument();
		 //get the parent node of tables
	     Element tablesNodeParent = getTablesNode();
	     //create table in the table list, and append them to the tablenode
		 for(Table t:tables){
			 Element tableElement = createTableElement(t);
		     tablesNodeParent.appendChild(tableElement);
		 }
		 //write tablenodes into xml file
		 writeToXML();	    	 
	 }

     public void setOutFilePath(String outPath){
		 outputFile = outPath;
	 }

	 public static void main(String[] args){
		 Column id = new Column("ID", " ", "INTEGER", 10, true, true);
	     Column userName = new Column("UserName"," ","VARCHAR",10,false,true);
			
		 Column address = new Column("Address", "_0","VARCHAR", 10,false,false);
		 Column accBal = new Column("AccBal","_0","DECIMAL",10,false,false);
		 Column comment = new Column("Comment", "_0","VARCHAR", 10,false,false);
			
	     ArrayList<Column> rowkey = new ArrayList<Column>();
	     rowkey.add(id);
	     rowkey.add(userName);
	     ArrayList<Column> columns = new ArrayList<Column>();
	     columns.add(address);
	     columns.add(accBal);
	     columns.add(comment);
	       
		 Table t = new Table("Z",20,rowkey,columns) ;
		 t.printTableInfo();
			
		 XmlBuilder builder = new XmlBuilder();
		 
		 ArrayList<Table> tables = new ArrayList<Table>();     
	     tables.add(t);
	  	
		 builder.setOutFilePath("z.xml");	 
		 builder.addTableToXML(tables);
         } 
}
