package duke.hbase.sd;

public class Column {
  
  static int column_count = 0;

  String family = "_0:";
  String key;
  String type = null;
  int average_key_size = 0;
  int average_value_size = 0;
  int numberOfColumns = 1;


  public int getAverage_key_size() {
    return average_key_size;
  }

  public void setAverage_key_size(int average_key_size) {
    this.average_key_size = average_key_size;
  }

  public Column(String key) {
    super();
    this.key = key;
  }

  public Column(String key, String type) {
    super();
    this.family = "_0";
    this.key = key;
    this.type = type;
  }

  public int getAverage_value_size() {
    return average_value_size;
  }

  public void setAverage_value_size(int average_value_size) {
    this.average_value_size = average_value_size;
  }

  public int getNumberOfColumns() {
    return numberOfColumns;
  }

  public void setNumberOfColumns(int numberOfColumns) {
    this.numberOfColumns = numberOfColumns;
  }
  

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
