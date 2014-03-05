package duke.hbase.cm.tdg;

import java.nio.file.Paths;
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
	private static final String HADOOP_HOME = System.getenv("HADOOP_HOME");
        private static final String hadoopCoreSiteXML = HADOOP_HOME + "/conf/core-site.xml";
        private static final String hadoopHdfsSiteXML = HADOOP_HOME + "/conf/hdfs-site.xml";
        private static final String hadoopMapredSiteXML = HADOOP_HOME+ "/conf/mapred-site.xml";
         	
	public void copyFromLocal (String source, String dest) throws IOException {
		 
		Configuration conf = new Configuration();
		conf.addResource(new Path(hadoopCoreSiteXML));
		conf.addResource(new Path(hadoopHdfsSiteXML));
		conf.addResource(new Path(hadoopMapredSiteXML));
		 
		FileSystem fileSystem = FileSystem.get(conf);
		Path srcPath = new Path(source);
		 
		Path dstPath = new Path(dest);
                Path schemaDir = dstPath.getParent();
		// Check if the schema dir exists
		if (!(fileSystem.exists(schemaDir))) {
		   System.out.println("No such destination " + schemaDir);
           System.out.println("Creating new diretory in hdfs: " + schemaDir);
		   fileSystem.mkdirs(schemaDir);
		}
   
		 
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1, source.length());
		 
		try{
                        //keep the local file, and overwrite the file on hdfs 
			System.out.println("-------------------------------------------");
			System.out.println("Copying " + filename + " from " + srcPath + " to /hdfs"  + dest);
                        fileSystem.copyFromLocalFile(false,true,srcPath, dstPath);
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
