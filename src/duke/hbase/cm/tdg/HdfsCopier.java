package duke.hbase.cm.tdg;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
public class HdfsCopier {
       
        private static final String hadoopCoreSiteXML = "/home/hadoop/hadoop/conf/core-site.xml";
        private static final String hadoopHdfsSiteXML = "/home/hadoop/hadoop/conf/hdfs-site.xml";
        private static final String hadoopMapredSiteXML = "/home/hadoop/hadoop/conf/mapred-site.xml";
         	
	public void copyFromLocal (String source, String dest) throws IOException {
		 
		Configuration conf = new Configuration();
		conf.addResource(new Path(hadoopCoreSiteXML));
		conf.addResource(new Path(hadoopHdfsSiteXML));
		conf.addResource(new Path(hadoopMapredSiteXML));
		 
		FileSystem fileSystem = FileSystem.get(conf);
		Path srcPath = new Path(source);
		 
		Path dstPath = new Path(dest);
		// Check if the file already exists
		if (!(fileSystem.exists(dstPath))) {
		   System.out.println("No such destination " + dstPath);
		   return;
		}
		 
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1, source.length());
		 
		try{
                        //keep the local file, and overwrite the file on hdfs 
			fileSystem.copyFromLocalFile(false,true,srcPath, dstPath);
			System.out.println("-------------------------------------------");
			System.out.println("File " + filename + "copied to " + dest);
                        System.out.println("-------------------------------------------");
		}catch(Exception e){
			System.err.println("Exception caught! :" + e);
			System.exit(1);
		}finally{
			fileSystem.close();
		}
		}
	public static void main(String[] args){
		HdfsCopier hdfsCopier = new HdfsCopier();
		String localDir = "./workdir/csvdir/";
		String hdfsDir =  "/tdg/";
         	try {
		        hdfsCopier.copyFromLocal(localDir,hdfsDir);
		} catch (IOException e) {
		        e.printStackTrace();
	        }
	}

}
