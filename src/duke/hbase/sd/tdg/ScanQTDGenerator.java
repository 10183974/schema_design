package duke.hbase.sd.tdg;

import java.io.BufferedWriter;

import duke.hbase.sd.Util;

public class ScanQTDGenerator {

  public void generate() {
    System.out.println("generating training data from scan operation...");
    BufferedWriter bw = Util.getFileWriter("training_data/scan.csv");
    Util.closeFileWriter(bw);
  }

}
