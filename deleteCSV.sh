#!/bin/bash

#delete the /tdg/csvdir on the hdfs
echo "---------------------------------------"
echo 'delete /tdg/csvdir on hdfs'
hadoop dfs -rmr /tdg/


echo "---------------------------------------"
cd $PROJECT_HOME

#delete the workdir for the schema_1
rm -r workdir/scanTD*

rm scanTD.txt
