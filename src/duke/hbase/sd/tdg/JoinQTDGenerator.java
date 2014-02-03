package duke.hbase.sd.tdg;

import java.io.BufferedWriter;

import duke.hbase.sd.Util;

public class JoinQTDGenerator {

  public void generate() {
    System.out.println("generating training data from join operation...");
    BufferedWriter bw = Util.getFileWriter("training_data/join.csv");
    Util.closeFileWriter(bw);
  }

}
