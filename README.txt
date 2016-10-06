Thanks for downloading and testing the ClassToSource decompiler/obfuscator package!
This decompiler was written for and is documented at length in "Decompiling Java" 
published by Apress.  Lots of information on obfuscation technique and an overview
of this obfuscator is provided in the book as well.

The package consists of three parts:

- A Java disassembler program (ClassToXML.java)
- A JavaCUP/JLex decompiler (decompiler.cup and decompiler.lex)
- An XML-to-class assembler (XMLToClass.java)

In order to test the decompiler, you must first install CUP and JLex.

You can find JLex at http://www.cs.princeton.edu/~appel/modern/java/JLex/
Download Main.java into a subdirectory called JLex and compile.
CUP is available at http://www.cs.princeton.edu/~appel/modern/java/CUP/
Download CUP, extract and copy java_cup into this directory

To generate the decompiler
-------------------------
1. Compile the Lex spec as follows: 
	java JLex.Main decompiler.lex (this will take some time)
2. Compile the Cup spec as follows: 
	java java_cup.Main decompiler.cup
3. Rename decompiler.lex.java to Yylex.java 
	ren decompiler.lex.java Yylex.java 
4. Compile the whole decompiler into one package (XMLToSource.parser):
	javac -d . parser.java sym.java Yylex.java

Note the included windows batch file compile.bat performs these steps. 

To execute the decompiler
-------------------------
1. Compile the disassembler ClassToXML.java as follows:
	javac ClassToXML.java
2. Compile any Java source file (use a sample file from the testsuite such as ForLoop.java).  
	copy Examples\ForLoop.java .
	javac ForLoop.java
3. Disassemble the ForLoop.class file into XML
	java ClassToXML ForLoop.class > ForLoop.xml
4. Decompile the XML into the source code for ForLoop.java
	java XMLToSource.parser < ForLoop.xml

Note the included decompile.bat performs these steps. 

To execute the obfuscator
--------------------------
1. Compile the "assembler" XMLToClass.java as follows:
	javac XMLToClass.java
2. Compile any Java source file (use a sample file from the testsuite such as ForLoop.java).  
	copy Examples\ForLoop.java .
	javac ForLoop.java
3. Disassemble the ForLoop.class file into XML
	java ClassToXML ForLoop.class > ForLoop.xml
4. Rename the original class file.
        ren ForLoop.class ForLoop.old.class
5. Manually obfuscate the Constant Pool entries you'd like to hide by editing the XML document
    in a text editor.  Make sure not to obfuscate any references to external methods or fields!
6. Assemble the XML into an executable.
	java XMLToClass ForLoop.java ForLoop.class

Note the included roundtrip.bat performs these steps and binary-compares the generated file 
against the original file.

Thanks

Godfrey Nolan
godfrey@riis.com


