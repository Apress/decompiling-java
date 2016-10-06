rem compile the decompiler :-;
rem may need to add your own classpath

java java_cup.Main decompiler.cup
java JLex.Main decompiler.lex
IF EXIST Yylex.java del Yylex.java
ren decompiler.lex.java Yylex.java
javac -d . parser.java sym.java Yylex.java
