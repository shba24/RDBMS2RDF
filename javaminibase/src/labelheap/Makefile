JDKPATH = /usr
LIBPATH = /p/course/cs784-raghu/minibase_share/javaminibase/construction
CLASSPATH = $(JDKPATH)/lib/classes.zip:.:..:$(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)

PROGS = together

all: $(PROGS)

lhf:LabelHeapfile.java
	$(JAVAC) LabelHeapfile.java

lhp:LHFPage.java
	$(JAVAC) LHFPage.java

label:Label.java
	$(JAVAC) Label.java

lscan:LScan.java
	$(JAVAC) LScan.java

quadruple:Quadruple.java
	$(JAVAC) Quadruple.java

together:*.java
	$(JAVAC) *.java

clean:
	\rm -f *.class *~ \#* core 
