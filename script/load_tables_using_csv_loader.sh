#! /bin/bash

export phoenix_home=/home/hadoop/git/phoenix
export csv_data_dir=/csv_files/
export raw_data_dir=/raw_data/ 
export project_home=~/git/schema_design/

# commands for converting raw_data to csv format
$HADOOP_HOME/bin/hadoop  jar /home/hadoop/hadoop/contrib/streaming/hadoop-streaming-1.2.1.jar -input $raw_data_dir -output $csv_data_dir -mapper $project_home/bin/rawdatatocsv.sh -reducer org.apache.hadoop.mapred.lib.IdentityMapper


# commands for loading data

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/customer.csv -t customer -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/supplier.csv -t supplier -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/part.csv -t part -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/partsupp.csv -t partsupp -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/nation.csv -t nation -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/region.csv -t region -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/orders.csv -t orders -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/lineitem.csv -t lineitem -zk master:2181 -hd hdfs://master:54310 -mr master:54311
