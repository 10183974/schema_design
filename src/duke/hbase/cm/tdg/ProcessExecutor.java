package duke.hbase.cm.tdg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ProcessExecutor {
	private String name = null;
	private ProcessBuilder pb = null;
    private Process p;

    public ProcessExecutor(String aName, String cmd[], String workDir) {
    	this.name = aName;
	    this.pb = new ProcessBuilder(cmd);
	    pb.directory(new File(workDir));   
        pb.redirectErrorStream(true);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Starting process: " + this.name);
        try {
			p = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}     
    }

    public void stop() {
        p.destroy();    
        System.out.println("Stopping process: " + this.name);
        System.out.println("----------------------------------------------------------------------");
    }

    public void read() {
    	//read from process
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.p.getInputStream()));
            String s = null;
			while ((s = reader.readLine()) !=null) {
			    System.out.println(this.name + ": " + s);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}  
    }
    public void write(String cmd) {
    	//write new command to process
        try {
    	    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(this.p.getOutputStream()));
			bw.write(cmd, 0, cmd.length());
		    bw.newLine();
		    bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
  
}

