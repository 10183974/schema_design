#! /bin/bash

set -x

droptablescript=/home/hadoop/git/schema_design/script/drop_usertable.sql
createtablescript=/home/hadoop/git/schema_design/script/create_user_table1.sql

#clean
echo "droping the usertable..."
#ssh hadoop@master /home/hadoop/hbase/bin/hbase shell $1
ssh hadoop@master /home/hadoop/hbase/bin/hbase shell $droptablescript
echo "sleeping for short time..."

#create
echo "creating usertable..."
#ssh hadoop@master /home/hadoop/hbase/bin/hbase shell $2
ssh hadoop@master /home/hadoop/hbase/bin/hbase shell $createtablescript
echo "sleeping for short time..."

set +x
