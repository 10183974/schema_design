Commands for creating and loading data

export PHOENIX_HOME=/home/hadoop/git/phoenix/
export PROJECT_HOME=/home/hadoop/git/schema_design/
export DATA_DIR=/home/hadoop/git/TPC-H-Hive/dbgen/

$PHOENIX_HOME/bin/psql.sh localhost $PROJECT_HOME/bin/create_tables.sql

$PROJECT_HOME/bin/load_tables.sh $DATA_DIR

Commands for dropping tables

$PHOENIX_HOME/bin/psql.sh localhost $PROJECT_HOME/drop_tables.sql
