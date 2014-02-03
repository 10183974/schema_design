package duke.hbase.sd.tdg;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.util.Properties;

import duke.hbase.sd.Util;

public class JoinQTDGenerator {
  BufferedWriter bw;
  Connection conn = null;

  // client thread, server thread, table1 row count, table1 rowkey size, table2 row count, table2
  // rowkey size, cardinality
  public void generate(String t1, String t2, String[] t1_cols, String[] t2_cols) {

  }

  public void generate() {
    System.out.println("generating training data for join operation...");
    Properties prop = new Properties();
    conn = Util.getConnection(prop);
    bw = Util.getFileWriter("training_data/join.csv");
    generate("customer", "orders", new String[] { "c_custkey" }, new String[] { "o_custkey" });
    generate("orders", "lineitem", new String[] { "o_orderkey" }, new String[] { "l_orderkey" });
    generate("lineitem", "part", new String[] { "l_partkey" }, new String[] { "p_partkey" });
    generate("part", "partsupp", new String[] { "p_partkey" }, new String[] { "ps_partkey" });
    generate("partsupp", "lineitem", new String[] { "ps_partkey, ps_suppkey" }, new String[] {
        "l_partkey", "l_suppkey" });
    generate("partsupp", "supplier", new String[] { "ps_suppkey" }, new String[] { "s_suppkey" });
    generate("supplier", "nation", new String[] { "s_nationkey" }, new String[] { "n_nationkey" });
    generate("nation", "customer", new String[] { "n_nationkey" }, new String[] { "c_nationkey" });
    generate("nation", "region", new String[] { "n_regionkey" }, new String[] { "r_nationkey" });
    Util.closeFileWriter(bw);
  }
}
