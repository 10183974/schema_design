package duke.hbase.cm.tdg;

public class PdgfDataGenerator {
	 private String PDGF_HOME = System.getenv("PROJECT_HOME") + "/pdgf";
	 private String xmlFile = null;
     public void generate(){
          String[] cmd = new String[]{"java","-jar","pdgf.jar","-load",this.xmlFile};
          ProcessExecutor pe = new ProcessExecutor("pdgf", cmd,this.PDGF_HOME);
          pe.write("start");
          pe.read();
          pe.stop();
     }
 	 public void setXmlFile(String fileName){
		this.xmlFile = fileName;
	 }
     public static void main(String[] args){
         PdgfDataGenerator pdgf = new PdgfDataGenerator();
         pdgf.setXmlFile(System.getenv("PROJECT_HOME") + "/pdgf/config/simple.xml");
         pdgf.generate();
    	 
     }
}
