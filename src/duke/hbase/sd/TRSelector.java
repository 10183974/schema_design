package duke.hbase.sd;

import java.io.BufferedWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import duke.hbase.cm.CMDProxy;

public class TRSelector {

  private CMDProxy cm_read;
  private CMDProxy cm_write;
  private CMDProxy cm_update;
  private CMDProxy cm_scan;
  private CMDProxy cm_join;
  private BufferedWriter bw_read;
  private BufferedWriter bw_write;
  private BufferedWriter bw_update;
  private BufferedWriter bw_scan;
  private BufferedWriter bw_join;

  public TRSelector() throws Exception {
    /*
     * try{ cm_read = new CMDProxy("cm-1.2/bin/cm.sh", "read_training_data.csv"); cm_write = new
     * CMDProxy("cm-1.2/bin/cm.sh", "write_training_data.csv"); cm_update = new
     * CMDProxy("cm-1.2/bin/cm.sh", "update_training_data.csv"); cm_scan = new
     * CMDProxy("cm-1.2/bin/cm.sh", "scan_training_data.csv"); cm_join = new
     * CMDProxy("cm-1.2/bin/cm.sh", "join_training_data.csv"); bw_read = cm_read.GetInputWriter();
     * bw_write = cm_write.GetInputWriter(); bw_update = cm_update.GetInputWriter(); bw_scan =
     * cm_scan.GetInputWriter(); bw_join = cm_join.GetInputWriter(); } catch(Exception e) { throw
     * new Exception("Error: while starting the cost model"); }
     */
  }

  public void finalize() {
    cm_read.stop();
    cm_write.stop();
    cm_update.stop();
    cm_scan.stop();
    cm_join.stop();
  }

  public Double cost(Application app) {
    return new Double(app.toString().length());
  }

  public Transformation select(Application _app) throws Exception {
    System.out.println("Inside TRSelector.select()");
    System.out.println("printing argument app");
    System.out.println(_app.toShortString());
    Transformation selected_transformation = null;
    Double selected_app_cost = Double.MAX_VALUE;
    RuleBasedTREnumerator rte = new RuleBasedTREnumerator();
    ArrayList<Transformation> transformations = rte.enumerate(_app);

    Iterator<Transformation> tr_itr = transformations.iterator();
    Application app = null;
    while (tr_itr.hasNext()) {
      Transformation tr = tr_itr.next();
      System.out.println("applying transformation below");
      System.out.println(tr.toShortString());
      Method tr_method = tr.getTransformationRule();
      ArrayList<Object> tr_args = tr.getArguments();
      Class<?> tm = Class.forName("duke.hbase.sd.TransformationMethods");

      if ("join".equals(tr.getType()) || "nesting".equals(tr.getType())) {
        app =
            (Application) tr_method.invoke(tm.newInstance(), _app, tr.getQ(), tr_args.get(0),
              tr_args.get(1), tr_args.get(2), tr_args.get(3));
      }
      System.out.println("application after transformation");
      System.out.println(app.toShortString());
      Double new_cost = cost(app);
      if (new_cost < selected_app_cost) {
        selected_app_cost = new_cost;
        selected_transformation = tr;
      }
    }
    return selected_transformation;
  }

  public CMDProxy getCm_read() {
    return cm_read;
  }

  public void setCm_read(CMDProxy cm_read) {
    this.cm_read = cm_read;
  }

  public CMDProxy getCm_write() {
    return cm_write;
  }

  public void setCm_write(CMDProxy cm_write) {
    this.cm_write = cm_write;
  }

  public CMDProxy getCm_update() {
    return cm_update;
  }

  public void setCm_update(CMDProxy cm_update) {
    this.cm_update = cm_update;
  }

  public CMDProxy getCm_scan() {
    return cm_scan;
  }

  public void setCm_scan(CMDProxy cm_scan) {
    this.cm_scan = cm_scan;
  }

  public CMDProxy getCm_join() {
    return cm_join;
  }

  public void setCm_join(CMDProxy cm_join) {
    this.cm_join = cm_join;
  }

  public BufferedWriter getBw_read() {
    return bw_read;
  }

  public void setBw_read(BufferedWriter bw_read) {
    this.bw_read = bw_read;
  }

  public BufferedWriter getBw_write() {
    return bw_write;
  }

  public void setBw_write(BufferedWriter bw_write) {
    this.bw_write = bw_write;
  }

  public BufferedWriter getBw_update() {
    return bw_update;
  }

  public void setBw_update(BufferedWriter bw_update) {
    this.bw_update = bw_update;
  }

  public BufferedWriter getBw_scan() {
    return bw_scan;
  }

  public void setBw_scan(BufferedWriter bw_scan) {
    this.bw_scan = bw_scan;
  }

  public BufferedWriter getBw_join() {
    return bw_join;
  }

  public void setBw_join(BufferedWriter bw_join) {
    this.bw_join = bw_join;
  }

  public static void main(String[] args) throws Exception {
    Application _app =
        Util.initApplication(new String[] { "workdir/schema.xml", "workdir/workload.xml" });
    TRSelector trs = new TRSelector();
    Transformation tr = trs.select(_app);

    // System.out.println(tr);
  }

}
