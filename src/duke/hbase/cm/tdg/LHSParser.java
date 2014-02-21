package duke.hbase.cm.tdg;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import duke.hbase.cm.tdg.Schema;

public class LHSParser {
	//parsing kth row of lhsFile to schema
  public void parse(Schema schema, String lhsFile,  int k) {  
	BufferedReader br = null;
	String line = "";
	String seperator = ",";
 
	try {
		System.out.println("Parsing " + k + "th row of LHS data from " + lhsFile + " to schema " + schema.schemaName);
		br = new BufferedReader(new FileReader(lhsFile));
		int lineNumber = 1;
		while ((line = br.readLine()) != null) {
			if (k==lineNumber){
				String[] s = line.split(seperator);
				schema.numRows= Integer.parseInt(s[0]);
				schema.numColumns = Integer.parseInt(s[1]);
				schema.rowkeySize = Integer.parseInt(s[2]);
				schema.columnSize = Integer.parseInt(s[3]);	
				System.out.println("-------------------------------------------");
				System.out.println("Number of rows = " + schema.numRows + ", " + 
	                               "number of columns = " + schema.numColumns + ", " + 
						            "rowkey size = " + schema.rowkeySize + ", " + 
	                                "columnSize = " + schema.columnSize);	
				System.out.println("-------------------------------------------");
			}
			lineNumber++;

		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	System.out.println("Finish parsing LHS file.");
  }
  
  public static void main(String[] args) {
	String PROJECT_HOME = System.getenv("PROJECT_HOME");
    String lhsFile = PROJECT_HOME  + "/src/duke/hbase/cm/tdg/LHS.csv";
	LHSParser parser = new LHSParser();
	parser.parse(new Schema(),lhsFile,2);
  }
 
}