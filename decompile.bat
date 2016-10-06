rem usage: decompile Filename

javac %1.java
java ClassToXML %1.class>%1.xml
java XMLToSource.parser<%1.xml