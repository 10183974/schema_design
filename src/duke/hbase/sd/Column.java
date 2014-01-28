package duke.hbase.sd;

public class Column {
  
  String name;
  String family = "default";
  int size = 0;
  ColumnType type = ColumnType.VARCHAR;
  boolean isRowKey = false;
  

  public Column(String name, String family, int size, ColumnType type, boolean isRowKey) {
    super();
    this.name = name;
    this.family = family;
    this.size = size;
    this.type = type;
    this.isRowKey = isRowKey;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public String getFamily() {
    return family;
  }


  public void setFamily(String family) {
    this.family = family;
  }


  public int getSize() {
    return size;
  }


  public void setSize(int size) {
    this.size = size;
  }


  public ColumnType getType() {
    return type;
  }


  public void setType(ColumnType type) {
    this.type = type;
  }


  public boolean isRowKey() {
    return isRowKey;
  }


  public void setRowKey(boolean isRowKey) {
    this.isRowKey = isRowKey;
  }

}
