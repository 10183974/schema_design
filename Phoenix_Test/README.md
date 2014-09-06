Phoenix_Test
==================

Workload and Schema Tests

This is a test program to test the seperate programs that integreted to the final phoenix_YCSB project. It is mainly used to read and parse the schema and workload information from external XML file; build the prepared statement accordingly and execute them.

The following files in src are the test programs the other xml files are example files for schema and workload information.

Test.java: takes both the property and workload from the properties.xml file, prepare the 
JDBC prepared statement accordingly, build connection to server and execute the prepared queries

WorkloadTest.java: This test program is used to read the upgraded schema information from xml files like ColumnFamilySchema.xml, which can include bindings

WorkloadParse.java: This test program read the upgraded workload xml files like promoteKeyWorkload2.xml, building the prepared statement according to the statement and bindings and executes the queries
