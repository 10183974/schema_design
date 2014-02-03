package duke.hbase.sd.tdg;


public class TrainingDataGeneratorForTPCH {

  public static void main(String[] args) {

    if ("join".equals(args[0]) || "all".equals(args[0])) {
      JoinQTDGenerator g = new JoinQTDGenerator();
      g.generate();
    }

    if ("scan".equals(args[0]) || "all".equals(args[0])) {
      ScanQTDGenerator g = new ScanQTDGenerator();
      g.generate();
    }

    if ("read".equals(args[0]) || "all".equals(args[0])) {
      ReadQTDGenerator g = new ReadQTDGenerator();
      g.generate();
    }

    if ("write".equals(args[0]) || "all".equals(args[0])) {
      WriteQTDGenerator g = new WriteQTDGenerator();
      g.generate();
    }
    if ("delete".equals(args[0]) || "all".equals(args[0])) {
      DeleteQTDGenerator g = new DeleteQTDGenerator();
      g.generate();
    }
  }
}
