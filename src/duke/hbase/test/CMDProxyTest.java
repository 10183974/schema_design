package duke.hbase.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.junit.Test;

import duke.hbase.cm.CMDProxy;

public class CMDProxyTest {

  @Test
  public void test() throws IOException {
    CMDProxy cm =
        new CMDProxy(
"cm-1.2/bin/cm.sh",
            "join_training_data.csv");
    BufferedWriter bw = cm.GetInputWriter();

    String querystr =
        "12" + "\t" + "4" + "\t" + "10000" + "\t" + "2.86" + "\t" + "10000" +
 "\t" + "2.86" + "\t"
            + "10000" + "\n";
    bw.write(querystr, 0, querystr.length());
    bw.flush();

    String a = null;

    while (a == null) {
      BufferedReader br = cm.GetOutputReader();
      a = (String) br.readLine();
      System.out.println("latency " + a);
    }
    
    a = null;

    querystr =
        "12" + "\t" + "4" + "\t" + "20000" + "\t" + "2.86" + "\t" + "20000" + "\t" + "2.86" + "\t"
            + "20000" + "\n";
    bw.write(querystr, 0, querystr.length());
    bw.flush();

    while (a == null) {
    BufferedReader br = cm.GetOutputReader();
      a = (String) br.readLine();
      System.out.println("latency " + a);
    }
    cm.stop();
  }

}
