#!/bin/bash

rm drop.sql
touch drop.sql

x=1
while [ $x -le $1 ]
do
  a="SCANTD"$x
  b="Z"
  tableName=${a}_${b}
  echo "drop table $tableName;" >> drop.sql
  
  let x=x+1 
done
cat drop.sql

cd ~/git/phoenix/bin
./psql.sh localhost ~/git/schema_design/drop.sql
