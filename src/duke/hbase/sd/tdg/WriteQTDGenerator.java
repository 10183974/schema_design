package duke.hbase.sd.tdg;

import java.io.BufferedWriter;

import duke.hbase.sd.Util;

public class WriteQTDGenerator {

  public void generate() {
    System.out.println("generating training data from write operation...");
    BufferedWriter bw = Util.getFileWriter("training_data/write.csv");
    Util.closeFileWriter(bw);
  }

}
