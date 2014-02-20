#!/bin/bash
#delete the table X and Z in hbase
echo "---------------------------------------"
cd $PHOENIX_HOME/bin
echo 'deleting table Z on hbase'
./sqlline.sh localhost drop table Z

echo "---------------------------------------"
echo 'deleting table Z on hbase'
./sqlline.sh localhost drop table X

#delete the /tdg/csvdir on the hdfs
echo "---------------------------------------"
echo 'delete /tdg/csvdir on hdfs'
hadoop dfs -rmr /tdg/csvdir


echo "---------------------------------------"
cd $PROJECT_HOME
