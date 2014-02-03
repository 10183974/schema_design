package duke.hbase.sd.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.junit.Test;

import duke.hbase.sd.CMDProxy;

public class CMDProxyTest {

  @Test
  public void test() throws IOException {
    CMDProxy cm =
        new CMDProxy(
"cm-1.2/bin/cm.sh",
            "/home/abhisdub/git/abhishek1015/schema_design/join_training_data.csv");
    BufferedWriter bw = cm.GetInputWriter();

    String querystr =
        "12" + "\t" + "4" + "\t" + "1999" + "\t" + "2.86" + "\t" + "7996" +
 "\t" + "2.86" + "\t"
            + "1998" + "\n";
    bw.write(querystr, 0, querystr.length());
    bw.flush();
    String a = null;
    while (a == null) {
    BufferedReader br = cm.GetOutputReader();
      a = (String) br.readLine();
      System.out.println("latency " + a);
    }
    cm.stop();
  }

}
