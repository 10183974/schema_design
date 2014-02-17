GS = -g
JC = javac -d classes -cp "lib/junit.jar:lib/org.hamcrest.core_1.1.0.v20090501071000.jar:lib/pdgf.jar:lib/hadoop-core-1.2.1.jar:pdgf/lib/flanagan.jar:pdgf/lib/log4j-1.2.15.jar:classes/"
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS)	$*.java

CLASSES	= $(wildcard src/duke/hbase/sd/*.java)

compile: cm sd test tdg createjar

test: src/duke/hbase/test
	$(JC) $(JFLAGS)	$(wildcard src/duke/hbase/test/CMDProxyTest.java)

cm: src/duke/hbase/cm
	$(JC) $(JFLAGS) $(wildcard src/duke/hbase/cm/CMDProxy.java)

sd: src/duke/hbase/sd
	$(JC) $(JFLAGS)	$(wildcard src/duke/hbase/sd/*.java)

tdg: src/duke/hbase/cm/tdg
	$(JC) $(JFLAGS) $(wildcard src/duke/hbase/cm/tdg/*.java)

createjar: clean compile
	jar cmf  META-INF/MANIFEST.MF SchemaDesign-1.0.jar -C classes .

run:
	java -jar SchemaDesign-1.0.jar -cp "lib/junit.jar:lib/org.hamcrest.core_1.1.0.v20090501071000.jar:lib/phoenix-3.0.0-SNAPSHOT-client.jar"

clean:
	rm -rf	SchemaDesign-1.0.jar	classes/duke/hbase/cm/*.class	classes/duke/hbase/sd/*.class	classes/duke/hbase/test/*.class

.PHONY:	default	clean	classes
