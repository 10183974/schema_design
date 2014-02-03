package duke.hbase.sd.tdg;

import java.io.BufferedWriter;

import duke.hbase.sd.Util;

public class DeleteQTDGenerator {

  public void generate() {
    System.out.println("generating training data from delete operation...");
    BufferedWriter bw = Util.getFileWriter("training_data/delete.csv");
    Util.closeFileWriter(bw);

  }

}
