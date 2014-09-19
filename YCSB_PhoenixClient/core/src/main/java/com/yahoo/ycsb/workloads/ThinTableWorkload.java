package com.yahoo.ycsb.workloads;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Utils;
import com.yahoo.ycsb.WorkloadException;
import com.yahoo.ycsb.generator.UniformIntegerGenerator;
import com.yahoo.ycsb.generator.ZipfianGenerator;
import com.yahoo.ycsb.RandomByteIterator;
import com.yahoo.ycsb.StringByteIterator;


public class ThinTableWorkload extends OriginalCoreWorkload {

	/**
	 ** Number of users
	 **/
	public static final String USER_COUNT = "usercount";

	/**
	 ** Default value of user count
	 **/
	public static final String USER_COUNT_DEFAULT = "1000";

	/**
	 ** Number of regions
	 **/

	public static String ORDER_FIELD_SIZE = "orderfieldsize";
	public static String ORDER_FIELD_SIZE_DEFAULT = "2000";
	
	//public static final String ORDERCOUNT = "ordercount";

	/**
	 ** Default value of region count
	 **/
	//public static final String ORDER_COUNT_DEFAULT = "200";

	ZipfianGenerator useridsequence_zipflan;
	UniformIntegerGenerator useridsequence_uniform;

	UniformIntegerGenerator ordersequence;

	ConcurrentHashMap<String, Integer> userordercount = new ConcurrentHashMap<String,Integer>(); 

	int usercount = 0;
	int ordercount = 0;	
	String keyname="";
	int keynum=0;
	String requestdistrib;
	int  orderfieldsize;
	/**
	 ** Initialize the scenario. 
	 ** Called once, in the main client thread, before any operations are started.
	 **/
	public void init(Properties p) throws WorkloadException
	{
		super.init(p);
		System.out.println("initializing fat table workload...");
		usercount = Integer.parseInt(p.getProperty(USER_COUNT, USER_COUNT_DEFAULT));
		orderfieldsize = Integer.parseInt(p.getProperty(ORDER_FIELD_SIZE, ORDER_FIELD_SIZE_DEFAULT));
		//ordercount = Integer.parseInt(p.getProperty(ORDERCOUNT, ORDER_COUNT_DEFAULT));
		requestdistrib=p.getProperty(REQUEST_DISTRIBUTION_PROPERTY,REQUEST_DISTRIBUTION_PROPERTY_DEFAULT);
		useridsequence_zipflan=new ZipfianGenerator(usercount);
		useridsequence_uniform=new UniformIntegerGenerator(0, usercount-1);
		ordersequence=new UniformIntegerGenerator(0, ordercount);
	}

	public String buildKeyName(long keynum) {
		keyname = "user" + keynum;
		return keyname ;
	}

	public boolean doInsert(DB db, Object threadstate)
	{
		if (requestdistrib.equals("uniform"))
			keynum=useridsequence_uniform.nextInt();
		else if (requestdistrib.equals("zipfian"))
			keynum=useridsequence_zipflan.nextInt();                   

		String dbkey = buildKeyName(keynum);
		HashMap<String, ByteIterator> values = buildValues(dbkey);
		if(!userordercount.containsKey(dbkey)) {
			userordercount.put(dbkey,0);
		}
		int orderid = userordercount.get(dbkey);
		String fieldkey = "order" + (orderid+1);
		userordercount.put(dbkey, orderid+1);
		if (db.insert(table,dbkey+":"+fieldkey,values) == 0)
			return true;
		else
			return false;
	}
	
	public void doTransactionInsert(DB db)
	{
		if (requestdistrib.equals("uniform"))
			keynum=useridsequence_uniform.nextInt();
		else if (requestdistrib.equals("zipfian"))
			keynum=useridsequence_zipflan.nextInt();                   

		String dbkey = buildKeyName(keynum);
		
		if(!userordercount.containsKey(dbkey)) {
			userordercount.put(dbkey,0);
		}
		int orderid = userordercount.get(dbkey);
		String fieldkey = "order" + (orderid+1);
		userordercount.put(dbkey, orderid+1);
		HashMap<String, ByteIterator> values = buildValues(dbkey);
		db.insert(table,dbkey+":"+fieldkey,values);
	}

	HashMap<String, ByteIterator> buildValues(String dbkey) {

		HashMap<String,ByteIterator> values=new HashMap<String,ByteIterator>();
		ByteIterator data = new RandomByteIterator(orderfieldsize); 
		values.put("order",data);
		return values;
	}

	public void doTransactionRead(DB db)
	{
		Random randomGenerator = new Random();
		int size = userordercount.size();
		HashSet<String> fields= new HashSet<String>();
		if(size<=0) {
			fields.add("order"+3232);
			db.read(table,"user787",fields,new HashMap<String,ByteIterator>());
			return;
		}
		int userpos = randomGenerator.nextInt(userordercount.size());
		String username="";
		int ordernum=0;
		int count=0;
		for(String key: userordercount.keySet()) {
			if(count<userpos) {count++; continue;}
			ordernum = userordercount.get(key);
			username = key;
		}
		int orderid = randomGenerator.nextInt(ordernum);
		fields.add("order");
		db.read(table,username+":"+"order"+orderid,fields,new HashMap<String,ByteIterator>());
	}
}
