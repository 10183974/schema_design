package duke.hbase.cm.tdg;

public class Configuration {
	private String name = null;
	private int hbaseRegionSize = 0;
	private int phoenixThreadSize = 0;
    private int hbaseThreadSize = 0;
	public int getHbaseRegionSize() {
		return hbaseRegionSize;
	}
	public void setHbaseRegionSize(int hbaseRegionSize) {
		this.hbaseRegionSize = hbaseRegionSize;
	}
	public int getPhoenixThreadSize() {
		return phoenixThreadSize;
	}
	public void setPhoenixThreadSize(int phoenixThreadSize) {
		this.phoenixThreadSize = phoenixThreadSize;
	}
	public int getHbaseThreadSize() {
		return hbaseThreadSize;
	}
	public void setHbaseThreadSize(int hbaseThreadSize) {
		this.hbaseThreadSize = hbaseThreadSize;
	}
     

}
