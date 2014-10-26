#! /bin/bash

set -x

workloadfile=workloads/$1

RESULT_DIR='./result/'
SHORT_SLEEP_TIME=10
LONG_SLEEP_TIME=100
number_of_threads=20
output_file_suffix='.dat'

#restart dfs and hbase

echo "stoping hbase..."
ssh hadoop@master /home/hadoop/hbase/bin/stop-hbase.sh
sleep $SHORT_SLEEP_TIME

echo "stoping dfs..."
ssh hadoop@master /home/hadoop/hadoop/bin/stop-dfs.sh
sleep $SHORT_SLEEP_TIME

echo "starting dfs..."
ssh hadoop@master /home/hadoop/hadoop/bin/start-dfs.sh
sleep $LONG_SLEEP_TIME

echo "starting hbase..."
ssh hadoop@master /home/hadoop/hbase/bin/start-hbase.sh
sleep $LONG_SLEEP_TIME

echo "starting hbase..."
ssh hadoop@master /home/hadoop/hbase/bin/start-hbase.sh
sleep $LONG_SLEEP_TIME

#clean
echo "droping the usertable..."
ssh hadoop@master /home/hadoop/hbase/bin/hbase shell $2
echo "sleeping for short time..."
sleep $SHORT_SLEEP_TIME

#create
echo "creating usertable..."
ssh hadoop@master /home/hadoop/hbase/bin/hbase shell $3
echo "sleeping for short time..."
sleep $SHORT_SLEEP_TIME

#run experiment
echo "running experiment..."
echo "start time:"
echo `date +%s`
java -classpath ../lib/phoenix-4.1.0-client.jar:core/target/core-0.1.4.jar:hbase/target/hbase-binding-0.1.4.jar:distribution/target/ycsb-0.1.4.tar.gz:jdbc/target/jdbc-binding-0.1.4.jar:hbase/src/main/conf/ com.yahoo.ycsb.OriginalClient -db com.yahoo.ycsb.db.OriginalHBaseClient -P  ~/git/schema_design/YCSB_PhoenixClient/$workloadfile -p columnfamily=cf  -p readproportion=$4 -p insertproportion=$5 -load -threads $number_of_threads -s &> $6
echo "end time:"
echo `date +%s`
echo "sleeping for short time..."
sleep $SHORT_SLEEP_TIME

set +x
