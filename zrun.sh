#!/bin/bash
make clean
make compile
make createjar


echo 'run DataGenerator'
java -cp "SchemaDesign-1.0.jar:lib/*" duke.hbase.cm.tdg.DataGenerator
