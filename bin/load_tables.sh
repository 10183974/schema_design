#! /bin/bash

data_dir = $1

echo "loading customer table"
./psql.sh -t customer -d '|' 2 3 -h C_CUSTKEY,C_NAME,C_ADDRESS,C_NATIONKEY,C_PHONE,C_ACCTBAL,C_MKTSEGMENT,C_COMMENT,C_EMPTY localhost $data_dir/customer.csv

echo "loading supplier table"
./psql.sh -t supplier -d '|' 2 3 -h S_SUPPKEY,S_NAME,S_ADDRESS,S_NATIONKEY,S_PHONE,S_ACCTBAL,S_COMMENT,S_EMPTY localhost $data_dir/supplier.csv

echo "loading orders table"
./psql.sh -t orders -d '|' 2 3 -h O_ORDERKEY,O_CUSTKEY,O_ORDERSTATUS,O_TOTALPRICE,O_ORDERDATE,O_ORDERPRIORITY,O_CLERK,O_SHIPPRIORITY,O_COMMENT,O_EMPTY localhost $data_dir/orders.csv

echo "loading lineitem"
./psql.sh -t  lineitem -d '|' 2 3 -h L_ORDERKEY,L_PARTKEY,L_SUPPKEY,L_LINENUMBER,L_QUANTITY,L_EXTENDEDPRICE,L_DISCOUNT,L_TAX,L_RETURNFLAG,L_LINESTATUS,L_SHIPDATE,L_COMMITDATE,L_RECEIPTDATE,L_SHIPINSTRUCT,L_SHIPMODE,L_COMMENT,L_EMPTY localhost $data_dir/lineitem.csv 

echo "loading part table"
./psql.sh -t  part -d '|' 2 3 -h P_PARTKEY,P_NAME,P_MFGR,P_BRAND,P_TYPE,P_SIZE,P_CONTAINER,P_RETAILPRICE,P_COMMENT,P_EMPTY localhost $data_dir/part.csv

echo "loading partsupp table"
./psql.sh -t  partsupp -d '|' 2 3 -h PS_PARTKEY,PS_SUPPKEY,PS_AVAILQTY,PS_SUPPLYCOST,PS_COMMENT,PS_EMPTY localhost $data_dir/partsupp.csv

echo "loading nation table"
./psql.sh -t  nation -d '|' 2 3 -h N_NATIONKEY,N_NAME,N_REGIONKEY,N_COMMENT,N_EMPTY localhost $data_dir/nation.csv

echo "loading region table"
./psql.sh -t  lineitem -d '|' 2 3 -h R_REGIONKEYT,R_NAME,R_COMMENT,R_EMPTY localhost $data_dir/region.csv
