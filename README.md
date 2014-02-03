Command for creating and loading data

export PHOENIX_HOME=/home/hadoop/git/phoenix/
export PROJECT_HOME=/home/hadoop/git/schema_design/
export DATA_DIR=/home/hadoop/git/TPC-H-Hive/dbgen/

$PHOENIX_HOME/bin/psql.sh localhost $PROJECT_HOME/bin/create_tables.sql

$PROJECT_HOME/bin/load_tables.sh $DATA_DIR

Command for dropping tables

$PHOENIX_HOME/bin/psql.sh localhost $PROJECT_HOME/drop_tables.sql

Command for generating training data

java -cp $PROJECT_HOME/lib/TrainingDataGeneratorForTPCH.jar:$PROJECT_HOME/lib/phoenix-3.0.0-SNAPSHOT-client.jar  duke.hbase.sd.tdg.TrainingDataGeneratorForTPCH all

Command for running schema design

java -cp $PROJECT_HOME/lib/SchemaDesignImpl.jar:$PROJECT_HOME/lib/phoenix-3.0.0-SNAPSHOT-client.jar  duke.hbase.sd.SchemaDesignImpl 

