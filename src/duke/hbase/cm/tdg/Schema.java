package duke.hbase.cm.tdg;

import duke.hbase.cm.tdg.LHSParser;

public class Schema {
	 String schemaName = "mySchema";
	 int NoRows =  0;
	 int NoColumns = 0;
	 int rowkeySize = 0;
	 int columnSize = 0;
	 int hbaseRegionSize = 0;
	 int phoenixThreadSize =0;
     int hbaseThreadSize = 0;
	
	private void parsingLHS(){
//		LHSParser parser = new LHSParser();
	}
	public static void main(String[] args){
		   String lhsFile = "/Users/Weizheng/git/schema_design/src/duke/hbase/cm/tdg/LHS.csv";
		   LHSParser parser = new LHSParser();
		   Schema schema = new Schema();
		   parser.parse(schema,lhsFile,2);
		   System.out.println(schema.NoRows);
	}

}
