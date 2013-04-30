@echo off
REM POC - Ciência da Computação - UNI-BH 
REM Gerador de compilador de fórmula em "fisrt-order logic"
REM Aluno: Frederico Martins Biber Sampaio
REM 01/12/2012

@REM Ajuste o caminho para o Java SDK do computador específico
set JAVA_HOME="C:\Program Files\Java\jdk1.7.0_21"

set CUP_LIB="%CD%\lib\java-cup-11a.jar"
set JFLEX_LIB="%CD%\lib\JFlex.jar"
set GETOPT_LIB="%CD%\lib\java-getopt-1.0.9.jar"
set CLASSPATH=%CD%\bin;%JFLEX_LIB%;%CUP_LIB%;%GETOPT_LIB%

%JAVA_HOME%\bin\java.exe -Dfile.encoding=UTF-8 -classpath %CLASSPATH% ProofApp -o "%CD%\test\teste.dot" "%CD%\test\teste.txt"