package duke.hbase.sd.tdg;

import java.io.BufferedWriter;

import duke.hbase.sd.Util;

public class ReadQTDGenerator {

  public void generate() {
    System.out.println("generating training data from read operation...");
    BufferedWriter bw = Util.getFileWriter("training_data/read.csv");
    Util.closeFileWriter(bw);
  }

}
