package duke.hbase.sd;

public class Column {
  
  String family = "_0:";
  String key;
  int average_key_size = 0;
  int average_value_size = 0;
  int numberOfColumns = 1;

  ColumnType type = ColumnType.VARCHAR;
  

  public String getKey() {
    return key;
  }


  public void setKey(String name) {
    this.key = name;
  }

  public String getFamily() {
    return family;
  }


  public void setFamily(String family) {
    this.family = family;
  }


  public ColumnType getType() {
    return type;
  }


  public void setType(ColumnType type) {
    this.type = type;
  }

}
