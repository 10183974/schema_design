package duke.hbase.cm.tdg;

import java.io.BufferedWriter;

import duke.hbase.sd.Util;

public class DeleteQTDGenerator {

  private void generate(String string) {

  }

  public void generate() {
    System.out.println("generating training data from delete operation...");
    BufferedWriter bw = Util.getFileWriter("training_data/delete.csv");
    generate("customer");
    generate("orders");
    generate("supplier");
    generate("partsupp");
    generate("part");
    generate("lineitem");
    generate("nation");
    generate("region");
    Util.closeFileWriter(bw);

  }

}
