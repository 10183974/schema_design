package duke.hbase.sd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Method;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.HashMap;

public class RuleBasedTREnumerator {
	
	private class JoinExprVisitor implements IExpressionVisitor {
		private HashMap<String, ArrayList<String>> keys = new HashMap<String, ArrayList<String>>();
	    private String l_key;
	    private String r_key;
	    private int count_leaf = 0;
		public boolean exprVisit(TParseTreeNode pNode,boolean isLeafNode){
	         String sign = "";
	        if (isLeafNode){
	        	if(count_leaf==0) {
	        		l_key = pNode.toString();
	        		count_leaf++;
	        	}
	        	else {
	        		r_key = pNode.toString();
	        		count_leaf++;
	        	}
	            sign ="*";
	        } else {
	        	if(count_leaf==2) {
	        		String l_alias = l_key.split("\\.")[0];
	        		String l_column = l_key.split("\\.")[1];
	        		String r_alias = r_key.split("\\.")[0];
	        		String r_column = r_key.split("\\.")[1];
	        		if(keys.containsKey(l_alias)) {
	        			ArrayList<String> cols = keys.get(l_alias);
	        			cols.add(l_column);
	        		}
	        		else {
	        			ArrayList<String> cols = new ArrayList<String>();
	        			cols.add(l_column);
	        			keys.put(l_alias, cols);
	        		}
	        		if(keys.containsKey(r_alias)) {
	        			ArrayList<String> cols = keys.get(r_alias);
	        			cols.add(r_column);
	        		}
	        		else {
	        			ArrayList<String> cols = new ArrayList<String>();
	        			cols.add(r_column);
	        			keys.put(r_alias, cols);
	        		}
	        		count_leaf=0;
	        	}
	        }
	         //System.out.println(sign+pNode.getClass().toString()+" "+ pNode.toString());
	        return true;
	    };
	}
	
	private boolean isValid(Application app, Transformation tr) {
		boolean isValid = false;
		if(tr.getType().equals("join")) {
			Table t1 = (Table)tr.getArguments().get(0);
			@SuppressWarnings("unchecked")
			ArrayList<Column> t1_jkeys = (ArrayList<Column>) tr.getArguments().get(1);
			Table t2 = (Table) tr.getArguments().get(2);
			@SuppressWarnings("unchecked")
			ArrayList<Column> t2_jkeys = (ArrayList<Column>) tr.getArguments().get(3);
			if(t1.areColumns(t1_jkeys) && t2.areColumns(t2_jkeys)) {
				isValid = true;
			}
		}
		if(tr.getType().equals("nesting")) {
			Table t1 = (Table)tr.getArguments().get(0);
			@SuppressWarnings("unchecked")
			ArrayList<Column> t1_jkeys = (ArrayList<Column>) tr.getArguments().get(1);
			Table t2 = (Table) tr.getArguments().get(2);
			@SuppressWarnings("unchecked")
			ArrayList<Column> t2_jkeys = (ArrayList<Column>) tr.getArguments().get(3);
			
			Integer[] cardinality = app.getCardinality(t1.getName(), t2.getName());
	
			if(cardinality[0]==1){
				if(t1.areRowKeys(t1_jkeys) && t2.areColumns(t2_jkeys)) {
					isValid = true;
				}
			}
			if(cardinality[1]==1) {
				if(t2.areRowKeys(t2_jkeys) && t1.areColumns(t1_jkeys)) {
					isValid = true;
				}
			}
		}
		return isValid;
	}
	
	private Collection<? extends Transformation> generateJoinAndNestingTR(Application app, Query q) throws Exception{
		ArrayList<Transformation> tr_list = new ArrayList<Transformation>();
		HashMap<String, String> tables = new HashMap<String, String>();
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
		sqlparser.sqltext = q.getQuerystr();
		int ret = sqlparser.parse();
		if(ret==0) {
			TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
			TJoin full_join_clause = qstmt.joins.getJoin(0);
			TTable first_t = full_join_clause.getTable();
			//System.out.println("first table " + first_t.getName() + " " + first_t.getAliasClause());
			tables.put(first_t.getAliasClause().toString(), first_t.getName());
			TJoinItemList join_items = full_join_clause.getJoinItems();
			//System.out.println("join item count " + join_items.size());
			for(int i=0; i<join_items.size(); i++) {
				Transformation j_tr = new Transformation();
				Transformation n_tr = new Transformation();
				j_tr.setQ(q);
				n_tr.setQ(q);
				Method join = Class.forName("duke.hbase.sd.TransformationMethods").
						getDeclaredMethod("join", Application.class, Query.class, Table.class, ArrayList.class, Table.class, ArrayList.class);
				Method nesting = Class.forName("duke.hbase.sd.TransformationMethods").
						getDeclaredMethod("nesting", Application.class, Query.class, Table.class, ArrayList.class, Table.class, ArrayList.class);
				j_tr.setTransformationRule(join);
				j_tr.setType("join");
				n_tr.setTransformationRule(nesting);
				n_tr.setType("nesting");
				TJoinItem join_item = (TJoinItem) join_items.elementAt(i);
				//System.out.println(i + "-->" + join_item.toString());
				TTable next_t = join_item.getTable();
				tables.put(next_t.getAliasClause().toString(), next_t.getName());
				TExpression t_exp = join_item.getOnCondition();
				JoinExprVisitor ev = new JoinExprVisitor();
				t_exp.postOrderTraverse(ev);
				Iterator<String> e_itr = ev.keys.keySet().iterator();
				Table t1 = null, t2 = null;
				ArrayList<Object> args = new ArrayList<Object>();
				while(e_itr.hasNext()) {
					String alias = e_itr.next();
					ArrayList<String> cols = ev.keys.get(alias);
					//System.out.println( alias + "(" + cols + ")-> " +  tables.get(alias));
					if(t1==null) {
						t1 = app.getTables().get(tables.get(alias));
					    Iterator<String> jkey_itr = cols.iterator();
					    ArrayList<Column> t1_jkeys = new ArrayList<Column>();
					    while(jkey_itr.hasNext()) {
					    	String c = jkey_itr.next();
					    	Column col = app.getTables().get(tables.get(alias)).getColumns().get(Column.DEFAULT_FAMILY+c);
					    	t1_jkeys.add(col);
					    }
						args.add(t1);
					    args.add(t1_jkeys);
					} else if(t2==null) {
						t2 = app.getTables().get(tables.get(alias));
					    Iterator<String> jkey_itr = cols.iterator();
					    ArrayList<Column> t2_jkeys = new ArrayList<Column>();
					    while(jkey_itr.hasNext()) {
					    	Column col = app.getTables().get(tables.get(alias)).getColumns().get(Column.DEFAULT_FAMILY+ jkey_itr.next());
					    	t2_jkeys.add(col);
					    }
					    args.add(t2);
					    args.add(t2_jkeys);
					}
				}
				j_tr.setArguments(args);
				n_tr.setArguments(args);
				if(isValid(app, j_tr)) {
					tr_list.add(j_tr);
				}
				if(isValid(app, n_tr)) {
					tr_list.add(n_tr);
				}
			}
		}
		else {
			throw new Exception("Error: syntex error in " + q);
		}
		return tr_list;
	}

	private Collection<? extends Transformation> 
	enumerateForJoinQ(Application app, Query q) throws Exception {
		ArrayList<Transformation> tr_list = new ArrayList<Transformation>();
		tr_list.addAll(generateJoinAndNestingTR(app, q));
		return tr_list;
	}

	private Collection<? extends Transformation> enumerateForScanQ(
			Application app, Query q) {
		ArrayList<Transformation> tr_list = new ArrayList<Transformation>();
		return tr_list;
	}

	private Collection<? extends Transformation> enumerateForUpdateQ(
			Application app, Query q) {
		ArrayList<Transformation> tr_list = new ArrayList<Transformation>();
		return tr_list;
	}

	private Collection<? extends Transformation> enumerateForWriteQ(
			Application app, Query q) {
		ArrayList<Transformation> tr_list = new ArrayList<Transformation>();
		return tr_list;
	}

	private Collection<? extends Transformation> enumerateForReadQ(Application app, Query q) {
		ArrayList<Transformation> tr_list = new ArrayList<Transformation>();
		return tr_list;	
	}
	
	public ArrayList<Transformation> enumerate(Application app) throws Exception {
		
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();
		
		Iterator<Query> q_itr = app.getQueries().iterator();
		while(q_itr.hasNext()) {
			Query q = q_itr.next();
			switch(q.getType()) {
			case "read":
				transformations.addAll(enumerateForReadQ(app, q));
				break;
			case "write":
				transformations.addAll(enumerateForWriteQ(app, q));
			break;
			case "update":
				transformations.addAll(enumerateForUpdateQ(app, q));
			break;	
			case "scan":
				transformations.addAll(enumerateForScanQ(app, q));
				break;
			case "join":
				transformations.addAll(enumerateForJoinQ(app, q));
				break;
			default:
				throw new Exception("Invalid query type");
			}
			
		}
	
		System.out.println("Rulebased enumerator suggests " + transformations.size() + " transformations");
		return transformations;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		RuleBasedTREnumerator rbe = new RuleBasedTREnumerator();
		Application app = Util.initApplication(new String[] {"workdir/schema.xml", "workdir/workload.xml"});
		Iterator<Query> q_itr = app.getQueries().iterator();
		while(q_itr.hasNext()) {
			Query q = q_itr.next();
			if("join".equals(q.getType())) {
				ArrayList<Transformation> tr_list = (ArrayList<Transformation>) rbe.generateJoinAndNestingTR(app, q);
				Iterator<Transformation> tr_itr = tr_list.iterator();
				while(tr_itr.hasNext()) {
					Transformation tr = tr_itr.next();
					System.out.println(tr);
				}		
			}
		}
	}
	
	
}
