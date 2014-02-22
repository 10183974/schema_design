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
hadoop dfs -rmr /tdg/schema_1

echo "---------------------------------------"
cd $PROJECT_HOME

#delete the workdir for the schema_1
rm -r workdir/schema_1
