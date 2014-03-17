package duke.hbase.sd;

public class Column implements Cloneable {
  
  private static int column_count = 0;
  public static final String DEFAULT_FAMILY = "_0:";

  private int id;
  private String family = DEFAULT_FAMILY;
  private String name;
  private String type = null;
  private int average_key_size = 0;
  private int average_value_size = 0;

  public Column() {
  }

  public Column(String key) {
    super();
    this.name = key;
  }

  public Column(String key, String type, int average_key_size, int average_value_size) {
    super();
    this.family = DEFAULT_FAMILY;
    this.name = key;
    this.type = type;
    this.average_key_size = average_key_size;
    this.average_value_size = average_value_size;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getAverage_key_size() {
    return average_key_size;
  }

  public void setAverage_key_size(int average_key_size) {
    this.average_key_size = average_key_size;
  }

  public int getAverage_value_size() {
    return average_value_size;
  }

  public void setAverage_value_size(int average_value_size) {
    this.average_value_size = average_value_size;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public static int getColumnId() {
    return column_count++;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(" family: " + getFamily() + ",");
    sb.append(" name: " + getName() + ",");
    sb.append(" type: " + getType() + ",");
    sb.append(" keysize: " + getAverage_key_size() + ",");
    sb.append(" valuesize: " + getAverage_value_size() + ",");
    return sb.toString();
  }

  public Column clone() {
    Column c = new Column();
    c.setId(Column.getColumnId());
    c.setFamily(new String(this.getFamily()));
    c.setName(new String(this.getName()));
    c.setType(new String(this.getType()));
    c.setAverage_key_size(this.getAverage_key_size());
    c.setAverage_value_size(this.getAverage_value_size());
    return c;
  }

}
