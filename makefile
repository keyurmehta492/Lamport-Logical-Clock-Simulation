JFLAGS = -g
JC = javac -cp ".:mysql-connector.jar"
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
Queue.java\
masterObj.java\
processObj.java\
LamportSimu.java\

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) */*.class