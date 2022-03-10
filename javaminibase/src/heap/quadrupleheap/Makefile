JDKPATH = /usr
LIBPATH = ../../
CLASSPATH = $(JDKPATH)/lib/classes.zip:.:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)

PROGS = together

all: $(PROGS)

qhf:QuadrupleHeapFile.java
	$(JAVAC) QuadrupleHeapFile.java

thp:THFPage.java
	$(JAVAC) THFPage.java


tscan:TScan.java
	$(JAVAC) TScan.java

qdpi:QuadDataPageInfo.java
	$(JAVAC) QuadDataPageInfo.java

together:*.java
	$(JAVAC) *.java

clean:
	\rm -f *.class *~ \#* core 
