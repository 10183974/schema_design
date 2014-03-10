package duke.hbase.cm.tdg;

public class Column {
	  private String fName = null;
	  private String name = null;
	  private String type = null;
	  private int    size = 0;
	  private boolean isPrimary = false;
	  private boolean isRowkey = false;
	  private boolean isNotNull = false;
	  private int min = 0;
	  private int max = 0;

     public Column(String fName, String name, String type, int size,
				boolean isPrimary, boolean isRowkey, boolean isNotNull) {
			this.fName = fName;
			this.name = name;
			this.type = type;
			this.size = size;
			this.isPrimary = isPrimary;
			this.isRowkey = isRowkey;
			this.isNotNull = isNotNull;
	}  
     public Column(String fName, String name, String type, int size,
 				boolean isPrimary, boolean isRowkey, boolean isNotNull,int min, int max) {
 			this.fName = fName;
 			this.name = name;
 			this.type = type;
 			this.size = size;
 			this.isPrimary = isPrimary;
 			this.isRowkey = isRowkey;
 			this.isNotNull = isNotNull;
 			this.min = min;
 			this.max = max;
 	} 
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}

	  public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public boolean isPrimary() {
		return isPrimary;
	}
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public boolean isRowkey() {
		return isRowkey;
	}
	public void setRowkey(boolean isRowkey) {
		this.isRowkey = isRowkey;
	}
	public boolean isNotNull() {
		return isNotNull;
	}
	public void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}
	public void printColumnInfo(){
		  System.out.println("\t\tfname: " + this.getfName() + ", " 
                  +"name: " + this.getName() + ", "
                  + "type: " + this.getType() + ", "
                  + "szie: "+ this.getSize() + ", "
                  + "isPrimary: " + this.isPrimary() + ", "
                  + "isRowkey: " + this.isRowkey() + ", " 
                  + "isNotNull: " + this.isNotNull());
	  }
	  

}
