package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PdgfDataGenerator {
	 private static final String PROJECT_HOME = System.getenv("PROJECT_HOME");
	 private static final String PDGF_HOME = PROJECT_HOME + "/pdgf/";
	 private String xmlFile = null;
	//generate csv file using xmlFile
     public void generate(){
        try {
                  //load xml file to pdgf     
                  //ProcessBuilder pb = new ProcessBuilder(new String[]{"pdgf/mypdgfscript.sh",xmlFilePath});
                 
                  System.out.println("Starting pdgf...");
                
                  System.out.println("Loading xml file " + xmlFile + " to pdgf");
                  ProcessBuilder pb = new ProcessBuilder(new String[]{
                           "java","-jar","pdgf.jar","-load",xmlFile});
                  pb.directory(new File(PDGF_HOME));
                  pb.redirectErrorStream(true);
                  
                  Process p;
	              p = pb.start(); 
                  BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
	  
                  String a = null;
                  //start generating
                  String cmdStr = "start"; 
		          bw.write(cmdStr, 0, cmdStr.length());
                  bw.newLine();
		          bw.flush();
		  
                  while ((a = reader.readLine()) !=null) {
                      System.out.println("pdgf: " + a);
	              }
                  //exit automatically, since closeWhenDone is set to be true in pdgf Controller
                  System.out.println("Exiting pdgf");

         } catch (IOException e) {
	         e.printStackTrace();
         } 
     }
 	public void setInFile(String fileName){
		this.xmlFile = fileName;
	}
     public static void main(String[] args){
    	 PdgfDataGenerator pdgf = new PdgfDataGenerator();
    	 pdgf.setInFile("workdir/z.xml");
    	 pdgf.generate();
    	 
     }
}
