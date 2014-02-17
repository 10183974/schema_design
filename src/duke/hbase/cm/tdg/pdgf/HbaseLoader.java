import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class HbaseLoader {
	private void executeCommand(ProcessBuilder pb){
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            String s;
            // read from the process's combined stdout & stderr
            BufferedReader stdout = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
            while ((s = stdout.readLine()) != null) {
                System.out.println(s);
            }
            System.out.println("Exit value: " + p.waitFor());
            p.getInputStream().close();
            p.getOutputStream().close();
            p.getErrorStream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	public void loadTableToHbase(){
	     ProcessBuilder pb = new ProcessBuilder("bash", "-c","pwd");
	     pb.directory(new File("/home/hadoop/git/phoenix/bin"));
	     executeCommand(pb);
	     
	     pb = new ProcessBuilder("bash","-c",
	         "./psql.sh localhost ~/git/schema_design/src/duke/hbase/cm/tdg/pdgf/createTable.sql");
         executeCommand(pb);
         
   
	     
	}
	public static void main(String[] args){
		HbaseLoader hLoader = new HbaseLoader();
		hLoader.loadTableToHbase();
	
	}

}
