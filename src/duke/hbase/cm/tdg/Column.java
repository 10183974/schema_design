package duke.hbase.cm.tdg;

public class Column {
	  private String columnType = null;
	  private int    columnSize = 0;
	  private String familyName = null;
	  private String columnName = null;
	  private boolean isPrimary = false;
	  private boolean isRowkey = false;
	  private boolean isNotNull = false;

	  public Column() {
		  //default Column constructor
//		  this.columnType = "VARCHAR";
//		  this.columnSize = 10;
//		  this.familyName = "_0";
//		  this.columnName = "Comment";
//		  this.isPrimary = false;   
//		  this.isNotNull = false;
	  }
	  public Column(String aName,  String aFamilyName, String aType, int aSize,
			   boolean aIsPrimary, boolean aIsRowkey, boolean aIsNotNull) {
	    this.columnType = aType;
	    this.columnSize = aSize;
	    this.familyName = aFamilyName;
	    this.columnName = aName;
	    this.isPrimary = aIsPrimary;
	    this.setIsRowkey(aIsRowkey);
	    this.isNotNull = aIsNotNull;
	  }
	  public String getColumnType() {
	    return this.columnType;
	  }
	  public int getColumnSize() {
	 	return this.columnSize;
	  }
	  public String getFamilyName(){
		  return this.familyName;
	  }
	  public String getColumnName(){
		return this.columnName;
	  }
	  public boolean getIsPrimary(){
		  return this.isPrimary;
	  }
		public boolean getIsRowkey() {
			return this.isRowkey;
		}
	  public boolean getIsNotNull(){
		  return this.isNotNull;
	  }
	  public void setColumnType(String aType) {
	    this.columnType = aType;
	  } 
	  public void setFamilyName(String aFamilyName){
		  this.familyName = aFamilyName;
	  }
	  public void setColumnSize(int aSize) {
	    this.columnSize = aSize;
	  }
	  public void setColumnName(String aName){
		  this.columnName=aName;
	  }
	  public void setIsPrimary(boolean aIsPrimary){
		  this.isPrimary = aIsPrimary;
	  }

	 public void setIsRowkey(boolean aIsRowkey) {
			this.isRowkey = aIsRowkey;
	}
	  public void setIsNotNull(boolean aIsNotNull){
		  this.isNotNull = aIsNotNull;
	  }
	  public void printColumnInfo(){
		  System.out.println("\t\tfname: " + this.getFamilyName() + ", " 
                  +"name: " + this.getColumnName() + ", "
                  + "type: " + this.getColumnType() + ", "
                  + "szie: "+ this.getColumnSize() + ", "
                  + "isPrimary: " + this.getIsPrimary() + ", "
                  + "isNotNull: " + this.getIsNotNull());
	  }
	  
	  public static void main(String[] args){
		  Column c = new Column();
		  c.printColumnInfo();

	  }

}
