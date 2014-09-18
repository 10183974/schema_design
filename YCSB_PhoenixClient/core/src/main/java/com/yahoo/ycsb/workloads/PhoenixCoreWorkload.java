/**
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */

package com.yahoo.ycsb.workloads;

import java.util.Properties;

import com.yahoo.ycsb.*;
import com.yahoo.ycsb.generator.CounterGenerator;
import com.yahoo.ycsb.generator.DiscreteGenerator;
import com.yahoo.ycsb.generator.ExponentialGenerator;
import com.yahoo.ycsb.generator.Generator;
import com.yahoo.ycsb.generator.ConstantIntegerGenerator;
import com.yahoo.ycsb.generator.HotspotIntegerGenerator;
import com.yahoo.ycsb.generator.HistogramGenerator;
import com.yahoo.ycsb.generator.IntegerGenerator;
import com.yahoo.ycsb.generator.ScrambledZipfianGenerator;
import com.yahoo.ycsb.generator.SkewedLatestGenerator;
import com.yahoo.ycsb.generator.UniformIntegerGenerator;
import com.yahoo.ycsb.generator.ZipfianGenerator;
import com.yahoo.ycsb.measurements.Measurements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The core benchmark scenario. Represents a set of clients doing simple CRUD operations. The relative 
 * proportion of different kinds of operations, and other properties of the workload, are controlled
 * by parameters specified at runtime.
 * 
 * Properties to control the client:
 * <UL>
 * <LI><b>fieldcount</b>: the number of fields in a record (default: 10)
 * <LI><b>fieldlength</b>: the size of each field (default: 100)
 * <LI><b>readallfields</b>: should reads read all fields (true) or just one (false) (default: true)
 * <LI><b>writeallfields</b>: should updates and read/modify/writes update all fields (true) or just one (false) (default: false)
 * <LI><b>readproportion</b>: what proportion of operations should be reads (default: 0.95)
 * <LI><b>updateproportion</b>: what proportion of operations should be updates (default: 0.05)
 * <LI><b>insertproportion</b>: what proportion of operations should be inserts (default: 0)
 * <LI><b>scanproportion</b>: what proportion of operations should be scans (default: 0)
 * <LI><b>readmodifywriteproportion</b>: what proportion of operations should be read a record, modify it, write it back (default: 0)
 * <LI><b>requestdistribution</b>: what distribution should be used to select the records to operate on - uniform, zipfian, hotspot, or latest (default: uniform)
 * <LI><b>maxscanlength</b>: for scans, what is the maximum number of records to scan (default: 1000)
 * <LI><b>scanlengthdistribution</b>: for scans, what distribution should be used to choose the number of records to scan, for each scan, between 1 and maxscanlength (default: uniform)
 * <LI><b>insertorder</b>: should records be inserted in order by key ("ordered"), or in hashed order ("hashed") (default: hashed)
 * </ul> 
 */
public class PhoenixCoreWorkload extends CoreWorkload
{	
	Hashtable<String, Hashtable<Integer, String>> binds= new Hashtable<String, Hashtable<Integer, String>>();
	Hashtable<String, Double> proportions = new Hashtable<String, Double>();

	public void init(Properties p) throws WorkloadException
	{
		try{
			String query_id;
			String query_stmt;
			String query_proportion;
			File fXmlFile = new File(p.getProperty("workloadfile"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList nList = doc.getElementsByTagName("query");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
				    query_id = eElement.getAttribute("name");
					query_stmt = eElement.getElementsByTagName("statement").item(0).getTextContent();
					query_proportion = eElement.getElementsByTagName("proportion").item(0).getTextContent();
					proportions.put(query_id, Double.parseDouble(query_proportion));					
					NodeList binding=eElement.getElementsByTagName("binding");
					if (binding!=null){
						int len= binding.getLength();
						String[] bindings=new String[len];
						Hashtable<Integer, String> q_bind = new Hashtable<Integer, String>(); 
						for (int i = 0; i < len; i++) {
							Node textChild = binding.item(i);
							Element e=(Element)textChild;
							bindings[i]=textChild.getTextContent();
							q_bind.put(i,bindings[i]);
						}
						binds.put(query_id, q_bind);
					}
				}
			}
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

		table = p.getProperty(TABLENAME_PROPERTY,TABLENAME_PROPERTY_DEFAULT);

		fieldcount=Integer.parseInt(p.getProperty(FIELD_COUNT_PROPERTY,FIELD_COUNT_PROPERTY_DEFAULT));
		fieldlengthgenerator = CoreWorkload.getFieldLengthGenerator(p);		
		recordcount=Integer.parseInt(p.getProperty(Client.RECORD_COUNT_PROPERTY));
		String requestdistrib=p.getProperty(REQUEST_DISTRIBUTION_PROPERTY,REQUEST_DISTRIBUTION_PROPERTY_DEFAULT);
		int insertstart=Integer.parseInt(p.getProperty(INSERT_START_PROPERTY,INSERT_START_PROPERTY_DEFAULT));

		if (p.getProperty(INSERT_ORDER_PROPERTY,INSERT_ORDER_PROPERTY_DEFAULT).compareTo("hashed")==0)
		{
			orderedinserts=false;
		}
		else
		{
			orderedinserts=true;
		}

		keysequence=new CounterGenerator(insertstart);
		operationchooser=new DiscreteGenerator();
		for(String queryid: proportions.keySet()) {
			operationchooser.addValue(Double.parseDouble(queryid), queryid);
		}

		transactioninsertkeysequence=new CounterGenerator(recordcount);
		if (requestdistrib.compareTo("uniform")==0)
		{
			keychooser=new UniformIntegerGenerator(0,recordcount-1);
		}
		else if (requestdistrib.compareTo("zipfian")==0)
		{
			//it does this by generating a random "next key" in part by taking the modulus over the number of keys
			//if the number of keys changes, this would shift the modulus, and we don't want that to change which keys are popular
			//so we'll actually construct the scrambled zipfian generator with a keyspace that is larger than exists at the beginning
			//of the test. that is, we'll predict the number of inserts, and tell the scrambled zipfian generator the number of existing keys
			//plus the number of predicted keys as the total keyspace. then, if the generator picks a key that hasn't been inserted yet, will
			//just ignore it and pick another key. this way, the size of the keyspace doesn't change from the perspective of the scrambled zipfian generator

			int opcount=Integer.parseInt(p.getProperty(Client.OPERATION_COUNT_PROPERTY));
			int expectednewkeys=(int)(((double)opcount)*insertproportion*2.0); //2 is fudge factor

			keychooser=new ScrambledZipfianGenerator(recordcount+expectednewkeys);
		}
		else if (requestdistrib.compareTo("latest")==0)
		{
			keychooser=new SkewedLatestGenerator(transactioninsertkeysequence);
		}
		else if (requestdistrib.equals("hotspot")) 
		{
			double hotsetfraction = Double.parseDouble(p.getProperty(
					HOTSPOT_DATA_FRACTION, HOTSPOT_DATA_FRACTION_DEFAULT));
			double hotopnfraction = Double.parseDouble(p.getProperty(
					HOTSPOT_OPN_FRACTION, HOTSPOT_OPN_FRACTION_DEFAULT));
			keychooser = new HotspotIntegerGenerator(0, recordcount - 1, 
					hotsetfraction, hotopnfraction);
		}
		else
		{
			throw new WorkloadException("Unknown request distribution \""+requestdistrib+"\"");
		}

		fieldchooser=new UniformIntegerGenerator(0,fieldcount-1);

		//initialize keychooser for differenct trasactions
		if (readbinds.get(0)!=null){
			String readDis=readbinds.get(0);
			if (readDis.equals("uniform"))selectKey=new UniformIntegerGenerator(0,recordcount-1);
			if (readDis.equals("zipfian")){
				int opcount=Integer.parseInt(p.getProperty(Client.OPERATION_COUNT_PROPERTY));
				int expectednewkeys=(int)(((double)opcount)*insertproportion*2.0); //2 is fudge factor				
				selectKey=new ScrambledZipfianGenerator(recordcount+expectednewkeys);	    		
			}
			if (readDis.equals("latest"))selectKey=new SkewedLatestGenerator(transactioninsertkeysequence);			
		}
		if (scanbinds.get(0)!=null){
			String readDis=scanbinds.get(0);
			if (readDis.equals("uniform"))scanKey=new UniformIntegerGenerator(0,recordcount-1);
			if (readDis.equals("zipfian")){
				int opcount=Integer.parseInt(p.getProperty(Client.OPERATION_COUNT_PROPERTY));
				int expectednewkeys=(int)(((double)opcount)*insertproportion*2.0); //2 is fudge factor				
				scanKey=new ScrambledZipfianGenerator(recordcount+expectednewkeys);	    		
			}
			if (readDis.equals("latest"))scanKey=new SkewedLatestGenerator(transactioninsertkeysequence);			
		}
		if (updatebinds.get(0)!=null){
			String readDis=updatebinds.get(0);
			if (readDis.equals("uniform"))updateKey=new UniformIntegerGenerator(0,recordcount-1);
			if (readDis.equals("zipfian")){
				int opcount=Integer.parseInt(p.getProperty(Client.OPERATION_COUNT_PROPERTY));
				int expectednewkeys=(int)(((double)opcount)*insertproportion*2.0); //2 is fudge factor				
				updateKey=new ScrambledZipfianGenerator(recordcount+expectednewkeys);	    		
			}
			if (readDis.equals("latest"))updateKey=new SkewedLatestGenerator(transactioninsertkeysequence);			
		}
		if (insertbinds.get(0)!=null){
			String readDis=insertbinds.get(0);
			if (readDis.equals("uniform"))insertKey=new UniformIntegerGenerator(0,recordcount-1);
			if (readDis.equals("zipfian")){
				int opcount=Integer.parseInt(p.getProperty(Client.OPERATION_COUNT_PROPERTY));
				int expectednewkeys=(int)(((double)opcount)*insertproportion*2.0); //2 is fudge factor				
				insertKey=new ScrambledZipfianGenerator(recordcount+expectednewkeys);	    		
			}
			if (readDis.equals("latest"))insertKey=new SkewedLatestGenerator(transactioninsertkeysequence);			
		}

		uniformGenerator=new UniformIntegerGenerator(1,200);
		zipfianGenerator=new ScrambledZipfianGenerator(1,200);
		exponentialGenerator=new ExponentialGenerator(95,200);
	}

	public String buildKeyName(long keynum) {
		if (!orderedinserts)
		{
			keynum=Utils.hash(keynum);
		}
		return "user"+keynum;
	}
	HashMap<String, ByteIterator> buildValues() {
		HashMap<String,ByteIterator> values=new HashMap<String,ByteIterator>();

		for (int i=0; i<fieldcount; i++)
		{
			String fieldkey="field"+i;
			ByteIterator data= new RandomByteIterator(fieldlengthgenerator.nextInt());
			values.put(fieldkey,data);
		}
		return values;
	}
	
	//updated buildValue method to generator different distributions for different transactions, by Meizhen
	HashMap<String, ByteIterator> buildValuesToSQLQ(Hashtable<Integer,String> binds) {
		HashMap<String,ByteIterator> values=new HashMap<String,ByteIterator>();
		for (int i=0; i<binds.size()-1;i++){
			String fieldkey="field"+i+1;
			String dis=binds.get(i+1);
			//by default: uniform distribution
			ByteIterator data=new UniformByteIterator(uniformGenerator);
			if (dis.equals("uniform")){
				//generator=new UniformIntegerGenerator(1,200);
				data=new UniformByteIterator(uniformGenerator);
			}
			if (dis.equals("zipfian")){
				//generator=new ScrambledZipfianGenerator(1,200);
				data=new UniformByteIterator(zipfianGenerator);
			}
			if (dis.equals("exponential")){
				//generator=new ExponentialGenerator(95,200);
				data=new UniformByteIterator(exponentialGenerator);
			}
			//ByteIterator data=new UniformByteIterator(binds.get(i+1));
			values.put(fieldkey,data);
		}
		return values;
	}
	HashMap<String, ByteIterator> buildUpdate() {
		//update a random field
		HashMap<String, ByteIterator> values=new HashMap<String,ByteIterator>();
		String fieldname="field"+fieldchooser.nextString();
		ByteIterator data = new RandomByteIterator(fieldlengthgenerator.nextInt());
		//ByteIterator data = new UniformByteIterator("uniform");
		values.put(fieldname,data);
		return values;
	}

	/**
	 * Do one insert operation. Because it will be called concurrently from multiple client threads, this 
	 * function must be thread safe. However, avoid synchronized, or the threads will block waiting for each 
	 * other, and it will be difficult to reach the target throughput. Ideally, this function would have no side
	 * effects other than DB operations.
	 */
	public boolean doInsert(DB db, Object threadstate)
	{
		int keynum=keysequence.nextInt();
		String dbkey = buildKeyName(keynum);
		HashMap<String, ByteIterator> values = buildValues();
		//HashMap<String, ByteIterator> values = buildValue(insertbinds);
		if (db.insert(table,dbkey,values) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Do one transaction operation. Because it will be called concurrently from multiple client threads, this 
	 * function must be thread safe. However, avoid synchronized, or the threads will block waiting for each 
	 * other, and it will be difficult to reach the target throughput. Ideally, this function would have no side
	 * effects other than DB operations.
	 */
	public boolean doTransaction(DB db, Object threadstate)
	{
		String op=operationchooser.nextString();

		if (op.compareTo("READ")==0)
		{
			doTransactionRead(db);
		}
		else if (op.compareTo("UPDATE")==0)
		{
			doTransactionUpdate(db);
		}
		else if (op.compareTo("INSERT")==0)
		{
			doTransactionInsert(db);
		}
		else if (op.compareTo("SCAN")==0)
		{
			doTransactionScan(db);
		}
		else
		{
			doTransactionReadModifyWrite(db);
		}

		return true;
	}

	int nextKeynum() {
		int keynum;
		if(keychooser instanceof ExponentialGenerator) {
			do
			{
				keynum=transactioninsertkeysequence.lastInt() - keychooser.nextInt();
			}
			while(keynum < 0);
		} else {
			do
			{
				keynum=keychooser.nextInt();
			}
			while (keynum > transactioninsertkeysequence.lastInt());
		}
		return keynum;
	}
	int nextKeynum2(IntegerGenerator keyGenerator){
		int keynum;
		do
		{
			keynum=keyGenerator.nextInt();
		}
		while (keynum > transactioninsertkeysequence.lastInt());
		return keynum;
	}

	public void doTransactionRead(DB db)
	{
		//choose a random key
		//int keynum = nextKeynum();
		int keynum = nextKeynum2(selectKey);
		String keyname = buildKeyName(keynum);

		HashSet<String> fields=null;

		if (!readallfields)
		{
			//read a random field  
			String fieldname="field"+fieldchooser.nextString();

			fields=new HashSet<String>();
			fields.add(fieldname);
		}
		//db.read(table, keyname, new HashMap<String, ByteIterator>(), new HashMap<String, ByteIterator>());
		db.read(table,keyname,fields,new HashMap<String,ByteIterator>());
	}

	public void doTransactionReadModifyWrite(DB db)
	{
		//choose a random key
		int keynum = nextKeynum();

		String keyname = buildKeyName(keynum);

		HashSet<String> fields=null;

		if (!readallfields)
		{
			//read a random field  
			String fieldname="field"+fieldchooser.nextString();

			fields=new HashSet<String>();
			fields.add(fieldname);
		}

		HashMap<String,ByteIterator> values;

		if (writeallfields)
		{
			//new data for all the fields
			values = buildValues();
		}
		else
		{
			//update a random field
			values = buildUpdate();
		}

		//do the transaction

		long st=System.nanoTime();

		db.read(table,keyname,fields,new HashMap<String,ByteIterator>());

		db.update(table,keyname,values);

		long en=System.nanoTime();

		Measurements.getMeasurements().measure("READ-MODIFY-WRITE", (int)((en-st)/1000));
	}

	public void doTransactionScan(DB db)
	{
		//choose a random key
		int keynum = nextKeynum();
		//int keynum = nextKeynum2(scanKey);
		String startkeyname = buildKeyName(keynum);

		//choose a random scan length
		int len=scanlength.nextInt();

		HashSet<String> fields=null;

		if (!readallfields)
		{
			//read a random field  
			String fieldname="field"+fieldchooser.nextString();

			fields=new HashSet<String>();
			fields.add(fieldname);
		}

		db.scan(table,startkeyname,len,fields,new Vector<HashMap<String,ByteIterator>>());
	}
	//IntegerGenerator co=new UniformIntegerGenerator(1,200); 

	public void doTransactionUpdate(DB db)
	{
		//choose a random key
		//int keynum = nextKeynum();
		int keynum = nextKeynum2(updateKey);
		String keyname=buildKeyName(keynum);
		HashMap<String,ByteIterator> values;
		//Hashtable<String,Integer> value=new Hashtable<String,Integer>();

		if (writeallfields)
		{
			//new data for all the fields
			//values = buildValues();
			values = buildValue(updatebinds);
		}
		else
		{
			//update a random field
			//values = buildUpdate();
			values = buildValue(updatebinds);
			//value.put("field1",co.nextInt());
		}

		db.update(table,keyname,values);
	}

	public void doTransactionInsert(DB db)
	{
		//choose the next key
		int keynum=transactioninsertkeysequence.nextInt();

		String dbkey = buildKeyName(keynum);
		//HashMap<String, ByteIterator> values = buildValues();
		HashMap<String, ByteIterator> values = buildValue(insertbinds);
		//HashMap<String, ByteIterator> values = new HashMap<String, ByteIterator>();
		db.insert(table,dbkey,values);
	}
}
