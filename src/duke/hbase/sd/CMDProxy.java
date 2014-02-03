package duke.hbase.sd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CMDProxy {
  private Process process;

  public CMDProxy(String cmd, String arg) throws IOException {
    process = new ProcessBuilder(cmd, arg).start();
  }

  public void stop() {
    process.destroy();
    System.out.println("stoping cmdproxy process...");
  }

  public BufferedReader GetOutputReader() {
    InputStream is = process.getErrorStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    return br;
  }

  public BufferedWriter GetInputWriter() {
    OutputStream os = process.getOutputStream();
    OutputStreamWriter osw = new OutputStreamWriter(os);
    BufferedWriter bw = new BufferedWriter(osw);
    return bw;
  }
}

