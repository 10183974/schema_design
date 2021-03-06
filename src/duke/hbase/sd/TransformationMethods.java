package duke.hbase.sd;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TJoinItemList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TransformationMethods {

  public Table singleColf2multiColf(Table _t) {
    Table t = (Table) _t.clone();

    return null;
  }

  public Table multiColf2singleColf() {
    return null;
  }

  public Table colValuePromo2rowkey() {
    return null;
  }

  public Table uniqueValPromo2rowkey() {
    return null;
  }

  public Table colValuePromo2colkey() {
    return null;
  }

  public Table keySalting() {
    return null;
  }

  private String substitute(String token, String t1name, String t1alias, String t2name,
      String t2alias, String tname, String talias) {
    if (token.equals(t1name) || token.equals(t2name)) {
      return tname;
    } else if (token.equals(t1alias) || token.equals(t2alias)) {
      return talias;
    }
    return token;
  }

  // replace t1 and t2 in query by t
  private String renameTable(TGSqlParser sqlparser, String t1, String t2, String t) {
    System.out.println("Before rewrite: " + sqlparser.sqltext);
    TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
    TSourceTokenList stl = qstmt.sourcetokenlist;
    StringBuffer q_r = new StringBuffer();
    while (stl.hasNext()) {
      String token = stl.next().toString();
      if (token.equals(t1) || token.equals(t2)) {
        q_r.append(t);
      } else {
        q_r.append(token);
      }

    }
    System.out.println("After rewrite: " + q_r);
    return q_r.toString();
  }

  // replace t1 and t2 in query by t
  // remove the join clause between these two table
  // rewrite the alias
  private void renameTablesAndRemoveJoinItem(TGSqlParser sqlparser, Query q, Table t1, Table t2,
      String t, String from) {
    System.out.println("Before rewrite: " + sqlparser.sqltext);
    String talias = Util.getRandomString();
    String _t1alias = null, _t2alias = null;
    TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
    TJoin full_join_clause = qstmt.joins.getJoin(0);
    TTable first_t = full_join_clause.getTable();
    if (first_t.toString().equals(t1.getName())) {
      _t1alias = first_t.getAliasClause().toString();
    }
    if (first_t.toString().equals(t2.getName())) {
      _t2alias = first_t.getAliasClause().toString();
    }
    TJoinItemList join_items = full_join_clause.getJoinItems();
    for (int i = 0; i < join_items.size(); i++) {
      TJoinItem join_item = (TJoinItem) join_items.elementAt(i);
      TTable next_t = join_item.getTable();
      if (next_t.getTableName().toString().equals(t1.getName())) {
        _t1alias = next_t.getAliasClause().toString();
      }
      if (next_t.getTableName().toString().equals(t2.getName())) {
        _t2alias = next_t.getAliasClause().toString();
      }
    }

    TSourceTokenList stl = qstmt.sourcetokenlist;
    StringBuffer q_r = new StringBuffer();
    ArrayList<StringBuffer> sq_r = new ArrayList<StringBuffer>();
    boolean joinItemStarted = false;
    boolean whereStarted = false;
    int joinItemIndex = -1;
    while (stl.hasNext()) {
      String token = stl.next().toString();

      if ("join".equals(token)) {
        joinItemStarted = true;
        joinItemIndex++;
      }
      if ("where".equals(token)) {
        joinItemStarted = false;
        whereStarted = true;
        for (StringBuffer ji : sq_r) {
          // System.out.println("debug " + ji.toString().split("\\s"));
          String tname = ji.toString().split("\\s")[1];
          if (!t.equals(tname)) {
            q_r.append(ji);
          }
        }
      }
      if (joinItemStarted) {
        if (sq_r.size() - 1 < joinItemIndex) {
          sq_r.add(new StringBuffer());
        }
        sq_r.get(joinItemIndex).append(
          substitute(token, t1.getName(), _t1alias, t2.getName(), _t2alias, t, talias));
      } else {
        q_r.append(substitute(token, t1.getName(), _t1alias, t2.getName(), _t2alias, t, talias));
      }
      // System.out.println("--->" + q_r);
    }
    if (!whereStarted) {
      for (StringBuffer ji : sq_r) {
        // System.out.println("debug " + ji.toString().split("\\s"));
        String tname = ji.toString().split("\\s")[1];
        if (!t.equals(tname)) {
          q_r.append(ji);
        }
      }
    }

    if (sq_r.size() == 0) {
      q.setType("scan");
    }
    System.out.println("After rewrite: " + q_r.toString());
    q.setQuerystr(q_r.toString());
  }

  private String updateNameAndFields(TGSqlParser sqlparser, Table t1, Table t2,
      ArrayList<Column> t1_jkeys, ArrayList<Column> t2_jkeys, String t) {
    System.out.println("Before rewrite: " + sqlparser.sqltext);
    StringBuffer sb = new StringBuffer();
    sb.append("insert into " + t + " values (");
    for (int i = 0; i < t1.getColumns().size() + t2.getColumns().size() - t1_jkeys.size(); i++) {
      sb.append("?, ");
    }
    sb.delete(sb.length() - 2, sb.length());
    sb.append(")");
    System.out.println("After rewrite: " + sb.toString());
    return sb.toString();
  }

  private ArrayList<Column> scanByColumns(String q, Table t) throws Exception {

    ArrayList<Column> cols = new ArrayList<Column>();

    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
    sqlparser.sqltext = q;
    int ret = sqlparser.parse();
    if (ret == 0) {
      TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
      TWhereClause where = qstmt.getWhereClause();
      TSourceTokenList stl = new TSourceTokenList();
      where.addAllMyTokensToTokenList(stl, 0);
      while (stl.hasNext()) {
        String token = stl.next().toString();
        Column col = t.getColumnByName(token);
        if (col != null) {
          //System.out.println(col.getName());
          cols.add(col);
        }
      }
      // System.out.println(where.toString());
    }
 else {
      System.out.println(sqlparser.getErrormessage());
      throw new Exception("Error in parsing query: " + q);
    }
    return cols;
  }

  private boolean isScanByTRowkey(Query q, ArrayList<Column> t_jkeys) {
    boolean isScanByTRowkey = false;
    String q_str = q.getQuerystr();
    // check where clause

    return isScanByTRowkey;
  }

  private void rewriteQueries(Application _app, Application app, Table t1, Table t2,
      ArrayList<Column> t1_jkeys, ArrayList<Column> t2_jkeys, String from) throws Exception {
    Integer[] cardinality = app.getCardinality(t1.getName(), t2.getName());
    Iterator<Query> _q_itr = _app.getQueries().iterator();
    app.setQueries(new ArrayList<Query>());

    while (_q_itr.hasNext()) {
      Query _q = _q_itr.next();
      Query q = _q.clone();

      boolean ist1inQuery = _q.getQuerystr().matches("^.*\\s" + t1.getName() + "\\s.*$");
      boolean ist2inQuery = _q.getQuerystr().matches("^.*\\s" + t2.getName() + "\\s.*$");

      // added table
      String t_n = t1.getName() + "_" + t2.getName();
      Table t = app.getTables().get(t_n);

      // rewrite the Query q
      if (ist1inQuery || ist2inQuery) {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = q.getQuerystr();
        int ret = sqlparser.parse();

        if (ret != 0) {
          throw new Exception("Error: parsing error " + q);
        }

        switch (_q.getType()) {
        case "read":
          q.setQuerystr(renameTable(sqlparser, t1.getName(), t2.getName(), t_n));
          if (ist1inQuery) {
            if ("join".equals(from)) {
              q.setType("scan");
            } else if ("nesting".equals(from)) {
              q.setType("read");
            }
          } else if (ist2inQuery) {
            if ("join".equals(from)) {
              q.setType("read");
            } else if ("nesting".equals(from)) {
              q.setType("scan");
            }
          }
          break;
        case "scan":
          q.setQuerystr(renameTable(sqlparser, t1.getName(), t2.getName(), t_n));
          if ("join".equals(from)) {
            q.setType("scan");
          } else if ("nesting".equals(from)) {
            // in one case query can become read on fat table
            // horizontal scan
            if (ist2inQuery) {
              ArrayList<Column> scancols = scanByColumns(q.getQuerystr(), t2);
              boolean allInJkey = true;
              for(Column scancol : scancols) {
                if(!t2_jkeys.contains(scancol)) {
                  allInJkey = false;
                }
              }
              if (allInJkey) {
                q.setType("read");
              }
            } else {
              q.setType("scan");
            }
          }
          break;
        case "write":
          q.setQuerystr(updateNameAndFields(sqlparser, t1, t2, t1_jkeys, t2_jkeys, t_n));
          break;
        case "update":
          // not handled correctly
          q.setQuerystr(updateNameAndFields(sqlparser, t1, t2, t1_jkeys, t2_jkeys, t_n));
          break;
        case "join":
          renameTablesAndRemoveJoinItem(sqlparser, q, t1, t2, t_n, from);
          if(q.getTables(app).size()==1) {
        	  q.setType("scan");
          }
          else {
        	  q.setType("join");
          }
          break;
        default:
          System.out.println("Error: invalid query type " + _q);
        }
      }
      app.getQueries().add(q);
    }
  }

  private void removeConcernedRelation(Application app, Table t1, Table t2) {
    Iterator<String> r_itr = app.getRels().keySet().iterator();
    // String cardinalityOfJoin = "";
    Relation _joinrel = null;
    String _joinrelkey = null;
    while (r_itr.hasNext()) {
      _joinrelkey = r_itr.next();
      _joinrel = app.getRels().get(_joinrelkey);
      if ((_joinrel.getT1().getName().equals(t1.getName()) && _joinrel.getT2().getName()
          .equals(t2.getName()))
          || (_joinrel.getT2().getName().equals(t1.getName()) && _joinrel.getT1().getName()
              .equals(t2.getName()))) {
        // cardinalityOfJoin = _joinrel.getCardinality();
      }
    }

    app.getRels().remove(_joinrelkey);
  }

  public Application join(Application _app, Query tr_q, Table t1, ArrayList<Column> t1_jkeys,
      Table t2, ArrayList<Column> t2_jkeys) throws Exception {

    System.out.println("Tables being joined: table1=" + t1.getName() + " table2=" + t2.getName());
    Application app = _app.clone();
    Integer[] cardinality = app.getCardinality(t1.getName(), t2.getName());

    // swap if cardinality not 1:m
    if (cardinality[0] == 1) {
    } else if (cardinality[1] == 1) {
      Table temp = t1;
      t1 = t2;
      t2 = temp;

      ArrayList<Column> temp_jkeys = t1_jkeys;
      t1_jkeys = t2_jkeys;
      t2_jkeys = temp_jkeys;
    } else {
      throw new Exception("cardinality " + cardinality[0] + ":" + cardinality[1]
          + " are found between " + t1.getName() + " and " + t2.getName());
    }

    // 1. rewrite the Queries
    rewriteQueries(_app, app, t1, t2, t1_jkeys, t2_jkeys, "join");

    // 2. remove this relation between t1 and t2
    removeConcernedRelation(app, t1, t2);

    // 3. add the new join table
    Table t = t2.clone();
    t.setName(t1.getName() + "_" + t2.getName());
    for (String colkey : app.getTables().get(t1.getName()).getColumns().keySet()) {
      if (!t1_jkeys.contains(app.getTables().get(t1.getName()).getColumns().get(colkey))) {
        t.getColumns().put(colkey, t1.getColumns().get(colkey).clone());
      }
    }
    app.getTables().put(t.getName(), t);

    // 4. re-wiring the relationships
    for (String _rel : app.getRels().keySet()) {
      Relation _relation = app.getRels().get(_rel);
      if (_relation.getT1().getName().equals(t1.getName())
          || _relation.getT1().getName().equals(t2.getName())) {
        app.getRels().get(_rel).setT1(t);
      }

      if (_relation.getT2().getName().equals(t1.getName())
          || _relation.getT2().getName().equals(t2.getName())) {
        app.getRels().get(_rel).setT2(t);
      }
    }

    // 5. deleting old tables
    app.getTables().remove(t1.getName());
    app.getTables().remove(t2.getName());

    // 4. setting up query type
    // setQueryType(app);

    // 5. reestimating the statistics based on query type
    // estimateStatistics(app);

    return app;
  }

  public ArrayList<Table> split(Table t) {
    ArrayList<Table> ts = new ArrayList<Table>();
    return ts;
  }

  public Application nesting(Application _app, Query tr_q, Table t1, ArrayList<Column> t1_jkeys,
      Table t2, ArrayList<Column> t2_jkeys) throws Exception {

    System.out.println("Tables being nested: table1=" + t1.getName() + " table2=" + t2.getName());
    Application app = _app.clone();
    Integer[] cardinality = app.getCardinality(t1.getName(), t2.getName());

    // swap if cardinality not 1:m
    if (cardinality[0] == 1) {
    } else if (cardinality[1] == 1) {
      Table temp = t1;
      t1 = t2;
      t2 = temp;

      ArrayList<Column> temp_jkeys = t1_jkeys;
      t1_jkeys = t2_jkeys;
      t2_jkeys = temp_jkeys;

      cardinality[1] = cardinality[0];
      cardinality[0] = 1;
    } else {
      throw new Exception("cardinality " + cardinality[0] + ":" + cardinality[1]
          + " found between " + t1.getName() + " and " + t2.getName());
    }

    // 1. add the new join table
    Table t = t1.clone();
    t.setName(t1.getName() + "_" + t2.getName());
    HashMap<String, SuperColumn> scl = t.getSuperColumns();

    SuperColumn sc = new SuperColumn();
    sc.setId(SuperColumn.getSuperColumnId());
    sc.setName(t2.getName());
    sc.setColumns(t2.clone().getColumns());
    ArrayList<Column> columnkey = new ArrayList<Column>();
    for (Column c : t2.getRowkey()) {
      columnkey.add(sc.getColumns().get(c.getFamily() + c.getName()));
    }

    sc.setNumberOfSuperColumnPerRow(cardinality[1]);

    scl.put(t2.getName(), sc);
    app.getTables().put(t.getName(), t);

    // 2. rewrite the Queries
    rewriteQueries(_app, app, t1, t2, t1_jkeys, t2_jkeys, "nesting");

    // 3. remove this relation between t1 and t2
    removeConcernedRelation(app, t1, t2);

    // 4. re-wiring the relationships
    // not complete
    for (String _rel : app.getRels().keySet()) {
      Relation _relation = app.getRels().get(_rel);
      if (_relation.getT1().getName().equals(t1.getName())
          || _relation.getT1().getName().equals(t2.getName())) {
        app.getRels().get(_rel).setT1(t);
      }

      if (_relation.getT2().getName().equals(t1.getName())
          || _relation.getT2().getName().equals(t2.getName())) {
        app.getRels().get(_rel).setT2(t);
      }
    }

    // 5. deleting old tables
    app.getTables().remove(t1.getName());
    app.getTables().remove(t2.getName());

    // 4. setting up query type
    // setQueryType(app);

    // 5. reestimating the statistics based on query type
    // estimateStatistics(app);

    return app;
  }

  public ArrayList<Table> denesting(Table t) {
    return null;
  }

  public static void main(String[] args) throws Exception {
    Application app =
        Util.initApplication(new String[] { "workdir/schema.xml", "workdir/workload.xml" });
    String q =
        "select C_CUSTKEY, C_ADDRESS from CUSTOMER where C_ADDRESS='campus walk' and C_NATIONKEY=10";
    TransformationMethods tm = new TransformationMethods();
    ArrayList<Column> cols = tm.scanByColumns(q, app.getTables().get("CUSTOMER"));
    System.out.println(cols);

    String q1 = "select O_ORDERKEY from CUSTOMER_ORDERS where O_ORDERSTATUS='1'";
    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
    sqlparser.sqltext = q1;
    int ret = sqlparser.parse();
    if (ret == 0) {
      System.out.println("success");
      TSelectSqlStatement qstmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
      TWhereClause where = qstmt.getWhereClause();
      TSourceTokenList stl = new TSourceTokenList();
    } else {
      System.out.println(sqlparser.getErrormessage());
      throw new Exception("Error in parsing query: " + q1);
    }
  }

}
