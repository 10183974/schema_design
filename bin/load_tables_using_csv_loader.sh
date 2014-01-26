#! /bin/bash

export phoenix_home=/home/hadoop/git/phoenix
export csv_dir=/csv_files

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/customer.csv -t customer -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/supplier.csv -t supplier -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/part.csv -t part -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/partsupp.csv -t partsupp -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/nation.csv -t nation -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/region.csv -t region -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/orders.csv -t orders -zk master:2181 -hd hdfs://master:54310 -mr master:54311

$phoenix_home/bin/csv-bulk-loader.sh -i $csv_dir/lineitem.csv -t lineitem -zk master:2181 -hd hdfs://master:54310 -mr master:54311
